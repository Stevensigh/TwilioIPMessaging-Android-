package io.palliassist.palliassistmobile.twilio.chat.channels;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.Channel.ChannelType;
import com.twilio.chat.ChannelDescriptor;
import com.twilio.chat.Channels;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Paginator;
import com.twilio.chat.StatusListener;
import com.twilio.chat.UserInfo;

import io.palliassist.palliassistmobile.R;
import io.palliassist.palliassistmobile.twilio.chat.ChatClientManager;
import io.palliassist.palliassistmobile.twilio.application.TwilioApplication;
import io.palliassist.palliassistmobile.twilio.chat.listener.TaskCompletionListener;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChannelManager implements ChatClientListener {
  private static ChannelManager sharedManager = new ChannelManager();
  public Channel generalChannel;
  private ChatClientManager chatClientManager;
  private ChannelExtractor channelExtractor;
  private List<Channel> channels;
  private Channels channelsObject;
  private ChatClientListener listener;
  private String defaultChannelName;
  private String defaultChannelUniqueName;
  private Handler handler;
  private Boolean isRefreshingChannels = false;

  private ChannelManager() {
    this.chatClientManager = TwilioApplication.get().getChatClientManager();
    this.channelExtractor = new ChannelExtractor();
    this.listener = this;
    defaultChannelName = "Demo Channel";
    defaultChannelUniqueName = "Demo Channel";
    handler = setupListenerHandler();
  }

  public static ChannelManager getInstance() {
    return sharedManager;
  }

  public List<Channel> getChannels() {
    return channels;
  }

  public String getDefaultChannelName() {
    return this.defaultChannelName;
  }

  public void leaveChannelWithHandler(Channel channel, StatusListener handler) {
    channel.leave(handler);
  }

  public void deleteChannelWithHandler(Channel channel, StatusListener handler) {
    channel.destroy(handler);
  }

  public void populateChannels(final LoadChannelListener listener) {
    if (this.chatClientManager == null || this.isRefreshingChannels) {
      return;
    }
    this.isRefreshingChannels = true;

    handler.post(new Runnable() {
      @Override
      public void run() {
        channelsObject = chatClientManager.getChatClient().getChannels();

        channelsObject.getPublicChannels(new CallbackListener<Paginator<ChannelDescriptor>>() {
          @Override
          public void onSuccess(Paginator<ChannelDescriptor> channelDescriptorPaginator) {
            extractChannelsFromPaginatorAndPopulate(channelDescriptorPaginator, listener);
          }
        });

      }
    });
  }

  private void extractChannelsFromPaginatorAndPopulate(final Paginator<ChannelDescriptor> channelsPaginator,
                                                       final LoadChannelListener listener) {
    channels = new ArrayList<>();
    ChannelManager.this.channels.clear();
    channelExtractor.extractAndSortFromChannelDescriptor(channelsPaginator,
        new TaskCompletionListener<List<Channel>, String>() {
      @Override
      public void onSuccess(List<Channel> channels) {
        ChannelManager.this.channels.addAll(channels);
        Collections.sort(ChannelManager.this.channels, new CustomChannelComparator());
        ChannelManager.this.isRefreshingChannels = false;
        chatClientManager.setClientListener(ChannelManager.this);
        listener.onChannelsFinishedLoading(ChannelManager.this.channels);
      }

      @Override
      public void onError(String errorText) {
        System.out.println("Error populating channels: " + errorText);
      }
    });
  }

  public void createChannelWithName(String name, final StatusListener handler) {
    this.channelsObject
        .channelBuilder()
        .withFriendlyName(name)
        .withType(ChannelType.PUBLIC)
        .build(new CallbackListener<Channel>() {
          @Override
          public void onSuccess(final Channel newChannel) {
            handler.onSuccess();
          }

          @Override
          public void onError(ErrorInfo errorInfo) {
            handler.onError(errorInfo);
          }
        });
  }

  public void joinOrCreateGeneralChannelWithCompletion(final StatusListener listener) {
    channelsObject.getChannel(defaultChannelUniqueName, new CallbackListener<Channel>() {
      @Override
      public void onSuccess(Channel channel) {
        ChannelManager.this.generalChannel = channel;
        if (channel != null) {
          joinGeneralChannelWithCompletion(listener);
        } else {
          createGeneralChannelWithCompletion(listener);
        }
      }
    });
  }

  private void joinGeneralChannelWithCompletion(final StatusListener listener) {
    this.generalChannel.join(new StatusListener() {
      @Override
      public void onSuccess() {
        listener.onSuccess();
      }

      @Override
      public void onError(ErrorInfo errorInfo) {
        listener.onError(errorInfo);
      }
    });
  }

  private void createGeneralChannelWithCompletion(final StatusListener listener) {
    this.channelsObject
        .channelBuilder()
        .withFriendlyName(defaultChannelName)
        .withUniqueName(defaultChannelUniqueName)
        .withType(ChannelType.PUBLIC)
        .build(new CallbackListener<Channel>() {
          @Override
          public void onSuccess(final Channel channel) {
            ChannelManager.this.generalChannel = channel;
            ChannelManager.this.channels.add(channel);
            joinGeneralChannelWithCompletion(listener);
          }

          @Override
          public void onError(ErrorInfo errorInfo) {
            listener.onError(errorInfo);
          }
        });
  }

  public void setChannelListener(ChatClientListener listener) {
    this.listener = listener;
  }

  private String getStringResource(int id) {
    Resources resources = TwilioApplication.get().getResources();
    return resources.getString(id);
  }

  @Override
  public void onChannelAdd(Channel channel) {
    if (listener != null) {
      listener.onChannelAdd(channel);
    }
  }

  @Override
  public void onChannelInvite(Channel channel) {

  }

  @Override
  public void onChannelChange(Channel channel) {
    if (listener != null) {
      listener.onChannelChange(channel);
    }
  }

  @Override
  public void onChannelDelete(Channel channel) {
    if (listener != null) {
      listener.onChannelDelete(channel);
    }
  }

  @Override
  public void onChannelSynchronizationChange(Channel channel) {
    if (listener != null) {
      listener.onChannelSynchronizationChange(channel);
    }
  }

  @Override
  public void onError(ErrorInfo errorInfo) {
    if (listener != null) {
      listener.onError(errorInfo);
    }
  }

  @Override
  public void onUserInfoChange(UserInfo userInfo, UserInfo.UpdateReason updateReason) {
    if (listener != null) {
      listener.onUserInfoChange(userInfo, updateReason);
    }
  }

  @Override
  public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {

  }

  @Override
  public void onToastNotification(String s, String s1) {

  }

  @Override
  public void onToastSubscribed() {

  }

  @Override
  public void onToastFailed(ErrorInfo errorInfo) {

  }

  @Override
  public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {

  }

  private Handler setupListenerHandler() {
    Looper looper;
    Handler handler;
    if ((looper = Looper.myLooper()) != null) {
      handler = new Handler(looper);
    } else if ((looper = Looper.getMainLooper()) != null) {
      handler = new Handler(looper);
    } else {
      throw new IllegalArgumentException("Channel Listener must have a Looper.");
    }
    return handler;
  }
}

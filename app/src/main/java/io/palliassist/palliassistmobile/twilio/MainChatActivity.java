package io.palliassist.palliassistmobile.twilio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.twilio.chat.Channel;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.StatusListener;
import com.twilio.chat.UserInfo;

import io.palliassist.palliassistmobile.LoginActivity;
import io.palliassist.palliassistmobile.R;

import io.palliassist.palliassistmobile.twilio.application.TwilioApplication;
import io.palliassist.palliassistmobile.twilio.application.SessionManager;
import io.palliassist.palliassistmobile.twilio.application.AlertDialogHandler;
import io.palliassist.palliassistmobile.twilio.chat.channels.ChannelAdapter;
import io.palliassist.palliassistmobile.twilio.chat.channels.ChannelManager;
import io.palliassist.palliassistmobile.twilio.chat.listener.InputOnClickListener;
import io.palliassist.palliassistmobile.twilio.chat.listener.TaskCompletionListener;
import io.palliassist.palliassistmobile.twilio.chat.channels.LoadChannelListener;


import java.util.List;

import io.palliassist.palliassistmobile.twilio.chat.ChatClientManager;

public class MainChatActivity extends AppCompatActivity implements ChatClientListener {
  private Context context;
  private Activity mainActivity;
  private Button logoutButton;
  private Button addChannelButton;
  private TextView usernameTextView;
  private ChatClientManager chatClientManager;
  private ListView channelsListView;
  private ChannelAdapter channelAdapter;
  private ChannelManager channelManager;
  private MainChatFragment chatFragment;
  private DrawerLayout drawer;
  private ProgressDialog progressDialog;
  private MenuItem leaveChannelMenuItem;
  private MenuItem deleteChannelMenuItem;
  private SwipeRefreshLayout refreshLayout;

  @Override
  protected void onDestroy() {
    super.onDestroy();
    new Handler().post(new Runnable() {
      @Override
      public void run() {
        chatClientManager.shutdown();
        TwilioApplication.get().getChatClientManager().setChatClient(null);
      }
    });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_chat);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
        R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);

    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

    chatFragment = new MainChatFragment();
    fragmentTransaction.add(R.id.fragment_container, chatFragment);
    fragmentTransaction.commit();

    context = this;
    mainActivity = this;
    logoutButton = (Button) findViewById(R.id.buttonLogout);
    addChannelButton = (Button) findViewById(R.id.buttonAddChannel);
    usernameTextView = (TextView) findViewById(R.id.textViewUsername);
    channelsListView = (ListView) findViewById(R.id.listViewChannels);

    channelManager = ChannelManager.getInstance();
    setUsernameTextView();

    setUpListeners();
    checkTwilioClient();
  }

  private void setUpListeners() {
    logoutButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        promptLogout();
      }
    });
    addChannelButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showAddChannelDialog();
      }
    });
    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refreshLayout.setRefreshing(true);
        refreshChannels();
      }
    });
    channelsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setChannel(position);
      }
    });
  }

  @Override
  public void onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_chat, menu);
    this.leaveChannelMenuItem = menu.findItem(R.id.action_leave_channel);
    this.leaveChannelMenuItem.setVisible(false);
    this.deleteChannelMenuItem = menu.findItem(R.id.action_delete_channel);
    this.deleteChannelMenuItem.setVisible(false);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_leave_channel) {
      leaveCurrentChannel();
      return true;
    }
    if (id == R.id.action_delete_channel) {
      promptChannelDeletion();
    }

    return super.onOptionsItemSelected(item);
  }

  private String getStringResource(int id) {
    Resources resources = getResources();
    return resources.getString(id);
  }

  private void refreshChannels() {
    channelManager.populateChannels(new LoadChannelListener() {
      @Override
      public void onChannelsFinishedLoading(final List<Channel> channels) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            channelAdapter.setChannels(channels);
            refreshLayout.setRefreshing(false);
          }
        });
      }
    });
  }

  private void populateChannels() {
    channelManager.setChannelListener(this);
    channelManager.populateChannels(new LoadChannelListener() {
      @Override
      public void onChannelsFinishedLoading(List<Channel> channels) {
        channelAdapter = new ChannelAdapter(mainActivity, channels);
        channelsListView.setAdapter(channelAdapter);
        MainChatActivity.this.channelManager
            .joinOrCreateGeneralChannelWithCompletion(new StatusListener() {
              @Override
              public void onSuccess() {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    channelAdapter.notifyDataSetChanged();
                    stopActivityIndicator();
                    setChannel(0);
                  }
                });
              }

              @Override
              public void onError(ErrorInfo errorInfo) {
                showAlertWithMessage(getStringResource(R.string.generic_error));
              }
            });
      }
    });
  }

  private void setChannel(final int position) {
    List<Channel> channels = channelManager.getChannels();
    if (channels == null) {
      return;
    }
    final Channel currentChannel = chatFragment.getCurrentChannel();
    final Channel selectedChannel = channels.get(position);
    if (currentChannel != null && currentChannel.getSid().contentEquals(selectedChannel.getSid())) {
      drawer.closeDrawer(GravityCompat.START);
      return;
    }
    hideMenuItems(position);
    if (selectedChannel != null) {
      showActivityIndicator("Joining " + selectedChannel.getFriendlyName() + " channel");
      if (currentChannel != null && currentChannel.getStatus() == Channel.ChannelStatus.JOINED) {
        this.channelManager.leaveChannelWithHandler(currentChannel, new StatusListener() {
          @Override
          public void onSuccess() {
            joinChannel(selectedChannel);
          }

          @Override
          public void onError(ErrorInfo errorInfo) {
            stopActivityIndicator();
          }
        });
        return;
      }
      joinChannel(selectedChannel);
      stopActivityIndicator();
    } else {
      stopActivityIndicator();
      showAlertWithMessage(getStringResource(R.string.generic_error));
      System.out.println("Selected channel out of range");
    }
  }

  private void joinChannel(final Channel selectedChannel) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        chatFragment.setCurrentChannel(selectedChannel, new StatusListener() {
          @Override
          public void onSuccess() {
            MainChatActivity.this.stopActivityIndicator();
          }

          @Override
          public void onError(ErrorInfo errorInfo) {
          }
        });
        setTitle(selectedChannel.getFriendlyName());
        drawer.closeDrawer(GravityCompat.START);
      }
    });
  }

  private void hideMenuItems(final int position) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        MainChatActivity.this.leaveChannelMenuItem.setVisible(position != 0);
        MainChatActivity.this.deleteChannelMenuItem.setVisible(position != 0);
      }
    });
  }

  private void showAddChannelDialog() {
    String message = getStringResource(R.string.new_channel_prompt);
    AlertDialogHandler.displayInputDialog(message, context, new InputOnClickListener() {
      @Override
      public void onClick(String input) {
        if (input.length() == 0) {
          showAlertWithMessage(getStringResource(R.string.channel_name_required_message));
          return;
        }
        createChannelWithName(input);
      }
    });
  }

  private void createChannelWithName(String name) {
    name = name.trim();
    if (name.toLowerCase()
        .contentEquals(this.channelManager.getDefaultChannelName().toLowerCase())) {
      showAlertWithMessage(getStringResource(R.string.channel_name_equals_default_name));
      return;
    }
    this.channelManager.createChannelWithName(name, new StatusListener() {
      @Override
      public void onSuccess() {
        refreshChannels();
      }

      @Override
      public void onError(ErrorInfo errorInfo) {
        showAlertWithMessage(getStringResource(R.string.generic_error));
      }
    });
  }

  private void promptChannelDeletion() {
    String message = getStringResource(R.string.channel_delete_prompt_message);
    AlertDialogHandler.displayCancellableAlertWithHandler(message, context,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            deleteCurrentChannel();
          }
        });
  }

  private void deleteCurrentChannel() {
    Channel currentChannel = chatFragment.getCurrentChannel();
    channelManager.deleteChannelWithHandler(currentChannel, new StatusListener() {
      @Override
      public void onSuccess() {
      }

      @Override
      public void onError(ErrorInfo errorInfo) {
        showAlertWithMessage(getStringResource(R.string.message_deletion_forbidden));
      }
    });
  }

  private void leaveCurrentChannel() {
    final Channel currentChannel = chatFragment.getCurrentChannel();
    if (currentChannel.getStatus() == Channel.ChannelStatus.NOT_PARTICIPATING) {
      setChannel(0);
      return;
    }
    channelManager.leaveChannelWithHandler(currentChannel, new StatusListener() {
      @Override
      public void onSuccess() {
        setChannel(0);
      }

      @Override
      public void onError(ErrorInfo errorInfo) {
        stopActivityIndicator();
      }
    });
  }

  private void checkTwilioClient() {
    showActivityIndicator(getStringResource(R.string.loading_channels_message));
    chatClientManager = TwilioApplication.get().getChatClientManager();
    if (chatClientManager.getChatClient() == null) {
      initializeClient();
    } else {
      populateChannels();
    }
  }

  private void initializeClient() {
    chatClientManager.connectClient(new TaskCompletionListener<Void, String>() {
      @Override
      public void onSuccess(Void aVoid) {
        populateChannels();
      }

      @Override
      public void onError(String errorMessage) {
        stopActivityIndicator();
        showAlertWithMessage("Client connection error");
      }
    });
  }

  private void promptLogout() {
    final String message = getStringResource(R.string.logout_prompt_message);
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        AlertDialogHandler.displayCancellableAlertWithHandler(message, context,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                SessionManager.getInstance().logoutUser();
                showLoginActivity();
              }
            });
      }
    });

  }

  private void showLoginActivity() {
    Intent launchIntent = new Intent();
    launchIntent.setClass(getApplicationContext(), LoginActivity.class);
    startActivity(launchIntent);
    finish();
  }

  private void setUsernameTextView() {
    String username =
        SessionManager.getInstance().getUserDetails().get(SessionManager.KEY_USERNAME);
    usernameTextView.setText(username);
  }

  private void stopActivityIndicator() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (progressDialog.isShowing()) {
          progressDialog.dismiss();
        }
      }
    });
  }

  private void showActivityIndicator(final String message) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        progressDialog = new ProgressDialog(MainChatActivity.this.mainActivity);
        progressDialog.setMessage(message);
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
      }
    });
  }

  private void showAlertWithMessage(final String message) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        AlertDialogHandler.displayAlertWithMessage(message, context);
      }
    });
  }

  @Override
  public void onChannelAdd(Channel channel) {
    System.out.println("Channel Added");
    refreshChannels();
  }

  @Override
  public void onChannelDelete(final Channel channel) {
    System.out.println("Channel Deleted");
    Channel currentChannel = chatFragment.getCurrentChannel();
    if (channel.getSid().contentEquals(currentChannel.getSid())) {
      chatFragment.setCurrentChannel(null, null);
      setChannel(0);
    }
    refreshChannels();
  }

  @Override
  public void onChannelInvite(Channel channel) {

  }

  @Override
  public void onChannelSynchronizationChange(Channel channel) {

  }

  @Override
  public void onError(ErrorInfo errorInfo) {

  }

  @Override
  public void onUserInfoChange(UserInfo userInfo, UserInfo.UpdateReason updateReason) {

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

  @Override
  public void onChannelChange(Channel channel) {
  }
}

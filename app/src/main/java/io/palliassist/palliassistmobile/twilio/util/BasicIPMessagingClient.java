package io.palliassist.palliassistmobile.twilio.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.twilio.common.TwilioAccessManager;
import com.twilio.common.TwilioAccessManagerFactory;
import com.twilio.common.TwilioAccessManagerListener;
import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.Constants.StatusListener;
import com.twilio.ipmessaging.Constants.InitListener;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.ipmessaging.TwilioIPMessagingClient;
import com.twilio.ipmessaging.TwilioIPMessagingSDK;

import io.palliassist.palliassistmobile.twilio.ui.ChannelActivity;

public class BasicIPMessagingClient implements IPMessagingClientListener, TwilioAccessManagerListener {
    private static final String TAG = "BasicIPMessagingClient";
    private TwilioIPMessagingClient ipMessagingClient;
    private Context context;
    private static String capabilityToken;
    private TwilioAccessManager accessMgr;
    private Handler loginListenerHandler;
    private String urlString;

    public BasicIPMessagingClient(Context context) {
        super();
        this.context = context;
    }

    public void setCapabilityToken(String capabilityToken) {
        this.capabilityToken = capabilityToken;
    }

    public static String getCapabilityToken() {
        return capabilityToken;
    }


    public void doLogin(final ILoginListener listener, final String url) {
        TwilioIPMessagingSDK.initializeSDK(context, new InitListener()
        {
            @Override
            public void onInitialized()
            {
                ipMessagingClient = TwilioIPMessagingSDK.createIPMessagingClientWithToken(capabilityToken, BasicIPMessagingClient.this);
                if(ipMessagingClient != null) {
                    Intent intent = new Intent(context,ChannelActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    ipMessagingClient.setIncomingIntent(pendingIntent);
                    if(listener != null) {
                        listener.onLoginFinished();
                    }
                } else {
                    listener.onLoginError("ipMessagingClient is null");
                }
            }

            @Override
            public void onError(Exception error)
            {

            }
        });
    }

    @Override
    public void onChannelAdd(Channel channel) {
        if(channel != null) {
            Log.d(TAG, "A Channel :" + channel.getFriendlyName() + " got added");
        } else {
            Log.d(TAG, "Received onChannelAdd event.");
        }
    }

    @Override
    public void onChannelChange(Channel channel) {
        if(channel != null) {
            Log.d(TAG, "Channel Name : "+ channel.getFriendlyName() + " got Changed");
        } else {
            Log.d(TAG, "received onChannelChange event.");
        }
    }

    @Override
    public void onChannelDelete(Channel channel) {
        if(channel != null) {
            Log.d(TAG, "A Channel :"+ channel.getFriendlyName() + " got deleted");
        } else {
            Log.d(TAG, "received onChannelDelete event.");
        }
    }

    @Override
    public void onError(int errorCode, String errorText) {
        Log.d(TAG, "Received onError event.");
    }

    @Override
    public void onAttributesChange(String attributes) {
        Log.d(TAG, "Received onAttributesChange event.");
    }



    @Override
    public void onAccessManagerTokenExpire(TwilioAccessManager arg0) {
        Log.d(TAG, "Received AccessManager:onAccessManagerTokenExpire.");
    }

    @Override
    public void onError(TwilioAccessManager arg0, String arg1) {
        Log.d(TAG, "Received AccessManager:onError.");
    }

    @Override
    public void onTokenUpdated(TwilioAccessManager arg0) {
        Log.d(TAG, "Received AccessManager:onTokenUpdated.");
    }

    private Handler setupListenerHandler() {
        Looper looper;
        Handler handler;
        if((looper = Looper.myLooper()) != null) {
            handler = new Handler(looper);
        } else if((looper = Looper.getMainLooper()) != null) {
            handler = new Handler(looper);
        } else {
            throw new IllegalArgumentException("Channel Listener must have a Looper.");
        }
        return handler;
    }

    public TwilioIPMessagingClient getIpMessagingClient() {
        return ipMessagingClient;
    }



    private void createClientWithToken(ILoginListener listener) {
        ipMessagingClient = TwilioIPMessagingSDK.createIPMessagingClientWithToken(this.capabilityToken, BasicIPMessagingClient.this);
        if(ipMessagingClient != null) {
            if(listener != null) {
                listener.onLoginFinished();
            }
        } else {
            listener.onLoginError("ipMessagingClient is null");
        }
    }


}

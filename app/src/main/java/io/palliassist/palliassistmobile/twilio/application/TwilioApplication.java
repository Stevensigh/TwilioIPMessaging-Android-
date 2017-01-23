package io.palliassist.palliassistmobile.twilio.application;


import android.app.Application;

import io.palliassist.palliassistmobile.twilio.util.BasicIPMessagingClient;


public class TwilioApplication extends Application {
    private BasicIPMessagingClient rtdJni;
    private static TwilioApplication instance;

    public static TwilioApplication get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TwilioApplication.instance = this;
        rtdJni = new BasicIPMessagingClient(getApplicationContext());

    }

    public BasicIPMessagingClient getBasicClient() {
        return this.rtdJni;
    }

    public BasicIPMessagingClient getRtdJNI() {
        return this.rtdJni;
    }
}
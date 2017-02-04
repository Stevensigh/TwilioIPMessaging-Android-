package io.palliassist.palliassistmobile.twilio.application;

import android.app.Application;

import io.palliassist.palliassistmobile.twilio.chat.ChatClientManager;

public class TwilioApplication extends Application {
    private static TwilioApplication instance;
    private ChatClientManager basicClient;

    public static TwilioApplication get() {
        return instance;
    }

    public ChatClientManager getChatClientManager() {
        return this.basicClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        TwilioApplication.instance = this;
        basicClient = new ChatClientManager(getApplicationContext());
    }
}
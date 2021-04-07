package com.edward_costache.stay_fitrpg;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class BaseApplication extends Application {
    public static final String CHANNEL_INVITES_ID = "channelInvites";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel()
    {
        // Notification channel is not supported under API level 26, AKA Android Oreo
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel invites_channel = new NotificationChannel(CHANNEL_INVITES_ID, "Invite Notifications", NotificationManager.IMPORTANCE_HIGH);
            invites_channel.setDescription("This is a notification channel for the invites you receive from other users");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(invites_channel);
        }
    }
}

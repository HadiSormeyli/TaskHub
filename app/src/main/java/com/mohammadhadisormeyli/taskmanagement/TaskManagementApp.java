package com.mohammadhadisormeyli.taskmanagement;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class TaskManagementApp extends Application {

    public static final String CHANNEL_ID = "my_channel_id";
    private static final String CHANNEL_NAME = "My Channel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    @Override
    protected void attachBaseContext(Context base) {
        Locale.setDefault(Locale.ENGLISH);
        Configuration config = new Configuration();
        config.setLocale(Locale.ENGLISH);
        super.attachBaseContext(base.createConfigurationContext(config));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

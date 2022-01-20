package com.example.talk2me.helper;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

public class Notifications extends Application {
    public static final String CHANNEL_1_ID = "channel1";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){     //so depois do android Oreo, 4.0 e que isto funciona
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_1_ID,
                    "T2M Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.argb(0, 153, 0,204));       //se a luz nao funcionar mudar o alpha
            channel.setDescription("This is Channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}

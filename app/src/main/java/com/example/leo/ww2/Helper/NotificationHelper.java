package com.example.leo.ww2.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.leo.ww2.R;

public class NotificationHelper extends ContextWrapper {

    private static final String TAG = "NotificationHelper";
    public static final String MY_CHANNEL_ID = "com.example.leo.ww2";
    private static final String MY_CHANNEL_NAME = "ww2";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    //Later use when API>=26
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        Log.d(TAG, "createChannel: ");
        NotificationChannel channel = new NotificationChannel(
                MY_CHANNEL_ID,
                MY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public NotificationManager getManager() {
        if (manager == null)
            manager = getSystemService(NotificationManager.class); // or use this replace: Context.NOTIFICATION_SERVICE
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder setMyNotificationContent(String title, String body, PendingIntent contentIntent , Uri soundUri){
        return new android.app.Notification.Builder(getApplicationContext(), MY_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setSound(soundUri)
                .setAutoCancel(false);
    }

    //
    public NotificationCompat.Builder setNotificationContent(String title, String body, PendingIntent contentIntent , Uri soundUri){
        return new NotificationCompat.Builder(this, MY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
    }
}

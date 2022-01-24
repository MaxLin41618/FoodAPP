package com.example.leo.ww2.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.leo.ww2.Common.Common;
import com.example.leo.ww2.Helper.NotificationHelper;
import com.example.leo.ww2.OrderStatus;
import com.example.leo.ww2.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFireBaseMessaging extends FirebaseMessagingService {

    private static final String TAG = "MyFireBaseMessaging";

    //接收到訊息就執行
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendNotificationAPI26(remoteMessage);
            Log.d(TAG, "sendNotificationAPI26: ");
        } else {
            sendNotification(remoteMessage);
            Log.d(TAG, "sendNotification: ");
        }
    }

    // >=API26
    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String title = notification.getTitle();
        String content = notification.getBody();

        createNotificationChannel();

        //TAP action
        Intent intent = new Intent(this, OrderStatus.class);
        /*intent.putExtra(Common.PHONE_TEXT, Common.currentUser.getPhone());*/
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);  //back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(19, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationHelper.MY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(new Random().nextInt(), builder.build());

        /*
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //if activity on top then use it
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 90, intent, 0);
        //show notification
        NotificationHelper helper = new NotificationHelper(this);
        Notification.Builder builder = helper.setMyNotificationContent(title, content, pendingIntent, defaultSoundUri);
        helper.getManager().notify(new Random().nextInt(), builder.build()); //Gen random Id for notification
        */
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        //TAP action
        Intent intent = new Intent(this, OrderStatus.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 5, intent, 0);
        //show notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationHelper.MY_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        /*NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);*/
        notificationManager.notify(0, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NotificationHelper.MY_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

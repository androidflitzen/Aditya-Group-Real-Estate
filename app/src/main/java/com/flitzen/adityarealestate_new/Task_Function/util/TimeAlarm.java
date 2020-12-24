package com.flitzen.adityarealestate_new.Task_Function.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import android.util.Log;

/*import com.asu.remind.R;
import com.asu.remind.activity.ShowReminderMessage;*/
import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.Task_Function.activity.ShowReminderMessage;

/**
 * Created by asu on 17-Aug-16.
 */
public class TimeAlarm extends BroadcastReceiver {

    private NotificationManager nm;
    private String event;
    private Notification noti;
    String channel_id = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        event = extras.getString("event");
        Log.v("TAG","=== event onReceive "+event);
        Bitmap bigIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // when you click on the notification
        Intent intent2 = new Intent(context, ShowReminderMessage.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent2.putExtra("ReminderMsg", event);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_ONE_SHOT);



        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);



        int importance = NotificationManager.IMPORTANCE_HIGH;


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id,
                    context.getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        // notificationManager.notify(Utility.getNotiCount(MyFirebaseMessagingService.this), notificationBuilder.build());

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,channel_id)
                .setContentTitle("Reminder")
                .setContentText(event)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.launcher_icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(0, notificationBuilder.build());




    }
}


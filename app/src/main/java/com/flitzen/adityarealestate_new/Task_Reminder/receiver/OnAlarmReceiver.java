package com.flitzen.adityarealestate_new.Task_Reminder.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;

import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.Task_Reminder.activity.TaskEditActivity;
import com.flitzen.adityarealestate_new.Task_Reminder.provider.TaskProvider;


/**
 * Created by Sumir on 05-06-2017.
 */

public class OnAlarmReceiver extends BroadcastReceiver {

    String channel_id = "";
    private Notification notification;
    Uri soundUri;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Uri ringtoneUri;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultToneKey = context.getString(R.string.tone_default_key);

        String ringtonePreference = sharedPreferences.getString(defaultToneKey, "content://settings/system/notification_sound");
        ringtoneUri = Uri.parse(ringtonePreference);

        soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_new_tone);

        Intent taskEditIntent = new Intent(context, TaskEditActivity.class);

        long taskId = intent.getLongExtra(TaskProvider.COLUMN_TASKID, -1);
        String title = intent.getStringExtra(TaskProvider.COLUMN_TITLE);
        String date = intent.getStringExtra(TaskProvider.COLUMN_DATE_TIME);
        taskEditIntent.putExtra(TaskEditActivity.EXTRA_TASKID, taskId);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) taskId, taskEditIntent, PendingIntent.FLAG_ONE_SHOT);


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
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.launcher_icon)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);



      /*  NotificationCompat.Builder note = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notify_new_task_title))
                .setContentText(title)
                .setContentInfo(date)
                .setSmallIcon(R.drawable.launcher_icon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(ringtoneUri);*/

        notificationBuilder.setVibrate(new long[]{500, 1000});
        notificationManager.notify((int) taskId, notificationBuilder.build());

    }
}

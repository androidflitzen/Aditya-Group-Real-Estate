package com.flitzen.adityarealestate_new.Task_Reminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import androidx.annotation.RequiresApi;


import com.flitzen.adityarealestate_new.Task_Reminder.provider.TaskProvider;
import com.flitzen.adityarealestate_new.Task_Reminder.util.ReminderManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sumir on 06-06-2017.
 */

public class OnBootReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        Cursor cursor = context.getContentResolver().query(TaskProvider.CONTENT_URI,null,null,null,null);

        if(cursor == null){
            return;
        }

        try{
            cursor.moveToFirst();
            int taskIdColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TASKID);
            int dateTimeColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_DATE_TIME);
            int titleColumnIndex = cursor.getColumnIndex(TaskProvider.COLUMN_TITLE);

            while (!cursor.isAfterLast())
            {
                long taskId = cursor.getLong(taskIdColumnIndex);
                long dateTime = cursor.getLong(dateTimeColumnIndex);
                String title = cursor.getString(titleColumnIndex);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(dateTime));

                ReminderManager.setReminder(context,taskId,title,calendar);
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
    }
}

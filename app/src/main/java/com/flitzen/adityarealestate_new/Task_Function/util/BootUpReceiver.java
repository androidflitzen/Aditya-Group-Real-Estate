package com.flitzen.adityarealestate_new.Task_Function.util;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flitzen.adityarealestate_new.Task_Function.activity.Task_MainActivity;


/**
 * Created by asu on 21-Aug-16.
 */
public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, Task_MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}

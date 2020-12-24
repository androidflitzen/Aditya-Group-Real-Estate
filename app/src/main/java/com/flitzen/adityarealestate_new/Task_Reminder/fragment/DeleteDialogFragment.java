package com.flitzen.adityarealestate_new.Task_Reminder.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;


import androidx.fragment.app.DialogFragment;

import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.Task_Reminder.activity.TaskListActivity;
import com.flitzen.adityarealestate_new.Task_Reminder.provider.TaskProvider;
import com.flitzen.adityarealestate_new.Task_Reminder.util.ReminderManager;


/**
 * Created by Sumir on 31-05-2017.
 */

public class DeleteDialogFragment extends DialogFragment {


    public static DeleteDialogFragment newInstance(String title,long id)
    {
        DeleteDialogFragment alertDialogFragment = new DeleteDialogFragment();
        Bundle args = new Bundle();
        args.putString(TaskListActivity.TITLE,title);
        args.putLong(TaskEditFragment.TASK_ID,id);

        alertDialogFragment.setArguments(args);
        return alertDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String title = getArguments().getString(TaskListActivity.TITLE);
        final long id = getArguments().getLong(TaskEditFragment.TASK_ID);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_task)
                .setMessage(title)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel,null)
                .setPositiveButton(R.string.delete_task,new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteTask(getActivity(),id);
                            }
                        }
                ).show();
    }

    void deleteTask(Context context, long id)
    {
        context.getContentResolver().delete(ContentUris.withAppendedId(TaskProvider.CONTENT_URI,id),null,null);
        ReminderManager.removeReminder(context);
    }

}

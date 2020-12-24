package com.flitzen.adityarealestate_new.Task_Reminder.activity;

import android.content.Intent;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.Task_Reminder.fragment.DeleteDialogFragment;
import com.flitzen.adityarealestate_new.Task_Reminder.interfaces.OnDeleteTask;
import com.flitzen.adityarealestate_new.Task_Reminder.interfaces.OnEditTask;


public class TaskListActivity extends AppCompatActivity implements OnEditTask, OnDeleteTask {

    public static final String TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

    }

    @Override
    public void editTask(long id) {
        startActivity(new Intent(this,TaskEditActivity.class).putExtra(TaskEditActivity.EXTRA_TASKID,id));
    }

    @Override
    public void showDialog(String message,long id) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DialogFragment newFragment = DeleteDialogFragment.newInstance(message,id);
        newFragment.show(fragmentTransaction,"deleteDialog");
    }
}

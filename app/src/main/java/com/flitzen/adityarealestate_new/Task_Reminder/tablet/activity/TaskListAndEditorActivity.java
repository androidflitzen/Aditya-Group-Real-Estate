package com.flitzen.adityarealestate_new.Task_Reminder.tablet.activity;

import android.os.Build;
import android.os.Bundle;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.Task_Reminder.fragment.AlertDialogFragment;
import com.flitzen.adityarealestate_new.Task_Reminder.fragment.DeleteDialogFragment;
import com.flitzen.adityarealestate_new.Task_Reminder.fragment.TaskEditFragment;
import com.flitzen.adityarealestate_new.Task_Reminder.interfaces.OnDeleteTask;
import com.flitzen.adityarealestate_new.Task_Reminder.interfaces.OnEditFinished;
import com.flitzen.adityarealestate_new.Task_Reminder.interfaces.OnEditTask;
import com.flitzen.adityarealestate_new.Task_Reminder.interfaces.onSaveTask;


/**
 * Created by Sumir on 07-06-2017.
 */

public class TaskListAndEditorActivity extends AppCompatActivity implements onSaveTask, OnEditTask, OnEditFinished, OnDeleteTask {

    TaskEditFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_and_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTablet);
        setSupportActionBar(toolbar);
    }
    @Override
    public void editTask(long id) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = fragmentManager.findFragmentByTag(TaskEditFragment.DEFAULT_FRAGMENT_TAG);
        if(fragment != null) {
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }

        fragment = TaskEditFragment.newInstance(id);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.edit_container, fragment, TaskEditFragment.DEFAULT_FRAGMENT_TAG);

        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();

    }

    @Override
    public void finishEditingTask() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DialogFragment newFragment = new AlertDialogFragment();
        newFragment.show(fragmentTransaction,"saveDialog");
  }

    @Override
    public void showDialog(String message, long id) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DialogFragment newFragment = DeleteDialogFragment.newInstance(message,id);
        newFragment.show(fragmentTransaction,"deleteDialog");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void doSave() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = (TaskEditFragment) fragmentManager.findFragmentByTag(TaskEditFragment.DEFAULT_FRAGMENT_TAG);
        fragment.save();


        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = fragmentManager.findFragmentByTag(TaskEditFragment.DEFAULT_FRAGMENT_TAG);
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}

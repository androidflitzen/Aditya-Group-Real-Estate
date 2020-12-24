package com.flitzen.adityarealestate_new.Task_Reminder.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.flitzen.adityarealestate_new.Task_Reminder.fragment.PreferencesFragment;


/**
 * Created by Sumir on 06-06-2017.
 */

public class PreferencesActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
    }
}

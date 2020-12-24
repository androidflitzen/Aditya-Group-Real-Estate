package com.flitzen.adityarealestate_new.Task_Reminder.fragment;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.text.method.DigitsKeyListener;

import com.flitzen.adityarealestate_new.R;


/**
 * Created by Sumir on 06-06-2017.
 */

public class PreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.task_preferences);

        EditTextPreference timeDefault = (EditTextPreference)findPreference(getString(R.string.pref_default_time_from_now_key));
        timeDefault.getEditText().setKeyListener(DigitsKeyListener.getInstance());
    }
}

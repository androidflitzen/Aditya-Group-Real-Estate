package com.flitzen.adityarealestate_new.Task_Reminder.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;


import android.app.TimePickerDialog;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;


import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.palette.graphics.Palette;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.Task_Reminder.activity.TaskEditActivity;
import com.flitzen.adityarealestate_new.Task_Reminder.adapter.TaskListAdapter;
import com.flitzen.adityarealestate_new.Task_Reminder.interfaces.OnEditFinished;
import com.flitzen.adityarealestate_new.Task_Reminder.provider.TaskProvider;
import com.flitzen.adityarealestate_new.Task_Reminder.util.ReminderManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Sumir on 29-05-2017.
 */

public class TaskEditFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
        , LoaderManager.LoaderCallbacks<Cursor> {


    static final String TASK_ID = "taskId";
    public static final String DEFAULT_FRAGMENT_TAG = "taskEditFragment";
    private static final int MENU_SAVE = 1;
    static final String TASK_DATE_AND_TIME = "taskDateAndTime";

    View rootView;
    EditText titleText;
    EditText notesText;
    ImageView imageView;
    TextView dateButton;
    TextView timeButton;
    Button btnSave;

    long taskId;
    Calendar taskDateAndTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            taskId = arguments.getLong(TaskEditActivity.EXTRA_TASKID, 0L);
        }

        if (savedInstanceState != null) {
            taskId = savedInstanceState.getLong(TASK_ID);
            taskDateAndTime = (Calendar) savedInstanceState.getSerializable(TASK_DATE_AND_TIME);
        }

        if (taskDateAndTime == null) {
            taskDateAndTime = Calendar.getInstance();
        }

        NotificationManager note = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        note.cancel((int) taskId);


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(TASK_ID, taskId);
        outState.putSerializable(TASK_DATE_AND_TIME, taskDateAndTime);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View v = layoutInflater.inflate(R.layout.fragment_task_edit, container, false);
        rootView = v.getRootView();
        titleText = (EditText) v.findViewById(R.id.title);
        notesText = (EditText) v.findViewById(R.id.notes);
        imageView = (ImageView) v.findViewById(R.id.image);
        dateButton = (EditText) v.findViewById(R.id.task_date);
        timeButton = (EditText) v.findViewById(R.id.task_time);
        btnSave = (Button) v.findViewById(R.id.btnSave);


        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

              if (titleText.getText().toString().equals("")){
                  titleText.setError(getString(R.string.isEmpty));
              }else if( dateButton.getText().toString().equals("")){

                  dateButton.setError("Select Date");

                }else if(timeButton.getText().toString().equals("")){
                  dateButton.setError("Select Time");

              }else {
                  save();
                  getActivity().finish();

              }



            }
        });

        if (taskId == 0) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String defaultTitleKey = getString(R.string.pref_task_title_key);
            String defaultTimeKey = getString(R.string.pref_default_time_from_now_key);

            String defaultTitle = sharedPreferences.getString(defaultTitleKey, null);
            String defaultTime = sharedPreferences.getString(defaultTimeKey, null);

            if (defaultTitle != null)
                titleText.setText(defaultTitle);

            if (defaultTime != null && defaultTime.length() > 0)
                taskDateAndTime.add(Calendar.MINUTE, Integer.parseInt(defaultTime));

            updateDateAndTimeButtons();
        } else {
            getLoaderManager().initLoader(0, null, this);
        }

        return v;
    }

    private void updateDateAndTimeButtons() {
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        String timeForButton = timeFormat.format(taskDateAndTime.getTime());
        timeButton.setText(timeForButton);

        DateFormat dateFormat = DateFormat.getDateInstance();
        String dateForButton = dateFormat.format(taskDateAndTime.getTime());
        dateButton.setText(dateForButton);

    }

    private void showDatePicker() {

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        DatePickerDialogFragment newFragment = DatePickerDialogFragment.newInstance(taskDateAndTime);
        newFragment.show(fragmentTransaction, "datePicker");
    }

    private void showTimePicker() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        TimePickerDialogFragment newFragment = TimePickerDialogFragment.newInstance(taskDateAndTime);
        newFragment.show(fragmentTransaction, "timePicker");
    }

    public static TaskEditFragment newInstance(long id) {
        TaskEditFragment fragment = new TaskEditFragment();
        Bundle args = new Bundle();
        args.putLong(TaskEditActivity.EXTRA_TASKID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);

        menu.add(0, MENU_SAVE, 0, R.string.confirm).setIcon(android.R.drawable.ic_menu_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case MENU_SAVE:
                if (titleText.getText().toString().equals(""))
                    titleText.setError(getString(R.string.isEmpty));

                else {
                    Log.d("", "in task edit fragment calling finish Editing task");
                    ((OnEditFinished) getActivity()).finishEditingTask();
                }
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }  */


    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        taskDateAndTime.set(Calendar.YEAR, year);
        taskDateAndTime.set(Calendar.MONTH, monthOfYear);
        taskDateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateDateAndTimeButtons();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        taskDateAndTime.set(Calendar.HOUR_OF_DAY, hour);
        taskDateAndTime.set(Calendar.MINUTE, minute);
        updateDateAndTimeButtons();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void save() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskProvider.COLUMN_TITLE, titleText.getText().toString());
        contentValues.put(TaskProvider.COLUMN_NOTES, notesText.getText().toString());
        contentValues.put(TaskProvider.COLUMN_DATE_TIME, taskDateAndTime.getTimeInMillis());

        if (taskId == 0) {
            Uri itemUri = getActivity().getContentResolver().insert(TaskProvider.CONTENT_URI, contentValues);
            taskId = ContentUris.parseId(itemUri);
        } else {
            Uri uri = ContentUris.withAppendedId(TaskProvider.CONTENT_URI, taskId);
            int count = getActivity().getContentResolver().update(uri, contentValues, null, null);

            if (count != 1)
                throw new IllegalArgumentException("Unable to update" + taskId);
        }

        Toast.makeText(getActivity(), getString(R.string.task_saved_message), Toast.LENGTH_SHORT).show();
        ReminderManager.setReminder(getActivity(), taskId, titleText.getText().toString(), taskDateAndTime);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri taskUri = ContentUris.withAppendedId(TaskProvider.CONTENT_URI, taskId);
        return new CursorLoader(getActivity(), taskUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor task) {
        if (task.getCount() == 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((OnEditFinished) getActivity()).finishEditingTask();
                }
            });
            return;
        }
        titleText.setText(task.getString(task.getColumnIndexOrThrow(TaskProvider.COLUMN_TITLE)));
        notesText.setText(task.getString(task.getColumnIndexOrThrow(TaskProvider.COLUMN_NOTES)));

        Long dateInMillis = task.getLong(task.getColumnIndexOrThrow(TaskProvider.COLUMN_DATE_TIME));
        Date date = new Date(dateInMillis);
        taskDateAndTime.setTime(date);
        if (TaskListFragment.internetPresent)
            Picasso.with(getActivity()).load(TaskListAdapter.getImageUrlForTask(taskId))
                    .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Activity activity = getActivity();
                                    if (activity == null)
                                        return;

                                    if (!Utils.SHOULD_USE_PALETTE)
                                        return;

                                    Bitmap bitmap = ((BitmapDrawable) imageView
                                            .getDrawable())
                                            .getBitmap();
                                    Palette palette = Palette.generate(bitmap, 32);
                                    int bgColor = palette.getLightMutedColor(0);

                                    if (bgColor != 0)
                                        rootView.setBackgroundColor(bgColor);

                                }

                                @Override
                                public void onError() {

                                }
                            }

                    );

        updateDateAndTimeButtons();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

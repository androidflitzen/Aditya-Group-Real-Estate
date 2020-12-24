package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Adapter.AttachmentGridAdapter;
import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Items_Attachment;
import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.reminder.AlarmService;
import com.flitzen.adityarealestate_new.reminder.ReminderContract;
import com.flitzen.adityarealestate_new.reminder.ReminderDataHelper;
import com.flitzen.adityarealestate_new.reminder.ReminderItem;
import com.flitzen.adityarealestate_new.reminder.ReminderParams;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Activity_Task_Details extends AppCompatActivity {

    Activity mActivity;
    ProgressDialog prd;

    String type = "";

    TextView txt_task_subject, txt_task_desc, txt_task_date, txt_task_time;
    View ll_plot, view_complete_task, view_remove_task, view_edit_task;
    GridView gvAttachments;
    ArrayList<Items_Attachment> attachmenList = new ArrayList<>();
    private ContentResolver mContentResolver;
    private ReminderItem mData;
    private Calendar mAlertTime;
    private String REMINDER_DATE, REMINDER_TIME;
    ReminderDataHelper mDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        mActivity = Activity_Task_Details.this;
        mContentResolver = getContentResolver();
        mAlertTime = Calendar.getInstance();
        mDB = new ReminderDataHelper(mActivity);
        getSupportActionBar().setTitle(Html.fromHtml("Task Details"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().hasExtra(ReminderParams.TASK_ID)) {
            Aditya.ID = getIntent().getStringExtra(ReminderParams.TASK_ID);
            mContentResolver.delete(ReminderContract.Notes.CONTENT_URI, ReminderParams.TASK_ID + " = ? ", new String[]{Aditya.ID});
        } else {
            Cursor cursor = mDB.getTaskData(Aditya.ID);
            if (cursor.moveToFirst()) {
                mData = new ReminderItem();
                mData.setId(cursor.getInt(cursor.getColumnIndex(ReminderParams.ID)));
                mData.setmTaskID(cursor.getString(cursor.getColumnIndex(ReminderParams.TASK_ID)));
                mData.setTitle(cursor.getString(cursor.getColumnIndex(ReminderParams.TITLE)));
                mData.setContent(cursor.getString(cursor.getColumnIndex(ReminderParams.CONTENT)));
                mData.setTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndex(ReminderParams.TIME))));
            }
            cursor.close();

        }

        txt_task_subject = (TextView) findViewById(R.id.txt_task_subject);
        txt_task_desc = (TextView) findViewById(R.id.txt_task_desc);
        txt_task_date = (TextView) findViewById(R.id.txt_task_date);
        txt_task_time = (TextView) findViewById(R.id.txt_task_time);

        view_complete_task = findViewById(R.id.view_complete_task);
        view_remove_task = findViewById(R.id.view_remove_task);
        view_edit_task = findViewById(R.id.view_edit_task);
        gvAttachments = (GridView) findViewById(R.id.gvAttachments);

        view_complete_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeTask();
            }
        });

        view_remove_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelDialog();
            }
        });
        view_edit_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateTaskDialog();
            }
        });
        getTaskDetail();
    }

    private void openUpdateTaskDialog() {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_task_add, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);

        final EditText edt_task_sub = (EditText) promptsView.findViewById(R.id.edt_task_sub);
        final EditText edt_task_desc = (EditText) promptsView.findViewById(R.id.edt_task_desc);
        if (mData != null) {
            edt_task_sub.setText(mData.getTitle());
            edt_task_desc.setText(mData.getContent());
        }

        TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        Button btn_add_task = (Button) promptsView.findViewById(R.id.btn_add_task);
        btn_add_task.setText("Update");

        /*RelativeLayout relTime = (RelativeLayout) promptsView.findViewById(R.id.relTime);
        RelativeLayout relDate = (RelativeLayout) promptsView.findViewById(R.id.relDate);

        final TextView tvSelectTime = (TextView) promptsView.findViewById(R.id.tvSelectTime);
        final TextView tvSelectDate = (TextView) promptsView.findViewById(R.id.tvSelectDate);
        relTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker(tvSelectTime);
            }
        });

        relDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(tvSelectDate);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });*/

        btn_add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_task_sub.getText().toString().trim().equals("")) {
                    edt_task_sub.setError("Task Subject");
                    edt_task_sub.requestFocus();
                    return;
                } else if (edt_task_desc.getText().toString().trim().equals("")) {
                    edt_task_desc.setError("Task Description");
                    edt_task_desc.requestFocus();
                    return;
                } else {
                    if (REMINDER_TIME != null || REMINDER_DATE != null) {
                        if (REMINDER_TIME == null) {
                            new CToast(mActivity).simpleToast("Please Select Reminder Time", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        } else if (REMINDER_DATE == null) {
                            new CToast(mActivity).simpleToast("Please Select Reminder Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        } else {
                            alertDialog.dismiss();
                            editTask(edt_task_sub.getText().toString().trim(), edt_task_desc.getText().toString().trim(), true);
                        }
                    } else {
                        alertDialog.dismiss();
                        editTask(edt_task_sub.getText().toString().trim(), edt_task_desc.getText().toString().trim(), true);
                    }
                }
            }
        });

        alertDialog.show();
    }

    public void editTask(final String task_subject, final String task_desc, final boolean setReminder) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TASKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hidePrd();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Response Tasks Add", "====TASKS " + response);
                    if (jsonObject.getInt("result") == 1) {
                        if (setReminder) {
                            REMINDER_TIME = null;
                            REMINDER_DATE = null;
                            mData.setmTaskID(Aditya.ID);
                            mData.setTitle(task_subject);
                            mData.setContent(task_desc);
                            mData.setFrequency(0);
                            saveAlert(mData);
                        }
                        getTaskDetail();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }
                } catch (Exception e) {
                    new CToast(mActivity).simpleToast("Something went wrong...", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();
                new CToast(mActivity).simpleToast("Something went wrong...", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type", "Edit");
                params.put("id", Aditya.ID);
                params.put("Subject", task_subject);
                params.put("Description", task_desc);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    public void getTaskDetail() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.TASKS + "?type=Details&id=" + Aditya.ID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hidePrd();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Task detail", response);
                    if (jsonObject.getInt("result") == 1) {
                        //getSupportActionBar().setTitle(Html.fromHtml(jsonObject.getString("property_name")));
                        txt_task_subject.setText(jsonObject.getString("Subject"));
                        txt_task_desc.setText(jsonObject.getString("Description"));
                        txt_task_time.setText(jsonObject.getString("Task_time"));
                        if (jsonObject.has("Task_files")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("Task_files");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                Items_Attachment items_attachment = new Items_Attachment();
                                items_attachment.setId(jsonObj.getString("id"));
                                items_attachment.setFile(jsonObj.getString("File"));
                                attachmenList.add(items_attachment);
                            }
                        }
                        gvAttachments.setAdapter(new AttachmentGridAdapter(Activity_Task_Details.this, attachmenList));
                        String[] date = jsonObject.getString("Task_date").split("-");
                        String month = date[1];
                        String mm = Helper.getMonth(month);
                        txt_task_date.setText(date[0] + " " + mm + " " + date[2]);
                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void completeTask() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TASKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hidePrd();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Task complete", response);
                    if (jsonObject.getInt("result") == 1) {
                        finish();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type", "Complete");
                params.put("id", Aditya.ID);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Task_Details.this);
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure delete this task?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                deleteTask();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteTask() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TASKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hidePrd();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response task delete", response);
                    if (jsonObject.getInt("result") == 1) {
                        finish();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type", "Delete");
                params.put("id", Aditya.ID);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    public void showPrd() {
        prd = new ProgressDialog(mActivity);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }

    private void openDatePicker(final TextView tvSelectDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        REMINDER_DATE = day + "-" + (month + 1) + "-" + year;
                        tvSelectDate.setText(REMINDER_DATE);
                        mAlertTime.set(Calendar.YEAR, year);
                        mAlertTime.set(Calendar.MONTH, month);
                        mAlertTime.set(Calendar.DAY_OF_MONTH, day);
                        mData.setTimeInMillis(mAlertTime.getTimeInMillis());
                    }
                }, mAlertTime.get(Calendar.YEAR), mAlertTime.get(Calendar.MONTH),
                mAlertTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void openTimePicker(final TextView tvSelectTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String min = String.valueOf(minute);
                if (min.length() <= 1) {
                    min = "0" + minute;
                }
                REMINDER_TIME = hourOfDay + ":" + min;
                tvSelectTime.setText(REMINDER_TIME);
                mAlertTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mAlertTime.set(Calendar.MINUTE, minute);
                mAlertTime.set(Calendar.SECOND, 0);
               /* mTime = TIME_FORMAT.format(mAlertTime.getTime());
                mAlarmTime.put(ITEM_CONTENT, mTime);*/
                mData.setTimeInMillis(mAlertTime.getTimeInMillis());
            }
        }, mAlertTime.get(Calendar.HOUR_OF_DAY), mAlertTime.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void saveAlert(final ReminderItem item) {
        //if (item.getId() > 0) {
        Intent cancelPrevious = new Intent(mActivity, AlarmService.class);
        cancelPrevious.putExtra(ReminderParams.ID, item.getId());
        cancelPrevious.setAction(AlarmService.CANCEL);
        mActivity.startService(cancelPrevious);
        ContentValues values = new ContentValues();
        values.put(ReminderContract.Alerts.TASK_ID, item.getmTaskID());
        values.put(ReminderContract.Alerts.TITLE, item.getTitle());
        values.put(ReminderContract.Alerts.CONTENT, item.getContent());
        values.put(ReminderContract.Alerts.TIME, item.getTimeInMillis());
        values.put(ReminderContract.Alerts.FREQUENCY, item.getFrequency());
        Uri uri = ContentUris.withAppendedId(ReminderContract.Alerts.CONTENT_URI, item.getId());
        int result = mContentResolver.update(uri, values, null, null);
        Utils.showLog("==== result " + result);
        createAlarm(item.getId());
        /*} else {
            ContentValues values = new ContentValues();
            values.put(ReminderContract.Alerts.TYPE, ReminderType.ALERT.getName());
            values.put(ReminderContract.Alerts.TASK_ID, item.getmTaskID());
            values.put(ReminderContract.Alerts.TITLE, item.getTitle());
            values.put(ReminderContract.Alerts.CONTENT, item.getContent());
            values.put(ReminderContract.Alerts.TIME, item.getTimeInMillis());
            values.put(ReminderContract.Alerts.FREQUENCY, item.getFrequency());
            Uri uri = mContentResolver.insert(ReminderContract.Notes.CONTENT_URI, values);
            if (uri != null) {
                createAlarm(Integer.parseInt(uri.getLastPathSegment()));
            }
        }*/
    }

    private void createAlarm(int id) {
        Intent alarm = new Intent(mActivity, AlarmService.class);
        alarm.putExtra(ReminderParams.ID, id);
        alarm.setAction(AlarmService.CREATE);
        mActivity.startService(alarm);
    }
}

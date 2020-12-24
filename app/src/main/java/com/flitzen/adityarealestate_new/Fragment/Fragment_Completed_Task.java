package com.flitzen.adityarealestate_new.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.flitzen.adityarealestate_new.Activity.Activity_Complete_Task_Details;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Tasks_List;
import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.Item_Task_List;
import com.flitzen.adityarealestate_new.R;

import com.flitzen.adityarealestate_new.reminder.AlarmService;
import com.flitzen.adityarealestate_new.reminder.ReminderContract;
import com.flitzen.adityarealestate_new.reminder.ReminderItem;
import com.flitzen.adityarealestate_new.reminder.ReminderParams;
import com.flitzen.adityarealestate_new.reminder.ReminderType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Fragment_Completed_Task extends Fragment {

    ProgressDialog prd;

    FloatingActionButton fab_add_sites;
    SwipeRefreshLayout swipe_refresh;
    RecyclerView rec_sites_list;
    Adapter_Tasks_List adapter_tasks_list;
    ArrayList<Item_Task_List> itemList = new ArrayList<>();
    ArrayList<Item_Task_List> itemListTemp = new ArrayList<>();

    private EditText edtSearch;
    private ImageView imgClearSearch, ivAddAttachment;
    Activity activity;
    public final int IMG_REQUEST_CODE = 100;
    public final int FILE_REQUEST_CODE = 101;
    TextView tvFileName;
    private ContentResolver mContentResolver;
    private ReminderItem mData;
    private Calendar mAlertTime;
    File[] attachmentFile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_tasks_list, null);
        activity = getActivity();
        mContentResolver = activity.getContentResolver();
        mData = new ReminderItem();
        mAlertTime = Calendar.getInstance();
        edtSearch = (EditText) view.findViewById(R.id.edt_search);
        imgClearSearch = (ImageView) view.findViewById(R.id.img_clear_search);

        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                edtSearch.setText(null);
                edtSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

                getCompleteTaskList();

            }
        });

        rec_sites_list = (RecyclerView) view.findViewById(R.id.rec_sites_list);
        rec_sites_list.setLayoutManager(new LinearLayoutManager(activity));
        rec_sites_list.setHasFixedSize(true);
        adapter_tasks_list = new Adapter_Tasks_List(activity, itemList, false);
        rec_sites_list.setAdapter(adapter_tasks_list);
        fab_add_sites = (FloatingActionButton) view.findViewById(R.id.fab_add_sites);
        fab_add_sites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater localView = LayoutInflater.from(activity);
                View promptsView = localView.inflate(R.layout.dialog_task_add, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);

                final EditText edt_task_sub = (EditText) promptsView.findViewById(R.id.edt_task_sub);
                final EditText edt_task_desc = (EditText) promptsView.findViewById(R.id.edt_task_desc);

                TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                Button btn_add_task = (Button) promptsView.findViewById(R.id.btn_add_task);

                RelativeLayout relTime = (RelativeLayout) promptsView.findViewById(R.id.relTime);
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

                tvFileName = (TextView) promptsView.findViewById(R.id.tvFileName);
                LinearLayout layoutAttachment = (LinearLayout) promptsView.findViewById(R.id.layoutAttachment);
                ivAddAttachment = (ImageView) promptsView.findViewById(R.id.ivAddAttachment);
                layoutAttachment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFileChooser();
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

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
                            alertDialog.dismiss();
                            mData.setmTaskID("5");
                            mData.setTitle(edt_task_sub.getText().toString());
                            mData.setContent(edt_task_desc.getText().toString());
                            mData.setFrequency(0);
                            saveAlert(mData);
                            addTask(edt_task_sub.getText().toString().trim(), edt_task_desc.getText().toString().trim());
                        }
                    }
                });

                alertDialog.show();

            }
        });
        adapter_tasks_list.setOnItemClickListener(new Adapter_Tasks_List.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {

                Intent intent = new Intent(activity, Activity_Complete_Task_Details.class);
                intent.putExtra("is_complete_task","1");
                Aditya.ID = itemList.get(position).getId();
                startActivityForResult(intent, 001);

                /*LayoutInflater localView = LayoutInflater.from(activity);
                View promptsView = localView.inflate(R.layout.dialog_task_info, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();

                final TextView txt_task_status = (TextView) promptsView.findViewById(R.id.txt_task_status);
                final TextView txt_task_subject = (TextView) promptsView.findViewById(R.id.txt_task_subject);
                final TextView txt_task_desc = (TextView) promptsView.findViewById(R.id.txt_task_desc);
                final TextView txt_task_date = (TextView) promptsView.findViewById(R.id.txt_task_date);
                final TextView txt_task_time = (TextView) promptsView.findViewById(R.id.txt_task_time);

                View view_complete_task = promptsView.findViewById(R.id.view_complete_task);
                View view_remove_task = promptsView.findViewById(R.id.view_remove_task);

                txt_task_status.setText(itemList.get(position).getStatus());
                txt_task_subject.setText(itemList.get(position).getSubject());
                txt_task_desc.setText(itemList.get(position).getDescription());
                txt_task_date.setText(itemList.get(position).getTask_date());
                txt_task_time.setText(itemList.get(position).getTask_time());

                view_complete_task.setVisibility(View.GONE);
                txt_task_status.setBackgroundColor(getResources().getColor(R.color.approw_bg));

                view_remove_task.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        deleteTask(itemList.get(position).getId());
                    }
                });

                alertDialog.show();*/

            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int ii, int i1, int i2) {
                String word = edtSearch.getText().toString().trim().toLowerCase();
                itemList.clear();
                if (word.trim().isEmpty()) {
                    itemList.addAll(itemListTemp);
                    adapter_tasks_list.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < itemListTemp.size(); i++) {
                        if (itemListTemp.get(i).getSubject().toLowerCase().contains(word)) {
                            itemList.add(itemListTemp.get(i));
                        } else if (itemListTemp.get(i).getStatus().toLowerCase().contains(word)) {
                            itemList.add(itemListTemp.get(i));
                        } else if (itemListTemp.get(i).getDescription().toLowerCase().contains(word)) {
                            itemList.add(itemListTemp.get(i));
                        }
                    }
                    adapter_tasks_list.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    imgClearSearch.setVisibility(View.VISIBLE);
                else
                    imgClearSearch.setVisibility(View.GONE);

            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        imgClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText(null);
                edtSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getCompleteTaskList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 001) {
            getCompleteTaskList();
        } else if (requestCode == IMG_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files != null && files.size() > 0) {
                    String fileNames = "";
                    attachmentFile = new File[files.size()];
                    for (int i = 0; i < files.size(); i++) {
                        attachmentFile[i] = new File(files.get(i).getPath());
                        fileNames = fileNames + "" + attachmentFile[i].getName() + ",";
                    }
                    fileNames = fileNames.substring(0, fileNames.length() - 1);
                    if (ivAddAttachment != null) {
                        //ivAddAttachment.setImageBitmap(BitmapFactory.decodeFile(attachmentFile.getPath()));
                        ivAddAttachment.setVisibility(View.GONE);
                    }
                    tvFileName.setText(fileNames);
                }
                tvFileName.setMovementMethod(new ScrollingMovementMethod());
            }
        } else if (requestCode == FILE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files != null && files.size() > 0) {
                    String fileNames = "";
                    attachmentFile = new File[files.size()];
                    for (int i = 0; i < files.size(); i++) {
                        attachmentFile[i] = new File(files.get(i).getPath());
                        fileNames = fileNames + "" + attachmentFile[i].getName() + ",";
                    }
                    fileNames = fileNames.substring(0, fileNames.length() - 1);
                    tvFileName.setText(fileNames);
                    if (ivAddAttachment != null) {
                        //ivAddAttachment.setImageResource(R.drawable.ic_doc);
                        ivAddAttachment.setVisibility(View.GONE);
                    }
                }
                tvFileName.setMovementMethod(new ScrollingMovementMethod());
            }
        }
    }

    public void getCompleteTaskList() {
        //showPrd();
        swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.TASKS + "?type=New_List&Status=1", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //hidePrd();
                swipe_refresh.setRefreshing(false);
                try {
                    itemList.clear();
                    itemListTemp.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Sites List", response);

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("task_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Task_List item = new Item_Task_List();
                            item.setId(object.getString("id"));
                            item.setSubject(object.getString("Subject"));
                            item.setDescription(object.getString("Description"));
                            item.setTask_date(object.getString("Task_date"));
                            item.setTask_time(object.getString("Task_time"));
                            item.setStatus(object.getString("Status"));

                            itemList.add(item);
                            itemListTemp.add(item);
                        }
                        adapter_tasks_list.notifyDataSetChanged();

                    } else {
                        new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hidePrd();
                swipe_refresh.setRefreshing(false);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(stringRequest);
    }

    public void addTask(final String task_subject, final String task_desc) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TASKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //  Log.e("Response Tasks Add", response);

                    if (jsonObject.getInt("result") == 1) {

                        getCompleteTaskList();
                        new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("type", "Add");
                params.put("Subject", task_subject);
                params.put("Description", task_desc);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(stringRequest);
    }

    public void completeTask(final String task_id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TASKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Task complete", response);

                    if (jsonObject.getInt("result") == 1) {

                        getCompleteTaskList();
                        new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("id", task_id);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(stringRequest);
    }

    public void deleteTask(final String task_id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TASKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response task delete", response);

                    if (jsonObject.getInt("result") == 1) {

                        getCompleteTaskList();
                        new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("id", task_id);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(stringRequest);
    }

    public void showPrd() {
        prd = new ProgressDialog(activity);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }

    private void openDatePicker(final TextView tvSelectDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        tvSelectDate.setText(day + "-" + (month + 1) + "-" + year);
                        mAlertTime.set(Calendar.YEAR, year);
                        mAlertTime.set(Calendar.MONTH, month);
                        mAlertTime.set(Calendar.DAY_OF_MONTH, day);
                        /*mDate = DATE_FORMAT.format(mAlertTime.getTime());
                        mAlarmDate.put(ITEM_CONTENT, mDate);*/
                        mData.setTimeInMillis(mAlertTime.getTimeInMillis());
                    }
                }, mAlertTime.get(Calendar.YEAR), mAlertTime.get(Calendar.MONTH),
                mAlertTime.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void openTimePicker(final TextView tvSelectTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String min = String.valueOf(minute);
                if (min.length() <= 1) {
                    min = "0" + minute;
                }
                tvSelectTime.setText(hourOfDay + ":" + min);
                mAlertTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mAlertTime.set(Calendar.MINUTE, minute);
                mAlertTime.set(Calendar.SECOND, 0);
                /*mTime = TIME_FORMAT.format(mAlertTime.getTime());
                mAlarmTime.put(ITEM_CONTENT, mTime);*/
                mData.setTimeInMillis(mAlertTime.getTimeInMillis());
            }
        }, mAlertTime.get(Calendar.HOUR_OF_DAY), mAlertTime.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    private void saveAlert(final ReminderItem item) {
        if (item.getId() > 0) {
            Intent cancelPrevious = new Intent(activity, AlarmService.class);
            cancelPrevious.putExtra(ReminderParams.ID, item.getId());
            cancelPrevious.setAction(AlarmService.CANCEL);
            activity.startService(cancelPrevious);
            ContentValues values = new ContentValues();
            values.put(ReminderContract.Alerts.TASK_ID, item.getmTaskID());
            values.put(ReminderContract.Alerts.TITLE, item.getTitle());
            values.put(ReminderContract.Alerts.CONTENT, item.getContent());
            values.put(ReminderContract.Alerts.TIME, item.getTimeInMillis());
            values.put(ReminderContract.Alerts.FREQUENCY, item.getFrequency());
            Uri uri = ContentUris.withAppendedId(ReminderContract.Alerts.CONTENT_URI, item.getId());
            mContentResolver.update(uri, values, null, null);
            createAlarm(item.getId());
        } else {
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
        }
    }

    private void createAlarm(int id) {
        Intent alarm = new Intent(activity, AlarmService.class);
        alarm.putExtra(ReminderParams.ID, id);
        alarm.setAction(AlarmService.CREATE);
        activity.startService(alarm);
    }

    private void showFileChooser() {
        CharSequence[] items_name = {"Select Image", "Select File"};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Option");
        builder.setItems(items_name, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(activity, FilePickerActivity.class);
                    intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                            .setCheckPermission(true)
                            .setShowImages(true)
                            .setShowAudios(false)
                            .setShowFiles(false)
                            .setShowVideos(false)
                            .setSuffixes("jpg", "jpeg", "png")
                            .enableImageCapture(true)
                            .setMaxSelection(10)
                            .setSkipZeroSizeFiles(true)
                            .build());
                    startActivityForResult(intent, IMG_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(activity, FilePickerActivity.class);
                    intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                            .setCheckPermission(true)
                            .setShowImages(false)
                            .setShowAudios(false)
                            .setShowFiles(true)
                            .setShowVideos(false)
                            .setSuffixes("pdf")
                            .enableImageCapture(true)
                            .setMaxSelection(10)
                            .setSkipZeroSizeFiles(true)
                            .build());
                    startActivityForResult(intent, FILE_REQUEST_CODE);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}

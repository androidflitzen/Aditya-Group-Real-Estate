package com.flitzen.adityarealestate.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate.Aditya;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Classes.Helper;
import com.flitzen.adityarealestate.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_Complete_Task_Details extends AppCompatActivity {

    Activity mActivity;
    ProgressDialog prd;

    String type = "";

    TextView txt_task_subject, txt_task_desc, txt_task_date, txt_task_time;
    View view_remove_task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_task_details);

        getSupportActionBar().setTitle(Html.fromHtml("Complete Task Details"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Complete_Task_Details.this;
        txt_task_subject = (TextView) findViewById(R.id.txt_task_subject);
        txt_task_desc = (TextView) findViewById(R.id.txt_task_desc);
        txt_task_date = (TextView) findViewById(R.id.txt_task_date);
        txt_task_time = (TextView) findViewById(R.id.txt_task_time);

        view_remove_task = findViewById(R.id.view_remove_task);

        view_remove_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask();
            }
        });

        getTaskDetail();
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void deleteTask() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TASKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response task delete", response);

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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
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
      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}

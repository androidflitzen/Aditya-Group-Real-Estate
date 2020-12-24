package com.flitzen.adityarealestate_new.Activity;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link androidx.fragment.app.Fragment} subclass.
 */
public class Activity_Pending_EMI extends AppCompatActivity {

    private LinearLayout viewEmiUser;
    private TextView txtTitle, txtSubtitle, txtBottom, txtTotal;
    private Calendar c;
    private String data;
    private String EMI_date_format;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pending_emi_);

        getSupportActionBar().setTitle("Pending EMI");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewEmiUser = (LinearLayout) findViewById(R.id.view_pending_emi_user_list);
        txtTitle = (TextView) findViewById(R.id.txt_pending_emi_title);
        txtSubtitle = (TextView) findViewById(R.id.txt_pending_emi_sub_title);
        txtBottom = (TextView) findViewById(R.id.txt_pending_emi_bottom);
        txtTotal = (TextView) findViewById(R.id.txt_pending_emi_total);

        c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy");
        SimpleDateFormat df1 = new SimpleDateFormat("dd MM yyyy");
        String formattedDate = df.format(c.getTime());

        txtTitle.setText("EMI Recovery list - " + df.format(c.getTime()));
        txtSubtitle.setText("As on " + df1.format(c.getTime()));
        txtBottom.setText("Total Pending EMI Recovery for " + df.format(c.getTime()));

        setData();

    }

    public void setData() {
        try {
            jsonArray = new JSONArray(data);
            int totalAmount = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView1 = inflater.inflate(R.layout.view_pending_emi_user, null);

                TextView txtDate = (TextView) customView1.findViewById(R.id.txt_pending_emi_date);
                TextView txtAmount = (TextView) customView1.findViewById(R.id.txt_pending_emi_amount);
                TextView txtName = (TextView) customView1.findViewById(R.id.txt_pending_emi_c_name);
                TextView txtEmi_Date = (TextView) customView1.findViewById(R.id.txt_pending_emi_last_emi_date);

                totalAmount += jObj.getInt("Pending_Amount");
                txtDate.setText(jObj.getString("Pay_EMI_Date"));
                txtAmount.setText(getResources().getString(R.string.rupee) + jObj.getString("Pending_Amount"));
                txtName.setText(jObj.getString("Customer"));
                txtEmi_Date.setText(jObj.getString("Last_Date"));

                int date = (c.getTime().getDate());
                int last_date = Integer.parseInt(txtDate.getText().toString());

                if (jObj.getString("Status").equals("Unpaid")) {// && date >= last_date
                    txtDate.setTextColor(getResources().getColor(R.color.reject_bg));
                    txtName.setTextColor(getResources().getColor(R.color.reject_bg));
                    txtAmount.setTextColor(getResources().getColor(R.color.reject_bg));
                    txtEmi_Date.setTextColor(getResources().getColor(R.color.reject_bg));
                }

                customView1.setTag(String.valueOf(i));
                customView1.setOnClickListener(onClickListener);
                viewEmiUser.addView(customView1);
            }

            txtTotal.setText(getResources().getString(R.string.rupee) + new DecimalFormat(Helper.DECIMAL_FORMATE).format(totalAmount));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = Integer.parseInt(v.getTag().toString());
            try {
                JSONObject jObj = jsonArray.getJSONObject(position);
                String loanId = jObj.getString("Loan_Id");
                String customerId = jObj.getString("Customer_Id");
                String Amount = jObj.getString("Pending_Amount");

                AddEmiDialog(customerId, loanId, Amount);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void AddEmiDialog(final String customerId, final String applicationId, String Amount) {

        LayoutInflater localView = LayoutInflater.from(getApplicationContext());
        View promptsView = localView.inflate(R.layout.dialog_admin_pay_emi, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText edt_amount = (EditText) promptsView.findViewById(R.id.edt_emi_amount);
        final EditText edt_date = (EditText) promptsView.findViewById(R.id.edt_emi_date);
        final EditText edt_remark = (EditText) promptsView.findViewById(R.id.edt_emi_remark);
        final Button btn_pay = (Button) promptsView.findViewById(R.id.btn_pay_emi);
        final Button btn_cancel = (Button) promptsView.findViewById(R.id.btn_pay_emi_cancel);
        final RadioButton Rbtn_Interest = (RadioButton) promptsView.findViewById(R.id.rbtn_installmenttype_interest);
        final RadioButton Rbtn_Deposit = (RadioButton) promptsView.findViewById(R.id.rbtn_installmenttype_deposit);
        final LinearLayout LayoutType = (LinearLayout) promptsView.findViewById(R.id.layout_installmenttype);

        LayoutType.setVisibility(View.GONE);

        Rbtn_Interest.setChecked(true);

        edt_amount.setText(Amount);

        edt_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Pick_date(edt_date, 0);
                }
            }
        });

        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pick_date(edt_date, 0);
            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(edt_date.getText().toString())) {
                    edt_date.setError("Select Date");
                    edt_date.requestFocus();
                } else if (TextUtils.isEmpty(edt_amount.getText().toString().trim())) {
                    edt_amount.setError("Enter Amount");
                    edt_amount.requestFocus();
                } else if (Integer.parseInt(edt_amount.getText().toString().trim()) == 0) {
                    edt_amount.setError("Amount must be greater than 0(Zero)");
                    edt_amount.requestFocus();
                } else {

                    String InstallMent_Type;
                    if (Rbtn_Deposit.isChecked()) {
                        InstallMent_Type = Rbtn_Deposit.getText().toString().trim();
                    } else {
                        InstallMent_Type = Rbtn_Interest.getText().toString().trim();
                    }
                    //json
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    public void Pick_date(final EditText editText, final int action) {

        LayoutInflater localView = LayoutInflater.from(getApplicationContext());
        View promptsView = localView.inflate(R.layout.pick_dilog, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getApplicationContext());
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final DatePicker datePicker = (DatePicker) promptsView.findViewById(R.id.datePicker);

        final Button btn_cancel = (Button) promptsView.findViewById(R.id.btn_cancel);
        final Button btn_ok = (Button) promptsView.findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int dmonth = datePicker.getMonth();
                dmonth++;
                int dday = datePicker.getDayOfMonth();
                int dyear = datePicker.getYear();

                alertDialog.dismiss();
                EMI_date_format = dyear + "-" + dmonth + "-" + dday;
                editText.setText("" + dday + " / " + dmonth + " / " + dyear);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

            }
        });

        alertDialog.show();

    }

    public void getData() {
        //swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.GET_TOTAL_LOAN_COUNT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //hidePrd();
                //swipe_refresh.setRefreshing(false);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                   // Log.e("Response Sites List", response);

                    if (jsonObject.getString("result").equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("EMI_Users");

                        int totalAmount = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jObj = jsonArray.getJSONObject(i);
                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View customView1 = inflater.inflate(R.layout.view_pending_emi_user, null);

                            TextView txtDate = (TextView) customView1.findViewById(R.id.txt_pending_emi_date);
                            TextView txtAmount = (TextView) customView1.findViewById(R.id.txt_pending_emi_amount);
                            TextView txtName = (TextView) customView1.findViewById(R.id.txt_pending_emi_c_name);
                            TextView txtEmi_Date = (TextView) customView1.findViewById(R.id.txt_pending_emi_last_emi_date);

                            totalAmount += jObj.getInt("Pending_Amount");
                            txtDate.setText(jObj.getString("Pay_EMI_Date"));
                            txtAmount.setText(getResources().getString(R.string.rupee) + jObj.getString("Pending_Amount"));
                            txtName.setText(jObj.getString("Customer"));
                            txtEmi_Date.setText(jObj.getString("Last_Date"));

                            int date = (c.getTime().getDate());
                            int last_date = Integer.parseInt(txtDate.getText().toString());

                            if (jObj.getString("Status").equals("Unpaid")) {// && date >= last_date
                                txtDate.setTextColor(getResources().getColor(R.color.reject_bg));
                                txtName.setTextColor(getResources().getColor(R.color.reject_bg));
                                txtAmount.setTextColor(getResources().getColor(R.color.reject_bg));
                                txtEmi_Date.setTextColor(getResources().getColor(R.color.reject_bg));
                            }

                            customView1.setTag(String.valueOf(i));
                            customView1.setOnClickListener(onClickListener);
                            viewEmiUser.addView(customView1);
                        }

                        txtTotal.setText(getResources().getString(R.string.rupee) + new DecimalFormat(Helper.DECIMAL_FORMATE).format(totalAmount));

                    } else {
                        new CToast(getApplicationContext()).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                //swipe_refresh.setRefreshing(false);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
     //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}

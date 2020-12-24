package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
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
import com.flitzen.adityarealestate_new.Adapter.Spn_Adapter;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Iteams_All_Loan_Application;
import com.flitzen.adityarealestate_new.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_Add_Payment_For_Loan extends AppCompatActivity {

    Activity mActivity;
    ProgressDialog prd;

    TextView spn_loan, txt_aplican_name, txt_loan_amount;
    EditText edt_emi_date, edt_emi_amount, edt_emi_remark;
    RadioButton Rbtn_Interest, Rbtn_Deposit;
    View btn_add_payment;
    LinearLayout LayoutType;

    String loan_type = "";

    private ArrayList<Iteams_All_Loan_Application> itemArray_new = new ArrayList<>();

    String EMI_date_format = null, APPROVE_LOAN_DATE = null;

    String customerId, applicationId, LOAN_TYPE;
    private String InstallMent_Type = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payament_for_loan);

        getSupportActionBar().setTitle(Html.fromHtml("Add Payment For Loan"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Add_Payment_For_Loan.this;

        spn_loan = (TextView) findViewById(R.id.spn_loan);
        txt_aplican_name = (TextView) findViewById(R.id.txt_aplican_name);
        txt_loan_amount = (TextView) findViewById(R.id.txt_loan_amount);

        edt_emi_date = (EditText) findViewById(R.id.edt_emi_date);
        edt_emi_amount = (EditText) findViewById(R.id.edt_emi_amount);
        edt_emi_remark = (EditText) findViewById(R.id.edt_emi_remark);

        Rbtn_Interest = (RadioButton) findViewById(R.id.rbtn_installmenttype_interest);
        Rbtn_Deposit = (RadioButton) findViewById(R.id.rbtn_installmenttype_deposit);

        LayoutType = (LinearLayout) findViewById(R.id.layout_installmenttype);

        btn_add_payment = findViewById(R.id.btn_add_payment);

        if (loan_type.equals("Regular")) {
            LayoutType.setVisibility(View.VISIBLE);
        } else {
            LayoutType.setVisibility(View.GONE);
        }


        spn_loan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemArray_new.size() == 0) {
                    getLoanList();
                } else {
                    loanDialog();
                }
            }
        });

        edt_emi_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Pick_date(edt_emi_date, 0);
                }
            }
        });

        edt_emi_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pick_date(edt_emi_date, 0);
            }
        });

        Rbtn_Interest.setChecked(true);
        InstallMent_Type = Rbtn_Interest.getText().toString();

        Rbtn_Deposit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                InstallMent_Type = Rbtn_Interest.getText().toString();
            }
        });

        Rbtn_Interest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                InstallMent_Type = Rbtn_Deposit.getText().toString();
            }
        });

        btn_add_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spn_loan.getText().toString().equals("Select Loan")) {
                    Toast.makeText(mActivity, "Select Loan", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(edt_emi_date.getText().toString())) {
                    edt_emi_date.setError("Select Date");
                    edt_emi_date.requestFocus();
                } else if (TextUtils.isEmpty(edt_emi_amount.getText().toString().trim())) {
                    edt_emi_amount.setError("Enter Amount");
                    edt_emi_amount.requestFocus();
                } else if (Integer.parseInt(edt_emi_amount.getText().toString().trim()) == 0) {
                    edt_emi_amount.setError("Amount must be greater than 0(Zero)");
                    edt_emi_amount.requestFocus();
                } else {
                    addPaymentforProperty();
                }
            }
        });
    }

    public void addPaymentforProperty() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.EMI_PAYMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                   // Log.e("Response Customer Add", response);

                    if (jsonObject.getString("result").equals("success")) {
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

                params.put("row_id", customerId);
                params.put("loan_id", applicationId);
                params.put("type", "Insert");
                params.put("EMI_Amount", edt_emi_amount.getText().toString());
                params.put("EMI_Date", EMI_date_format);
                params.put("Loan_Remarks", edt_emi_remark.getText().toString().trim());
                params.put("Loan_Type", LOAN_TYPE);
                params.put("Type", InstallMent_Type);

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

    public void getLoanList() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.GET_ALL_APPLICANTS + "?type=Loans", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    itemArray_new.clear();

                    JSONObject jsonObject = new JSONObject(response);
                   // Log.e("Response Sites List", response);

                    if (jsonObject.getString("result").equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Loans");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Iteams_All_Loan_Application item = new Iteams_All_Loan_Application();
                            item.setAll_loan_loan_id(object.getString("id"));
                            item.setAll_loan_applicant_name(object.getString("Applicant_Name"));
                            item.setAll_loan_applicant_id(object.getString("Customer_Id"));
                            item.setAll_loan_applicant_number(object.getString("Applicantion_Number"));
                            item.setAll_loan_applicant_status(object.getString("Loan_Status"));
                            item.setAll_loan_applicant_approve_date(object.getString("Approved_Date"));
                            item.setAll_loan_applicant_amount(object.getString("Loan_Amount"));
                            item.setAll_loan_applicant_tenure(object.getString("Loan_Tenure"));
                            item.setAll_loan_applicant_loan_type(object.getString("Loan_Type"));
                            item.setAll_loan_applicant_approve_amount(object.getString("Approved_Amount"));

                            item.setAll_loan_applicant_applied_date(object.getString("Date_Applied"));

                            itemArray_new.add(item);
                        }


                        loanDialog();


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
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    public void loanDialog() {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_spinner, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText edtSearchLocation = (EditText) promptsView.findViewById(R.id.edt_spn_search);
        final ListView list_location = (ListView) promptsView.findViewById(R.id.list_spn);

        edtSearchLocation.setHint("Search Loan");
        final ArrayList<String> arrayListTemp = new ArrayList<>();
        final ArrayList<String> arrayListId = new ArrayList<>();
        final ArrayList<String> arrayListLoanType = new ArrayList<>();
        final ArrayList<String> arrayListCustName = new ArrayList<>();
        final ArrayList<String> arrayListCustId = new ArrayList<>();
        final ArrayList<String> arrayListLoanAmount = new ArrayList<>();

        for (int i = 0; i < itemArray_new.size(); i++) {
            arrayListTemp.add(itemArray_new.get(i).getAll_loan_applicant_number());
            arrayListId.add(itemArray_new.get(i).getAll_loan_loan_id());
            arrayListLoanType.add(itemArray_new.get(i).getAll_loan_applicant_loan_type());
            arrayListCustName.add(itemArray_new.get(i).getAll_loan_applicant_name());
            arrayListCustId.add(itemArray_new.get(i).getAll_loan_applicant_id());
            arrayListLoanAmount.add(itemArray_new.get(i).getAll_loan_applicant_amount());
        }

        list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));

        edtSearchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int position, int i1, int i2) {

                if (edtSearchLocation.getText().toString().trim().length() > 0) {
                    arrayListTemp.clear();
                    arrayListId.clear();

                    for (int j = 0; j < itemArray_new.size(); j++) {
                        String word = edtSearchLocation.getText().toString().toLowerCase();
                        if (itemArray_new.get(j).getAll_loan_applicant_number().toLowerCase().contains(word)) {
                            arrayListTemp.add(itemArray_new.get(j).getAll_loan_applicant_number());
                            arrayListId.add(itemArray_new.get(j).getAll_loan_loan_id());
                            arrayListLoanType.add(itemArray_new.get(j).getAll_loan_applicant_loan_type());
                            arrayListCustName.add(itemArray_new.get(j).getAll_loan_applicant_name());
                            arrayListCustId.add(itemArray_new.get(j).getAll_loan_applicant_id());
                            arrayListLoanAmount.add(itemArray_new.get(j).getAll_loan_applicant_amount());
                        }
                    }
                    list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));
                } else {
                    arrayListTemp.clear();
                    arrayListId.clear();

                    for (int i = 0; i < itemArray_new.size(); i++) {
                        arrayListTemp.add(itemArray_new.get(i).getAll_loan_applicant_number());
                        arrayListId.add(itemArray_new.get(i).getAll_loan_loan_id());
                        arrayListLoanType.add(itemArray_new.get(i).getAll_loan_applicant_loan_type());
                        arrayListCustName.add(itemArray_new.get(i).getAll_loan_applicant_name());
                        arrayListCustId.add(itemArray_new.get(i).getAll_loan_applicant_id());
                        arrayListLoanAmount.add(itemArray_new.get(i).getAll_loan_applicant_amount());
                    }
                    list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        list_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                spn_loan.setText(arrayListTemp.get(position));
                spn_loan.setTag(arrayListId.get(position));
                loan_type = arrayListLoanType.get(position);
                txt_aplican_name.setText(arrayListCustName.get(position));
                txt_aplican_name.setTag(arrayListCustId.get(position));
                txt_loan_amount.setText(arrayListLoanAmount.get(position));
                customerId = arrayListCustId.get(position);
                applicationId = arrayListId.get(position);

                if (loan_type.trim().equals("EMI")) {
                    LOAN_TYPE = Helper.LOAN_TYPE_EMI;
                } else {
                    LOAN_TYPE = Helper.LOAN_TYPE_SIMPLE;
                }

                if (loan_type.equals("Regular")) {
                    LayoutType.setVisibility(View.VISIBLE);
                } else {
                    LayoutType.setVisibility(View.GONE);
                }

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

        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.pick_dilog, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
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
                if (action == 0) {
                    EMI_date_format = dyear + "-" + dmonth + "-" + dday;
                    editText.setText("" + dday + " / " + dmonth + " / " + dyear);
                } else {
                    APPROVE_LOAN_DATE = dyear + "-" + dmonth + "-" + dday;
                    editText.setText("" + dday + " / " + dmonth + " / " + dyear);
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
        overridePendingTransition(0, 0);
        finish();
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}

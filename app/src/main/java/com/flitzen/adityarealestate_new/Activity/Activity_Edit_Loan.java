package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
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
import com.flitzen.adityarealestate_new.Items.LoanDetailsForPDF;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_Edit_Loan extends AppCompatActivity {

    Activity mActivity;

    private TextView txtSelectUser, txtTotalInterest, txtAmount, txtTotalAmount, txtEmi, txtSelectEmidate;
    private RadioButton rdb_emi, rdb_simple, rdb_daily;
    private EditText edtAmount, edtInterest, edtMonth, edtReason, edtApproveAmount;
    private Button btnCalculate, btnAdd, btnLoanReport;
    private View viewCalculation, viewTypeEmiContent, viewTenure, viewDailyLoanContent;

    String UsersData, SELECTED_U_NAME, SELECTED_U_ID;
    DecimalFormat formatter;
    SharedPreferences sharedPreferences;

    ///FOR emi CALCULATOR
    double lAMOUNT, lTENURE, lRATE, TOTAL_PAYABLE, EMI, INTEREST_PAYABLE;
    String SelectedEmiDate = null;
    ProgressDialog prd;

    LoanDetailsForPDF loanDetailsForPDF = new LoanDetailsForPDF();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__new__loan_);

        mActivity = Activity_Edit_Loan.this;

        formatter = new DecimalFormat(Helper.DECIMAL_FORMATE);
        UsersData = getIntent().getStringExtra("USERS");

        getSupportActionBar().setTitle("Edit Loan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            loanDetailsForPDF = (LoanDetailsForPDF) getIntent().getSerializableExtra("loanDetails");
        }

        initV();
        getAllUsers();

        setData();

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean loanType;
                if (rdb_emi.isChecked()) {
                    loanType = true;
                } else {
                    loanType = false;
                }

                if (TextUtils.isEmpty(edtAmount.getText().toString())) {
                    edtAmount.setError((Html.fromHtml("<font color='#ffffff'>Enter Amount</font>")));
                    edtAmount.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(edtInterest.getText().toString())) {
                    edtInterest.setError((Html.fromHtml("<font color='#ffffff'>Enter Interest</font>")));
                    edtInterest.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(edtMonth.getText().toString())) {
                    if (loanType) {
                        edtMonth.setError((Html.fromHtml("<font color='#ffffff'>Enter Number Months</font>")));
                        edtMonth.requestFocus();
                        return;
                    }
                }

                /*InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(imm!=null){
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }*/

                CalculateLoan(loanType);
            }
        });

        edtInterest.addTextChangedListener(new OnTextChange());
        edtAmount.addTextChangedListener(new OnTextChange());
        edtMonth.addTextChangedListener(new OnTextChange());
        rdb_emi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewCalculation.setVisibility(View.GONE);
            }
        });
        rdb_simple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewCalculation.setVisibility(View.GONE);
                if (isChecked) {
                    viewTenure.setVisibility(View.GONE);
                } else {
                    viewTenure.setVisibility(View.VISIBLE);
                }
            }
        });
        rdb_daily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewCalculation.setVisibility(View.GONE);
                if (isChecked) {
                    viewDailyLoanContent.setVisibility(View.VISIBLE);
                } else {
                    viewDailyLoanContent.setVisibility(View.GONE);
                }
            }
        });

        btnLoanReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //report();
                Intent i = new Intent(Activity_Edit_Loan.this, Loan_Report_Activity.class);
                i.putExtra("AMOUNT", lAMOUNT);
                i.putExtra("TOTAL", TOTAL_PAYABLE);
                i.putExtra("MONTHS", lTENURE);
                i.putExtra("EMI", EMI);
                i.putExtra("RATE", lRATE);
                startActivity(i);
            }
        });

        txtSelectUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUsersList();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SelectedEmiDate == null) {
                    Toast.makeText(Activity_Edit_Loan.this, "Select EMI date", Toast.LENGTH_SHORT).show();
                    Helper.Vibrate(Activity_Edit_Loan.this, 50);
                    Animation shake = AnimationUtils.loadAnimation(Activity_Edit_Loan.this, R.anim.shake);
                    txtSelectEmidate.startAnimation(shake);
                    return;
                }

                if (SELECTED_U_NAME == null) {
                    Toast.makeText(Activity_Edit_Loan.this, "Select User", Toast.LENGTH_SHORT).show();
                    return;
                }
                String loanType;
                if (rdb_emi.isChecked()) {
                    loanType = Helper.LOAN_TYPE_EMI;
                } else if (rdb_simple.isChecked()) {
                    loanType = Helper.LOAN_TYPE_SIMPLE;
                } else {
                    loanType = Helper.LOAN_TYPE_DAILY;
                }

                AddNewLoan(loanType);
            }
        });

        txtSelectEmidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectEmiDateDialog();
            }
        });


    }

    private void setData() {
        edtInterest.setText(loanDetailsForPDF.getApprovedAmount());
        edtAmount.setText(loanDetailsForPDF.getLoanAmount());
        edtMonth.setText(loanDetailsForPDF.getLoanTenure());
        edtReason.setText(loanDetailsForPDF.getReasonForLoan());
        edtInterest.setText(loanDetailsForPDF.getInterestRate());
        txtSelectEmidate.setText(loanDetailsForPDF.getPayEMIDate());
        SelectedEmiDate = loanDetailsForPDF.getPayEMIDate();

        if (loanDetailsForPDF.getLoanType().equals("1")) {
            rdb_simple.setChecked(true);
        } else if (loanDetailsForPDF.getLoanType().equals("2")) {
            rdb_emi.setChecked(true);
        } else if (loanDetailsForPDF.getLoanType().equals("3")) {
            rdb_daily.setChecked(true);
        }

        SELECTED_U_ID = loanDetailsForPDF.getCustomerID();
        SELECTED_U_NAME = loanDetailsForPDF.getCustomerName();
        txtSelectUser.setText(SELECTED_U_NAME);

    }

    public void SelectEmiDateDialog() {

        LayoutInflater localView = LayoutInflater.from(Activity_Edit_Loan.this);
        View promptsView = localView.inflate(R.layout.dialog_admin_select_emi_date, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(Activity_Edit_Loan.this);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        Button btnOk = (Button) promptsView.findViewById(R.id.btn_select_emi_date_ok);
        Button btnCancel = (Button) promptsView.findViewById(R.id.btn_select_emi_date_cancel);
        final NumberPicker numberPicker = (NumberPicker) promptsView.findViewById(R.id.number_picker_emi_date);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(31);
        numberPicker.setValue(1);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String value = String.valueOf(numberPicker.getValue());
                txtSelectEmidate.setText(Html.fromHtml("Emi pay date is <b>" + value + "</b> on every months"));
                SelectedEmiDate = value;
                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    public void DialogUsersList() {

        LayoutInflater localView = LayoutInflater.from(Activity_Edit_Loan.this);
        View promptsView = localView.inflate(R.layout.dialog_users_list, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(Activity_Edit_Loan.this);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final ArrayList<String> userNamelist = new ArrayList<>();
        final ArrayList<String> userNamelistTemp = new ArrayList<>();
        final ArrayList<String> userNameid = new ArrayList<>();
        final ArrayList<String> userNameidTemp = new ArrayList<>();
        final ArrayList<String> userContact = new ArrayList<>();
        final ListView lstView = (ListView) promptsView.findViewById(R.id.listview_users);
        final EditText edtSearch = (EditText) promptsView.findViewById(R.id.edt_users_dialog_search);

        if (UsersData != null) {
            try {
                JSONObject jsonObject = new JSONObject(UsersData);
                JSONArray jsonArray = jsonObject.getJSONArray("Applicants");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jobj = jsonArray.getJSONObject(i);
                    userNamelist.add(jobj.getString("Applicant_Name"));
                    userNamelistTemp.add(jobj.getString("Applicant_Name"));
                    userNameid.add(jobj.getString("id"));
                    userNameidTemp.add(jobj.getString("id"));
                    userContact.add(jobj.getString("Primary_Number"));
                }

                ArrayAdapter arrayAdapter = new ArrayAdapter(Activity_Edit_Loan.this, R.layout.layout_user_list_row, userNamelistTemp);
                lstView.setAdapter(arrayAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SELECTED_U_ID = userNameidTemp.get(position).trim();
                SELECTED_U_NAME = userNamelistTemp.get(position).trim();
                txtSelectUser.setText(SELECTED_U_NAME);
                alertDialog.dismiss();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String word = edtSearch.getText().toString().trim().toLowerCase();
                userNameidTemp.clear();
                userNamelistTemp.clear();

                if (word.length() > 0) {
                    for (int i = 0; i < userNamelist.size(); i++) {
                        if (userNamelist.get(i).trim().toLowerCase().contains(word)) {
                            userNamelistTemp.add(userNamelist.get(i));
                            userNameidTemp.add(userNameid.get(i));
                        }
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(Activity_Edit_Loan.this, R.layout.layout_user_list_row, userNamelistTemp);
                    lstView.setAdapter(arrayAdapter);
                } else {
                    userNamelistTemp.addAll(userNamelist);
                    userNameidTemp.addAll(userNameid);
                    ArrayAdapter arrayAdapter = new ArrayAdapter(Activity_Edit_Loan.this, R.layout.layout_user_list_row, userNamelistTemp);
                    lstView.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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

    class OnTextChange implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            viewCalculation.setVisibility(View.GONE);
        }
    }

    public void AddNewLoan(final String loanType) {

        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("LoanDetails").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(loanDetailsForPDF.getLoanId())) {

                                DatabaseReference cineIndustryRef = databaseReference.child("LoanDetails").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("Customer_Id", SELECTED_U_ID);
                                map.put("Loan_Amount", edtAmount.getText().toString().trim());
                                map.put("Loan_Tenure", edtMonth.getText().toString().trim());
                                map.put("Loan_Type", loanType);
                                map.put("Reason_For_Loan", edtReason.getText().toString().trim());
                                map.put("Interest_Rate", edtInterest.getText().toString().trim());

                                if (loanType.equals(Helper.LOAN_TYPE_DAILY)) {
                                    map.put("Approved_Amount", edtInterest.getText().toString().trim());
                                    map.put("Payable_Amount", "0");
                                    map.put("Monthly_EMI", "0");
                                    map.put("Pay_EMI_Date", SelectedEmiDate);
                                } else {
                                    map.put("Approved_Amount", edtAmount.getText().toString().trim());
                                    map.put("Payable_Amount", String.valueOf(Helper.getRoundValue(TOTAL_PAYABLE)));
                                    map.put("Monthly_EMI", String.valueOf(Helper.getRoundValue(EMI)));
                                    map.put("Pay_EMI_Date", SelectedEmiDate);
                                }
                                map.put("Loan_Status", loanDetailsForPDF.getLoanStatus());
                                map.put("Original_Amount", edtAmount.getText().toString().trim());

                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        new CToast(mActivity).simpleToast("Loan updated successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                                        overridePendingTransition(0, 0);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hidePrd();
                                        new CToast(mActivity).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("exception   ", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
                Log.e("databaseError   ", databaseError.getMessage());
            }
        });
    }

    public void AddNewLoan1(final String loanType) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.ADD_LOAN_AND_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Customer Add", response);

                    if (jsonObject.getString("result").equals("success")) {

                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        Activity_Admin_All_LoanApplication.REFRESH = true;
                        // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                        overridePendingTransition(0, 0);
                        finish();

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
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("type", "InsertL");
                params.put("customer_id", SELECTED_U_ID);
                params.put("loan_amount", edtAmount.getText().toString().trim());
                params.put("loan_tenure", edtMonth.getText().toString().trim());
                params.put("loan_type", loanType);
                params.put("loan_reason", edtReason.getText().toString().trim());
                params.put("Interest_Rate", edtInterest.getText().toString().trim());

                if (loanType.equals(Helper.LOAN_TYPE_DAILY)) {
                    params.put("Approved_Amount", edtInterest.getText().toString().trim());
                    params.put("Pay_EMI_Date", SelectedEmiDate);
                } else {
                    params.put("Approved_Amount", edtAmount.getText().toString().trim());
                    params.put("Payable_Amount", String.valueOf(Helper.getRoundValue(TOTAL_PAYABLE)));
                    params.put("Monthly_EMI", String.valueOf(Helper.getRoundValue(EMI)));
                    params.put("Pay_EMI_Date", SelectedEmiDate);
                }

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void getAllUsers() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Customers").orderByKey();
        // databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        JSONObject PersonObject = new JSONObject();
                        JSONArray personarray = new JSONArray();
                        for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                            if (npsnapshot1.child("Type").getValue().toString().equals("2")) {

                                JSONObject person1 = new JSONObject();
                                try {
                                    person1.put("id", npsnapshot1.child("id").getValue().toString());
                                    person1.put("Applicant_Name", npsnapshot1.child("name").getValue().toString());
                                    person1.put("Primary_Number", npsnapshot1.child("contact_no").getValue().toString());
                                    personarray.put(person1);

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }


                            }
                        }
                        PersonObject.put("Applicants", personarray);

                        String JsonData = PersonObject.toString();
                        System.out.println("======jsonString: " + JsonData);
                        UsersData = JsonData;

                    }
                } catch (Exception e) {
                    Log.e("Ex   ", e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("error ", error.getMessage());
            }
        });
    }

    public void getAllUsers1() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.GET_ALL_APPLICANTS + "?type=Applicants", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Customer List", response);

                    UsersData = response;

                    /*if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("customers");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Customer_List item = new Item_Customer_List();
                            item.setId(object.getString("id"));
                            item.setName(object.getString("name"));
                            item.setCity(object.getString("city"));
                            item.setContact_no(object.getString("contact_no"));
                            item.setAnother_no(object.getString("contact_no1"));
                            item.setEmail(object.getString("email"));
                            item.setAddress(object.getString("address"));

                            itemListCustomer.add(item);
                            itemListCustomerTemp.add(item);
                        }
                        adapter_cutomer_list.notifyDataSetChanged();

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        return;
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    public void initV() {

        txtSelectUser = (TextView) findViewById(R.id.txt_new_loan_select_user);
        txtSelectEmidate = (EditText) findViewById(R.id.txt_new_loan_select_emi_date);

        rdb_emi = (RadioButton) findViewById(R.id.rdb_new_loan_emi);
        rdb_simple = (RadioButton) findViewById(R.id.rdb_new_loan_simple);
        rdb_daily = (RadioButton) findViewById(R.id.rdb_new_loan_daily);

        edtAmount = (EditText) findViewById(R.id.edt_new_loan_amount);
        edtInterest = (EditText) findViewById(R.id.edt_new_loan_interest);
        edtMonth = (EditText) findViewById(R.id.edt_new_loan_months);
        edtReason = (EditText) findViewById(R.id.edt_new_loan_reason);
        edtApproveAmount = (EditText) findViewById(R.id.edt_new_loan_approve_amount);

        btnCalculate = (Button) findViewById(R.id.btn_new_loan_calculate);
        btnAdd = (Button) findViewById(R.id.btn_new_loan_add);
        btnLoanReport = (Button) findViewById(R.id.btn_new_loan_report);

        viewDailyLoanContent = findViewById(R.id.view_new_loan_daily_content);
        viewCalculation = findViewById(R.id.view_new_loan_calculation);
        viewTypeEmiContent = findViewById(R.id.view_new_loan_emi_content);
        viewTenure = findViewById(R.id.view_new_loan_tenure);
        txtTotalInterest = (TextView) findViewById(R.id.txt_new_loan_total_interest_pay);
        txtAmount = (TextView) findViewById(R.id.txt_new_loan_amount);
        txtTotalAmount = (TextView) findViewById(R.id.txt_new_loan_total_amount_pay);
        txtEmi = (TextView) findViewById(R.id.txt_new_loan_emi_pay);

        btnAdd.setText("Edit Loan");

    }

    public void CalculateLoan(boolean status) {

        lAMOUNT = Double.parseDouble(edtAmount.getText().toString().trim());
        //lTENURE = Double.parseDouble(edtMonth.getText().toString().trim());
        lTENURE = ((edtMonth.getText().toString().trim().length() == 0) ? 0.0 : Double.parseDouble(edtMonth.getText().toString().trim()));
        lRATE = Double.parseDouble(edtInterest.getText().toString().trim());

        if (status) {//EMI LOAN
            /*EMI = [P x R x (1+R)^N]/[(1+R)^N-1]*/
            viewTypeEmiContent.setVisibility(View.VISIBLE);
            btnLoanReport.setVisibility(View.VISIBLE);
            viewCalculation.setVisibility(View.VISIBLE);
            EMI = Helper.GetEMI(lAMOUNT, lRATE, lTENURE);
            TOTAL_PAYABLE = EMI * lTENURE;
            INTEREST_PAYABLE = TOTAL_PAYABLE - lAMOUNT;

            txtTotalInterest.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(INTEREST_PAYABLE)));
            txtAmount.setText(Helper.RUPEE + formatter.format(lAMOUNT));
            txtTotalAmount.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(TOTAL_PAYABLE)));
            txtEmi.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(EMI)));

        } else {// SIMPLE LOAN
            //Formula 1.Find interest = (AMOUNT*RATE) / (100) -------- 2.Find total payable = Find interest + AMOUNT
            viewTypeEmiContent.setVisibility(View.GONE);
            INTEREST_PAYABLE = (lAMOUNT * lRATE) / (100);
            EMI = INTEREST_PAYABLE;
            //TOTAL_PAYABLE = (INTEREST_PAYABLE * lTENURE) + lAMOUNT;
            TOTAL_PAYABLE = 0.0;

            viewCalculation.setVisibility(View.VISIBLE);
            //txtTotalInterest.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(INTEREST_PAYABLE * lTENURE)));
            //txtAmount.setText(Helper.RUPEE + formatter.format(lAMOUNT));
            //txtTotalAmount.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(TOTAL_PAYABLE)));
            txtEmi.setText(Helper.RUPEE + formatter.format(Helper.getRoundValue(INTEREST_PAYABLE)));
            btnLoanReport.setVisibility(View.GONE);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPrd() {
        prd = new ProgressDialog(Activity_Edit_Loan.this);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }
}

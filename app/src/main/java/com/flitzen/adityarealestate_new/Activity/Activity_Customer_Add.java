package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.SharePref;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_Customer_Add extends AppCompatActivity {

    ProgressDialog prd;
    Activity mActivity;

    String type = "", id = "", name = "", mobile = "", mobile1 = "", emial = "", address = "", city = "";

    TextView txt_add_customer;
    EditText edt_cust_name, edt_cust_no, edt_cust_other_no, edt_cust_email, edt_cust_address, edt_cust_city;
    View btn_add_customer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_add);

        type = getIntent().getStringExtra("TYPE");

        if (type.equals("ADD")) {
            getSupportActionBar().setTitle(Html.fromHtml("Add Customer"));
        } else if (type.equals("EDIT")) {
            getSupportActionBar().setTitle(Html.fromHtml("Edit Customer"));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Customer_Add.this;

        txt_add_customer = (TextView) findViewById(R.id.txt_add_customer);
        edt_cust_name = (EditText) findViewById(R.id.edt_cust_name);
        edt_cust_no = (EditText) findViewById(R.id.edt_cust_no);
        edt_cust_other_no = (EditText) findViewById(R.id.edt_cust_other_no);
        edt_cust_email = (EditText) findViewById(R.id.edt_cust_email);
        edt_cust_address = (EditText) findViewById(R.id.edt_cust_address);
        edt_cust_city = (EditText) findViewById(R.id.edt_cust_city);

        btn_add_customer = findViewById(R.id.btn_add_customer);

        if (type.equals("EDIT")) {
            txt_add_customer.setText("Update Customer");

            id = getIntent().getStringExtra("ID");
            name = getIntent().getStringExtra("NAME");
            mobile = getIntent().getStringExtra("MOBILE");
            mobile1 = getIntent().getStringExtra("MOBILE1");
            emial = getIntent().getStringExtra("EMAIL");
            address = getIntent().getStringExtra("ADDRESS");
            city = getIntent().getStringExtra("CITY");

            edt_cust_name.setText(getIntent().getStringExtra("NAME"));
            edt_cust_no.setText(getIntent().getStringExtra("MOBILE"));
            edt_cust_other_no.setText(getIntent().getStringExtra("MOBILE1"));
            edt_cust_email.setText(getIntent().getStringExtra("EMAIL"));
            edt_cust_address.setText(getIntent().getStringExtra("ADDRESS"));
            edt_cust_city.setText(getIntent().getStringExtra("CITY"));

        } else {
            txt_add_customer.setText("Add Customer");
        }

        btn_add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_cust_name.getText().toString().equals("")) {
                    edt_cust_name.setError("Enter Name");
                    edt_cust_name.requestFocus();
                    return;
                } else if (edt_cust_no.getText().toString().equals("")) {
                    edt_cust_no.setError("Enter Mobile No.");
                    edt_cust_no.requestFocus();
                    return;
                } else if (edt_cust_no.getText().toString().length() != 10) {
                    edt_cust_no.setError("Mobile No. must be 10 digit");
                    edt_cust_no.requestFocus();
                    return;
                } else {
                    addCustomer();
                }
            }
        });
    }

    public void addCustomer() {

        if (type.equals("EDIT")) {

            showPrd();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = databaseReference.child("Customers").orderByKey();
            databaseReference.keepSynced(true);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot npsnapshot) {
                    hidePrd();
                    try {
                        if (npsnapshot.exists()) {
                            for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                                if (dataSnapshot.child("id").getValue().toString().equals(id)) {

                                    DatabaseReference cineIndustryRef = databaseReference.child("Customers").child(dataSnapshot.getKey());
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("name", edt_cust_name.getText().toString());
                                    map.put("city", edt_cust_city.getText().toString());
                                    map.put("contact_no", edt_cust_no.getText().toString());
                                    map.put("contact_no1", edt_cust_other_no.getText().toString());
                                    map.put("email", edt_cust_email.getText().toString());
                                    map.put("address", edt_cust_address.getText().toString());
                                    Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            hidePrd();
                                            new CToast(mActivity).simpleToast("Customer updated successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
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
                        Log.e("exception   ",e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hidePrd();
                    Log.e("databaseError   ",databaseError.getMessage());
                }
            });
        }

        else {
            showPrd();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            String key = rootRef.child("Customers").push().getKey();
            Map<String, Object> map = new HashMap<>();
            map.put("name", edt_cust_name.getText().toString());
            map.put("city", edt_cust_city.getText().toString());
            map.put("contact_no", edt_cust_no.getText().toString());
            map.put("contact_no1", edt_cust_other_no.getText().toString());
            map.put("email", edt_cust_email.getText().toString());
            map.put("address", edt_cust_address.getText().toString());
            map.put("kyc_aadhar","");
            map.put("kyc_aadhar_back","");
            map.put("kyc_pancard","");
            map.put("kyc_driving_lic","");
            map.put("kyc_ration","");
            map.put("kyc_other","");
            map.put("Type",2);
            map.put("Device_ID","");
            map.put("FCM_ID","");
            map.put("Date","0000-00-00");
            map.put("Applicant_Name","");
            map.put("Primary_Number","");
            map.put("Secondary_Numbers","");
            map.put("Password","");
            map.put("Birth_Date","");
            map.put("Age","");
            map.put("Bussiness_Tax_Return","");
            map.put("Vehicle_Detail","");
            map.put("Family_Details","");
            map.put("Residence_Rent","");
            map.put("Residence_Rent_Amount","");
            map.put("Residence_Rent_Year","");
            map.put("Residence_Type","");
            map.put("Residence_Purchase_Year","");
            map.put("Occupation_Type","");
            map.put("Business_Name","");
            map.put("Business_Address","");
            map.put("Business_On_Rent","");
            map.put("Business_Turnover","");
            map.put("Business_Staff","");
            map.put("Business_Latitude","");
            map.put("Business_Longitude","");
            map.put("Company_Name","");
            map.put("Company_Address","");
            map.put("Monthly_Income","");
            map.put("Current_Loan","");
            map.put("Current_Loan_Details","");
            map.put("Longitude","");
            map.put("Latitude","");
            map.put("Residence_Photo","");
            map.put("Residence_Photo_2","");
            map.put("Residence_Photo_3","");
            map.put("Business_Photo","");
            map.put("Business_Photo_2","");
            map.put("Business_Photo_3","");
            map.put("id", key);
            map.put("status", 0);

            rootRef.child("Customers").child(key).setValue(map).addOnCompleteListener(Activity_Customer_Add.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hidePrd();
                    new CToast(mActivity).simpleToast("Customer added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                  //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                    overridePendingTransition(0, 0);
                    finish();
                }

            }).addOnFailureListener(Activity_Customer_Add.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hidePrd();
                    Toast.makeText(Activity_Customer_Add.this, "Please try later...", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    public void addCustomer1() {

        String URL = "";

        if (type.equals("EDIT")) {
            URL = API.EDIT_CUSTOMER;
        } else {
            URL = API.CUSTOMER;
        }

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Customer Add", response);

                    if (jsonObject.getInt("result") == 1) {

                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
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
                hidePrd();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                if (type.equals("EDIT")) {
                    params.put("customer_id", id);
                } else {
                    params.put("type", "Add");
                }

                params.put("name", edt_cust_name.getText().toString());
                params.put("city", edt_cust_city.getText().toString());
                params.put("contact_no", edt_cust_no.getText().toString());
                params.put("contact_no1", edt_cust_other_no.getText().toString());
                params.put("email", edt_cust_email.getText().toString());
                params.put("address", edt_cust_address.getText().toString());

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
   //     overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}

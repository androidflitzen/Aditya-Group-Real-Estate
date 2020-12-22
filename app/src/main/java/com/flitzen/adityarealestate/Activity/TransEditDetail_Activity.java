package com.flitzen.adityarealestate.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


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
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Classes.Helper;
import com.flitzen.adityarealestate.Classes.Utils;
import com.flitzen.adityarealestate.R;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransEditDetail_Activity extends AppCompatActivity {


    ProgressDialog prd;
    Activity mActivity;
    @BindView(R.id.edt_Transcustname)
    EditText edtTranscustname;
    @BindView(R.id.edt_Transcustno)
    EditText edtTranscustno;
    @BindView(R.id.edt_Transcustotherno)
    EditText edtTranscustotherno;
    @BindView(R.id.edt_Transcustemail)
    EditText edtTranscustemail;
    @BindView(R.id.edt_Transcustaddress)
    EditText edtTranscustaddress;
    @BindView(R.id.edt_Transcustcity)
    EditText edtTranscustcity;
    @BindView(R.id.tvEditSave)
    TextView tvEditSave;
    @BindView(R.id.btn_TransEditSave)
    CardView btnTransEditSave;

    String customer_id, customer_name, contact_no, contact_no1, email, city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_edit_detail_);
        ButterKnife.bind(this);

        mActivity = TransEditDetail_Activity.this;

        initUi();

    }

    private void initUi() {

        getSupportActionBar().setTitle(Html.fromHtml("Edit Customers Profile"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        customer_id = getIntent().getStringExtra("customer_id");
        customer_name = getIntent().getStringExtra("customer_name");
        contact_no = getIntent().getStringExtra("contact_no");
        contact_no1 = getIntent().getStringExtra("contact_no1");
        email = getIntent().getStringExtra("email");
        city = getIntent().getStringExtra("city");


        edtTranscustname.setText(customer_name);
        edtTranscustno.setText(contact_no);
        edtTranscustotherno.setText(contact_no1);
        edtTranscustemail.setText(email);
        edtTranscustcity.setText(city);

        Utils.showLog("==id" + customer_id);

        btnTransEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtTranscustname.getText().toString().equals("")) {
                    edtTranscustname.setError("Enter Name");
                    edtTranscustname.requestFocus();
                    return;
                } else if (edtTranscustno.getText().toString().equals("")) {
                    edtTranscustno.setError("Enter Mobile No.");
                    edtTranscustno.requestFocus();
                    return;
                } else if (edtTranscustno.getText().toString().length() != 10) {
                    edtTranscustno.setError("Mobile No. must be 10 digit");
                    edtTranscustno.requestFocus();
                    return;
                } else if (!edtTranscustotherno.getText().toString().equals("") && edtTranscustotherno.getText().toString().length() != 10) {
                    edtTranscustotherno.setError("Mobile No. must be 10 digit");
                    edtTranscustotherno.requestFocus();
                    return;
                } else if (!edtTranscustemail.getText().toString().equals("") && !edtTranscustemail.getText().toString().trim().matches(Helper.emailPattern)) {
                    edtTranscustemail.setError("Enter Valid Email");
                    edtTranscustemail.requestFocus();
                    return;

                } else if (edtTranscustcity.getText().toString().equals("")) {
                    edtTranscustcity.setError("Enter City");
                    edtTranscustcity.requestFocus();
                    return;


                } else {
                    EditCustomerProfile();
                }

            }
        });

    }

    private void EditCustomerProfile() {
        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Transacation_Customers").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(customer_id)) {

                                DatabaseReference cineIndustryRef = databaseReference.child("Transacation_Customers").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();

                                map.put("name", edtTranscustname.getText().toString());
                                map.put("city", edtTranscustcity.getText().toString());
                                map.put("contact_no", edtTranscustno.getText().toString());
                                map.put("contact_no1", edtTranscustotherno.getText().toString());
                                map.put("email", edtTranscustemail.getText().toString());
                                map.put("address", edtTranscustaddress.getText().toString());

                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        new CToast(mActivity).simpleToast("Customer updated successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                    //    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                                        TransactionDetails_Activity.tvTansCustName.setText(edtTranscustname.getText().toString());
                                        TransactionDetails_Activity.tvTransCustMobile.setText(edtTranscustno.getText().toString());
                                        TransactionDetails_Activity.tvTransCustMobile1.setText(edtTranscustotherno.getText().toString());
                                        TransactionDetails_Activity.tvTransCustEmailId.setText(edtTranscustemail.getText().toString());
                                        TransactionDetails_Activity.tvTransCustCity.setText(edtTranscustcity.getText().toString());
                                        TransactionDetails_Activity.tvTitle.setText(edtTranscustname.getText().toString());
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

    private void EditCustomerProfile1() {


        String URL = "";
        URL = API.TRANS_EDITPROFILE;

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
                   //     overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                        TransactionDetails_Activity.tvTansCustName.setText(edtTranscustname.getText().toString());
                        TransactionDetails_Activity.tvTransCustMobile.setText(edtTranscustno.getText().toString());
                        TransactionDetails_Activity.tvTransCustMobile1.setText(edtTranscustotherno.getText().toString());
                        TransactionDetails_Activity.tvTransCustEmailId.setText(edtTranscustemail.getText().toString());
                        TransactionDetails_Activity.tvTransCustCity.setText(edtTranscustcity.getText().toString());
                        TransactionDetails_Activity.tvTitle.setText(edtTranscustname.getText().toString());
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

                params.put("name", edtTranscustname.getText().toString());
                params.put("city", edtTranscustcity.getText().toString());
                params.put("contact_no", edtTranscustno.getText().toString());
                params.put("contact_no1", edtTranscustotherno.getText().toString());
                params.put("email", edtTranscustemail.getText().toString());
                params.put("address", edtTranscustaddress.getText().toString());
                params.put("customer_id", customer_id);

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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
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
    public void onBackPressed() {
        super.onBackPressed();
    //    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }


}

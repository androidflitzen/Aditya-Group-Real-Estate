package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.text.Html;
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
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransCustomer_Add_Activity extends AppCompatActivity {

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
    @BindView(R.id.txt_add_customer)
    TextView txtAddCustomer;
    @BindView(R.id.btn_Transaddcustomer)
    CardView btnTransaddcustomer;

    ProgressDialog prd;
    Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_customer__add_);
        ButterKnife.bind(this);

        mActivity = TransCustomer_Add_Activity.this;

        initUi();

    }

    private void initUi() {

        getSupportActionBar().setTitle(Html.fromHtml("Add New Customers"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnTransaddcustomer.setOnClickListener(new View.OnClickListener() {
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
                } else {
                    addCustomer();
                }

            }
        });

    }

    private void addCustomer() {

        showPrd();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String key = rootRef.child("Transacation_Customers").push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("name", edtTranscustname.getText().toString());
        map.put("city", edtTranscustcity.getText().toString());
        map.put("contact_no", edtTranscustno.getText().toString());
        map.put("contact_no1", edtTranscustotherno.getText().toString());
        map.put("email", edtTranscustemail.getText().toString());
        map.put("address", edtTranscustaddress.getText().toString());
        map.put("id", key);
        map.put("status", 0);

        rootRef.child("Transacation_Customers").child(key).setValue(map).addOnCompleteListener(TransCustomer_Add_Activity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hidePrd();
                new CToast(mActivity).simpleToast("Customer added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
             //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                finish();
            }

        }).addOnFailureListener(TransCustomer_Add_Activity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePrd();
                Toast.makeText(TransCustomer_Add_Activity.this, "Please try later...", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void addCustomer1() {

        String URL = "";
        URL = API.ADD_TRANSACTION_CUSTOMERLIST;

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
                     //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
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
    //    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
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
     //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }

}

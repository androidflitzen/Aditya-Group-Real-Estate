package com.flitzen.adityarealestate_new.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.flitzen.adityarealestate_new.Adapter.Adapter_Transaction_List;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Fragment.ActionBottomDialogFragment;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.Items.Item_Sites_List;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Add_fab_Payment_Activity extends AppCompatActivity implements ActionBottomDialogFragment.ItemClickListener{


    @BindView(R.id.tvTransCompanySpn)
    TextView tvTransCompanySpn;
    @BindView(R.id.rvTranCompnyspn)
    RelativeLayout rvTranCompnyspn;
    @BindView(R.id.tvTransType1)
    TextView tvTransType1;
    @BindView(R.id.tvTransTypespn)
    TextView tvTransTypespn;
    @BindView(R.id.rvTransTypespn)
    RelativeLayout rvTransTypespn;
    @BindView(R.id.etTrans_Date)
    TextView etTransDate;
    @BindView(R.id.etTrans_Amount)
    EditText etTransAmount;
    @BindView(R.id.etTrans_Note)
    EditText etTransNote;
    @BindView(R.id.btn_TransAdd)
    TextView btnTransAdd;
    @BindView(R.id.btn_add_share_payment)
    Button btn_add_share_payment;

    Context context;
    API webapi;
    ProgressDialog prd;
    String cust_id = "", typeid = "1";

    Adapter_Transaction_List adapterTransactionList;
    ArrayList<Item_Sites_List> transactionlist = new ArrayList<>();

    List<Item_Customer_List> itemListCustomer = new ArrayList<>();
    List<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();


    String customer_name="", customer_id, type1, type2 = "Cash Payment";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fab__transaction_);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(Html.fromHtml("Add Payment"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = Add_fab_Payment_Activity.this;


        customer_id = getIntent().getStringExtra("customer_id");
        customer_name = getIntent().getStringExtra("customer_name");


        tvTransCompanySpn.setText(customer_name);
        tvTransTypespn.setText(type2);


        rvTransTypespn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {
                        "Cash Received", "Cash Payment"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Add_fab_Payment_Activity.this);
                builder.setTitle("Make your selection");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        tvTransTypespn.setText(items[item]);
                        tvTransType1.setTextColor(getResources().getColor(R.color.blackText1));
                        // typeid = "1";


                        if (item == 0) {
                            typeid = "0";

                        } else if (item == 1) {
                            typeid = "1";
                        }


                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        etTransDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etTransDate.setText(Helper.getCurrentDate("dd/MM/yyyy"));
                etTransDate.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

                etTransDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Helper.pick_Date(Add_fab_Payment_Activity.this, etTransDate);
                    }
                });

            }
        });

        btnTransAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvTransCompanySpn.getText().toString().equals("")) {
                    tvTransCompanySpn.setError("Select Party Name");
                    tvTransCompanySpn.requestFocus();
                    return;
                } else if (tvTransTypespn.getText().toString().equals("")) {
                    tvTransTypespn.setError("Select Payment Type");
                    tvTransTypespn.requestFocus();
                    return;
                } else if (etTransDate.getText().toString().equals("")) {
                    etTransDate.setError("Select Payment Date");
                    etTransDate.requestFocus();
                    return;
                } else if (etTransAmount.getText().toString().equals("")) {
                    etTransAmount.setError("Select Payment Amount");
                    etTransAmount.requestFocus();
                    return;
                } else {

                    Utils.showLog("cust_id" + customer_id + typeid);
                    addTransactionApi(customer_id, typeid, etTransDate.getTag().toString().trim(), etTransAmount.getText().toString().trim(), etTransNote.getText().toString().trim(),0);
                }
            }
        });

        btn_add_share_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvTransCompanySpn.getText().toString().equals("")) {
                    tvTransCompanySpn.setError("Select Party Name");
                    tvTransCompanySpn.requestFocus();
                    return;
                } else if (tvTransTypespn.getText().toString().equals("")) {
                    tvTransTypespn.setError("Select Payment Type");
                    tvTransTypespn.requestFocus();
                    return;
                } else if (etTransDate.getText().toString().equals("")) {
                    etTransDate.setError("Select Payment Date");
                    etTransDate.requestFocus();
                    return;
                } else if (etTransAmount.getText().toString().equals("")) {
                    etTransAmount.setError("Select Payment Amount");
                    etTransAmount.requestFocus();
                    return;
                } else {

                    Utils.showLog("cust_id" + customer_id + typeid);
                    addTransactionApi(customer_id, typeid, etTransDate.getTag().toString().trim(), etTransAmount.getText().toString().trim(), etTransNote.getText().toString().trim(),1);
                }
            }
        });


    }

    private void addTransactionApi(final String customer_id, final String typeid, final String date, final String amount, final String note,int checkButton) {

        showPrd();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String key = rootRef.child("Transactions").push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("customer_id", customer_id);
        map.put("payment_type", typeid);
        map.put("transaction_date", date);
        map.put("amount", amount);
        map.put("transaction_note", note);
        map.put("transaction_id", key);
        map.put("last_updated", date+" "+"00:00:00");
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        map.put("created_at", date+" "+currentTime);


        rootRef.child("Transactions").child(key).setValue(map).addOnCompleteListener(Add_fab_Payment_Activity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hidePrd();
                if (checkButton == 1) {
                    if (appInstalledOrNot() == 0) {
                        if (checkButton == 1) {
                            sendMessage(amount, "com.whatsapp");
                        }
                    } else if (appInstalledOrNot() == 1) {
                        if (checkButton == 1) {
                            sendMessage(amount, "com.whatsapp.w4b");
                        }
                    } else if (appInstalledOrNot() == 2) {
                        if (checkButton == 1) {
                            sendMessage(amount, "both");
                        }
                    }
                }
                new CToast(Add_fab_Payment_Activity.this).simpleToast("Payment added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                finish();
            }

        }).addOnFailureListener(Add_fab_Payment_Activity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePrd();
                Toast.makeText(Add_fab_Payment_Activity.this, "Please try later...", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void sendMessage(String amount, String pkg) {
        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("text/plain");
        String text="";
        text = "Dear  '"+customer_name+"'\n"+"Your payment has been credited "+getResources().getString(R.string.rupee)+amount+" to "+getResources().getString(R.string.app_name)+".\n"+"\nThanks";

        if (pkg.equalsIgnoreCase("both")) {
            showBottomSheet(text);
        } else {
            waIntent.setPackage(pkg);
            if (waIntent != null) {
                waIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(waIntent, text));
            } else {
                Toast.makeText(this, "WhatsApp not found", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private int appInstalledOrNot() {
        String pkgW = "com.whatsapp";
        String pkgWB = "com.whatsapp.w4b";
        PackageManager pm = getPackageManager();
        int app_installed_whatsapp = -1;
        int app_installed_w4b = -1;
        int common = -1;
        try {
            pm.getPackageInfo(pkgW, PackageManager.GET_ACTIVITIES);
            app_installed_whatsapp = 1;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed_whatsapp = 0;
        }

        try {
            pm.getPackageInfo(pkgWB, PackageManager.GET_ACTIVITIES);
            app_installed_w4b = 1;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed_w4b = 0;
        }

        if (app_installed_w4b == 1 & app_installed_whatsapp == 1) {
            common = 2;
        } else if (app_installed_whatsapp == 1) {
            common = 0;
        } else if (app_installed_w4b == 1) {
            common = 1;
        }

        return common;
    }

    public void showBottomSheet(String text) {
        ActionBottomDialogFragment addPhotoBottomDialogFragment =
                new ActionBottomDialogFragment(text);
        addPhotoBottomDialogFragment.show(this.getSupportFragmentManager(),
                ActionBottomDialogFragment.TAG);
    }

    @Override
    public void onItemClick(View view,String text) {
        if (view.getId() == R.id.button1) {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            if(waIntent!=null){
                waIntent.setPackage("com.whatsapp");
                if (waIntent != null) {
                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, text));
                } else {
                    Toast.makeText(this, "WhatsApp not found", Toast.LENGTH_SHORT)
                            .show();
                }
            }else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        } else if (view.getId() == R.id.button2) {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            if(waIntent!=null){
                waIntent.setPackage("com.whatsapp.w4b");
                if (waIntent != null) {
                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, text));
                } else {
                    Toast.makeText(this, "WhatsApp not found", Toast.LENGTH_SHORT)
                            .show();
                }
            }else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addTransactionApi1(final String customer_id, final String typeid, final String date, final String amount, final String note) {

        //Utils.showToast(context,"Date"+date);

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.ADD_TRANSACTION_PAYMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Plot Add", response);
                    if (jsonObject.getInt("result") == 1) {

                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        finish();

                    } else {
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("customer_id", customer_id);
                params.put("payment_type", typeid);
                params.put("transaction_date", date);
                params.put("amount", amount);
                params.put("transaction_note", note);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void showPrd() {
        prd = new ProgressDialog(context);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }


}

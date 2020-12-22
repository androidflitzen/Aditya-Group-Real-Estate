package com.flitzen.adityarealestate.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
import com.flitzen.adityarealestate.Adapter.Adapter_Transaction_List;
import com.flitzen.adityarealestate.Adapter.Spn_Adapter;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Classes.Helper;
import com.flitzen.adityarealestate.Classes.Utils;
import com.flitzen.adityarealestate.Items.Item_Customer_List;
import com.flitzen.adityarealestate.Items.Item_Sites_List;
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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Edit_Transaction_Activity extends AppCompatActivity {


    Context context;
    API webapi;
    ProgressDialog prd;
    String cust_id = "", typeid = "";

    String customer_id, customer_name, transaction_id, payment_type, transaction_date, amount, transaction_note;

    Adapter_Transaction_List adapterTransactionList;
    ArrayList<Item_Sites_List> transactionlist = new ArrayList<>();

    List<Item_Customer_List> itemListCustomer = new ArrayList<>();
    List<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();
    @BindView(R.id.tvTransType1)
    TextView tvTransType1;
    @BindView(R.id.tvTransCompanySpn)
    TextView tvTransCompanySpn;
    @BindView(R.id.rvTranCompnyspn)
    RelativeLayout rvTranCompnyspn;
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
    @BindView(R.id.btn_TransEdit)
    TextView btnTransEdit;

    public static String finaldate = "";
    public static Boolean isDateChange = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__transaction_);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(Html.fromHtml("Edit Transaction"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = Edit_Transaction_Activity.this;


        customer_id = getIntent().getStringExtra("customer_id");
        transaction_id = getIntent().getStringExtra("transaction_id");
        customer_name = getIntent().getStringExtra("customer_name");
        payment_type = getIntent().getStringExtra("payment_type");
        transaction_date = getIntent().getStringExtra("transaction_date");
        amount = getIntent().getStringExtra("amount");
        transaction_note = getIntent().getStringExtra("transaction_note");

        tvTransCompanySpn.setText(customer_name);

        if (payment_type.equals("0")) {
            tvTransTypespn.setText("Cash Received");
        } else if (payment_type.equals("1")) {
            tvTransTypespn.setText("Cash Payment");
        }

        //tvTransTypespn.setText(payment_type);

        String datePattern = "\\d{2}-\\d{2}-\\d{4}";
        boolean isDate1 = transaction_date.matches(datePattern);

        if (isDate1 == true) {
            etTransDate.setText(transaction_date);
        } else {
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            Date d = null;
            try {
                d = f.parse(transaction_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            DateFormat date2 = new SimpleDateFormat("dd-MM-yyyy");
            String date22 = date2.format(d);
            etTransDate.setText(date22);
        }


        if (payment_type.equals("0")) {
            etTransAmount.setText(amount);
        } else if (payment_type.equals("1")) {
            etTransAmount.setText(amount);
        }
        // etTransAmount.setText(amount);
        etTransNote.setText(transaction_note);


        rvTranCompnyspn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getCompanyapi(tvTransCompanySpn);

            }
        });

        rvTransTypespn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence[] items = {
                        "Cash Received", "Cash Payment"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Transaction_Activity.this);
                builder.setTitle("Make your selection");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        tvTransTypespn.setText(items[item]);
                        tvTransType1.setTextColor(getResources().getColor(R.color.blackText1));
                        if (item == 0) {
                            payment_type = "0";

                        } else if (item == 1) {
                            payment_type = "1";
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

               // etTransDate.setText(Helper.getCurrentDate("dd-MM-yyyy"));
                etTransDate.setTag(Helper.getCurrentDate("yyyy-MM-dd"));


                etTransDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Helper.pick_Date(Edit_Transaction_Activity.this, etTransDate);


                    }
                });

            }
        });

        btnTransEdit.setOnClickListener(new View.OnClickListener() {
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
                    etTransAmount.setError("Select Payment Date");
                    etTransAmount.requestFocus();
                    return;
                } else {

                    // Utils.showLog("cust_id" + transaction_id + customer_id + payment_type + etTransNote.getText().toString().trim() + etTransDate.getText().toString().trim() + etTransAmount.getText().toString().trim());
                    editTransactionApi(transaction_id, customer_id, payment_type, etTransDate.getText().toString().trim(), etTransAmount.getText().toString().trim(), etTransNote.getText().toString().trim());
                }

            }
        });

    }

    private void editTransactionApi(final String transaction_id, final String customer_id, final String payment_type, final String date, final String amount, final String note) {

         showPrd();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Transactions").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {
                            if (dataSnapshot.child("transaction_id").getValue().toString().equals(transaction_id)) {
                                DatabaseReference cineIndustryRef = databaseReference.child("Transactions").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("transaction_id", transaction_id);
                                map.put("customer_id", customer_id);
                                map.put("payment_type", payment_type);

                                map.put("amount", amount);
                                map.put("transaction_note", note);

                                String date11=date.replace("/", "-");

                                String datePattern = "\\d{4}-\\d{2}-\\d{2}";
                                boolean isDate1 = date11.matches(datePattern);

                                if (isDate1 == true) {
                                    map.put("transaction_date", date11);
                                } else {
                                    DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                                    Date d = f.parse(date11);
                                    DateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
                                    String date22 = date2.format(d);
                                    map.put("transaction_date", date22);
                                }

                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        new CToast(context).simpleToast("Payment updated successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        finaldate = date;
                                        isDateChange = true;
                                        //  Utils.showLog("Finaldate" + finaldate);

                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hidePrd();
                                        new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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

    private void editTransactionApi1(final String transaction_id, final String customer_id, final String payment_type, final String date, final String amount, final String note) {

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.EDIT_TRANSACTION_PAYMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Plot Add", response);
                    if (jsonObject.getInt("result") == 1) {


                        // Utils.showLog("cust_id" +transaction_id+customer_id+ typeid+etTransNote.getText().toString().trim() + etTransDate.getText().toString().trim() + etTransAmount.getText().toString().trim());


                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();


                        finaldate = date;
                        isDateChange = true;
                        //  Utils.showLog("Finaldate" + finaldate);

                        finish();
                        adapterTransactionList.notifyDataSetChanged();

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
                params.put("transaction_id", transaction_id);
                params.put("customer_id", customer_id);
                params.put("payment_type", payment_type);
                params.put("transaction_date", date.replace("/", "-"));
                params.put("amount", amount);
                params.put("transaction_note", note);

                Utils.showLog("date33" + new JSONObject(params).toString());

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

    private void getCompanyapi(final TextView view) {

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
                        itemListCustomer.clear();
                        itemListCustomerTemp.clear();
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {
                                if(dataSnapshot.child("status").getValue().toString().equals("0")){
                                    Item_Customer_List item = new Item_Customer_List();
                                    item.setId(dataSnapshot.child("id").getValue().toString());
                                    item.setName(dataSnapshot.child(("name")).getValue().toString());
                                    item.setCity(dataSnapshot.child(("city")).getValue().toString());
                                    item.setContact_no(dataSnapshot.child(("contact_no")).getValue().toString());
                                    item.setEmail(dataSnapshot.child(("email")).getValue().toString());
                                    item.setAddress(dataSnapshot.child(("address")).getValue().toString());

                                    itemListCustomer.add(item);
                                    itemListCustomerTemp.add(item);
                                }
                        }
                        if (!view.equals("")) {

                            itemListCustomer.clear();

                            for (int i = 0; i < itemListCustomerTemp.size(); i++) {
                                if (!view.equals(itemListCustomerTemp.get(i).getId())) {
                                    itemListCustomer.add(itemListCustomerTemp.get(i));
                                }
                            }

                            customerDialog(view);

                        } else {
                            customerDialog(view);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();

            }
        });

    }

    private void getCompanyapi1(final TextView view) {

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.CUSTOMER + "?type=List", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hidePrd();
                try {
                    itemListCustomer.clear();
                    itemListCustomerTemp.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Customer List", response);

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("customers");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Customer_List item = new Item_Customer_List();
                            item.setId(object.getString("id"));
                            item.setName(object.getString("name"));
                            item.setCity(object.getString("city"));
                            item.setContact_no(object.getString("contact_no"));
                            item.setEmail(object.getString("email"));
                            item.setAddress(object.getString("address"));

                            itemListCustomer.add(item);
                            itemListCustomerTemp.add(item);
                        }

                        if (!view.equals("")) {

                            itemListCustomer.clear();

                            for (int i = 0; i < itemListCustomerTemp.size(); i++) {
                                if (!view.equals(itemListCustomerTemp.get(i).getId())) {
                                    itemListCustomer.add(itemListCustomerTemp.get(i));
                                }
                            }

                            customerDialog(view);

                        } else {
                            customerDialog(view);
                        }

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
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);


    }

    private void customerDialog(final TextView textView) {

        LayoutInflater localView = LayoutInflater.from(context);
        View promptsView = localView.inflate(R.layout.dialog_spinner, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText edtSearchLocation = (EditText) promptsView.findViewById(R.id.edt_spn_search);
        final ListView list_location = (ListView) promptsView.findViewById(R.id.list_spn);
        final TextView tvAddNewCustomer = (TextView) promptsView.findViewById(R.id.tvAddNewCustomer);

        tvAddNewCustomer.setVisibility(View.GONE);

        final ArrayList<String> arrayListTemp = new ArrayList<>();
        final ArrayList<String> arrayListId = new ArrayList<>();

        edtSearchLocation.setHint("Search Customer");
        for (int i = 0; i < itemListCustomer.size(); i++) {
            arrayListTemp.add(itemListCustomer.get(i).getName());
            arrayListId.add(itemListCustomer.get(i).getId());
        }


        list_location.setAdapter(new Spn_Adapter(context, arrayListTemp));

        edtSearchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int position, int i1, int i2) {

                if (edtSearchLocation.getText().toString().trim().length() > 0) {
                    arrayListTemp.clear();
                    arrayListId.clear();

                    for (int j = 0; j < itemListCustomer.size(); j++) {
                        String word = edtSearchLocation.getText().toString().toLowerCase();
                        if (itemListCustomer.get(j).getName().toLowerCase().contains(word)) {
                            arrayListTemp.add(itemListCustomer.get(j).getName());
                            arrayListId.add(itemListCustomer.get(j).getId());
                        }
                    }
                    list_location.setAdapter(new Spn_Adapter(context, arrayListTemp));
                } else {
                    arrayListTemp.clear();
                    arrayListId.clear();
                    for (int i = 0; i < itemListCustomer.size(); i++) {
                        arrayListTemp.add(itemListCustomer.get(i).getName());
                        arrayListId.add(itemListCustomer.get(i).getId());
                    }
                    list_location.setAdapter(new Spn_Adapter(context, arrayListTemp));
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

     /*   list_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                OpenAddNewCustomer(textView);
                alertDialog.dismiss();
            }

        });
        */


        list_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // OpenAddNewCustomer(textView);
                textView.setText(arrayListTemp.get(position));
                textView.setTag(arrayListId.get(position));
                customer_id = arrayListId.get(position);
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

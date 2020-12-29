package com.flitzen.adityarealestate_new.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Adapter.RentCustomerAdapter;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Items.Customer;
import com.flitzen.adityarealestate_new.Items.Item_Property_List;
import com.flitzen.adityarealestate_new.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RentCustomerHistoryActivity extends AppCompatActivity {
    public String property_id, property_name;
    @BindView(R.id.rec_rent_list)
    RecyclerView recRentList;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    ArrayList<Customer> itemList = new ArrayList<>();
    RentCustomerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_history);
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {

        property_id = getIntent().getStringExtra("property_id");
        // property_id = itemList.get(0).getProperty_id();
        property_name = getIntent().getStringExtra("property_name");
        // getSupportActionBar().setTitle(Html.fromHtml(property_name+" Customers"));

        Utils.showLog("==pro_id" + property_id);

        // getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        TextView tvtitle = (TextView) findViewById(R.id.tvtitle);
        tvtitle.setText(property_name + " Customers");

        ImageView ivEdit1 = (ImageView) findViewById(R.id.ivEdit1);
        ivEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(0, 0);
                onBackPressed();

            }
        });

        adapter = new RentCustomerAdapter(this, itemList);
        recRentList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recRentList.setAdapter(adapter);
        adapter.setOnItemClickListener(new RentCustomerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(RentCustomerHistoryActivity.this, Activity_Rent_Details.class);
                intent.putExtra("position", position);
                intent.putExtra("property_name", property_name);
                intent.putExtra("property_id", property_id);
                intent.putExtra("customer_id", itemList.get(position).getCustomer_id());
                intent.putExtra("status", itemList.get(position).getRent_status());
                intent.putExtra("pdf_code", "0");
                startActivity(intent);
            }
        });
        getRentList();

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                getRentList();
            }
        });
    }

    public void getRentList() {

        swipeRefresh.setRefreshing(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("RentHistory").orderByKey();
        //databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipeRefresh.setRefreshing(false);
                itemList.clear();
                ArrayList<String> allCustomerID = new ArrayList<>();
                allCustomerID.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if (npsnapshot.child("property_id").getValue().toString().equals(property_id)) {
                                    allCustomerID.add(npsnapshot.child("customer_id").getValue().toString());
                                    Customer customer = new Customer();
                                    customer.setCustomer_id(npsnapshot.child("customer_id").getValue().toString());
                                    customer.setProperty_id(npsnapshot.child("property_id").getValue().toString());
                                    customer.setId(npsnapshot.child("id").getValue().toString());
                                    //customer.setRent_status(npsnapshot.child("rent_status").getValue().toString());

                                    customer.setStart_date(npsnapshot.child("start_date").getValue().toString());
                                    customer.setEnd_date(npsnapshot.child("end_date").getValue().toString());

                                    if(npsnapshot.child("end_date").getValue().toString().equals("0000-00-00")){
                                        customer.setRent_status("0");
                                    }
                                    else {
                                        customer.setRent_status("1");
                                    }

                                    //customer.setPayment_date(npsnapshot.child("payment_date").getValue().toString());

                                   /* customer.setStart_date(npsnapshot.child("payment_date").getValue().toString());
                                    customer.setEnd_date(npsnapshot.child("payment_date").getValue().toString());*/

                                    String customerId = npsnapshot.child("customer_id").getValue().toString();

                                    Query query = databaseReference.child("Customers").orderByKey();
                                    // databaseReference.keepSynced(true);
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                        if (npsnapshot1.child("id").getValue().toString().equals(customerId)) {
                                                            String name = npsnapshot1.child("name").getValue().toString();
                                                            Log.e("Name  ", name);
                                                            customer.setCustomer_name(name);
                                                        }
                                                    }
                                                }
                                            } catch (Exception e) {
                                                Log.e("Ex   ", e.toString());
                                            }

                                            Collections.sort(itemList, new Comparator<Customer>() {
                                                @Override
                                                public int compare(Customer o1, Customer o2) {
                                                    return o1.getStart_date().compareTo(o2.getStart_date());
                                                }
                                            }.reversed());
                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("error ", error.getMessage());
                                        }
                                    });
                                    itemList.add(customer);
                            }
                            // }
                        }

                        Collections.sort(itemList, new Comparator<Customer>() {
                            @Override
                            public int compare(Customer o1, Customer o2) {
                                return o1.getStart_date().compareTo(o2.getStart_date());
                            }
                        }.reversed());
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.e("Test  ", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                new CToast(RentCustomerHistoryActivity.this).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    public void getRentList2() {

        swipeRefresh.setRefreshing(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Payments").orderByKey();
        //databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipeRefresh.setRefreshing(false);
                itemList.clear();
                ArrayList<String> allCustomerID = new ArrayList<>();
                allCustomerID.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if (npsnapshot.child("property_id").getValue().toString().equals(property_id)) {
                                if (allCustomerID.contains(npsnapshot.child("customer_id").getValue().toString())) {
                                    for (int i = 0; i < itemList.size(); i++) {
                                        if (itemList.get(i).getCustomer_id().equals(npsnapshot.child("customer_id").getValue().toString())) {
                                           /* itemList.get(i).setEnd_date(npsnapshot.child("payment_date").getValue().toString());
                                            if(itemList.get(i).getStart_date().equals("0000-00-00 00:00:00")){
                                                itemList.get(i).setStart_date(npsnapshot.child("payment_date").getValue().toString());
                                            }*/
                                        }
                                    }
                                } else {
                                    allCustomerID.add(npsnapshot.child("customer_id").getValue().toString());
                                    Customer customer = new Customer();
                                    customer.setCustomer_id(npsnapshot.child("customer_id").getValue().toString());
                                    customer.setProperty_id(npsnapshot.child("property_id").getValue().toString());
                                    customer.setRent_status(npsnapshot.child("rent_status").getValue().toString());
                                    customer.setPayment_date(npsnapshot.child("payment_date").getValue().toString());

                                   /* customer.setStart_date(npsnapshot.child("payment_date").getValue().toString());
                                    customer.setEnd_date(npsnapshot.child("payment_date").getValue().toString());*/

                                    String customerId = npsnapshot.child("customer_id").getValue().toString();

                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    Query queryR = databaseReference.child("RentHistory").orderByKey();
                                    //databaseReference.keepSynced(true);
                                    queryR.addValueEventListener(new ValueEventListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot npsnapshot2 : dataSnapshot.getChildren()) {
                                                        if(npsnapshot2.child("property_id").getValue().toString().equals(property_id)){
                                                            if(npsnapshot2.child("customer_id").getValue().toString().equals(customerId)){
                                                                customer.setStart_date(npsnapshot2.child("start_date").getValue().toString());
                                                                customer.setEnd_date(npsnapshot2.child("end_date").getValue().toString());
                                                            }
                                                        }
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                }
                                            } catch (Exception e) {
                                                Log.e("Test  ", e.getMessage());
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.e("ViewAllSitesFragment", databaseError.getMessage());
                                            new CToast(RentCustomerHistoryActivity.this).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                            swipeRefresh.setRefreshing(false);
                                        }
                                    });

                                    Query query = databaseReference.child("Customers").orderByKey();
                                    // databaseReference.keepSynced(true);
                                    query.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                        if (npsnapshot1.child("id").getValue().toString().equals(customerId)) {
                                                            String name = npsnapshot1.child("name").getValue().toString();
                                                            Log.e("Name  ", name);
                                                            customer.setCustomer_name(name);
                                                        }
                                                    }
                                                }
                                            } catch (Exception e) {
                                                Log.e("Ex   ", e.toString());
                                            }

                                            Collections.sort(itemList, new Comparator<Customer>() {
                                                @Override
                                                public int compare(Customer o1, Customer o2) {
                                                    return o1.getPayment_date().compareTo(o2.getPayment_date());
                                                }
                                            }.reversed());
                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("error ", error.getMessage());
                                        }
                                    });
                                    itemList.add(customer);
                                }
                            }
                            // }
                        }

                        Collections.sort(itemList, new Comparator<Customer>() {
                            @Override
                            public int compare(Customer o1, Customer o2) {
                                return o1.getPayment_date().compareTo(o2.getPayment_date());
                            }
                        }.reversed());


                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.e("Test  ", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                new CToast(RentCustomerHistoryActivity.this).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    public void getRentList1() {

        swipeRefresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.PAYMENT_HISTORY_CUSTOMER_URL + property_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                swipeRefresh.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("history");
                        itemList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Customer customer = new Customer();
                            customer.setCustomer_id(object.getString("customer_id"));
                            customer.setCustomer_name(object.getString("customer_name"));
                            customer.setProperty_id(object.getString("property_id"));
                            itemList.add(customer);
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);

                    } else {
                        new CToast(RentCustomerHistoryActivity.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        return;

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefresh.setRefreshing(false);
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
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        getRentList();
        super.onBackPressed();

        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        Intent intent = new Intent(RentCustomerHistoryActivity.this, Activity_Rent_List.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
        //startActivity(new Intent(getContext(), Activity_Rent_List.class));

    }
}

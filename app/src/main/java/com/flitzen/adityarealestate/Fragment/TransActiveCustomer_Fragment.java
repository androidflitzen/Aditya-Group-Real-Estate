package com.flitzen.adityarealestate.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate.Activity.TransCustomer_Add_Activity;
import com.flitzen.adityarealestate.Adapter.Adapter_TransactionCustList;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Items.Item_Customer_List;
import com.flitzen.adityarealestate.Items.Trans_Customer_List;
import com.flitzen.adityarealestate.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TransActiveCustomer_Fragment extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.rvTransActiveCustomer)
    RecyclerView rvTransActiveCustomer;
    @BindView(R.id.fab_add_TransCustomer)
    FloatingActionButton fabAddTransCustomer;
    Activity mActivity;
    SwipeRefreshLayout swipe_refresh;

    Adapter_TransactionCustList adapterTransactionCustList;


    ArrayList<Trans_Customer_List> ListTransCustomer = new ArrayList<>();
    ArrayList<Trans_Customer_List> ListTransCustomerTemp = new ArrayList<>();
    @BindView(R.id.tvNoActiveCustomer)
    TextView tvNoActiveCustomer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfragment_activecust, null);
        unbinder = ButterKnife.bind(this, view);

        initUI(view);

        return view;


    }

    private void initUI(View view) {

        mActivity = getActivity();

        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));

        rvTransActiveCustomer.setLayoutManager(new LinearLayoutManager(mActivity));
        rvTransActiveCustomer.setHasFixedSize(true);
        adapterTransactionCustList = new Adapter_TransactionCustList(mActivity, ListTransCustomer, false);
        rvTransActiveCustomer.setAdapter(adapterTransactionCustList);


        fabAddTransCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(mActivity, TransCustomer_Add_Activity.class));
               // mActivity.overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                getActivity().overridePendingTransition(0, 0);


            }
        });


        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                getCustomerList();

            }
        });


        getCustomerList();


    }


    @Override
    public void onResume() {
        super.onResume();
        getCustomerList();
    }

    public void getCustomerList() {
        swipe_refresh.setRefreshing(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Transacation_Customers").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipe_refresh.setRefreshing(false);
                ListTransCustomer.clear();
                ListTransCustomerTemp.clear();
                tvNoActiveCustomer.setVisibility(View.GONE);
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if (npsnapshot.child("status").getValue().toString().equals("0")) {
                                // for (int i = 0; i < jsonArray.length(); i++) {

                                Trans_Customer_List item = new Trans_Customer_List();
                                item.setId(npsnapshot.child("id").getValue().toString());
                                item.setName(npsnapshot.child("name").getValue().toString());
                                item.setCity(npsnapshot.child("city").getValue().toString());
                                item.setContact_no(npsnapshot.child("contact_no").getValue().toString());
                                item.setAnother_no(npsnapshot.child("contact_no1").getValue().toString());
                                item.setEmail(npsnapshot.child("email").getValue().toString());
                                item.setAddress(npsnapshot.child("address").getValue().toString());

                                ListTransCustomer.add(item);
                                ListTransCustomerTemp.add(item);
                                tvNoActiveCustomer.setVisibility(View.GONE);
                            }
                        }
                        if(ListTransCustomer.size()!=0){
                            adapterTransactionCustList.notifyDataSetChanged();
                            tvNoActiveCustomer.setVisibility(View.GONE);
                        }
                        else {
                            tvNoActiveCustomer.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    Log.e("Test  ",e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                new CToast(mActivity).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                swipe_refresh.setRefreshing(false);

            }
        });
    }

    public void getCustomerList1() {
        swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.TRANSACTION_ACTIVECUSTOMERLIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                swipe_refresh.setRefreshing(false);
                try {
                    ListTransCustomer.clear();
                    ListTransCustomerTemp.clear();
                    tvNoActiveCustomer.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Customer List", response);

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("transcations");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Trans_Customer_List item = new Trans_Customer_List();
                            item.setId(object.getString("customer_id"));
                            item.setName(object.getString("customer_name"));
                            item.setCity(object.getString("city"));
                            item.setContact_no(object.getString("contact_no"));
                            item.setAnother_no(object.getString("contact_no1"));
                            item.setEmail(object.getString("email"));
                            item.setAddress(object.getString("address"));

                            ListTransCustomer.add(item);
                            ListTransCustomerTemp.add(item);
                            tvNoActiveCustomer.setVisibility(View.GONE);
                        }
                        adapterTransactionCustList.notifyDataSetChanged();
                        tvNoActiveCustomer.setVisibility(View.GONE);

                    } else {
                        tvNoActiveCustomer.setVisibility(View.VISIBLE);
                        // new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                swipe_refresh.setRefreshing(false);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}

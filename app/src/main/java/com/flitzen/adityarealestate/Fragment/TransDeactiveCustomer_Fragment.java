package com.flitzen.adityarealestate.Fragment;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate.Adapter.Adapter_TransDeactiveCust;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Items.Trans_Customer_List;
import com.flitzen.adityarealestate.R;
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

public class TransDeactiveCustomer_Fragment extends Fragment {


    Unbinder unbinder;
    @BindView(R.id.rvTransDeactiveCustomer)
    RecyclerView rvTransDeactiveCustomer;
    Activity mActivity;

    Adapter_TransDeactiveCust adapterTransDeactiveCust;

    ArrayList<Trans_Customer_List> ListTransCustomer = new ArrayList<>();
    ArrayList<Trans_Customer_List> ListTransCustomerTemp = new ArrayList<>();
    @BindView(R.id.tvNoDeActiveCustomer)
    TextView tvNoDeActiveCustomer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfragment_deactivecust, null);

        unbinder = ButterKnife.bind(this, view);
        initUI(view);


        return view;


    }

    private void initUI(View view) {


        mActivity = getActivity();

        rvTransDeactiveCustomer.setLayoutManager(new LinearLayoutManager(mActivity));
        rvTransDeactiveCustomer.setHasFixedSize(true);
        adapterTransDeactiveCust = new Adapter_TransDeactiveCust(mActivity, ListTransCustomer, false);
        rvTransDeactiveCustomer.setAdapter(adapterTransDeactiveCust);

        getDeactiveCustomerList();


    }


    @Override
    public void onResume() {
        super.onResume();
        getDeactiveCustomerList();
    }


    private void getDeactiveCustomerList() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Transacation_Customers").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ListTransCustomer.clear();
                ListTransCustomerTemp.clear();
                tvNoDeActiveCustomer.setVisibility(View.GONE);
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if (npsnapshot.child("status").getValue().toString().equals("1")) {
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
                                tvNoDeActiveCustomer.setVisibility(View.GONE);
                            }
                        }
                        if(ListTransCustomer.size()!=0){
                            adapterTransDeactiveCust.notifyDataSetChanged();
                            tvNoDeActiveCustomer.setVisibility(View.GONE);
                        }
                        else {
                            tvNoDeActiveCustomer.setVisibility(View.VISIBLE);
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

            }
        });
    }

    private void getDeactiveCustomerList1() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.TRANSACTION_DEACTIVECUSTOMERLIST, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                //       swipe_refresh.setRefreshing(false);
                try {
                    ListTransCustomer.clear();
                    ListTransCustomerTemp.clear();
                    tvNoDeActiveCustomer.setVisibility(View.GONE);

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
                            tvNoDeActiveCustomer.setVisibility(View.GONE);
                        }
                        adapterTransDeactiveCust.notifyDataSetChanged();
                        tvNoDeActiveCustomer.setVisibility(View.GONE);

                    } else {
                        tvNoDeActiveCustomer.setVisibility(View.VISIBLE);
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

                //  swipe_refresh.setRefreshing(false);
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

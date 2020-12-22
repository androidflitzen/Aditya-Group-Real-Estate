package com.flitzen.adityarealestate.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate.Activity.Add_fab_Payment_Activity;
import com.flitzen.adityarealestate.Activity.PdfCreatorRentActivity;
import com.flitzen.adityarealestate.Activity.PdfCreatorTransactionActivity;
import com.flitzen.adityarealestate.Activity.PdfViewActivity;
import com.flitzen.adityarealestate.Activity.TransactionDetails_Activity;
import com.flitzen.adityarealestate.Adapter.Adapter_Trans_PaymentList;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.Utils;
import com.flitzen.adityarealestate.Classes.WebAPI;
import com.flitzen.adityarealestate.Items.Transcation;
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

@SuppressLint("ValidFragment")
public class TranPayment_Fragment extends Fragment {

    Adapter_Trans_PaymentList adapterTransPaymentList;


    String rent_amount = "", customer_id = "", customer_name = "";
    int position;

    Activity mActivity;
    ArrayList<Transcation> transactionlist = new ArrayList<>();
    ArrayList<Transcation> transactionlistTemp = new ArrayList<>();

    WebAPI webapi;

    String file_url = "";

    Unbinder unbinder;

    RecyclerView rvTransPayment;
    @BindView(R.id.ivTransPaymentPdf)
    RelativeLayout ivTransPaymentPdf;
    @BindView(R.id.fab_addTransPayment)
    FloatingActionButton fabAddTransPayment;

   /* @BindView(R.id.rvTransPayment)
    RecyclerView rvTransPayment;*/

    TextView tvNoPayment ;

    String PaymentTotltal="0" ;

    @SuppressLint("ValidFragment")
    public TranPayment_Fragment(String customer_id, int position, String customer_name) {

        this.customer_id = customer_id ;
        this.position = position ;
        this.customer_name = customer_name ;

    }

    public TranPayment_Fragment() {
        // doesn't do anything special
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragmenttrans_payment, null);
        unbinder = ButterKnife.bind(this, view);
        initUI(view);

        return view;


        // customer_id = getArguments().getString(customer_id) ;


    }

    private void initUI(View view) {

        mActivity = getActivity();

        rvTransPayment = (RecyclerView) view.findViewById(R.id.rvTransPayment);
        rvTransPayment.setLayoutManager(new LinearLayoutManager(mActivity));
        rvTransPayment.setHasFixedSize(true);
        adapterTransPaymentList = new Adapter_Trans_PaymentList(mActivity, transactionlist, false);
        rvTransPayment.setAdapter(adapterTransPaymentList);

        //   Utils.showLog("cusrt" + customer_id);

        tvNoPayment = (TextView) view.findViewById(R.id.tvNoPayment);

        ivTransPaymentPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getActivity(), PdfCreatorTransactionActivity.class);
                intent.putExtra("customer_name",customer_name);
                intent.putExtra("phone_no","");
                intent.putExtra("paymentList",transactionlist);
                startActivity(intent);

              /*  file_url = API.TRANSACTION_PAYMENTPDF + "customer_id=" + customer_id;
                startActivity(new Intent(mActivity, PdfViewActivity.class)
                        .putExtra("pdf_url", file_url));
                Utils.showLog("=== file_url " + file_url);*/

            }
        });

        fabAddTransPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(mActivity, Add_fab_Payment_Activity.class);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("customer_name", customer_name);
                //    intent.putExtra("type2", transactionlist.get(1).getPaymentType());
                mActivity.startActivity(intent);


             /*   startActivity(new Intent(TransactionDetails_Activity.this, Add_fab_Transaction_Activity.class));
                TransactionDetails_Activity.this.overridePendingTransition(R.anim.feed_in, R.anim.feed_out);*/
            }
        });


    }


    private void getPaymentlist() {

        System.out.println("=========getPaymentlist   "+customer_id);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query queryDocument = databaseReference.child("Transactions").orderByKey();
        queryDocument.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        transactionlist.clear();
                        transactionlistTemp.clear();
                        tvNoPayment.setVisibility(View.GONE);
                        int PaymentTotltal1=0;
                        for (DataSnapshot npsnapshot5 : dataSnapshot.getChildren()) {
                            if (npsnapshot5.child("customer_id").getValue().toString().equals(customer_id)) {
                                if(npsnapshot5.child("payment_type").getValue().toString().equals("1")){
                                    Transcation item1 = new Transcation();
                                    item1.setTransactionId(npsnapshot5.child("transaction_id").getValue().toString());
                                    item1.setCustomerId(npsnapshot5.child("customer_id").getValue().toString());
                                    item1.setPaymentType(npsnapshot5.child("payment_type").getValue().toString());
                                    item1.setTransactionDate(npsnapshot5.child("transaction_date").getValue().toString());
                                    item1.setTransactionNote(npsnapshot5.child("transaction_note").getValue().toString());
                                    item1.setAmount(npsnapshot5.child("amount").getValue().toString());

                                    PaymentTotltal1=PaymentTotltal1+Integer.parseInt(npsnapshot5.child("amount").getValue().toString());
                                    PaymentTotltal=String.valueOf(PaymentTotltal1);

                                    Query queryCustomer = databaseReference.child("Transacation_Customers").orderByKey();
                                    queryCustomer.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            try {
                                                if (dataSnapshot.exists()) {
                                                    for (DataSnapshot npsnapshotCustomer : dataSnapshot.getChildren()) {
                                                        if (npsnapshotCustomer.child("id").getValue().toString().equals(customer_id)) {
                                                            String name = npsnapshotCustomer.child("name").getValue().toString();
                                                            item1.setCustomerName(name);
                                                        }
                                                    }
                                                    adapterTransPaymentList.notifyDataSetChanged();
                                                    tvNoPayment.setVisibility(View.GONE);
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
                                    transactionlist.add(item1);
                                    transactionlistTemp.add(item1);
                                    tvNoPayment.setVisibility(View.GONE);
                                }
                            }
                        }

                        if(transactionlist.size()>0){
                            rvTransPayment.setVisibility(View.VISIBLE);
                            tvNoPayment.setVisibility(View.GONE);
                        }
                        else {
                            rvTransPayment.setVisibility(View.GONE);
                            tvNoPayment.setVisibility(View.VISIBLE);
                        }
                        adapterTransPaymentList.notifyDataSetChanged();


                        if(PaymentTotltal.equals("0")){
                            TransactionDetails_Activity.tvTransCustPayment.setText("--------");
                        }
                        else {
                            TransactionDetails_Activity.tvTransCustPayment.setText(PaymentTotltal);
                        }

                    }
                    else {
                        rvTransPayment.setVisibility(View.GONE);
                        tvNoPayment.setVisibility(View.VISIBLE);
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

    private void getPaymentlist1(){
        // swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TRANSACTION_DETAILSPAYMENT + " &customer_id=" + customer_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //     swipe_refresh.setRefreshing(false);

                try {
                    transactionlist.clear();
                    transactionlistTemp.clear();
                    tvNoPayment.setVisibility(View.GONE);

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {

                        PaymentTotltal = jsonObject.getString("total_payment");

                        TransactionDetails_Activity.tvTransCustPayment.setText(PaymentTotltal);

                        JSONArray jsonArray = jsonObject.getJSONArray("transcations");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Transcation item1 = new Transcation();
                            item1.setTransactionId(object.getString("transaction_id"));
                            item1.setCustomerId(object.getString("customer_id"));
                            item1.setCustomerName(object.getString("customer_name"));
                            item1.setPaymentType(object.getString("payment_type"));
                            item1.setTransactionDate(object.getString("transaction_date"));
                            item1.setTransactionNote(object.getString("transaction_note"));
                            item1.setAmount(object.getString("amount"));

                            transactionlist.add(item1);
                            transactionlistTemp.add(item1);
                            tvNoPayment.setVisibility(View.GONE);
                        }
                        adapterTransPaymentList.notifyDataSetChanged();
                        tvNoPayment.setVisibility(View.GONE);

                    } else {
                        tvNoPayment.setVisibility(View.VISIBLE);
                        //new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   swipe_refresh.setRefreshing(false);
                Utils.showLog("==== VolleyError " + error.getMessage());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        Log.e("URL  ",stringRequest.getUrl());
        queue.add(stringRequest);


    }

    @Override
    public void onResume() {
        super.onResume();
        getPaymentlist();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

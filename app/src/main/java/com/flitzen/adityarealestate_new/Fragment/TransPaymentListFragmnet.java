package com.flitzen.adityarealestate_new.Fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flitzen.adityarealestate_new.Adapter.Adapter_Trans_PaymentList;
import com.flitzen.adityarealestate_new.Classes.Network;
import com.flitzen.adityarealestate_new.Items.Transcation;
import com.flitzen.adityarealestate_new.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressLint("ValidFragment")
public class TransPaymentListFragmnet extends Fragment {

    RecyclerView rvTransPaymentlist;
    String rent_amount = "", customer_id = "",customer_name="";
    int position;
    Activity mActivity;
    ProgressDialog prd;
    SwipeRefreshLayout swipe_refresh;

    Adapter_Trans_PaymentList adapterTransPaymentList;

    ArrayList<Transcation> transactionlist = new ArrayList<>();
    ArrayList<Transcation> transactionlistTemp = new ArrayList<>();
    @BindView(R.id.tvTranstabPayment)
    TextView tvTranstabPayment;
    @BindView(R.id.tvTranstabRecevied)
    TextView tvTranstabRecevied;
    @BindView(R.id.Trans_frame)
    FrameLayout TransFrame;
    Unbinder unbinder;

    String PaymentTotal;

    @SuppressLint("ValidFragment")
    public TransPaymentListFragmnet(String customer_id, int position) {
        this.customer_id = customer_id;
        this.position = position;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transcation_list, null);
        initUI(view);
        unbinder = ButterKnife.bind(this, view);
        return view;


    }

    private void initUI(View view) {

        mActivity = getActivity();
        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        //   rvTransPaymentlist = (RecyclerView) view.findViewById(R.id.rvTrans_Payment_list);
        //  rvTransPaymentlist.setLayoutManager(new LinearLayoutManager(mActivity));
        //  rvTransPaymentlist.setHasFixedSize(true);
        // adapterTransPaymentList = new Adapter_Trans_PaymentList(mActivity, transactionlist, false);
        //  rvTransPaymentlist.setAdapter(adapterTransPaymentList);

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Network.isNetworkAvailable(mActivity)) {
                    // getAllTransactionlist();
                } else {
                    hideSwipeRefresh();
                }
            }
        });

        tvTranstabPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvTranstabPayment.setBackground(getResources().getDrawable(R.drawable.trans_tab_bg));
                tvTranstabRecevied.setBackground(getResources().getDrawable(R.drawable.task_bg_title));
                pushFragment(new TranPayment_Fragment(customer_id,position,customer_name,""), "");
            }
        });

        tvTranstabRecevied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                tvTranstabPayment.setBackground(getResources().getDrawable(R.drawable.trans_tab_bg));
                tvTranstabRecevied.setBackground(getResources().getDrawable(R.drawable.task_bg_title));
                //    tvTitleTask.setText("Pending Task");
                pushFragment(new TranPayment_Fragment(customer_id,position,customer_name,""), "");

            }
        });


    }

    /* private void getAllTransactionlist() {


         swipe_refresh.setRefreshing(true);
         StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TRANSACTION_DETAILS +" &customer_id=" + customer_id , new com.android.volley.Response.Listener<String>() {
             @Override
             public void onResponse(String response) {

                 swipe_refresh.setRefreshing(false);

                 try {
                     transactionlist.clear();
                     transactionlistTemp.clear();


                     JSONObject jsonObject = new JSONObject(response);

                     if (jsonObject.getInt("result") == 1) {
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
                         }
                         adapterTransPaymentList.notifyDataSetChanged();

                     } else {
                         new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                 Utils.showLog("==== VolleyError "+error.getMessage());
             }
         });

         stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                 30000,
                 DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                 DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

         RequestQueue queue = Volley.newRequestQueue(mActivity);
         queue.add(stringRequest);


     }
 */
    private void hideSwipeRefresh() {
        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
            swipe_refresh.setRefreshing(false);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //  getAllTransactionlist();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }



    private boolean pushFragment(Fragment fragment, String tag) {
        if (fragment != null) {

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.Trans_frame, fragment, tag)
                    //.addToBackStack("fragment")

                    .commit();
            return true;
        }
        return false;
    }


}

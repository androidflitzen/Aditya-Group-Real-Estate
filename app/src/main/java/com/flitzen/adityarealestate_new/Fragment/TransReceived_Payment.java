package com.flitzen.adityarealestate_new.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Activity.Add_fab_Transaction_Activity;
import com.flitzen.adityarealestate_new.Activity.PdfViewActivity;
import com.flitzen.adityarealestate_new.Activity.TransactionDetails_Activity;
import com.flitzen.adityarealestate_new.Activity.ViewPdfForAll;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Trans_PaymentList;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Items.Transcation;
import com.flitzen.adityarealestate_new.PDFUtility_Transaction;
import com.flitzen.adityarealestate_new.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressLint("ValidFragment")
public class TransReceived_Payment extends Fragment {

    Adapter_Trans_PaymentList adapterTransPaymentList;


    String rent_amount = "", customer_id = "",customer_name="",phoneNo="";
    int position;

    Activity mActivity;
    ArrayList<Transcation> transactionlist = new ArrayList<>();
    ArrayList<Transcation> transactionlistTemp = new ArrayList<>();


    Unbinder unbinder;

    String file_url = "";

    RecyclerView rvTransReceived;
    @BindView(R.id.ivTransReceivedPdf)
    RelativeLayout ivTransReceivedPdf;
    @BindView(R.id.fab_addTransReceived)
    FloatingActionButton fabAddTransReceived;

    String ReceicedTotal="0" ;
    String path="";

    TextView tvNoPaymentReceived;


    @SuppressLint("ValidFragment")
    public TransReceived_Payment(String customer_id, int position,String customer_name,String phoneNo) {
        this.customer_id = customer_id;
        this.position = position;
        this.customer_name= customer_name;
        this.phoneNo= phoneNo;

    }


    public TransReceived_Payment() {
        // doesn't do anything special
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmenttrans_received, null);
        unbinder = ButterKnife.bind(this, view);
        initUI(view);

        return view;

    }

    private void initUI(View view) {

        mActivity = getActivity();

        rvTransReceived = (RecyclerView) view.findViewById(R.id.rvTransReceived);
        rvTransReceived.setLayoutManager(new LinearLayoutManager(mActivity));
        rvTransReceived.setHasFixedSize(true);
        adapterTransPaymentList = new Adapter_Trans_PaymentList(mActivity, transactionlist, false);
        rvTransReceived.setAdapter(adapterTransPaymentList);

        tvNoPaymentReceived = (TextView) view.findViewById(R.id.tvNoPaymentReceived);

        Utils.showLog("cusrt" + customer_id);


        ivTransReceivedPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int finalTotalAmount=0;
                for (int i = 0; i < transactionlist.size(); i++) {
                    if(transactionlist.get(i).getAmount()!=null && !(transactionlist.get(i).getAmount().equals(""))){
                        finalTotalAmount=finalTotalAmount+Integer.parseInt(transactionlist.get(i).getAmount());
                    }
                }

                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                path = Environment.getExternalStorageDirectory().toString() + "/" + ts + "_payment_list.pdf";
                System.out.println("========path  " + path);
                try {
                    PDFUtility_Transaction.createPdf(v.getContext(), new PDFUtility_Transaction.OnDocumentClose() {
                        @Override
                        public void onPDFDocumentClose(File file) {
                           // Toast.makeText(getActivity(), "Sample Pdf Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ViewPdfForAll.class);
                            intent.putExtra("path",path);
                            startActivity(intent);
                        }
                    }, getSampleData(), path, true, customer_name, phoneNo,finalTotalAmount);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", "Error Creating Pdf");
                    Toast.makeText(v.getContext(), "Error Creating Pdf", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Utils.showLog("==name"+customer_name);

        fabAddTransReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, Add_fab_Transaction_Activity.class);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("customer_name",customer_name);
               // intent.putExtra("type1", transactionlist.get(0).getPaymentType());
                mActivity.startActivity(intent);
            }
        });

    }

    private List<String[]> getSampleData() {

        List<String[]> temp = new ArrayList<>();
        int finalTotalAmount=0;
        for (int i = 0; i < transactionlist.size(); i++) {

            String data1="";
            String data2="";
            String data3="";
            String data4="";


            if (transactionlist.get(i).getTransactionDate() != null) {
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
                // SimpleDateFormat inputT = new SimpleDateFormat("hh:mm:ss");
                SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
                // SimpleDateFormat outputT = new SimpleDateFormat("hh:mm a");
                try {
                    Date oneWayTripDate;
                    Date oneWayTripDateT;
                    oneWayTripDate = input.parse(transactionlist.get(i).getTransactionDate());  // parse input
                    // oneWayTripDateT = inputT.parse(transactionlist.get(i).getTransactionTime());  // parse input
                    data1=(output.format(oneWayTripDate));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            if(transactionlist.get(i).getPaymentType()!=null){
                if(transactionlist.get(i).getPaymentType().equals("0")){
                    data2="Cash Received";
                }
            }

            data3=transactionlist.get(i).getTransactionNote();
            data4= Helper.getFormatPrice(Integer.parseInt(transactionlist.get(i).getAmount()));

            temp.add(new String[] {data1,data2,data3,data4});
        }
        return temp;
    }


    private void getReceivedlist() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query queryDocument = databaseReference.child("Transactions").orderByKey();
        queryDocument.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        transactionlist.clear();
                        transactionlistTemp.clear();
                        tvNoPaymentReceived.setVisibility(View.GONE);
                        int ReceicedTotal1=0;
                        for (DataSnapshot npsnapshot5 : dataSnapshot.getChildren()) {
                            if (npsnapshot5.child("customer_id").getValue().toString().equals(customer_id)) {
                                if(npsnapshot5.child("payment_type").getValue().toString().equals("0")){

                                    Transcation item1 = new Transcation();
                                    item1.setTransactionId(npsnapshot5.child("transaction_id").getValue().toString());
                                    item1.setCustomerId(npsnapshot5.child("customer_id").getValue().toString());
                                    item1.setPaymentType(npsnapshot5.child("payment_type").getValue().toString());
                                    item1.setTransactionDate(npsnapshot5.child("transaction_date").getValue().toString());
                                    item1.setTransactionNote(npsnapshot5.child("transaction_note").getValue().toString());
                                    item1.setAmount(npsnapshot5.child("amount").getValue().toString());

                                    ReceicedTotal1=ReceicedTotal1+Integer.parseInt(npsnapshot5.child("amount").getValue().toString());
                                    ReceicedTotal=String.valueOf(ReceicedTotal1);

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

                                                    Collections.sort(transactionlist, new Comparator<Transcation>() {
                                                        @Override
                                                        public int compare(Transcation o1, Transcation o2) {
                                                            return o1.getTransactionDate().compareTo(o2.getTransactionDate());
                                                        }
                                                    }.reversed());
                                                    adapterTransPaymentList.notifyDataSetChanged();
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
                                }
                                }
                        }
                        if(transactionlist.size()>0){
                            rvTransReceived.setVisibility(View.VISIBLE);
                            tvNoPaymentReceived.setVisibility(View.GONE);
                        }
                        else {
                            rvTransReceived.setVisibility(View.GONE);
                            tvNoPaymentReceived.setVisibility(View.VISIBLE);
                        }

                        Collections.sort(transactionlist, new Comparator<Transcation>() {
                            @Override
                            public int compare(Transcation o1, Transcation o2) {
                                return o1.getTransactionDate().compareTo(o2.getTransactionDate());
                            }
                        }.reversed());
                        adapterTransPaymentList.notifyDataSetChanged();
                        if(ReceicedTotal.equals("0")){
                            TransactionDetails_Activity.tvTransCustReceived.setText("--------");
                        }
                        else {
                            TransactionDetails_Activity.tvTransCustReceived.setText(ReceicedTotal);
                        }
                    }
                    else {
                        rvTransReceived.setVisibility(View.GONE);
                        tvNoPaymentReceived.setVisibility(View.VISIBLE);
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

    private void getReceivedlist1() {


        // swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TRANSACTION_DETAILSRECEIVED + " &customer_id=" + customer_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //     swipe_refresh.setRefreshing(false);

                try {
                    transactionlist.clear();
                    transactionlistTemp.clear();
                    tvNoPaymentReceived.setVisibility(View.GONE);

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

                            ReceicedTotal = jsonObject.getString("total_received");
                            TransactionDetails_Activity.tvTransCustReceived.setText(ReceicedTotal);
                            tvNoPaymentReceived.setVisibility(View.GONE);

                        }
                        adapterTransPaymentList.notifyDataSetChanged();
                        tvNoPaymentReceived.setVisibility(View.GONE);

                    } else {
                        tvNoPaymentReceived.setVisibility(View.VISIBLE);
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
        queue.add(stringRequest);


    }

    @Override
    public void onResume() {
        super.onResume();
        getReceivedlist();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

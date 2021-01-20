package com.flitzen.adityarealestate_new.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.flitzen.adityarealestate_new.Activity.PdfCreatorCashPaymentActivity;
import com.flitzen.adityarealestate_new.Activity.PdfCreatorCashReceivedActivity;
import com.flitzen.adityarealestate_new.Activity.PdfViewActivity;
import com.flitzen.adityarealestate_new.Activity.ViewPdfForAll;
import com.flitzen.adityarealestate_new.Adapter.ReceivedPaymentListAdapter;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Network;
import com.flitzen.adityarealestate_new.Items.CashPaymentDetailsForPDF;
import com.flitzen.adityarealestate_new.Items.Item_Site_Payment_List;
import com.flitzen.adityarealestate_new.Items.ReceivedPayment;
import com.flitzen.adityarealestate_new.PDFUtility_cashPayment;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CashReceiveFragment extends Fragment {
    RelativeLayout tvViewPaymentPDF;
    SwipeRefreshLayout swipeRefresh;
    RecyclerView rvCashReceive;
    Activity activity;
    ProgressDialog prd;
    String site_id = "", file_url = "",site_address="",site_size="",site_name="";
    ArrayList<ReceivedPayment> receivedPaymentList = new ArrayList<>();
    ReceivedPaymentListAdapter receivedPaymentListAdapter;
    FloatingActionButton fabAddPayment;
    TextView tvNoActiveCustomer;
    String path="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_receive, null);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        activity = getActivity();
        site_id = getArguments().getString("site_id");
        site_address = getArguments().getString("site_address");
        site_size = getArguments().getString("site_size");
        site_name = getArguments().getString("site_name");
        tvViewPaymentPDF = view.findViewById(R.id.tvViewPaymentPDF);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        rvCashReceive = view.findViewById(R.id.rvCashReceive);
        tvNoActiveCustomer = view.findViewById(R.id.tvNoActiveCustomer);

        rvCashReceive.setLayoutManager(new LinearLayoutManager(activity));
        rvCashReceive.setHasFixedSize(true);
        rvCashReceive.setNestedScrollingEnabled(false);
        fabAddPayment = view.findViewById(R.id.fabAddPayment);
        fabAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater localView = LayoutInflater.from(activity);
                View promptsView = localView.inflate(R.layout.dialog_sites_payment_add, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);

                final EditText edt_paid_amount = (EditText) promptsView.findViewById(R.id.edt_paid_amount);
                final EditText edt_remark = (EditText) promptsView.findViewById(R.id.edt_remark);
                final TextView txt_date = (TextView) promptsView.findViewById(R.id.txt_date);

                TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                Button btn_add_payment = (Button) promptsView.findViewById(R.id.btn_add_payment);

                txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
                txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

                txt_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Helper.pick_Date(activity, txt_date);
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_add_payment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (txt_date.getText().toString().equals("")) {
                            new CToast(activity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            return;
                        } else if (edt_paid_amount.getText().toString().equals("")) {
                            edt_paid_amount.setError("Enter pending amount");
                            edt_paid_amount.requestFocus();
                            return;
                        } /*else if (Integer.parseInt(edt_paid_amount.getText().toString()) > Integer.parseInt(remaining_amount)) {
                            edt_paid_amount.setError("You enter more then pending amount");
                            edt_paid_amount.requestFocus();
                            return;
                        }*/ else {
                            alertDialog.dismiss();
                            addSitePayment(site_id, txt_date.getTag().toString().trim(), edt_paid_amount.getText().toString().trim(), edt_remark.getText().toString().trim());
                        }
                    }
                });

                alertDialog.show();
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Network.isNetworkAvailable(activity)) {
                    getSitePaymentList();
                } else {
                    hideSwipeRefresh();
                }
            }
        });

        tvViewPaymentPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                path = Environment.getExternalStorageDirectory().toString() + "/" + ts + "_payment_list.pdf";
                System.out.println("========path  " + path);
                try {
                    PDFUtility_cashPayment.createPdf(v.getContext(), new PDFUtility_cashPayment.OnDocumentClose() {
                        @Override
                        public void onPDFDocumentClose(File file) {
                       //     Toast.makeText(getActivity(), "Sample Pdf Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ViewPdfForAll.class);
                            intent.putExtra("path",path);
                            startActivity(intent);
                        }
                    }, getSampleData(), path, true, site_size, site_address, site_name,0);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", "Error Creating Pdf");
                    Toast.makeText(v.getContext(), "Error Creating Pdf", Toast.LENGTH_SHORT).show();
                }

              /*  Intent intent=new Intent(getActivity(), PdfCreatorCashReceivedActivity.class);
                CashPaymentDetailsForPDF cashPaymentDetailsForPDF=new CashPaymentDetailsForPDF();
                cashPaymentDetailsForPDF.setSize(site_size);
                cashPaymentDetailsForPDF.setAddress(site_address);
                cashPaymentDetailsForPDF.setSiteName(site_name);
                intent.putExtra("CashPaymentDetails",cashPaymentDetailsForPDF);
                intent.putExtra("paymentList",receivedPaymentList);
                startActivity(intent);*/

            }
        });

        receivedPaymentListAdapter = new ReceivedPaymentListAdapter(activity, receivedPaymentList);
        rvCashReceive.setAdapter(receivedPaymentListAdapter);
       /* receivedPaymentListAdapter.OnItemLongClickListener(new ReceivedPaymentListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemClick(final int position) {
                new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
                        .setTitle(Html.fromHtml("<b> Delete </b>"))
                        .setMessage("Are you sure you want to delete this payment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                remove_Payment(receivedPaymentList.get(position).getId());
                            }
                        }).setNegativeButton("No", null).show();
            }
        });*/

        getSitePaymentList();
    }

    private List<String[]> getSampleData() {

        List<String[]> temp = new ArrayList<>();
        int finalTotalAmount=0;
        for (int i = 0; i < receivedPaymentList.size(); i++) {

            String data1="";
            String data2="";
            String data3="";
            String data4="";

            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat inputT = new SimpleDateFormat("hh:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
            SimpleDateFormat outputT = new SimpleDateFormat("hh:mm a");
            try {
                Date oneWayTripDate;
                Date oneWayTripDateT;
                oneWayTripDate = input.parse(receivedPaymentList.get(i).getPayment_date());  // parse input
                oneWayTripDateT = inputT.parse(receivedPaymentList.get(i).getPayment_time());  // parse input
                data1=output.format(oneWayTripDate) + " " + outputT.format(oneWayTripDateT);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            data2=receivedPaymentList.get(i).getRemarks();
            data3=String.valueOf(getResources().getString(R.string.rupee)+String.valueOf(Helper.getFormatPrice(Integer.parseInt(receivedPaymentList.get(i).getAmount()))));

            temp.add(new String[] {data1,data2,data3});
        }
        return temp;
    }

    public void addSitePayment(final String id, final String date, final String amount, final String remarks) {

        showPrd();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String key = rootRef.child("Site_Receive_Payment").push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("id", key);
        map.put("site_id", id);
        map.put("amount", amount);
        map.put("remarks", remarks);
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        map.put("payment_date", date + " " + currentTime);


        rootRef.child("Site_Receive_Payment").child(key).setValue(map).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hidePrd();
                new CToast(getActivity()).simpleToast("Payment added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                getSitePaymentList();
            }

        }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePrd();
                Toast.makeText(getActivity(), "Please try later...", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void addSitePayment1(final String id, final String date, final String amount, final String remarks) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.SITE_ADD_RECEIVE_PAYMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Sites Add", response);

                    if (jsonObject.getInt("result") == 1) {

                        getSitePaymentList();
                        new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("type", "Add");
                params.put("site_id", id);
                params.put("amount", amount);
                params.put("payment_date", date);
                params.put("remarks", remarks);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(stringRequest);
    }

    public void getSitePaymentList() {
        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Site_Receive_Payment").orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                swipeRefresh.setRefreshing(false);
                receivedPaymentList.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshotPay : dataSnapshot.getChildren()) {
                            if (npsnapshotPay.child("site_id").getValue().toString().equals(site_id)) {

                                ReceivedPayment receivedPayment = new ReceivedPayment();
                                receivedPayment.setId(npsnapshotPay.child("id").getValue().toString());
                                receivedPayment.setSite_id(npsnapshotPay.child("site_id").getValue().toString());
                                receivedPayment.setAmount(npsnapshotPay.child("amount").getValue().toString());
                                receivedPayment.setRemarks(npsnapshotPay.child("remarks").getValue().toString());

                                try {
                                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    Date d = f.parse(npsnapshotPay.child("payment_date").getValue().toString());
                                    DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat time = new SimpleDateFormat("hh:mm:ss");
                                    System.out.println("=====Date: " + date.format(d));
                                    System.out.println("======Time: " + time.format(d));
                                    receivedPayment.setPayment_date(date.format(d));
                                    receivedPayment.setPayment_time(time.format(d));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                receivedPaymentList.add(receivedPayment);
                            }
                        }

                        if (receivedPaymentList.size() > 0) {
                            tvViewPaymentPDF.setVisibility(View.VISIBLE);
                            tvNoActiveCustomer.setVisibility(View.GONE);
                        } else {
                            tvViewPaymentPDF.setVisibility(View.GONE);
                            tvNoActiveCustomer.setVisibility(View.VISIBLE);
                        }
                        receivedPaymentListAdapter.notifyDataSetChanged();
                    }
                    else {
                        tvViewPaymentPDF.setVisibility(View.GONE);
                        tvNoActiveCustomer.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    hidePrd();
                    Log.e("Test  ", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                new CToast(activity).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                hidePrd();
                swipeRefresh.setRefreshing(false);

            }
        });
    }

    public void getSitePaymentList1() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.SITE_RECEIVE_PAYMENT + "?site_id=" + site_id + "&type=List", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideSwipeRefresh();
                hidePrd();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("result") == 1) {
                        receivedPaymentList.clear();
                        JSONArray jsonArray = jsonObject.getJSONArray("payment");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            ReceivedPayment receivedPayment = new ReceivedPayment();
                            receivedPayment.setId(obj.getString("id"));
                            receivedPayment.setSite_id(obj.getString("site_id"));
                            receivedPayment.setPayment_date(obj.getString("payment_date"));
                            receivedPayment.setPayment_time(obj.getString("payment_time"));
                            receivedPayment.setAmount(obj.getString("amount"));
                            receivedPayment.setRemarks(obj.getString("remarks"));
                            receivedPaymentList.add(receivedPayment);
                        }
                        if (receivedPaymentList.size() > 0) {
                            tvViewPaymentPDF.setVisibility(View.VISIBLE);
                        } else {
                            tvViewPaymentPDF.setVisibility(View.GONE);
                        }
                        //receivedPaymentListAdapter.notifyDataSetChanged();
                        receivedPaymentListAdapter = new ReceivedPaymentListAdapter(activity, receivedPaymentList);
                        rvCashReceive.setAdapter(receivedPaymentListAdapter);
                        receivedPaymentListAdapter.OnItemLongClickListener(new ReceivedPaymentListAdapter.OnItemLongClickListener() {
                            @Override
                            public void onItemClick(final int position) {
                                new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
                                        .setTitle(Html.fromHtml("<b> Delete </b>"))
                                        .setMessage("Are you sure you want to delete this payment?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                remove_Payment(receivedPaymentList.get(position).getId());
                                            }
                                        }).setNegativeButton("No", null).show();
                            }
                        });
                    } else {
                        new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideSwipeRefresh();
                hidePrd();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(stringRequest);
    }

    public void showPrd() {
        prd = new ProgressDialog(activity);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        if (prd != null && prd.isShowing()) {
            prd.dismiss();
        }
    }

    private void hideSwipeRefresh() {
        if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    private void remove_Payment(final String id) {
        showPrd();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Site_Receive_Payment").orderByChild("id").equalTo(id);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                    new CToast(activity).simpleToast("Delete payment successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    getSitePaymentList();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
                Log.e("Cancel ", "onCancelled", databaseError.toException());
            }
        });
    }

    private void remove_Payment1(final String id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.DELETE_RECEIVE_SITE_PAYMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log.e("Delete payment", response);

                        hidePrd();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("result") == 1) {

                                new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                getSitePaymentList();

                            } else {
                                new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hidePrd();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("type", "Delete");
                return params;
            }

        };

        try {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(activity);
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

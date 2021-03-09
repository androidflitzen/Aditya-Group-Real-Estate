package com.flitzen.adityarealestate_new.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageView;
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

import com.flitzen.adityarealestate_new.Activity.ViewPdfForAll;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Site_Payment_List;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Network;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate_new.Items.Item_Site_Payment_List;
import com.flitzen.adityarealestate_new.PDFUtility_cashPayment;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.util.DateTime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CashPaymentFragment extends Fragment implements ActionBottomDialogFragment.ItemClickListener{
    RelativeLayout tvViewPaymentPDF;
    SwipeRefreshLayout swipeRefresh;
    RecyclerView rvCashPayment;
    FloatingActionButton fabAddPayment;
    Adapter_Site_Payment_List adapter_site_payment_list;
    ArrayList<Item_Site_Payment_List> cashPaymentList = new ArrayList<>();
    Activity activity;
    ProgressDialog prd;
    TextView tvNoActiveCustomer;
    String site_id = "", remaining_amount = "0", file_url = "", editfinaldate,site_address="",site_size="",site_name="";
    int purchase_price = 0;
    int remaining_amount1 = 0;
    String path="";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_payment, null);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        activity = getActivity();
        site_id = getArguments().getString("site_id");
        site_address = getArguments().getString("site_address");
        site_size = getArguments().getString("site_size");
        site_name = getArguments().getString("site_name");

        Utils.showLog("==siteid" + site_id);

        tvViewPaymentPDF = view.findViewById(R.id.tvViewPaymentPDF);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        rvCashPayment = view.findViewById(R.id.rvCashPayment);
        fabAddPayment = view.findViewById(R.id.fabAddPayment);
        tvNoActiveCustomer = view.findViewById(R.id.tvNoActiveCustomer);
        rvCashPayment.setLayoutManager(new LinearLayoutManager(activity));
        rvCashPayment.setHasFixedSize(true);
        rvCashPayment.setNestedScrollingEnabled(false);
        adapter_site_payment_list = new Adapter_Site_Payment_List(activity, cashPaymentList);
        rvCashPayment.setAdapter(adapter_site_payment_list);
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
                Button btn_add_share_payment = (Button) promptsView.findViewById(R.id.btn_add_share_payment);

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
                        } else if (Integer.parseInt(edt_paid_amount.getText().toString()) > Integer.parseInt(remaining_amount)) {
                            edt_paid_amount.setError("You enter more then pending amount");
                            edt_paid_amount.requestFocus();
                            return;
                        } else {
                            alertDialog.dismiss();
                            addSitePayment(site_id, txt_date.getTag().toString().trim(), edt_paid_amount.getText().toString().trim(), edt_remark.getText().toString().trim(),0);
                        }
                    }
                });

                btn_add_share_payment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (txt_date.getText().toString().equals("")) {
                            new CToast(activity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            return;
                        } else if (edt_paid_amount.getText().toString().equals("")) {
                            edt_paid_amount.setError("Enter pending amount");
                            edt_paid_amount.requestFocus();
                            return;
                        } else if (Integer.parseInt(edt_paid_amount.getText().toString()) > Integer.parseInt(remaining_amount)) {
                            edt_paid_amount.setError("You enter more then pending amount");
                            edt_paid_amount.requestFocus();
                            return;
                        } else {
                            alertDialog.dismiss();
                            addSitePayment(site_id, txt_date.getTag().toString().trim(), edt_paid_amount.getText().toString().trim(), edt_remark.getText().toString().trim(),1);
                        }
                    }
                });

                alertDialog.show();
            }
        });
        adapter_site_payment_list.OnItemLongClickListener(new Adapter_Site_Payment_List.OnItemLongClickListener() {
            @Override
            public void onItemClick(final int position) {
                opendailogedit(cashPaymentList.get(position).getPayment_date(), cashPaymentList.get(position).getAmount(), cashPaymentList.get(position).getRemarks(), cashPaymentList.get(position).getId(), true);


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

                int finalTotalAmount=0;
                for (int i = 0; i < cashPaymentList.size(); i++) {
                    if(cashPaymentList.get(i).getAmount()!=null && !(cashPaymentList.get(i).getAmount().equals(""))){
                        finalTotalAmount=finalTotalAmount+Integer.parseInt(cashPaymentList.get(i).getAmount());
                    }
                }

                try {
                    PDFUtility_cashPayment.createPdf(v.getContext(), new PDFUtility_cashPayment.OnDocumentClose() {
                        @Override
                        public void onPDFDocumentClose(File file) {
                            //Toast.makeText(getActivity(), "Sample Pdf Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ViewPdfForAll.class);
                            intent.putExtra("path",path);
                            startActivity(intent);
                        }
                    }, getSampleData(), path, true, site_size, site_address, site_name,finalTotalAmount);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", "Error Creating Pdf");
                    Toast.makeText(v.getContext(), "Error Creating Pdf", Toast.LENGTH_SHORT).show();
                }

               /* Intent intent=new Intent(getActivity(), PdfCreatorCashPaymentActivity.class);
                CashPaymentDetailsForPDF cashPaymentDetailsForPDF=new CashPaymentDetailsForPDF();
                cashPaymentDetailsForPDF.setSize(site_size);
                cashPaymentDetailsForPDF.setAddress(site_address);
                cashPaymentDetailsForPDF.setSiteName(site_name);
                intent.putExtra("CashPaymentDetails",cashPaymentDetailsForPDF);
                intent.putExtra("paymentList",cashPaymentList);
                startActivity(intent);*/
            }
        });
        // getSitePaymentList();
    }

    private List<String[]> getSampleData() {

        List<String[]> temp = new ArrayList<>();
        int finalTotalAmount=0;
        for (int i = 0; i < cashPaymentList.size(); i++) {

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
                oneWayTripDate = input.parse(cashPaymentList.get(i).getPayment_date());  // parse input
                oneWayTripDateT = inputT.parse(cashPaymentList.get(i).getPayment_time());  // parse input
                data1=output.format(oneWayTripDate) + " " + outputT.format(oneWayTripDateT);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            data2=cashPaymentList.get(i).getRemarks();
            data3=getResources().getString(R.string.rupee)+Helper.getFormatPrice(Integer.parseInt(cashPaymentList.get(i).getAmount()));

            temp.add(new String[] {data1,data2,data3});
        }
        return temp;
    }


    private void opendailogedit(final String edite_date, String edit_amount, String edit_remark, final String edit_id, final boolean isedit) {

        LayoutInflater localView = LayoutInflater.from(getActivity());
        View promptsView = localView.inflate(R.layout.dailog_site_payment_edit, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(false);

        final EditText edt_paid_amount = (EditText) promptsView.findViewById(R.id.edt_paid_amount);
        final EditText edt_remark = (EditText) promptsView.findViewById(R.id.edt_remark);
        final TextView txt_date = (TextView) promptsView.findViewById(R.id.txt_date);

        ImageView btn_cancel = (ImageView) promptsView.findViewById(R.id.btn_cancel);
        Button btn_add_payment = (Button) promptsView.findViewById(R.id.btn_add_payment);
        Button btn_delete = (Button) promptsView.findViewById(R.id.btn_delete);
        txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
        txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.AppCompatAlertDialogStyle)
                        .setTitle(Html.fromHtml("<b> Delete </b>"))
                        .setMessage("Are you sure you want to delete this payment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                remove_Payment(edit_id);
                                //getSitePaymentList();
                                alertDialog.dismiss();
                            }
                        }).setNegativeButton("No", null).show();
            }
        });

        edt_paid_amount.setText(edit_amount);
        edt_remark.setText(edit_remark);
        editfinaldate = edite_date;
        if (isedit) {
            txt_date.setText(edite_date);
        } else {
            Helper.pick_Date((Activity) getActivity(), txt_date);
        }
        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.pick_Date((Activity) getActivity(), txt_date);
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
                    new CToast(getActivity()).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else if (edt_paid_amount.getText().toString().equals("")) {
                    edt_paid_amount.setError("Enter pending amount");
                    edt_paid_amount.requestFocus();
                    return;
                } else {
                    alertDialog.dismiss();
                    editfinaldate = txt_date.getText().toString();
                    //addSitePayment(edit_id, editfinaldate, edt_paid_amount.getText().toString().trim(), edt_remark.getText().toString().trim());
                    Log.w("Response Sites edit", API.SITE_PAYMENT);

                    showPrd();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    Query query = databaseReference.child("SitePayments").orderByKey();
                    databaseReference.keepSynced(true);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot npsnapshot) {
                            hidePrd();
                            try {
                                if (npsnapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                                        if (dataSnapshot.child("id").getValue().toString().equals(edit_id)) {

                                            DatabaseReference cineIndustryRef = databaseReference.child("SitePayments").child(dataSnapshot.getKey());
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("amount", edt_paid_amount.getText().toString().trim());
                                            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                                            String datePattern = "\\d{4}-\\d{2}-\\d{2}";
                                            boolean isDate1 = editfinaldate.matches(datePattern);

                                            if(isDate1==true){
                                                map.put("payment_date", editfinaldate+" "+currentTime);
                                            }
                                            else {
                                                DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                                                Date d = f.parse(editfinaldate);
                                                DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                                editfinaldate= date.format(d);
                                                map.put("payment_date", editfinaldate+" "+currentTime);
                                            }


                                            map.put("remarks", edt_remark.getText().toString().trim());

                                            Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    hidePrd();
                                                    new CToast(getActivity()).simpleToast("Payment updated successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                    getSitePaymentList();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    hidePrd();
                                                    new CToast(getActivity()).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
            }
        });

        alertDialog.show();

    }

    private void remove_Payment(final String id) {

       // showPrd();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("SitePayments").orderByChild("id").equalTo(id);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // hidePrd();
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                    new CToast(activity).simpleToast("Delete payment successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                }
                getSitePaymentList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               // hidePrd();
                Log.e("Cancel ", "onCancelled", databaseError.toException());
            }
        });
    }

    private void remove_Payment1(final String id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.DELETE_SITE_PAYMENT,
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

    private void hideSwipeRefresh() {
        if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getSitePaymentList();
    }

    public void getSitePaymentList() {

        showPrd();
        purchase_price = 0;
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
        Query querySite = databaseReference1.child("Sites").orderByKey();
        querySite.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipeRefresh.setRefreshing(false);
                hidePrd();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshotSite : dataSnapshot.getChildren()) {
                            if (npsnapshotSite.child("id").getValue().toString().equals(site_id)) {
                                purchase_price = Integer.parseInt(npsnapshotSite.child("purchase_price").getValue().toString());

                                Query query = databaseReference1.child("SitePayments").orderByKey();
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        hidePrd();
                                        cashPaymentList.clear();
                                        try {
                                            if (dataSnapshot.exists()) {
                                                int total_payments = 0;
                                                remaining_amount1 = 0;
                                                for (DataSnapshot npsnapshotPay : dataSnapshot.getChildren()) {
                                                    if (npsnapshotPay.child("site_id").getValue().toString().equals(site_id)) {

                                                        Item_Site_Payment_List item = new Item_Site_Payment_List();
                                                        item.setId(npsnapshotPay.child("id").getValue().toString());
                                                        item.setSite_id(npsnapshotPay.child("site_id").getValue().toString());
                                                        item.setAmount(npsnapshotPay.child("amount").getValue().toString());
                                                        item.setRemarks(npsnapshotPay.child("remarks").getValue().toString());

                                                        total_payments = total_payments + Integer.parseInt(npsnapshotPay.child("amount").getValue().toString());

                                                        try {
                                                            DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                            Date d = f.parse(npsnapshotPay.child("payment_date").getValue().toString());
                                                            DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                                            DateFormat time = new SimpleDateFormat("hh:mm:ss");
                                                            System.out.println("=====Date: " + date.format(d));
                                                            System.out.println("======Time: " + time.format(d));
                                                            item.setPayment_date(date.format(d));
                                                            item.setPayment_time(time.format(d));
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        cashPaymentList.add(item);
                                                    }
                                                }

                                                remaining_amount1 = purchase_price - total_payments;
                                                remaining_amount = String.valueOf(remaining_amount1);

                                                if (cashPaymentList.size() > 0) {
                                                    tvViewPaymentPDF.setVisibility(View.VISIBLE);
                                                    tvNoActiveCustomer.setVisibility(View.GONE);
                                                } else {
                                                    tvViewPaymentPDF.setVisibility(View.GONE);
                                                    tvNoActiveCustomer.setVisibility(View.VISIBLE);
                                                }

                                                Collections.sort(cashPaymentList, new Comparator<Item_Site_Payment_List>() {
                                                    @Override
                                                    public int compare(Item_Site_Payment_List o1, Item_Site_Payment_List o2) {
                                                        return o1.getPayment_date().compareTo(o2.getPayment_date());
                                                    }
                                                }.reversed());

                                                adapter_site_payment_list.notifyDataSetChanged();

                                            }
                                            else {
                                                remaining_amount=String.valueOf(purchase_price);
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
                                        new CToast(getActivity()).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                        hidePrd();

                                    }
                                });

                            }
                        }
                        if (cashPaymentList.size() > 0) {
                            tvViewPaymentPDF.setVisibility(View.VISIBLE);
                        } else {
                            tvViewPaymentPDF.setVisibility(View.GONE);
                        }

                        Collections.sort(cashPaymentList, new Comparator<Item_Site_Payment_List>() {
                            @Override
                            public int compare(Item_Site_Payment_List o1, Item_Site_Payment_List o2) {
                                return o1.getPayment_date().compareTo(o2.getPayment_date());
                            }
                        }.reversed());

                        adapter_site_payment_list.notifyDataSetChanged();

                    }
                } catch (Exception e) {
                    Log.e("Test  ", e.getMessage());
                    e.printStackTrace();
                    hidePrd();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                new CToast(getActivity()).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                hidePrd();
                swipeRefresh.setRefreshing(false);

            }
        });
    }

    public void getSitePaymentList1() {
        // showPrd();
        //swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.SITE_PAYMENT + "?type=List&site_id=" + site_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideSwipeRefresh();
                //hidePrd();
                //swipe_refresh.setRefreshing(false);
                try {
                    cashPaymentList.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Sites List", response);
                    if (jsonObject.getInt("result") == 1) {

                        /*txt_site_name.setText(jsonObject.getString("site_name"));
                        txt_site_address.setText("(" + jsonObject.getString("site_address") + ")" + "(" + jsonObject.getString("size") + "Sq yard)");
                        txt_site_address.setTag(jsonObject.getString("site_address"));

                        txt_purchase_price.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(jsonObject.getString("purchase_price"))));
                        txt_purchase_price.setTag(jsonObject.getString("purchase_price"));
                        txt_size.setText(jsonObject.getString("size"));

                        if (!jsonObject.getString("total_payments").equals("0"))
                            txt_total_paid.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(jsonObject.getString("total_payments"))));
                        else
                            txt_total_paid.setText("-");

                        if (!jsonObject.getString("remaining_amount").equals("0")) {
                            txt_pending_amount.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(jsonObject.getString("remaining_amount"))));
                            txt_pending_amount.setTag(jsonObject.getString("remaining_amount"));
                        } else
                            txt_pending_amount.setText("-");

                        if (!jsonObject.getString("total_received").equals("0"))
                            txt_income_cust.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(jsonObject.getString("total_received"))));
                        else
                            txt_income_cust.setText("-");

                        if (jsonObject.getString("file").equals("")) {
                            linAddFileView.setVisibility(View.VISIBLE);
                        } else {
                            isFileAttached = true;
                            file_url = jsonObject.getString("file");
                            Log.d("TAG", "=== file_url " + file_url);
                            linPdfView.setVisibility(View.VISIBLE);
                        }*/

                        remaining_amount = jsonObject.getString("remaining_amount");
                        JSONArray jsonArray = jsonObject.getJSONArray("payment");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Site_Payment_List item = new Item_Site_Payment_List();
                            item.setId(object.getString("id"));
                            item.setSite_id(object.getString("site_id"));
                            item.setAmount(object.getString("amount"));
                            item.setRemarks(object.getString("remarks"));
                            item.setPayment_date(object.getString("payment_date"));
                            item.setPayment_time(object.getString("payment_time"));

                            cashPaymentList.add(item);
                        }
                        if (cashPaymentList.size() > 0) {
                            tvViewPaymentPDF.setVisibility(View.VISIBLE);
                        } else {
                            tvViewPaymentPDF.setVisibility(View.GONE);
                        }
                        adapter_site_payment_list.notifyDataSetChanged();

                    } else {
                        new CToast(activity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideSwipeRefresh();
                //hidePrd();
                //swipe_refresh.setRefreshing(false);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(activity);
        queue.add(stringRequest);
    }

    public void addSitePayment(final String id, final String date, final String amount, final String remarks,int checkButton) {
        showPrd();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String key = rootRef.child("SitePayments").push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("site_id", site_id);
        map.put("amount", amount);
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        map.put("payment_date", date + " " + currentTime);
        map.put("remarks", remarks);
        map.put("id", key);

        rootRef.child("SitePayments").child(key).setValue(map).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hidePrd();
                new CToast(getActivity()).simpleToast("Payment added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                if(checkButton==1){
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

    private int appInstalledOrNot() {
        String pkgW = "com.whatsapp";
        String pkgWB = "com.whatsapp.w4b";
        PackageManager pm = getContext().getPackageManager();
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

    private void sendMessage(String amount, String pkg) {
        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("text/plain");
        String text="";
        text = "Dear  'customer"+"'\n"+"Your payment has been credited "+getResources().getString(R.string.rupee)+amount+" to "+getResources().getString(R.string.app_name)+".\n"+"\nThanks";

        if (pkg.equalsIgnoreCase("both")) {
            showBottomSheet(text);
        } else {
            waIntent.setPackage(pkg);
            if (waIntent != null) {
                waIntent.putExtra(Intent.EXTRA_TEXT, text);//
                startActivity(Intent.createChooser(waIntent, text));
            } else {
                Toast.makeText(getActivity(), "WhatsApp not found", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void showBottomSheet(String text) {
        ActionBottomDialogFragment addPhotoBottomDialogFragment =
                new ActionBottomDialogFragment(text);
        addPhotoBottomDialogFragment.show(getFragmentManager(),
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
                    Toast.makeText(getActivity(), "WhatsApp not found", Toast.LENGTH_SHORT)
                            .show();
                }
            }else {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "WhatsApp not found", Toast.LENGTH_SHORT)
                            .show();
                }
            }else {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addSitePayment1(final String id, final String date, final String amount, final String remarks) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.SITE_PAYMENT, new Response.Listener<String>() {
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

    public void showPrd() {
        prd = new ProgressDialog(activity);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }
}

package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Plot_List;
import com.flitzen.adityarealestate_new.Adapter.Spn_Adapter;
import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Apiutils;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.WebAPI;
import com.flitzen.adityarealestate_new.CommonModel;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.Items.Item_Plot_List;
import com.flitzen.adityarealestate_new.Items.Item_Sites_List;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

import static com.flitzen.adityarealestate_new.Aditya.itemListPlot;

public class Activity_Plots_List extends AppCompatActivity {

    Activity mActivity;
    ProgressDialog prd;

    //String id = "", name = "";
    TextView txt_no_data, txt_title, tvAddNewCustomer;
    FloatingActionButton fab_add_plot;
    View view_site_summary, view_add_plot;
    SwipeRefreshLayout swipe_refresh;
    RecyclerView rec_plot_list;
    Adapter_Plot_List adapter_plot_list;
    //ArrayList<Item_Plot_List> itemListPlot = new ArrayList<>();
    ArrayList<Item_Plot_List> plotList = new ArrayList<>();
    EditText edt_add_customer_name, edt_add_mobile, edt_add_Anothermobile, edt_add_email, edt_add_address, edt_add_city;
    Button btn_add_Newcstmr_cancel, btn_add_Newcstmr_add;
    int pending_plote;
    String cust_id = "";
    List<Item_Customer_List> itemListCustomer = new ArrayList<>();
    List<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();

    String type = "", id = "", name = "", mobile = "", mobile1 = "", emial = "", address = "", city = "",size="",active_deActive="";
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.ivCancel)
    ImageView ivCancel;

    TextView tvtitle;
    ImageView ivEdit1;
    TextView txt_size;
    int siteSize=0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plots_list);
        ButterKnife.bind(this);


        type = getIntent().getStringExtra("TYPE");
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        size = getIntent().getStringExtra("size");
        active_deActive = getIntent().getStringExtra("ACTIVE_DEACTIVE");

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edt_add_customer_name = (EditText) findViewById(R.id.edt_add_customer_name);
        edt_add_mobile = (EditText) findViewById(R.id.edt_add_mobile);
        edt_add_email = (EditText) findViewById(R.id.edt_add_email);
        edt_add_address = (EditText) findViewById(R.id.edt_add_address);
        edt_add_Anothermobile = (EditText) findViewById(R.id.edt_add_Anthormobile);
        edt_add_city = (EditText) findViewById(R.id.edt_add_city);

        txt_size = (TextView) findViewById(R.id.txt_size);

        btn_add_Newcstmr_cancel = (Button) findViewById(R.id.btn_add_Newcstmr_cancel);
        btn_add_Newcstmr_add = (Button) findViewById(R.id.btn_add_Newcstmr_add);

        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");

//        getSupportActionBar().setTitle(Html.fromHtml("PLOT LIST OF " + name.toUpperCase()));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvtitle = (TextView) findViewById(R.id.tvtitle);
        ivEdit1 = (ImageView) findViewById(R.id.ivEdit1);

        tvtitle.setText("PLOT LIST OF " + name.toUpperCase());
        ivEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mActivity = Activity_Plots_List.this;
        txt_no_data = (TextView) findViewById(R.id.txt_no_data);
        txt_title = (TextView) findViewById(R.id.txt_title);
        fab_add_plot = (FloatingActionButton) findViewById(R.id.fab_add_plot);
        view_site_summary = findViewById(R.id.view_site_summary);
        view_add_plot = findViewById(R.id.view_add_plot);

        txt_title.setText("PLOT LIST OF " + name.toUpperCase());

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPlotList();
            }
        });


        rec_plot_list = (RecyclerView) findViewById(R.id.rec_plot_list);
        rec_plot_list.setLayoutManager(new GridLayoutManager(mActivity, 3));
        rec_plot_list.setHasFixedSize(true);
        adapter_plot_list = new Adapter_Plot_List(mActivity, itemListPlot);
        rec_plot_list.setAdapter(adapter_plot_list);

        getPlotList();

//        txt_size.setText(String.valueOf(pending_plote));

        view_add_plot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*if (spn_sites.getText().toString().equals("Select Site")) {
                    new CToast(mActivity).simpleToast("Please Select Site", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                } else {*/

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_plot_add, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);

                final TextView txt_title = (TextView) promptsView.findViewById(R.id.txt_title);
                final EditText edt_plot_no = (EditText) promptsView.findViewById(R.id.edt_plot_no);
                final EditText edt_plot_size = (EditText) promptsView.findViewById(R.id.edt_plot_size);

                TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                Button btn_add_site = (Button) promptsView.findViewById(R.id.btn_add_plot);

                txt_title.setText("Add plot for " + name);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_add_site.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (edt_plot_no.getText().toString().trim().equals("")) {
                            edt_plot_no.setError("Enter Plot No");
                            edt_plot_no.requestFocus();
                            return;
                        } else if (edt_plot_size.getText().toString().trim().equals("")) {
                            edt_plot_size.setError("Enter Plot Size");
                            edt_plot_size.requestFocus();
                            return;
                        } else {
                            try {
                                int plotSize = Integer.parseInt(edt_plot_size.getText().toString());
                                if (plotSize <= pending_plote) {
                                    alertDialog.dismiss();
                                    addPlot(edt_plot_no.getText().toString().trim(), edt_plot_size.getText().toString().trim());
                                } else {
                                    new CToast(mActivity).simpleToast(" " + pending_plote + " Sq. Yard plot is available.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });

                alertDialog.show();
                //}
            }
        });

        view_site_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, Activity_Sites_Purchase_Summary.class);
                intent.putExtra("id", id);
                intent.putExtra("name", name);
                intent.putExtra("address", address);
                intent.putExtra("size", size);
                startActivity(intent);

             //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });

        adapter_plot_list.setOnItemClickListener(new Adapter_Plot_List.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                if (itemListPlot.get(position).getIs_assign().equals("1")) {

                    //dialogPlotInfo(position);

                    Aditya.ID = itemListPlot.get(position).getId();
                    Intent intent = new Intent(mActivity, Activity_Plot_Details.class);
                    intent.putExtra("ACTIVE_DEACTIVE", active_deActive);
                    //intent.putExtra("POS", position);
                    startActivityForResult(intent, 001);
                    overridePendingTransition(0, 0);
                    //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

                } else {
                    dialogPlotAssign(position);
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etSearch.getText().toString().isEmpty()) {
                    itemListPlot.clear();
                    itemListPlot.addAll(plotList);
                    adapter_plot_list.notifyDataSetChanged();
                    ivCancel.setVisibility(View.GONE);
                } else {
                    ivCancel.setVisibility(View.VISIBLE);
                    itemListPlot.clear();
                    for (int j = 0; j < plotList.size(); j++) {
                        if (plotList.get(j).getPlot_no().toLowerCase().contains(etSearch.getText().toString()) || plotList.get(j).getCustomer_name().toLowerCase().contains(etSearch.getText().toString()) || plotList.get(j).getPlot_size().toLowerCase().contains(etSearch.getText().toString())) {
                            itemListPlot.add(plotList.get(j));
                        }
                    }
                    adapter_plot_list.notifyDataSetChanged();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etSearch.setText("");
                itemListPlot.clear();
                itemListPlot.addAll(plotList);
                adapter_plot_list.notifyDataSetChanged();
                ivCancel.setVisibility(View.GONE);
            }
        });
        // getPlotList();
    }

    private void addnewCustomer(final String cstmrname, final String mobile, final String Anothermobile, final String Email, final String Adress, final String CstmrCity, final AlertDialog alertDialog, TextView textView) {

        String URL = "";

        if (type.equals("EDIT")) {
            URL = API.EDIT_CUSTOMER;
        } else {
            URL = API.CUSTOMER;
        }

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Customer Add", response);

                    if (jsonObject.getInt("result") == 1) {

                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                        //finish();
                        overridePendingTransition(0, 0);
                        alertDialog.dismiss();
                        //customerDialog(textView);
                        getCustomerList(textView, "");

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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

                if (type.equals("EDIT")) {
                    params.put("customer_id", id);
                } else {
                    params.put("type", "Add");
                }

                params.put("name", cstmrname);
                params.put("city", CstmrCity);
                params.put("contact_no", mobile);
                params.put("contact_no1", Anothermobile);
                params.put("email", Email);
                params.put("address", Adress);

                return params;

               /* edt_add_customer_name.setText(getIntent().getStringExtra("NAME"));
                edt_add_mobile.setText(getIntent().getStringExtra("MOBILE"));
                edt_add_Anthormobile.setText(getIntent().getStringExtra("MOBILE1"));
                edt_add_email.setText(getIntent().getStringExtra("EMAIL"));
                edt_add_address.setText(getIntent().getStringExtra("ADDRESS"));
                edt_add_city.setText(getIntent().getStringExtra("CITY"));*/
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 001) {
                getPlotList();
            }
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (etSearch.getText().toString().isEmpty()) {
//            getPlotList();
//        }
//
//    }

    public void getPlotList() {


        siteSize=0;

        swipe_refresh.setRefreshing(true);

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
        Query querySite = databaseReference1.child("Sites").orderByKey();
        //databaseReference.keepSynced(true);
        querySite.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipe_refresh.setRefreshing(false);
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if(npsnapshot.child("id").getValue().toString().equals(id)){
                                if(!(npsnapshot.child("size").getValue().toString().equals(""))){
                                    siteSize=Integer.parseInt(npsnapshot.child("size").getValue().toString());
                                }
                            }
                            // }
                        }

                    }
                } catch (Exception e) {
                    Log.e("Test  ", e.getMessage());
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

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Plots").orderByKey();
        //databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipe_refresh.setRefreshing(false);
                itemListPlot.clear();
                plotList.clear();
                try {
                    if (dataSnapshot.exists()) {
                        int plot=0;
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if(npsnapshot.child("site_id").getValue().toString().equals(id)){
                                Item_Plot_List item = new Item_Plot_List();
                                item.setKey(npsnapshot.getKey());
                                item.setId(npsnapshot.child("id").getValue().toString());
                                item.setPlot_no(npsnapshot.child("plot_no").getValue().toString());
                                item.setSite_id(npsnapshot.child("site_id").getValue().toString());
                                item.setPlot_size(npsnapshot.child("size").getValue().toString());
                                item.setCustomer_id(npsnapshot.child("customer_id").getValue().toString());
                                String customerID=npsnapshot.child("customer_id").getValue().toString();
                                item.setPurchase_price(npsnapshot.child("purchase_price").getValue().toString());
                                item.setDate_of_purchase(npsnapshot.child("date_of_purchase").getValue().toString());

                                plot= plot+Integer.parseInt(npsnapshot.child("size").getValue().toString());

                                if(npsnapshot.child("customer_id").getValue().toString().equals("0")){
                                    item.setIs_assign("0");
                                }
                                else {
                                    item.setIs_assign("1");
                                }

                                Query query = databaseReference.child("Customers").orderByKey();
                                // databaseReference.keepSynced(true);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                    if(customerID.equals("0")){
                                                        item.setCustomer_name("");
                                                    }
                                                    else {
                                                         if (npsnapshot1.child("id").getValue().toString().equals(customerID)) {
                                                        String name = npsnapshot1.child("name").getValue().toString();
                                                        item.setCustomer_name(name);
                                                    }
                                                    }

                                                }
                                            }
                                        } catch (Exception e) {
                                            swipe_refresh.setRefreshing(false);
                                            Log.e("Ex   ", e.toString());
                                        }
                                        if (itemListPlot.size() > 0) {
                                            txt_no_data.setVisibility(View.GONE);
                                            rec_plot_list.setVisibility(View.VISIBLE);
                                            adapter_plot_list.notifyDataSetChanged();
                                        } else {
                                            txt_no_data.setVisibility(View.VISIBLE);
                                            rec_plot_list.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        swipe_refresh.setRefreshing(false);
                                        Log.e("error ", error.getMessage());
                                    }
                                });

                                Query queryPayment = databaseReference.child("Payments").orderByKey();
                                // databaseReference.keepSynced(true);
                                queryPayment.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.exists()) {
                                                int payeeAmount=0;
                                                for (DataSnapshot npsnapshot2 : dataSnapshot.getChildren()) {
                                                    if (npsnapshot2.child("plot_id").getValue().toString().equals(npsnapshot.child("id").getValue().toString())) {

                                                        String amount = npsnapshot2.child("amount").getValue().toString();
                                                        payeeAmount= payeeAmount+Integer.parseInt(amount);
                                                    }
                                                }
                                                int purchasePrice=Integer.parseInt(npsnapshot.child("purchase_price").getValue().toString());
                                                int pendingAmount=purchasePrice-payeeAmount;
                                                item.setPending_amount(String.valueOf(pendingAmount));
                                            }
                                        } catch (Exception e) {
                                            swipe_refresh.setRefreshing(false);
                                            Log.e("Ex   ", e.toString());
                                        }
                                        if (itemListPlot.size() > 0) {
                                            txt_no_data.setVisibility(View.GONE);
                                            rec_plot_list.setVisibility(View.VISIBLE);
                                            adapter_plot_list.notifyDataSetChanged();
                                        } else {
                                            txt_no_data.setVisibility(View.VISIBLE);
                                            rec_plot_list.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        swipe_refresh.setRefreshing(false);
                                        Log.e("error ", error.getMessage());
                                    }
                                });

                                itemListPlot.add(item);
                                plotList.add(item);

                            }
                        }

                        pending_plote=siteSize-plot;

                        if (itemListPlot.size() > 0) {
                            txt_no_data.setVisibility(View.GONE);
                            rec_plot_list.setVisibility(View.VISIBLE);
                            adapter_plot_list.notifyDataSetChanged();
                        } else {
                            txt_no_data.setVisibility(View.VISIBLE);
                            rec_plot_list.setVisibility(View.GONE);
                        }                    }
                } catch (Exception e) {
                    swipe_refresh.setRefreshing(false);
                    Log.e("Test  ", e.getMessage());
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

    public void getPlotList1() {

        swipe_refresh.setRefreshing(true);
        Log.w("url", API.PLOTS + "?type=List&site_id=" + id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.PLOTS + "?type=List&site_id=" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                swipe_refresh.setRefreshing(false);

                try {
                    itemListPlot.clear();
                    plotList.clear();
                    JSONObject jsonObject = new JSONObject(response);
                     Log.e("Response Plot List", response);

                    pending_plote = jsonObject.getInt("pending_plote");
                    txt_size.setText(String.valueOf(jsonObject.getInt("pending_plote")));

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("plots_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Plot_List item = new Item_Plot_List();
                            item.setId(object.getString("id"));
                            item.setPlot_no(object.getString("plot_no"));
                            item.setPlot_size(object.getString("size"));
                            item.setSite_id(object.getString("site_id"));
                            item.setCustomer_id(object.getString("customer_id"));
                            item.setCustomer_name(object.getString("customer_name"));
                            item.setPurchase_price(object.getString("purchase_price"));
                            item.setDate_of_purchase(object.getString("date_of_purchase"));
                            item.setPending_amount(object.getString("pending_amount"));
                            item.setIs_assign(object.getString("is_assign"));
//                            pending_plote = jsonObject.getInt("pending_plote");
//                            txt_size.setText(String.valueOf(jsonObject.getInt("pending_plote")));
                            itemListPlot.add(item);
                            plotList.add(item);
                        }

                        if (itemListPlot.size() > 0) {
                            txt_no_data.setVisibility(View.GONE);
                            rec_plot_list.setVisibility(View.VISIBLE);
                            adapter_plot_list.notifyDataSetChanged();
                        } else {
                            txt_no_data.setVisibility(View.VISIBLE);
                            rec_plot_list.setVisibility(View.GONE);
                        }

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
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void dialogPlotInfo(final int position) {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_plot_info, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        TextView txt_plot_no = (TextView) promptsView.findViewById(R.id.txt_plot_no);
        TextView txt_purchased_by = (TextView) promptsView.findViewById(R.id.txt_purchased_by);
        TextView txt_purchased_date = (TextView) promptsView.findViewById(R.id.txt_purchased_date);
        TextView txt_purchased_price = (TextView) promptsView.findViewById(R.id.txt_purchased_price);
        TextView txt_pending_amount = (TextView) promptsView.findViewById(R.id.txt_pending_amount);

        View view_add_payment = promptsView.findViewById(R.id.view_add_payment);
        View view_change_customer = promptsView.findViewById(R.id.view_change_customer);
        View view_remove_customer = promptsView.findViewById(R.id.view_remove_customer);

        txt_plot_no.setText("Plot No : " + itemListPlot.get(position).getPlot_no());
        txt_purchased_by.setText(itemListPlot.get(position).getCustomer_name());

        String[] date = itemListPlot.get(position).getDate_of_purchase().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
        txt_purchased_date.setText(date[0] + " " + mm + " " + date[2]);

        txt_purchased_price.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(itemListPlot.get(position).getPurchase_price())));

        if (!itemListPlot.get(position).getPending_amount().equals("0")) {
            txt_pending_amount.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(itemListPlot.get(position).getPending_amount())));
            view_add_payment.setVisibility(View.VISIBLE);
        } else {
            txt_pending_amount.setText(" - ");
            view_add_payment.setVisibility(View.GONE);
        }

        view_add_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

                LayoutInflater inflater = LayoutInflater.from(mActivity);
                View view1 = inflater.inflate(R.layout.dialog_payment_add, null);

                androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(mActivity);
                builder1.setView(view1);
                final androidx.appcompat.app.AlertDialog dialog = builder1.create();

                final EditText edt_paid_amount = (EditText) view1.findViewById(R.id.edt_paid_amount);
                final EditText edt_remark = (EditText) view1.findViewById(R.id.edt_remark);
                final TextView txt_date = (TextView) view1.findViewById(R.id.txt_date);
                final TextView txt_time = (TextView) view1.findViewById(R.id.txt_time);

                TextView btn_cancel = (TextView) view1.findViewById(R.id.btn_cancel);
                Button btn_add_payment = (Button) view1.findViewById(R.id.btn_add_payment);

                edt_paid_amount.setText(itemListPlot.get(position).getPending_amount());

                txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
                txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));
                ImageView img_close = (ImageView) view1.findViewById(R.id.img_close);
                img_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                txt_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Helper.pick_Date(mActivity, txt_date);
                    }
                });

                txt_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Calendar mcurrentTime = Calendar.getInstance();
                        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                                String hours = String.valueOf(selectedHour);
                                String min = String.valueOf(selectedMinute);

                                if (hours.length() == 1) {
                                    hours = "0" + hours;
                                }
                                if (min.length() == 1) {
                                    min = "0" + min;
                                    System.out.println("========min    "+min);
                                }

                                txt_time.setText(hours + ":" + min + ":00");
                                System.out.println("========min    "+min);
                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    }
                });

                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                txt_time.setText(currentTime);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btn_add_payment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (edt_paid_amount.getText().toString().equals("")) {
                            edt_paid_amount.setError("Enter pending amount");
                            edt_paid_amount.requestFocus();
                            return;
                        } else if (txt_date.getText().toString().equals("")) {
                            new CToast(mActivity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            return;
                        } else if (txt_time.getText().toString().equals("")) {
                            new CToast(mActivity).simpleToast("Select Time", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            return;
                        } else if (Integer.parseInt(edt_paid_amount.getText().toString()) > Integer.parseInt(itemListPlot.get(position).getPending_amount())) {
                            edt_paid_amount.setError("You enter more then pending amount");
                            edt_paid_amount.requestFocus();
                            return;
                        } else {
                            dialog.dismiss();
                            addPaymentforPlot(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString(), itemListPlot.get(position).getId(), itemListPlot.get(position).getCustomer_id(), edt_remark.getText().toString());
                        }
                    }
                });

                dialog.show();
            }
        });

        view_change_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_plot_change_customer, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);

                final TextView txt_plot_no = (TextView) promptsView.findViewById(R.id.txt_plot_no);
                final TextView txt_old_cusomer = (TextView) promptsView.findViewById(R.id.txt_old_cusomer);
                final TextView spn_customer = (TextView) promptsView.findViewById(R.id.spn_customer);

                TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                Button btn_change = (Button) promptsView.findViewById(R.id.btn_change_customer);

                txt_plot_no.setText(itemListPlot.get(position).getPlot_no());
                txt_old_cusomer.setText(itemListPlot.get(position).getCustomer_name());
                txt_old_cusomer.setTag(itemListPlot.get(position).getCustomer_id());

                spn_customer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getCustomerList(spn_customer, itemListPlot.get(position).getCustomer_id());

                        /*if (itemListCustomer.size() == 0) {
                            getCustomerList(spn_customer, itemListPlot.get(position).getCustomer_id());
                        } else {
                            customerDialog(spn_customer);
                        }*/
                    }
                });

                try {
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


                btn_change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (spn_customer.getTag().toString().trim().equals("")) {
                            new CToast(mActivity).simpleToast("Select Customer", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            return;
                        } else {
                            alertDialog.dismiss();
                            changeCustomer(txt_old_cusomer.getTag().toString(), spn_customer.getTag().toString(), itemListPlot.get(position).getId());
                        }
                    }
                });

                alertDialog.show();
            }
        });

        view_remove_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                        .setTitle("Remove Customer")
                        .setMessage("Are you sure you want to remove this customer")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                                removeCustomer(itemListPlot.get(position).getId(), itemListPlot.get(position).getCustomer_id());

                            }
                        }).setNegativeButton("NO", null).show();

            }
        });

        alertDialog.show();

    }

    public void dialogPlotAssign(final int position) {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_plot_assign_customer, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);

        final TextView txt_plot_no = (TextView) promptsView.findViewById(R.id.txt_plot_no);
        final TextView spn_customer = (TextView) promptsView.findViewById(R.id.spn_customer);
        final TextView txt_date = (TextView) promptsView.findViewById(R.id.txt_date);
        final EditText edt_sell_price = (EditText) promptsView.findViewById(R.id.edt_sell_price);
        final TextView btn_assign_delete = (TextView) promptsView.findViewById(R.id.btn_assign_delete);

        TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
        Button btn_add_site = (Button) promptsView.findViewById(R.id.btn_assign_plot);

        txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
        txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.pick_Date(mActivity, txt_date);
            }
        });

        if (itemListPlot.get(position).getPlot_size().equals("")) {
            txt_plot_no.setText("Empty plot : " + itemListPlot.get(position).getPlot_no());
        } else {
            txt_plot_no.setText("Empty plot : " + itemListPlot.get(position).getPlot_no() + " (" + itemListPlot.get(position).getPlot_size() + "Sq. Yard)");

        }

        spn_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCustomerList(spn_customer, "");

                /*if (itemListCustomer.size() == 0) {
                    getCustomerList(spn_customer, "");
                } else {
                    customerDialog(spn_customer);
                }*/
            }
        });

        try {
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        btn_add_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cust_id.trim().equals("")) {
                    new CToast(mActivity).simpleToast("Select Customer", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else if (edt_sell_price.getText().toString().trim().equals("")) {
                    edt_sell_price.setError("Enter sell price");
                    edt_sell_price.requestFocus();
                    return;
                } else if (txt_date.getText().toString().trim().equals("")) {
                    new CToast(mActivity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else {
                    alertDialog.dismiss();
                    assignPlot(txt_date.getTag().toString(), edt_sell_price.getText().toString(), itemListPlot.get(position).getId());
                }
            }
        });

        btn_assign_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showPrd();

                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Plots").child(itemListPlot.get(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() != null) {
                            databaseReference.child("Plots").child(itemListPlot.get(position).getKey()).removeValue().addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    hidePrd();
                                    if(task.isSuccessful()){
                                        new CToast(mActivity).simpleToast("Plot Deleted Successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        getPlotList();
                                        alertDialog.dismiss();
                                    }
                                    else {
                                        new CToast(mActivity).simpleToast(task.getException().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    }

                                }
                            }).addOnFailureListener(mActivity, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    hidePrd();
                                    new CToast(mActivity).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                }
                            });
                        }
                        else {
                            hidePrd();
                            new CToast(mActivity).simpleToast("Plot not exist", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        hidePrd();
                        new CToast(mActivity).simpleToast(error.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }
                });

              /*  WebAPI webAPI = Apiutils.getClient().create(WebAPI.class);
                Call<CommonModel> call = webAPI.delete_plot(itemListPlot.get(position).getId());
                call.enqueue(new Callback<CommonModel>() {
                    @Override
                    public void onResponse(Call<CommonModel> call, retrofit2.Response<CommonModel> response) {
                        if (response.body().getStatus() == 1) {
                            new CToast(mActivity).simpleToast("Plot Deleted Successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                            getPlotList();
                            alertDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonModel> call, Throwable t) {
                        new CToast(mActivity).simpleToast("Failed to remove", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }
                });*/
            }
        });

        alertDialog.show();
    }


    public void getCustomerList(final TextView view, final String id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Customers").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemListCustomer.clear();
                itemListCustomerTemp.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if (npsnapshot.child("status").getValue().toString().equals("0")) {
                                // for (int i = 0; i < jsonArray.length(); i++) {

                                Item_Customer_List item = new Item_Customer_List();
                                item.setId(npsnapshot.child("id").getValue().toString());
                                item.setName(npsnapshot.child("name").getValue().toString());
                                item.setCity(npsnapshot.child("city").getValue().toString());
                                item.setContact_no(npsnapshot.child("contact_no").getValue().toString());
                                item.setAnother_no(npsnapshot.child("contact_no1").getValue().toString());
                                item.setEmail(npsnapshot.child("email").getValue().toString());
                                item.setAddress(npsnapshot.child("address").getValue().toString());

                                itemListCustomer.add(item);
                                itemListCustomerTemp.add(item);
                                // }
                            }
                        }
                        if (!id.equals("")) {

                            itemListCustomer.clear();

                            for (int i = 0; i < itemListCustomerTemp.size(); i++) {
                                if (!id.equals(itemListCustomerTemp.get(i).getId())) {
                                    itemListCustomer.add(itemListCustomerTemp.get(i));
                                }
                            }

                            customerDialog(view);
                            Log.w("ravi_cus", "customerDialog 1");
                        } else {
                            customerDialog(view);
                            Log.w("ravi_cus", "customerDialog 2");
                        }
                    }
                } catch (Exception e) {
                    Log.e("Test  ", e.getMessage());
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

    public void getCustomerList1(final TextView view, final String id) {
        showPrd();
        Log.w("url", API.CUSTOMER + "?type=List");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.CUSTOMER + "?type=List", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hidePrd();
                try {
                    itemListCustomer.clear();
                    itemListCustomerTemp.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    //  Log.e("Response Customer List", response);

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

                        if (!id.equals("")) {

                            itemListCustomer.clear();

                            for (int i = 0; i < itemListCustomerTemp.size(); i++) {
                                if (!id.equals(itemListCustomerTemp.get(i).getId())) {
                                    itemListCustomer.add(itemListCustomerTemp.get(i));
                                }
                            }

                            customerDialog(view);
                            Log.w("ravi_cus", "customerDialog 1");
                        } else {
                            customerDialog(view);
                            Log.w("ravi_cus", "customerDialog 2");
                        }

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    public void customerDialog(final TextView textView) {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_spinner, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText edtSearchLocation = (EditText) promptsView.findViewById(R.id.edt_spn_search);
        final ListView list_location = (ListView) promptsView.findViewById(R.id.list_spn);
        tvAddNewCustomer = (TextView) promptsView.findViewById(R.id.tvAddNewCustomer);

        final ArrayList<String> arrayListTemp = new ArrayList<>();
        final ArrayList<String> arrayListId = new ArrayList<>();

        edtSearchLocation.setHint("Search Customer");
        for (int i = 0; i < itemListCustomer.size(); i++) {
            arrayListTemp.add(itemListCustomer.get(i).getName());
            arrayListId.add(itemListCustomer.get(i).getId());
        }

        list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));

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
                    list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));
                } else {
                    arrayListTemp.clear();
                    arrayListId.clear();
                    for (int i = 0; i < itemListCustomer.size(); i++) {
                        arrayListTemp.add(itemListCustomer.get(i).getName());
                        arrayListId.add(itemListCustomer.get(i).getId());
                    }
                    list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        list_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                textView.setText(arrayListTemp.get(position));
                textView.setTag(arrayListId.get(position));
                cust_id = arrayListId.get(position);
                alertDialog.dismiss();

            }
        });

        tvAddNewCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OpenNewCustomerDailog(tvAddNewCustomer,textView);

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dailog_add_customer, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.setCanceledOnTouchOutside(false);

                final EditText edt_add_customer_name = (EditText) promptsView.findViewById(R.id.edt_add_customer_name);
                final EditText edt_add_mobile = (EditText) promptsView.findViewById(R.id.edt_add_mobile);
                final EditText edt_add_email = (EditText) promptsView.findViewById(R.id.edt_add_email);
                final EditText edt_add_address = (EditText) promptsView.findViewById(R.id.edt_add_address);
                final EditText edt_add_Anothermobile = (EditText) promptsView.findViewById(R.id.edt_add_Anthormobile);
                final EditText edt_add_city = (EditText) promptsView.findViewById(R.id.edt_add_city);

                Button btn_add_Newcstmr_cancel = (Button) promptsView.findViewById(R.id.btn_add_Newcstmr_cancel);
                Button btn_add_Newcstmr_add = (Button) promptsView.findViewById(R.id.btn_add_Newcstmr_add);
                edt_add_email.setVisibility(View.GONE);
                edt_add_Anothermobile.setVisibility(View.GONE);
                edt_add_address.setVisibility(View.GONE);
                edt_add_city.setVisibility(View.GONE);


                btn_add_Newcstmr_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btn_add_Newcstmr_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (edt_add_customer_name.getText().toString().equals("")) {
                            edt_add_customer_name.setError("Enter Your Name");
                            edt_add_customer_name.requestFocus();
                            return;
                        } else if (edt_add_mobile.getText().toString().equals("")) {
                            edt_add_mobile.setError("Enter Mobile No.");
                            edt_add_mobile.requestFocus();
                            return;
                        } else {
                            //addnewCustomer(edt_add_customer_name.getText().toString(), edt_add_mobile.getText().toString(), edt_add_Anothermobile.getText().toString(), edt_add_email.getText().toString(), edt_add_address.getText().toString(), edt_add_city.getText().toString(), alertDialog,textView);


                            String URL = "";

                            if (type.equals("EDIT")) {
                                URL = API.EDIT_CUSTOMER;
                            } else {
                                URL = API.CUSTOMER;
                            }

                            showPrd();
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    hidePrd();
                                    try {

                                        JSONObject jsonObject = new JSONObject(response);
                                        //Log.e("Response Customer Add", response);

                                        if (jsonObject.getInt("result") == 1) {

                                            new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                         //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                                            //finish();
                                            overridePendingTransition(0, 0);
                                            alertDialog.dismiss();
                                            //customerDialog(textView);
                                            //getCustomerList(textView, "");

                                            showPrd();
                                            Log.w("url", API.CUSTOMER + "?type=List");
                                            StringRequest stringRequest = new StringRequest(Request.Method.GET, API.CUSTOMER + "?type=List", new Response.Listener<String>() {

                                                @Override
                                                public void onResponse(String response) {

                                                    hidePrd();
                                                    try {
                                                        itemListCustomer.clear();
                                                        itemListCustomerTemp.clear();

                                                        JSONObject jsonObject = new JSONObject(response);
                                                        //  Log.e("Response Customer List", response);

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

                                                            arrayListTemp.clear();
                                                            arrayListId.clear();

                                                            edtSearchLocation.setHint("Search Customer");
                                                            for (int i = 0; i < itemListCustomer.size(); i++) {
                                                                arrayListTemp.add(itemListCustomer.get(i).getName());
                                                                arrayListId.add(itemListCustomer.get(i).getId());
                                                            }

                                                            list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));

                                                        } else {
                                                            new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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

                                            RequestQueue queue = Volley.newRequestQueue(mActivity);
                                            queue.add(stringRequest);


                                        } else {
                                            new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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

                                    if (type.equals("EDIT")) {
                                        params.put("customer_id", id);
                                    } else {
                                        params.put("type", "Add");
                                    }

                                    params.put("name", edt_add_customer_name.getText().toString());
                                    //params.put("city", edt_add_city.getText().toString());
                                    params.put("contact_no", edt_add_mobile.getText().toString());
                                    //  params.put("contact_no1", edt_add_Anothermobile.getText().toString());
                                    // params.put("email",  edt_add_email.getText().toString());
                                    //  params.put("address", edt_add_address.getText().toString());

                                    return params;

               /* edt_add_customer_name.setText(getIntent().getStringExtra("NAME"));
                edt_add_mobile.setText(getIntent().getStringExtra("MOBILE"));
                edt_add_Anthormobile.setText(getIntent().getStringExtra("MOBILE1"));
                edt_add_email.setText(getIntent().getStringExtra("EMAIL"));
                edt_add_address.setText(getIntent().getStringExtra("ADDRESS"));
                edt_add_city.setText(getIntent().getStringExtra("CITY"));*/
                                }
                            };

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    30000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            queue.add(stringRequest);

                        }
                    }
                });

                alertDialog.show();


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

    /*private void OpenNewCustomerDailog(TextView tvAddNewCustomer,TextView textView) {

        if (type.equals("ADD")) {
            getSupportActionBar().setTitle(Html.fromHtml("Add Customer"));
        } else if (type.equals("EDIT")) {
            getSupportActionBar().setTitle(Html.fromHtml("Edit Customer"));
        }

        if (type.equals("EDIT")) {

            tvAddNewCustomer.setText("Update Customer");

            id = getIntent().getStringExtra("ID");
            name = getIntent().getStringExtra("NAME");
            mobile = getIntent().getStringExtra("MOBILE");
            mobile1 = getIntent().getStringExtra("MOBILE1");
            emial = getIntent().getStringExtra("EMAIL");
            address = getIntent().getStringExtra("ADDRESS");
            city = getIntent().getStringExtra("CITY");

            edt_add_customer_name.setText(getIntent().getStringExtra("NAME"));
            edt_add_mobile.setText(getIntent().getStringExtra("MOBILE"));
            edt_add_Anothermobile.setText(getIntent().getStringExtra("MOBILE1"));
            edt_add_email.setText(getIntent().getStringExtra("EMAIL"));
            edt_add_address.setText(getIntent().getStringExtra("ADDRESS"));
            edt_add_city.setText(getIntent().getStringExtra("CITY"));
        } else {
            tvAddNewCustomer.setText("Add Customer");
        }

        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dailog_add_customer, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setCanceledOnTouchOutside(false);

        final EditText edt_add_customer_name = (EditText) promptsView.findViewById(R.id.edt_add_customer_name);
        final EditText edt_add_mobile = (EditText) promptsView.findViewById(R.id.edt_add_mobile);
        final EditText edt_add_email = (EditText) promptsView.findViewById(R.id.edt_add_email);
        final EditText edt_add_address = (EditText) promptsView.findViewById(R.id.edt_add_address);
        final EditText edt_add_Anothermobile = (EditText) promptsView.findViewById(R.id.edt_add_Anthormobile);
        final EditText edt_add_city = (EditText) promptsView.findViewById(R.id.edt_add_city);

        Button btn_add_Newcstmr_cancel = (Button) promptsView.findViewById(R.id.btn_add_Newcstmr_cancel);
        Button btn_add_Newcstmr_add = (Button) promptsView.findViewById(R.id.btn_add_Newcstmr_add);


        btn_add_Newcstmr_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btn_add_Newcstmr_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_add_customer_name.getText().toString().equals("")) {
                    edt_add_customer_name.setError("Enter Your Name");
                    edt_add_customer_name.requestFocus();
                    return;
                } else if (edt_add_mobile.getText().toString().equals("")) {
                    edt_add_mobile.setError("Enter Mobile No.");
                    edt_add_mobile.requestFocus();
                    return;
                } else if (edt_add_mobile.getText().toString().length() != 10) {
                    edt_add_mobile.setError("Mobile No. must be 10 digit");
                    edt_add_mobile.requestFocus();
                    return;
                } else if (!edt_add_Anothermobile.getText().toString().equals("") && edt_add_Anothermobile.getText().toString().length() != 10) {
                    edt_add_Anothermobile.setError("Mobile No. must be 10 digit");
                    edt_add_Anothermobile.requestFocus();
                    return;
                } else if (!edt_add_email.getText().toString().equals("") && !edt_add_email.getText().toString().trim().matches(Helper.emailPattern)) {
                    edt_add_email.setError("Enter Valid Email");
                    edt_add_email.requestFocus();
                    return;
                } else if (edt_add_address.getText().toString().equals("")) {
                    edt_add_address.setError("Enter Your Address");
                    edt_add_address.requestFocus();
                    return;
                } else if (edt_add_city.getText().toString().equals("")) {
                    edt_add_city.setError("Enter Your City");
                    edt_add_city.requestFocus();
                    return;
                } else {
                    addnewCustomer(edt_add_customer_name.getText().toString(), edt_add_mobile.getText().toString(), edt_add_Anothermobile.getText().toString(), edt_add_email.getText().toString(), edt_add_address.getText().toString(), edt_add_city.getText().toString(), alertDialog,textView);
                }
            }
        });

        alertDialog.show();

    }*/

    public void assignPlot(final String date, final String price, final String id) {
        showPrd();

        //update data to Properties table
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Plots").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(id)) {

                                DatabaseReference cineIndustryRef = databaseReference.child("Plots").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("customer_id", cust_id);
                                map.put("date_of_purchase", date);
                                map.put("purchase_price", price);
                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        getPlotList();
                                        new CToast(mActivity).simpleToast("Plot assign successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hidePrd();
                                        new CToast(mActivity).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    hidePrd();
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

    public void assignPlot1(final String date, final String price, final String id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.ASSIGN_CUSTOMER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //  Log.e("Response Plot Add", response);

                    if (jsonObject.getInt("result") == 1) {

                        getPlotList();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("type", "Plot");
                params.put("customer_id", cust_id);
                params.put("plot_id", id);
                params.put("date_of_purchase", date);
                params.put("purchase_price", price);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    public void changeCustomer(final String old_cusomer_id, final String new_cusomer_id, final String id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.UNASSIGN_CUSTOMER_PLOT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Plot change", response);

                    if (jsonObject.getInt("result") == 1) {

                        getPlotList();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("plot_id", id);
                params.put("old_customer_id", old_cusomer_id);
                params.put("new_customer_id", new_cusomer_id);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    public void removeCustomer(final String id, final String cusomer_id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.REMOVE_CUSTOMER_PLOT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Plot remove ", response);

                    if (jsonObject.getInt("result") == 1) {

                        getPlotList();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("plot_id", id);
                params.put("customer_id", cusomer_id);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    public void addPaymentforPlot(final String date, final String time, final String amount, final String id, final String customer_id, final String remark) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.PAYMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    // Log.e("Response Plot Add", response);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {
                        getPlotList();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("plot_id", id);
                params.put("amount", amount);
                params.put("payment_date", date);
                params.put("payment_time", time);
                params.put("customer_id", customer_id);
                params.put("remarks", remark);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    public void addPlot(final String plot_no, final String plot_size) {
        showPrd();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String key = rootRef.child("Plots").push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("site_id", id);
        map.put("plot_no", plot_no);
        map.put("size", plot_size);
        map.put("customer_id", 0);
        map.put("date_of_purchase","0000-00-00");
        map.put("id", key);
        map.put("purchase_price", "");

        rootRef.child("Plots").child(key).setValue(map).addOnCompleteListener(Activity_Plots_List.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hidePrd();
                new CToast(mActivity).simpleToast("Plot added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                getPlotList();

            }

        }).addOnFailureListener(Activity_Plots_List.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePrd();
                Toast.makeText(Activity_Plots_List.this, "Please try later...", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void addPlot1(final String plot_no, final String plot_size) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.PLOTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    // Log.e("Response Plot Add", response);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {

                        getPlotList();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("plot_no", plot_no);
                params.put("size", plot_size);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void showPrd() {
        prd = new ProgressDialog(mActivity);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }

    @Override
    public boolean onSupportNavigateUp() {
        pending_plote = 0;
        finish();
        overridePendingTransition(0, 0);
    //    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pending_plote = 0;

     //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        Intent intent = new Intent(Activity_Plots_List.this, Activity_Plot_ActiveList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
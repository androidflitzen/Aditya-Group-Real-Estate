package com.flitzen.adityarealestate_new.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Property_List;
import com.flitzen.adityarealestate_new.Adapter.Spn_Adapter;
import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.Items.Item_Property_List;
import com.flitzen.adityarealestate_new.R;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Activity_Rent_List_test extends AppCompatActivity {

    Activity mActivity;
    ProgressDialog prd;
    TextView tvNoActiveCustomer;

    FloatingActionButton fab_add_property;
    SwipeRefreshLayout swipe_refresh;
    RecyclerView rec_rent_list;
    Adapter_Property_List adapter_property_list;
    ArrayList<Item_Property_List> itemList = new ArrayList<>();
    ArrayList<Item_Property_List> itemListTemp = new ArrayList<>();

    String cust_id = "";
    List<Item_Customer_List> itemListCustomer = new ArrayList<>();

    String type = "", id = "", name = "", mobile = "", mobile1 = "", emial = "", address = "", city = "";
    Button btn_add_Newcstmr_add;

    private EditText edtSearch;
    private ImageView imgClearSearch;
    TextView tvtitle;
    boolean check = false;
    String currentMonth = "";
    String currentYear = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_list);

        //getSupportActionBar().setTitle(Html.fromHtml("Rents"));
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Rent_List_test.this;

        edtSearch = (EditText) findViewById(R.id.edt_search);
        tvNoActiveCustomer = findViewById(R.id.tvNoActiveCustomer);
        imgClearSearch = (ImageView) findViewById(R.id.img_clear_search);
        tvtitle = findViewById(R.id.tvtitle);

        tvtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter_property_list.notifyDataSetChanged();
            }
        });

        ImageView ivEdit1 = (ImageView) findViewById(R.id.ivEdit1);
        ivEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                edtSearch.setText(null);
                edtSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

                getRentList();
            }
        });

        rec_rent_list = (RecyclerView) findViewById(R.id.rec_rent_list);
        rec_rent_list.setLayoutManager(new LinearLayoutManager(this));
        rec_rent_list.setHasFixedSize(true);
        adapter_property_list = new Adapter_Property_List(mActivity, itemList, false);
        rec_rent_list.setAdapter(adapter_property_list);

        fab_add_property = (FloatingActionButton) findViewById(R.id.fab_add_property);
        fab_add_property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_property_add, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);

                final EditText edt_property_name = (EditText) promptsView.findViewById(R.id.edt_property_name);
                final EditText edt_property_address = (EditText) promptsView.findViewById(R.id.edt_property_address);

                TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                Button btn_add_site = (Button) promptsView.findViewById(R.id.btn_add_property);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_add_site.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (edt_property_name.getText().toString().trim().equals("")) {
                            edt_property_name.setError("Property Name");
                            edt_property_name.requestFocus();
                            return;
                        } else if (edt_property_address.getText().toString().trim().equals("")) {
                            edt_property_address.setError("Property Address");
                            edt_property_address.requestFocus();
                            return;
                        } else {
                            alertDialog.dismiss();
                            addProperty(edt_property_name.getText().toString().trim(), edt_property_address.getText().toString().trim());
                        }
                    }
                });

                alertDialog.show();
            }
        });

        adapter_property_list.setOnItemClickListener(new Adapter_Property_List.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (itemList.get(position).getIs_hired().equals("1")) {
                    //dialogPropertyInfo(position);

                    Intent intent = new Intent(mActivity, RentCustomerHistoryActivity.class);
                    Aditya.ID = itemList.get(position).getId();
                    intent.putExtra("property_id", itemList.get(position).getId());
                    intent.putExtra("property_name", itemList.get(position).getProperty_name());
                    startActivity(intent);
                    overridePendingTransition(0, 0);

                } else {
                    dialogPropertyAssign(position);
                }
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int ii, int i1, int i2) {
                String word = edtSearch.getText().toString().trim().toLowerCase();
                itemList.clear();
                if (word.trim().isEmpty()) {
                    itemList.addAll(itemListTemp);
                    adapter_property_list.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < itemListTemp.size(); i++) {
                        if (itemListTemp.get(i).getProperty_name() != null && itemListTemp.get(i).getCustomer_name() != null) {
                            if (itemListTemp.get(i).getProperty_name().toLowerCase().contains(word) || itemListTemp.get(i).getCustomer_name().toLowerCase().contains(word)) {
                                itemList.add(itemListTemp.get(i));
                            } else if (itemListTemp.get(i).getCustomer_name().contains(word)) {
                                itemList.add(itemListTemp.get(i));
                            }
                        }

                    }
                    adapter_property_list.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    imgClearSearch.setVisibility(View.VISIBLE);
                else
                    imgClearSearch.setVisibility(View.GONE);

            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        imgClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText(null);
                edtSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });

        // getRentList();

    }

    public void getRentList() {
        swipe_refresh.setRefreshing(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Properties").orderByKey();
        //databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipe_refresh.setRefreshing(false);
                itemList.clear();
                itemListTemp.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            Item_Property_List item = new Item_Property_List();
                            item.setId(npsnapshot.child("id").getValue().toString());
                            String propertyID = npsnapshot.child("id").getValue().toString();
                            item.setProperty_name(npsnapshot.child("property_name").getValue().toString());
                            item.setAddress(npsnapshot.child("address").getValue().toString());
                            //item.setHired_since(npsnapshot.child("hired_since").getValue().toString());
                            if (npsnapshot.child("hired_since").getValue().toString().equals("0000-00-00")) {
                                item.setIs_hired("0");
                            } else {
                                item.setIs_hired("1");
                            }

                            item.setCustomer_id(npsnapshot.child("customer_id").getValue().toString());
                            String customerId = npsnapshot.child("customer_id").getValue().toString();
                            Log.e("id  ", customerId);

                            Query query = databaseReference.child("Customers").orderByKey();
                            // databaseReference.keepSynced(true);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                Log.e("mainID  ", npsnapshot1.child("id").getValue().toString());
                                                if (npsnapshot1.child("id").getValue().toString().equals(customerId)) {
                                                    String name = npsnapshot1.child("name").getValue().toString();
                                                    Log.e("Name  ", name);
                                                    item.setCustomer_name(name);
                                        /*String key=snapshot.getKey();
                                        String name=snapshot.child(key).child("name").getValue().toString();*/
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("Ex   ", e.toString());
                                    }
                                    adapter_property_list.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("error ", error.getMessage());
                                }
                            });


                            //Check payment

                            try {

                                LocalDate todaydate = LocalDate.now();
                                String monthFirstDay = String.valueOf(todaydate.withDayOfMonth(1));
                                System.out.println("=======Months first date in yyyy-mm-dd: " + todaydate.withDayOfMonth(1));

                                Calendar calendar = Calendar.getInstance();
                                int year = calendar.get(Calendar.YEAR);
                                int cMonth = calendar.get(Calendar.MONTH);
                                String hDate = npsnapshot.child("hired_since").getValue().toString();
                                System.out.println("========hDate   " + hDate);

                                String[] date = hDate.split("-");
                                String hYear = date[0];
                                hYear = String.valueOf(year);
                                String hDay = date[2];

                                String monthHDate = hYear + "-" + date[1] + "-" + date[2];
                                System.out.println("=========monthHDate   " + monthHDate);

                                Date c = Calendar.getInstance().getTime();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String todayDate = df.format(c);
                                String[] date11 = todayDate.split("-");
                                final String currentMonth = date11[1];
                                final String currentYear = date11[0];
                                final String currentDay = date11[2];

                                System.out.println("==========currentYear   "+currentYear);

                                String monthHHDate = hYear + "-" +currentMonth + "-" + date[2];

                                Calendar cc= Calendar.getInstance();
                                //int next_month = cc.get(Calendar.MONTH) + 1;
                               // int next_month = 03;


                                Calendar cal = GregorianCalendar.getInstance();
                                SimpleDateFormat df1 = new SimpleDateFormat("MMMM yyyy");

                                Date currentMonth1 = new Date();
                                cal.setTime(currentMonth1);

                                // current month
                                String currentMonthAsSting = df1.format(cal.getTime());

                                // Add next month
                                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+1);
                                String nextMonthAsString = df1.format(cal.getTime());

                                System.out.println("==========next_month   "+nextMonthAsString);

                                String nextMonthDate = hYear + "-" +nextMonthAsString + "-" + date[2];
                                System.out.println("=======monthHHDate   "+monthHHDate);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1 = sdf.parse(monthHDate);
                                Date date2 = sdf.parse(todayDate);
                                int i = date1.compareTo(date2);
                               // switch (i) {
                                    //case -1: //date1<date2 = -1   date gone
                                        Query queryPay = databaseReference.child("Payments").orderByKey();
                                        // databaseReference.keepSynced(true);
                                        queryPay.addValueEventListener(new ValueEventListener() {
                                            @SuppressLint("NewApi")
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshotPay) {
                                                try {
                                                    if (dataSnapshotPay.exists()) {
                                                        for (DataSnapshot npsnapshot2 : dataSnapshotPay.getChildren()) {
                                                            if (npsnapshot2.child("property_id").getValue().toString().equals(propertyID)) {
                                                                if (npsnapshot2.child("customer_id").getValue().toString().equals(customerId)) {
                                                                    String paymentDate = npsnapshot2.child("payment_date").getValue().toString();
                                                                    Log.e("paymentDate  ", paymentDate);

                                                                    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                                    SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                                                                    try {
                                                                        Date oneWayTripDate;
                                                                        oneWayTripDate = input.parse(paymentDate);
                                                                        String d = output.format(oneWayTripDate);

                                                                        String[] date = d.split("-");
                                                                        String month = date[1];
                                                                        String year = date[0];
                                                                        String day = date[2];

                                                                        Date date1 = output.parse(monthHHDate);
                                                                        Date date2 = output.parse(d);
                                                                        Date date3 = output.parse(todayDate);
                                                                        Date date4 = output.parse(nextMonthDate);

                                                                        System.out.println("========monthHHDate  "+monthHHDate);
                                                                        System.out.println("========d  "+d);
                                                                        System.out.println("========todayDate  "+todayDate);
                                                                        System.out.println("========nextMonthDate  "+nextMonthDate);



                                                                        /*Date d1 = new Date();
                                                                        if (date2 != null && date1 != null && date4 != null) {
                                                                            if (date2.after(date1) && date2.before(date4)) {
                                                                                item.setCheckDateIsGone(false);
                                                                            }
                                                                            else {
                                                                                if(date3.before(date1)){
                                                                                    item.setCheckDateIsGone(false);
                                                                                }else {
                                                                                    item.setCheckDateIsGone(true);
                                                                                }
                                                                            }
                                                                        }*/






                                                                       /* Date d1 = new Date();

                                                                        if((d1.after(date1) && (d1.before(date4))) || (d.equals(sdf.format(date1)) ||d.equals(sdf.format(date4)))){
                                                                            System.out.println("Date is between ");
                                                                            item.setCheckDateIsGone(false);
                                                                        }
                                                                        else{
                                                                            System.out.println("Date is not between ");
                                                                            item.setCheckDateIsGone(true);
                                                                        }*/









                                                                        int i = date1.compareTo(date2);
                                                                        switch (i) {
                                                                            case -1: //date1<date2 = -1   date gone
                                                                                item.setCheckDateIsGone(false);
                                                                                break;
                                                                            case 1: //date1>date2 = 1
                                                                                int i1 = date1.compareTo(date3);
                                                                                switch (i1) {
                                                                                    case -1: //date1<date3 = -1   date gone
                                                                                        item.setCheckDateIsGone(true);
                                                                                        break;
                                                                                    case 1:
                                                                                        item.setCheckDateIsGone(false);
                                                                                        break;
                                                                                    case 0:
                                                                                        item.setCheckDateIsGone(false);
                                                                                        break;

                                                                                }
                                                                                break;
                                                                                //return sDate1;

                                                                            case 0: //date1==date2= 0
                                                                                item.setCheckDateIsGone(false);
                                                                                break;

                                                                            default:
                                                                        }







                                                                        /*System.out.println("=========year  "+year);

                                                                        if (Integer.parseInt(month) == Integer.parseInt(currentMonth)) {
                                                                            if (Integer.parseInt(year) == Integer.parseInt(currentYear)) {
                                                                                item.setCheckDateIsGone(false);
                                                                                System.out.println("=========id false  "+npsnapshot2.child("id").getValue().toString());
                                                                            } else {
                                                                                if(Integer.parseInt(hDay) < Integer.parseInt(currentDay)){
                                                                                    item.setCheckDateIsGone(true);
                                                                                }else {
                                                                                    item.setCheckDateIsGone(false);
                                                                                }
                                                                                System.out.println("=========id  if else "+npsnapshot2.child("id").getValue().toString());
                                                                            }
                                                                        } else {
                                                                            if(Integer.parseInt(hDay) < Integer.parseInt(currentDay)){
                                                                                item.setCheckDateIsGone(true);
                                                                            }else {
                                                                                item.setCheckDateIsGone(false);
                                                                            }
                                                                            System.out.println("=========id else "+npsnapshot2.child("id").getValue().toString());
                                                                        }*/


                                                                    } catch (ParseException e) {
                                                                        System.out.println("=========e  parse   "+e.getMessage());
                                                                        e.printStackTrace();
                                                                    }


                                                                }
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    Log.e("Ex   ", e.toString());
                                                    new CToast(mActivity).simpleToast("Something went wrong", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                }
                                                adapter_property_list.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("error ", error.getMessage());
                                                new CToast(mActivity).simpleToast("Something went wrong", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                            }
                                        });
                                   /* case 1: //date1>date2 = 1
                                        //return sDate1;
                                    case 0: //date1==date2= 0
                                    default:*/
                                        //return sDate2;
                              //  }
                            } catch (ParseException e) {
                                // return sDate1;
                            }


                            Log.e("main For  ", "main for");
                            item.setRent(npsnapshot.child("rent").getValue().toString());
                            item.setHired_since(npsnapshot.child("hired_since").getValue().toString());

                            itemList.add(item);
                            itemListTemp.add(item);

                            // }

                        }
                        if (itemList.size() > 0) {
                            rec_rent_list.setVisibility(View.VISIBLE);
                            tvNoActiveCustomer.setVisibility(View.GONE);
                        } else {
                            rec_rent_list.setVisibility(View.GONE);
                            tvNoActiveCustomer.setVisibility(View.VISIBLE);
                        }
                        adapter_property_list.notifyDataSetChanged();
                    } else {
                        tvNoActiveCustomer.setVisibility(View.VISIBLE);
                    }
                } catch (
                        Exception e) {
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

    public void getRentList1() {

        swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.PROPERTY + "?type=List", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                swipe_refresh.setRefreshing(false);

                try {
                    itemList.clear();
                    itemListTemp.clear();

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("property");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Property_List item = new Item_Property_List();
                            item.setId(object.getString("id"));
                            item.setProperty_name(object.getString("property_name"));
                            item.setAddress(object.getString("address"));
                            item.setIs_hired(object.getString("is_hired"));
                            item.setCustomer_id(object.getString("customer_id"));
                            item.setCustomer_name(object.getString("customer_name"));
                            item.setRent(object.getString("rent"));
                            item.setHired_since(object.getString("hired_since"));

                            itemList.add(item);
                            itemListTemp.add(item);
                        }
                        adapter_property_list.notifyDataSetChanged();

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
                Utils.showLog("==== VolleyError " + error.getMessage());
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    public void dialogPropertyInfo(final int position) {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_customer_property_info, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        TextView txt_property_name = (TextView) promptsView.findViewById(R.id.txt_property_name);
        TextView txt_customer_name = (TextView) promptsView.findViewById(R.id.txt_customer_name);
        TextView txt_hired_since = (TextView) promptsView.findViewById(R.id.txt_hired_since);
        TextView txt_rent = (TextView) promptsView.findViewById(R.id.txt_rent);
        View view_unassign_property = promptsView.findViewById(R.id.view_unassign_property);

        view_unassign_property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                        .setTitle(Html.fromHtml("<b> Remove Customer</b>"))
                        .setMessage("Are you sure you want to remove customer?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                unAssignProperty(itemList.get(position).getCustomer_id(), itemList.get(position).getId());

                            }
                        }).setNegativeButton("No", null).show();
            }
        });

        txt_property_name.setText(itemList.get(position).getProperty_name());
        txt_customer_name.setText(itemList.get(position).getCustomer_name());

        String[] date = itemList.get(position).getHired_since().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
        txt_hired_since.setText(date[0] + " " + mm + " " + date[2]);

        txt_rent.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(itemList.get(position).getRent())));

        alertDialog.show();
    }

    public void dialogPropertyAssign(final int position) {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_customer_property_assign, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);

        final TextView txt_property_name = (TextView) promptsView.findViewById(R.id.txt_property_name);
        final TextView spn_customer = (TextView) promptsView.findViewById(R.id.spn_customer);
        final TextView txt_date = (TextView) promptsView.findViewById(R.id.txt_date);
        final EditText edt_rent = (EditText) promptsView.findViewById(R.id.edt_rent);

        TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
        Button btn_add_site = (Button) promptsView.findViewById(R.id.btn_assign_plot);

        txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
        txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

        txt_property_name.setText(itemList.get(position).getProperty_name());

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.pick_Date(mActivity, txt_date);
            }
        });

        spn_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListCustomer.size() == 0) {
                    getCustomerList(spn_customer);
                } else {
                    customerDialog(spn_customer);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btn_add_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cust_id.equals("")) {
                    new CToast(mActivity).simpleToast("Select Customer", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else if (edt_rent.getText().toString().trim().equals("")) {
                    edt_rent.setError("Enter Rent");
                    edt_rent.requestFocus();
                    return;
                } else if (txt_date.getText().toString().trim().equals("")) {
                    new CToast(mActivity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else {//if (itemList.get(position).getId().equals("") && itemList.get(position).getId().isEmpty()) {
                    // alertDialog.dismiss();
                    assignProperty(txt_date.getTag().toString(), edt_rent.getText().toString(), itemList.get(position).getId(), alertDialog);
                }
            }
        });

        alertDialog.show();
    }

    public void getCustomerList(final TextView view) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Customers").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemListCustomer.clear();
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
                                // }
                            }
                        }
                        customerDialog(view);
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

    public void getCustomerList1(final TextView view) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.CUSTOMER + "?type=List", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hidePrd();
                try {
                    itemListCustomer.clear();

                    JSONObject jsonObject = new JSONObject(response);

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
                        }

                        customerDialog(view);

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
        final TextView tvAddNewCustomer = (TextView) promptsView.findViewById(R.id.tvAddNewCustomer);

        final ArrayList<String> arrayListTemp = new ArrayList<>();
        final ArrayList<String> arrayListId = new ArrayList<>();

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

                //OpenNewCustomerDailog(textView);

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dailog_add_customer, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog_b = alertDialogBuilder.create();


                final EditText edt_add_customer_name = (EditText) promptsView.findViewById(R.id.edt_add_customer_name);
                final EditText edt_add_mobile = (EditText) promptsView.findViewById(R.id.edt_add_mobile);
                final EditText edt_add_Anthormobile = (EditText) promptsView.findViewById(R.id.edt_add_Anthormobile);
                final EditText edt_add_email = (EditText) promptsView.findViewById(R.id.edt_add_email);
                final EditText edt_add_address = (EditText) promptsView.findViewById(R.id.edt_add_address);
                final EditText edt_add_city = (EditText) promptsView.findViewById(R.id.edt_add_city);

                final Button btn_add_Newcstmr_cancel = (Button) promptsView.findViewById(R.id.btn_add_Newcstmr_cancel);
                final Button btn_add_Newcstmr_add = (Button) promptsView.findViewById(R.id.btn_add_Newcstmr_add);
                edt_add_Anthormobile.setVisibility(View.GONE);
                edt_add_email.setVisibility(View.GONE);
                edt_add_address.setVisibility(View.GONE);
                edt_add_city.setVisibility(View.GONE);

        /*this.type = getIntent().getStringExtra("TYPE");

        if (this.type.equals("ADD")) {
            getSupportActionBar().setTitle(Html.fromHtml("Add Customer"));
        } else if (this.type.equals("EDIT")) {
            getSupportActionBar().setTitle(Html.fromHtml("Edit Customer"));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/

       /* txt_add_customer = (TextView) findViewById(R.id.txt_add_customer);
        edt_cust_name = (EditText) findViewById(R.id.edt_cust_name);
        edt_cust_no = (EditText) findViewById(R.id.edt_cust_no);
        edt_cust_other_no = (EditText) findViewById(R.id.edt_cust_other_no);
        edt_cust_email = (EditText) findViewById(R.id.edt_cust_email);
        edt_cust_address = (EditText) findViewById(R.id.edt_cust_address);
        edt_cust_city = (EditText) findViewById(R.id.edt_cust_city);

        btn_add_customer = findViewById(R.id.btn_add_customer);*/
                type = "Add";
                if (type.equals("EDIT")) {
                    btn_add_Newcstmr_add.setText("Update Customer");

                    id = getIntent().getStringExtra("ID");
                    name = getIntent().getStringExtra("NAME");
                    mobile = getIntent().getStringExtra("MOBILE");
                    mobile1 = getIntent().getStringExtra("MOBILE1");
                    emial = getIntent().getStringExtra("EMAIL");
                    address = getIntent().getStringExtra("ADDRESS");
                    city = getIntent().getStringExtra("CITY");

                    edt_add_customer_name.setText(getIntent().getStringExtra("NAME"));
                    edt_add_mobile.setText(getIntent().getStringExtra("MOBILE"));
                    edt_add_Anthormobile.setText(getIntent().getStringExtra("MOBILE1"));
                    edt_add_email.setText(getIntent().getStringExtra("EMAIL"));
                    edt_add_address.setText(getIntent().getStringExtra("ADDRESS"));
                    edt_add_city.setText(getIntent().getStringExtra("CITY"));

                } else {
                    // btn_add_Newcstmr_add.setText("Add Customer");
                }

                btn_add_Newcstmr_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (edt_add_customer_name.getText().toString().equals("")) {
                            edt_add_customer_name.setError("Enter Name");
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

                        } else {

                            //addCustomer(edt_add_customer_name.getText().toString(),edt_add_mobile.getText().toString(),edt_add_Anthormobile.getText().toString(),edt_add_email.getText().toString(),edt_add_address.getText().toString(),edt_add_city.getText().toString(),alertDialog,textView);

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
                                            alertDialog_b.dismiss();
                                            // getCustomerList(textView);

                                            showPrd();
                                            StringRequest stringRequest = new StringRequest(Request.Method.GET, API.CUSTOMER + "?type=List", new Response.Listener<String>() {

                                                @Override
                                                public void onResponse(String response) {

                                                    hidePrd();
                                                    try {
                                                        itemListCustomer.clear();

                                                        JSONObject jsonObject = new JSONObject(response);
                                                        // Log.e("Response Customer List", response);

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
                                                            }


                                                            arrayListTemp.clear();
                                                            arrayListId.clear();

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

                                            //finish();

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
                                    params.put("contact_no", edt_add_mobile.getText().toString());
                                    return params;
                                }
                            };

                            //alertDialog.dismiss();

                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    30000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            RequestQueue queue = Volley.newRequestQueue(Activity_Rent_List_test.this);
                            queue.add(stringRequest);


                            // alertDialog.show();
                        }
                    }

                });


                btn_add_Newcstmr_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog_b.dismiss();
                    }
                });


                alertDialog_b.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = alertDialog_b.getWindow();
                lp.copyFrom(window.getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);


                //alertDialog.dismiss();
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

    private void OpenNewCustomerDailog(final TextView textView) {

        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dailog_add_customer, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();


        final EditText edt_add_customer_name = (EditText) promptsView.findViewById(R.id.edt_add_customer_name);
        final EditText edt_add_mobile = (EditText) promptsView.findViewById(R.id.edt_add_mobile);
        final EditText edt_add_Anthormobile = (EditText) promptsView.findViewById(R.id.edt_add_Anthormobile);
        final EditText edt_add_email = (EditText) promptsView.findViewById(R.id.edt_add_email);
        final EditText edt_add_address = (EditText) promptsView.findViewById(R.id.edt_add_address);
        final EditText edt_add_city = (EditText) promptsView.findViewById(R.id.edt_add_city);

        final Button btn_add_Newcstmr_cancel = (Button) promptsView.findViewById(R.id.btn_add_Newcstmr_cancel);
        final Button btn_add_Newcstmr_add = (Button) promptsView.findViewById(R.id.btn_add_Newcstmr_add);


        /*this.type = getIntent().getStringExtra("TYPE");

        if (this.type.equals("ADD")) {
            getSupportActionBar().setTitle(Html.fromHtml("Add Customer"));
        } else if (this.type.equals("EDIT")) {
            getSupportActionBar().setTitle(Html.fromHtml("Edit Customer"));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/

       /* txt_add_customer = (TextView) findViewById(R.id.txt_add_customer);
        edt_cust_name = (EditText) findViewById(R.id.edt_cust_name);
        edt_cust_no = (EditText) findViewById(R.id.edt_cust_no);
        edt_cust_other_no = (EditText) findViewById(R.id.edt_cust_other_no);
        edt_cust_email = (EditText) findViewById(R.id.edt_cust_email);
        edt_cust_address = (EditText) findViewById(R.id.edt_cust_address);
        edt_cust_city = (EditText) findViewById(R.id.edt_cust_city);

        btn_add_customer = findViewById(R.id.btn_add_customer);*/
        type = "Add";
        if (this.type.equals("EDIT")) {
            btn_add_Newcstmr_add.setText("Update Customer");

            id = getIntent().getStringExtra("ID");
            name = getIntent().getStringExtra("NAME");
            mobile = getIntent().getStringExtra("MOBILE");
            mobile1 = getIntent().getStringExtra("MOBILE1");
            emial = getIntent().getStringExtra("EMAIL");
            address = getIntent().getStringExtra("ADDRESS");
            city = getIntent().getStringExtra("CITY");

            edt_add_customer_name.setText(getIntent().getStringExtra("NAME"));
            edt_add_mobile.setText(getIntent().getStringExtra("MOBILE"));
            edt_add_Anthormobile.setText(getIntent().getStringExtra("MOBILE1"));
            edt_add_email.setText(getIntent().getStringExtra("EMAIL"));
            edt_add_address.setText(getIntent().getStringExtra("ADDRESS"));
            edt_add_city.setText(getIntent().getStringExtra("CITY"));

        } else {
            // btn_add_Newcstmr_add.setText("Add Customer");
        }

        btn_add_Newcstmr_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_add_customer_name.getText().toString().equals("")) {
                    edt_add_customer_name.setError("Enter Name");
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
                } else if (!edt_add_Anthormobile.getText().toString().equals("") && edt_add_Anthormobile.getText().toString().length() != 10) {
                    edt_add_Anthormobile.setError("Mobile No. must be 10 digit");
                    edt_add_Anthormobile.requestFocus();
                    return;
                } else if (!edt_add_email.getText().toString().equals("") && !edt_add_email.getText().toString().trim().matches(Helper.emailPattern)) {
                    edt_add_email.setError("Enter Valid Email");
                    edt_add_email.requestFocus();
                    return;
                } else if (edt_add_address.getText().toString().equals("")) {
                    edt_add_address.setError("Enter Valid Address");
                    edt_add_address.requestFocus();
                    return;
                } else if (edt_add_city.getText().toString().equals("")) {
                    edt_add_city.setError("Enter Valid City");
                    edt_add_city.requestFocus();
                    return;
                } else {

                    addCustomer(edt_add_customer_name.getText().toString(), edt_add_mobile.getText().toString(), edt_add_Anthormobile.getText().toString(), edt_add_email.getText().toString(), edt_add_address.getText().toString(), edt_add_city.getText().toString(), alertDialog, textView);
                    // alertDialog.show();
                }
            }

        });


        btn_add_Newcstmr_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void addCustomer(final String cstmerName, final String CstmerMo,
                             final String CstmerMo1, final String CstmerEmail, final String CstmerAdress,
                             final String CstmerCity, final AlertDialog alertDialog, final TextView textView) {

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
                        //    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                        alertDialog.dismiss();
                        getCustomerList(textView);
                        //finish();

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

                params.put("name", cstmerName);
                params.put("city", CstmerCity);
                params.put("contact_no", CstmerMo);
                params.put("contact_no1", CstmerMo1);
                params.put("email", CstmerEmail);
                params.put("address", CstmerAdress);
                return params;
            }
        };

        //alertDialog.dismiss();

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);


    }

    public void assignProperty(final String date, final String price, final String id,
                               final AlertDialog alertDialog) {

        showPrd();

        //insert new entry to payments table
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String key = rootRef.child("Payments").push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("amount", price);
        map.put("customer_id", cust_id);
        map.put("id", key);
        map.put("next_payment_date", "0000-00-00");
        map.put("payment_attachment", "");
        map.put("payment_date", "0000-00-00 00:00:00");
        map.put("plot_id", 0);
        map.put("property_id", id);
        map.put("remarks", "");
        map.put("customer_status", 0);

        //TODO
        map.put("rent_status", 0);
        map.put("payment_status", 0);

        rootRef.child("Payments").child(key).setValue(map).addOnCompleteListener(Activity_Rent_List_test.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }

        }).addOnFailureListener(Activity_Rent_List_test.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Activity_Rent_List_test.this, "Please try later...", Toast.LENGTH_SHORT).show();
            }

        });


        //insert new entry to payments table

        String key1 = rootRef.child("RentHistory").push().getKey();
        Map<String, Object> map1 = new HashMap<>();

        map1.put("property_id", id);
        map1.put("customer_id", cust_id);
        map1.put("id", key1);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateStart = new Date();

        map1.put("start_date", dateFormat.format(dateStart));
        map1.put("end_date", "0000-00-00");
        map1.put("customer_status_for_property", 0);
        //TODO
        rootRef.child("RentHistory").child(key).setValue(map1).addOnCompleteListener(Activity_Rent_List_test.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }

        }).addOnFailureListener(Activity_Rent_List_test.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Activity_Rent_List_test.this, "Please try later...", Toast.LENGTH_SHORT).show();
            }

        });

        //update data to Properties table
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Properties").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(id)) {

                                DatabaseReference cineIndustryRef = databaseReference.child("Properties").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("customer_id", cust_id);
                                map.put("hired_since", date);
                                map.put("rent", price);
                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        getRentList();
                                        new CToast(mActivity).simpleToast("Property assign successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        alertDialog.dismiss();
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

    public void assignProperty1(final String date, final String price, final String id,
                                final AlertDialog alertDialog) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.ASSIGN_CUSTOMER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Respons Property Assign", response);

                    if (jsonObject.getInt("result") == 1) {

                        getRentList();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        alertDialog.dismiss();

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
                params.put("type", "Property");
                params.put("customer_id", cust_id);
                params.put("property_id", id);
                // params.put("TYPE", "ADD");
                params.put("hired_since", date);
                params.put("rent", price);
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

    public void unAssignProperty(final String customer_id, final String id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.UNASSIGN_CUSTOMER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Property Unassign", response);

                    if (jsonObject.getInt("result") == 1) {

                        getRentList();
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
                params.put("customer_id", customer_id);
                params.put("property_id", id);
                params.put("remarks", "");
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

    public void addProperty(final String site_name, final String site_Address) {
        showPrd();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String key = rootRef.child("Properties").push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("address", site_Address);
        map.put("customer_id", 0);
        map.put("hired_since", "0000-00-00");
        map.put("id", key);
        map.put("property_name", site_name);
        map.put("rent", "");

        rootRef.child("Properties").child(key).setValue(map).addOnCompleteListener(Activity_Rent_List_test.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hidePrd();
                new CToast(mActivity).simpleToast("Property added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                getRentList();

            }

        }).addOnFailureListener(Activity_Rent_List_test.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePrd();
                Toast.makeText(Activity_Rent_List_test.this, "Please try later...", Toast.LENGTH_SHORT).show();
            }

        });
    }


    public void addProperty1(final String site_name, final String site_Address) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.PROPERTY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Response Sites Add", response);

                    if (jsonObject.getInt("result") == 1) {

                        getRentList();
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
                params.put("property_name", site_name);
                params.put("address", site_Address);
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
        prd = new ProgressDialog(Activity_Rent_List_test.this);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
        //    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        getRentList();
        super.onBackPressed();

        //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        Intent intent = new Intent(Activity_Rent_List_test.this, Activity_Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getRentList();
    }
}
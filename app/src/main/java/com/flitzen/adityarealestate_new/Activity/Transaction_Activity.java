package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Calender_TransList;
import com.flitzen.adityarealestate_new.Adapter.Adapter_TransactionCustList;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Transaction_List;
import com.flitzen.adityarealestate_new.Adapter.Adapter_calenderPopupList;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Classes.WebAPI;
import com.flitzen.adityarealestate_new.Fragment.TransActiveCustomer_Fragment;
import com.flitzen.adityarealestate_new.Fragment.TransDeactiveCustomer_Fragment;
import com.flitzen.adityarealestate_new.Items.Iteam_Transaction_list;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.Items.Transcation;
import com.flitzen.adityarealestate_new.R;


import com.flitzen.adityarealestate_new.Task_Reminder.activity.TaskListActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.flitzen.adityarealestate_new.R.layout.calender_spinner_year;

public class Transaction_Activity extends AppCompatActivity {

    Activity mActivity;
    ProgressDialog prd;

    Iteam_Transaction_list gettranscation;


    // SwipeRefreshLayout swipe_refresh;


    // RecyclerView rvTransactionlist;
    @BindView(R.id.TransCalender)
    ImageView TransCalender;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvTranstabActive)
    TextView tvTranstabActive;
    @BindView(R.id.tvTranstabDeactive)
    TextView tvTranstabDeactive;
    @BindView(R.id.Trans_Cust_Fragment)
    FrameLayout TransCustFragment;
    @BindView(R.id.fab_add_TransCustomer1)
    FloatingActionButton fabAddTransCustomer;

    private EditText edtSearch;
    private ImageView imgClearSearch;

    WebAPI webapi;
    int REQUEST_ADD = 01;
    public String CalenderMonth;
    int FinalMonth = 1, finalYear;

    public static Boolean isonBack = false;

    Adapter_Transaction_List adapterTransactionList;
    Adapter_calenderPopupList adapterCalenderPopupList;
    Adapter_TransactionCustList adapterTransactionCustList;
    Adapter_Calender_TransList adapterCalenderTransList;

    ArrayList<Transcation> transactionlist = new ArrayList<>();
    ArrayList<Transcation> transactionlistTemp = new ArrayList<>();

    ArrayList<Item_Customer_List> itemListCustomer = new ArrayList<>();
    ArrayList<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();

    TransActiveCustomer_Fragment transActiveCustomerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_);
        ButterKnife.bind(this);


        mActivity = Transaction_Activity.this;


        webapi = Utils.getRetrofitClient().create(WebAPI.class);


        initUi();


    }

    private void initUi() {

        tvTitle.setText("Customer List");

        edtSearch = (EditText) findViewById(R.id.edt_search);
        imgClearSearch = (ImageView) findViewById(R.id.img_clear_search);
        //  swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        transActiveCustomerFragment = new TransActiveCustomer_Fragment();




      /*  rvTransactionlist = (RecyclerView) findViewById(R.id.rv_Transactionlist);
        rvTransactionlist.setLayoutManager(new LinearLayoutManager(this));
        rvTransactionlist.setHasFixedSize(true);
        adapterTransactionCustList = new Adapter_TransactionCustList(mActivity, itemListCustomer, false);
        rvTransactionlist.setAdapter(adapterTransactionCustList);
*/
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        tvTranstabActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isonBack = true;
                tvTranstabActive.setBackground(getResources().getDrawable(R.drawable.task_bg_title));
                tvTranstabDeactive.setBackground(getResources().getDrawable(R.drawable.trans_tab_bg));
                pushFragment(new TransActiveCustomer_Fragment());
            }
        });


        tvTranstabDeactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isonBack = true;
                tvTranstabActive.setBackground(getResources().getDrawable(R.drawable.trans_tab_bg));
                tvTranstabDeactive.setBackground(getResources().getDrawable(R.drawable.task_bg_title));
                //    tvTitleTask.setText("Pending Task");
                pushFragment(new TransDeactiveCustomer_Fragment());
            }
        });

       /* swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (edtSearch.getText().toString().trim().length() == 0) {
                    if (Network.isNetworkAvailable(mActivity)) {
                        //getAllTransactionlist();
                        transActiveCustomerFragment.getCustomerList();
                    }
                } else {
                    hideSwipeRefresh();
                }

            }
        });*/

        /*edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int ii, int i1, int i2) {
                String word = edtSearch.getText().toString().trim().toLowerCase();
                itemListCustomer.clear();
                if (word.trim().isEmpty()) {
                    itemListCustomer.addAll(itemListCustomerTemp);
                    adapterTransactionCustList.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < itemListCustomerTemp.size(); i++) {
                        if (itemListCustomerTemp.get(i).getName().toLowerCase().contains(word)) {
                            itemListCustomer.add(itemListCustomerTemp.get(i));
                        } else if (itemListCustomerTemp.get(i).getName().contains(word)) {
                            itemListCustomer.add(itemListCustomerTemp.get(i));
                        }
                    }
                    adapterTransactionCustList.notifyDataSetChanged();
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
        });  */

        TransCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //  startActivity(new Intent(mActivity, CalenderView_Activity.class));
                //   mActivity.overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

                TransCalanderDailog();


            }
        });

        pushFragment(new TransActiveCustomer_Fragment());

    }


    private void TransCalanderDailog() {


        LayoutInflater localView = LayoutInflater.from(mActivity);
        final View promptsView = localView.inflate(R.layout.dailog_transcalender, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final MaterialCalendarView calendarView = (MaterialCalendarView) promptsView.findViewById(R.id.calendarView);
        final ImageView ivAddReminder = (ImageView) promptsView.findViewById(R.id.ivAddReminder);


        final RelativeLayout rvCalenderMonth = (RelativeLayout) promptsView.findViewById(R.id.rvCalenderMonth);
        final RelativeLayout rvCalenderYear = (RelativeLayout) promptsView.findViewById(R.id.rvCalenderYear);
        final TextView tvCalenderYear = (TextView) promptsView.findViewById(R.id.tvCalenderYear);
        final TextView tvCalenderMonth = (TextView) promptsView.findViewById(R.id.tvCalenderMonth);

        final Spinner spinner1 = (Spinner) promptsView.findViewById(R.id.spinner1);


        ivAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(mActivity, TaskListActivity.class);
                startActivity(intent);
                alertDialog.dismiss();

            }
        });


        //   int curentmonth =calendarView.getCurrentDate().getMonth();
        //   int currentyear = calendarView.getCurrentDate().getYear();
        //  int currentdate = calendarView.getCurrentDate().getDay();

        //  calendarView.setCurrentDate(CalendarDay.from(currentyear, curentmonth, currentdate));
        // calendarView.setDateSelected(CalendarDay.from(currentyear, curentmonth, currentdate), true);

        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        // String formattedDate = df.format(currentDate);
        String formattedDate = DateFormat.getDateInstance(DateFormat.SHORT).format(currentDate);
        String[] finalcurrentdate = formattedDate.split("/");

        //  Utils.showLog("==FinalCuurrent"+formattedDate);

        int currentyear = Integer.valueOf(finalcurrentdate[2]);
        int currentmonth = Integer.valueOf(finalcurrentdate[1]);
        int currentdate = Integer.valueOf(finalcurrentdate[0]);

        // Utils.showLog("==currentmonth"+finalcurrentdate+"  "+currentmonth+"  "+currentyear);


        ArrayList<String> list = new ArrayList<String>();
        ArrayList<String> Finallist = new ArrayList<>();

        for (int i = 0; i < transactionlist.size(); i++) {

            String date = transactionlist.get(i).getTransactionDate();


            String[] datearry = date.split("-");

            int year = Integer.valueOf(datearry[2]);

            list.add(String.valueOf(year));

            Utils.showLog("-yearlist" + list);


            // Finallist = new ArrayList<String>();
            for (String dupWord : list) {
                if (!Finallist.contains(dupWord)) {
                    Finallist.addAll(Collections.singleton(dupWord));


                }
            }

        }


        final ArrayList<String> finalFinallist = Finallist;
        // finalFinallist.addAll(list);

        //  Utils.showLog("==yearlist"+Finallist);


        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                finalYear = Integer.parseInt(finalFinallist.get(position));
                int curentmonth = calendarView.getCurrentDate().getMonth();
                calendarView.setCurrentDate(CalendarDay.from(finalYear, curentmonth, 1));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

      /*  String[] Subject = new String[]{
                "Maths",
                "Hindi",
                "English",
                "Computer"
        };*/


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mActivity, R.layout.calender_spinner_year, Finallist);

        spinnerArrayAdapter.setDropDownViewResource(calender_spinner_year);

        spinner1.setAdapter(spinnerArrayAdapter);

        System.out.println("==========currentyear "+calendarView.getCurrentDate().getYear());
        spinner1.setSelection(((ArrayAdapter<String>)spinner1.getAdapter()).getPosition(String.valueOf(calendarView.getCurrentDate().getYear())));

        rvCalenderMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {
                        "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Transaction_Activity.this);
                builder.setTitle("Select Month");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        tvCalenderMonth.setText(items[item]);
                        tvCalenderMonth.setTextColor(getResources().getColor(R.color.text_color1));
                        CalenderMonth = tvCalenderMonth.getText().toString();
                        FinalMonth = item + 1;
                        calendarView.setCurrentDate(CalendarDay.from(finalYear, FinalMonth, 1));

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        //  calendarView.setCurrentDate(CalendarDay.from(finalYear,FinalMonth,1));


        //Utils.showLog("===list"+transactionlist.size());


        for (int i = 0; i < transactionlist.size(); i++) {

            String date = transactionlist.get(i).getTransactionDate();


            String[] datearry = date.split("-");

            Utils.showLog("getdate" + date);

            int month = Integer.valueOf(datearry[1]);
            int year = Integer.valueOf(datearry[2]);
            int date1 = Integer.valueOf(datearry[0]);

            // Utils.showLog("=== date1 " + date1 + "month " + month + " year " + year);
            //  String mm = Helper.getMonth(month);


            calendarView.setDateSelected(CalendarDay.from(year, month, date1), true);

            // calendarView.setDateSelected(CalendarDay.from(2019,8,23),true);


        }


        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                //  Utils.showLog("hello"+selected);

                if (!selected) {
                    calendarView.setDateSelected(date, true);


                    ArrayList<Transcation> Newtransactionlist = new ArrayList<>();


                    // Utils.showLog("newdatelist" + transactionlist.size());

                    for (int i = 0; i < transactionlist.size(); i++) {

                        //  Utils.showLog("==tempdate"+transactionlist.size());


                        String tempdate = getFormatedMonth(date.getDay()) + "-" + getFormatedMonth(date.getMonth()) + "-" + getFormatedMonth(date.getYear());


                        if (tempdate.equals(transactionlist.get(i).getTransactionDate())) {


                            Newtransactionlist.add(transactionlist.get(i));

                            //  Utils.showLog("==getdate"+Newtransactionlist);


                        }

                    }
                    if(transactionlist.size()>0){
                        openTransDateListDailog(Newtransactionlist);
                    }
                    else {
                        new CToast(Transaction_Activity.this).simpleToast("No data", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }


                } else {

                    calendarView.setDateSelected(date, false);
                }


            }
        });


        //calendarView.setCurrentDate(CalendarDay.from(currentyear,currentmonth,currentdate));

        Calendar cal = Calendar.getInstance();
        tvCalenderMonth.setText((String)android.text.format.DateFormat.format("MMMM", new Date()));
        tvCalenderMonth.setTextColor(getResources().getColor(R.color.text_color1));
        CalenderMonth = tvCalenderMonth.getText().toString();
        FinalMonth = cal.get(Calendar.MONTH) + 1;
        calendarView.setCurrentDate(CalendarDay.from(currentyear, FinalMonth, 1));

        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);


    }


    private void OpenDailogYear(TextView tvCalenderYear) {

        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dailog_calender_year, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();


        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);


    }

    private void openTransDateListDailog(ArrayList<Transcation> Newtransactionlist) {

        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dailog_calender_popup, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();


        final RecyclerView rvTransPaymentlist = (RecyclerView) promptsView.findViewById(R.id.rvTransPopUpList);
        final TextView btn_add_Trans = (TextView) promptsView.findViewById(R.id.btn_add_Trans);
        final TextView TransPopUpDate = (TextView) promptsView.findViewById(R.id.TransPopUpDate);
        final ImageView ivBack = (ImageView) promptsView.findViewById(R.id.ivBack);
        rvTransPaymentlist.setLayoutManager(new LinearLayoutManager(mActivity));
        rvTransPaymentlist.setHasFixedSize(true);
        adapterCalenderTransList = new Adapter_Calender_TransList(mActivity, Newtransactionlist, false , alertDialog);
        rvTransPaymentlist.setAdapter(adapterCalenderTransList);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // Utils.showLog("==size" + Newtransactionlist.size());

        if(Newtransactionlist.size()>0){
            TransPopUpDate.setText(Newtransactionlist.get(0).getTransactionDate());
        }
        else {

        }


      /*  btn_add_Trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(mActivity, Add_fab_Transaction_Activity.class));
                mActivity.overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

            }
        });*/


        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }


    private String getFormatedMonth(int month) {
        String tempMonth = String.valueOf(month);
        if (tempMonth.length() == 1) {
            tempMonth = "0" + tempMonth;
            return tempMonth;
        }
        return String.valueOf(month);
    }


    public void getCustomerList() {
        // swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.CUSTOMER + "?type=List", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                //       swipe_refresh.setRefreshing(false);
                try {
                    itemListCustomer.clear();
                    itemListCustomerTemp.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Customer List", response);

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("customers");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Customer_List item = new Item_Customer_List();
                            item.setId(object.getString("id"));
                            item.setName(object.getString("name"));
                            item.setCity(object.getString("city"));
                            item.setContact_no(object.getString("contact_no"));
                            item.setAnother_no(object.getString("contact_no1"));
                            item.setEmail(object.getString("email"));
                            item.setAddress(object.getString("address"));

                            itemListCustomer.add(item);
                            itemListCustomerTemp.add(item);
                        }
                        adapterTransactionCustList.notifyDataSetChanged();

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

    private void getAllTransactionlist() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query queryDocument = databaseReference.child("Transactions").orderByKey();
        queryDocument.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        transactionlist.clear();
                        transactionlistTemp.clear();
                        for (DataSnapshot npsnapshot5 : dataSnapshot.getChildren()) {
                            Transcation item1 = new Transcation();
                            item1.setTransactionId(npsnapshot5.child("transaction_id").getValue().toString());
                            item1.setCustomerId(npsnapshot5.child("customer_id").getValue().toString());
                            item1.setPaymentType(npsnapshot5.child("payment_type").getValue().toString());

                            try {
                                DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                                Date d = f.parse(npsnapshot5.child("transaction_date").getValue().toString());
                                DateFormat date2 = new SimpleDateFormat("dd-MM-yyyy");
                                String date22 = date2.format(d);
                                item1.setTransactionDate(date22);
                            }
                            catch (Exception e){
                                Log.e("DateFormate Ex ",e.getMessage());
                            }


                            item1.setTransactionNote(npsnapshot5.child("transaction_note").getValue().toString());
                            item1.setAmount(npsnapshot5.child("amount").getValue().toString());

                            Query queryCustomer = databaseReference.child("Transacation_Customers").orderByKey();
                            queryCustomer.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot npsnapshotCustomer : dataSnapshot.getChildren()) {
                                                if (npsnapshotCustomer.child("id").getValue().toString().equals(npsnapshot5.child("customer_id").getValue().toString())) {
                                                    String name = npsnapshotCustomer.child("name").getValue().toString();
                                                    item1.setCustomerName(name);
                                                }
                                            }
                                            adapterCalenderTransList.notifyDataSetChanged();
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
                        adapterCalenderTransList.notifyDataSetChanged();
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

    private void getAllTransactionlist1() {


        //  swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.All_TRANSACTION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //    swipe_refresh.setRefreshing(false);


                Log.w("url", API.All_TRANSACTION);
                Log.w("response", response);

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
                        adapterCalenderTransList.notifyDataSetChanged();

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
                //  swipe_refresh.setRefreshing(false);
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


    private boolean pushFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    //.setCustomAnimations(R.anim.feed_in, R.anim.feed_out)
                    .setCustomAnimations(0,0)
                    .replace(R.id.Trans_Cust_Fragment, fragment)
                    //.addToBackStack("fragment")
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // pushFragment(new TransActiveCustomer_Fragment());
        System.out.println("========onResume ");
        getAllTransactionlist();


        //getCustomerList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Transaction_Activity.this, Activity_Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }

  /*  @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }*/

   /* private void hideSwipeRefresh() {
        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
            swipe_refresh.setRefreshing(false);
        }
    }*/

}

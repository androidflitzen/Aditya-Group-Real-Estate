package com.flitzen.adityarealestate_new.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Fragment.TranPayment_Fragment;
import com.flitzen.adityarealestate_new.Fragment.TransActiveCustomer_Fragment;
import com.flitzen.adityarealestate_new.Fragment.TransReceived_Payment;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.Items.Item_Loan_Details;
import com.flitzen.adityarealestate_new.Items.Transcation;
import com.flitzen.adityarealestate_new.PDFUtility_Transaction;
import com.flitzen.adityarealestate_new.PDFUtility_Transaction_All;
import com.flitzen.adityarealestate_new.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionDetails_Activity extends AppCompatActivity {


    @BindView(R.id.Trans_Fragment)
    FrameLayout TransFragment;

    Context context;


    String customer_id, customer_name, city, email, contact_no1, contact_no, type1, type2;
    String file_url = "";
    int position;

    // ArrayList<Transcation> transactionlist;
    /* @BindView(R.id.tvTransDetNote)
     TextView tvTransDetNote;*/
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.tvTranstabPayment)
    TextView tvTranstabPayment;
    @BindView(R.id.tvTranstabRecevied)
    TextView tvTranstabRecevied;
    @BindView(R.id.liTabmain)
    LinearLayout liTabmain;
    @BindView(R.id.Trans_frame)
    FrameLayout TransFrame;
    /* @BindView(R.id.fab_add_Transaction)
     FloatingActionButton fabAddTransaction;*/
    @BindView(R.id.ivBack)
    ImageView ivBack;

   /* @BindView(R.id.swipe_refresh1)
    SwipeRefreshLayout swipeRefresh1;

*/

    ArrayList<Item_Customer_List> itemList = new ArrayList<>();
    ArrayList<Transcation> transactionlist = new ArrayList<>();
    ArrayList<Transcation> transactionlistTemp = new ArrayList<>();

    ArrayList<Transcation> transactionlistAll = new ArrayList<>();
    ArrayList<Transcation> transactionlistTempAll = new ArrayList<>();

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    String uniqID;
    int action, selectedPosition;

    public static TextView tvTitle, tvTansCustName, tvTransCustMobile, tvTransCustMobile1, tvTransCustEmailId, tvTransCustCity;


    public static TextView tvTransCustPayment;

    public static TextView tvTransCustReceived;
    String ReceicedTotal="0";
    String PaymentTotltal="0";

    @BindView(R.id.ivTransEditProfile)
    RelativeLayout ivTransEditProfile;
    @BindView(R.id.ivPdfAll)
    RelativeLayout ivPdfAll;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details_);
        ButterKnife.bind(this);


        context = TransactionDetails_Activity.this;

        initUI();


    }

    private void initUI() {

        tvTansCustName = findViewById(R.id.tvTansCustName);
        tvTransCustMobile = findViewById(R.id.tvTransCustMobile);
        tvTransCustMobile1 = findViewById(R.id.tvTransCustMobile1);
        tvTransCustEmailId = findViewById(R.id.tvTransCustEmailId);
        tvTransCustCity = findViewById(R.id.tvTransCustCity);
        tvTitle = findViewById(R.id.tvTitle);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushFragment(new TransActiveCustomer_Fragment());
                onBackPressed();
            }
        });

        ivPdfAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                if (!Network.isNetworkAvailable(context)) {
                    return;
                }
                action = 0;
                selectedPosition = position;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Permission.hasPermissions((Activity) context, permissions)) {
                        downloadFile( action);
                    } else {
                        Permission.requestPermissions(TransactionDetails_Activity.this, permissions,101);
                    }
                } else {
                    downloadFile( action);
                } */

              /*  file_url = API.TRANSACTION_BOTHPDF + "customer_id=" + customer_id;
                startActivity(new Intent(context, PdfViewActivity.class)
                        .putExtra("pdf_url", file_url));
                Utils.showLog("=== file_url " + file_url);*/

                int finalTotalAmount=0;
                for (int i = 0; i < transactionlistAll.size(); i++) {
                    if(transactionlistAll.get(i).getAmount()!=null && !(transactionlistAll.get(i).getAmount().equals(""))){
                        finalTotalAmount=finalTotalAmount+Integer.parseInt(transactionlistAll.get(i).getAmount());
                    }
                }

                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                path = Environment.getExternalStorageDirectory().toString() + "/" + ts + "_payment_list.pdf";
                System.out.println("========path  " + path);
                try {
                    PDFUtility_Transaction_All.createPdf(v.getContext(), new PDFUtility_Transaction_All.OnDocumentClose() {
                        @Override
                        public void onPDFDocumentClose(File file) {
                           // Toast.makeText(TransactionDetails_Activity.this, "Sample Pdf Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TransactionDetails_Activity.this, ViewPdfForAll.class);
                            intent.putExtra("path",path);
                            startActivity(intent);
                        }
                    }, getSampleData(), path, true, customer_name, contact_no,finalTotalAmount);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", "Error Creating Pdf");
                    Toast.makeText(v.getContext(), "Error Creating Pdf", Toast.LENGTH_SHORT).show();
                }

            }
        });

        tvTransCustReceived = (TextView) findViewById(R.id.tvTransCustReceived);
        tvTransCustPayment = (TextView) findViewById(R.id.tvTransCustPayment);


        customer_id = getIntent().getStringExtra("customer_id");
        customer_name = getIntent().getStringExtra("customer_name");
        contact_no = getIntent().getStringExtra("contact_no");
        contact_no1 = getIntent().getStringExtra("contact_no1");
        email = getIntent().getStringExtra("email");
        city = getIntent().getStringExtra("city");

        type1 = "0";
        type2 = "1";

        Utils.showLog("==customer_name" + customer_id);


        if (!customer_name.equals("")) {
            tvTansCustName.setText(customer_name);
        } else {
            tvTansCustName.setText("--------");
        }

        if (!contact_no1.equals("")) {
            tvTransCustMobile1.setText(contact_no1);
        } else {
            tvTransCustMobile1.setText("--------");
        }

        if (!contact_no.equals("")) {
            tvTransCustMobile.setText(contact_no);
        } else {
            tvTransCustMobile.setText("--------");
        }

        if (!email.equals("")) {
            tvTransCustEmailId.setText(email);
        } else {
            tvTransCustEmailId.setText("--------");
        }

        if (!city.equals("")) {
            tvTransCustCity.setText(city);
        } else {
            tvTransCustCity.setText("--------");
        }

        if (!city.equals("")) {
            tvTransCustCity.setText(city);
        } else {
            tvTransCustCity.setText("--------");
        }


        tvTitle.setText(customer_name);
        //  tvTansCustName.setText(customer_name);
        //  tvTransCustMobile.setText(contact_no);
        //  tvTransCustMobile1.setText(contact_no1);
        //  tvTransCustEmailId.setText(email);
        //  tvTransCustCity.setText(city);


        tvTransCustMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + tvTransCustMobile.getText().toString()));
                startActivity(intent);
            }
        });

        tvTransCustMobile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + tvTransCustMobile1.getText().toString()));
                startActivity(intent);
            }
        });


        final int position = getIntent().getIntExtra("position", 0);


        tvTranstabPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvTranstabPayment.setBackground(getResources().getDrawable(R.drawable.task_bg_title));
                tvTranstabRecevied.setBackground(getResources().getDrawable(R.drawable.trans_tab_bg));
                pushFragment(new TranPayment_Fragment(customer_id, position, customer_name,contact_no));
                //   pushFragment(new TranPayment_Fragment(customer_id, position), "");
            }
        });

        tvTranstabRecevied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                tvTranstabPayment.setBackground(getResources().getDrawable(R.drawable.trans_tab_bg));
                tvTranstabRecevied.setBackground(getResources().getDrawable(R.drawable.task_bg_title));
                //    tvTitleTask.setText("Pending Task");
                pushFragment(new TransReceived_Payment(customer_id, position, customer_name,contact_no));

            }
        });


        ivTransEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionDetails_Activity.this, TransEditDetail_Activity.class);
               /* intent.putExtra("customer_id", customer_id);
                intent.putExtra("customer_name", customer_name);
                intent.putExtra("contact_no", contact_no);
                intent.putExtra("contact_no1", contact_no1);
                intent.putExtra("email", email);
                intent.putExtra("city", city);  */

                intent.putExtra("customer_id", customer_id);
                if(tvTansCustName.getText().toString().equals("--------")){
                    intent.putExtra("customer_name", "");
                }
                else {
                    intent.putExtra("customer_name", tvTansCustName.getText().toString());
                }

                if(tvTransCustMobile.getText().toString().equals("--------")){
                    intent.putExtra("contact_no", "");
                }
                else {
                    intent.putExtra("contact_no", tvTransCustMobile.getText().toString());
                }

                if(tvTransCustMobile1.getText().toString().equals("--------")){
                    intent.putExtra("contact_no1", "");
                }
                else {
                    intent.putExtra("contact_no1", tvTransCustMobile1.getText().toString());
                }

                if(tvTransCustEmailId.getText().toString().equals("--------")){
                    intent.putExtra("email", "");
                }
                else {
                    intent.putExtra("email", tvTransCustEmailId.getText().toString());
                }

                if(tvTransCustCity.getText().toString().equals("--------")){
                    intent.putExtra("city", "");
                }
                else {
                    intent.putExtra("city", tvTransCustCity.getText().toString());
                }


                startActivity(intent);
            }
        });

       /* ivTransPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                file_url = API.TRANSACTION_DETAILPDF + "customer_id=" + customer_id;
                startActivity(new Intent(TransactionDetails_Activity.this, PdfViewActivity.class)
                        .putExtra("pdf_url", file_url));
                Utils.showLog("=== file_url " + file_url);

            }
        });

*/
       /* fabAddTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(context, Add_fab_Transaction_Activity.class);
                intent.putExtra("customer_id", customer_id);
                intent.putExtra("customer_name", customer_name);
                intent.putExtra("type1", type1);
                intent.putExtra("type2", type2);

                context.startActivity(intent);


             *//*   startActivity(new Intent(TransactionDetails_Activity.this, Add_fab_Transaction_Activity.class));
                TransactionDetails_Activity.this.overridePendingTransition(R.anim.feed_in, R.anim.feed_out);*//*
            }
        });*/


    }

    private void downloadFile(int action) {

      /*  uniqID = transactionlist.get(position).getTransactionId();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        File myFile = new File(new File(Utils.getItemDir()), transactionlist.get(position).getTransactionId() + ""+transactionlist.get(position).getCustomerName() + ".pdf");
        try {
            if (myFile.exists()) {
                if (action == 0) {
                    openPDFFile(myFile);
                } else if (action == 1) {
                    sendMailInvoice(myFile);
                }
            } else {
                myFile.createNewFile();
                new DownloadFileFromURL(myFile, action).execute(transactionlist.get(position).g());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } */

    }

    private List<String[]> getSampleData() {

        List<String[]> temp = new ArrayList<>();
        int finalTotalAmount=0;
        for (int i = 0; i < transactionlistAll.size(); i++) {

            String data1="";
            String data2="";
            String data3="";
            String data4="";


            if (transactionlistAll.get(i).getTransactionDate() != null) {
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
                // SimpleDateFormat inputT = new SimpleDateFormat("hh:mm:ss");
                SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
                // SimpleDateFormat outputT = new SimpleDateFormat("hh:mm a");
                try {
                    Date oneWayTripDate;
                    Date oneWayTripDateT;
                    oneWayTripDate = input.parse(transactionlistAll.get(i).getTransactionDate());  // parse input
                    // oneWayTripDateT = inputT.parse(transactionlist.get(i).getTransactionTime());  // parse input
                    data1=(output.format(oneWayTripDate));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            data2=transactionlistAll.get(i).getTransactionNote();

            if(transactionlistAll.get(i).getPaymentType()!=null){
                if(transactionlistAll.get(i).getPaymentType().equals("1")){
                    data4=(getResources().getString(R.string.rupee)+Helper.getFormatPrice(Integer.parseInt(transactionlistAll.get(i).getAmount())));
                    data3="";
                }
                else {
                    data4="";
                    data3=(getResources().getString(R.string.rupee)+Helper.getFormatPrice(Integer.parseInt(transactionlistAll.get(i).getAmount())));
                }
            }

            temp.add(new String[] {data1,data2,data3,data4});
        }
        return temp;
    }


    private void getReceivedlist() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query queryDocument = databaseReference.child("Transactions").orderByKey();
        queryDocument.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        transactionlist.clear();
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
                                }
                            }
                        }
                        if(ReceicedTotal.equals("0")){
                            TransactionDetails_Activity.tvTransCustReceived.setText("--------");
                        }
                        else {
                            TransactionDetails_Activity.tvTransCustReceived.setText(ReceicedTotal);
                        }
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

    private void getAllPaymentList() {

        System.out.println("=========getPaymentlist   "+customer_id);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query queryDocument = databaseReference.child("Transactions").orderByKey();
        queryDocument.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        transactionlistAll.clear();
                        transactionlistTempAll.clear();
                        int PaymentTotltal1=0;
                        for (DataSnapshot npsnapshot5 : dataSnapshot.getChildren()) {
                            if (npsnapshot5.child("customer_id").getValue().toString().equals(customer_id)) {
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
                                    transactionlistAll.add(item1);
                                    transactionlistTempAll.add(item1);

                            }
                        }
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

        Utils.showLog("==custmerid" + customer_id);
        // swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TRANSACTION_DETAILSRECEIVED + " &customer_id=" + customer_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //     swipe_refresh.setRefreshing(false);


                try {
                    transactionlist.clear();
                    //  transactionlistTemp.clear();


                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {


                        ReceicedTotal = jsonObject.getString("total_received");
                        TransactionDetails_Activity.tvTransCustReceived.setText(ReceicedTotal);


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
                            //transactionlistTemp.add(item1);


                        }
                        //adapterTransPaymentList.notifyDataSetChanged();

                    } else {
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);


    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getReceivedlist();
        getAllPaymentList();

        tvTranstabPayment.setBackground(getResources().getDrawable(R.drawable.task_bg_title));
        tvTranstabRecevied.setBackground(getResources().getDrawable(R.drawable.trans_tab_bg));
        pushFragment(new TranPayment_Fragment(customer_id, position, customer_name,contact_no));

        if (Edit_Transaction_Activity.isDateChange) {
            Edit_Transaction_Activity.isDateChange = false;
            //  tvTransDetDate.setText(Edit_Transaction_Activity.finaldate);
        } else {
            Edit_Transaction_Activity.isDateChange = false;
            //  tvTransDetDate.setText(transaction_date);
        }
        // tvTransDetDate.setText(Edit_Transaction_Activity.finaldate);
    }

    private boolean pushFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    //.setCustomAnimations(R.anim.feed_in, R.anim.feed_out)
                    .setCustomAnimations(0,0)
                    .replace(R.id.Trans_Fragment, fragment)
                    //.addToBackStack("fragment")
                    .commit();
            return true;
        }
        return false;
    }

    private void hideSwipeRefresh() {
        if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }
}

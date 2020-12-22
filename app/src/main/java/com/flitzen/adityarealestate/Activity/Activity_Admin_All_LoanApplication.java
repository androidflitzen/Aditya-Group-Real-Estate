package com.flitzen.adityarealestate.Activity;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate.Adapter.Adapter_All_Loan_Applications;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Items.Iteams_All_Loan_Application;
import com.flitzen.adityarealestate.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate.R;
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
import org.json.JSONObject;

import java.util.ArrayList;

public class Activity_Admin_All_LoanApplication extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Activity mActivity;
    Adapter_All_Loan_Applications mAdapter_new;
    private RecyclerView rec_applicant_admin;
    private ArrayList<Iteams_All_Loan_Application> itemArray_new = new ArrayList<>();
    private ArrayList<Iteams_All_Loan_Application> itemArray_Temp = new ArrayList<>();
    private EditText edtSearch;
    private FloatingActionButton fabAddNewLoan;
    private SwipeRefreshLayout swipe_refresh;

    public static boolean REFRESH = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_admin_allloan_applicant);

        getSupportActionBar().setTitle(Html.fromHtml("All Loan Applications"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Admin_All_LoanApplication.this;

        rec_applicant_admin = (RecyclerView) findViewById(R.id.rec_applicant_admin);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_all_loan);
        fabAddNewLoan = (FloatingActionButton) findViewById(R.id.fab_add_new_loan);
        edtSearch = (EditText) findViewById(R.id.edt_admin_loan_application_search);

        //rec_applicant_admin.setOnScrollListener(onScrollListener());
        swipe_refresh.setOnRefreshListener(this);
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);

        rec_applicant_admin.setHasFixedSize(true);
        rec_applicant_admin.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter_new = new Adapter_All_Loan_Applications(getApplicationContext(), itemArray_Temp);
        rec_applicant_admin.setAdapter(mAdapter_new);

        mAdapter_new.OnItemLongClickListener(new Adapter_All_Loan_Applications.OnItemLongClickListener() {
            @Override
            public void onItemClick(int position) {
                DeleteLoan(position);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchWord = edtSearch.getText().toString().trim().toLowerCase();
                if (searchWord.length() > 0) {

                    itemArray_Temp.clear();
                    for (int i = 0; i < itemArray_new.size(); i++) {
                        if (itemArray_new.get(i).getAll_loan_applicant_name().toLowerCase().contains(searchWord) ||
                                itemArray_new.get(i).getAll_loan_applicant_number().toLowerCase().contains(searchWord) ||
                                itemArray_new.get(i).getAll_loan_applicant_status().toLowerCase().contains(searchWord) ||
                                itemArray_new.get(i).getAll_loan_applicant_amount().toLowerCase().contains(searchWord)) {
                            itemArray_Temp.add(itemArray_new.get(i));
                        }
                    }
                    /*mAdapter_new = new Adapter_All_Loan_Applications(mActivity, itemArray_Temp);
                    lst_applicant.setAdapter(mAdapter_new);*/
                    mAdapter_new.notifyDataSetChanged();
                } else {
                    /*mAdapter_new = new Adapter_All_Loan_Applications(mActivity, itemArray_new);
                    lst_applicant.setAdapter(mAdapter_new);*/

                    mAdapter_new.notifyDataSetChanged();
                }
            }
        });

        SET_DATA();

        fabAddNewLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, Activity_Add_New_Loan.class));
            }
        });
    }

    public void DeleteLoan(final int position) {

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(30);
        new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                .setTitle("Delete Loan")
                .setMessage(Html.fromHtml("Delete <b>" + itemArray_Temp.get(position).getAll_loan_applicant_name() + "'s</b> <i>#" + itemArray_Temp.get(position).getAll_loan_applicant_number() + "</i> loan ?"))
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        System.out.println("==========DeleteLoan");
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        Query query = databaseReference.child("EMI_Received").orderByKey();
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                                            if(npsnapshot.child("Loan_Id").getValue().toString().equals(itemArray_Temp.get(position).getAll_loan_loan_id())){
                                                npsnapshot.getRef().removeValue();

                                            }
                                        }
                                        SET_DATA();
                                    }
                                } catch (Exception e) {
                                    Log.e("Test  ", e.getMessage());
                                    e.printStackTrace();
                                    swipe_refresh.setRefreshing(false);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                                new CToast(Activity_Admin_All_LoanApplication.this).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                swipe_refresh.setRefreshing(false);
                            }
                        });


                        databaseReference.child("LoanDetails").child(itemArray_Temp.get(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    databaseReference.child("LoanDetails").child(itemArray_Temp.get(position).getKey()).removeValue().addOnCompleteListener(Activity_Admin_All_LoanApplication.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                new CToast(Activity_Admin_All_LoanApplication.this).simpleToast("Loan delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                SET_DATA();
                                            } else {
                                                new CToast(Activity_Admin_All_LoanApplication.this).simpleToast(task.getException().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                            }

                                        }
                                    }).addOnFailureListener(Activity_Admin_All_LoanApplication.this, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            new CToast(Activity_Admin_All_LoanApplication.this).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                        }
                                    });
                                } else {
                                    new CToast(Activity_Admin_All_LoanApplication.this).simpleToast("Loan not exist", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                new CToast(Activity_Admin_All_LoanApplication.this).simpleToast(error.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }
                        });
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    public void SET_DATA() {

        swipe_refresh.setRefreshing(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("LoanDetails").orderByKey();
        //databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                swipe_refresh.setRefreshing(false);
                itemArray_new.clear();
                itemArray_Temp.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            Iteams_All_Loan_Application item = new Iteams_All_Loan_Application();
                            item.setAll_loan_loan_id(npsnapshot.child("id").getValue().toString());
                            item.setKey(npsnapshot.getKey());

                            item.setAll_loan_applicant_number(npsnapshot.child("Applicantion_Number").getValue().toString());
                            String loanStatus = npsnapshot.child("Loan_Status").getValue().toString();
                            if (loanStatus.equals("1")) {
                                item.setAll_loan_applicant_status("Pending");
                            } else if (loanStatus.equals("2")) {
                                item.setAll_loan_applicant_status("Approved");
                            } else if (loanStatus.equals("3")) {
                                item.setAll_loan_applicant_status("Rejected");
                            }else if (loanStatus.equals("4")) {
                                item.setAll_loan_applicant_status("Completed");
                            }

                            String loanType = npsnapshot.child("Loan_Type").getValue().toString();
                            if (loanType.equals("1")) {
                                item.setAll_loan_applicant_loan_type("Regular");
                            } else if (loanType.equals("2")) {
                                item.setAll_loan_applicant_loan_type("EMI");
                            } else if (loanType.equals("3")) {
                                item.setAll_loan_applicant_loan_type("Daily");
                            }

                            item.setAll_loan_applicant_approve_date(npsnapshot.child("Approved_Date").getValue().toString());
                            item.setAll_loan_applicant_amount(npsnapshot.child("Loan_Amount").getValue().toString());
                            item.setAll_loan_applicant_tenure(npsnapshot.child("Loan_Tenure").getValue().toString());
                            item.setAll_loan_applicant_approve_amount(npsnapshot.child("Approved_Amount").getValue().toString());
                            item.setAll_loan_applicant_applied_date(npsnapshot.child("Date_Applied").getValue().toString());

                            String customerId = npsnapshot.child("Customer_Id").getValue().toString();

                            Query query = databaseReference.child("Customers").orderByKey();
                            // databaseReference.keepSynced(true);
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                if (npsnapshot1.child("id").getValue().toString().equals(customerId)) {
                                                    String name = npsnapshot1.child("name").getValue().toString();
                                                    Log.e("Name  ", name);
                                                    item.setAll_loan_applicant_name(name);
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("Ex   ", e.toString());
                                    }
                                    mAdapter_new.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("error ", error.getMessage());
                                }
                            });
                            itemArray_new.add(item);
                            itemArray_Temp.add(item);
                        }
                        mAdapter_new.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.e("Test  ", e.getMessage());
                    e.printStackTrace();
                    swipe_refresh.setRefreshing(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                new CToast(Activity_Admin_All_LoanApplication.this).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                swipe_refresh.setRefreshing(false);
            }
        });
    }

    public void SET_DATA1() {

        swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.GET_ALL_APPLICANTS + "?type=Loans", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //hidePrd();
                swipe_refresh.setRefreshing(false);
                try {
                    itemArray_new.clear();
                    itemArray_Temp.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Sites List", response);

                    if (jsonObject.getString("result").equals("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Loans");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Iteams_All_Loan_Application item = new Iteams_All_Loan_Application();
                            item.setAll_loan_applicant_id(object.getString("id"));
                            item.setAll_loan_applicant_name(object.getString("Applicant_Name"));
                            item.setAll_loan_applicant_number(object.getString("Applicantion_Number"));
                            item.setAll_loan_applicant_status(object.getString("Loan_Status"));
                            //item.setSite_price(object.getString("Reject_Remarks"));
                            item.setAll_loan_applicant_approve_date(object.getString("Approved_Date"));
                            //item.setSite_price(object.getString("Pay_EMI_Date"));
                            item.setAll_loan_applicant_amount(object.getString("Loan_Amount"));
                            item.setAll_loan_applicant_tenure(object.getString("Loan_Tenure"));
                            item.setAll_loan_applicant_loan_type(object.getString("Loan_Type"));
                            item.setAll_loan_applicant_approve_amount(object.getString("Approved_Amount"));
                            //item.setAll_loan_applicant_approve_amount(object.getString("Original_Amount"));
                            //item.setSite_price(object.getString("Interest_Rate"));
                            //item.setSite_price(object.getString("Payable_Amount"));
                            //item.setSite_price(object.getString("Reason_For_Loan"));
                            item.setAll_loan_applicant_applied_date(object.getString("Date_Applied"));

                            itemArray_new.add(item);
                            itemArray_Temp.add(item);
                        }

                        /*mAdapter_new = new Adapter_All_Loan_Applications(mActivity, itemArray_new);
                        lst_applicant.setAdapter(mAdapter_new);*/

                        mAdapter_new.notifyDataSetChanged();

                    } else {
                        new CToast(getApplicationContext()).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hidePrd();
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

    @Override
    public void onRefresh() {
        SET_DATA();
    }

    /*public AbsListView.OnScrollListener onScrollListener() {
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int s = 0;
                if (firstVisibleItem == 0) {
                    View v = lst_applicant.getChildAt(0);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        //Log.i("Sroll", "top reached");
                        swipe_refresh.setEnabled(true);
                    }
                } else if (totalItemCount - visibleItemCount == firstVisibleItem) {
                    View v = lst_applicant.getChildAt(totalItemCount - 1);
                    int offset = (v == null) ? 0 : v.getTop();
                    if (offset == 0) {
                        //Log.i("Sroll", "bottom reached!");
                        swipe_refresh.setEnabled(false);
                    }
                } else if (totalItemCount - visibleItemCount > firstVisibleItem) {
                    // on scrolling
                    swipe_refresh.setEnabled(false);
                }
            }
        };
    }*/

    @Override
    public void onResume() {
        super.onResume();
        if (REFRESH) {
            swipe_refresh.setRefreshing(true);
            SET_DATA();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Activity_Admin_All_LoanApplication.this, Activity_Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}

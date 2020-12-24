package com.flitzen.adityarealestate_new.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.flitzen.adityarealestate_new.Activity.Activity_Customer_Add;
import com.flitzen.adityarealestate_new.Activity.Activity_Rent_List;
import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Items.Customer;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RentCustomerAdapter extends RecyclerView.Adapter<RentCustomerAdapter.ViewHolder> {

    ArrayList<Customer> itemList = new ArrayList<>();

    Activity context;
    OnItemClickListener mItemClickListener;
    ProgressDialog prd;


    public RentCustomerAdapter(Activity context, ArrayList<Customer> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_rent_customer_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.txt_cust_name.setText(itemList.get(position).getCustomer_name());

        if(itemList.get(position).getRent_status().equals("0")){
            holder.txt_status.setText("Current Customer");
            holder.txt_status.setTextColor(context.getResources().getColor(R.color.color_green));
            holder.ivRentHistoryDelete.setImageResource(R.drawable.ic_person_remove);
            holder.ivRentHistoryDelete.setPadding(1, 1, 1, 1);
            holder.ivRentHistoryDelete.setVisibility(View.VISIBLE);
            holder.ivRentHistoryDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure want to remove this active customer?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Log.e("unAssign", "Property");
                                    Log.e("cusomer id", itemList.get(position).getCustomer_id());
                                    Log.e("cusomer name", itemList.get(position).getCustomer_name());
                                    unAssignProperty(itemList.get(position).getCustomer_id().toString(), Aditya.ID, position);
                                }
                            });

                    builder.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
        else {
            holder.txt_status.setText("Old Customer");

            holder.ivRentHistoryDelete.setVisibility(View.VISIBLE);
            holder.ivRentHistoryDelete.setPadding(8, 8, 8, 8);
            holder.ivRentHistoryDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDeleteDialog(position);
                }
            });


            holder.txt_status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }

        holder.view_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
            }
        });
    }

    private void openDeleteDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure want delete this deactive customer?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteEntryAPI(position);
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void deleteEntryAPI(final int position) {

        showPrd();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Payments");

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    if(appleSnapshot.child("property_id").getValue().toString().equals(itemList.get(position).getProperty_id())){
                        if(appleSnapshot.child("customer_id").getValue().toString().equals(itemList.get(position).getCustomer_id())){
                            appleSnapshot.getRef().removeValue();
                        }
                    }
                    new CToast(context).simpleToast("Delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
                Log.e("Cancel  ", "onCancelled", databaseError.toException());
            }
        });
    }

    private void deleteEntryAPI1(final int position) {

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.RENTHISTRYDEAVTIVE_DELETE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {
                        itemList.remove(position);
                        notifyDataSetChanged();
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hidePrd();
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

                Utils.showLog("===1" + itemList.get(position).getProperty_id() + " custerid" + itemList.get(position).getCustomer_id());
                Map<String, String> params = new HashMap<>();
                params.put("customer_id", itemList.get(position).getCustomer_id());
                params.put("property_id", itemList.get(position).getProperty_id());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        System.out.println("========itemList  "+itemList.size());
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_cust_name, txt_status;
        RelativeLayout view_main;
        ImageView ivRentHistoryDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_cust_name = (TextView) itemView.findViewById(R.id.txt_cust_name);
            txt_status = (TextView) itemView.findViewById(R.id.txt_status);
            ivRentHistoryDelete = (ImageView) itemView.findViewById(R.id.ivRentHistoryDelete);
            view_main = (RelativeLayout) itemView.findViewById(R.id.view_main);
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    public void showPrd() {
        prd = new ProgressDialog(context);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }


    public void unAssignProperty(final String customer_id, final String id, final int position) {
        showPrd();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        Query queryPro = ref.child("Properties");
        queryPro.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    if(appleSnapshot.child("id").getValue().toString().equals(id)){

                        String key = ref.child("Property_History").push().getKey();
                        Map<String, Object> map = new HashMap<>();
                        map.put("customer_id", customer_id);
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        map.put("hired_end", currentDate);
                        map.put("id", key);
                        map.put("hired_since", appleSnapshot.child("hired_since").getValue().toString());
                        map.put("property_id", id);
                        map.put("remarks", "");
                        map.put("rent",  appleSnapshot.child("rent").getValue().toString());

                        ref.child("Property_History").child(key).setValue(map).addOnCompleteListener(context, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            }

                        }).addOnFailureListener(context, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hidePrd();
                                Toast.makeText(context, "Please try later...", Toast.LENGTH_SHORT).show();
                            }

                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Cancel  ", "onCancelled", databaseError.toException());
            }
        });



        Query removeDoc = ref.child("RantDocument");
        removeDoc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    if(appleSnapshot.child("p_id").getValue().toString().equals(id)){
                        if(appleSnapshot.child("customer_id").getValue().toString().equals(customer_id)){
                            appleSnapshot.getRef().removeValue();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Cancel  ", "onCancelled", databaseError.toException());
            }
        });


            Query query = ref.child("Properties").orderByKey();
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot npsnapshot) {
                    hidePrd();
                    try {
                        if (npsnapshot.exists()) {
                            for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {
                                if (dataSnapshot.child("id").getValue().toString().equals(id)) {
                                    DatabaseReference cineIndustryRef = ref.child("Properties").child(dataSnapshot.getKey());
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("rent", "");
                                    map.put("customer_id", 0);
                                    map.put("hired_since", "0000-00-00");
                                    Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("exception   ",e.toString());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    hidePrd();
                    Log.e("databaseError   ",databaseError.getMessage());
                }
            });

        Query applesQuery = ref.child("Payments");
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    if(appleSnapshot.child("property_id").getValue().toString().equals(id)){
                            DatabaseReference cineIndustryRef = ref.child("Payments").child(appleSnapshot.getKey());
                            Map<String, Object> map = new HashMap<>();
                            map.put("rent_status", 1);
                            Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    new CToast(context).simpleToast("Property unassigned successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                    Intent intent = new Intent(context, Activity_Rent_List.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Cancel  ", "onCancelled", databaseError.toException());
            }
        });
    }

    public void unAssignProperty1(final String customer_id, final String id, final int position) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.UNASSIGN_CUSTOMER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Property Unassign", response);
                    if (jsonObject.getInt("result") == 1) {
                        //finish();
//                        itemList.remove(position);
//                        notifyDataSetChanged();
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        Intent intent = new Intent(context, Activity_Rent_List.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        notifyDataSetChanged();
                    } else {
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }
}

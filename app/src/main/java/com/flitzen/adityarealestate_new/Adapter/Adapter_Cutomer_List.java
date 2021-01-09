package com.flitzen.adityarealestate_new.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.flitzen.adityarealestate_new.Classes.SharePref;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Adapter_Cutomer_List extends RecyclerView.Adapter<Adapter_Cutomer_List.ViewHolder> {

    ArrayList<Item_Customer_List> itemList = new ArrayList<>();
    Activity context;
    OnItemClickListener mItemClickListener;
    ProgressDialog progressDialog;
    boolean deleteEntry;
    public Adapter_Cutomer_List(Activity context, ArrayList<Item_Customer_List> itemList,boolean deleteEntry) {
        this.context = context;
        this.itemList = itemList;
        this.deleteEntry=deleteEntry;
        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_customer_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.txt_cust_name.setText(itemList.get(position).getName());
        holder.txt_cust_no.setText(itemList.get(position).getContact_no());
        if (deleteEntry) {
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivPopUp.setChecked(false);

            holder.ivPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // OpenActiveSite(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to activate this customer ?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    customerActivation(position,0);
                                }
                            });

                    builder.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    holder.ivPopUp.setChecked(false);
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
        else {
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.ivPopUp.setChecked(true);

            holder.ivPopUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Are you sure you want to deactivate this customer ?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //holder.ivPopUp.setChecked(false);
                                    customerActivation(position,1);
                                }
                            });

                    builder.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    holder.ivPopUp.setChecked(true);
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    //opendailogDeactive(position);
                }
            });

        }
        holder.view_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfDialog(position);
            }
        });

    }

    private void customerActivation(int position,int status){
        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Customers").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(itemList.get(position).getId())) {

                                DatabaseReference cineIndustryRef = databaseReference.child("Customers").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("status", status);
                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        Query queryPay = databaseReference.child("Payments").orderByChild("customer_id").equalTo(itemList.get(position).getId());
                                        databaseReference.keepSynced(true);
                                        queryPay.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot npsnapshot) {
                                                hidePrd();
                                                try {
                                                    if (npsnapshot.exists()) {
                                                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {
                                                            DatabaseReference cineIndustryRef = databaseReference.child("Payments").child(dataSnapshot.getKey());
                                                            Map<String, Object> map = new HashMap<>();
                                                            map.put("customer_status", status);

                                                            Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    hidePrd();
                                                                    new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                                }
                                                            });
                                                        }
                                                        hidePrd();
                                                        if(status==1){
                                                            new CToast(context).simpleToast("Customer deactivate successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                        }
                                                        else {
                                                            new CToast(context).simpleToast("Customer activate successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                        }
                                                        //    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                                                        itemList.remove(position);
                                                        notifyDataSetChanged();
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

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hidePrd();
                                        new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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

    private void openConfDialog(final int positionList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want delete this customer?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                deleteCustomer(positionList);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_cust_name, txt_cust_no;
        View view_main;
        ImageView ivDelete;
        SwitchMaterial ivPopUp;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_cust_name = (TextView) itemView.findViewById(R.id.txt_cust_name);
            txt_cust_no = (TextView) itemView.findViewById(R.id.txt_cust_no);
            view_main = itemView.findViewById(R.id.view_main);
            ivDelete=itemView.findViewById(R.id.ivDelete);
            ivPopUp=itemView.findViewById(R.id.ivPopUp);
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    public void deleteCustomer(final int position) {
        showPrd();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Customers").child(itemList.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    databaseReference.child("Customers").child(itemList.get(position).getId()).removeValue().addOnCompleteListener(context, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hidePrd();
                            if(task.isSuccessful()){
                                new CToast(context).simpleToast("Customer delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                itemList.remove(position);
                                notifyDataSetChanged();
                            }
                            else {
                                new CToast(context).simpleToast(task.getException().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }

                        }
                    }).addOnFailureListener(context, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hidePrd();
                            new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        }
                    });
                }
                else {
                    hidePrd();
                    new CToast(context).simpleToast("Customer not exist", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hidePrd();
                new CToast(context).simpleToast(error.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
            }
        });

    }

    public void deleteCustomer1(final int position) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.DELETE_FINAL_CUSTOMER + "customer_id="+itemList.get(position).getId(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("result") == 1) {
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        itemList.remove(position);
                        notifyDataSetChanged();
                    } else {
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    new CToast(context).simpleToast("something went wrong.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();
                new CToast(context).simpleToast("something went wrong.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }

    public void showPrd(){
        if (progressDialog!=null && !progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    public void hidePrd(){
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}

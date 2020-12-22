package com.flitzen.adityarealestate.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate.Activity.Activity_Sites_Purchase_Summary;
import com.flitzen.adityarealestate.Activity.TransactionDetails_Activity;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Items.Trans_Customer_List;
import com.flitzen.adityarealestate.Items.Transcation;
import com.flitzen.adityarealestate.R;
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

public class Adapter_TransactionCustList extends RecyclerView.Adapter<Adapter_TransactionCustList.ViewHolder> {


    ArrayList<Trans_Customer_List> ListTransCustomer = new ArrayList<>();
    ArrayList<Transcation> transactionlist = new ArrayList<>();
    Context context;
    boolean value;

    ProgressDialog prd;

    public Adapter_TransactionCustList(Context context, ArrayList<Trans_Customer_List> ListTransCustomer, Boolean value) {
        this.context = context;
        this.ListTransCustomer = ListTransCustomer;
        this.value = value;

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_transaction_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.ivDelete.setChecked(true);
        holder.txt_cust_name.setText(ListTransCustomer.get(position).getName());
        holder.txt_cust_no.setText(ListTransCustomer.get(position).getContact_no());

        holder.txt_cust_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+holder.txt_cust_no.getText().toString()));
                context.startActivity(intent);
            }
        });

        holder.ivDelete.setVisibility(View.VISIBLE);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ivDelete.setVisibility(View.VISIBLE);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Deactive this Customer ?");
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
                                holder.ivDelete.setChecked(true);
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                //openDeleteDialog(position);
            }
        });

        holder.liMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TransactionDetails_Activity.class);
                intent.putExtra("customer_id", ListTransCustomer.get(position).getId());
                intent.putExtra("customer_name", ListTransCustomer.get(position).getName());
                intent.putExtra("contact_no", ListTransCustomer.get(position).getContact_no());
                intent.putExtra("contact_no1", ListTransCustomer.get(position).getAnother_no());
                intent.putExtra("email", ListTransCustomer.get(position).getEmail());
                intent.putExtra("city", ListTransCustomer.get(position).getCity());

               /* intent.putExtra("transaction_id", transactionlist.get(position).getTransactionId());
                intent.putExtra("customer_name", transactionlist.get(position).getCustomerName());
                intent.putExtra("payment_type", transactionlist.get(position).getPaymentType());
                intent.putExtra("transaction_date", transactionlist.get(position).getTransactionDate());
                intent.putExtra("amount", transactionlist.get(position).getAmount());
                intent.putExtra("transaction_note", transactionlist.get(position).getTransactionNote());*/
                context.startActivity(intent);
            }
        });

    }

    private void openDeleteDialog(final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Deactive this Customer ?");
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Transacation_Customers").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {
                            if (dataSnapshot.child("id").getValue().toString().equals(ListTransCustomer.get(position).getId())) {
                                DatabaseReference cineIndustryRef = databaseReference.child("Transacation_Customers").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("status", 1);

                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        new CToast(context).simpleToast("Customer deactivated successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        ListTransCustomer.remove(position);
                                        notifyDataSetChanged();
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

    private void deleteEntryAPI1(final int position) {

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.DEACTIVE_TRANSCUSTOMER + "customer_id="+ListTransCustomer.get(position).getId(), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("result") == 1) {
                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        ListTransCustomer.remove(position);
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

    @Override
    public int getItemCount() {
        return ListTransCustomer.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_cust_name, txt_cust_no;
        View view_main;
        SwitchMaterial ivDelete;
        LinearLayout liMain;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            liMain = (LinearLayout) itemView.findViewById(R.id.liMain);
            txt_cust_name = (TextView) itemView.findViewById(R.id.txt_cust_name);
            txt_cust_no = (TextView) itemView.findViewById(R.id.txt_cust_no);
            view_main = itemView.findViewById(R.id.view_main);
            ivDelete=itemView.findViewById(R.id.ivDelete);

        }
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

}

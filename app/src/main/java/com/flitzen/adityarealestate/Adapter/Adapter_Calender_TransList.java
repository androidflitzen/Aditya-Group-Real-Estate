package com.flitzen.adityarealestate.Adapter;

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
import android.widget.LinearLayout;
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
import com.flitzen.adityarealestate.Activity.Edit_Transaction_Activity;
import com.flitzen.adityarealestate.Activity.Transaction_Activity;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Classes.Helper;
import com.flitzen.adityarealestate.Classes.Utils;
import com.flitzen.adityarealestate.Items.Item_Customer_List;
import com.flitzen.adityarealestate.Items.Transcation;
import com.flitzen.adityarealestate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter_Calender_TransList extends RecyclerView.Adapter<Adapter_Calender_TransList.ViewHolder> {


    Context context;
    ArrayList<Transcation> transactionlist = new ArrayList<>();
    boolean value;
    ProgressDialog prd;
    android.app.AlertDialog alertDialog;

    List<Item_Customer_List> itemListCustomer = new ArrayList<>();
    List<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();

    public Adapter_Calender_TransList(Context context, ArrayList<Transcation> Transactionlist, Boolean value, android.app.AlertDialog alertDialog) {
        this.context = context;
        this.transactionlist = Transactionlist;
        this.value = value;
        this.alertDialog = alertDialog;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_transactionlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {


        holder.tvTransCustomerName.setText(transactionlist.get(position).getCustomerName());
        holder.tvTrancNote.setText(transactionlist.get(position).getTransactionNote());

        if (transactionlist.get(position).getPaymentType().equals("0")  ) {
            holder.tvTransAmount.setText(transactionlist.get(position).getAmount());
            holder.tvTransAmount.setTextColor(context.getResources().getColor(R.color.color_green));
        } else  {
            holder.tvTransAmount.setText(transactionlist.get(position).getAmount());
            holder.tvTransAmount.setTextColor(context.getResources().getColor(R.color.msg_fail));
        }

        String[] date = transactionlist.get(position).getTransactionDate().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
        holder.tvTransDate.setText(date[0] + " " + mm + " " + date[2]);

        // holder.tvTransDate.setText(transactionlist.get(position).getTransactionDate());

        holder.ivTransDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDialog(position);

            }
        });


        holder.ivTransEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // openUpdateDialog(position);
                alertDialog.dismiss();
                Intent intent = new Intent(context, Edit_Transaction_Activity.class);
                intent.putExtra("customer_id", transactionlist.get(position).getCustomerId());
                intent.putExtra("transaction_id", transactionlist.get(position).getTransactionId());
                intent.putExtra("customer_name", transactionlist.get(position).getCustomerName());
                intent.putExtra("payment_type", transactionlist.get(position).getPaymentType());
                intent.putExtra("transaction_date", transactionlist.get(position).getTransactionDate());
                intent.putExtra("amount", transactionlist.get(position).getAmount());
                intent.putExtra("transaction_note", transactionlist.get(position).getTransactionNote());
                context.startActivity(intent);

            }
        });

    }


    private void openDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Delete this Transaction ?");
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
        Query applesQuery = ref.child("Transactions").orderByChild("transaction_id").equalTo(transactionlist.get(position).getTransactionId());

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                    new CToast(context).simpleToast("Transaction delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    transactionlist.remove(position);
                    notifyDataSetChanged();
                    if(transactionlist.size()>0){

                    }
                    else {
                        alertDialog.dismiss();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Cancel ", "onCancelled", databaseError.toException());
            }
        });
    }

    private void deleteEntryAPI1(final int position) {

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.DELETE_TRANSCATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {
                        transactionlist.remove(position);
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

                Utils.showLog("===" + transactionlist.get(position).getTransactionId());
                Map<String, String> params = new HashMap<>();
                params.put("transaction_id", transactionlist.get(position).getTransactionId());
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
        return transactionlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTransCustomerName, tvTrancNote, tvTransAmount, tvTransDate;
        ImageView ivTransEdit, ivTransDelete, ivTransDetail;
        LinearLayout liMianView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            tvTransCustomerName = (TextView) itemView.findViewById(R.id.tvTransCustomerName);
            tvTrancNote = (TextView) itemView.findViewById(R.id.tvTrancNote);
            tvTransAmount = (TextView) itemView.findViewById(R.id.tvTransAmount);
            tvTransDate = (TextView) itemView.findViewById(R.id.tvTransDate);

            ivTransDelete = (ImageView) itemView.findViewById(R.id.ivTransDelete);
            ivTransEdit = (ImageView) itemView.findViewById(R.id.ivTransEdit);
            liMianView = (LinearLayout) itemView.findViewById(R.id.liMianView);

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

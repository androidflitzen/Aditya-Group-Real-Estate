package com.flitzen.adityarealestate_new.Adapter;

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
import com.flitzen.adityarealestate_new.Activity.Activity_Rent_Details;
import com.flitzen.adityarealestate_new.Activity.Edit_Transaction_Activity;
import com.flitzen.adityarealestate_new.Activity.ViewAgreementPDF;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.Items.Transcation;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Adapter_Trans_PaymentList extends RecyclerView.Adapter<Adapter_Trans_PaymentList.ViewHolder> {

    Context context;
    ArrayList<Transcation> transactionlist = new ArrayList<>();
    boolean value;
    ProgressDialog prd;

    List<Item_Customer_List> itemListCustomer = new ArrayList<>();
    List<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();


    public Adapter_Trans_PaymentList(Context context, ArrayList<Transcation> Transactionlist, Boolean value) {
        this.context = context;
        this.transactionlist = Transactionlist;
        this.value = value;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_transactionlist1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {

      //  holder.tvTransCustomerName.setText(transactionlist.get(position).getCustomerName());
        holder.tvTrancNote.setText(transactionlist.get(position).getTransactionNote());

        if (transactionlist.get(position).getPaymentType().equals("0")  ) {

            holder.tvTransAmount1.setText("");
            holder.tvTransAmount1.setTextColor(context.getResources().getColor(R.color.red_dark));
            holder.linPay.setBackgroundColor(context.getResources().getColor(R.color.very_light_red));
            holder.tvTransAmount.setText(context.getResources().getString(R.string.rupee)+" "+transactionlist.get(position).getAmount());
            holder.tvTransAmount.setTextColor(context.getResources().getColor(R.color.color_green));
            holder.linPay.setBackgroundColor(context.getResources().getColor(R.color.white));



        } else  {
            holder.tvTransAmount.setText("");
            holder.linPay.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.tvTransAmount.setTextColor(context.getResources().getColor(R.color.color_green));
            holder.tvTransAmount1.setText(context.getResources().getString(R.string.rupee)+" "+transactionlist.get(position).getAmount());
            holder.tvTransAmount1.setTextColor(context.getResources().getColor(R.color.red_dark));
            holder.linPay.setBackgroundColor(context.getResources().getColor(R.color.very_light_red));
        }

        String[] date = transactionlist.get(position).getTransactionDate().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
        holder.tvTransDate.setText(date[2] + " " + mm + " " + date[0]);

        // holder.tvTransDate.setText(transactionlist.get(position).getTransactionDate());

        holder.linPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateDialog_view(position);
            }
        });

        holder.linReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateDialog_view(position);
            }
        });

       /* holder.ivTransDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDialog(position);

            }
        });

        holder.ivTransEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // openUpdateDialog(position);
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
        });*/

    }

    private void openUpdateDialog_view(int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.edit_delete_transaction_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        LinearLayout layout_edit = (LinearLayout) view.findViewById(R.id.layout_edit);
        LinearLayout layou_delete = (LinearLayout) view.findViewById(R.id.layou_delete);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        layout_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
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

        layou_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openDeleteDialog(position);
            }
        });
        dialog.show();
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Transactions").orderByChild("transaction_id").equalTo(transactionlist.get(position).getTransactionId());

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                    notifyDataSetChanged();
                    new CToast(context).simpleToast("Payment deleted successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
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

        TextView tvTransCustomerName, tvTrancNote, tvTransAmount, tvTransDate,tvTransAmount1;
        ImageView ivTransEdit, ivTransDelete, ivTransDetail;
        LinearLayout liMianView,linPay,linReceive;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

         //   tvTransCustomerName = (TextView) itemView.findViewById(R.id.tvTransCustomerName);
            tvTrancNote = (TextView) itemView.findViewById(R.id.tvTrancNote);
            tvTransAmount = (TextView) itemView.findViewById(R.id.tvTransAmount);
            tvTransDate = (TextView) itemView.findViewById(R.id.tvTransDate);
            tvTransAmount1 = (TextView) itemView.findViewById(R.id.tvTransAmount1);

            ivTransDelete = (ImageView) itemView.findViewById(R.id.ivTransDelete);
            ivTransEdit = (ImageView) itemView.findViewById(R.id.ivTransEdit);
            liMianView = (LinearLayout) itemView.findViewById(R.id.liMianView);
            linPay = (LinearLayout) itemView.findViewById(R.id.linPay);
            linReceive = (LinearLayout) itemView.findViewById(R.id.linReceive);

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

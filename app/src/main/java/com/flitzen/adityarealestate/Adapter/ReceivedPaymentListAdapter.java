package com.flitzen.adityarealestate.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate.Activity.Activity_Sites_Purchase_Summary;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Classes.Helper;
import com.flitzen.adityarealestate.Classes.Utils;
import com.flitzen.adityarealestate.Fragment.CashReceiveFragment;
import com.flitzen.adityarealestate.Items.ReceivedPayment;
import com.flitzen.adityarealestate.R;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReceivedPaymentListAdapter extends RecyclerView.Adapter<ReceivedPaymentListAdapter.ViewHolder> {

    Context context;
    List<ReceivedPayment> receivedPaymentList;
    OnItemLongClickListener mItemClickListener;
    ProgressDialog prd;

    String edit_id = "", edite_date = "", edit_amount = "", edit_remark = "", editfinaldate = "";
    boolean isedit = false;

    CashReceiveFragment cashReceiveFragment;

    public ReceivedPaymentListAdapter(Context context, List<ReceivedPayment> receivedPaymentList) {
        this.context = context;
        this.receivedPaymentList = receivedPaymentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_site_payment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.txt_amount.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(receivedPaymentList.get(position).getAmount())));

        String[] date = receivedPaymentList.get(position).getPayment_date().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
        holder.txt_date.setText(date[2] + " " + mm + " " + date[0]);

        holder.txt_remarks.setText("Remarks : " + receivedPaymentList.get(position).getRemarks());

        if (!receivedPaymentList.get(position).getRemarks().equals("")) {
            holder.txt_remarks.setVisibility(View.VISIBLE);
        } else {
            holder.txt_remarks.setVisibility(View.INVISIBLE);
        }

     /*   holder.view_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
                return true;
            }
        });*/

        holder.view_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit_id = receivedPaymentList.get(position).getId();
                edit_amount = receivedPaymentList.get(position).getAmount();
                edite_date = receivedPaymentList.get(position).getPayment_date();
                edit_remark = receivedPaymentList.get(position).getRemarks();
                opendailogEdit(edite_date, edit_amount, edit_remark, edit_id, isedit = true,position);
            }
        });

       /* holder.view_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
                        .setTitle(Html.fromHtml("<b> Delete </b>"))
                        .setMessage("Are you sure you want to delete this payment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                remove_Payment(receivedPaymentList.get(position).getId());
                            }
                        }).setNegativeButton("No", null).show();
                return false;
            }
        });*/




        /*holder.view_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
            }
        });*/
    }

    private void opendailogEdit(final String edite_date, String edit_amount, String edit_remark, final String edit_id, final boolean isedit,int position) {

        LayoutInflater localView = LayoutInflater.from(context);
        View promptsView = localView.inflate(R.layout.dailog_site_received_edit, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(false);

        final EditText edt_paid_amount = (EditText) promptsView.findViewById(R.id.edt_paid_amount);
        final EditText edt_remark = (EditText) promptsView.findViewById(R.id.edt_remark);
        final TextView txt_date = (TextView) promptsView.findViewById(R.id.txt_date);

        ImageView btn_cancel = (ImageView) promptsView.findViewById(R.id.btn_cancel);
        Button btn_add_payment = (Button) promptsView.findViewById(R.id.btn_add_payment);
        Button btn_delete_payment = (Button) promptsView.findViewById(R.id.btn_delete_payment);

        txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
        txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));


        edt_paid_amount.setText(edit_amount);
        edt_remark.setText(edit_remark);
        editfinaldate = edite_date;
        if (isedit) {
            txt_date.setText(edite_date);
        } else {
            Helper.pick_Date((Activity) context, txt_date);
        }
        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.pick_Date((Activity) context, txt_date);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btn_delete_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
                        .setTitle(Html.fromHtml("<b> Delete </b>"))
                        .setMessage("Are you sure you want to delete this payment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                remove_Payment(edit_id,position);
                                alertDialog.dismiss();
                            }
                        }).setNegativeButton("No", null).show();
            }
        });

        btn_add_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_date.getText().toString().equals("")) {
                    new CToast(context).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else if (edt_paid_amount.getText().toString().equals("")) {
                    edt_paid_amount.setError("Enter pending amount");
                    edt_paid_amount.requestFocus();
                    return;
                } else {
                    alertDialog.dismiss();
                    editfinaldate = txt_date.getText().toString();
                    addSitePayment(edit_id, editfinaldate, edt_paid_amount.getText().toString().trim(), edt_remark.getText().toString().trim());
                }
            }
        });

        alertDialog.show();

    }

    private void remove_Payment(final String id,int position) {

        showPrd();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Site_Receive_Payment").orderByChild("id").equalTo(id);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                    receivedPaymentList.remove(position);
                    notifyDataSetChanged();
                    new CToast(context).simpleToast("Delete payment successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
                Log.e("Cancel ", "onCancelled", databaseError.toException());
            }
        });
    }

    private void addSitePayment(final String edit_id, final String date, final String amount, final String remarks) {

        showPrd();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Site_Receive_Payment").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(edit_id)) {

                                DatabaseReference cineIndustryRef = databaseReference.child("Site_Receive_Payment").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("amount", amount);
                                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                                String datePattern = "\\d{4}-\\d{2}-\\d{2}";
                                boolean isDate1 = date.matches(datePattern);

                                if (isDate1 == true) {
                                    map.put("payment_date", date + " " + currentTime);
                                } else {
                                    DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                                    Date d = f.parse(date);
                                    DateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
                                    String date11 = date2.format(d);
                                    map.put("payment_date", date11 + " " + currentTime);
                                }

                                map.put("remarks", remarks);

                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        new CToast(context).simpleToast("Edit Received Payment Successful.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
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

    private void addSitePayment1(final String edit_id, final String date, final String amount, final String remarks) {

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.SITE_ADD_RECEIVE_PAYMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Sites Add", response);
                    if (jsonObject.getInt("result") == 1) {
                        //cashReceiveFragment.getSitePaymentList();
                        // notifyDataSetChanged();
                        Utils.showToast(context, "Edit Received Payment Successful.", R.color.color_green);
                        //   new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
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
                params.put("type", "Edit");
                params.put("id", edit_id);
                params.put("amount", amount);
                params.put("payment_date", date);
                params.put("remarks", remarks);
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
        return receivedPaymentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_amount, txt_date, txt_time, txt_name, txt_remarks;
        View view_main;
        ImageView plotSummaryEdit;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_amount = (TextView) itemView.findViewById(R.id.txt_amount);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            txt_remarks = (TextView) itemView.findViewById(R.id.txt_remarks);
            view_main = itemView.findViewById(R.id.view_main);
            plotSummaryEdit = itemView.findViewById(R.id.plotSummaryEdit);
        }
    }

    public void OnItemLongClickListener(final OnItemLongClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {

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


}

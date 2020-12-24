package com.flitzen.adityarealestate_new.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.flitzen.adityarealestate_new.Activity.Activity_ImageViewer;
import com.flitzen.adityarealestate_new.Activity.CustomerBillsActivity;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.CustomerBill;
import com.flitzen.adityarealestate_new.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerBillAdapter extends RecyclerView.Adapter<CustomerBillAdapter.ViewHolder>  {
    Context context;
    List<CustomerBill> customerBillList;
    LayoutInflater inflter;
    ProgressDialog prd;

    public CustomerBillAdapter(Context applicationContext, List<CustomerBill> customerBillList) {
        this.context = applicationContext;
        this.customerBillList = customerBillList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_customer_bill_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.tvBillName.setText("Bill Month : "+customerBillList.get(position).getBill_month());
        viewHolder.tvBillDate.setText("Create On : " + customerBillList.get(position).getCreate_date());

        if (customerBillList.get(position).getBill_photo() != null && !customerBillList.get(position).getBill_photo().equals("")) {
            Picasso.with(context)
                    .load(customerBillList.get(position).getBill_photo())
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.ic_img_not_available)
                    .into(viewHolder.ivBill);
        }else {
            viewHolder.ivBill.setImageResource(R.drawable.ic_img_not_available);
        }
        viewHolder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Activity_ImageViewer.class)
                        .putExtra("img_url",customerBillList.get(position).getBill_photo()));
            }
        });

        viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDialog(position);
            }
        });

        viewHolder.ivBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customerBillList.get(position).getBill_photo()!=null && !customerBillList.get(position).getBill_photo().equals("")){
                    context.startActivity(new Intent(context,Activity_ImageViewer.class)
                            .putExtra("img_url",customerBillList.get(position).getBill_photo()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerBillList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBillDate,tvBillName;
        public ImageView ivBill, ivDelete;
        public CardView mainView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBillDate = (TextView) itemView.findViewById(R.id.tvBillDate);
            tvBillName = (TextView) itemView.findViewById(R.id.tvBillName);
            ivBill = (ImageView) itemView.findViewById(R.id.ivBill);
            ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            mainView=itemView.findViewById(R.id.mainView);
        }
    }

    private void openDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure delete this Bill");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        deleteBillAPI(position);
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
    private void deleteBillAPI(final int position) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.CUSTOMER_BILL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Customer Add", response);

                    if (jsonObject.getInt("result") == 1) {

                        new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        customerBillList.remove(position);
                        notifyDataSetChanged();
                        if (customerBillList.size()==0){
                            CustomerBillsActivity.layoutNoResult.setVisibility(View.VISIBLE);
                        }
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
                params.put("type", "Delete");
                params.put("Bill_id", customerBillList.get(position).getBill_id());

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

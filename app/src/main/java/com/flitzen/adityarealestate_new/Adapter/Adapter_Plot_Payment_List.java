package com.flitzen.adityarealestate_new.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.flitzen.adityarealestate_new.Activity.Activity_ImageViewer;
import com.flitzen.adityarealestate_new.Activity.PdfViewActivity;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate_new.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Adapter_Plot_Payment_List extends RecyclerView.Adapter<Adapter_Plot_Payment_List.ViewHolder> {

    Context context;
    ArrayList<Item_Plot_Payment_List> itemList = new ArrayList<>();
    OnItemClickListener mItemClickListener;
    private OnItemClickListener listener;

    ProgressDialog prd;
    boolean showFile;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }





    public Adapter_Plot_Payment_List(Context context, ArrayList<Item_Plot_Payment_List> itemList,boolean showFile) {
        this.context = context;
        this.itemList = itemList;
        this.showFile=showFile;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_payment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        holder.txt_amount.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(itemList.get(position).getAmount())));
        if (itemList.get(position).getNext_payment_date().equalsIgnoreCase("")){
            holder.txt_next_date.setText("-");
        }else if(itemList.get(position).getNext_payment_date().equals("0000-00-00")){
            holder.txt_next_date.setText("-");
        }else {
            try {
                String[] date = itemList.get(position).getNext_payment_date().split("-");
                String month = date[1];
                String mm = Helper.getMonth(month);
                holder.txt_next_date.setText(date[2] + " " + mm + " " + date[0]);
            }
            catch (Exception e){
                e.printStackTrace();
            }

          //  holder.txt_next_date.setText(itemList.get(position).getNext_payment_date());
        }

        try {
            String[] date = itemList.get(position).getPayment_date().split("-");
            String month = date[1];
            String mm = Helper.getMonth(month);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //holder.txt_date.setText(date[0] + " " + mm + " " + date[2]);

        if(itemList.get(position).getPayment_date()!=null){

            try {
                SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatOut = new SimpleDateFormat("dd MMM,yyyy");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(formatIn.parse(itemList.get(position).getPayment_date()));
                String newDate = formatOut.format(calendar.getTime());
                holder.txt_date.setText(newDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(itemList.get(position).getPayment_time()!=null){

            try {
                SimpleDateFormat formatIn = new SimpleDateFormat("hh:mm:ss");
                SimpleDateFormat formatOut = new SimpleDateFormat("hh:mm a");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(formatIn.parse(itemList.get(position).getPayment_time()));
                String newDate = formatOut.format(calendar.getTime());
                holder.txt_time.setText(newDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //holder.txt_time.setText(itemList.get(position).getPayment_time());

        holder.linName.setVisibility(View.GONE);
        holder.txt_name.setText(itemList.get(position).getCustomer_name());

        if (!itemList.get(position).getRemarks().equals("")) {
            holder.txt_remarks.setVisibility(View.VISIBLE);
            holder.txt_remarks.setText( itemList.get(position).getRemarks());
        } else {
            holder.txt_remarks.setVisibility(View.GONE);
        }

        holder.view_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
     /*   holder.view_main.setOnClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
                return true;
            }
        });*/

        if (showFile){

            if (itemList.get(position).getPayment_attachment()!=null && !itemList.get(position).getPayment_attachment().isEmpty()){
                holder.layout_demo.setVisibility(View.VISIBLE);

                /*if (itemList.get(position).getPayment_attachment().endsWith("png")){
                    Picasso.with(context)
                            .load(itemList.get(position).getPayment_attachment())
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .resize(300,300)
                            .into(holder.ivFile);
                }else if (itemList.get(position).getPayment_attachment().endsWith("pdf")){
                    holder.ivFile.setImageResource(R.drawable.ic_pdf);
                }*/
            }else {
                holder.layout_demo.setVisibility(View.GONE);
            }
        }else {
            holder.layout_demo.setVisibility(View.GONE);
        }

        holder.layout_demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemList.get(position).getFileType().endsWith("image/png") || itemList.get(position).getFileType().endsWith("image/png") ||
                        itemList.get(position).getFileType().endsWith("image/jpg") || itemList.get(position).getFileType().endsWith("image/jpeg")){
                    context.startActivity(new Intent(context, Activity_ImageViewer.class)
                            .putExtra("img_url",itemList.get(position).getPayment_attachment()));
                }else if (itemList.get(position).getFileType().endsWith("application/pdf")){
                    context.startActivity(new Intent(context, PdfViewActivity.class)
                    .putExtra("pdf_url",itemList.get(position).getPayment_attachment())
                    .putExtra("only_load",true));
                }
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeleteDialog(position);
            }
        });

        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateDialog(position);
            }
        });

        holder.view_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUpdateDialog(position);
                //openDeleteDialog(position);

            }
        });

        holder.bind(position, listener);
    }

    private void openUpdateDialog(final int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_update_payment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog dialog_main = builder.create();
        dialog_main.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final EditText edt_paid_amount=(EditText)  view.findViewById(R.id.edt_paid_amount);
        edt_paid_amount.setText(itemList.get(position).getAmount());
        final EditText edt_remark=(EditText)  view.findViewById(R.id.edt_remark);
        edt_remark.setText(itemList.get(position).getRemarks());
        Button btn_update_payment=(Button)  view.findViewById(R.id.btn_update_payment);
        Button btn_delete_payment = (Button)view.findViewById(R.id.btn_delete_payment);



        ImageView btn_cancel=(ImageView)  view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_main.dismiss();
            }
        });
        btn_update_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_paid_amount.getText().toString().equals("")){
                    edt_paid_amount.setError("required");
                }else if (edt_remark.getText().toString().equals("")) {
                    edt_remark.setError("required");
                }else {
                    updatePaymentAPI(position,edt_paid_amount.getText().toString(),edt_remark.getText().toString());
                    dialog_main.dismiss();
                }

            }
        });

        btn_delete_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openDeleteDialog(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Delete this Payment ?");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteEntryAPI(position);
                                dialog.dismiss();
                                dialog_main.dismiss();

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
        dialog_main.show();
    }

    private void updatePaymentAPI(final int position, final String amount, final String remarks) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.UPDATE_PAYMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {
                        itemList.get(position).setAmount(amount);
                        itemList.get(position).setRemarks(remarks);
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

                Map<String, String> params = new HashMap<>();
                params.put("id", itemList.get(position).getId());
                params.put("amount", amount);
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
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_amount, txt_date, txt_time, txt_name, txt_remarks,txt_next_date;

        ImageView ivDelete,ivEdit,ivFile;
        LinearLayout layout_demo;
        CardView view_main;
        LinearLayout linName;


        public ViewHolder(View itemView) {
            super(itemView);



            txt_amount = (TextView) itemView.findViewById(R.id.txt_amount);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            linName =  itemView.findViewById(R.id.linName);
            txt_remarks = (TextView) itemView.findViewById(R.id.txt_remarks);
            view_main = (CardView)itemView.findViewById(R.id.view_main);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivFile = itemView.findViewById(R.id.ivFile);
            txt_next_date =  itemView.findViewById(R.id.txt_next_date);
            layout_demo =  (LinearLayout)itemView.findViewById(R.id.layout_demo);
        }

        public void bind(final int position, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        }


    }

    public void OnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setOnItemListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private void openDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Delete this Payment ?");
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.DELETE_PAYMENT, new Response.Listener<String>() {
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

                Utils.showLog("==="+itemList.get(position).getId());
                Map<String, String> params = new HashMap<>();
                params.put("id", itemList.get(position).getId());

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

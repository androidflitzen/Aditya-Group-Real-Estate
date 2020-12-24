package com.flitzen.adityarealestate_new.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
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
import com.flitzen.adityarealestate_new.Activity.Activity_Admin_All_LoanApplication;
import com.flitzen.adityarealestate_new.Activity.Admin_LoanDetail_Activity;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.Iteams_All_Loan_Application;
import com.flitzen.adityarealestate_new.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jolly Android on 1/11/2018.
 */

public class Adapter_All_Loan_Applications extends RecyclerView.Adapter<Adapter_All_Loan_Applications.ViewHolder> {

    Context context;
    List<Iteams_All_Loan_Application> itemList = new ArrayList<>();
    private LayoutInflater inflater;
    SharedPreferences loan_form_details;
    private double final_amount;
    DecimalFormat formatter;

    OnItemLongClickListener mItemClickListener;

    public Adapter_All_Loan_Applications(Context context, List<Iteams_All_Loan_Application> itemList) {

        this.context = context;
        this.itemList = itemList;
        formatter = new DecimalFormat("#,##,###");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_loan_application_adapter, parent, false);
        return new Adapter_All_Loan_Applications.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        String amount = formatter.format(Integer.parseInt(itemList.get(position).getAll_loan_applicant_amount()));

        holder.txt_amount.setText(context.getResources().getString(R.string.rupee) + amount);
        holder.txt_tenure.setText(itemList.get(position).getAll_loan_applicant_tenure() + " Months");
        holder.txt_application_number.setText("#" + itemList.get(position).getAll_loan_applicant_number());
        holder.txt_name.setText(itemList.get(position).getAll_loan_applicant_name());
        holder.txt_loan_status.setText(itemList.get(position).getAll_loan_applicant_status());
        holder.txt_loan_type.setText(itemList.get(position).getAll_loan_applicant_loan_type());

        holder.txt_loan_status.setTextColor(getLoanStatusColor(itemList.get(position).getAll_loan_applicant_status().toLowerCase()));
        holder.cardViewType.setCardBackgroundColor(getLoanTypeColor(itemList.get(position).getAll_loan_applicant_loan_type().toLowerCase()));
        String status = itemList.get(position).getAll_loan_applicant_status();

        if (status.equals("Pending") || status.equals("Rejected")) {
            holder.txt_date_approve_title.setText("Date : ");
            holder.txt_amount_title.setText("Amount");
            holder.txt_date_approve.setText(itemList.get(position).getAll_loan_applicant_applied_date());

            if(itemList.get(position).getAll_loan_applicant_applied_date()!=null){

                try {

                    SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    SimpleDateFormat formatOut = new SimpleDateFormat("dd MMM,yyyy");
                    SimpleDateFormat formatOutT = new SimpleDateFormat("hh:mm a");
                    Calendar calendar = Calendar.getInstance();
                    Calendar calendarT = Calendar.getInstance();
                    calendar.setTime(formatIn.parse(itemList.get(position).getAll_loan_applicant_applied_date()));
                    String newDate = formatOut.format(calendar.getTime());
                    calendarT.setTime(formatIn.parse(itemList.get(position).getAll_loan_applicant_applied_date()));
                    String newDateT = formatOutT.format(calendarT.getTime());
                    holder.txt_date_approve.setText(newDate+" "+newDateT);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        } else {
            holder.txt_amount_title.setText("Approve Amount");
            holder.txt_date_approve_title.setText("Approved On : ");

            if(itemList.get(position).getAll_loan_applicant_applied_date()!=null){

                try {

                    SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    SimpleDateFormat formatOut = new SimpleDateFormat("dd MMM,yyyy");
                    SimpleDateFormat formatOutT = new SimpleDateFormat("hh:mm a");
                    Calendar calendar = Calendar.getInstance();
                    Calendar calendarT = Calendar.getInstance();
                    calendar.setTime(formatIn.parse(itemList.get(position).getAll_loan_applicant_applied_date()));
                    String newDate = formatOut.format(calendar.getTime());
                    calendarT.setTime(formatIn.parse(itemList.get(position).getAll_loan_applicant_applied_date()));
                    String newDateT = formatOutT.format(calendarT.getTime());
                    holder.txt_date_approve.setText(newDate+" "+newDateT);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
           // holder.txt_date_approve.setText(itemList.get(position).getAll_loan_applicant_approve_date());
        }

        if (itemList.get(position).getAll_loan_applicant_loan_type().equals("Simple")) {
            holder.viewTenure.setVisibility(View.GONE);
        } else {
            holder.viewTenure.setVisibility(View.VISIBLE);
        }

        holder.viewLoanStatus.setBackground(getLoanStatusBg(itemList.get(position).getAll_loan_applicant_status().toLowerCase()));
        holder.imgLoanStatus.setImageResource(getLoanStatusImg(itemList.get(position).getAll_loan_applicant_status().toLowerCase()));

        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, Admin_LoanDetail_Activity.class);
                i.putExtra("Application_id", itemList.get(position).getAll_loan_applicant_id());
                i.putExtra("Application_number", itemList.get(position).getAll_loan_applicant_number());
                i.putExtra("Application_name", itemList.get(position).getAll_loan_applicant_name());
                i.putExtra("node_id", itemList.get(position).getKey());
                i.putExtra("loan_id", itemList.get(position).getAll_loan_loan_id());
                Log.e("loan_id  ",itemList.get(position).getAll_loan_loan_id());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        });

        holder.mainView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
                return true;
            }
        });

        /*holder.mainView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //DeleteLoan(position);
                return false;
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mainView, viewTenure;
        TextView txt_amount_title, txt_amount, txt_tenure, txt_application_number, txt_date_approve_title, txt_date_approve, txt_name, txt_loan_status, txt_loan_type;
        ImageView imgLoanStatus;
        View viewLoanStatus;
        CardView cardViewType;

        public ViewHolder(View itemView) {
            super(itemView);

            loan_form_details = context.getSharedPreferences("loan_form_details", Context.MODE_PRIVATE);
            txt_amount = (TextView) itemView.findViewById(R.id.txt_all_application_amount_adapter);
            txt_amount_title = (TextView) itemView.findViewById(R.id.txt_all_application_amount_title_adapter);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name_all_application_adapter);
            txt_application_number = (TextView) itemView.findViewById(R.id.txt_all_application_no_adapter);
            txt_loan_type = (TextView) itemView.findViewById(R.id.txt_all_application_loan_type);
            txt_date_approve = (TextView) itemView.findViewById(R.id.txt_all_application_approve_date_adapter);
            txt_date_approve_title = (TextView) itemView.findViewById(R.id.txt_all_application_approve_date_title_adapter);
            txt_tenure = (TextView) itemView.findViewById(R.id.txt_all_application_tenure_adapter);
            imgLoanStatus = (ImageView) itemView.findViewById(R.id.img_all_application_loan_status);
            viewLoanStatus = itemView.findViewById(R.id.view_all_application_loan_status);
            txt_loan_status = (TextView) itemView.findViewById(R.id.txt_all_application_loan_status);
            mainView = itemView.findViewById(R.id.view_main_all_loan_application_adapter);
            viewTenure = itemView.findViewById(R.id.view_all_application_adapter_tenure);
            cardViewType = (CardView) itemView.findViewById(R.id.cardview_all_application_loan_type);

        }
    }

    public void OnItemLongClickListener(final OnItemLongClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {

        void onItemClick(int position);
    }

    public void DeleteLoan(final int position) {

        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(30);
        new AlertDialog.Builder(context.getApplicationContext(), R.style.AppCompatAlertDialogStyle)
                .setTitle("Delete Loan")
                .setMessage(Html.fromHtml("Delete <b>" + itemList.get(position).getAll_loan_applicant_name() + "'s</b> <i>#" + itemList.get(position).getAll_loan_applicant_number() + "</i> loan ?"))
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.DELETE_LOAN + "?type=DeleteLoan&id=" + itemList.get(position).getAll_loan_applicant_id(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //Log.e("Delete Customer", response);

                                        //hidePrd();
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            if (jsonObject.getString("result").equals("success")) {

                                                new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                if (context instanceof Activity_Admin_All_LoanApplication) {
                                                    ((Activity_Admin_All_LoanApplication) context).SET_DATA();
                                                }
                                                // getData();

                                            } else {
                                                new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //hidePrd();
                                    }
                                });

                        try {
                            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    30000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            RequestQueue requestQueue = Volley.newRequestQueue(context);
                            requestQueue.add(stringRequest);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    public Drawable getLoanStatusBg(String status) {

        if (status.equals("pending")) {
            return context.getResources().getDrawable(R.drawable.ic_bg_loan_pending);
        } else if (status.equals("rejected")) {
            return context.getResources().getDrawable(R.drawable.ic_bg_loan_reject);
        } else if (status.equals("approved")) {
            return context.getResources().getDrawable(R.drawable.ic_bg_loan_approved);
        } else {
            return context.getResources().getDrawable(R.drawable.ic_bg_loan_complete);
        }
    }

    public int getLoanStatusImg(String status) {

        if (status.equals("pending")) {
            return R.drawable.ic_loan_pending;
        } else if (status.equals("rejected")) {
            return R.drawable.ic_loan_reject;
        } else if (status.equals("approved")) {
            return R.drawable.ic_loan_approve;
        } else {
            return R.drawable.ic_complete_loan;
        }
    }

    public int getLoanStatusColor(String status) {

        if (status.equals("pending")) {
            return context.getResources().getColor(R.color.pending_bg);
        } else if (status.equals("rejected")) {
            return context.getResources().getColor(R.color.reject_bg);
        } else if (status.equals("approved")) {
            return context.getResources().getColor(R.color.approw_bg);
        } else {
            return context.getResources().getColor(R.color.complete_bg);
        }
    }

    public int getLoanTypeColor(String status) {

        if (status.equals("emi")) {
            return context.getResources().getColor(R.color.lTypeEmi);
        } else if (status.equals("daily")) {
            return context.getResources().getColor(R.color.lTypeDaily);
        } else {
            return context.getResources().getColor(R.color.lTypeSimple);
        }
    }
}

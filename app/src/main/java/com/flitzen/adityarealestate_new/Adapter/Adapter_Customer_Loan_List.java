package com.flitzen.adityarealestate_new.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Loan_Details;
import com.flitzen.adityarealestate_new.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Customer_Loan_List extends RecyclerView.Adapter<Adapter_Customer_Loan_List.ViewHolder> {

    Context context;
    List<Item_Loan_Details> arrayList = new ArrayList<>();

    OnItemClickListener mItemClickListener;

    public Adapter_Customer_Loan_List(Context context, List<Item_Loan_Details> itemsList) {
        this.context = context;
        this.arrayList = itemsList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_customer_loan_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.txt_loan_no.setText("Application Number : " + arrayList.get(position).getApplicantion_Number());
        holder.txt_loan_type.setText(arrayList.get(position).getLoan_Type());


        /*String[] date = arrayList.get(position).getDate_of_purchase().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
        holder.txt_purchased_on.setText(date[0] + " " + mm + " " + date[2]);*/

        holder.txt_loan_amount.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(arrayList.get(position).getLoan_Amount())));
        holder.txt_monthly_emi.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(arrayList.get(position).getMonthly_EMI())));

        if (!arrayList.get(position).getApproved_Amount().equals("0")) {
            holder.txt_pending_amount.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(arrayList.get(position).getApproved_Amount())));
            holder.txt_loan_no.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.txt_pending_amount.setText(" - ");
            holder.txt_loan_no.setBackgroundColor(context.getResources().getColor(R.color.msg_success));
        }

        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView mainView;
        TextView txt_loan_no, txt_loan_amount, txt_pending_amount, txt_loan_type, txt_monthly_emi;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = (CardView) itemView.findViewById(R.id.view_locker_main);
            txt_loan_no = (TextView) itemView.findViewById(R.id.txt_loan_no);
            txt_loan_amount = (TextView) itemView.findViewById(R.id.txt_loan_amount);
            txt_pending_amount = (TextView) itemView.findViewById(R.id.txt_pending_amount);
            txt_loan_type = (TextView) itemView.findViewById(R.id.txt_loan_type);
            txt_monthly_emi = (TextView) itemView.findViewById(R.id.txt_monthly_emi);

        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    private String colorTextview(int color, String val2) {

        return "<font color=" + color + ">" + val2 + "</font>";
    }

}
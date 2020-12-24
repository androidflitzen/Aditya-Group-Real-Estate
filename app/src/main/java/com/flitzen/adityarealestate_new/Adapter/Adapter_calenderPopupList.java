package com.flitzen.adityarealestate_new.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.Items.Transcation;
import com.flitzen.adityarealestate_new.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_calenderPopupList extends RecyclerView.Adapter<Adapter_calenderPopupList.ViewHolder> {


    Context context;
    ArrayList<Transcation> transactionlist = new ArrayList<>();
    boolean value;
    ProgressDialog prd;

    List<Item_Customer_List> itemListCustomer = new ArrayList<>();
    List<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();

    public Adapter_calenderPopupList(Context context, ArrayList<Transcation> Transactionlist, Boolean value) {
        this.context = context;
        this.transactionlist = Transactionlist;
        this.value = value;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_calenderlist, parent, false);
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


    }

    @Override
    public int getItemCount() {
        return transactionlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTransCustomerName, tvTrancNote, tvTransAmount, tvTransDate;
        LinearLayout liMianView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTransCustomerName = (TextView) itemView.findViewById(R.id.tvTransCustomerName);
            tvTrancNote = (TextView) itemView.findViewById(R.id.tvTrancNote);
            tvTransAmount = (TextView) itemView.findViewById(R.id.tvTransAmount);
            tvTransDate = (TextView) itemView.findViewById(R.id.tvTransDate);
            liMianView = (LinearLayout) itemView.findViewById(R.id.liMianView);
        }
    }
}

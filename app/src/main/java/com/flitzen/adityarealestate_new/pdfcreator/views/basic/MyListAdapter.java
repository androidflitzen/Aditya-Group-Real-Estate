package com.flitzen.adityarealestate_new.pdfcreator.views.basic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.flitzen.adityarealestate_new.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate_new.R;

import java.util.ArrayList;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{

    ArrayList<Item_Plot_Payment_List> arrayListPlotPayment;

    // RecyclerView recyclerView;
    public MyListAdapter(ArrayList<Item_Plot_Payment_List> arrayListPlotPayment) {
        this.arrayListPlotPayment = arrayListPlotPayment;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.custom_list_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtDate.setText(arrayListPlotPayment.get(position).getPayment_date());
        holder.txtCustomerName.setText(arrayListPlotPayment.get(position).getCustomer_name());
        holder.txtRemarks.setText(arrayListPlotPayment.get(position).getRemarks());
        holder.txtAmount.setText(arrayListPlotPayment.get(position).getAmount());
    }


    @Override
    public int getItemCount() {
        return arrayListPlotPayment.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDate,txtAmount,txtRemarks,txtCustomerName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.txtDate =  itemView.findViewById(R.id.txtDate);
            this.txtCustomerName =  itemView.findViewById(R.id.txtCustomerName);
            this.txtRemarks =  itemView.findViewById(R.id.txtRemarks);
            this.txtAmount =  itemView.findViewById(R.id.txtAmount);

        }
    }
}
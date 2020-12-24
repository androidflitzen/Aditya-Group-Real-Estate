package com.flitzen.adityarealestate_new.Adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Detail;
import com.flitzen.adityarealestate_new.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Purchased_Plot_List extends RecyclerView.Adapter<Adapter_Purchased_Plot_List.ViewHolder> {

    Context context;
    List<Item_Plot_Detail> arrayList = new ArrayList<>();

    OnItemClickListener mItemClickListener;

    public Adapter_Purchased_Plot_List(Context context, List<Item_Plot_Detail> itemsList) {
        this.context = context;
        this.arrayList = itemsList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_purchase_plot_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        System.out.println("=======arrayList address   " + arrayList.get(position).getSite_address());
        System.out.println("=======arrayList name   " + arrayList.get(position).getSite_name());

        holder.txt_plot_no.setText("Plot : " + arrayList.get(position).getPlot_no());
        holder.txt_site_address.setText(arrayList.get(position).getSite_name());

        String[] date = arrayList.get(position).getDate_of_purchase().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
        holder.txt_purchased_on.setText(date[2] + " " + mm + " " + date[0]);

        holder.txt_purchased_price.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(arrayList.get(position).getPurchase_price())));

        if (arrayList.get(position).getPaid_amount() != null) {
            if (!arrayList.get(position).getPaid_amount().equals("0")) {
                holder.txt_paid_amount.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(arrayList.get(position).getPaid_amount())));
            } else {
                holder.txt_paid_amount.setText(" - ");
            }
        }

        if(arrayList.get(position).getPending_amount() != null){
            if (!arrayList.get(position).getPending_amount().equals("0")) {
                holder.txt_pending_amount.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(arrayList.get(position).getPending_amount())));
                holder.txt_plot_no.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            } else {
                holder.txt_pending_amount.setText(" - ");
                holder.txt_plot_no.setBackgroundColor(context.getResources().getColor(R.color.msg_success));
            }
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
        TextView txt_plot_no, txt_site_address, txt_purchased_on, txt_purchased_price, txt_paid_amount, txt_pending_amount;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = (CardView) itemView.findViewById(R.id.view_locker_main);
            txt_plot_no = (TextView) itemView.findViewById(R.id.txt_plot_no);
            txt_site_address = (TextView) itemView.findViewById(R.id.txt_site_address);
            txt_purchased_on = (TextView) itemView.findViewById(R.id.txt_purchased_on);
            txt_purchased_price = (TextView) itemView.findViewById(R.id.txt_purchased_price);
            txt_paid_amount = (TextView) itemView.findViewById(R.id.txt_paid_amount);
            txt_pending_amount = (TextView) itemView.findViewById(R.id.txt_pending_amount);

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
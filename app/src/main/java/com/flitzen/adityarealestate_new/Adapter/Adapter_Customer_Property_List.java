package com.flitzen.adityarealestate_new.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Property_Detail;
import com.flitzen.adityarealestate_new.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Customer_Property_List extends RecyclerView.Adapter<Adapter_Customer_Property_List.ViewHolder> {

    Context context;
    List<Item_Property_Detail> arrayList = new ArrayList<>();

    OnItemClickListener mItemClickListener;

    public Adapter_Customer_Property_List(Context context, List<Item_Property_Detail> itemsList) {
        this.context = context;
        this.arrayList = itemsList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_customer_rented_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.txt_property_name.setText(arrayList.get(position).getProperty_name());

        String[] date = arrayList.get(position).getHired_since().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
        holder.txt_date.setText(date[2] + " " + mm + " " + date[0]);

        holder.txt_rent.setText(context.getResources().getString(R.string.rupee) +Helper.getFormatPrice(Integer.parseInt(arrayList.get(position).getRent())));
        holder.txt_address.setText(arrayList.get(position).getAddress());

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
        TextView txt_date, txt_rent, txt_address, txt_property_name;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = (CardView) itemView.findViewById(R.id.view_locker_main);
            txt_property_name = (TextView) itemView.findViewById(R.id.txt_property_name);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_rent = (TextView) itemView.findViewById(R.id.txt_rent);
            txt_address = (TextView) itemView.findViewById(R.id.txt_address);

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
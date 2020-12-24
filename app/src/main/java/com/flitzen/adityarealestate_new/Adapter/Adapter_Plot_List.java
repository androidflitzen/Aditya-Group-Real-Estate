package com.flitzen.adityarealestate_new.Adapter;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.flitzen.adityarealestate_new.Items.Item_Plot_List;
import com.flitzen.adityarealestate_new.R;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Plot_List extends RecyclerView.Adapter<Adapter_Plot_List.ViewHolder> {

    Context context;
    List<Item_Plot_List> arrayList = new ArrayList<>();

    OnItemClickListener mItemClickListener;

    public Adapter_Plot_List(Context context, List<Item_Plot_List> itemsList) {
        this.context = context;
        this.arrayList = itemsList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_plot_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        try {
            if (arrayList.get(position).getIs_assign().equals("1")) {
                holder.mainView.setCardBackgroundColor(context.getResources().getColor(R.color.gray));

                holder.ivSoldOut.setVisibility(View.VISIBLE);
                holder.tvCustomer.setVisibility(View.VISIBLE);
                holder.tvCustomer.setText(arrayList.get(position).getCustomer_name());
            } else {
                holder.mainView.setCardBackgroundColor(Color.WHITE);
                holder.ivSoldOut.setVisibility(View.GONE);
                holder.tvCustomer.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.txtName.setText(arrayList.get(position).getPlot_no());
        holder.tvSize.setText(arrayList.get(position).getPlot_size()+" Size");
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
        ImageView ivSoldOut;
        TextView txtName,tvSize,tvCustomer;

        public ViewHolder(View itemView) {
            super(itemView);
            mainView = (CardView) itemView.findViewById(R.id.view_locker_main);
            txtName = (TextView) itemView.findViewById(R.id.txt_locker_name);
            tvSize = (TextView) itemView.findViewById(R.id.tvSize);
            ivSoldOut=(ImageView) itemView.findViewById(R.id.ivSoldOut);
            tvCustomer = (TextView) itemView.findViewById(R.id.tvCustomer);
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }
}
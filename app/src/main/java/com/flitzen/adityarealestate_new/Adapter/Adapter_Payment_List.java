package com.flitzen.adityarealestate_new.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.flitzen.adityarealestate_new.Activity.Activity_ImageViewer;
import com.flitzen.adityarealestate_new.Activity.PdfViewActivity;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Detail;
import com.flitzen.adityarealestate_new.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Adapter_Payment_List extends RecyclerView.Adapter<Adapter_Payment_List.ViewHolder> {

    Context context;
    ArrayList<Item_Plot_Detail.Item_Plot_Payment> itemList = new ArrayList<>();
    OnItemLongClickListener mItemClickListener;

    public Adapter_Payment_List(Context context, ArrayList<Item_Plot_Detail.Item_Plot_Payment> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_payment_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.txt_amount.setText(context.getResources().getString(R.string.rupee) +  Helper.getFormatPrice(Integer.parseInt(itemList.get(position).getAmount())));

        String[] date = itemList.get(position).getPayment_date().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
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

       // holder.txt_time.setText(itemList.get(position).getPayment_time());

        holder.linName.setVisibility(View.VISIBLE);
        holder.txt_name.setText(itemList.get(position).getCustomer_name());

        if(itemList.get(position).getFile_type().equals("")){
            holder.layout_demo.setVisibility(View.GONE);
        }
        else {
            holder.layout_demo.setVisibility(View.VISIBLE);
        }


        holder.layout_demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemList.get(position).getFile_type().endsWith("image/png") || itemList.get(position).getFile_type().endsWith("image/png") ||
                        itemList.get(position).getFile_type().endsWith("image/jpg") || itemList.get(position).getFile_type().endsWith("image/jpeg")){
                    context.startActivity(new Intent(context, Activity_ImageViewer.class)
                            .putExtra("img_url",itemList.get(position).getPayment_attachment()));
                }else if (itemList.get(position).getFile_type().endsWith("application/pdf")){
                    context.startActivity(new Intent(context, PdfViewActivity.class)
                            .putExtra("pdf_url",itemList.get(position).getPayment_attachment())
                            .putExtra("only_load",true));
                }
            }
        });


        if (!itemList.get(position).getRemarks().equals("")) {
            holder.txt_remarks.setVisibility(View.VISIBLE);
            holder.txt_remarks.setText(itemList.get(position).getRemarks());
        } else {
            holder.txt_remarks.setVisibility(View.GONE);
        }

        holder.view_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
                return true;
            }
        });

        /*holder.view_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_amount, txt_date, txt_time, txt_name, txt_remarks;
        View view_main;
        LinearLayout linName,layout_demo;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_amount = (TextView) itemView.findViewById(R.id.txt_amount);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            linName =  itemView.findViewById(R.id.linName);
            txt_remarks = (TextView) itemView.findViewById(R.id.txt_remarks);
            view_main = itemView.findViewById(R.id.view_main);
            layout_demo = itemView.findViewById(R.id.layout_demo);
        }
    }

    public void OnItemLongClickListener(final OnItemLongClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLongClickListener {

        void onItemClick(int position);
    }

}

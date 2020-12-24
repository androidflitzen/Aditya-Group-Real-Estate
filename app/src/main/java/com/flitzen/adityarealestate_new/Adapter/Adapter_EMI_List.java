package com.flitzen.adityarealestate_new.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Loan_Details;
import com.flitzen.adityarealestate_new.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Adapter_EMI_List extends RecyclerView.Adapter<Adapter_EMI_List.ViewHolder> {

    Context context;
    ArrayList<Item_Loan_Details.Item_Loan_EMI> itemList = new ArrayList<>();
    OnItemLongClickListener mItemClickListener;

    public Adapter_EMI_List(Context context, ArrayList<Item_Loan_Details.Item_Loan_EMI> itemList) {
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
        holder.layout_demo.setVisibility(View.GONE);
        holder.txt_amount.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(itemList.get(position).getEMI_Amount())));

        String[] date = itemList.get(position).getEMI_Date().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
        //holder.txt_date.setText(date[0] + " " + mm + " " + date[2]);

        if(itemList.get(position).getEMI_Date()!=null){

            try {
                SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-mm-dd");
                SimpleDateFormat formatOut = new SimpleDateFormat("dd MMM,yyyy");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(formatIn.parse(itemList.get(position).getEMI_Date()));
                String newDate = formatOut.format(calendar.getTime());
                holder.txt_date.setText(newDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(itemList.get(position).getEMI_time()!=null){

            try {
                SimpleDateFormat formatIn = new SimpleDateFormat("hh:mm:ss");
                SimpleDateFormat formatOut = new SimpleDateFormat("hh:mm a");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(formatIn.parse(itemList.get(position).getEMI_time()));
                String newDate = formatOut.format(calendar.getTime());
                holder.txt_time.setText(newDate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

       // holder.txt_time.setText(itemList.get(position).getEMI_time());

        holder.linName.setVisibility(View.VISIBLE);
        holder.txt_name.setText(itemList.get(position).getCustomer_name());

        if (!itemList.get(position).getLoan_Remarks().equals("")) {
            holder.txt_remarks.setVisibility(View.VISIBLE);
            holder.txt_remarks.setText(itemList.get(position).getLoan_Remarks());
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

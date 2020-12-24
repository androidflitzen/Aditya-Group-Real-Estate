package com.flitzen.adityarealestate_new.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Task_List;
import com.flitzen.adityarealestate_new.R;

import java.util.ArrayList;

public class Adapter_Tasks_List extends RecyclerView.Adapter<Adapter_Tasks_List.ViewHolder> {

    ArrayList<Item_Task_List> itemList = new ArrayList<>();
    Context context;
    boolean value;
    OnItemClickListener mItemClickListener;

    public Adapter_Tasks_List(Context context, ArrayList<Item_Task_List> itemList, Boolean value) {
        this.context = context;
        this.itemList = itemList;
        this.value = value;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tasks_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.txt_task_subject.setText(itemList.get(position).getSubject());
        holder.txt_task_description.setText(itemList.get(position).getDescription());

        String[] date = itemList.get(position).getTask_date().split("-");
        String month = date[1];
        String mm = Helper.getMonth(month);
        holder.txt_task_date.setText(date[0] + " " + mm + " " + date[2] + ", " + itemList.get(position).getTask_time());
        holder.txt_task_time.setText(itemList.get(position).getTask_time());
        holder.txt_task_status.setText(itemList.get(position).getStatus());



        if (itemList.get(position).getStatus().trim().equals("Pending")) {

            GradientDrawable bgShape = (GradientDrawable) holder.txt_task_status.getBackground();
            bgShape.setColor(context.getResources().getColor(R.color.pending_bg));

        } else if (itemList.get(position).getStatus().trim().equals("Completed")) {

            GradientDrawable bgShape = (GradientDrawable) holder.txt_task_status.getBackground();
            bgShape.setColor(context.getResources().getColor(R.color.approw_bg));

        }

        holder.view_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_task_subject, txt_task_status, txt_task_description, txt_task_date, txt_task_time;
        View view_main;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_task_subject = (TextView) itemView.findViewById(R.id.txt_task_subject);
            txt_task_status = (TextView) itemView.findViewById(R.id.txt_task_status);
            txt_task_description = (TextView) itemView.findViewById(R.id.txt_task_description);
            txt_task_date = (TextView) itemView.findViewById(R.id.txt_task_date);
            txt_task_time = (TextView) itemView.findViewById(R.id.txt_task_time);
            view_main = itemView.findViewById(R.id.view_main);
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

}

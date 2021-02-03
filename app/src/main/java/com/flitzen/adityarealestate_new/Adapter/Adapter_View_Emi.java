package com.flitzen.adityarealestate_new.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flitzen.adityarealestate_new.Items.Items_View_EMI;
import com.flitzen.adityarealestate_new.R;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Jolly Rathod  on 11/15/2018.
 */

public class Adapter_View_Emi extends BaseAdapter {

    Context context;
    List<Items_View_EMI> itemList = new ArrayList<>();
    private LayoutInflater inflater;

    public Adapter_View_Emi(Context context, List<Items_View_EMI> iteamArray) {

        this.context = context;
        this.itemList = iteamArray;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {

        TextView emi_amount, emi_date, emi_position, txt_remarks;
        View mainView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Adapter_View_Emi.ViewHolder viewHolder;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            viewHolder = new Adapter_View_Emi.ViewHolder();
            convertView = inflater.inflate(R.layout.view_emi_adapter, null);

            viewHolder.txt_remarks = (TextView) convertView.findViewById(R.id.txt_remarks_emi_adapter);
            viewHolder.emi_amount = (TextView) convertView.findViewById(R.id.txt_amount_emi_adapter);
            viewHolder.emi_date = (TextView) convertView.findViewById(R.id.txt_date_emi_adapter);
            viewHolder.emi_position = (TextView) convertView.findViewById(R.id.txt_position_emi_adapter);
            viewHolder.mainView = convertView.findViewById(R.id.view_main_emi_adapter);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (Adapter_View_Emi.ViewHolder) convertView.getTag();
        }

        if (isOdd(position)) {
            viewHolder.mainView.setBackgroundColor(Color.parseColor("#FFF2F2F2"));
        } else {
            viewHolder.mainView.setBackgroundColor(context.getResources().getColor(R.color.whiteText1));
        }
        DecimalFormat formatter = new DecimalFormat("#,##,###");
        String amount = formatter.format(Integer.parseInt(itemList.get(position).getEmi_amount()));

        try {
            SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat formatOut = new SimpleDateFormat("dd MMM , yyyy hh:mm a");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formatIn.parse(itemList.get(position).getEmi_date()));
            String newDate = formatOut.format(calendar.getTime());
          //  viewHolder.emi_date.setText(itemList.get(position).getEmi_date());
            viewHolder.emi_date.setText(newDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        viewHolder.emi_amount.setText("\u20B9 " + amount);
        viewHolder.emi_position.setText(String.valueOf(position + 1));
        if (itemList.get(position).getEmi_remark().equals("")) {
            viewHolder.txt_remarks.setVisibility(View.GONE);
        } else {
            viewHolder.txt_remarks.setVisibility(View.VISIBLE);
            viewHolder.txt_remarks.setText(itemList.get(position).getEmi_remark());
        }
        return convertView;
    }

    boolean isOdd(int val) {
        return (val & 0x01) != 0;
    }
}

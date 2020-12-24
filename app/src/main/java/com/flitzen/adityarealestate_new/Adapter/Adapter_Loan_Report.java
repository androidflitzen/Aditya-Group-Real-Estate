package com.flitzen.adityarealestate_new.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Items_Loan_Reports;
import com.flitzen.adityarealestate_new.R;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Jolly Rathod on 11/15/2018.
 */

public class Adapter_Loan_Report extends BaseAdapter {

    private List<Items_Loan_Reports> itemlist;
    private Context context;
    private LayoutInflater inflater;
    String rupee;
    DecimalFormat formatter;

    public Adapter_Loan_Report(Context context, List<Items_Loan_Reports> itemArray) {

        this.context = context;
        this.itemlist = itemArray;
        rupee = context.getResources().getString(R.string.rupee);
        formatter = new DecimalFormat(Helper.DECIMAL_FORMATE);
    }

    @Override
    public int getCount() {
        return itemlist.size();
    }

    @Override
    public Object getItem(int position) {
        return itemlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    static class ViewHolder {

        TextView txtMonth, txtEmi, txtInterest, txtPrincipal, txtAmount, txtTotalAmount;
        View mainView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewholder;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.loan_report_adapter, null);

            viewholder = new ViewHolder();

            viewholder.txtMonth = (TextView) convertView.findViewById(R.id.txt_loan_report_a_month);
            viewholder.txtEmi = (TextView) convertView.findViewById(R.id.txt_loan_report_a_emi);
            viewholder.txtInterest = (TextView) convertView.findViewById(R.id.txt_loan_report_a_interest);
            viewholder.txtPrincipal = (TextView) convertView.findViewById(R.id.txt_loan_report_a_principal);
            viewholder.txtAmount = (TextView) convertView.findViewById(R.id.txt_loan_report_a_amount);
            viewholder.txtTotalAmount = (TextView) convertView.findViewById(R.id.txt_loan_report_a_total_amount);
            viewholder.mainView = convertView.findViewById(R.id.view_loan_report_a_main);

            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        if (isOdd(position)) {
            viewholder.mainView.setBackgroundColor(context.getResources().getColor(R.color.old_divider));
        } else {
            viewholder.mainView.setBackgroundColor(context.getResources().getColor(R.color.whiteText1));
        }

        if (position == 0) {
            viewholder.txtMonth.setText("");
            viewholder.txtEmi.setText("");
            viewholder.txtPrincipal.setText("");
            viewholder.txtInterest.setText("");
            viewholder.txtAmount.setText(rupee + formatter.format(Helper.getRoundValue(itemlist.get(position).getrAmont())));
            viewholder.txtTotalAmount.setText(rupee + formatter.format(Helper.getRoundValue(itemlist.get(position).getrTotalAmount())));
        } else {
            viewholder.txtMonth.setText(String.valueOf(itemlist.get(position).getrMonth()));
            viewholder.txtEmi.setText(rupee + formatter.format(Helper.getRoundValue(itemlist.get(position).getrEMI())));
            viewholder.txtPrincipal.setText(rupee + formatter.format(Helper.getRoundValue(itemlist.get(position).getrPrincipal())));
            viewholder.txtInterest.setText(rupee + formatter.format(Helper.getRoundValue(itemlist.get(position).getrInterest())));
            viewholder.txtAmount.setText(rupee + formatter.format(Helper.getRoundValue(itemlist.get(position).getrAmont())));
            viewholder.txtTotalAmount.setText(rupee + formatter.format(Helper.getRoundValue(itemlist.get(position).getrTotalAmount())));
        }

        return convertView;
    }

    boolean isOdd(int val) {
        return (val & 0x01) != 0;
    }
}

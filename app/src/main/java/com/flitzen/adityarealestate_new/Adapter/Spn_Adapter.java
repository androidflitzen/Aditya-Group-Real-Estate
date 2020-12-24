package com.flitzen.adityarealestate_new.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.flitzen.adityarealestate_new.R;

import java.util.ArrayList;
import java.util.List;

public class Spn_Adapter extends BaseAdapter {

    Context context;
    List<String> itemList = new ArrayList<>();

    private LayoutInflater inflater;

    public Spn_Adapter(Context context, List<String> iteamArray) {

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
        TextView txtName;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = inflater.inflate(R.layout.adapter_spn, null);

            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txt_spn_a);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(Html.fromHtml(itemList.get(position)));

        return convertView;
    }
}

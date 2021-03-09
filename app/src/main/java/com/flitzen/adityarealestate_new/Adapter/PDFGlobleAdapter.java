package com.flitzen.adityarealestate_new.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.flitzen.adityarealestate_new.Activity.GlobalListPDFActivity;
import com.flitzen.adityarealestate_new.Activity.ListPDFActivity;
import com.flitzen.adityarealestate_new.R;

import java.io.File;
import java.util.ArrayList;


public class PDFGlobleAdapter extends ArrayAdapter<File> {


    Context context;
    ViewHolder viewHolder;
    ArrayList<File> al_pdf;
    ArrayList<File> mOriginalValues;

    public PDFGlobleAdapter(Context context, ArrayList<File> al_pdf) {
        super(context, R.layout.adapter_pdf, al_pdf);
        this.context = context;
        this.al_pdf = al_pdf;
        this.mOriginalValues = al_pdf;

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
       /* if (al_pdf.size() > 0) {
            return al_pdf.size();
        } else {
            return 0;
        }*/
        if(getCount() > 0){
            return getCount();
        }else{
            return super.getViewTypeCount();
        }
    }

    @Override
    public int getCount() {
        if (al_pdf.size() > 0) {
            return al_pdf.size();
        } else {
            return 0;
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {


        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_pdf, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_filename = (TextView) view.findViewById(R.id.tv_name);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();

        }

        viewHolder.tv_filename.setText(al_pdf.get(position).getName());
        return view;

    }

    public class ViewHolder {

        TextView tv_filename;


    }

    public void updateList(ArrayList<File> al_pdf){
        this.al_pdf = al_pdf;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                al_pdf = (ArrayList<File>) results.values; // has the filtered values
                GlobalListPDFActivity.fileList=al_pdf;
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<File> FilteredArrList = new ArrayList<File>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<File>(al_pdf); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = mOriginalValues.get(i).getName();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(mOriginalValues.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
}



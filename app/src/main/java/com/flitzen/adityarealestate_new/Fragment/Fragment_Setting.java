package com.flitzen.adityarealestate_new.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flitzen.adityarealestate_new.Activity.Activity_Manage_Plots;
import com.flitzen.adityarealestate_new.Activity.Activity_Manage_Properties;
import com.flitzen.adityarealestate_new.Activity.Activity_Manage_Sites;
import com.flitzen.adityarealestate_new.R;

public class Fragment_Setting extends Fragment {

    View btn_manage_sites,btn_manage_plots, btn_manage_properties;

    public Fragment_Setting() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<b>" + getResources().getString(R.string.app_setting) + "<b>"));

        init(view);

        btn_manage_sites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Activity_Manage_Sites.class));
                getActivity().overridePendingTransition(0, 0);
               // getActivity().overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });

        btn_manage_plots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Activity_Manage_Plots.class));
                getActivity().overridePendingTransition(0, 0);
                //getActivity().overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });

        btn_manage_properties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Activity_Manage_Properties.class));
                getActivity().overridePendingTransition(0, 0);
               // getActivity().overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });

        return view;
    }

    private void init(View view) {

        btn_manage_sites = view.findViewById(R.id.btn_manage_sites);
        btn_manage_plots = view.findViewById(R.id.btn_manage_plots);
        btn_manage_properties = view.findViewById(R.id.btn_manage_properties);

    }
}
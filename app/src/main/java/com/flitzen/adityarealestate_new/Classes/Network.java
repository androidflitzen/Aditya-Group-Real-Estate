package com.flitzen.adityarealestate_new.Classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.flitzen.adityarealestate_new.R;



public class Network {
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isOnline = (networkInfo != null && networkInfo.isConnected());
        if (!isOnline)
        new CToast(context).simpleToast(context.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();

        return isOnline;
    }
}

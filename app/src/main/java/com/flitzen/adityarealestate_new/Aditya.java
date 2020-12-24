package com.flitzen.adityarealestate_new;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.StrictMode;
import androidx.annotation.NonNull;

import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Classes.SharePref;
import com.flitzen.adityarealestate_new.Items.Item_Plot_List;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

public class Aditya extends Application {

    public static int POSITION;
    public static String ID = "";
    public static String NAME = "";
    SharedPreferences sharedPreferences;
    //public static ArrayList<Item_Customer_List> itemListCustomer = new ArrayList<>();

    public static ArrayList<Item_Plot_List> itemListPlot = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = SharePref.getSharePref(getApplicationContext());
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        if (sharedPreferences.getString(SharePref.FIREBASE_TOKEN, "").equals("")) {
                            if (task.getResult() != null) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(SharePref.FIREBASE_TOKEN, task.getResult().getToken());
                                editor.commit();
                                Utils.showLog("==== firbase token " + task.getResult().getToken());
                            }
                        }

                    }
                });
    }
}

package com.flitzen.adityarealestate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flitzen.adityarealestate.Activity.Activity_Home;
import com.flitzen.adityarealestate.Activity.Activity_Login;
import com.flitzen.adityarealestate.Classes.SharePref;

import io.fabric.sdk.android.Fabric;

public class Splash_Screen extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private int appVersion;
    ImageView ivSplash_logo;

    Animation animZoomout;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.color_statusbar));
        }*/

        sharedPreferences = SharePref.getSharePref(Splash_Screen.this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        appVersion = getVersionCode(Splash_Screen.this);
        ivSplash_logo = findViewById(R.id.ivSplash_logo);

        animZoomout = AnimationUtils.loadAnimation(Splash_Screen.this, R.anim.zoom_in);

        startanimation();




       // readStoragePermission();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                checkPermission();

               /* if (sharedPreferences.getBoolean(SharePref.isLoggedIn, false)) {
                    startActivity(new Intent(Splash_Screen.this, Activity_Home.class));
                    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                } else {
                    startActivity(new Intent(Splash_Screen.this, Activity_Login.class));
                    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                }
                finish();


                //next();
                if (sharedPreferences.getBoolean(SharePref.isLoggedIn, false)) {
                    startActivity(new Intent(Splash_Screen.this, Activity_Home.class));
                    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                } else {
                    startActivity(new Intent(Splash_Screen.this, Activity_Login.class));
                    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                }
                finish();*/

            }
        }, 1500);
    }

    private void startanimation() {

        ivSplash_logo.setAnimation(animZoomout);
    }

    /*public void getVersionCode() {

        appVersion = getVersionCode(Splash_Screen.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.APP_VERSION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);

                    String result_m = jsonObject.getString("result");
                    int jsonVersion = jsonObject.getInt("version");
                    //Log.e("old Version:", String.valueOf(appVersion));
                    //Log.e("Version:", String.valueOf(jsonVersion));

                    if (appVersion >= jsonVersion) {
                        if (sharedPreferences.getBoolean(SharePref.isLoggedIn, false)) {
                            startActivity(new Intent(Splash_Screen.this, Activity_Home.class));
                            overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                        } else {
                            startActivity(new Intent(Splash_Screen.this, Activity_Login.class));
                            overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                        }
                        finish();
                    } else {
                        Dialog_update();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(Splash_Screen.this);
        queue.add(stringRequest);

    }*/

    public void Dialog_update() {

        LayoutInflater localView = LayoutInflater.from(Splash_Screen.this);
        View promptsView = localView.inflate(R.layout.dialog_update, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(Splash_Screen.this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(false);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final Button update = (Button) promptsView.findViewById(R.id.btn_update);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                        | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
                        | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }

            }
        });
        alertDialog.show();
    }

    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private boolean isGranted(int permission) {
        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void readStoragePermission() {
        int checkpermissions = ContextCompat.checkSelfPermission(Splash_Screen.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        boolean status = isGranted(checkpermissions);
        if (status) {
            wirteStoragePermission();
        } else {
            ActivityCompat.requestPermissions(Splash_Screen.this, permissions, 001);
        }
    }

    @SuppressLint("NewApi")
    public void wirteStoragePermission() {


        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("Ravi", "Permission is granted");
            ActivityCompat.requestPermissions(Splash_Screen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 002);
            //calendarPermission();
        } else {

            Log.v("Ravi", "Permission is revoked");
            ActivityCompat.requestPermissions(Splash_Screen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 002);

        }



        /*int checkpermissions = ContextCompat.checkSelfPermission(Splash_Screen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        boolean status = isGranted(checkpermissions);
        if (status) {

            calendarPermission();

        } else {
            ActivityCompat.requestPermissions(Splash_Screen.this, permissions, 002);
        }*/
    }

    private void calendarPermission() {
        int checkpermissions = ContextCompat.checkSelfPermission(Splash_Screen.this, Manifest.permission.READ_CALENDAR);
        String[] permissions = {Manifest.permission.READ_CALENDAR};
        boolean status = isGranted(checkpermissions);
        if (status) {
            phoneStoragePermission();
        } else {
            ActivityCompat.requestPermissions(Splash_Screen.this, permissions, 003);
        }
    }

  /*  public void audioStoragePermission() {
        int checkpermissions = ContextCompat.checkSelfPermission(Splash_Screen.this, Manifest.permission.RECORD_AUDIO);
        String[] permissions = {Manifest.permission.RECORD_AUDIO};
        boolean status = isGranted(checkpermissions);
        if (status) {
            phoneStoragePermission();
        } else {
            ActivityCompat.requestPermissions(Splash_Screen.this, permissions, 004);
        }
    }*/

    public void phoneStoragePermission() {
        int checkpermissions = ContextCompat.checkSelfPermission(Splash_Screen.this, Manifest.permission.READ_PHONE_STATE);
        String[] permissions = {Manifest.permission.READ_PHONE_STATE};
        boolean status = isGranted(checkpermissions);
        if (status) {
            //next();
            if (sharedPreferences.getBoolean(SharePref.isLoggedIn, false)) {
                startActivity(new Intent(Splash_Screen.this, Activity_Home.class));
              //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                overridePendingTransition(0, 0);
            } else {
                startActivity(new Intent(Splash_Screen.this, Activity_Login.class));
              //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                overridePendingTransition(0, 0);
            }
        } else {
            ActivityCompat.requestPermissions(Splash_Screen.this, permissions, 004);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                // When request is cancelled, the results array are empty
                if (
                        (grantResults.length > 0) &&
                                (grantResults[0]
                                        + grantResults[1]
                                        == PackageManager.PERMISSION_GRANTED
                                )
                ) {
                    // Permissions are granted
                    //Toast.makeText(SplashScreen.this,"Permissions granted.",Toast.LENGTH_SHORT).show();
                    if (sharedPreferences.getBoolean(SharePref.isLoggedIn, false)) {
                        startActivity(new Intent(Splash_Screen.this, Activity_Home.class));
                      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                        overridePendingTransition(0, 0);
                    } else {
                        startActivity(new Intent(Splash_Screen.this, Activity_Login.class));
                       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                        overridePendingTransition(0, 0);
                    }
                } else {
                    // Permissions are denied
                    Toast.makeText(Splash_Screen.this, "Permissions denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

       /* if (requestCode == 001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                wirteStoragePermission();
            } else {
                new CToast(this).simpleToast("Without STORAGE PERMISSION Aditya Group not open", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                readStoragePermission();
            }
        }
        if (requestCode == 002) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                calendarPermission();
                Log.v("ravi", "Permission: " + permissions[0] + "was " + grantResults[0]);
                //next();

            } else {
                new CToast(this).simpleToast("Without STORAGE PERMISSION Aditya Group not open", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                wirteStoragePermission();
            }
        }
        if (requestCode == 003) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                phoneStoragePermission();
            } else {
                new CToast(this).simpleToast("Without CALENDER PERMISSION Aditya Group not open", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                calendarPermission();
            }
        }

        if (requestCode == 004) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //next();
                if (sharedPreferences.getBoolean(SharePref.isLoggedIn, false)) {
                    startActivity(new Intent(Splash_Screen.this, Activity_Home.class));
                    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                } else {
                    startActivity(new Intent(Splash_Screen.this, Activity_Login.class));
                    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                }
            } else {
                new CToast(this).simpleToast("Without READ PHONE STATE PERMISSION Aditya Group not open", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                phoneStoragePermission();
            }
        }*/
    }

    /*public void next() {
        if (sharedPreferences.getBoolean(SharePref.isLoggedIn, false)) {
            startActivity(new Intent(Splash_Screen.this, Activity_Home.class));
            overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        } else {
            startActivity(new Intent(Splash_Screen.this, Activity_Login.class));
            overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        }
        finish();
    }*/


    protected void checkPermission() {
        if (ContextCompat.checkSelfPermission(Splash_Screen.this, Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
                Splash_Screen.this, Manifest.permission.READ_CALENDAR)
                + ContextCompat.checkSelfPermission(
                Splash_Screen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(
                Splash_Screen.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Do something, when permissions not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    Splash_Screen.this, Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    Splash_Screen.this, Manifest.permission.READ_CALENDAR)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    Splash_Screen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    Splash_Screen.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(Splash_Screen.this);
                builder.setMessage("Camera, Read Contacts and Write External" +
                        " Storage permissions are required to do the task.");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                Splash_Screen.this,
                                new String[]{
                                        Manifest.permission.CAMERA,
                                        Manifest.permission.READ_CALENDAR,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE

                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        Splash_Screen.this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_CALENDAR,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        } else {
            // Do something, when permissions are already granted
            //Toast.makeText(Splash_Screen.this, "Permissions already granted", Toast.LENGTH_SHORT).show();
            if (sharedPreferences.getBoolean(SharePref.isLoggedIn, false)) {
                startActivity(new Intent(Splash_Screen.this, Activity_Home.class));
               // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                overridePendingTransition(0, 0);
                finish();
            } else {
                startActivity(new Intent(Splash_Screen.this, Activity_Login.class));
              //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                overridePendingTransition(0, 0);
                finish();
            }
        }
    }
}

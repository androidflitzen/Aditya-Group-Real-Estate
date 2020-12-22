package com.flitzen.adityarealestate.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.view.GravityCompat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.flitzen.adityarealestate.Aditya;
import com.flitzen.adityarealestate.Classes.SharePref;
import com.flitzen.adityarealestate.Fragment.Fragment_Dashboard;
import com.flitzen.adityarealestate.R;
import com.flitzen.adityarealestate.Splash_Screen;
import com.google.android.material.navigation.NavigationView;

public class Activity_Home extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Activity mActivity;

    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerList;

    NavigationView navigationView;
    public static int onBackPress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActivity = Activity_Home.this;

        sharedPreferences = SharePref.getSharePref(mActivity);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (LinearLayout) findViewById(R.id.left_drawer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.new_menu_icon);

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/

        replaceFragment(new Fragment_Dashboard());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            switch (onBackPress) {
                case 0:
                    new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                            .setTitle("Exit")
                            .setMessage("Are you sure you want to exit?")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).setNegativeButton("NO", null).show();

                    break;
                case 1:
                    onBackPress = 0;
                    Fragment_Dashboard frag;
                    FragmentManager fm1 = getSupportFragmentManager();
                    FragmentTransaction ft1 = fm1.beginTransaction();
                    frag = new Fragment_Dashboard();
                    ft1.replace(R.id.frame_layout, frag);
                    ft1.commit();
                    overridePendingTransition(0, 0);
                   // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                    break;
            }
        }
    }

    public void replaceFragment(Fragment fragment) {
        //onBackPress = 1;
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        overridePendingTransition(0, 0);
        //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }

    public void onmenuItem(View view) {

        int id = view.getId();

        if (id == R.id.nav_home) {

            replaceFragment(new Fragment_Dashboard());

        } else if (id == R.id.nav_plotings) {

           /* startActivity(new Intent(mActivity, Activity_Sites_List.class));
          //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out); */

            startActivity(new Intent(mActivity, Activity_Plot_ActiveList.class));
            overridePendingTransition(0, 0);
          //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

        } else if (id == R.id.nav_rentals) {

            startActivity(new Intent(mActivity, Activity_Rent_List.class));
            overridePendingTransition(0, 0);
          //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

        } else if (id == R.id.nav_customer) {

            startActivity(new Intent(mActivity, Activity_Customer_List.class));
            overridePendingTransition(0, 0);
         //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

        } else if (id == R.id.nav_emi_recovery) {

            startActivity(new Intent(mActivity, Activity_Pending_EMI.class));
            overridePendingTransition(0, 0);
          //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

        } else if (id == R.id.nav_regular_recovery) {

            startActivity(new Intent(mActivity, Activity_Regular_Recovery_List.class));
            overridePendingTransition(0, 0);
         //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

        } else if (id == R.id.nav_load_application) {

            startActivity(new Intent(mActivity, Activity_Admin_All_LoanApplication.class));
            overridePendingTransition(0, 0);
         //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

        } else if (id == R.id.nav_calculator) {

            startActivity(new Intent(mActivity, Activity_EMI_Calculation.class));
            overridePendingTransition(0, 0);
         //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

       /* } else if (id == R.id.nav_Tasks) {

            startActivity(new Intent(mActivity, Activity_Tasks_List.class));
          //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);*/

        } else if (id == R.id.nav_logout) {

            new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                    .setTitle(Html.fromHtml("<b> Logout </b>"))
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();

                            Aditya.ID = "";
                            Aditya.NAME = "";

                            Intent intent = new Intent(mActivity, Activity_Login.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                         //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                        }
                    }).setNegativeButton("No", null).show();
        }

        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_home_screen_drawer, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }

        return super.onOptionsItemSelected(item);
    }

    /* @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            replaceFragment(new Fragment_Dashboard());
        }
       *//* if (id == R.id.nav_setting) {
            replaceFragment(new Fragment_Setting());
        }*//*

        if (id == R.id.nav_logout) {
            new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                    .setTitle(Html.fromHtml("<b> Logout </b>"))
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();

                            Aditya.ID="";
                            Aditya.NAME="";

                            Intent intent = new Intent(Activity_Home.this, Activity_Login.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                        }
                    }).setNegativeButton("No", null).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    } */
}
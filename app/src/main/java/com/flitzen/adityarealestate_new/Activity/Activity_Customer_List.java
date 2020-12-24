package com.flitzen.adityarealestate_new.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.flitzen.adityarealestate_new.Fragment.ActivateCustomerFragment;
import com.flitzen.adityarealestate_new.Fragment.DeactivateCustomerFragment;
import com.flitzen.adityarealestate_new.Fragment.Plot_Active_Fragment;
import com.flitzen.adityarealestate_new.Fragment.Plot_Deactive_Fragment;
import com.flitzen.adityarealestate_new.Activity.Activity_Customer_Add;
import com.flitzen.adityarealestate_new.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class Activity_Customer_List extends AppCompatActivity {
    TabLayout tabs;
    FloatingActionButton fab_add_customer;
    int REQUEST_ADD = 01;
    TextView tvCustomerActive,tvCustomerDeactive;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        getSupportActionBar().setTitle(Html.fromHtml("Customers"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // tabs=findViewById(R.id.tabs);
        tvCustomerActive=findViewById(R.id.tvCustomerActive);
        tvCustomerDeactive=findViewById(R.id.tvCustomerDeactive);
       // tabs.addTab(tabs.newTab().setText("Activate"));
       // tabs.addTab(tabs.newTab().setText("Deactivate"));
       // tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        pushFragment(new ActivateCustomerFragment());


        tvCustomerActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCustomerActive.setBackground(getResources().getDrawable(R.drawable.task_bg_title));
                tvCustomerDeactive.setBackground(getResources().getDrawable(R.drawable.trans_tab_bg));
                pushFragment(new ActivateCustomerFragment());
            }
        });


        tvCustomerDeactive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvCustomerActive.setBackground(getResources().getDrawable(R.drawable.trans_tab_bg));
                tvCustomerDeactive.setBackground(getResources().getDrawable(R.drawable.task_bg_title));
                pushFragment(new DeactivateCustomerFragment());
            }
        });

       /* tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

        fab_add_customer = (FloatingActionButton) findViewById(R.id.fab_add_customer);

        fab_add_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Customer_List.this, Activity_Customer_Add.class);
                intent.putExtra("TYPE", "ADD");
                startActivityForResult(intent, REQUEST_ADD);
                overridePendingTransition(0, 0);
               // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            }
        });
    }

    private void selectFragment(int position) {
        if (position==0){
            pushFragment(new ActivateCustomerFragment());
        }else if (position==1){
            pushFragment(new DeactivateCustomerFragment());
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_deactive_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_deactive_customer) {

            startActivityForResult(new Intent(mActivity, Activity_Deactive_Customer_List.class), 002);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Activity_Customer_List.this, Activity_Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }

    private boolean pushFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    //.setCustomAnimations(R.anim.feed_in, R.anim.feed_out)
                    .setCustomAnimations(0, 0)
                    .replace(R.id.fragment_container, fragment)
                    //.addToBackStack("fragment")
                    .commit();
            return true;
        }
        return false;
    }
}

package com.flitzen.adityarealestate.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate.Adapter.Adapter_Tasks_List;
import com.flitzen.adityarealestate.Adapter.View_pager_adapter;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Fragment.Fragment_Completed_Task;
import com.flitzen.adityarealestate.Fragment.Fragment_Pending_Task;
import com.flitzen.adityarealestate.Items.Item_Task_List;
import com.flitzen.adityarealestate.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_Tasks_List extends AppCompatActivity {

    ProgressDialog prd;
    Activity mActivity;

    public static final String PENDING = "Pending", COMPLETED = "Completed";
    private TabLayout tabView;
    private ViewPager viewPager;

    FloatingActionButton fab_add_sites;
    SwipeRefreshLayout swipe_refresh;
    RecyclerView rec_sites_list;
    Adapter_Tasks_List adapter_tasks_list;
    ArrayList<Item_Task_List> itemList = new ArrayList<>();
    ArrayList<Item_Task_List> itemListTemp = new ArrayList<>();

    private EditText edtSearch;
    private ImageView imgClearSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_list);

        getSupportActionBar().setTitle(Html.fromHtml("ALL TASKS LIST"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Tasks_List.this;


        tabView = (TabLayout) findViewById(R.id.tabview_task);
        viewPager = (ViewPager) findViewById(R.id.viewPager_all_task);

        setupViewPager();
        tabView.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(4);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabView));
        tabView.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    /*    tabView.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
*/
        edtSearch = (EditText) findViewById(R.id.edt_search);
        imgClearSearch = (ImageView) findViewById(R.id.img_clear_search);

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                edtSearch.setText(null);
                edtSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

                getPendingTaskList();

            }
        });

        rec_sites_list = (RecyclerView) findViewById(R.id.rec_sites_list);
        rec_sites_list.setLayoutManager(new LinearLayoutManager(this));
        rec_sites_list.setHasFixedSize(true);
        adapter_tasks_list = new Adapter_Tasks_List(mActivity, itemList, false);
        rec_sites_list.setAdapter(adapter_tasks_list);

        fab_add_sites = (FloatingActionButton) findViewById(R.id.fab_add_sites);

        fab_add_sites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_task_add, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);

                final EditText edt_task_sub = (EditText) promptsView.findViewById(R.id.edt_task_sub);
                final EditText edt_task_desc = (EditText) promptsView.findViewById(R.id.edt_task_desc);

                TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                Button btn_add_task = (Button) promptsView.findViewById(R.id.btn_add_task);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_add_task.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edt_task_sub.getText().toString().trim().equals("")) {
                            edt_task_sub.setError("Site Name");
                            edt_task_sub.requestFocus();
                            return;
                        } else if (edt_task_desc.getText().toString().trim().equals("")) {
                            edt_task_desc.setError("Site Address");
                            edt_task_desc.requestFocus();
                            return;
                        } else {
                            alertDialog.dismiss();
                            addTask(edt_task_sub.getText().toString().trim(), edt_task_desc.getText().toString().trim());
                        }
                    }
                });

                alertDialog.show();

            }
        });

        adapter_tasks_list.setOnItemClickListener(new Adapter_Tasks_List.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {


                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_task_info, null);

                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();

                final TextView txt_task_status = (TextView) promptsView.findViewById(R.id.txt_task_status);
                final TextView txt_task_subject = (TextView) promptsView.findViewById(R.id.txt_task_subject);
                final TextView txt_task_desc = (TextView) promptsView.findViewById(R.id.txt_task_desc);
                final TextView txt_task_date = (TextView) promptsView.findViewById(R.id.txt_task_date);
                final TextView txt_task_time = (TextView) promptsView.findViewById(R.id.txt_task_time);

                View view_complete_task = promptsView.findViewById(R.id.view_complete_task);
                View view_remove_task = promptsView.findViewById(R.id.view_remove_task);

                txt_task_status.setText(itemList.get(position).getStatus());
                txt_task_subject.setText(itemList.get(position).getSubject());
                txt_task_desc.setText(itemList.get(position).getDescription());
                txt_task_date.setText(itemList.get(position).getTask_date());
                txt_task_time.setText(itemList.get(position).getTask_time());

                if (itemList.get(position).getStatus().equals("Completed")) {
                    view_complete_task.setVisibility(View.GONE);
                    txt_task_status.setBackgroundColor(getResources().getColor(R.color.approw_bg));
                } else {
                    view_complete_task.setVisibility(View.VISIBLE);
                    txt_task_status.setBackgroundColor(getResources().getColor(R.color.pending_bg));
                }

                view_complete_task.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        completeTask(itemList.get(position).getId());
                    }
                });

                view_remove_task.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        deleteTask(itemList.get(position).getId());
                    }
                });

                alertDialog.show();

            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int ii, int i1, int i2) {
                String word = edtSearch.getText().toString().trim().toLowerCase();
                itemList.clear();
                if (word.trim().isEmpty()) {
                    itemList.addAll(itemListTemp);
                    adapter_tasks_list.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < itemListTemp.size(); i++) {
                        if (itemListTemp.get(i).getSubject().toLowerCase().contains(word)) {
                            itemList.add(itemListTemp.get(i));
                        } else if (itemListTemp.get(i).getStatus().toLowerCase().contains(word)) {
                            itemList.add(itemListTemp.get(i));
                        }
                    }
                    adapter_tasks_list.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    imgClearSearch.setVisibility(View.VISIBLE);
                else
                    imgClearSearch.setVisibility(View.GONE);

            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        imgClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText(null);
                edtSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });

        getPendingTaskList();

    }

    private void setupViewPager() {

        View_pager_adapter adapter = new View_pager_adapter(getSupportFragmentManager());
        adapter.addFrag(getFragment(new Fragment_Pending_Task(), PENDING), "PENDING");
        adapter.addFrag(getFragment(new Fragment_Completed_Task(), COMPLETED), "COMPLETED");

        viewPager.setAdapter(adapter);
        tabView.setupWithViewPager(viewPager);

    }

    public Fragment getFragment(Fragment fragment, String data) {
        Bundle b = new Bundle();
        b.putString("TYPE", data);
        fragment.setArguments(b);
        return fragment;
    }

    public void getPendingTaskList() {
        //showPrd();
        swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.TASKS + "?type=List", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //hidePrd();
                swipe_refresh.setRefreshing(false);
                try {
                    itemList.clear();
                    itemListTemp.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Response Sites List", response);

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("task_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Task_List item = new Item_Task_List();
                            item.setId(object.getString("id"));
                            item.setSubject(object.getString("Subject"));
                            item.setDescription(object.getString("Description"));
                            item.setTask_date(object.getString("Task_date"));
                            item.setTask_time(object.getString("Task_time"));
                            item.setStatus(object.getString("Status"));

                            itemList.add(item);
                            itemListTemp.add(item);
                        }
                        adapter_tasks_list.notifyDataSetChanged();

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hidePrd();
                swipe_refresh.setRefreshing(false);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void addTask(final String task_subject, final String task_desc) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TASKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Response Tasks Add", response);

                    if (jsonObject.getInt("result") == 1) {

                        getPendingTaskList();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("type", "Add");
                params.put("Subject", task_subject);
                params.put("Description", task_desc);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void completeTask(final String task_id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TASKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Response Task complete", response);

                    if (jsonObject.getInt("result") == 1) {

                        getPendingTaskList();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("type", "Complete");
                params.put("id", task_id);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void deleteTask(final String task_id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.TASKS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Response task delete", response);

                    if (jsonObject.getInt("result") == 1) {

                        getPendingTaskList();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("type", "Delete");
                params.put("id", task_id);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void showPrd() {
        prd = new ProgressDialog(mActivity);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }

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
        overridePendingTransition(0, 0);
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}

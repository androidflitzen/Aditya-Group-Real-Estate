package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.flitzen.adityarealestate_new.Adapter.Adapter_Sites_List;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.Item_Sites_List;

import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.Task_Reminder.activity.TaskListActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_Sites_List extends AppCompatActivity {

    ProgressDialog prd;
    Activity mActivity;

    FloatingActionButton fab_add_sites;
    SwipeRefreshLayout swipe_refresh;
    RecyclerView rec_sites_list;
    Adapter_Sites_List adapter_sites_list;
    ArrayList<Item_Sites_List> itemList = new ArrayList<>();
    ArrayList<Item_Sites_List> itemListTemp = new ArrayList<>();

    private EditText edtSearch;
    private ImageView imgClearSearch, ivback, ivAddReminder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sites_list);

      /*  getSupportActionBar().setTitle(Html.fromHtml("ALL SITES LIST"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); */

        mActivity = Activity_Sites_List.this;

        edtSearch = (EditText) findViewById(R.id.edt_search);
        imgClearSearch = (ImageView) findViewById(R.id.img_clear_search);
        ivAddReminder = (ImageView) findViewById(R.id.ivAddReminder);
        ivback = (ImageView) findViewById(R.id.ivback);

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ivAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, TaskListActivity.class);
                startActivity(intent);
            }
        });

        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                edtSearch.setText(null);
                edtSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);

                getSiteList();

            }
        });

        rec_sites_list = (RecyclerView) findViewById(R.id.rec_sites_list);
        rec_sites_list.setLayoutManager(new LinearLayoutManager(this));
        rec_sites_list.setHasFixedSize(true);
        adapter_sites_list = new Adapter_Sites_List(mActivity, itemList, false);
        rec_sites_list.setAdapter(adapter_sites_list);

        fab_add_sites = (FloatingActionButton) findViewById(R.id.fab_add_sites);

        fab_add_sites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_sites_add, null);

                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);

                final EditText edt_site_name = (EditText) promptsView.findViewById(R.id.edt_site_name);
                final EditText edt_site_address = (EditText) promptsView.findViewById(R.id.edt_site_address);
                final EditText edt_site_purchase_price = (EditText) promptsView.findViewById(R.id.edt_site_purchase_price);
                final EditText edt_site_size = (EditText) promptsView.findViewById(R.id.edt_site_size);

                TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                Button btn_add_site = (Button) promptsView.findViewById(R.id.btn_add_site);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_add_site.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edt_site_name.getText().toString().trim().equals("")) {
                            edt_site_name.setError("Site Name");
                            edt_site_name.requestFocus();
                            return;
                        } else if (edt_site_address.getText().toString().trim().equals("")) {
                            edt_site_address.setError("Site Address");
                            edt_site_address.requestFocus();
                            return;
                        } else if (edt_site_purchase_price.getText().toString().trim().equals("")) {
                            edt_site_purchase_price.setError("Site Purchase Price");
                            edt_site_purchase_price.requestFocus();
                            return;
                        } else if (edt_site_size.getText().toString().trim().equals("")) {
                            edt_site_size.setError("Site Size");
                            edt_site_size.requestFocus();
                            return;
                        } else {
                            alertDialog.dismiss();
                            addSite(edt_site_name.getText().toString().trim(), edt_site_address.getText().toString().trim(), edt_site_purchase_price.getText().toString().trim(), edt_site_size.getText().toString().trim());
                        }
                    }
                });

                alertDialog.show();

            }
        });

        adapter_sites_list.setOnItemClickListener(new Adapter_Sites_List.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {

               /* final CharSequence[] item_Name = {"View Plots", "Edit Site"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mActivity);
                builder.setTitle("Select Option");
                builder.setItems(item_Name, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int item) {
                        if (item == 0) {

                            Intent intent = new Intent(mActivity, Activity_Plots_List.class);
                            intent.putExtra("id", itemList.get(position).getId());
                            intent.putExtra("name", itemList.get(position).getSite_name());
                            startActivity(intent);
                            overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

                        } else if (item == 1) {

                            LayoutInflater localView = LayoutInflater.from(mActivity);
                            View promptsView = localView.inflate(R.layout.dialog_sites_edit, null);

                            final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
                            alertDialogBuilder.setView(promptsView);
                            final AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.setCancelable(false);

                            final TextView txt_site_name = (TextView) promptsView.findViewById(R.id.txt_site_name);
                            final TextView txt_site_address = (TextView) promptsView.findViewById(R.id.txt_site_address);
                            final EditText edt_site_purchase_price = (EditText) promptsView.findViewById(R.id.edt_site_purchase_price);
                            final EditText edt_site_size = (EditText) promptsView.findViewById(R.id.edt_site_size);

                            TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                            Button btn_add_site = (Button) promptsView.findViewById(R.id.btn_add_site);

                            txt_site_name.setText(itemList.get(position).getSite_name());
                            txt_site_name.setTag(itemList.get(position).getId());
                            txt_site_address.setText(itemList.get(position).getSite_address());

                            edt_site_purchase_price.setText(itemList.get(position).getSite_price());
                            edt_site_size.setText(itemList.get(position).getSite_size());

                            btn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                }
                            });

                            btn_add_site.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (edt_site_purchase_price.getText().toString().trim().equals("")) {
                                        edt_site_purchase_price.setError("Site Purchase Price");
                                        edt_site_purchase_price.requestFocus();
                                        return;
                                    } else if (edt_site_size.getText().toString().trim().equals("")) {
                                        edt_site_size.setError("Site Size");
                                        edt_site_size.requestFocus();
                                        return;
                                    } else {
                                        alertDialog.dismiss();
                                        editSite(txt_site_name.getTag().toString().trim(), txt_site_name.getText().toString().trim(), txt_site_address.getText().toString().trim(), edt_site_purchase_price.getText().toString().trim(), edt_site_size.getText().toString().trim());
                                    }
                                }
                            });

                            alertDialog.show();

                        }
                    }
                });

                android.support.v7.app.AlertDialog alert = builder.create();
                alert.show();*/

                Intent intent = new Intent(mActivity, Activity_Plots_List.class);
                intent.putExtra("id", itemList.get(position).getId());
                intent.putExtra("name", itemList.get(position).getSite_name());
                intent.putExtra("TYPE", "ADD");
                startActivity(intent);
                overridePendingTransition(0, 0);
                //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
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
                    adapter_sites_list.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < itemListTemp.size(); i++) {
                        if (itemListTemp.get(i).getSite_name().toLowerCase().contains(word)) {
                            itemList.add(itemListTemp.get(i));
                        } else if (itemListTemp.get(i).getSite_address().contains(word)) {
                            itemList.add(itemListTemp.get(i));
                        }
                    }
                    adapter_sites_list.notifyDataSetChanged();
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

        getSiteList();

    }

    public void getSiteList() {
        //showPrd();
        swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.SITES + "?type=List", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //hidePrd();
                swipe_refresh.setRefreshing(false);
                try {
                    itemList.clear();
                    itemListTemp.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Sites List", response);

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("sites_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Sites_List item = new Item_Sites_List();
                            item.setId(object.getString("id"));
                            item.setSite_name(object.getString("site_name"));
                            item.setSite_address(object.getString("site_address"));
                            item.setSite_size(object.getString("size"));
                            item.setSite_price(object.getString("purchase_price"));

                            itemList.add(item);
                            itemListTemp.add(item);
                        }
                        adapter_sites_list.notifyDataSetChanged();

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

    public void addSite(final String site_name, final String site_Address, final String site_Price, final String site_Size) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.SITES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //  Log.e("Response Sites Add", response);

                    if (jsonObject.getInt("result") == 1) {

                        getSiteList();
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
                params.put("site_name", site_name);
                params.put("site_address", site_Address);
                params.put("purchase_price", site_Price);
                params.put("size", site_Size);
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

    public void editSite(final String site_id, final String site_name, final String site_Address, final String site_Price, final String site_Size) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.EDIT_SITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Sites Edit", response);

                    if (jsonObject.getInt("result") == 1) {

                        getSiteList();
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
                params.put("site_id", site_id);
                params.put("site_name", site_name);
                params.put("site_address", site_Address);
                params.put("purchase_price", site_Price);
                params.put("size", site_Size);
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

  /*  @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }*/

  /*  @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        Intent intent = new Intent(Activity_Sites_List.this,Activity_Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }*/
}

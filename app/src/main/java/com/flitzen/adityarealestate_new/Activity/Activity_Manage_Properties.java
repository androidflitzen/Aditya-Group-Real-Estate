package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.flitzen.adityarealestate_new.Adapter.Adapter_Property_List;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Items.Item_Property_List;
import com.flitzen.adityarealestate_new.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_Manage_Properties extends AppCompatActivity {

    Activity mActivity;
    ProgressDialog prd;
    RecyclerView rec_property_list;
    FloatingActionButton fab_add_property;
    Adapter_Property_List adapter_property_list;
    ArrayList<Item_Property_List> itemList = new ArrayList<>();
    ArrayList<Item_Property_List> itemListTemp = new ArrayList<>();

    private EditText edtSearch;
    private ImageView imgClearSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_properties);

        getSupportActionBar().setTitle(Html.fromHtml("Manage Properties"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Manage_Properties.this;

        edtSearch = (EditText) findViewById(R.id.edt_search);
        imgClearSearch = (ImageView) findViewById(R.id.img_clear_search);

        fab_add_property = (FloatingActionButton) findViewById(R.id.fab_add_property);
        rec_property_list = (RecyclerView) findViewById(R.id.rec_property_list);

        fab_add_property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_property_add, null);

                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);

                final EditText edt_property_name = (EditText) promptsView.findViewById(R.id.edt_property_name);
                final EditText edt_property_address = (EditText) promptsView.findViewById(R.id.edt_property_address);

                TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                Button btn_add_site = (Button) promptsView.findViewById(R.id.btn_add_property);

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_add_site.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (edt_property_name.getText().toString().trim().equals("")) {
                            edt_property_name.setError("Property Name");
                            edt_property_name.requestFocus();
                            return;
                        } else if (edt_property_address.getText().toString().trim().equals("")) {
                            edt_property_address.setError("Property Address");
                            edt_property_address.requestFocus();
                            return;
                        } else {
                            alertDialog.dismiss();
                            addProperty(edt_property_name.getText().toString().trim(), edt_property_address.getText().toString().trim());
                        }
                    }
                });

                alertDialog.show();

            }
        });

        rec_property_list.setLayoutManager(new LinearLayoutManager(this));
        rec_property_list.setHasFixedSize(true);
        adapter_property_list = new Adapter_Property_List(mActivity, itemList, true);
        rec_property_list.setAdapter(adapter_property_list);

        adapter_property_list.setOnItemClickListener(new Adapter_Property_List.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_property_info, null);

                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                AlertDialog alertDialog = alertDialogBuilder.create();

                TextView txt_property_name = (TextView) promptsView.findViewById(R.id.txt_property_name);
                TextView txt_property_address = (TextView) promptsView.findViewById(R.id.txt_property_address);
                TextView txt_property_address_m = (TextView) promptsView.findViewById(R.id.txt_property_address_m);
                TextView txt_cust_name = (TextView) promptsView.findViewById(R.id.txt_cust_name);
                TextView txt_rent = (TextView) promptsView.findViewById(R.id.txt_rent);
                TextView txt_hired_date = (TextView) promptsView.findViewById(R.id.txt_hired_date);

                LinearLayout ll_not_assign = (LinearLayout) promptsView.findViewById(R.id.ll_not_assign);
                LinearLayout ll_assign = (LinearLayout) promptsView.findViewById(R.id.ll_assign);

                if (itemList.get(position).getIs_hired().equals("1")) {
                    ll_assign.setVisibility(View.VISIBLE);
                    ll_not_assign.setVisibility(View.GONE);
                } else {
                    ll_assign.setVisibility(View.GONE);
                    ll_not_assign.setVisibility(View.VISIBLE);
                }


                txt_property_name.setText(itemList.get(position).getProperty_name());
                txt_property_address.setText(itemList.get(position).getAddress());
                txt_property_address_m.setText(itemList.get(position).getAddress());
                txt_cust_name.setText(itemList.get(position).getCustomer_name());
                txt_rent.setText(itemList.get(position).getRent());
                txt_hired_date.setText(itemList.get(position).getHired_since());

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
                    adapter_property_list.notifyDataSetChanged();
                } else {
                    for (int i = 0; i < itemListTemp.size(); i++) {
                        if (itemListTemp.get(i).getProperty_name().toLowerCase().contains(word)) {
                            itemList.add(itemListTemp.get(i));
                        } else if (itemListTemp.get(i).getCustomer_name().contains(word)) {
                            itemList.add(itemListTemp.get(i));
                        }
                    }
                    adapter_property_list.notifyDataSetChanged();
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

        getPropertyList();
    }

    public void getPropertyList() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.PROPERTY + "?type=List", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {
                    itemList.clear();
                    itemListTemp.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Property List", response);

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("property");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Property_List item = new Item_Property_List();
                            item.setId(object.getString("id"));
                            item.setProperty_name(object.getString("property_name"));
                            item.setAddress(object.getString("address"));
                            item.setIs_hired(object.getString("is_hired"));
                            item.setCustomer_id(object.getString("customer_id"));
                            item.setCustomer_name(object.getString("customer_name"));
                            item.setRent(object.getString("rent"));
                            item.setHired_since(object.getString("hired_since"));

                            itemList.add(item);
                            itemListTemp.add(item);
                        }
                        adapter_property_list.notifyDataSetChanged();

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
                hidePrd();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void addProperty(final String site_name, final String site_Address) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.PROPERTY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Sites Add", response);

                    if (jsonObject.getInt("result") == 1) {

                        getPropertyList();
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
                params.put("property_name", site_name);
                params.put("address", site_Address);
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
      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}
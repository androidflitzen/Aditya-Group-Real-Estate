package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
import com.flitzen.adityarealestate_new.Adapter.Spn_Adapter;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Items.Item_Property_List;
import com.flitzen.adityarealestate_new.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Activity_Add_Payment_For_Rents extends AppCompatActivity {

    Activity mActivity;
    ProgressDialog prd;

    TextView spn_property, txt_cust_name, txt_mobile_no, txt_date;
    EditText edt_paid_amount, edt_remark;
    View btn_add_payment;

    int pending_amount;
    String time = "";

    ArrayList<Item_Property_List> itemListProperty = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payament_for_rents);

        getSupportActionBar().setTitle(Html.fromHtml("Add Payment For Property"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Add_Payment_For_Rents.this;
        spn_property = (TextView) findViewById(R.id.spn_property);
        txt_cust_name = (TextView) findViewById(R.id.txt_cust_name);
        txt_mobile_no = (TextView) findViewById(R.id.txt_mobile_no);
        txt_date = (TextView) findViewById(R.id.txt_date);

        edt_paid_amount = (EditText) findViewById(R.id.edt_paid_amount);
        edt_remark = (EditText) findViewById(R.id.edt_remark);

        btn_add_payment = findViewById(R.id.btn_add_payment);


        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        String hours = String.valueOf(hour);
        String min = String.valueOf(minute);

        if (hours.length() == 1) {
            hours = "0" + hours;
        }
        if (min.length() == 1) {
            min = "0" + min;
        }

        time = hours + ":" + min + ":00";

        //Toast.makeText(mActivity, "Time : " + hours + ":" + min + ":00", Toast.LENGTH_SHORT).show();


        spn_property.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (itemListProperty.size() == 0) {
                    getPropertyList();
                } else {
                    propertyDialog();
                }
            }
        });

        txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
        txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.pick_Date(mActivity, txt_date);
            }
        });

        btn_add_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spn_property.getText().toString().equals("Select Plot")) {
                    new CToast(mActivity).simpleToast("Please Select Property", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else if (edt_paid_amount.getText().toString().equals("")) {
                    edt_paid_amount.setError("Enter paid amount");
                    edt_paid_amount.requestFocus();
                    return;
                } else if (txt_date.getText().toString().equals("")) {
                    new CToast(mActivity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                }/* else if (Integer.parseInt(edt_paid_amount.getText().toString()) > pending_amount) {
                    edt_paid_amount.setError("You enter more then pending amount");
                    edt_paid_amount.requestFocus();
                    return;
                }*/ else {
                    addPaymentforProperty();
                }
            }
        });
    }

    public void addPaymentforProperty() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.PAYMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                   // Log.e("Response Plot Add", response);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {
                        finish();
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
                params.put("type", "Add_Rent");
                params.put("property_id", spn_property.getTag().toString());
                params.put("amount", edt_paid_amount.getText().toString());
                params.put("payment_date", txt_date.getTag().toString());
                params.put("payment_time", time);
                params.put("customer_id", txt_cust_name.getTag().toString());
                params.put("remarks", edt_remark.getText().toString());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    public void getPropertyList() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.PROPERTY + "?type=List", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {
                    itemListProperty.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Property List", response);

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("property");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            if (object.getString("is_hired").equals("1")) {
                                Item_Property_List item = new Item_Property_List();
                                item.setId(object.getString("id"));
                                item.setProperty_name(object.getString("property_name"));
                                item.setAddress(object.getString("address"));
                                item.setIs_hired(object.getString("is_hired"));
                                item.setCustomer_id(object.getString("customer_id"));
                                item.setCustomer_name(object.getString("customer_name"));
                                item.setContact_no(object.getString("contact_no"));
                                item.setRent(object.getString("rent"));
                                item.setHired_since(object.getString("hired_since"));

                                itemListProperty.add(item);
                            }
                        }

                        propertyDialog();

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

    public void propertyDialog() {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_spinner, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText edtSearchLocation = (EditText) promptsView.findViewById(R.id.edt_spn_search);
        final ListView list_location = (ListView) promptsView.findViewById(R.id.list_spn);

        edtSearchLocation.setHint("Search Property");
        final ArrayList<String> arrayListTemp = new ArrayList<>();
        final ArrayList<String> arrayListId = new ArrayList<>();
        final ArrayList<String> arrayListCustId = new ArrayList<>();
        final ArrayList<String> arrayListCustName = new ArrayList<>();
        final ArrayList<String> arrayListMobile = new ArrayList<>();
        final ArrayList<String> arrayListRent = new ArrayList<>();

        for (int i = 0; i < itemListProperty.size(); i++) {
            arrayListTemp.add(itemListProperty.get(i).getProperty_name());
            arrayListId.add(itemListProperty.get(i).getId());
            arrayListCustId.add(itemListProperty.get(i).getCustomer_id());
            arrayListCustName.add(itemListProperty.get(i).getCustomer_name());
            arrayListMobile.add(itemListProperty.get(i).getContact_no());
            arrayListRent.add(itemListProperty.get(i).getRent());
        }

        list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));

        edtSearchLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int position, int i1, int i2) {

                if (edtSearchLocation.getText().toString().trim().length() > 0) {
                    arrayListTemp.clear();
                    arrayListId.clear();

                    for (int j = 0; j < itemListProperty.size(); j++) {
                        String word = edtSearchLocation.getText().toString().toLowerCase();
                        if (itemListProperty.get(j).getProperty_name().toLowerCase().contains(word)) {
                            arrayListTemp.add(itemListProperty.get(j).getProperty_name());
                            arrayListId.add(itemListProperty.get(j).getId());
                            arrayListCustName.add(itemListProperty.get(j).getCustomer_name());
                            arrayListMobile.add(itemListProperty.get(j).getContact_no());
                            arrayListRent.add(itemListProperty.get(j).getRent());
                        }
                    }
                    list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));
                } else {
                    arrayListTemp.clear();
                    arrayListId.clear();

                    for (int i = 0; i < itemListProperty.size(); i++) {
                        arrayListTemp.add(itemListProperty.get(i).getProperty_name());
                        arrayListId.add(itemListProperty.get(i).getId());
                        arrayListCustName.add(itemListProperty.get(i).getCustomer_name());
                        arrayListMobile.add(itemListProperty.get(i).getContact_no());
                        arrayListRent.add(itemListProperty.get(i).getRent());
                    }
                    list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        list_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                spn_property.setText(arrayListTemp.get(position));
                spn_property.setTag(arrayListId.get(position));
                pending_amount = Integer.parseInt(arrayListRent.get(position));
                edt_paid_amount.setText(arrayListRent.get(position));
                txt_mobile_no.setText(arrayListMobile.get(position));
                txt_cust_name.setText(arrayListCustName.get(position));
                txt_cust_name.setTag(arrayListCustId.get(position));

                alertDialog.dismiss();

            }
        });

        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
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
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
       // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }
}

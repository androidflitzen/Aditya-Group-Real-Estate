package com.flitzen.adityarealestate_new.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;

import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Activity.Activity_Customer_Add;
import com.flitzen.adityarealestate_new.Activity.Activity_Customer_Details;
import com.flitzen.adityarealestate_new.Activity.Activity_ImageViewer;

import com.flitzen.adityarealestate_new.Activity.Activity_Plot_Details;
import com.flitzen.adityarealestate_new.Activity.Activity_Rent_Details;
import com.flitzen.adityarealestate_new.Activity.PdfCreatorPlotActivity;
import com.flitzen.adityarealestate_new.Activity.PdfCreatorRentActivity;
import com.flitzen.adityarealestate_new.Activity.PdfViewActivity;
import com.flitzen.adityarealestate_new.Activity.RentCustomerHistoryActivity;
import com.flitzen.adityarealestate_new.Activity.RentPaymentActivity;
import com.flitzen.adityarealestate_new.Activity.ViewPdfForAll;
import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.EncodeBased64Binary;
import com.flitzen.adityarealestate_new.Classes.FileUtils;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.ImageUtils;
import com.flitzen.adityarealestate_new.Classes.Permission;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Items.Customer;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate_new.Items.RentDetailsForPDF;
import com.flitzen.adityarealestate_new.MyFilePath;
import com.flitzen.adityarealestate_new.PDFUtility;
import com.flitzen.adityarealestate_new.PDFUtility_Rent;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressLint("ValidFragment")
public class RentPaymentListFragment extends Fragment {
    RecyclerView rec_purchased_plot_list;
    RentPaymentListAdapter mAdapterPlot;
    Activity mActivity;
    TextView tvNoActiveCustomer;
    ArrayList<Item_Plot_Payment_List> arrayListRentPayment = new ArrayList<>();
    FloatingActionButton fab_add_payment;
    String rent_amount = "", customer_id = "", property_id = "",status="";
    ProgressDialog prd;
    SwipeRefreshLayout swipe_refresh;
    //  TextView tvViewPaymentPDF;
    String file_url = "";
    String imageEncode = "", attachmentType = "", property_rent;
    int position;
    int UPLOAD_REQUEST = 001, SELECT_PICTURE = 002, CAMERA_REQUEST = 003, SELECT_AUDIO = 004, CAMERA_PERMISSION_CODE = 005, FILE_REQUEST_CODE = 101;
    private Uri capturedImageURI=Uri.parse("");
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    int ATTACH_ITEM_POS = 0;
    ImageUtils imageUtils;
    TextView tvUploadFile, tvUploadFile_update;
    boolean b_tvUploadFile = false;
    boolean b_tvUploadFile_update = false;
    ImageView ivUploadFile, img_delete, img_delete_update;
    String mime="";
    RentDetailsForPDF rentDetailsForPDF;
    String path="";

    public RentPaymentListFragment(String customer_id, int position, String property_id) {
        this.customer_id = customer_id;
        Log.e("customer_id Con  ", customer_id);

        this.position = position;
        this.property_id = property_id;
        // this.property_rent = property_rent;
    }

    public RentPaymentListFragment() {
    }

    @SuppressLint("ValidFragment")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rent_payment_list, null);
        initUI(view);
        getRentDetailMain();
        return view;
    }

    private void getRentDetailMain() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Properties").orderByKey();
        //databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                try {
                    if (dataSnapshot.exists()) {
                        rentDetailsForPDF=new RentDetailsForPDF();
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if (property_id.equals(npsnapshot.child("id").getValue().toString())) {
                                rentDetailsForPDF.setAddress(npsnapshot.child("address").getValue().toString());
                                rentDetailsForPDF.setPropertyName(npsnapshot.child("property_name").getValue().toString());
                                rentDetailsForPDF.setRent(npsnapshot.child("rent").getValue().toString());
                                rentDetailsForPDF.setHiredSince(npsnapshot.child("hired_since").getValue().toString());
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Test  ", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                new CToast(mActivity).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                hidePrd();

            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void initUI(View view) {
        mActivity = getActivity();
        imageUtils = new ImageUtils();

        property_id = getArguments().getString("property_id");
        position = Integer.parseInt(getArguments().getString("position"));
        customer_id = getArguments().getString("customer_id");
        status = getArguments().getString("status");

        //   customer_id = getIntent().getStringExtra("customer_id");


        swipe_refresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        rec_purchased_plot_list = (RecyclerView) view.findViewById(R.id.rec_purchased_plot_list);
        tvNoActiveCustomer = view.findViewById(R.id.tvNoActiveCustomer);
        rec_purchased_plot_list.setLayoutManager(new LinearLayoutManager(mActivity));

        rec_purchased_plot_list.setHasFixedSize(true);
        rec_purchased_plot_list.setNestedScrollingEnabled(false);

        mAdapterPlot = new RentPaymentListAdapter(mActivity, arrayListRentPayment, true);
        rec_purchased_plot_list.setAdapter(mAdapterPlot);

        RelativeLayout ivRentDetailPdf=((Activity_Rent_Details)getActivity()).findViewById(R.id.ivRentDetailPdf);
        ivRentDetailPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent=new Intent(getActivity(), PdfCreatorRentActivity.class);
                intent.putExtra("RentDetails",rentDetailsForPDF);
                intent.putExtra("paymentList",arrayListRentPayment);
                startActivity(intent);*/


                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                path = Environment.getExternalStorageDirectory().toString() + "/" + ts + "_payment_list.pdf";
                System.out.println("========path  " + path);
                try {
                    PDFUtility_Rent.createPdf(v.getContext(), new PDFUtility_Rent.OnDocumentClose() {
                        @Override
                        public void onPDFDocumentClose(File file) {
                            Toast.makeText(getActivity(), "Sample Pdf Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ViewPdfForAll.class);
                            intent.putExtra("path",path);
                            startActivity(intent);
                        }
                    }, getSampleData(), path, true, rentDetailsForPDF);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", "Error Creating Pdf");
                    Toast.makeText(v.getContext(), "Error Creating Pdf", Toast.LENGTH_SHORT).show();
                }
            }
        });

        RelativeLayout ivRentDeactivePdf=((Activity_Rent_Details)getActivity()).findViewById(R.id.ivRentDeactivePdf);
        ivRentDeactivePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent=new Intent(getActivity(), PdfCreatorRentActivity.class);
                intent.putExtra("RentDetails",rentDetailsForPDF);
                intent.putExtra("paymentList",arrayListRentPayment);
                startActivity(intent);*/

                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                path = Environment.getExternalStorageDirectory().toString() + "/" + ts + "_payment_list.pdf";
                System.out.println("========path  " + path);
                try {
                    PDFUtility_Rent.createPdf(v.getContext(), new PDFUtility_Rent.OnDocumentClose() {
                        @Override
                        public void onPDFDocumentClose(File file) {
                            Toast.makeText(getActivity(), "Sample Pdf Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ViewPdfForAll.class);
                            intent.putExtra("path",path);
                            startActivity(intent);
                        }
                    }, getSampleData(), path, true, rentDetailsForPDF);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", "Error Creating Pdf");
                    Toast.makeText(v.getContext(), "Error Creating Pdf", Toast.LENGTH_SHORT).show();
                }

            }
        });

        getPaymentRentCustomers();
        hidePrd();

        fab_add_payment = view.findViewById(R.id.fab_add_payment);
        fab_add_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddPaymentDialog();
/*                Intent intent = new Intent(getActivity(), RentPaymentActivity.class);
                startActivity(intent);*/
            }
        });
        // tvViewPaymentPDF = view.findViewById(R.id.tvViewPaymentPDF);

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onRefresh() {
                if (status.equals("0")) {
                    getPaymentRentCustomers();
                    // getRentDetail();
                } else {
                    getPaymentRentCustomers();
                    fab_add_payment.setVisibility(View.GONE);
                }
            }
        });

       /* tvViewPaymentPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file_url = API.PRINT_RENT_PAYMENT_URL+ Aditya.ID;
                startActivity(new Intent(mActivity, PdfViewActivity.class)
                        .putExtra("pdf_url", file_url));
                Utils.showLog("=== file_url "+file_url);
            }
        });*/

        if (status.equals("0")) {
            getPaymentRentCustomers();
            // getRentDetail();
        } else {
            getPaymentRentCustomers();
            fab_add_payment.setVisibility(View.GONE);
        }
    }

    private List<String[]> getSampleData() {

        List<String[]> temp = new ArrayList<>();
        int finalTotalAmount=0;
        for (int i = 0; i < arrayListRentPayment.size(); i++) {

            String data1="";
            String data2="";
            String data3="";
            String data4="";

            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat inputT = new SimpleDateFormat("hh:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
            SimpleDateFormat outputT = new SimpleDateFormat("hh:mm a");
            try {
                Date oneWayTripDate;
                Date oneWayTripDateT;
                oneWayTripDate = input.parse(arrayListRentPayment.get(i).getPayment_date());  // parse input
                oneWayTripDateT = inputT.parse(arrayListRentPayment.get(i).getPayment_time());  // parse input
                data1=output.format(oneWayTripDate) + " " + outputT.format(oneWayTripDateT);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(arrayListRentPayment.get(i).getCustomer_name()!=null){
                data2=arrayListRentPayment.get(i).getCustomer_name();
            }

            data3=arrayListRentPayment.get(i).getRemarks();
            data4=Helper.getFormatPrice(Integer.parseInt(arrayListRentPayment.get(i).getAmount()));

            temp.add(new String[] {data1,data2,data3,data4});
        }
        return temp;
    }

    private void openAddPaymentDialog() {

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view1 = inflater.inflate(R.layout.dialog_payment_add, null);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
        builder1.setView(view1);
        final AlertDialog dialog = builder1.create();

        final EditText edt_paid_amount = (EditText) view1.findViewById(R.id.edt_paid_amount);
        final EditText edt_remark = (EditText) view1.findViewById(R.id.edt_remark);
        final TextView txt_date = (TextView) view1.findViewById(R.id.txt_date);
        final TextView txt_time = (TextView) view1.findViewById(R.id.txt_time);
        TextView txt_next_date = (TextView) view1.findViewById(R.id.txt_next_date);
        final LinearLayout viewUploadFile = (LinearLayout) view1.findViewById(R.id.viewUploadFile);
        viewUploadFile.setVisibility(View.VISIBLE);
        ImageView img_close = (ImageView) view1.findViewById(R.id.img_close);

        Activity_Rent_Details activityRentDetails = (Activity_Rent_Details) getActivity();
//        String rents =
        edt_paid_amount.setText(activityRentDetails.getMyDataForFragment());


        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        viewUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b_tvUploadFile = true;
                showAttachImageOption();
            }
        });

        ivUploadFile = (ImageView) view1.findViewById(R.id.ivUploadFile);
        img_delete = (ImageView) view1.findViewById(R.id.img_delete);
        ivUploadFile.setVisibility(View.VISIBLE);
        tvUploadFile = (TextView) view1.findViewById(R.id.tvUploadFile);

        ivUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b_tvUploadFile = true;
                showAttachImageOption();
            }
        });

        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageEncode = "";

                int newHeight = 50; // New height in pixels
                int newWidth = 50; //

                ivUploadFile.getLayoutParams().height = newHeight;

                // Apply the new width for ImageView programmatically
                ivUploadFile.getLayoutParams().width = newWidth;


                ivUploadFile.requestLayout();

                ivUploadFile.setImageResource(R.drawable.ic_add_image);
                tvUploadFile.setVisibility(View.VISIBLE);
                tvUploadFile.setText("Upload File");
                img_delete.setVisibility(View.GONE);
            }
        });

        tvUploadFile.setVisibility(View.VISIBLE);
        tvUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b_tvUploadFile = true;

                showAttachImageOption();


            }
        });
        TextView btn_cancel = (TextView) view1.findViewById(R.id.btn_cancel);
        Button btn_add_payment = (Button) view1.findViewById(R.id.btn_add_payment);

        // edt_paid_amount.setText(rent_amount);
        txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
        txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.pick_Date(mActivity, txt_date);
            }
        });

        txt_next_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.pick_Date(mActivity, txt_next_date);

            }
        });
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        String formattedDate = dateFormat.format(new Date()).toString();
        txt_time.setText(formattedDate);

//        Calendar mcurrentTime = Calendar.getInstance();
//        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//        int minute = mcurrentTime.get(Calendar.MINUTE);
//        txt_time.setText(hour + ":" + minute);

        txt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Calendar mcurrentTime = Calendar.getInstance();
//                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
//                int minute = mcurrentTime.get(Calendar.MINUTE);
//                TimePickerDialog mTimePicker;
//                mTimePicker = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//
//                        String hours = String.valueOf(selectedHour);
//                        String min = String.valueOf(selectedMinute);
//
//                        if (hours.length() == 1) {
//                            hours = "0" + hours;
//                        }
//                        if (min.length() == 1) {
//                            min = "0" + min;
//                        }
//
//                        txt_time.setText(hours + ":" + min );
//                    }
//                }, hour, minute, true);//Yes 24 hour time
//                mTimePicker.setTitle("Select Time");
//                mTimePicker.show();


                 Calendar mcurrentTime = Calendar.getInstance();
                        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                                String hours = String.valueOf(selectedHour);
                                String min = String.valueOf(selectedMinute);

                                if (hours.length() == 1) {
                                    hours = "0" + hours;
                                }
                                if (min.length() == 1) {
                                    min = "0" + min;
                                }

                                txt_time.setText(hours + ":" + min + ":00");
                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();

               /* final Calendar myCalender = Calendar.getInstance();
                int hour = myCalender.get(Calendar.HOUR_OF_DAY);
                int minute = myCalender.get(Calendar.MINUTE);


                TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String am_pm = "";

                        Calendar datetime = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);

                        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                            am_pm = "AM";
                        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                            am_pm = "PM";

                        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";

                        txt_time.setText(strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);
                    }

                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, false);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                timePickerDialog.show();*/

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_add_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_paid_amount.getText().toString().equals("")) {
                    edt_paid_amount.setError("Enter paid amount");
                    edt_paid_amount.requestFocus();
                    return;
                } else if (txt_date.getText().toString().equals("")) {
                    new CToast(mActivity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else if (txt_time.getText().toString().equals("")) {
                    new CToast(mActivity).simpleToast("Select Time", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else if (txt_next_date.getText().toString().equals("")) {
                    new CToast(mActivity).simpleToast("Select Next Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else {
                    dialog.dismiss();
                    addPaymentForRent(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString(), Aditya.ID, edt_remark.getText().toString(), txt_next_date.getText().toString());
                }
            }
        });

        dialog.show();
    }

    public void getRentDetail() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.PROPERTY_DETAILS + "?property_id=" + Aditya.ID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideSwipeRefresh();
                try {
                    Log.w("response", response);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {
                        customer_id = jsonObject.getString("customer_id");
                        if (!jsonObject.getString("rent").equals("0")) {
                            rent_amount = jsonObject.getString("rent");
                        }
                        arrayListRentPayment.clear();

                        JSONArray jsonArrayPayment = jsonObject.getJSONArray("payments_list");
                        for (int j = 0; j < jsonArrayPayment.length(); j++) {
                            JSONObject object2 = jsonArrayPayment.getJSONObject(j);

                            Item_Plot_Payment_List payment = new Item_Plot_Payment_List();
                            payment.setId(object2.getString("id"));
                            payment.setAmount(object2.getString("amount"));
                            payment.setPayment_date(object2.getString("payment_date"));
                            payment.setPayment_time(object2.getString("payment_time"));
                            payment.setRemarks(object2.getString("remarks"));
                            payment.setCustomer_id(object2.getString("customer_id"));
                            payment.setCustomer_name(object2.getString("customer_name"));
                            payment.setPayment_attachment(object2.getString("payment_attachment"));
                            payment.setNext_payment_date(object2.getString("next_payment_date"));
                            arrayListRentPayment.add(payment);
                        }
                        if (arrayListRentPayment.size() > 0) {
                            //  tvViewPaymentPDF.setVisibility(View.VISIBLE);
                        } else {
                            // tvViewPaymentPDF.setVisibility(View.GONE);
                        }
                        mAdapterPlot.notifyDataSetChanged();
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
                hideSwipeRefresh();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    private void hideSwipeRefresh() {
        if (swipe_refresh != null && swipe_refresh.isRefreshing()) {
            swipe_refresh.setRefreshing(false);
        }
    }

    public void getPaymentRentCustomers() {

        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Payments").orderByKey();
        //databaseReference.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                hideSwipeRefresh();
                arrayListRentPayment.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if (npsnapshot.child("property_id").getValue().toString().equals(property_id)) {
                                if (customer_id.equals(npsnapshot.child("customer_id").getValue().toString())) {
                                    if(!(npsnapshot.child("payment_date").getValue().toString().equals("0000-00-00 00:00:00"))){
                                        Item_Plot_Payment_List payment = new Item_Plot_Payment_List();
                                        payment.setId(npsnapshot.child("id").getValue().toString());
                                        payment.setAmount(npsnapshot.child("amount").getValue().toString());
                                        if(npsnapshot.hasChild("file_type")){
                                            payment.setFileType(npsnapshot.child("file_type").getValue().toString());
                                        }
                                        else {
                                            payment.setFileType("");
                                        }
                                        // payment.setPayment_date(npsnapshot.child("payment_date").getValue().toString());

                                        try {
                                            DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                            Date d = f.parse(npsnapshot.child("payment_date").getValue().toString());
                                            DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                            DateFormat time = new SimpleDateFormat("hh:mm:ss");
                                            payment.setPayment_date(date.format(d));
                                            payment.setPayment_time(time.format(d));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        payment.setRemarks(npsnapshot.child("remarks").getValue().toString());
                                        payment.setCustomer_id(npsnapshot.child("customer_id").getValue().toString());
                                        payment.setPayment_attachment(npsnapshot.child("payment_attachment").getValue().toString());
                                        payment.setNext_payment_date(npsnapshot.child("next_payment_date").getValue().toString());
                                        String customerId = npsnapshot.child("customer_id").getValue().toString();

                                        Query query = databaseReference.child("Customers").orderByKey();
                                        // databaseReference.keepSynced(true);
                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                try {
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                            if (npsnapshot1.child("id").getValue().toString().equals(customerId)) {
                                                                String name = npsnapshot1.child("name").getValue().toString();
                                                                Log.e("Name  ", name);
                                                                payment.setCustomer_name(name);
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    Log.e("Ex   ", e.toString());
                                                }
                                                mAdapterPlot.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("error ", error.getMessage());
                                            }
                                        });
                                        arrayListRentPayment.add(payment);

                                    }
                                }
                            }

                        }
                        if (arrayListRentPayment.size() > 0) {
                            rec_purchased_plot_list.setVisibility(View.VISIBLE);
                            tvNoActiveCustomer.setVisibility(View.GONE);
                        } else {
                            rec_purchased_plot_list.setVisibility(View.GONE);
                            tvNoActiveCustomer.setVisibility(View.VISIBLE);
                        }
                        mAdapterPlot.notifyDataSetChanged();
                    }
                    else {
                        tvNoActiveCustomer.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e("Test  ", e.getMessage());
                    e.printStackTrace();
                    hidePrd();
                    hideSwipeRefresh();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                new CToast(getContext()).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                hidePrd();
                hideSwipeRefresh();
            }
        });
    }

    public void getPaymentRentCustomers1() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.PAYMENT_HISTORY_CUSTOMERS + "property_id=" + Aditya.ID + "&customer_id=" + customer_id, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.showLog("== PAYMENT_HISTORY_CUSTOMERS " + API.PAYMENT_HISTORY_CUSTOMERS + "property_id=" + Aditya.ID + "&customer_id=" + customer_id);
                Log.w("PAYMENT_HISTORY", API.PAYMENT_HISTORY_CUSTOMERS + "property_id=" + Aditya.ID + "&customer_id=" + customer_id);

                hidePrd();
                hideSwipeRefresh();
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    Log.w("payment_h", response);

                    if (jsonObject.getInt("result") == 1) {
                        arrayListRentPayment.clear();
                        JSONArray jsonArrayPayment = jsonObject.getJSONArray("payments_list");
                        for (int j = 0; j < jsonArrayPayment.length(); j++) {
                            JSONObject object2 = jsonArrayPayment.getJSONObject(j);
                            Item_Plot_Payment_List payment = new Item_Plot_Payment_List();
                            payment.setId(object2.getString("id"));
                            payment.setAmount(object2.getString("amount"));
                            payment.setPayment_date(object2.getString("payment_date"));
                            payment.setPayment_time(object2.getString("payment_time"));
                            payment.setRemarks(object2.getString("remarks"));
                            payment.setCustomer_id(object2.getString("customer_id"));
                            payment.setCustomer_name(object2.getString("customer_name"));
                            payment.setPayment_attachment(object2.getString("payment_attachment"));
                            payment.setNext_payment_date(object2.getString("next_payment_date"));
                            arrayListRentPayment.add(payment);
                        }
                        mAdapterPlot = new RentPaymentListAdapter(mActivity, arrayListRentPayment, true);
                        rec_purchased_plot_list.setAdapter(mAdapterPlot);
                       /* if (arrayListRentPayment.size() > 0) {
                            ll_plot.setVisibility(View.VISIBLE);
                        } else {
                            ll_plot.setVisibility(View.GONE);
                        }*/

                        //mAdapterPlot.notifyDataSetChanged();
                    } else {
                        hideSwipeRefresh();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        return;
                    }
                } catch (Exception e) {
                    hidePrd();
                    e.printStackTrace();
                    hideSwipeRefresh();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePrd();
                hideSwipeRefresh();

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    public void addPaymentForRent(final String date, final String time, final String amount, final String id, final String remark, String txt_next_date) {
        showPrd();

        if (String.valueOf(capturedImageURI).equals("")) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            String key = rootRef.child("Payments").push().getKey();

            Map<String, Object> map = new HashMap<>();
            map.put("amount", amount);
            map.put("next_payment_date", txt_next_date);
            map.put("payment_attachment", capturedImageURI.toString());
            map.put("file_type", mime);

            String finalTime = time.substring(0, 5);
            finalTime = finalTime + ":00";
            Log.e("Time  ", finalTime);
            map.put("payment_date", date + " " + finalTime);
            map.put("remarks", remark);
            Log.e("Customer Id   ", customer_id);
            map.put("customer_id", customer_id);
            map.put("id", key);
            map.put("plot_id", 0);
            map.put("property_id", id);
            map.put("customer_status", 0);

            //TODO
            map.put("rent_status", 0);
            map.put("payment_status", 0);

            rootRef.child("Payments").child(key).setValue(map).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hidePrd();
                    imageEncode = "";
                    capturedImageURI=Uri.parse("");
                    attachmentType = "";
                    mime="";
                    new CToast(mActivity).simpleToast("Payment add successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    getPaymentRentCustomers();
                    //getRentDetail();

                }

            }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hidePrd();
                    Toast.makeText(getContext(), "Please try later...", Toast.LENGTH_SHORT).show();
                }

            });
        }
        else {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            final StorageReference sRef = storageReference.child("/files" + "/" + UUID.randomUUID().toString());
            sRef.putFile(capturedImageURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                    String key = rootRef.child("Payments").push().getKey();

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("amount", amount);
                                    map.put("next_payment_date", txt_next_date);
                                    map.put("payment_attachment", uri.toString());
                                    map.put("file_type", mime);

                                    String finalTime = time.substring(0, 5);
                                    finalTime = finalTime + ":00";
                                    Log.e("Time  ", finalTime);
                                    map.put("payment_date", date + " " + finalTime);
                                    map.put("remarks", remark);
                                    Log.e("Customer Id   ", customer_id);
                                    map.put("customer_id", customer_id);
                                    map.put("id", key);
                                    map.put("plot_id", 0);
                                    map.put("property_id", id);
                                    map.put("customer_status", 0);

                                    //TODO
                                    map.put("rent_status", 0);
                                    map.put("payment_status", 0);

                                    rootRef.child("Payments").child(key).setValue(map).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            hidePrd();
                                            imageEncode = "";
                                            capturedImageURI=Uri.parse("");
                                            attachmentType = "";
                                            mime="";
                                            new CToast(mActivity).simpleToast("Payment add successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                            getPaymentRentCustomers();
                                            //getRentDetail();

                                        }

                                    }).addOnFailureListener(getActivity(), new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            hidePrd();
                                            Toast.makeText(getContext(), "Please try later...", Toast.LENGTH_SHORT).show();
                                        }

                                    });
                                }

                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            hidePrd();
                            Toast.makeText(getActivity(), "Please try later...", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            Log.e("progress  ", String.valueOf(progress));
                        }
                    });
        }


    }

    public void addPaymentForRent1(final String date, final String time, final String amount, final String id, final String remark, String txt_next_date) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.PAYMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Plot Add", response);
                    if (jsonObject.getInt("result") == 1) {
                        getPaymentRentCustomers();
                        // getRentDetail();
                        imageEncode = "";
                        attachmentType = "";
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
                params.put("property_id", id);
                params.put("amount", amount);
                params.put("payment_date", date);
                params.put("payment_time", time);
                params.put("customer_id", customer_id);
                params.put("remarks", remark);
                params.put("payment_attachment", imageEncode);
                params.put("attachment_type", attachmentType);
                params.put("next_payment_date", txt_next_date);
                Log.w("param", String.valueOf(params));
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

    public void showPrd() {
        prd = new ProgressDialog(mActivity);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {

        prd.dismiss();

    }

    public void showAttachImageOption() {

        CharSequence[] items_name = {"Capture Image", "Select From Gallery", "Select PDF"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Select Option");
        builder.setItems(items_name, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                ATTACH_ITEM_POS = item;
                if (Permission.hasPermissions(mActivity, permissions)) {
                    if (item == 0) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                        capturedImageURI = mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);
                        startActivityForResult(intent, CAMERA_REQUEST);

                    } else if (item == 1) {
              /*          Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);*/

                        Intent intent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, SELECT_PICTURE);

                    } else {
                        openFileChooser();
                    }
                } else {
                    Permission.requestPermissions(mActivity, permissions, CAMERA_PERMISSION_CODE);
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                if (b_tvUploadFile == true) {
                    b_tvUploadFile = false;
                    capturedImageURI = data.getData();
                    Uri mResultsBitmap = data.getData();


                    ContentResolver cR = getActivity().getContentResolver();
                    mime = cR.getType(capturedImageURI);

                    //tvUploadFile.setText("Upload " + data.getData().getLastPathSegment().substring(data.getData().getLastPathSegment().lastIndexOf("/")).replace("/", ""));
                    tvUploadFile.setText("Upload File");
                    tvUploadFile.setVisibility(View.GONE);

                    new ImageCompression(mActivity).execute(FileUtils.getPath(mActivity, capturedImageURI));

                    int newHeight = 500; // New height in pixels
                    int newWidth = 800; //

                    ivUploadFile.getLayoutParams().height = newHeight;

                    // Apply the new width for ImageView programmatically
                    ivUploadFile.getLayoutParams().width = newWidth;


                    ivUploadFile.requestLayout();

                    ivUploadFile.setImageURI(mResultsBitmap);


                    img_delete.setVisibility(View.VISIBLE);


                    // ivUploadFile.setImageURI(selectedImageUri);


                }
                else if (b_tvUploadFile_update == true) {
                    b_tvUploadFile_update = false;
                    capturedImageURI = data.getData();
                    Uri mResultsBitmap = data.getData();

                    ContentResolver cR = getActivity().getContentResolver();
                    mime = cR.getType(capturedImageURI);

                    // tvUploadFile_update.setText("Upload " + data.getData().getLastPathSegment().substring(data.getData().getLastPathSegment().lastIndexOf("/")).replace("/", ""));

                    new ImageCompression(mActivity).execute(FileUtils.getPath(mActivity, capturedImageURI));

                    int newHeight = 500; // New height in pixels
                    int newWidth = 800; //
                    //ivUploadFile.requestLayout();
                    tvUploadFile_update.setText("Upload File");
                    tvUploadFile_update.setVisibility(View.GONE);

                    ivUploadFile.getLayoutParams().height = newHeight;

                    // Apply the new width for ImageView programmatically
                    ivUploadFile.getLayoutParams().width = newWidth;

                    //ivUploadFile.setImageURI(selectedImageUri);


                    ivUploadFile.setImageURI(mResultsBitmap);
                    img_delete_update.setVisibility(View.VISIBLE);

                }
                else {
                    Uri selectedImageUri = data.getData();

                    ContentResolver cR = getActivity().getContentResolver();
                    mime = cR.getType(capturedImageURI);

                    new ImageCompression(mActivity).execute(FileUtils.getPath(mActivity, selectedImageUri));


                }

            } else if (requestCode == CAMERA_REQUEST) {
                if (b_tvUploadFile == true) {
                    b_tvUploadFile = false;

                    new ImageCompression(mActivity).execute(FileUtils.getPath(mActivity, capturedImageURI));
                    int newHeight = 500; // New height in pixels
                    int newWidth = 800; //

                    ivUploadFile.getLayoutParams().height = newHeight;

                    // Apply the new width for ImageView programmatically
                    ivUploadFile.getLayoutParams().width = newWidth;
                    ivUploadFile.setImageURI(capturedImageURI);
                    tvUploadFile.setVisibility(View.GONE);
                    img_delete.setVisibility(View.VISIBLE);

                } else if (b_tvUploadFile_update == true) {
                    b_tvUploadFile_update = false;

                    new ImageCompression(mActivity).execute(FileUtils.getPath(mActivity, capturedImageURI));
                    int newHeight = 500; // New height in pixels
                    int newWidth = 800; //

                    ivUploadFile.getLayoutParams().height = newHeight;

                    // Apply the new width for ImageView programmatically
                    ivUploadFile.getLayoutParams().width = newWidth;
                    ivUploadFile.setImageURI(capturedImageURI);
                    tvUploadFile_update.setVisibility(View.GONE);
                    img_delete_update.setVisibility(View.VISIBLE);

                }


                ContentResolver cR = getActivity().getContentResolver();
                mime = cR.getType(capturedImageURI);



            }
            if (requestCode == 1) {


                if (b_tvUploadFile == true) {
                    b_tvUploadFile = false;
                    ivUploadFile.setVisibility(View.GONE);
                    tvUploadFile.setVisibility(View.VISIBLE);

                    img_delete.setVisibility(View.VISIBLE);

                    try {


                        ArrayList<MediaFile> mediaFiles = new ArrayList<>();


                        mediaFiles.clear();
                        mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));

                        MediaFile mediaFile = mediaFiles.get(0);
                        Log.w("path_new", String.valueOf(mediaFile.getUri()));


                        //  String path1 = mediaFile.getUri().toString(); // "file:///mnt/sdcard/FileName.mp3"
                        // File file = new File(new URI(path1));


                        String id = DocumentsContract.getDocumentId(mediaFile.getUri());
                        InputStream inputStream = getContext().getContentResolver().openInputStream(mediaFile.getUri());

                        File file = new File(getContext().getCacheDir().getAbsolutePath() + "/" + id);
                        writeFile(inputStream, file);
                        String filePath1 = file.getAbsolutePath();
                        capturedImageURI=mediaFile.getUri();

                        ContentResolver cR = getActivity().getContentResolver();
                        mime = cR.getType(capturedImageURI);

                        System.out.println("=========mediaFile.getUri(    "+mediaFile.getUri());
                        Log.w("capturedImageURI  ",String.valueOf(capturedImageURI));
                        Log.w("path_new_file", filePath1);


                    /*String path = MyFilePath.getPath(Activity_Rent_Details.this, mediaFile.getUri());
                    Log.w("path_new", String.valueOf(path));*/
                        imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(filePath1);

                        if (imageEncode != null && !imageEncode.equalsIgnoreCase("")) {

                            attachmentType = ".pdf";
                            ivUploadFile.setImageResource(R.drawable.ic_add_image);
                            tvUploadFile.setText("Upload " + mediaFile.getName());

                        } else {
                            try {
                                // ArrayList<MediaFile> mediaFiles = new ArrayList<>();


                                mediaFiles.clear();
                                mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));

                                //MediaFile mediaFile = mediaFiles.get(0);
                                Log.w("path_new_e", String.valueOf(mediaFile.getUri()));

                                String path = MyFilePath.getPath(getContext(), mediaFile.getUri());

                                //ivUploadFile.setImageResource(R.drawable.ic_add_image);
                                //tvUploadFile.setText("Upload " + data.getData().getLastPathSegment().substring(data.getData().getLastPathSegment().lastIndexOf("/")).replace("/", ""));
                                //Toast.makeText(getApplicationContext(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                                ivUploadFile.setImageResource(R.drawable.ic_add_image);
                                tvUploadFile.setText("Upload " + mediaFile.getName());
                                //Toast.makeText(getApplicationContext(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                                imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);
                                attachmentType = "pdf";
                                if (imageEncode != null && !imageEncode.equalsIgnoreCase("")) {
                                    //uploadpdf();
                                } else {
                                    Toast.makeText(getContext(), "Can't upload wih drive", Toast.LENGTH_LONG).show();
                                }

                            } catch (IOException es) {
                                es.printStackTrace();
                            }
                        }


                        // uploadpdf();


                    } catch (Exception e) {
                        e.printStackTrace();


                    }





                   /* Uri filePath = data.getData();
                    if (String.valueOf(filePath).indexOf("com.android.providers.downloads.documents") != -1) {
                        Utils.showToast(getContext(), "Select PDF From Your Mobile Storage", R.color.red);
                    } else {

                        if(MyFilePath.getPath(getContext(), filePath) != null &&
                                !MyFilePath.getPath(getContext(), filePath).isEmpty() &&
                                !MyFilePath.getPath(getContext(), filePath).equalsIgnoreCase("")) {
                            String path = MyFilePath.getPath(getContext(), filePath);
                            try {
                                ivUploadFile.setImageResource(R.drawable.ic_add_image);
                                tvUploadFile.setText("Upload " + data.getData().getLastPathSegment().substring(data.getData().getLastPathSegment().lastIndexOf("/")).replace("/", ""));
                                //Toast.makeText(getApplicationContext(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                                imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);
                                attachmentType = "pdf";
                                //uploadpdf();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Utils.showToast(getContext(), "Select PDF From Your Mobile Storage", R.color.red);
                        }


                    }*/

                }
                else if (b_tvUploadFile_update == true) {
                    b_tvUploadFile_update = false;

                    try {

                        ivUploadFile.setVisibility(View.GONE);
                        tvUploadFile_update.setVisibility(View.VISIBLE);
                        img_delete_update.setVisibility(View.VISIBLE);
                        ArrayList<MediaFile> mediaFiles = new ArrayList<>();


                        mediaFiles.clear();
                        mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));

                        MediaFile mediaFile = mediaFiles.get(0);
                        Log.w("path_new", String.valueOf(mediaFile.getUri()));

                        capturedImageURI=mediaFile.getUri();

                        ContentResolver cR = getActivity().getContentResolver();
                        mime = cR.getType(capturedImageURI);

                        //  String path1 = mediaFile.getUri().toString(); // "file:///mnt/sdcard/FileName.mp3"
                        // File file = new File(new URI(path1));


                        String id = DocumentsContract.getDocumentId(mediaFile.getUri());
                        InputStream inputStream = getContext().getContentResolver().openInputStream(mediaFile.getUri());

                        File file = new File(getContext().getCacheDir().getAbsolutePath() + "/" + id);
                        writeFile(inputStream, file);
                        String filePath1 = file.getAbsolutePath();
                        Log.w("path_new_file", filePath1);

                    /*String path = MyFilePath.getPath(Activity_Rent_Details.this, mediaFile.getUri());
                    Log.w("path_new", String.valueOf(path));*/
                        imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(filePath1);

                        if (imageEncode != null && !imageEncode.equalsIgnoreCase("")) {

                            attachmentType = ".pdf";
                            ivUploadFile.setImageResource(R.drawable.ic_add_image);
                            tvUploadFile_update.setText("Upload " + mediaFile.getName());
                        } else {
                            try {
                                // ArrayList<MediaFile> mediaFiles = new ArrayList<>();


                                mediaFiles.clear();
                                mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));

                                //MediaFile mediaFile = mediaFiles.get(0);
                                Log.w("path_new_e", String.valueOf(mediaFile.getUri()));

                                String path = MyFilePath.getPath(getContext(), mediaFile.getUri());

                                //ivUploadFile.setImageResource(R.drawable.ic_add_image);
                                //tvUploadFile.setText("Upload " + data.getData().getLastPathSegment().substring(data.getData().getLastPathSegment().lastIndexOf("/")).replace("/", ""));
                                //Toast.makeText(getApplicationContext(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                                ivUploadFile.setImageResource(R.drawable.ic_add_image);
                                tvUploadFile_update.setText("Upload " + mediaFile.getName());
                                //Toast.makeText(getApplicationContext(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                                imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);
                                attachmentType = "pdf";
                                if (imageEncode != null && !imageEncode.equalsIgnoreCase("")) {
                                    //uploadpdf();
                                } else {
                                    Toast.makeText(getContext(), "Can't upload wih drive", Toast.LENGTH_LONG).show();
                                }

                            } catch (IOException es) {
                                es.printStackTrace();
                            }
                        }


                        // uploadpdf();


                    } catch (Exception e) {
                        e.printStackTrace();


                    }


               /*     Uri filePath = data.getData();
                    if (String.valueOf(filePath).indexOf("com.android.providers.downloads.documents") != -1) {
                        Utils.showToast(getContext(), "Select PDF From Your Mobile Storage", R.color.red);
                    } else {

                        if(MyFilePath.getPath(getContext(), filePath) != null &&
                                !MyFilePath.getPath(getContext(), filePath).isEmpty() &&
                                !MyFilePath.getPath(getContext(), filePath).equalsIgnoreCase("")) {
                            String path = MyFilePath.getPath(getContext(), filePath);
                            try {
                                ivUploadFile.setImageResource(R.drawable.ic_add_image);
                                tvUploadFile_update.setText("Upload " + data.getData().getLastPathSegment().substring(data.getData().getLastPathSegment().lastIndexOf("/")).replace("/", ""));
                                //Toast.makeText(getApplicationContext(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                                imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);
                                attachmentType = "pdf";
                                //uploadpdf();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Utils.showToast(getContext(), "Select PDF From Your Mobile Storage", R.color.red);
                        }


                    }*/

                } else {

                    Uri filePath = data.getData();
                    if (String.valueOf(filePath).indexOf("com.android.providers.downloads.documents") != -1) {
                        Utils.showToast(getContext(), "Select PDF From Your Mobile Storage", R.color.red);
                    } else {


                        if (MyFilePath.getPath(getContext(), filePath) != null &&
                                !MyFilePath.getPath(getContext(), filePath).isEmpty() &&
                                !MyFilePath.getPath(getContext(), filePath).equalsIgnoreCase("")) {
                            String path = MyFilePath.getPath(getContext(), filePath);
                            try {
                                //Toast.makeText(getApplicationContext(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                                imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);
                                attachmentType = "pdf";
                                //uploadpdf();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Utils.showToast(getContext(), "Select PDF From Your Mobile Storage", R.color.red);
                        }

                    }
                }


            }
        }
    }

    void writeFile(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void openFileChooser() {
        /*Intent intent = new Intent(mActivity, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowImages(false)
                .setShowAudios(false)
                .setShowFiles(true)
                .setShowVideos(false)
                .setSuffixes("pdf")
                .enableImageCapture(true)
                .setMaxSelection(1)
                .setSkipZeroSizeFiles(true)
                .build());
        startActivityForResult(intent, FILE_REQUEST_CODE);*/

        /*Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), 1);*/

        Intent intent = new Intent(getContext(), FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setSuffixes("pdf")
                .setShowFiles(true)
                .setShowImages(false)
                .setShowVideos(false)
                .setMaxSelection(1)
                .setRootPath(Environment.getExternalStorageDirectory().getPath() + "/Download")
                .build());
        startActivityForResult(intent, 1);

    }

    private Bitmap getRotateBitmap(Bitmap bitmap) throws IOException {
        InputStream input = mActivity.getContentResolver().openInputStream(capturedImageURI);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(capturedImageURI.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(bitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(bitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(bitmap, 270);
            default:
                return bitmap;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    class RentPaymentListAdapter extends RecyclerView.Adapter<RentPaymentListAdapter.ViewHolder> {

        Context context;
        ArrayList<Item_Plot_Payment_List> itemList = new ArrayList<>();
        ProgressDialog prd;
        boolean showFile;

        public RentPaymentListAdapter(Context context, ArrayList<Item_Plot_Payment_List> itemList, boolean showFile) {
            this.context = context;
            this.itemList = itemList;
            this.showFile = showFile;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_payment_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.txt_amount.setText(context.getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(itemList.get(position).getAmount())));
            if (itemList.get(position).getNext_payment_date() != null && itemList.get(position).getNext_payment_date().equalsIgnoreCase("")) {
                holder.txt_next_date.setText("-");
            } else if (itemList.get(position).getNext_payment_date() != null) {

                String[] date = itemList.get(position).getNext_payment_date().split("-");
                String month = date[1];
                String mm = Helper.getMonth(month);
                holder.txt_next_date.setText(date[0] + " " + mm + " " + date[2]);

                //holder.txt_next_date.setText(itemList.get(position).getNext_payment_date());
            }

            String[] date = itemList.get(position).getPayment_date().split("-");
            String month = date[1];
            String mm = Helper.getMonth(month);
            //holder.txt_date.setText(date[0] + " " + mm + " " + date[2]);

            if(itemList.get(position).getPayment_date()!=null){

                try {
                    SimpleDateFormat formatIn = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat formatOut = new SimpleDateFormat("dd MMM,yyyy");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(formatIn.parse(itemList.get(position).getPayment_date()));
                    String newDate = formatOut.format(calendar.getTime());
                    holder.txt_date.setText(newDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if(itemList.get(position).getPayment_time()!=null){

                try {
                    SimpleDateFormat formatIn = new SimpleDateFormat("hh:mm:ss");
                    SimpleDateFormat formatOut = new SimpleDateFormat("hh:mm a");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(formatIn.parse(itemList.get(position).getPayment_time()));
                    String newDate = formatOut.format(calendar.getTime());
                    holder.txt_time.setText(newDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

           // holder.txt_time.setText(itemList.get(position).getPayment_time());

            holder.linName.setVisibility(View.GONE);
            holder.txt_name.setText(itemList.get(position).getCustomer_name());

            if (!itemList.get(position).getRemarks().equals("")) {
                holder.txt_remarks.setVisibility(View.VISIBLE);
                holder.txt_remarks.setText(itemList.get(position).getRemarks());
            } else {
                holder.txt_remarks.setText("-");
            }

   /*         if (!itemList.get(position).getNext_payment_date().equals("")){
                Date current = new Date();
                //create a date one day before current date
                long prevDay = Long.parseLong(itemList.get(position).getNext_payment_date());
                //create date object
                Date prev = new Date(prevDay);
                //compare both dates
                if(prev.before(current)){
                    System.out.println("The date is older than current day");
                } else {
                    System.out.println("The date is future day");
                }

            }
*/
            if (!itemList.get(position).getNext_payment_date().equals("")) {
                try {
                    if (new SimpleDateFormat("dd-MM-yyyy").parse(itemList.get(position).getNext_payment_date()).before(new Date())) {
                        //Toast.makeText(getContext(), itemList.get(position).getNext_payment_date(), Toast.LENGTH_SHORT).show();
                        //holder.view_main.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                        holder.txt_next_date.setTextColor(getResources().getColor(R.color.white));
                        holder.txt_title_payment.setTextColor(getResources().getColor(R.color.white));
                        //holder.error_image.setVisibility(View.VISIBLE);
                        holder.layout_error.setBackground(getActivity().getResources().getDrawable(R.drawable.single_side_round_corner_red));
                    } else {
                        holder.txt_next_date.setTextColor(getResources().getColor(R.color.n));
                        holder.txt_title_payment.setTextColor(getResources().getColor(R.color.blackText2));
                        holder.layout_error.setBackground(null);
                    }
                } catch (
                        ParseException e) {
                    e.printStackTrace();
                }

            }






         /*   holder.view_main.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return true;
                }
            });*/

            Utils.showLog("==dataattach" + itemList.get(position).getPayment_attachment());

            if (showFile) {
                holder.ivFile.setVisibility(View.GONE);
                if (!itemList.get(position).getPayment_attachment().isEmpty()) {
                    holder.txt_view_attechment.setVisibility(View.VISIBLE);
                    holder.layout_demo.setVisibility(View.VISIBLE);
                    if (itemList.get(position).getFileType().endsWith("image/png") || itemList.get(position).getFileType().endsWith("image/png") ||
                            itemList.get(position).getFileType().endsWith("image/jpg") || itemList.get(position).getFileType().endsWith("image/jpeg")) {
                        Picasso.with(context)
                                .load(itemList.get(position).getPayment_attachment())
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .resize(300, 300)
                                .into(holder.ivFile);
                    } else if (itemList.get(position).getFileType().endsWith("application/pdf")) {
                        holder.ivFile.setImageResource(R.drawable.ic_pdf);
                    }
                } else {
                    holder.ivFile.setImageResource(R.drawable.ic_img_not_available);
                    holder.txt_view_attechment.setVisibility(View.GONE);
                    holder.layout_demo.setVisibility(View.GONE);
                }
            }

            holder.ivFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemList.get(position).getFileType().endsWith("image/png") || itemList.get(position).getFileType().endsWith("image/png") ||
                            itemList.get(position).getFileType().endsWith("image/jpg") || itemList.get(position).getFileType().endsWith("image/jpeg")) {
                        context.startActivity(new Intent(context, Activity_ImageViewer.class)
                                .putExtra("img_url", itemList.get(position).getPayment_attachment()));
                    } else if (itemList.get(position).getFileType().endsWith("application/pdf")) {
                        context.startActivity(new Intent(context, PdfViewActivity.class)
                                .putExtra("pdf_url", itemList.get(position).getPayment_attachment())
                                .putExtra("only_load", true));
                    }
                }
            });

            holder.layout_demo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemList.get(position).getFileType().endsWith("image/png") || itemList.get(position).getFileType().endsWith("image/png") ||
                            itemList.get(position).getFileType().endsWith("image/jpg") || itemList.get(position).getFileType().endsWith("image/jpeg")) {
                        context.startActivity(new Intent(context, Activity_ImageViewer.class)
                                .putExtra("img_url", itemList.get(position).getPayment_attachment()));
                    } else if (itemList.get(position).getFileType().endsWith("application/pdf")) {
                        context.startActivity(new Intent(context, PdfViewActivity.class)
                                .putExtra("pdf_url", itemList.get(position).getPayment_attachment())
                                .putExtra("only_load", true));
                    }
                }
            });
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDeleteDialog(position);
                }
            });

            holder.ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openUpdateDialog(position);
                }
            });


            holder.view_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openUpdateDialog(position);
                }
            });

            /*holder.view_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                *//*if (mItemClickListener != null)
                    mItemClickListener.onItemClick(position);*//*

                }
            });*/
        }

        private void openUpdateDialog(final int position) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_update_payment, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(view);
            final AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            final EditText edt_paid_amount = (EditText) view.findViewById(R.id.edt_paid_amount);
            edt_paid_amount.setText(itemList.get(position).getAmount());
            final EditText edt_remark = (EditText) view.findViewById(R.id.edt_remark);
            edt_remark.setText(itemList.get(position).getRemarks());
            Button btn_update_payment = (Button) view.findViewById(R.id.btn_update_payment);
            Button btn_delete_payment = (Button) view.findViewById(R.id.btn_delete_payment);
            ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
            img_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            btn_delete_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //openDeleteDialog(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Delete this Payment ?");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog11, int id) {
                                    deleteEntryAPI(position);
                                    dialog11.cancel();
                                    dialog.dismiss();

                                }
                            });

                    builder.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });


            ImageView btn_cancel = (ImageView) view.findViewById(R.id.btn_cancel);
            final LinearLayout viewUploadFile = (LinearLayout) view.findViewById(R.id.viewUploadFile);
            viewUploadFile.setVisibility(View.VISIBLE);
            viewUploadFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    b_tvUploadFile_update = true;
                    showAttachImageOption();
                }
            });
            ivUploadFile = (ImageView) view.findViewById(R.id.ivUploadFile);
            ivUploadFile.setVisibility(View.VISIBLE);
            ivUploadFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    b_tvUploadFile_update = true;
                    showAttachImageOption();
                }
            });
            img_delete_update = (ImageView) view.findViewById(R.id.img_delete_update);
            img_delete_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageEncode = "";
                    capturedImageURI=Uri.parse("");
                    mime="";

                    int newHeight = 50; // New height in pixels
                    int newWidth = 50; //

                    ivUploadFile.getLayoutParams().height = newHeight;

                    // Apply the new width for ImageView programmatically
                    ivUploadFile.getLayoutParams().width = newWidth;


                    ivUploadFile.requestLayout();

                    tvUploadFile_update.setText("Upload File");
                    tvUploadFile_update.setVisibility(View.VISIBLE);
                    ivUploadFile.setImageResource(R.drawable.ic_add_image);
                    img_delete_update.setVisibility(View.GONE);
                }
            });
            tvUploadFile_update = (TextView) view.findViewById(R.id.tvUploadFile);
            tvUploadFile_update.setVisibility(View.VISIBLE);
            tvUploadFile_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    b_tvUploadFile_update = true;

                    showAttachImageOption();


                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_update_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (edt_paid_amount.getText().toString().equals("")) {
                        edt_paid_amount.setError("required");
                    } else if (edt_remark.getText().toString().equals("")) {
                        edt_remark.setError("required");
                    } else {
                        updatePaymentAPI(position, edt_paid_amount.getText().toString(), edt_remark.getText().toString());
                        dialog.dismiss();
                    }

                }
            });
            dialog.show();
        }

        private void updatePaymentAPI(final int position, final String amount, final String remarks) {
            if(String.valueOf(capturedImageURI).equals("")){
                showPrd();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                Query query = databaseReference.child("Payments").orderByKey();
                databaseReference.keepSynced(true);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot npsnapshot) {
                        hidePrd();
                        try {
                            if (npsnapshot.exists()) {
                                for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                                    if (dataSnapshot.child("id").getValue().toString().equals(itemList.get(position).getId())) {

                                        DatabaseReference cineIndustryRef = databaseReference.child("Payments").child(dataSnapshot.getKey());
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("amount", amount);
                                        map.put("remarks", remarks);
                                        Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                hidePrd();
                                                new CToast(context).simpleToast("Payment update successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                notifyDataSetChanged();
                                                imageEncode = "";
                                                capturedImageURI = Uri.parse("");
                                                attachmentType = "";
                                                mime="";
                                                // getRentDetail();
                                                getPaymentRentCustomers();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                hidePrd();
                                                new CToast(mActivity).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                            }
                                        });
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("exception   ",e.toString());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hidePrd();
                        Log.e("databaseError   ",databaseError.getMessage());
                    }
                });
            }
            else {
                showPrd();
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                final StorageReference sRef = storageReference.child("/files" + "/" + UUID.randomUUID().toString());
                sRef.putFile(capturedImageURI)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @SuppressLint("LongLogTag")
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //if (uriList.size() == multiplefirebasePath.size()) {
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                        Query query = databaseReference.child("Payments").orderByKey();
                                        databaseReference.keepSynced(true);
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot npsnapshot) {
                                                hidePrd();
                                                try {
                                                    if (npsnapshot.exists()) {
                                                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                                                            if (dataSnapshot.child("id").getValue().toString().equals(itemList.get(position).getId())) {

                                                                DatabaseReference cineIndustryRef = databaseReference.child("Payments").child(dataSnapshot.getKey());
                                                                Map<String, Object> map = new HashMap<>();
                                                                map.put("amount", amount);
                                                                map.put("remarks", remarks);
                                                                map.put("payment_attachment", uri.toString());
                                                                map.put("file_type", mime);

                                                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        hidePrd();
                                                                        new CToast(context).simpleToast("Payment update successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                                        notifyDataSetChanged();
                                                                        imageEncode = "";
                                                                        capturedImageURI = Uri.parse("");
                                                                        attachmentType = "";
                                                                        mime="";
                                                                        // getRentDetail();
                                                                        getPaymentRentCustomers();

                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        hidePrd();
                                                                        new CToast(mActivity).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    hidePrd();
                                                    Log.e("exception   ",e.toString());
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                hidePrd();
                                                Log.e("databaseError   ",databaseError.getMessage());
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                hidePrd();
                                Toast.makeText(getContext(), "Please try later...", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                Log.e("progress  ", String.valueOf(progress));
                            }
                        });
            }
        }

        private void updatePaymentAPI1(final int position, final String amount, final String remarks) {
            showPrd();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, API.UPDATE_PAYMENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    hidePrd();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("result") == 1) {
                            itemList.get(position).setAmount(amount);
                            itemList.get(position).setRemarks(remarks);
                            notifyDataSetChanged();
                            imageEncode = "";
                            attachmentType = "";
                            // getRentDetail();
                            getPaymentRentCustomers();
                            new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                        } else {
                            new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        hidePrd();
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
                    params.put("id", itemList.get(position).getId());
                    params.put("amount", amount);
                    params.put("remarks", remarks);
                    params.put("payment_attachment", imageEncode);
                    params.put("attachment_type", attachmentType);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(stringRequest);
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView txt_amount, txt_date, txt_time, txt_name, txt_remarks, txt_view_attechment, txt_next_date;
            View view_main;
            ImageView ivDelete, ivEdit, ivFile;
            LinearLayout layout_demo;
            TextView txt_title_payment;
            LinearLayout layout_error,linName;

            public ViewHolder(View itemView) {
                super(itemView);
                txt_title_payment = (TextView) itemView.findViewById(R.id.txt_title_payment);
                layout_error = (LinearLayout) itemView.findViewById(R.id.layout_error);
                txt_amount = (TextView) itemView.findViewById(R.id.txt_amount);
                txt_date = (TextView) itemView.findViewById(R.id.txt_date);
                txt_time = (TextView) itemView.findViewById(R.id.txt_time);
                txt_name = (TextView) itemView.findViewById(R.id.txt_name);
                linName = itemView.findViewById(R.id.linName);
                txt_remarks = (TextView) itemView.findViewById(R.id.txt_remarks);
                view_main = itemView.findViewById(R.id.view_main);
                ivDelete = itemView.findViewById(R.id.ivDelete);
                ivEdit = itemView.findViewById(R.id.ivEdit);
                ivFile = itemView.findViewById(R.id.ivFile);
                txt_view_attechment = (TextView) itemView.findViewById(R.id.txt_view_attechment);
                txt_next_date = (TextView) itemView.findViewById(R.id.txt_next_date);
                layout_demo = (LinearLayout) itemView.findViewById(R.id.layout_demo);
            }
        }

        private void openDeleteDialog(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Delete this Payment ?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteEntryAPI(position);
                            dialog.cancel();
                        }
                    });

            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        private void deleteEntryAPI(final int position) {
            showPrd();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Payments").child(itemList.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        databaseReference.child("Payments").child(itemList.get(position).getId()).removeValue().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hidePrd();
                                if (task.isSuccessful()) {
                                   // itemList.remove(position);
                                    notifyDataSetChanged();
                                    new CToast(context).simpleToast("Payment delete successfully",Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                } else {
                                    new CToast(context).simpleToast(task.getException().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                }

                            }
                        }).addOnFailureListener(getActivity(), new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hidePrd();
                                new CToast(context).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }
                        });
                    } else {
                        hidePrd();
                        new CToast(context).simpleToast("Customer not exist", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    hidePrd();
                    new CToast(context).simpleToast(error.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }
            });

        }

        private void deleteEntryAPI1(final int position) {
            showPrd();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, API.DELETE_PAYMENT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    hidePrd();
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getInt("result") == 1) {
                            itemList.remove(position);
                            notifyDataSetChanged();
                            new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                        } else {
                            new CToast(context).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        hidePrd();
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

                    Utils.showLog("===" + itemList.get(position).getId());
                    Map<String, String> params = new HashMap<>();
                    params.put("id", itemList.get(position).getId());
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(stringRequest);
        }

        public void showPrd() {
            prd = new ProgressDialog(context);
            prd.setMessage("Please wait...");
            prd.setCancelable(false);
            prd.show();
        }

        public void hidePrd() {
            prd.dismiss();
        }
    }

    public class ImageCompression extends AsyncTask<String, Void, String> {

        private Context context;

        public ImageCompression(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length == 0 || strings[0] == null)
                return null;
            return imageUtils.compressImage(context, strings[0]);
        }

        protected void onPostExecute(String imagePath) {
            Uri filePath = Uri.fromFile(new File(imagePath));
            uriToBitmap(filePath);
        }

    }

    private void uriToBitmap(Uri filePath) {
        try {
            Bitmap bitmap = Helper.decodeUri(filePath, mActivity);
            if (bitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                imageEncode = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                attachmentType = "png";
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            new CToast(mActivity).simpleToast("Fail to attach image try again", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
        }
    }
}

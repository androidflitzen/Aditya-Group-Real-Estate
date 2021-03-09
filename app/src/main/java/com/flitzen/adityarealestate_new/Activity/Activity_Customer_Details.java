package com.flitzen.adityarealestate_new.Activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Customer_Loan_List;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Customer_Property_List;
import com.flitzen.adityarealestate_new.Adapter.Adapter_EMI_List;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Payment_List;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Property_Payment_List;
import com.flitzen.adityarealestate_new.Adapter.Adapter_Purchased_Plot_List;
import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Permission;
import com.flitzen.adityarealestate_new.Classes.SharePref;
import com.flitzen.adityarealestate_new.Fragment.ActionBottomDialogFragment;
import com.flitzen.adityarealestate_new.Items.Item_Loan_Details;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Detail;
import com.flitzen.adityarealestate_new.Items.Item_Property_Detail;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Activity_Customer_Details extends AppCompatActivity implements ActionBottomDialogFragment.ItemClickListener{

    Activity mActivity;
    ProgressDialog prd;
    int check = 0;

    int UPLOAD_REQUEST = 001, SELECT_PICTURE = 002, CAMERA_REQUEST = 003, SELECT_AUDIO = 004, CAMERA_PERMISSION_CODE = 005;
    int ATTACH_ITEM_POS = 0;
    private Uri photoURI;
    String imageEncode = "";
    String base64Image = "";
    Bitmap bitmapImage;
    String type = "";
    String city = "", pictureFilePath;
    MenuItem editItem, shareItem, deleteItem;
    View btn_deactivate_cusomer;
    TextView txt_cust_name, txt_cust_email, txt_cust_no, txt_cust_no_1, txt_cust_address;
    ImageView img_aadhar_card, img_aadhar_card_back, img_pan_card, img_driving_licence, img_ration_card, img_other;

    ArrayList<Item_Plot_Detail> arrayListPlotDetail = new ArrayList<>();
    RecyclerView rec_purchased_plot_list;
    Adapter_Purchased_Plot_List mAdapterPlot;

    ArrayList<Item_Property_Detail> arrayListPropertyDetail = new ArrayList<>();
    RecyclerView rec_rented_property_list;
    Adapter_Customer_Property_List mAdapterProperty;

    ArrayList<Item_Loan_Details> arrayListLoanDetails = new ArrayList<>();
    RecyclerView rec_loan_list;
    Adapter_Customer_Loan_List mAdapterLoan;

    LinearLayout ll_plot, ll_rented, ll_loan, ll_cust_another_no, ll_cust_email, ll_cust_address;

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private int mShortAnimationDurationEffect;

    String img_path_adhar = "", img_path_adhar_back = "", img_path_pan = "", img_path_driving = "", img_path_voteris = "", img_path_other = "";

    boolean isImageOpen = false;

    ImageView expandedImageView;
    LinearLayout ll_content;
    String imageName = "";
    String imgPath = "";
    private String[] permissions = {Manifest.permission.CAMERA};
    String customer_id = "", customer_name = "", customer_main_id = "";
    private Uri selectedImageUri=Uri.parse("");
    private int totlePaidAmount = 0;
    private Uri capturedImageURI = Uri.parse("");
    String mime="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        customer_id = getIntent().getStringExtra("id");
        customer_name = getIntent().getStringExtra("name");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Customers").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(customer_id)) {
                                customer_main_id = dataSnapshot.getKey();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("exception   ", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
                Log.e("databaseError   ", databaseError.getMessage());
            }
        });

        Log.e("Detail Screen  ", customer_id);

        getSupportActionBar().setTitle(Html.fromHtml(customer_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Customer_Details.this;

        txt_cust_name = (TextView) findViewById(R.id.txt_cust_name);

        txt_cust_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayListPlotDetail.size() > 0) {
                    ll_plot.setVisibility(View.VISIBLE);
                } else {
                    ll_plot.setVisibility(View.GONE);
                }

                for (int i = 0; i < arrayListPlotDetail.size(); i++) {
                    System.out.println("site name  " + arrayListPlotDetail.get(i).getSite_name());
                    System.out.println("site address  " + arrayListPlotDetail.get(i).getSite_address());
                    System.out.println("site paid Amount  " + arrayListPlotDetail.get(i).getPaid_amount());
                    System.out.println("site pending Amount  " + arrayListPlotDetail.get(i).getPending_amount());
                }

                mAdapterPlot.notifyDataSetChanged();
            }
        });

        txt_cust_email = (TextView) findViewById(R.id.txt_cust_email);
        txt_cust_no = (TextView) findViewById(R.id.txt_cust_no);
        txt_cust_no_1 = (TextView) findViewById(R.id.txt_cust_no_1);
        txt_cust_address = (TextView) findViewById(R.id.txt_cust_address);

        img_aadhar_card = (ImageView) findViewById(R.id.img_aadhar_card);
        img_aadhar_card_back = (ImageView) findViewById(R.id.img_aadhar_card_back);
        img_pan_card = (ImageView) findViewById(R.id.img_pan_card);
        img_driving_licence = (ImageView) findViewById(R.id.img_driving_licence);
        img_ration_card = (ImageView) findViewById(R.id.img_ration_card);
        img_other = (ImageView) findViewById(R.id.img_other);
        btn_deactivate_cusomer = findViewById(R.id.btn_deactivate_cusomer);
        txt_cust_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + txt_cust_no.getText().toString()));
                startActivity(intent);
            }
        });

        txt_cust_no_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + txt_cust_no_1.getText().toString()));
                startActivity(intent);
            }
        });

        img_aadhar_card.setTag("0");
        img_aadhar_card_back.setTag("0");
        img_pan_card.setTag("0");
        img_driving_licence.setTag("0");
        img_ration_card.setTag("0");
        img_other.setTag("0");

        img_aadhar_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type = "kyc_aadhar";
                if (img_aadhar_card.getTag().toString().equals("0")) {
                    showAttachImageOption();
                } else {
                    //showImageOption(view, img_path_adhar);
                    isImageOpen = true;
                    zoomImageFromThumb(view, img_path_adhar);
                    imgPath = img_path_adhar;
                }
            }
        });

        img_aadhar_card_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = "kyc_aadhar_back";
                if (img_aadhar_card_back.getTag().toString().equals("0")) {
                    showAttachImageOption();
                } else {
                    //showImageOption(view, img_path_adhar);
                    isImageOpen = true;
                    zoomImageFromThumb(v, img_path_adhar_back);
                    imgPath = img_path_adhar_back;
                }
            }
        });

        img_pan_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type = "kyc_pancard";
                if (img_pan_card.getTag().toString().equals("0")) {
                    showAttachImageOption();
                } else {
                    //showImageOption(view, img_path_pan);
                    isImageOpen = true;
                    zoomImageFromThumb(view, img_path_pan);
                    imgPath = img_path_pan;
                }
            }
        });

        img_driving_licence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type = "kyc_driving_lic";
                if (img_driving_licence.getTag().toString().equals("0")) {
                    showAttachImageOption();
                } else {
                    //showImageOption(view, img_path_driving);
                    isImageOpen = true;
                    zoomImageFromThumb(view, img_path_driving);
                    imgPath = img_path_driving;
                }
            }
        });

        img_ration_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type = "kyc_ration";
                if (img_ration_card.getTag().toString().equals("0")) {
                    showAttachImageOption();
                } else {
                    //showImageOption(view, img_path_voteris);
                    isImageOpen = true;
                    zoomImageFromThumb(view, img_path_voteris);
                    imgPath = img_path_voteris;
                }
            }
        });

        img_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type = "kyc_other";
                if (img_other.getTag().toString().equals("0")) {
                    showAttachImageOption();
                } else {
                    //showImageOption(view, img_path_other);
                    isImageOpen = true;
                    zoomImageFromThumb(view, img_path_other);
                    imgPath = img_path_other;
                }
            }
        });

        mShortAnimationDurationEffect = getResources().getInteger(android.R.integer.config_shortAnimTime);

        ll_plot = (LinearLayout) findViewById(R.id.ll_plot);
        ll_rented = (LinearLayout) findViewById(R.id.ll_rented);
        ll_loan = (LinearLayout) findViewById(R.id.ll_loan);
        ll_cust_another_no = (LinearLayout) findViewById(R.id.ll_cust_another_no);
        ll_cust_email = (LinearLayout) findViewById(R.id.ll_cust_email);
        ll_cust_address = (LinearLayout) findViewById(R.id.ll_cust_address);

        rec_purchased_plot_list = (RecyclerView) findViewById(R.id.rec_purchased_plot_list);
        rec_purchased_plot_list.setLayoutManager(new LinearLayoutManager(mActivity));
        rec_purchased_plot_list.setHasFixedSize(true);
        rec_purchased_plot_list.setNestedScrollingEnabled(false);
        mAdapterPlot = new Adapter_Purchased_Plot_List(mActivity, arrayListPlotDetail);
        rec_purchased_plot_list.setAdapter(mAdapterPlot);

        rec_rented_property_list = (RecyclerView) findViewById(R.id.rec_rented_property_list);
        rec_rented_property_list.setLayoutManager(new LinearLayoutManager(mActivity));
        rec_rented_property_list.setHasFixedSize(true);
        rec_rented_property_list.setNestedScrollingEnabled(false);
        mAdapterProperty = new Adapter_Customer_Property_List(mActivity, arrayListPropertyDetail);
        rec_rented_property_list.setAdapter(mAdapterProperty);

        rec_loan_list = (RecyclerView) findViewById(R.id.rec_loan_list);
        rec_loan_list.setLayoutManager(new LinearLayoutManager(mActivity));
        rec_loan_list.setHasFixedSize(true);
        rec_loan_list.setNestedScrollingEnabled(false);
        mAdapterLoan = new Adapter_Customer_Loan_List(mActivity, arrayListLoanDetails);
        rec_loan_list.setAdapter(mAdapterLoan);

        mAdapterPlot.setOnItemClickListener(new Adapter_Purchased_Plot_List.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {

                final LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
                final View promptView = layoutInflater.inflate(R.layout.dialog_payment_list, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setView(promptView);
                final AlertDialog alertDialog = builder.create();

                TextView txt_plot_no = (TextView) promptView.findViewById(R.id.txt_plot_no);
                View ll_title = promptView.findViewById(R.id.ll_title);
                View view_add_payment = promptView.findViewById(R.id.view_add_payment);
                RecyclerView rec_payment_list = (RecyclerView) promptView.findViewById(R.id.rec_payment_list);

                if (!arrayListPlotDetail.get(position).getPending_amount().equals("")) {
                    if (Integer.parseInt(arrayListPlotDetail.get(position).getPending_amount()) > 0) {
                        view_add_payment.setVisibility(View.VISIBLE);
                    } else {
                        view_add_payment.setVisibility(View.GONE);
                    }
                }


                txt_plot_no.setText("Plot No : " + arrayListPlotDetail.get(position).getPlot_no());

                rec_payment_list.setLayoutManager(new LinearLayoutManager(mActivity));
                rec_payment_list.setHasFixedSize(true);
                Adapter_Payment_List adapter_payment_list = new Adapter_Payment_List(mActivity, arrayListPlotDetail.get(position).getArrayListPayment());
                rec_payment_list.setAdapter(adapter_payment_list);

                adapter_payment_list.OnItemLongClickListener(new Adapter_Payment_List.OnItemLongClickListener() {
                    @Override
                    public void onItemClick(final int pos) {
                        alertDialog.dismiss();

                        new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                                .setTitle(Html.fromHtml("<b> Delete </b>"))
                                .setMessage("Are you sure you want to delete this payment?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        remove_Payment(arrayListPlotDetail.get(position).getArrayListPayment().get(pos).getId(), "Plot");
                                    }
                                }).setNegativeButton("No", null).show();

                    }
                });

                /*if (arrayListPlotDetail.get(position).getArrayListPayment().size() > 0) {
                    ll_title.setVisibility(View.VISIBLE);
                } else {
                    ll_title.setVisibility(View.GONE);
                }*/

                view_add_payment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();

                        LayoutInflater inflater = LayoutInflater.from(mActivity);
                        View view1 = inflater.inflate(R.layout.dialog_payment_add, null);

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
                        builder1.setView(view1);
                        final AlertDialog dialog = builder1.create();

                        final EditText edt_paid_amount = (EditText) view1.findViewById(R.id.edt_paid_amount);
                        final EditText edt_remark = (EditText) view1.findViewById(R.id.edt_remark);
                        final TextView txt_date = (TextView) view1.findViewById(R.id.txt_date);
                        final TextView txt_time = (TextView) view1.findViewById(R.id.txt_time);
                        final TextView txt_next_date = (TextView) view1.findViewById(R.id.txt_next_date);

                        TextView btn_cancel = (TextView) view1.findViewById(R.id.btn_cancel);
                        Button btn_add_payment = (Button) view1.findViewById(R.id.btn_add_payment);
                        Button btn_add_share_payment = (Button) view1.findViewById(R.id.btn_add_share_payment);

                        edt_paid_amount.setText(arrayListPlotDetail.get(position).getPending_amount());

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

                        ImageView img_close = (ImageView) view1.findViewById(R.id.img_close);
                        img_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        txt_time.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

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
                                    edt_paid_amount.setError("Enter pending amount");
                                    edt_paid_amount.requestFocus();
                                    return;
                                } else if (txt_date.getText().toString().equals("")) {
                                    new CToast(mActivity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    return;
                                } else if (txt_time.getText().toString().equals("")) {
                                    new CToast(mActivity).simpleToast("Select Time", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    return;
                                } else if (Integer.parseInt(edt_paid_amount.getText().toString()) > Integer.parseInt(arrayListPlotDetail.get(position).getPending_amount())) {
                                    edt_paid_amount.setError("You enter more then pending amount");
                                    edt_paid_amount.requestFocus();
                                    return;
                                }
                                else if (txt_next_date.getText().toString().equals("")) {
                                    new CToast(mActivity).simpleToast("Select Next Payment Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    return;
                                }else {
                                    dialog.dismiss();
                                    addPaymentforPlot(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString()
                                            , arrayListPlotDetail.get(position).getId(), edt_remark.getText().toString(), txt_next_date.getText().toString(),0);
                                }
                            }
                        });

                        btn_add_share_payment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (edt_paid_amount.getText().toString().equals("")) {
                                    edt_paid_amount.setError("Enter pending amount");
                                    edt_paid_amount.requestFocus();
                                    return;
                                } else if (txt_date.getText().toString().equals("")) {
                                    new CToast(mActivity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    return;
                                } else if (txt_time.getText().toString().equals("")) {
                                    new CToast(mActivity).simpleToast("Select Time", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    return;
                                } else if (Integer.parseInt(edt_paid_amount.getText().toString()) > Integer.parseInt(arrayListPlotDetail.get(position).getPending_amount())) {
                                    edt_paid_amount.setError("You enter more then pending amount");
                                    edt_paid_amount.requestFocus();
                                    return;
                                }
                                else if (txt_next_date.getText().toString().equals("")) {
                                    new CToast(mActivity).simpleToast("Select Next Payment Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    return;
                                }else {
                                    dialog.dismiss();
                                    addPaymentforPlot(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString()
                                            , arrayListPlotDetail.get(position).getId(), edt_remark.getText().toString(), txt_next_date.getText().toString(),1);
                                }
                            }
                        });

                        dialog.show();
                    }
                });

                alertDialog.show();

            }
        });

        mAdapterProperty.setOnItemClickListener(new Adapter_Customer_Property_List.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {

                final LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
                final View promptView = layoutInflater.inflate(R.layout.dialog_payment_list, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setView(promptView);
                final AlertDialog alertDialog = builder.create();

                TextView txt_plot_no = (TextView) promptView.findViewById(R.id.txt_plot_no);
                View view_add_payment = promptView.findViewById(R.id.view_add_payment);
                RecyclerView rec_payment_list = (RecyclerView) promptView.findViewById(R.id.rec_payment_list);

                txt_plot_no.setText(arrayListPropertyDetail.get(position).getProperty_name());

                rec_payment_list.setLayoutManager(new LinearLayoutManager(mActivity));
                rec_payment_list.setHasFixedSize(true);
                Adapter_Property_Payment_List adapter_payment_list = new Adapter_Property_Payment_List(mActivity, arrayListPropertyDetail.get(position).getArrayListPayment());
                rec_payment_list.setAdapter(adapter_payment_list);


                adapter_payment_list.OnItemLongClickListener(new Adapter_Property_Payment_List.OnItemLongClickListener() {
                    @Override
                    public void onItemClick(final int pos) {
                        alertDialog.dismiss();

                        new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                                .setTitle(Html.fromHtml("<b> Delete </b>"))
                                .setMessage("Are you sure you want to delete this payment?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        remove_Payment(arrayListPropertyDetail.get(position).getArrayListPayment().get(pos).getId(), "Property");

                                    }
                                }).setNegativeButton("No", null).show();


                    }
                });

                view_add_payment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDialog.dismiss();

                        LayoutInflater inflater = LayoutInflater.from(mActivity);
                        View view1 = inflater.inflate(R.layout.dialog_payment_add, null);

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
                        builder1.setView(view1);
                        final AlertDialog dialog = builder1.create();

                        final EditText edt_paid_amount = (EditText) view1.findViewById(R.id.edt_paid_amount);
                        final EditText edt_remark = (EditText) view1.findViewById(R.id.edt_remark);
                        final TextView txt_date = (TextView) view1.findViewById(R.id.txt_date);
                        final TextView txt_time = (TextView) view1.findViewById(R.id.txt_time);
                        final TextView txt_next_date = (TextView) view1.findViewById(R.id.txt_next_date);

                        TextView btn_cancel = (TextView) view1.findViewById(R.id.btn_cancel);
                        Button btn_add_payment = (Button) view1.findViewById(R.id.btn_add_payment);
                        Button btn_add_share_payment = (Button) view1.findViewById(R.id.btn_add_share_payment);

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

                        ImageView img_close = (ImageView) view1.findViewById(R.id.img_close);
                        img_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        txt_time.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

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
                                }
                                else if (txt_next_date.getText().toString().equals("")) {
                                    new CToast(mActivity).simpleToast("Select Next Payment Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    return;
                                }else {
                                    dialog.dismiss();
                                    addPaymentForRent(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString()
                                            , arrayListPropertyDetail.get(position).getId(), edt_remark.getText().toString(),txt_next_date.getText().toString(),0);
                                }
                            }
                        });

                        btn_add_share_payment.setOnClickListener(new View.OnClickListener() {
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
                                }
                                else if (txt_next_date.getText().toString().equals("")) {
                                    new CToast(mActivity).simpleToast("Select Next Payment Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    return;
                                }else {
                                    dialog.dismiss();
                                    addPaymentForRent(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString()
                                            , arrayListPropertyDetail.get(position).getId(), edt_remark.getText().toString(),txt_next_date.getText().toString(),1);
                                }
                            }
                        });

                        dialog.show();
                    }
                });

                alertDialog.show();

            }
        });

        mAdapterLoan.setOnItemClickListener(new Adapter_Customer_Loan_List.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {

                if (arrayListLoanDetails.get(position).getArrayListEMI().size() > 0) {

                    final LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
                    final View promptView = layoutInflater.inflate(R.layout.dialog_payment_list, null);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setView(promptView);
                    final AlertDialog alertDialog = builder.create();

                    TextView txt_plot_no = (TextView) promptView.findViewById(R.id.txt_plot_no);
                    View ll_title = promptView.findViewById(R.id.ll_title);
                    View view_add_payment = promptView.findViewById(R.id.view_add_payment);
                    RecyclerView rec_payment_list = (RecyclerView) promptView.findViewById(R.id.rec_payment_list);

                /*if (!arrayListLoanDetails.get(position).getApproved_Amount().equals("")) {
                    if (Integer.parseInt(arrayListLoanDetails.get(position).getApproved_Amount()) > 0) {
                        view_add_payment.setVisibility(View.VISIBLE);
                    } else {
                        view_add_payment.setVisibility(View.GONE);
                    }
                }*/
                    view_add_payment.setVisibility(View.GONE);


                    txt_plot_no.setText("Application Number : " + arrayListLoanDetails.get(position).getApplicantion_Number());

                    rec_payment_list.setLayoutManager(new LinearLayoutManager(mActivity));
                    rec_payment_list.setHasFixedSize(true);
                    Adapter_EMI_List adapter_payment_list = new Adapter_EMI_List(mActivity, arrayListLoanDetails.get(position).getArrayListEMI());
                    rec_payment_list.setAdapter(adapter_payment_list);

                    adapter_payment_list.OnItemLongClickListener(new Adapter_EMI_List.OnItemLongClickListener() {
                        @Override
                        public void onItemClick(final int pos) {
                            alertDialog.dismiss();

                            new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                                    .setTitle(Html.fromHtml("<b> Delete </b>"))
                                    .setMessage("Are you sure you want to delete this EMI?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            remove_EMI(arrayListLoanDetails.get(position).getArrayListEMI().get(pos).getId());
                                        }
                                    }).setNegativeButton("No", null).show();

                        }
                    });

                    if (arrayListLoanDetails.get(position).getArrayListEMI().size() > 0) {
                        ll_title.setVisibility(View.VISIBLE);
                    } else {
                        ll_title.setVisibility(View.GONE);
                    }

                    view_add_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            alertDialog.dismiss();

                            LayoutInflater inflater = LayoutInflater.from(mActivity);
                            View view1 = inflater.inflate(R.layout.dialog_payment_add, null);

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
                            builder1.setView(view1);
                            final AlertDialog dialog = builder1.create();

                            final EditText edt_paid_amount = (EditText) view1.findViewById(R.id.edt_paid_amount);
                            final EditText edt_remark = (EditText) view1.findViewById(R.id.edt_remark);
                            final TextView txt_date = (TextView) view1.findViewById(R.id.txt_date);
                            final TextView txt_time = (TextView) view1.findViewById(R.id.txt_time);
                            final TextView txt_next_date = (TextView) view1.findViewById(R.id.txt_next_date);

                            TextView btn_cancel = (TextView) view1.findViewById(R.id.btn_cancel);
                            Button btn_add_payment = (Button) view1.findViewById(R.id.btn_add_payment);
                            Button btn_add_share_payment =  view1.findViewById(R.id.btn_add_share_payment);

                            edt_paid_amount.setText(arrayListLoanDetails.get(position).getApproved_Amount());

                            txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
                            txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

                            ImageView img_close = (ImageView) view1.findViewById(R.id.img_close);
                            img_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

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

                            txt_time.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

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
                                }
                            });

                            /*String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                            txt_time.setText(currentTime);*/

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
                                        edt_paid_amount.setError("Enter pending amount");
                                        edt_paid_amount.requestFocus();
                                        return;
                                    } else if (txt_date.getText().toString().equals("")) {
                                        new CToast(mActivity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                        return;
                                    } else if (txt_time.getText().toString().equals("")) {
                                        new CToast(mActivity).simpleToast("Select Time", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                        return;
                                    } else if (Integer.parseInt(edt_paid_amount.getText().toString()) > Integer.parseInt(arrayListLoanDetails.get(position).getApproved_Amount())) {
                                        edt_paid_amount.setError("You enter more then pending amount");
                                        edt_paid_amount.requestFocus();
                                        return;
                                    }
                                    else if (txt_next_date.getText().toString().equals("")) {
                                        new CToast(mActivity).simpleToast("Select Next Payment Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                        return;
                                    }else {
                                        dialog.dismiss();
                                        addPaymentforPlot(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString()
                                                , arrayListLoanDetails.get(position).getId(), edt_remark.getText().toString(), txt_next_date.getText().toString(),0);
                                    }
                                }
                            });

                            btn_add_share_payment.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (edt_paid_amount.getText().toString().equals("")) {
                                        edt_paid_amount.setError("Enter pending amount");
                                        edt_paid_amount.requestFocus();
                                        return;
                                    } else if (txt_date.getText().toString().equals("")) {
                                        new CToast(mActivity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                        return;
                                    } else if (txt_time.getText().toString().equals("")) {
                                        new CToast(mActivity).simpleToast("Select Time", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                        return;
                                    } else if (Integer.parseInt(edt_paid_amount.getText().toString()) > Integer.parseInt(arrayListLoanDetails.get(position).getApproved_Amount())) {
                                        edt_paid_amount.setError("You enter more then pending amount");
                                        edt_paid_amount.requestFocus();
                                        return;
                                    }
                                    else if (txt_next_date.getText().toString().equals("")) {
                                        new CToast(mActivity).simpleToast("Select Next Payment Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                        return;
                                    }else {
                                        dialog.dismiss();
                                        addPaymentforPlot(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString()
                                                , arrayListLoanDetails.get(position).getId(), edt_remark.getText().toString(), txt_next_date.getText().toString(),1);
                                    }
                                }
                            });

                            dialog.show();
                        }
                    });

                    alertDialog.show();
                }

            }
        });

        btn_deactivate_cusomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                        .setTitle(Html.fromHtml("<b> Deactivate Customer </b>"))
                        .setMessage("Are you sure you want to deactivate " + txt_cust_name.getText().toString() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                remove_Customer(txt_cust_name.getTag().toString());

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //adapter_cutomer_list.notifyDataSetChanged();
                    }
                }).show();
            }
        });

        getCustomerDetail();

    }

    private void remove_Customer(final String id) {


        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Customers").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(id)) {

                                DatabaseReference cineIndustryRef = databaseReference.child("Customers").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("status", 1);
                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Query queryPay = databaseReference.child("Payments").orderByChild("customer_id").equalTo(id);
                                        databaseReference.keepSynced(true);
                                        queryPay.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot npsnapshot) {
                                                hidePrd();
                                                try {
                                                    if (npsnapshot.exists()) {
                                                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {
                                                                DatabaseReference cineIndustryRef = databaseReference.child("Payments").child(dataSnapshot.getKey());
                                                                Map<String, Object> map = new HashMap<>();
                                                                map.put("customer_status", "1");

                                                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        hidePrd();
                                                                        new CToast(mActivity).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                                    }
                                                                });
                                                        }
                                                        hidePrd();
                                                        new CToast(mActivity).simpleToast("Customer deactivate successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                        //    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                                                        overridePendingTransition(0, 0);
                                                        finish();
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
                    Log.e("exception   ", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
                Log.e("databaseError   ", databaseError.getMessage());
            }
        });

       /* showPrd();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference cineIndustryRef = rootRef.child("Customers").child(id);

        Map<String, Object> map = new HashMap<>();
        map.put("status", "1");
        Task<Void> voidTask = cineIndustryRef.updateChildren(map);
        if (voidTask.isSuccessful()) {
            hidePrd();
            new CToast(mActivity).simpleToast("Customer deactivate successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
            overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
            finish();
        } else {
            hidePrd();
            new CToast(mActivity).simpleToast("Please try later...", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
        }*/
    }

    private void remove_Customer1(final String id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.CUSTOMER_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log.e("Delete Customer", response);

                        hidePrd();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("result") == 1) {

                                new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                //getCustomerDetail();
                                finish();
                                overridePendingTransition(0, 0);
                               // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

                            } else {
                                new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hidePrd();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("customer_id", id);

                return params;
            }

        };

        try {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCustomerDetail() {
        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Customers").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {
                            if (dataSnapshot.child("id").getValue().toString().equals(customer_id)) {
                                arrayListPlotDetail.clear();
                                arrayListPropertyDetail.clear();
                                arrayListLoanDetails.clear();

                                txt_cust_name.setTag(dataSnapshot.child("id").getValue().toString());
                                txt_cust_name.setText(dataSnapshot.child("name").getValue().toString());

                                if (!dataSnapshot.child("email").getValue().toString().equals("")) {
                                    ll_cust_email.setVisibility(View.VISIBLE);
                                    txt_cust_email.setText(dataSnapshot.child("email").getValue().toString());
                                } else {
                                    ll_cust_email.setVisibility(View.GONE);
                                }

                                txt_cust_no.setText(dataSnapshot.child("contact_no").getValue().toString());

                                txt_cust_no_1.setText(dataSnapshot.child("contact_no1").getValue().toString());

                                if (dataSnapshot.child("contact_no1").getValue().toString().equals("")) {
                                    ll_cust_another_no.setVisibility(View.VISIBLE);
                                    txt_cust_no_1.setText(dataSnapshot.child("contact_no1").getValue().toString());
                                } else {
                                    ll_cust_another_no.setVisibility(View.GONE);
                                }

                                if (!dataSnapshot.child("address").getValue().toString().equals("")) {
                                    ll_cust_address.setVisibility(View.VISIBLE);
                                   // txt_cust_address.setText(dataSnapshot.child("address").getValue().toString() + " " + dataSnapshot.child("city").getValue().toString());
                                    txt_cust_address.setText(dataSnapshot.child("address").getValue().toString());
                                    txt_cust_address.setTag(dataSnapshot.child("address").getValue().toString());
                                    city = dataSnapshot.child("city").getValue().toString();
                                } else {
                                    ll_cust_address.setVisibility(View.GONE);
                                }

                                if (!dataSnapshot.child("kyc_aadhar").getValue().toString().equals("")) {

                                    img_aadhar_card.setTag("1");
                                    img_path_adhar = dataSnapshot.child("kyc_aadhar").getValue().toString();

                                    Picasso.with(mActivity)
                                            .load(dataSnapshot.child("kyc_aadhar").getValue().toString())
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .placeholder(R.drawable.img_placeholder)
                                            .into(img_aadhar_card);

                                } else {
                                    img_aadhar_card.setTag("0");
                                    img_aadhar_card.setImageResource(R.drawable.ic_add_image);
                                    img_path_adhar = "";
                                }

                                if (!dataSnapshot.child("kyc_aadhar_back").getValue().toString().equals("")) {

                                    img_aadhar_card_back.setTag("1");
                                    img_path_adhar_back = dataSnapshot.child("kyc_aadhar_back").getValue().toString();

                                    Picasso.with(mActivity)
                                            .load(dataSnapshot.child("kyc_aadhar_back").getValue().toString())
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .placeholder(R.drawable.img_placeholder)
                                            .into(img_aadhar_card_back);

                                } else {
                                    img_aadhar_card_back.setTag("0");
                                    img_aadhar_card_back.setImageResource(R.drawable.ic_add_image);
                                    img_path_adhar_back = "";
                                }


                                if (!dataSnapshot.child("kyc_pancard").getValue().toString().equals("")) {

                                    img_pan_card.setTag("1");
                                    img_path_pan = dataSnapshot.child("kyc_pancard").getValue().toString();
                                    Picasso.with(mActivity)
                                            .load(dataSnapshot.child("kyc_pancard").getValue().toString())
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .placeholder(R.drawable.img_placeholder)
                                            .into(img_pan_card);
                                } else {
                                    img_pan_card.setTag("0");
                                    img_path_pan = "";
                                    img_pan_card.setImageResource(R.drawable.ic_add_image);

                                }

                                if (!dataSnapshot.child("kyc_driving_lic").getValue().toString().equals("")) {

                                    img_driving_licence.setTag("1");
                                    img_path_driving = dataSnapshot.child("kyc_driving_lic").getValue().toString();
                                    Picasso.with(mActivity)
                                            .load(dataSnapshot.child("kyc_driving_lic").getValue().toString())
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .placeholder(R.drawable.img_placeholder)
                                            .into(img_driving_licence);
                                } else {

                                    img_driving_licence.setTag("0");
                                    img_path_driving = "";
                                    img_driving_licence.setImageResource(R.drawable.ic_add_image);

                                }

                                if (!dataSnapshot.child("kyc_ration").getValue().toString().equals("")) {

                                    img_ration_card.setTag("1");
                                    img_path_voteris = dataSnapshot.child("kyc_ration").getValue().toString();
                                    Picasso.with(mActivity)
                                            .load(dataSnapshot.child("kyc_ration").getValue().toString())
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .placeholder(R.drawable.img_placeholder)
                                            .into(img_ration_card);
                                } else {
                                    img_ration_card.setTag("0");
                                    img_path_voteris = "";
                                    img_ration_card.setImageResource(R.drawable.ic_add_image);
                                }

                                if (!dataSnapshot.child("kyc_other").getValue().toString().equals("")) {

                                    img_other.setTag("1");
                                    img_path_other = dataSnapshot.child("kyc_other").getValue().toString();
                                    Picasso.with(mActivity)
                                            .load(dataSnapshot.child("kyc_other").getValue().toString())
                                            .networkPolicy(NetworkPolicy.NO_CACHE)
                                            .placeholder(R.drawable.img_placeholder)
                                            .into(img_other);

                                } else {
                                    img_other.setTag("0");
                                    img_path_other = "";
                                    img_other.setImageResource(R.drawable.ic_add_image);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
            }
        });


        Query queryPlots = databaseReference.child("Plots").orderByKey();
        databaseReference.keepSynced(true);
        queryPlots.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {

                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {
                            if (dataSnapshot.child("customer_id").getValue().toString().equals(customer_id)) {

                                System.out.println("======plot no  " + dataSnapshot.child("plot_no").getValue().toString());

                                Item_Plot_Detail item = new Item_Plot_Detail();

                                item.setId(dataSnapshot.child("id").getValue().toString());
                                item.setPlot_no(dataSnapshot.child("plot_no").getValue().toString());
                                item.setPurchase_price(dataSnapshot.child("purchase_price").getValue().toString());
                                item.setDate_of_purchase(dataSnapshot.child("date_of_purchase").getValue().toString());

                                //get Site name and site address from site table

                                Query querySites = databaseReference.child("Sites").orderByKey();
                                databaseReference.keepSynced(true);
                                querySites.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot dataSnapshotSite : snapshot.getChildren()) {
                                                if (dataSnapshotSite.child("id").getValue().toString().equals(dataSnapshot.child("site_id").getValue().toString())) {
                                                    item.setSite_name(dataSnapshotSite.child("site_name").getValue().toString());
                                                    item.setSite_address(dataSnapshotSite.child("site_address").getValue().toString());

                                                    Log.e("site_name   ", dataSnapshotSite.child("site_name").getValue().toString());
                                                    Log.e("site_address   ", dataSnapshotSite.child("site_address").getValue().toString());

                                                }
                                            }
                                            if (arrayListPlotDetail.size() > 0) {
                                                ll_plot.setVisibility(View.VISIBLE);
                                            } else {
                                                ll_plot.setVisibility(View.GONE);
                                            }

                                            mAdapterPlot.notifyDataSetChanged();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("Site ", error.getMessage());
                                        hidePrd();
                                    }
                                });


                                ArrayList<Item_Plot_Detail.Item_Plot_Payment> paymentArrayList = new ArrayList<>();
                                Query queryPayments = databaseReference.child("Payments").orderByKey();
                                databaseReference.keepSynced(true);
                                queryPayments.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            totlePaidAmount = 0;

                                            for (DataSnapshot dataSnapshotPayment : snapshot.getChildren()) {
                                                if (dataSnapshotPayment.child("plot_id").getValue().toString().equals(dataSnapshot.child("id").getValue().toString())) {

                                                    System.out.println("=========plot id    " + dataSnapshotPayment.child("plot_id").getValue().toString());

                                                    Item_Plot_Detail.Item_Plot_Payment payment = new Item_Plot_Detail.Item_Plot_Payment();
                                                    payment.setId(dataSnapshotPayment.child("id").getValue().toString());
                                                    payment.setAmount(dataSnapshotPayment.child("amount").getValue().toString());
                                                    payment.setRemarks(dataSnapshotPayment.child("remarks").getValue().toString());
                                                    payment.setCustomer_id(dataSnapshotPayment.child("customer_id").getValue().toString());
                                                    payment.setPayment_attachment(dataSnapshotPayment.child("payment_attachment").getValue().toString());
                                                    if(dataSnapshotPayment.hasChild("file_type")){
                                                        payment.setFile_type(dataSnapshotPayment.child("file_type").getValue().toString());
                                                    }
                                                    else {
                                                        payment.setFile_type("");
                                                    }
                                                    payment.setCustomer_name(customer_name);
                                                    // payment.setPayment_date(dataSnapshotPayment.child("payment_date").getValue().toString());
                                                    // String paidAmount = dataSnapshotPayment.child("amount").getValue().toString();
                                                    System.out.println("=======amount  " + dataSnapshotPayment.child("amount").getValue().toString());
                                                    totlePaidAmount = totlePaidAmount + Integer.valueOf(dataSnapshotPayment.child("amount").getValue().toString());
                                                    System.out.println("=======totlePaidAmount  " + String.valueOf(totlePaidAmount));

                                                    try {
                                                        DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                        Date d = f.parse(dataSnapshotPayment.child("payment_date").getValue().toString());
                                                        DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                                        DateFormat time = new SimpleDateFormat("hh:mm:ss");
                                                        System.out.println("=====Date: " + date.format(d));
                                                        System.out.println("======Time: " + time.format(d));
                                                        payment.setPayment_date(date.format(d));
                                                        payment.setPayment_time(time.format(d));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }

                                                    paymentArrayList.add(payment);
                                                }
                                            }
                                            item.setPending_amount(String.valueOf(Integer.parseInt(dataSnapshot.child("purchase_price").getValue().toString()) - totlePaidAmount));
                                            item.setPaid_amount(String.valueOf(totlePaidAmount));

                                            if (arrayListPlotDetail.size() > 0) {
                                                ll_plot.setVisibility(View.VISIBLE);
                                            } else {
                                                ll_plot.setVisibility(View.GONE);
                                            }

                                            mAdapterPlot.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("Payments ", error.getMessage());
                                        hidePrd();
                                    }
                                });

                               /* item.setPending_amount(object1.getString("pending_amount"));
                                item.setPaid_amount(object1.getString("paid_amount"));*/


                                item.setArrayListPayment(paymentArrayList);
                                arrayListPlotDetail.add(item);

                            }

                        }

                        if (arrayListPlotDetail.size() > 0) {
                            ll_plot.setVisibility(View.VISIBLE);
                        } else {
                            ll_plot.setVisibility(View.GONE);
                        }

                        for (int i = 0; i < arrayListPlotDetail.size(); i++) {
                            System.out.println("site name  " + arrayListPlotDetail.get(i).getSite_name());
                            System.out.println("site address  " + arrayListPlotDetail.get(i).getSite_address());
                            System.out.println("site paid Amount  " + arrayListPlotDetail.get(i).getPaid_amount());
                            System.out.println("site pending Amount  " + arrayListPlotDetail.get(i).getPending_amount());
                        }

                        mAdapterPlot.notifyDataSetChanged();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Plots   ", databaseError.getMessage());
                hidePrd();
            }
        });


        Query queryProperties = databaseReference.child("Properties").orderByKey();
        queryProperties.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshotPro : npsnapshot.getChildren()) {
                            if (dataSnapshotPro.child("customer_id").getValue().toString().equals(customer_id)) {

                                Item_Property_Detail item = new Item_Property_Detail();

                                item.setId(dataSnapshotPro.child("id").getValue().toString());
                                item.setProperty_name(dataSnapshotPro.child("property_name").getValue().toString());
                                item.setAddress(dataSnapshotPro.child("address").getValue().toString());
                                item.setRent(dataSnapshotPro.child("rent").getValue().toString());
                                item.setHired_since(dataSnapshotPro.child("hired_since").getValue().toString());

                                ArrayList<Item_Property_Detail.Item_Plot_Payment> paymentArrayList = new ArrayList<>();


                                Query queryDocument = databaseReference.child("Payments").orderByKey();
                                queryDocument.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                    if (npsnapshot1.child("property_id").getValue().toString().equals(dataSnapshotPro.child("id").getValue().toString())) {
                                                        if (npsnapshot1.child("customer_id").getValue().toString().equals(dataSnapshotPro.child("customer_id").getValue().toString())) {
                                                            if(!(npsnapshot.child("payment_date").getValue().toString().equals("0000-00-00 00:00:00"))){
                                                                 Item_Property_Detail.Item_Plot_Payment payment = new Item_Property_Detail.Item_Plot_Payment();
                                                            payment.setId(npsnapshot1.child("id").getValue().toString());
                                                            payment.setAmount(npsnapshot1.child("amount").getValue().toString());
                                                            payment.setRemarks(npsnapshot1.child("remarks").getValue().toString());
                                                            payment.setPayment_attachment(npsnapshot1.child("payment_attachment").getValue().toString());
                                                            if(npsnapshot1.hasChild("file_type")){
                                                                payment.setFile_type(npsnapshot1.child("file_type").getValue().toString());
                                                            }
                                                            else {
                                                                payment.setFile_type("");
                                                            }

                                                            try {
                                                                DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                                Date d = f.parse(npsnapshot1.child("payment_date").getValue().toString());
                                                                DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                                                DateFormat time = new SimpleDateFormat("hh:mm:ss");
                                                                System.out.println("=====Date: " + date.format(d));
                                                                System.out.println("======Time: " + time.format(d));
                                                                payment.setPayment_date(date.format(d));
                                                                payment.setPayment_time(time.format(d));
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }
                                                            paymentArrayList.add(payment);
                                                            }
                                                        }
                                                    }
                                                }
                                                if (arrayListPropertyDetail.size() > 0) {
                                                    ll_rented.setVisibility(View.VISIBLE);
                                                } else {
                                                    ll_rented.setVisibility(View.GONE);
                                                }

                                                mAdapterProperty.notifyDataSetChanged();
                                            }
                                        } catch (Exception e) {
                                            Log.e("Ex   ", e.toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("error ", error.getMessage());
                                    }
                                });


                                item.setArrayListPayment(paymentArrayList);
                                arrayListPropertyDetail.add(item);

                                if (arrayListPropertyDetail.size() > 0) {
                                    ll_rented.setVisibility(View.VISIBLE);
                                } else {
                                    ll_rented.setVisibility(View.GONE);
                                }

                                mAdapterProperty.notifyDataSetChanged();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("exception   ", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
                Log.e("databaseError   ", databaseError.getMessage());
            }
        });


        Query queryLoan = databaseReference.child("LoanDetails").orderByKey();
        queryLoan.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshotLoan : npsnapshot.getChildren()) {
                            if (dataSnapshotLoan.child("Customer_Id").getValue().toString().equals(customer_id)) {

                                Item_Loan_Details item = new Item_Loan_Details();

                                item.setId(dataSnapshotLoan.child("id").getValue().toString());
                                item.setApplicantion_Number(dataSnapshotLoan.child("Applicantion_Number").getValue().toString());
                                item.setLoan_Amount(dataSnapshotLoan.child("Loan_Amount").getValue().toString());
                                item.setApproved_Amount(dataSnapshotLoan.child("Approved_Amount").getValue().toString());
                                item.setInterest_Rate(dataSnapshotLoan.child("Interest_Rate").getValue().toString());

                                String loanStatus = dataSnapshotLoan.child("Loan_Status").getValue().toString();
                                if (loanStatus.equals("1")) {
                                    item.setLoan_Status("Pending");
                                } else if (loanStatus.equals("2")) {
                                    item.setLoan_Status("Approved");
                                } else if (loanStatus.equals("3")) {
                                    item.setLoan_Status("Rejected");
                                } else if (loanStatus.equals("4")) {
                                    item.setLoan_Status("Completed");
                                }

                                String loanType = dataSnapshotLoan.child("Loan_Type").getValue().toString();
                                if (loanType.equals("1")) {
                                    item.setLoan_Type("Regular");
                                } else if (loanType.equals("2")) {
                                    item.setLoan_Type("EMI");
                                } else if (loanType.equals("3")) {
                                    item.setLoan_Type("Daily");
                                }

                                item.setMonthly_EMI(dataSnapshotLoan.child("Monthly_EMI").getValue().toString());
                                item.setDate_Applied(dataSnapshotLoan.child("Date_Applied").getValue().toString());


                                ArrayList<Item_Loan_Details.Item_Loan_EMI> EMIArrayList = new ArrayList<>();

                                Query queryDocument = databaseReference.child("EMI_Received").orderByKey();
                                queryDocument.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot npsnapshot5 : dataSnapshot.getChildren()) {
                                                    if (npsnapshot5.child("Loan_Id").getValue().toString().equals(dataSnapshotLoan.child("id").getValue().toString())) {
                                                        if (npsnapshot5.child("Customer_Id").getValue().toString().equals(customer_id)) {

                                                            Item_Loan_Details.Item_Loan_EMI payment = new Item_Loan_Details.Item_Loan_EMI();
                                                            payment.setId(npsnapshot5.child("id").getValue().toString());
                                                            payment.setEMI_Amount(npsnapshot5.child("EMI_Amount").getValue().toString());
                                                            payment.setLoan_Remarks(npsnapshot5.child("Loan_Remarks").getValue().toString());
                                                            payment.setCustomer_id(npsnapshot5.child("Customer_Id").getValue().toString());


                                                            try {
                                                                DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                                                Date d = f.parse(npsnapshot5.child("EMI_Date").getValue().toString());
                                                                DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                                                DateFormat time = new SimpleDateFormat("hh:mm:ss");
                                                                System.out.println("=====Date: " + date.format(d));
                                                                System.out.println("======Time: " + time.format(d));
                                                                payment.setEMI_Date(date.format(d));
                                                                payment.setEMI_time(time.format(d));
                                                            } catch (ParseException e) {
                                                                e.printStackTrace();
                                                            }

                                                            Query queryCustomer = databaseReference.child("Customers").orderByKey();
                                                            queryCustomer.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    try {
                                                                        if (dataSnapshot.exists()) {
                                                                            for (DataSnapshot npsnapshotCustomer : dataSnapshot.getChildren()) {
                                                                                if (npsnapshotCustomer.child("id").getValue().toString().equals(customer_id)) {
                                                                                    String name = npsnapshotCustomer.child("name").getValue().toString();
                                                                                    payment.setCustomer_name(name);
                                                                                }
                                                                            }
                                                                            if (arrayListLoanDetails.size() > 0) {
                                                                                ll_loan.setVisibility(View.VISIBLE);
                                                                            } else {
                                                                                ll_loan.setVisibility(View.GONE);
                                                                            }

                                                                            mAdapterLoan.notifyDataSetChanged();
                                                                        }
                                                                    } catch (Exception e) {
                                                                        hidePrd();
                                                                        Log.e("Ex   ", e.toString());
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                    hidePrd();
                                                                    Log.e("error ", error.getMessage());
                                                                }
                                                            });
                                                            EMIArrayList.add(payment);
                                                        }
                                                    }
                                                }
                                                if (arrayListLoanDetails.size() > 0) {
                                                    ll_loan.setVisibility(View.VISIBLE);
                                                } else {
                                                    ll_loan.setVisibility(View.GONE);
                                                }

                                                mAdapterLoan.notifyDataSetChanged();
                                            }
                                        } catch (Exception e) {
                                            Log.e("Ex   ", e.toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("error ", error.getMessage());
                                    }
                                });
                                item.setArrayListEMI(EMIArrayList);
                                arrayListLoanDetails.add(item);

                                if (arrayListLoanDetails.size() > 0) {
                                    ll_loan.setVisibility(View.VISIBLE);
                                } else {
                                    ll_loan.setVisibility(View.GONE);
                                }

                                mAdapterLoan.notifyDataSetChanged();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("exception   ", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
                Log.e("databaseError   ", databaseError.getMessage());
            }
        });

    }

    public void getCustomerDetail1() {
        showPrd();

        Log.e("Cusomer Details ", API.CUSTOMER + "?type=View&customer_id=" + Aditya.ID);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.CUSTOMER + "?type=View&customer_id=" + Aditya.ID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Response Customer List", response);

                    if (jsonObject.getInt("result") == 1) {

                        arrayListPlotDetail.clear();
                        arrayListPropertyDetail.clear();
                        arrayListLoanDetails.clear();

                        JSONArray jsonArrayCust = jsonObject.getJSONArray("customer_detail");
                        JSONObject object = jsonArrayCust.getJSONObject(0);

                        txt_cust_name.setTag(object.getString("id"));
                        txt_cust_name.setText(object.getString("name"));

                        if (!object.getString("email").equals("")) {
                            ll_cust_email.setVisibility(View.VISIBLE);
                            txt_cust_email.setText(object.getString("email"));
                        } else {
                            ll_cust_email.setVisibility(View.GONE);
                        }

                        txt_cust_no.setText(object.getString("contact_no"));

                        if (!object.getString("contact_no1").equals("")) {
                            ll_cust_another_no.setVisibility(View.VISIBLE);
                            txt_cust_no_1.setText(object.getString("contact_no1"));
                        } else {
                            ll_cust_another_no.setVisibility(View.GONE);
                        }

                        if (!object.getString("address").equals("")) {
                            ll_cust_address.setVisibility(View.VISIBLE);
                            txt_cust_address.setText(object.getString("address") + " " + object.getString("city"));
                            txt_cust_address.setTag(object.getString("address"));
                            city = object.getString("city");
                        } else {
                            ll_cust_address.setVisibility(View.GONE);
                        }

                        if (!object.getString("kyc_aadhar").equals("")) {

                            img_aadhar_card.setTag("1");
                            img_path_adhar = object.getString("kyc_aadhar");

                            Picasso.with(mActivity)
                                    .load(object.getString("kyc_aadhar"))
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .placeholder(R.drawable.img_placeholder)
                                    .into(img_aadhar_card);

                        } else {
                            img_aadhar_card.setTag("0");
                            img_aadhar_card.setImageResource(R.drawable.ic_add_image);
                            img_path_adhar = "";
                        }

                        if (!object.getString("kyc_aadhar_back").equals("")) {

                            img_aadhar_card_back.setTag("1");
                            img_path_adhar_back = object.getString("kyc_aadhar_back");

                            Picasso.with(mActivity)
                                    .load(object.getString("kyc_aadhar_back"))
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .placeholder(R.drawable.img_placeholder)
                                    .into(img_aadhar_card_back);

                        } else {
                            img_aadhar_card_back.setTag("0");
                            img_aadhar_card_back.setImageResource(R.drawable.ic_add_image);
                            img_path_adhar_back = "";
                        }


                        if (!object.getString("kyc_pancard").equals("")) {

                            img_pan_card.setTag("1");
                            img_path_pan = object.getString("kyc_pancard");
                            Picasso.with(mActivity)
                                    .load(object.getString("kyc_pancard"))
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .placeholder(R.drawable.img_placeholder)
                                    .into(img_pan_card);
                        } else {
                            img_pan_card.setTag("0");
                            img_path_pan = "";
                            img_pan_card.setImageResource(R.drawable.ic_add_image);

                        }

                        if (!object.getString("kyc_driving_lic").equals("")) {

                            img_driving_licence.setTag("1");
                            img_path_driving = object.getString("kyc_driving_lic");
                            Picasso.with(mActivity)
                                    .load(object.getString("kyc_driving_lic"))
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .placeholder(R.drawable.img_placeholder)
                                    .into(img_driving_licence);
                        } else {

                            img_driving_licence.setTag("0");
                            img_path_driving = "";
                            img_driving_licence.setImageResource(R.drawable.ic_add_image);

                        }

                        if (!object.getString("kyc_ration").equals("")) {

                            img_ration_card.setTag("1");
                            img_path_voteris = object.getString("kyc_ration");
                            Picasso.with(mActivity)
                                    .load(object.getString("kyc_ration"))
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .placeholder(R.drawable.img_placeholder)
                                    .into(img_ration_card);
                        } else {
                            img_ration_card.setTag("0");
                            img_path_voteris = "";
                            img_ration_card.setImageResource(R.drawable.ic_add_image);
                        }

                        if (!object.getString("kyc_other").equals("")) {

                            img_other.setTag("1");
                            img_path_other = object.getString("kyc_other");
                            Picasso.with(mActivity)
                                    .load(object.getString("kyc_other"))
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .placeholder(R.drawable.img_placeholder)
                                    .into(img_other);

                        } else {
                            img_other.setTag("0");
                            img_path_other = "";
                            img_other.setImageResource(R.drawable.ic_add_image);
                        }

                        JSONArray jsonArray = jsonObject.getJSONArray("plot_detail");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object1 = jsonArray.getJSONObject(i);

                            Item_Plot_Detail item = new Item_Plot_Detail();

                            item.setId(object1.getString("id"));
                            item.setPlot_no(object1.getString("plot_no"));
                            item.setSite_name(object1.getString("site_name"));
                            item.setSite_address(object1.getString("site_address"));
                            item.setPurchase_price(object1.getString("purchase_price"));
                            item.setDate_of_purchase(object1.getString("date_of_purchase"));
                            item.setPending_amount(object1.getString("pending_amount"));
                            item.setPaid_amount(object1.getString("paid_amount"));

                            ArrayList<Item_Plot_Detail.Item_Plot_Payment> paymentArrayList = new ArrayList<>();

                            JSONArray jsonArrayPayment = object1.getJSONArray("payment_list");
                            for (int j = 0; j < jsonArrayPayment.length(); j++) {
                                JSONObject object2 = jsonArrayPayment.getJSONObject(j);

                                Item_Plot_Detail.Item_Plot_Payment payment = new Item_Plot_Detail.Item_Plot_Payment();
                                payment.setId(object2.getString("id"));
                                payment.setAmount(object2.getString("amount"));
                                payment.setRemarks(object2.getString("remarks"));
                                payment.setCustomer_id(object2.getString("customer_id"));
                                payment.setCustomer_name(object2.getString("customer_name"));
                                payment.setPayment_date(object2.getString("payment_date"));
                                payment.setPayment_time(object2.getString("payment_time"));
                                paymentArrayList.add(payment);

                            }

                            item.setArrayListPayment(paymentArrayList);
                            arrayListPlotDetail.add(item);
                        }

                        if (arrayListPlotDetail.size() > 0) {
                            ll_plot.setVisibility(View.VISIBLE);
                        } else {
                            ll_plot.setVisibility(View.GONE);
                        }

                        mAdapterPlot.notifyDataSetChanged();

                        JSONArray jsonArrayProperty = jsonObject.getJSONArray("properties_detail");
                        for (int i = 0; i < jsonArrayProperty.length(); i++) {
                            JSONObject object1 = jsonArrayProperty.getJSONObject(i);

                            Item_Property_Detail item = new Item_Property_Detail();

                            item.setId(object1.getString("id"));
                            item.setProperty_name(object1.getString("property_name"));
                            item.setAddress(object1.getString("address"));
                            item.setRent(object1.getString("rent"));
                            item.setHired_since(object1.getString("hired_since"));

                            ArrayList<Item_Property_Detail.Item_Plot_Payment> paymentArrayList = new ArrayList<>();

                            JSONArray jsonArrayPayment = object1.getJSONArray("payment_list");
                            for (int j = 0; j < jsonArrayPayment.length(); j++) {
                                JSONObject object2 = jsonArrayPayment.getJSONObject(j);

                                Item_Property_Detail.Item_Plot_Payment payment = new Item_Property_Detail.Item_Plot_Payment();
                                payment.setId(object2.getString("id"));
                                payment.setAmount(object2.getString("amount"));
                                payment.setRemarks(object2.getString("remarks"));
                                payment.setPayment_date(object2.getString("payment_date"));
                                payment.setPayment_time(object2.getString("payment_time"));
                                paymentArrayList.add(payment);

                            }

                            item.setArrayListPayment(paymentArrayList);
                            arrayListPropertyDetail.add(item);
                        }

                        if (arrayListPropertyDetail.size() > 0) {
                            ll_rented.setVisibility(View.VISIBLE);
                        } else {
                            ll_rented.setVisibility(View.GONE);
                        }

                        mAdapterProperty.notifyDataSetChanged();

                        JSONArray jsonArrayLoan = jsonObject.getJSONArray("loan_detail");
                        for (int i = 0; i < jsonArrayLoan.length(); i++) {
                            JSONObject object1 = jsonArrayLoan.getJSONObject(i);

                            Item_Loan_Details item = new Item_Loan_Details();

                            item.setId(object1.getString("id"));
                            item.setApplicantion_Number(object1.getString("Applicantion_Number"));
                            item.setLoan_Amount(object1.getString("Loan_Amount"));
                            item.setApproved_Amount(object1.getString("Approved_Amount"));
                            item.setInterest_Rate(object1.getString("Interest_Rate"));
                            item.setLoan_Status(object1.getString("Loan_Status"));
                            item.setLoan_Type(object1.getString("Loan_Type"));
                            item.setMonthly_EMI(object1.getString("Monthly_EMI"));
                            item.setDate_Applied(object1.getString("Date_Applied"));

                            ArrayList<Item_Loan_Details.Item_Loan_EMI> EMIArrayList = new ArrayList<>();

                            JSONArray jsonArrayPayment = object1.getJSONArray("emi_list");
                            for (int j = 0; j < jsonArrayPayment.length(); j++) {
                                JSONObject object2 = jsonArrayPayment.getJSONObject(j);

                                Item_Loan_Details.Item_Loan_EMI payment = new Item_Loan_Details.Item_Loan_EMI();
                                payment.setId(object2.getString("id"));
                                payment.setEMI_Amount(object2.getString("EMI_Amount"));
                                payment.setLoan_Remarks(object2.getString("Loan_Remarks"));
                                payment.setCustomer_id(object2.getString("customer_id"));
                                payment.setCustomer_name(object2.getString("customer_name"));
                                payment.setEMI_Date(object2.getString("EMI_Date"));
                                payment.setEMI_time(object2.getString("EMI_time"));
                                EMIArrayList.add(payment);

                            }

                            item.setArrayListEMI(EMIArrayList);
                            arrayListLoanDetails.add(item);
                        }

                        if (arrayListLoanDetails.size() > 0) {
                            ll_loan.setVisibility(View.VISIBLE);
                        } else {
                            ll_loan.setVisibility(View.GONE);
                        }

                        mAdapterProperty.notifyDataSetChanged();

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


    public void addPaymentforPlot(final String date, final String time, final String amount, final String id, final String remark, final String next_date,int checkButton) {
        showPrd();


        if (String.valueOf(capturedImageURI).equals("")) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            String key = rootRef.child("Payments").push().getKey();

            Map<String, Object> map = new HashMap<>();
            map.put("amount", amount);

            try {
                DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                Date d = f.parse(next_date);
                DateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
                System.out.println("=====Date: " + date2.format(d));
                map.put("next_payment_date", date2.format(d));
                System.out.println("=======next_date   " + date2.format(d));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            map.put("payment_attachment", String.valueOf(capturedImageURI));
            String finalTime = time.substring(0, 5);
            finalTime = finalTime + ":00";
            Log.e("Time  ", finalTime);
            map.put("payment_date", date + " " + finalTime);
            map.put("remarks", remark);
            Log.e("Customer Id   ", customer_id);
            map.put("customer_id", customer_id);
            map.put("id", key);
            map.put("plot_id", id);
            map.put("property_id", 0);
            map.put("customer_status", 0);
            map.put("file_type", mime);

            //TODO
            map.put("rent_status", 0);
            map.put("payment_status", 0);

            rootRef.child("Payments").child(key).setValue(map).addOnCompleteListener(Activity_Customer_Details.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    new CToast(mActivity).simpleToast("Payment add successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    hidePrd();
                    imageEncode = "";
                    capturedImageURI = Uri.parse("");
                    mime="";
                    if (checkButton == 1) {
                        if (appInstalledOrNot() == 0) {
                            if (checkButton == 1) {
                                sendMessage(amount, "com.whatsapp");
                            }
                        } else if (appInstalledOrNot() == 1) {
                            if (checkButton == 1) {
                                sendMessage(amount, "com.whatsapp.w4b");
                            }
                        } else if (appInstalledOrNot() == 2) {
                            if (checkButton == 1) {
                                sendMessage(amount, "both");
                            }
                        }
                    }
                    getCustomerDetail();
                }

            }).addOnFailureListener(Activity_Customer_Details.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hidePrd();
                    Toast.makeText(Activity_Customer_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
                }

            });
        } else {
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

                                    try {
                                        DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                                        Date d = f.parse(next_date);
                                        DateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
                                        System.out.println("=====Date: " + date2.format(d));
                                        map.put("next_payment_date", date2.format(d));
                                        System.out.println("=======next_date   " + date2.format(d));

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    map.put("payment_attachment", uri.toString());
                                    String finalTime = time.substring(0, 5);
                                    finalTime = finalTime + ":00";
                                    Log.e("Time  ", finalTime);
                                    map.put("payment_date", date + " " + finalTime);
                                    map.put("remarks", remark);
                                    Log.e("Customer Id   ", customer_id);
                                    map.put("customer_id", customer_id);
                                    map.put("id", key);
                                    map.put("plot_id", id);
                                    map.put("property_id", 0);
                                    map.put("customer_status", 0);
                                    map.put("file_type", mime);

                                    //TODO
                                    map.put("rent_status", 0);
                                    map.put("payment_status", 0);

                                    rootRef.child("Payments").child(key).setValue(map).addOnCompleteListener(Activity_Customer_Details.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            new CToast(mActivity).simpleToast("Payment add successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                            hidePrd();
                                            imageEncode = "";
                                            capturedImageURI = Uri.parse("");
                                            mime="";
                                            if (checkButton == 1) {
                                                if (appInstalledOrNot() == 0) {
                                                    if (checkButton == 1) {
                                                        sendMessage(amount, "com.whatsapp");
                                                    }
                                                } else if (appInstalledOrNot() == 1) {
                                                    if (checkButton == 1) {
                                                        sendMessage(amount, "com.whatsapp.w4b");
                                                    }
                                                } else if (appInstalledOrNot() == 2) {
                                                    if (checkButton == 1) {
                                                        sendMessage(amount, "both");
                                                    }
                                                }
                                            }
                                            getCustomerDetail();

                                        }

                                    }).addOnFailureListener(Activity_Customer_Details.this, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            hidePrd();
                                            Toast.makeText(Activity_Customer_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Activity_Customer_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
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

    private void sendMessage(String amount, String pkg) {
        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("text/plain");
        String text="";
        text = "Dear  '"+customer_name+"'\n"+"Your payment has been credited "+getResources().getString(R.string.rupee)+amount+" to "+getResources().getString(R.string.app_name)+".\n"+"\nThanks";

        if (pkg.equalsIgnoreCase("both")) {
            showBottomSheet(text);
        } else {
            waIntent.setPackage(pkg);
            if (waIntent != null) {
                waIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(waIntent, text));
            } else {
                Toast.makeText(this, "WhatsApp not found", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void showBottomSheet(String text) {
        ActionBottomDialogFragment addPhotoBottomDialogFragment =
                new ActionBottomDialogFragment(text);
        addPhotoBottomDialogFragment.show(this.getSupportFragmentManager(),
                ActionBottomDialogFragment.TAG);
    }

    @Override
    public void onItemClick(View view,String text) {
        if (view.getId() == R.id.button1) {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            if(waIntent!=null){
                waIntent.setPackage("com.whatsapp");
                if (waIntent != null) {
                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, text));
                } else {
                    Toast.makeText(this, "WhatsApp not found", Toast.LENGTH_SHORT)
                            .show();
                }
            }else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        } else if (view.getId() == R.id.button2) {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            if(waIntent!=null){
                waIntent.setPackage("com.whatsapp.w4b");
                if (waIntent != null) {
                    waIntent.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(waIntent, text));
                } else {
                    Toast.makeText(this, "WhatsApp not found", Toast.LENGTH_SHORT)
                            .show();
                }
            }else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addPaymentforPlot1(final String date, final String time, final String amount, final String id, final String remark) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.PAYMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Plot Add", response);

                    if (jsonObject.getInt("result") == 1) {
                        getCustomerDetail();
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
                params.put("plot_id", id);
                params.put("amount", amount);
                params.put("payment_date", date);
                params.put("payment_time", time);
                params.put("customer_id", txt_cust_name.getTag().toString());
                params.put("remarks", remark);
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

    public void addPaymentForRent(final String date, final String time, final String amount, final String id, final String remark, final String next_date,int checkButton) {
        showPrd();

        if (String.valueOf(capturedImageURI).equals("")) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            String key = rootRef.child("Payments").push().getKey();

            Map<String, Object> map = new HashMap<>();
            map.put("amount", amount);

            try {
                DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                Date d = f.parse(next_date);
                DateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
                System.out.println("=====Date: " + date2.format(d));
                map.put("next_payment_date", date2.format(d));
                System.out.println("=======next_date   " + date2.format(d));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            map.put("payment_attachment", String.valueOf(capturedImageURI));
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
            map.put("file_type", mime);

            //TODO
            map.put("rent_status", 0);
            map.put("payment_status", 0);

            rootRef.child("Payments").child(key).setValue(map).addOnCompleteListener(Activity_Customer_Details.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    new CToast(mActivity).simpleToast("Payment add successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    hidePrd();
                    imageEncode = "";
                    capturedImageURI = Uri.parse("");
                    mime="";
                    if (checkButton == 1) {
                        if (appInstalledOrNot() == 0) {
                            if (checkButton == 1) {
                                sendMessage(amount, "com.whatsapp");
                            }
                        } else if (appInstalledOrNot() == 1) {
                            if (checkButton == 1) {
                                sendMessage(amount, "com.whatsapp.w4b");
                            }
                        } else if (appInstalledOrNot() == 2) {
                            if (checkButton == 1) {
                                sendMessage(amount, "both");
                            }
                        }
                    }
                    getCustomerDetail();
                }

            }).addOnFailureListener(Activity_Customer_Details.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hidePrd();
                    Toast.makeText(Activity_Customer_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
                }

            });
        } else {
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

                                    try {
                                        DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                                        Date d = f.parse(next_date);
                                        DateFormat date2 = new SimpleDateFormat("yyyy-MM-dd");
                                        System.out.println("=====Date: " + date2.format(d));
                                        map.put("next_payment_date", date2.format(d));
                                        System.out.println("=======next_date   " + date2.format(d));

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    map.put("payment_attachment", uri.toString());
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
                                    map.put("file_type", mime);

                                    //TODO
                                    map.put("rent_status", 0);
                                    map.put("payment_status", 0);

                                    rootRef.child("Payments").child(key).setValue(map).addOnCompleteListener(Activity_Customer_Details.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            new CToast(mActivity).simpleToast("Payment add successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                            hidePrd();
                                            imageEncode = "";
                                            capturedImageURI = Uri.parse("");
                                            mime="";
                                            if (checkButton == 1) {
                                                if (appInstalledOrNot() == 0) {
                                                    if (checkButton == 1) {
                                                        sendMessage(amount, "com.whatsapp");
                                                    }
                                                } else if (appInstalledOrNot() == 1) {
                                                    if (checkButton == 1) {
                                                        sendMessage(amount, "com.whatsapp.w4b");
                                                    }
                                                } else if (appInstalledOrNot() == 2) {
                                                    if (checkButton == 1) {
                                                        sendMessage(amount, "both");
                                                    }
                                                }
                                            }
                                            getCustomerDetail();

                                        }

                                    }).addOnFailureListener(Activity_Customer_Details.this, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            hidePrd();
                                            Toast.makeText(Activity_Customer_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Activity_Customer_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
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

    private int appInstalledOrNot() {
        String pkgW = "com.whatsapp";
        String pkgWB = "com.whatsapp.w4b";
        PackageManager pm = getPackageManager();
        int app_installed_whatsapp = -1;
        int app_installed_w4b = -1;
        int common = -1;
        try {
            pm.getPackageInfo(pkgW, PackageManager.GET_ACTIVITIES);
            app_installed_whatsapp = 1;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed_whatsapp = 0;
        }

        try {
            pm.getPackageInfo(pkgWB, PackageManager.GET_ACTIVITIES);
            app_installed_w4b = 1;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed_w4b = 0;
        }

        if (app_installed_w4b == 1 & app_installed_whatsapp == 1) {
            common = 2;
        } else if (app_installed_whatsapp == 1) {
            common = 0;
        } else if (app_installed_w4b == 1) {
            common = 1;
        }

        return common;
    }

    public void addPaymentForRent1(final String date, final String time, final String amount, final String id, final String remark) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.PAYMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Plot Add", response);

                    if (jsonObject.getInt("result") == 1) {
                        getCustomerDetail();
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
                params.put("customer_id", txt_cust_name.getTag().toString());
                params.put("remarks", remark);
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

    public void showImageOption(final View view, final String img_path) {
        CharSequence[] items_name = {"Open Image", "Remove Image"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Select Option");
        builder.setItems(items_name, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    isImageOpen = true;
                    zoomImageFromThumb(view, img_path);
                    imgPath = img_path;
                } else {

                    new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete this image")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    remove_Image(img_path);
                                }
                            }).setNegativeButton("NO", null).show();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "AdityaGroup_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File img = File.createTempFile(pictureFile, ".jpg", storageDir);
        if (storageDir.isDirectory()) {
            String[] children = storageDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(storageDir, children[i]).delete();
            }
        }
        pictureFilePath = img.getAbsolutePath();
        return img;
    }

    public void showAttachImageOption() {
        CharSequence[] items_name = {"Capture Image", "Select From Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Select Option");
        builder.setItems(items_name, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                ATTACH_ITEM_POS = item;
                if (Permission.hasPermissions(mActivity, permissions)) {
                    if (item == 0) {

                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            File pictureFile = null;
                            try {
                                pictureFile = getPictureFile();

                            } catch (IOException ex) {
                                Log.e("5419", ex.getMessage());
                                return;
                            }
                            if (pictureFile != null) {
                                photoURI = FileProvider.getUriForFile(Activity_Customer_Details.this,
                                        getPackageName() + ".fileProvider",
                                        pictureFile);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                overridePendingTransition(0, 0);
                               // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                            }
                        }

                       /* ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                        capturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);
                        startActivityForResult(intent, CAMERA_REQUEST);
                        overridePendingTransition(R.anim.feed_in, R.anim.feed_out);*/

                    } else {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                    }
                } else {
                    Permission.requestPermissions(mActivity, permissions, CAMERA_PERMISSION_CODE);
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                if (data != null) {
                    if (data.getData() != null) {
                        selectedImageUri = data.getData();
                        capturedImageURI=selectedImageUri;

                        ContentResolver cR = getContentResolver();
                        mime = cR.getType(selectedImageUri);

                        upload_Image();
                       /* try {
                            Bitmap bitmap = Helper.decodeUri(selectedImageUri, mActivity);
                            //addImage(bitmap);

                            if (bitmap != null) {
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                imageEncode = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                                upload_Image();
                            }

                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                            new CToast(this).simpleToast("Fail to attach image try again", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                        }*/
                    }
                } else {
                    new CToast(mActivity).simpleToast("Fail to attach image try again", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }

            } else if (requestCode == CAMERA_REQUEST) {

                selectedImageUri = photoURI;

                ContentResolver cR = getContentResolver();
                mime = cR.getType(selectedImageUri);

                upload_Image();

            } else if (requestCode == UPLOAD_REQUEST) {
                getCustomerDetail();
            }
        }

        if (requestCode == UPLOAD_REQUEST) {
            getCustomerDetail();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (Permission.hasPermissions(mActivity, permissions)) {
                if (ATTACH_ITEM_POS == 0) {

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        File pictureFile = null;
                        try {
                            pictureFile = getPictureFile();

                        } catch (IOException ex) {
                            Log.e("5419", ex.getMessage());
                            return;
                        }
                        if (pictureFile != null) {
                            photoURI = FileProvider.getUriForFile(Activity_Customer_Details.this,
                                    getPackageName() + ".fileProvider",
                                    pictureFile);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    }
                   // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

                  /*  ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    capturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);
                    startActivityForResult(intent, CAMERA_REQUEST);
                    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);*/

                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                }
            }
        }
    }

    public void addImage(Bitmap bitmap) {
        String imageEncode = "";
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            imageEncode = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        }

        base64Image = imageEncode;
        bitmapImage = bitmap;

        try {
            //Write file
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup
            stream.close();
            bitmapImage.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void upload_Image() {

        showPrd();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference cineIndustryRef = rootRef.child("Customers").child(customer_main_id);

        final StorageReference sRef = storageReference.child("/files" + "/" + UUID.randomUUID().toString());
        sRef.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onSuccess(Uri uri) {
                                //if (uriList.size() == multiplefirebasePath.size()) {
                                Map<String, Object> map = new HashMap<>();
                                String childName;
                                if (type.equals("kyc_other")) {
                                    map.put("kyc_other", uri.toString());

                                } else if (type.equals("kyc_ration")) {
                                    map.put("kyc_ration", uri.toString());
                                } else if (type.equals("kyc_driving_lic")) {
                                    map.put("kyc_driving_lic", uri.toString());
                                } else if (type.equals("kyc_pancard")) {
                                    map.put("kyc_pancard", uri.toString());
                                } else if (type.equals("kyc_aadhar_back")) {
                                    map.put("kyc_aadhar_back", uri.toString());
                                } else if (type.equals("kyc_aadhar")) {
                                    map.put("kyc_aadhar", uri.toString());
                                }

                                rootRef.child("Customers").child(customer_main_id).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        new CToast(mActivity).simpleToast("Image uploaded successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                                        getCustomerDetail();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hidePrd();
                                        Toast.makeText(Activity_Customer_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                    /*rootRef.child("Customers").child(customer_main_id).child().setValue(map).addOnCompleteListener(Activity_Customer_Details.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                new CToast(mActivity).simpleToast("Image uploaded successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                                                getCustomerDetail();
                                            }
                                        }).addOnFailureListener(Activity_Customer_Details.this, new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                hidePrd();
                                                Toast.makeText(Activity_Customer_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
                                            }
                                        });*/
                                //}
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hidePrd();
                        Toast.makeText(Activity_Customer_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
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

    private void remove_Image(String img_path) {

        showPrd();
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(img_path);

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hidePrd();
                // File deleted successfully

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                Query query = databaseReference.child("Customers").orderByKey();
                databaseReference.keepSynced(true);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot npsnapshot) {
                        hidePrd();
                        try {
                            if (npsnapshot.exists()) {
                                for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                                    if (dataSnapshot.child("id").getValue().toString().equals(customer_id)) {
                                        DatabaseReference cineIndustryRef = databaseReference.child("Customers").child(dataSnapshot.getKey());
                                        Map<String, Object> map = new HashMap<>();
                                        if (type.equals("kyc_other")) {
                                            map.put("kyc_other", "");

                                        } else if (type.equals("kyc_ration")) {
                                            map.put("kyc_ration", "");
                                        } else if (type.equals("kyc_driving_lic")) {
                                            map.put("kyc_driving_lic", "");
                                        } else if (type.equals("kyc_pancard")) {
                                            map.put("kyc_pancard", "");
                                        } else if (type.equals("kyc_aadhar_back")) {
                                            map.put("kyc_aadhar_back", "");
                                        } else if (type.equals("kyc_aadhar")) {
                                            map.put("kyc_aadhar", "");
                                        }
                                        Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                new CToast(mActivity).simpleToast("Image removed successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                              //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                                                mActivity.recreate();
                                                Log.d("Photo Delete S  ", "onSuccess: deleted file");
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                hidePrd();
                // Uh-oh, an error occurred!
                Log.d("Photo Delete F   ", "onFailure: did not delete file");
            }
        });
    }


    private void remove_Image1() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.UPLOAD_KYC,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log.e("Remove image", response);
                        Log.e("Upload image", response);

                        hidePrd();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("result") == 1) {

                                new CToast(mActivity).simpleToast("Image removed successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                               // overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                                overridePendingTransition(0, 0);
                                mActivity.recreate();
                                //expandedImageView.setVisibility(View.GONE);
                                //ll_content.setVisibility(View.VISIBLE);
                                /*shareItem.setVisible(false);
                                deleteItem.setVisible(false);
                                editItem.setVisible(true);
                                getCustomerDetail();*/
                                /*Log.d("TAG","=== type "+type);
                                if (type.equals("kyc_aadhar")) {
                                    img_aadhar_card.setImageResource(R.drawable.ic_add_image);
                                } else if (type.equals("kyc_pancard")) {
                                    img_pan_card.setImageResource(R.drawable.ic_add_image);
                                } else if (type.equals("kyc_driving_lic")) {
                                    img_driving_licence.setImageResource(R.drawable.ic_add_image);
                                } else if (type.equals("kyc_ration")) {
                                    img_ration_card.setImageResource(R.drawable.ic_add_image);
                                } else if (type.equals("kyc_other")) {
                                    img_other.setImageResource(R.drawable.ic_add_image);
                                }

                                expandedImageView.setVisibility(View.GONE);
                                ll_content.setVisibility(View.VISIBLE);
                                mCurrentAnimator = null;
                                isImageOpen = false;
                                editItem.setVisible(true);
                                shareItem.setVisible(false);
                                deleteItem.setVisible(false);
                                overridePendingTransition(R.anim.feed_in, R.anim.feed_out);*/

                            } else {
                                new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hidePrd();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "Remove");
                params.put("kyc", type);
                params.put("customer_id", txt_cust_name.getTag().toString().trim());

                return params;
            }

        };

        try {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void remove_Payment(final String id, String name) {

        Log.e("id   ",id);

        showPrd();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Payments").orderByKey();

        if (name.equals("Plot")) {
            applesQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    hidePrd();
                    try {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                                if (npsnapshot.getKey().toString().equals(id)) {
                                    npsnapshot.getRef().removeValue();
                                    new CToast(Activity_Customer_Details.this).simpleToast("Payment delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                    getCustomerDetail();
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
                    hidePrd();
                    Log.e("ViewAllSitesFragment", databaseError.getMessage());
                    new CToast(Activity_Customer_Details.this).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }
            });
        }
        else {
            applesQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    hidePrd();
                    try {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                                if (npsnapshot.getKey().toString().equals(id)) {
                                    npsnapshot.getRef().removeValue();
                                    new CToast(Activity_Customer_Details.this).simpleToast("Payment delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                    getCustomerDetail();
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
                    hidePrd();
                    Log.e("ViewAllSitesFragment", databaseError.getMessage());
                    new CToast(Activity_Customer_Details.this).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }
            });
        }

    }

    private void remove_Payment1(final String id, String name) {

        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.PAYMENTS_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  Log.e("Delete payment", response);

                        hidePrd();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("result") == 1) {

                                new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                getCustomerDetail();

                            } else {
                                new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hidePrd();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);

                return params;
            }

        };

        try {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void remove_EMI(final String id) {

        showPrd();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("EMI_Received").orderByKey();
        applesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if (npsnapshot.getKey().toString().equals(id)) {
                                npsnapshot.getRef().removeValue();
                                new CToast(Activity_Customer_Details.this).simpleToast("Payment delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                getCustomerDetail();
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
                hidePrd();
                Log.e("ViewAllSitesFragment", databaseError.getMessage());
                new CToast(Activity_Customer_Details.this).simpleToast(databaseError.getMessage().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
            }
        });

    }

    private void remove_EMI1(final String id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.DELETE_EMI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log.e("Delete payment", response);

                        hidePrd();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("result").equals("success")) {

                                new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                getCustomerDetail();

                            } else {
                                new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hidePrd();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "Delete");
                params.put("id", id);

                return params;
            }

        };

        try {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void zoomImageFromThumb(final View thumbView, String imageResId) {

        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        expandedImageView = (ImageView) findViewById(R.id.expanded_image);

        ll_content = (LinearLayout) findViewById(R.id.ll_content);

        Picasso.with(getApplicationContext()).load(imageResId)
                .into(expandedImageView);


        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds
                .width() / startBounds.height()) {

            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {

            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);
        ll_content.setVisibility(View.GONE);
        editItem.setVisible(false);
        shareItem.setVisible(true);
        deleteItem.setVisible(true);
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set.play(
                ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y,
                        startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();
                set.play(
                        ObjectAnimator.ofFloat(expandedImageView, View.X,
                                startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                                startBounds.top))
                        .with(ObjectAnimator.ofFloat(expandedImageView,
                                View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator.ofFloat(expandedImageView,
                                View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        ll_content.setVisibility(View.VISIBLE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        ll_content.setVisibility(View.VISIBLE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
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
        if (isImageOpen) {

            if (type.equals("kyc_aadhar")) {
                img_aadhar_card.setAlpha(1f);
            } else if (type.equals("kyc_pancard")) {
                img_pan_card.setAlpha(1f);
            } else if (type.equals("kyc_driving_lic")) {
                img_driving_licence.setAlpha(1f);
            } else if (type.equals("kyc_ration")) {
                img_ration_card.setAlpha(1f);
            } else if (type.equals("kyc_other")) {
                img_other.setAlpha(1f);
            } else if (type.equals("kyc_aadhar_back")) {
                img_aadhar_card_back.setAlpha(1f);
            }

            expandedImageView.setVisibility(View.GONE);
            ll_content.setVisibility(View.VISIBLE);
            mCurrentAnimator = null;
            isImageOpen = false;
            editItem.setVisible(true);
            shareItem.setVisible(false);
            deleteItem.setVisible(false);
            overridePendingTransition(0, 0);
            //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        } else {
            super.onBackPressed();
            overridePendingTransition(0, 0);
            //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_site_edit, menu);
        editItem = menu.findItem(R.id.edit_site);
        shareItem = menu.findItem(R.id.share);
        shareItem.setVisible(false);
        deleteItem = menu.findItem(R.id.delete);
        deleteItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_site:
                Intent intent = new Intent(mActivity, Activity_Customer_Add.class);
                intent.putExtra("TYPE", "EDIT");
                intent.putExtra("ID", txt_cust_name.getTag().toString());
                intent.putExtra("NAME", txt_cust_name.getText().toString());
                intent.putExtra("MOBILE", txt_cust_no.getText().toString());
                intent.putExtra("MOBILE1", txt_cust_no_1.getText().toString());
                intent.putExtra("EMAIL", txt_cust_email.getText().toString());
                intent.putExtra("ADDRESS", txt_cust_address.getText().toString());
                intent.putExtra("CITY", city);
                startActivityForResult(intent, UPLOAD_REQUEST);
                overridePendingTransition(0, 0);
                //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

                return true;
            case R.id.share:
                shareImg(imgPath);
                return true;
            case R.id.delete:
                showDeleteDialog(imgPath);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDeleteDialog(String imgPath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Customer_Details.this);
        builder.setTitle("Delete Image");
        builder.setMessage("Are you sure delete this image?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                remove_Image(imgPath);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void shareImg(String img_url) {
        Bitmap image = null;
        try {
            URL url = new URL(img_url);
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
            File file = new File(this.getExternalCacheDir(), "share.png");
            FileOutputStream fOut = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
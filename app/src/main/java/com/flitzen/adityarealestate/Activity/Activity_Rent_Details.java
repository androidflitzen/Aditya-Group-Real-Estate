package com.flitzen.adityarealestate.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.flitzen.adityarealestate.Aditya;
import com.flitzen.adityarealestate.Apiutils;
import com.flitzen.adityarealestate.Classes.API;
import com.flitzen.adityarealestate.Classes.CToast;
import com.flitzen.adityarealestate.Classes.EncodeBased64Binary;
import com.flitzen.adityarealestate.Classes.Helper;
import com.flitzen.adityarealestate.Classes.Permission;
import com.flitzen.adityarealestate.Classes.Utils;
import com.flitzen.adityarealestate.Classes.WebAPI;
import com.flitzen.adityarealestate.CommonModel;
import com.flitzen.adityarealestate.FileDownloader;
import com.flitzen.adityarealestate.Fragment.RentPaymentListFragment;
import com.flitzen.adityarealestate.Items.Item_Customer_List;
import com.flitzen.adityarealestate.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate.Items.Item_Property_List;
import com.flitzen.adityarealestate.Items.PlotDetailsForPDF;
import com.flitzen.adityarealestate.Items.RentDetailsForPDF;
import com.flitzen.adityarealestate.MyFilePath;
import com.flitzen.adityarealestate.R;
import com.flitzen.adityarealestate.UploadPdfModel;
import com.flitzen.adityarealestate.service.PDFGETMODEL;
import com.google.android.gms.common.api.Api;
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
import com.google.firebase.storage.StorageReference;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.internal.async.ArgumentsHolder;
import retrofit2.Call;
import retrofit2.Callback;

public class Activity_Rent_Details extends AppCompatActivity {

    Activity mActivity;
    ProgressDialog prd;

    String type = "", property_name = "", property_addess = "", property_id, agreement_pdf_url = "", property_rent = "",status="";

    TextView txt_property_name, txt_customer_name, txt_hired_since, txt_rent, txt_address, tvtitle, txt_agreement_status;

    ArrayList<Item_Plot_Payment_List> arrayListRentPayment = new ArrayList<>();

    View ll_plot, view_add_payment, view_remove_customer, view_manage_bill;
    RelativeLayout ivEdit;
    RelativeLayout ivRentDetailPdf;
    List<Item_Customer_List> itemListCustomer = new ArrayList<>();
    List<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();
    Toolbar toolbar;
    ImageView ivEdit1;
    String rent_amount = "";
    int UPLOAD_REQUEST = 001, SELECT_PICTURE = 002, CAMERA_REQUEST = 003, SELECT_AUDIO = 004, CAMERA_PERMISSION_CODE = 005, FILE_REQUEST_CODE = 101;
    String file_url = "", customer_id = "";
    private Uri capturedImageURI;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    int ATTACH_ITEM_POS = 0;
    LinearLayout tabs;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    LinearLayout layout_agreement_upload;
    String imageEncode = "", attachmentType = "";
    public static Uri filePathUri = null;
    RelativeLayout ivRentDeactivePdf;
    RentDetailsForPDF rentDetailsForPDF=new RentDetailsForPDF();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_details);
        ButterKnife.bind(this);

        requestStoragePermission();

        layout_agreement_upload = (LinearLayout) findViewById(R.id.layout_agreement_upload);


        property_name = getIntent().getStringExtra("property_name");
        property_id = getIntent().getStringExtra("property_id");
        status = getIntent().getStringExtra("status");


        txt_agreement_status = (TextView) findViewById(R.id.txt_agreement_status);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml(property_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ivEdit = findViewById(R.id.ivEdit);
        mActivity = Activity_Rent_Details.this;
        txt_property_name = (TextView) findViewById(R.id.txt_property_name);
        txt_customer_name = (TextView) findViewById(R.id.txt_customer_name);
        txt_hired_since = (TextView) findViewById(R.id.txt_hired_since);
        txt_rent = (TextView) findViewById(R.id.txt_rent);
        txt_address = (TextView) findViewById(R.id.txt_address);
        tvtitle = (TextView) findViewById(R.id.tvtitle);
        tvtitle.setText(Html.fromHtml(property_name));
        view_remove_customer = findViewById(R.id.view_remove_customer);
        ivRentDetailPdf = findViewById(R.id.ivRentDetailPdf);

        ivRentDeactivePdf = findViewById(R.id.ivRentDeactivePdf);

        /*ll_plot = (LinearLayout) findViewById(R.id.ll_plot);
        view_add_payment = findViewById(R.id.view_add_payment);
        view_manage_bill = findViewById(R.id.view_manage_bill);*/

        view_manage_bill = findViewById(R.id.view_manage_bill);


        tabs = findViewById(R.id.tabs1);

        // tabs.addTab(tabs.newTab().setText("Payment"));
        // tabs.addTab(tabs.newTab().setText("Bills"));
        //  tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        // if (getIntent().hasExtra("position")) {

        int position = getIntent().getIntExtra("position", 0);
        if (status.equals("0")) {
            llContent.setVisibility(View.VISIBLE);
            getRentDetail(String.valueOf(position));
        } else {
            llContent.setVisibility(View.GONE);
            ivRentDetailPdf.setVisibility(View.GONE);
            ivRentDeactivePdf.setVisibility(View.VISIBLE);
            // getPaymentRentCustomers();
        }
        // }
        customer_id = getIntent().getStringExtra("customer_id");
        Log.e("Customer Id Detail  ", customer_id);


        Fragment fragment = new RentPaymentListFragment();
        if (fragment != null) {

            Bundle bundle = new Bundle();
            bundle.putString("rent", property_rent);
            bundle.putString("customer_id", customer_id);
            bundle.putString("position", String.valueOf(position));
            bundle.putString("property_id", property_id);
            bundle.putString("status", status);
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                   // .setCustomAnimations(R.anim.feed_in, R.anim.feed_out)
                    .setCustomAnimations(0, 0)
                    .replace(R.id.fragment_container, fragment)
                    //.addToBackStack("fragment")
                    .commit();
        }

        //  pushFragment(new RentPaymentListFragment(customer_id , position,property_id));

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


        txt_agreement_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                 /*   Dexter.withActivity(Activity_Rent_Details.this)
                            .withPermissions(

                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                @Override
                                public void onPermissionsChecked(MultiplePermissionsReport report) {
                                    // check if all permissions are granted
                                    if (report.areAllPermissionsGranted()) {
                                        Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                                    }

                                    // check for permanent denial of any permission
                                    if (report.isAnyPermissionPermanentlyDenied()) {
                                        // show alert dialog navigating to Settings

                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).
                            withErrorListener(new PermissionRequestErrorListener() {
                                @Override
                                public void onError(DexterError error) {
                                    Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .onSameThread()
                            .check();
*/


                if (Permission.hasPermissions(Activity_Rent_Details.this, permissions)) {
                    if (txt_agreement_status.getText().toString().equals("Add Agreement")) {
                        //showAttachImageOption();
                        Intent intent = new Intent(Activity_Rent_Details.this, ListPDFActivity.class);
                        intent.putExtra("property_name", property_name);
                        intent.putExtra("property_id", property_id);
                        intent.putExtra("customer_id", customer_id);
                        intent.putExtra("position", position);

                        startActivity(intent);
                    } else {
                        openUpdateDialog_view();

                   /* startActivity(new Intent(mActivity, PdfViewActivity.class)
                            .putExtra("pdf_url", agreement_pdf_url));*/
                    }
                } else {
                    Permission.requestPermissions(Activity_Rent_Details.this, permissions,123);
                }
            }
        });



        /*view_add_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(mActivity);
                View view1 = inflater.inflate(R.layout.dialog_payment_add, null);

                AlertDialog.Builder builder1 = new AlertDialog.Builder(mActivity);
                builder1.setView(view1);
                final AlertDialog dialog = builder1.create();

                final EditText edt_paid_amount = (EditText) view1.findViewById(R.id.edt_paid_amount);
                final EditText edt_remark = (EditText) view1.findViewById(R.id.edt_remark);
                final TextView txt_date = (TextView) view1.findViewById(R.id.txt_date);
                final TextView txt_time = (TextView) view1.findViewById(R.id.txt_time);

                TextView btn_cancel = (TextView) view1.findViewById(R.id.btn_cancel);
                Button btn_add_payment = (Button) view1.findViewById(R.id.btn_add_payment);

                edt_paid_amount.setText(rent_amount);
                txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
                txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

                txt_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Helper.pick_Date(mActivity, txt_date);
                    }
                });
                Calendar mcurrentTime = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                txt_time.setText(hour + ":" + minute + ":00");
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
                        } else {
                            dialog.dismiss();
                            addPaymentForRent(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString(), Aditya.ID, edt_remark.getText().toString());
                        }
                    }
                });

                dialog.show();
            }
        }); */

        view_remove_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                        .setTitle(Html.fromHtml("<b> Remove Customer</b>"))
                        .setMessage("Are you sure you want to remove customer?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                unAssignProperty(txt_customer_name.getTag().toString(), Aditya.ID);

                            }
                        }).setNegativeButton("No", null).show();
            }
        });

       /* ivRentDetailPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                *//*Utils.showLog("==data" + customer_id + "  " + property_id);

                file_url = API.RENTDEACTIVECUSTOMER_PDFFILE + "property_id=" + property_id + "&" + "customer_id=" + customer_id;
                startActivity(new Intent(mActivity, PdfViewActivity.class)
                        .putExtra("pdf_url", file_url));
                Utils.showLog("=== file_url " + file_url);*//*

                Intent intent=new Intent(Activity_Rent_Details.this,PdfCreatorPlotActivity.class);
                intent.putExtra("RentDetails",rentDetailsForPDF);
                intent.putExtra("paymentList",arrayListRentPayment);
                startActivity(intent);


              *//*  file_url = API.PRINT_RENT_PAYMENT_URL + Aditya.ID;
                startActivity(new Intent(mActivity, PdfViewActivity.class)
                        .putExtra("pdf_url", file_url));
                Utils.showLog("=== file_url " + file_url); *//*
            }
        });*/

       /* ivRentDeactivePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(Activity_Rent_Details.this,PdfCreatorPlotActivity.class);
                intent.putExtra("RentDetails",rentDetailsForPDF);
                intent.putExtra("paymentList",arrayListRentPayment);
                startActivity(intent);

            }
        });*/

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateDialog();
            }
        });


        view_manage_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Rent_Details.this, CustomerBillsActivity.class);
                intent.putExtra("property_id", Aditya.ID);
                intent.putExtra("property_name", txt_property_name.getText().toString());
                intent.putExtra("customer_name", txt_customer_name.getText().toString());
                intent.putExtra("hired_since", txt_hired_since.getText().toString());
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });


        if (agreement_pdf_url.equalsIgnoreCase("")) {
            txt_agreement_status.setText("Add Agreement");
            txt_agreement_status.setBackgroundResource(R.drawable.round_simple_button);
            txt_agreement_status.setTextColor(getResources().getColor(R.color.black));
        } else {
            txt_agreement_status.setText("View Agreement");
            txt_agreement_status.setBackgroundResource(R.drawable.round_feel_button);
            txt_agreement_status.setTextColor(getResources().getColor(R.color.white));
        }

        ivEdit1 = (ImageView) findViewById(R.id.ivEdit1);
        ivEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

   /* private void selectFragment(int position) {
        if (position == 0) {
            pushFragment(new RentPaymentListFragment());
        } else if (position == 1) {
            //Fragment fragment = new CustomerBillListFragment();
            Bundle args = new Bundle();
            args.putString("property_id", Aditya.ID);
            args.putString("property_name", txt_property_name.getText().toString());
            args.putString("customer_name", txt_customer_name.getText().toString());
            args.putString("hired_since", txt_hired_since.getText().toString());
            // fragment.setArguments(args);
            // pushFragment(fragment);
        }
    }*/


    private void openUpdateDialog_view() {
        LayoutInflater inflater = LayoutInflater.from(Activity_Rent_Details.this);
        View view = inflater.inflate(R.layout.agreement_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Rent_Details.this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        LinearLayout layout_view = (LinearLayout) view.findViewById(R.id.layout_view);
        LinearLayout layou_delete = (LinearLayout) view.findViewById(R.id.layou_delete);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        layout_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //String maven = "maven.pdf";
                //new DownloadFile().execute(agreement_pdf_url, maven);


      /*          File pdfFile = new File(Environment.getExternalStorageDirectory() + "/testthreepdf/" + maven);  // -> filename = maven.pdf
                Uri path = Uri.fromFile(pdfFile);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/

                // try {

                try {
                    Toast.makeText(getBaseContext(), "Opening PDF... ", Toast.LENGTH_SHORT).show();
                    Intent inte = new Intent(Intent.ACTION_VIEW);
                    inte.setDataAndType(Uri.parse(agreement_pdf_url), "application/pdf");
                    startActivity(inte);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(Activity_Rent_Details.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                    Log.e("No available view PDF.", e.getMessage());
                }

//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(agreement_pdf_url));
//                    startActivity(browserIntent);

                    /*Intent intent = new Intent(Activity_Rent_Details.this,PdfViewActivity.class);
                    intent.putExtra("pdf_url",agreement_pdf_url);
                    intent.putExtra("only_load",true);
                      startActivity(intent);
                      */


//                } catch (ActivityNotFoundException e) {
//                    Toast.makeText(Activity_Rent_Details.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
//                }


            }
        });

        layou_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(Activity_Rent_Details.this);
                progressDialog.setTitle("Aditya Group");
                progressDialog.setMessage("Please wait..!!");

                progressDialog.show();

                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(agreement_pdf_url);

                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query applesQuery = ref.child("RantDocument").orderByChild("document").equalTo(agreement_pdf_url);

                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                    new CToast(mActivity).simpleToast("Agreement deleted successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                    progressDialog.dismiss();

                                        txt_agreement_status.setText("Add Agreement");
                                        txt_agreement_status.setBackgroundResource(R.drawable.round_simple_button);
                                        txt_agreement_status.setTextColor(getResources().getColor(R.color.black));

                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                Log.e("Cancel ", "onCancelled", databaseError.toException());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        dialog.dismiss();
                        // Uh-oh, an error occurred!
                        Log.d("Photo Delete F   ", "onFailure: did not delete file");
                    }
                });

                //Toast.makeText(getApplicationContext(),"delete",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }


    public void showAttachImageOption() {

        CharSequence[] items_name = {"Select PDF"};
        final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Select Option");
        builder.setItems(items_name, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                ATTACH_ITEM_POS = item;
                if (Permission.hasPermissions(mActivity, permissions)) {
                    /*if (item == 0) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Picture");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                        capturedImageURI = mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);
                        startActivityForResult(intent, CAMERA_REQUEST);

                    } else if (item == 1) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

                    } else {*/
                    openFileChooser();
                    //}
                } else {
                    Permission.requestPermissions(mActivity, permissions, 101);
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void openFileChooser() {
        try {
            /*Intent intent = new Intent(mActivity, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setShowImages(false)
                    .setShowAudios(false)
                    .setShowFiles(true)
                    .setShowVideos(false)
                    .setSuffixes("application/pdf")
                    .enableImageCapture(true)
                    .setMaxSelection(1)
                    .setSkipZeroSizeFiles(true)
                    .build());
            startActivityForResult(intent, FILE_REQUEST_CODE);*/


/*
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, 1);
*/

           /* Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
            startActivityForResult(intent, 1);
*/

            Intent intent = new Intent(Activity_Rent_Details.this, FilePickerActivity.class);
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





           /* Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), 1);*/

            /*File file = new File("/sdcard/");
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "testthreepdf");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
          /*  if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                new Activity_Rent_Details().ImageCompression(mActivity).execute(FileUtils.getPath(mActivity,selectedImageUri));
            } else if (requestCode == CAMERA_REQUEST) {
                new Activity_Rent_Details.ImageCompression(mActivity).execute(FileUtils.getPath(mActivity,capturedImageURI));
            }*/
            if (requestCode == FILE_REQUEST_CODE) {
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files != null && files.size() > 0) {
                    File attachmentFile = new File(files.get(0).getPath());
                    try {
                        imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(attachmentFile.getPath());
                        attachmentType = "pdf";
                        uploadpdf();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == 7) {
                Uri uri = data.getData();
                String uriString = uri.toString();
                File myFile = new File(uriString);
                String path = myFile.getAbsolutePath();
                Log.w("ravi_test", convertFileToByteArray(myFile));


            } else if (requestCode == 1) {

                //Uri filePath = data.getData();
                try {


                    ArrayList<MediaFile> mediaFiles = new ArrayList<>();


                    mediaFiles.clear();
                    mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));

                    MediaFile mediaFile = mediaFiles.get(0);
                    Log.w("path_new", String.valueOf(mediaFile.getUri()));


                    //  String path1 = mediaFile.getUri().toString(); // "file:///mnt/sdcard/FileName.mp3"
                    // File file = new File(new URI(path1));


                    String id = DocumentsContract.getDocumentId(mediaFile.getUri());
                    InputStream inputStream = getContentResolver().openInputStream(mediaFile.getUri());

                    File file = new File(getCacheDir().getAbsolutePath() + "/" + id);
                    writeFile(inputStream, file);
                    String filePath1 = file.getAbsolutePath();
                    Log.w("path_new_file", filePath1);

                    /*String path = MyFilePath.getPath(Activity_Rent_Details.this, mediaFile.getUri());
                    Log.w("path_new", String.valueOf(path));*/
                    imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(filePath1);
                    attachmentType = ".pdf";

                    uploadpdf();


                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        ArrayList<MediaFile> mediaFiles = new ArrayList<>();


                        mediaFiles.clear();
                        mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));

                        MediaFile mediaFile = mediaFiles.get(0);
                        Log.w("path_new_e", String.valueOf(mediaFile.getUri()));

                        String path = MyFilePath.getPath(getApplicationContext(), mediaFile.getUri());

                        //ivUploadFile.setImageResource(R.drawable.ic_add_image);
                        //tvUploadFile.setText("Upload " + data.getData().getLastPathSegment().substring(data.getData().getLastPathSegment().lastIndexOf("/")).replace("/", ""));
                        //Toast.makeText(getApplicationContext(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                        imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);

                        attachmentType = "pdf";
                        if (imageEncode != null && !imageEncode.equalsIgnoreCase("")) {
                            uploadpdf();
                        } else {
                            Toast.makeText(getApplicationContext(), "Can't upload wih drive", Toast.LENGTH_LONG).show();
                        }

                    } catch (IOException es) {
                        es.printStackTrace();
                    }

                }


                /*Uri filePath = data.getData();


                String path = MyFilePath.getPath(Activity_Rent_Details.this, filePath);
                try {
                    //Toast.makeText(getApplicationContext(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                    imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);
                    attachmentType = "pdf";
                    uploadpdf();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/




               /* Uri selectedPdfUri = data.getData();
                try {
                    if (String.valueOf(selectedPdfUri).indexOf("com.android.providers.downloads.documents") != -1) {
                        Utils.showToast(getApplicationContext(), "Select PDF From Your Mobile Storage", R.color.red);
                    } else {
                        if(MyFilePath.getPath(getApplicationContext(), selectedPdfUri) != null && !MyFilePath.getPath(getApplicationContext(), selectedPdfUri).isEmpty() && !MyFilePath.getPath(getApplicationContext(), selectedPdfUri).equalsIgnoreCase("")){
                            Log.w("ravi_n", MyFilePath.getPath(getApplicationContext(), selectedPdfUri));
                            String path = MyFilePath.getPath(getApplicationContext(), selectedPdfUri);
                            if (path != null) {
                                Log.w("selectedPdfUri", path);
                                imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);
                                attachmentType = ".pdf";
                                if (!Utils.isNullOrEmpty(imageEncode)) {
                                    new AlertDialog.Builder(Activity_Rent_Details.this)
                                            .setTitle("Confirmation")
                                            .setMessage("Are you sure want to upload this pdf?")
                                            .setPositiveButton("Yes, sure", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    uploadpdf();
                                                }
                                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).show();
                                }
                            } else {
                                Utils.showToast(getApplicationContext(), "Select PDF From Your Mobile Storage", R.color.red);
                            }
                        }else {
                            Utils.showToast(getApplicationContext(), "Select PDF From Your Mobile Storage", R.color.red);
                        }

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
*/

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


    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        FileInputStream input = null;
        FileOutputStream output = null;

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();

            File file = new File(context.getCacheDir(), "tmp");
            String filePath = file.getAbsolutePath();

            try {
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(filePathUri, "r");
                if (pfd == null)
                    return null;

                FileDescriptor fd = pfd.getFileDescriptor();
                input = new FileInputStream(fd);
                output = new FileOutputStream(filePath);
                int read;
                byte[] bytes = new byte[4096];
                while ((read = input.read(bytes)) != -1) {
                    output.write(bytes, 0, read);
                }

                input.close();
                output.close();
                return new File(filePath).getAbsolutePath();
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copystream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static int copystream(InputStream input, OutputStream output) throws Exception, IOException {
        final int BUFFER_SIZE = 1024 * 2;
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
        }
        return count;
    }


    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }


    public static String convertFileToByteArray(File f) {
        byte[] byteArray = null;
        try {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 11];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();

            Log.e("Byte array", ">" + byteArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }


    public void uploadpdf() {
        final ProgressDialog progressDialog = new ProgressDialog(Activity_Rent_Details.this);
        progressDialog.setTitle("Aditya Group");
        progressDialog.setMessage("Please wait..!");
        progressDialog.show();
        WebAPI webAPI = Apiutils.getClient().create(WebAPI.class);
        Log.w("ravi_nimavat", imageEncode);
        Call<UploadPdfModel> call = webAPI.Uploadpdf(property_id, customer_id, imageEncode);
        call.enqueue(new Callback<UploadPdfModel>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(Call<UploadPdfModel> call, retrofit2.Response<UploadPdfModel> response) {
                if (response.body().getResult() == 0) {

                    Utils.showToast(getApplicationContext(), response.body().getMessage());

                    //   Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    txt_agreement_status.setText("Add Agreement");
                    txt_agreement_status.setBackgroundResource(R.drawable.round_simple_button);
                    txt_agreement_status.setTextColor(getResources().getColor(R.color.black));
                } else {
                    agreement_pdf_url = String.valueOf(response.body().getImage());
                    Utils.showToast(getApplicationContext(), response.body().getMessage());
                    //Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    txt_agreement_status.setText("View Agreement");
                    txt_agreement_status.setBackgroundResource(R.drawable.round_feel_button);
                    txt_agreement_status.setTextColor(getResources().getColor(R.color.white));

                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<UploadPdfModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void openUpdateDialog() {
        LayoutInflater inflater = LayoutInflater.from(Activity_Rent_Details.this);
        View view = inflater.inflate(R.layout.dialog_update_property, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Rent_Details.this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        final EditText edt_property_name = (EditText) view.findViewById(R.id.edt_property_name);
        edt_property_name.setText(property_name);
        final EditText edt_property_address = (EditText) view.findViewById(R.id.edt_property_address);
        edt_property_address.setText(property_addess);
        final EditText edit_rent = (EditText) view.findViewById(R.id.edit_rent);
        edit_rent.setText(property_rent);
        Button btn_update_payment = (Button) view.findViewById(R.id.btn_update_payment);
        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);

        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
                if (edt_property_name.getText().toString().equals("")) {
                    edt_property_name.setError("required");
                } else if (edt_property_address.getText().toString().equals("")) {
                    edt_property_address.setError("required");
                } else if (edit_rent.getText().toString().equals("")) {
                    edit_rent.setError("required");
                } else {
                    updatePropertyAPI(edt_property_name.getText().toString(), edt_property_address.getText().toString(), edit_rent.getText().toString());
                    dialog.dismiss();
                }

            }
        });
        dialog.show();
    }

    private void updatePropertyAPI(final String prop_name, final String prop_address, final String rent) {
        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Properties").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(property_id)) {

                                DatabaseReference cineIndustryRef = databaseReference.child("Properties").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("rent", rent);
                                map.put("property_name", prop_name);
                                map.put("address", prop_address);
                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        new CToast(Activity_Rent_Details.this).simpleToast("Property update successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        property_name = prop_name;
                                        txt_property_name.setText(property_name);
                                        property_addess = prop_address;
                                        txt_address.setText(property_addess);
                                        property_rent = rent;
                                        txt_rent.setText(getResources().getString(R.string.rupee) + rent);

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
    }

    private void updatePropertyAPI1(final String prop_name, final String prop_address, final String rent) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.UPDATE_PROPERTY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("result") == 1) {
                        property_name = prop_name;
                        txt_property_name.setText(property_name);
                        property_addess = prop_address;
                        txt_address.setText(property_addess);
                        property_rent = rent;
                        txt_rent.setText(getResources().getString(R.string.rupee) + rent);
                        new CToast(Activity_Rent_Details.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    } else {
                        new CToast(Activity_Rent_Details.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("id", Aditya.ID);
                params.put("property_name", prop_name);
                params.put("address", prop_address);
                params.put("rent", rent);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(Activity_Rent_Details.this);
        queue.add(stringRequest);
    }


    public void unAssignProperty(final String customer_id, final String id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.UNASSIGN_CUSTOMER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Property Unassign", response);
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
                params.put("customer_id", customer_id);
                params.put("property_id", id);
                params.put("remarks", "");
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

    public void getRentDetail(String position) {
        showPrd();

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
                                getSupportActionBar().setTitle(Html.fromHtml(npsnapshot.child("property_name").getValue().toString()));
                                tvtitle.setText(Html.fromHtml(npsnapshot.child("property_name").getValue().toString()));
                                property_name = npsnapshot.child("property_name").getValue().toString();
                                txt_property_name.setText(property_name);
                                property_addess = npsnapshot.child("address").getValue().toString();
                                property_rent = npsnapshot.child("rent").getValue().toString();
                                txt_address.setText(property_addess);
                                txt_customer_name.setTag(npsnapshot.child("customer_id").getValue().toString());

                                rentDetailsForPDF.setAddress(property_addess);
                                rentDetailsForPDF.setPropertyName(property_name);
                                rentDetailsForPDF.setRent(property_rent);


                                Query query = databaseReference.child("Customers").orderByKey();
                                // databaseReference.keepSynced(true);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                    Log.e("mainID  ", npsnapshot1.child("id").getValue().toString());
                                                    if (npsnapshot1.child("id").getValue().toString().equals(npsnapshot.child("customer_id").getValue().toString())) {
                                                        String name = npsnapshot1.child("name").getValue().toString();
                                                        Log.e("Name  ", name);
                                                        txt_customer_name.setText(name);
                                                    }
                                                }
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

                                String[] date = npsnapshot.child("hired_since").getValue().toString().split("-");
                                String month = date[1];
                                String mm = Helper.getMonth(month);
                                txt_hired_since.setText(date[2] + " " + mm + " " + date[0]);

                                rentDetailsForPDF.setHiredSince(npsnapshot.child("hired_since").getValue().toString());

                                if (!npsnapshot.child("rent").equals("0")) {
                                    txt_rent.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(npsnapshot.child("rent").getValue().toString())));
                                    rent_amount = npsnapshot.child("rent").getValue().toString();
                                    txt_rent.setTag(npsnapshot.child("rent").getValue().toString());
                                } else {
                                    txt_rent.setText(" - ");
                                }

                                Query queryDocument = databaseReference.child("RantDocument").orderByKey();
                                // databaseReference.keepSynced(true);
                                queryDocument.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                    if (npsnapshot1.child("p_id").getValue().toString().equals(property_id)) {
                                                        Log.e("===p_id ",property_id);
                                                        Log.e("===status ",npsnapshot1.child("status").getValue().toString());
                                                        if (npsnapshot1.child("status").getValue().toString().equals("1")) {
                                                            txt_agreement_status.setText("View Agreement");
                                                            txt_agreement_status.setBackgroundResource(R.drawable.round_feel_button);
                                                            txt_agreement_status.setTextColor(getResources().getColor(R.color.white));
                                                            agreement_pdf_url = String.valueOf(npsnapshot1.child("document").getValue().toString());
                                                        } else {
                                                            txt_agreement_status.setText("Add Agreement");
                                                            txt_agreement_status.setBackgroundResource(R.drawable.round_simple_button);
                                                            txt_agreement_status.setTextColor(getResources().getColor(R.color.black));
                                                        }
                                                    }
                                                }
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

    public void getRentDetail1() {
        showPrd();
        Utils.showLog("==" + API.PROPERTY_DETAILS + "?property_id=" + Aditya.ID);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.PROPERTY_DETAILS + "?property_id=" + Aditya.ID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Log.w("Response", String.valueOf(response));
                    if (jsonObject.getInt("result") == 1) {
                        getSupportActionBar().setTitle(Html.fromHtml(jsonObject.getString("property_name")));
                        tvtitle.setText(Html.fromHtml(jsonObject.getString("property_name")));
                        property_name = jsonObject.getString("property_name");
                        txt_property_name.setText(property_name);
                        property_addess = jsonObject.getString("address");
                        property_rent = jsonObject.getString("rent");
                        txt_address.setText(property_addess);
                        txt_customer_name.setText(jsonObject.getString("customer_name"));
                        txt_customer_name.setTag(jsonObject.getString("customer_id"));

                        String[] date = jsonObject.getString("hired_since").split("-");
                        String month = date[1];
                        String mm = Helper.getMonth(month);
                        txt_hired_since.setText(date[0] + " " + mm + " " + date[2]);

                        if (!jsonObject.getString("rent").equals("0")) {
                            txt_rent.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(jsonObject.getString("rent"))));
                            rent_amount = jsonObject.getString("rent");
                            txt_rent.setTag(jsonObject.getString("rent"));
                        } else {
                            txt_rent.setText(" - ");
                        }

                        if (jsonObject.get("properties_status").equals(1)) {
                            txt_agreement_status.setText("View Agreement");
                            txt_agreement_status.setBackgroundResource(R.drawable.round_feel_button);
                            txt_agreement_status.setTextColor(getResources().getColor(R.color.white));
                            agreement_pdf_url = String.valueOf(jsonObject.get("properties_attachment"));
                        } else {
                            txt_agreement_status.setText("Add Agreement");
                            txt_agreement_status.setBackgroundResource(R.drawable.round_simple_button);
                            txt_agreement_status.setTextColor(getResources().getColor(R.color.black));
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

                            arrayListRentPayment.add(payment);
                        }

                       /* if (arrayListRentPayment.size() > 0) {
                            ll_plot.setVisibility(View.VISIBLE);
                        } else {
                            ll_plot.setVisibility(View.GONE);
                        }*/

                        //mAdapterPlot.notifyDataSetChanged();
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

    public void getPaymentRentCustomers() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.PAYMENT_HISTORY_CUSTOMERS + "property_id=" + Aditya.ID + "&customer_id=" + customer_id, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Utils.showLog("== PAYMENT_HISTORY_CUSTOMERS " + API.PAYMENT_HISTORY_CUSTOMERS + "property_id=" + Aditya.ID + "&customer_id=" + customer_id);
                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {

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

                            arrayListRentPayment.add(payment);
                        }

                       /*  if (arrayListRentPayment.size() > 0) {
                            ll_plot.setVisibility(View.VISIBLE);
                        } else {
                            ll_plot.setVisibility(View.GONE);
                        } */

                        //mAdapterPlot.notifyDataSetChanged();

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

    public void addPaymentForRent(final String date, final String time, final String amount, final String id, final String remark) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.PAYMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Plot Add", response);
                    if (jsonObject.getInt("result") == 1) {
                        getRentDetail("");
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
                params.put("customer_id", txt_customer_name.getTag().toString());
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


   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_payment, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_plot_payment:

                LayoutInflater inflater = LayoutInflater.from(mActivity);
                View view1 = inflater.inflate(R.layout.dialog_payment_add, null);

                android.support.v7.app.AlertDialog.Builder builder1 = new android.support.v7.app.AlertDialog.Builder(mActivity);
                builder1.setView(view1);
                final android.support.v7.app.AlertDialog dialog = builder1.create();

                final EditText edt_paid_amount = (EditText) view1.findViewById(R.id.edt_paid_amount);
                final EditText edt_remark = (EditText) view1.findViewById(R.id.edt_remark);
                final TextView txt_date = (TextView) view1.findViewById(R.id.txt_date);
                final TextView txt_time = (TextView) view1.findViewById(R.id.txt_time);

                TextView btn_cancel = (TextView) view1.findViewById(R.id.btn_cancel);
                Button btn_add_payment = (Button) view1.findViewById(R.id.btn_add_payment);

                edt_paid_amount.setText(txt_pending_amount.getTag().toString());

                txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
                txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

                txt_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Helper.pick_Date(mActivity, txt_date);
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
                        } else if (Integer.parseInt(edt_paid_amount.getText().toString()) > Integer.parseInt(txt_pending_amount.getTag().toString())) {
                            edt_paid_amount.setError("You enter more then pending amount");
                            edt_paid_amount.requestFocus();
                            return;
                        } else {
                            dialog.dismiss();
                            //addPaymentforPlot(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString(), Aditya.ID, txt_purchased_by.getTag().toString(), edt_remark.getText().toString());
                        }
                    }
                });

                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

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
    //    overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

     //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

        Intent intent = new Intent(mActivity, RentCustomerHistoryActivity.class);
        intent.putExtra("property_id", property_id);
        intent.putExtra("property_name", property_name);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);


    }

    private boolean pushFragment(Fragment fragment) {
        if (fragment != null) {

            Bundle bundle = new Bundle();
            bundle.putString("rent", property_rent);
            fragment.setArguments(bundle);
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


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
    }

    class Upload extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;
        private Context c;
        private Uri path;

        public Upload(Context c, Uri path) {
            this.c = c;
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(c, "Uploading", "Please Wait");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pd.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url_path = "http://192.168.43.50/projectpri/upload.php";
            HttpURLConnection conn = null;

            int maxBufferSize = 1024;
            try {
                URL url = new URL(url_path);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setChunkedStreamingMode(1024);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data");

                OutputStream outputStream = conn.getOutputStream();
                InputStream inputStream = c.getContentResolver().openInputStream(path);

                int bytesAvailable = inputStream.available();
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead;
                while ((bytesRead = inputStream.read(buffer, 0, bufferSize)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                inputStream.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    Log.i("result", line);
                }
                reader.close();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return null;
        }
    }

    //    this property_rent pass for RentPaymentListFragment.java
    public String getMyDataForFragment() {
        String rents = property_rent;
        return rents;
    }
}

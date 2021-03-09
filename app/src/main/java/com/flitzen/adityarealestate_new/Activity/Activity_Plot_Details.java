package com.flitzen.adityarealestate_new.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.flitzen.adityarealestate_new.Adapter.Adapter_Plot_Payment_List;
import com.flitzen.adityarealestate_new.Adapter.Spn_Adapter;
import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Apiutils;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.EncodeBased64Binary;
import com.flitzen.adityarealestate_new.Classes.FileUtils;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.ImageUtils;
import com.flitzen.adityarealestate_new.Classes.Permission;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Classes.WebAPI;
import com.flitzen.adityarealestate_new.Fragment.ActionBottomDialogFragment;
import com.flitzen.adityarealestate_new.Fragment.RentPaymentListFragment;
import com.flitzen.adityarealestate_new.Items.Item_Customer_List;
import com.flitzen.adityarealestate_new.Items.Item_Plot_List;
import com.flitzen.adityarealestate_new.Items.Item_Plot_Payment_List;
import com.flitzen.adityarealestate_new.Items.PlotDetailsForPDF;
import com.flitzen.adityarealestate_new.MyFilePath;
import com.flitzen.adityarealestate_new.PDFUtility;
import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.pdfcreator.views.basic.PDFTextView;
import com.flitzen.adityarealestate_new.service.PDFGETMODEL;
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
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;

public class Activity_Plot_Details extends AppCompatActivity implements PDFUtility.OnDocumentClose,ActionBottomDialogFragment.ItemClickListener {

    Activity mActivity;
    ProgressDialog prd;
    Menu menu;
    int ATTACH_ITEM_POS = 0;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    int UPLOAD_REQUEST = 001, SELECT_PICTURE = 002, CAMERA_REQUEST = 003, SELECT_AUDIO = 004, CAMERA_PERMISSION_CODE = 005, FILE_REQUEST_CODE = 101;
    ImageUtils imageUtils = new ImageUtils();
    String imageEncode = "", attachmentType = "";
    String type = "", plot_id = "", plot_no = "", plot_size = "",active_deActive="";
    TextView tvUploadFile, tvUploadFile_update;
    ImageView ivUploadFile, ivUploadFile_update, img_delete, img_delete_update;
    LinearLayout viewUploadFile;
    boolean update_file = false;
    TextView txt_plot_no, txt_purchased_by, txt_purchased_date, txt_purchased_price, txt_pending_amount, txt_paid_amount, tvPlotNo, tvPlotSize, tvAddNewCustomer;
    ImageView ivEditPlotNo;
    LinearLayout ivEditPlotSize;
    ArrayList<Item_Plot_Payment_List> arrayListPlotPayment = new ArrayList<>();
    RecyclerView rec_purchased_plot_list;
    Adapter_Plot_Payment_List mAdapterPlot;

    View ll_plot, view_change_customer, view_remove_customer;

    String cust_id = "", file_url = "";
    List<Item_Customer_List> itemListCustomer = new ArrayList<>();
    List<Item_Customer_List> itemListCustomerTemp = new ArrayList<>();
    RelativeLayout tvViewPaymentPDF;
    MenuItem item;
    private Uri capturedImageURI = Uri.parse("");
    String purchase_price = "";
    String mime = "";
    PlotDetailsForPDF plotDetailsForPDF = new PlotDetailsForPDF();
    String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_details);

        getSupportActionBar().setTitle(Html.fromHtml("Plot No : "));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Plot_Details.this;

        active_deActive = getIntent().getStringExtra("ACTIVE_DEACTIVE");

        //txt_plot_no = (TextView) findViewById(R.id.txt_plot_no);
        txt_purchased_by = (TextView) findViewById(R.id.txt_purchased_by);
        txt_purchased_date = (TextView) findViewById(R.id.txt_purchased_date);
        txt_purchased_price = (TextView) findViewById(R.id.txt_purchased_price);
        txt_pending_amount = (TextView) findViewById(R.id.txt_pending_amount);
        txt_paid_amount = (TextView) findViewById(R.id.txt_paid_amount);
        tvPlotNo = (TextView) findViewById(R.id.tvPlotNo);
        tvPlotSize = (TextView) findViewById(R.id.tvPlotSize);
        ivEditPlotNo = (ImageView) findViewById(R.id.ivEditPlotNo);
        ivEditPlotSize = (LinearLayout) findViewById(R.id.ivEditPlotSize);
        tvViewPaymentPDF = findViewById(R.id.tvViewPaymentPDF);
        tvAddNewCustomer = findViewById(R.id.tvAddNewCustomer);

        tvViewPaymentPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent=new Intent(Activity_Plot_Details.this,PdfCreatorPlotActivity.class);
                intent.putExtra("SiteDetails",plotDetailsForPDF);
                intent.putExtra("paymentList",arrayListPlotPayment);
                startActivity(intent);*/

                int finalTotalAmount=0;
                for (int i = 0; i < arrayListPlotPayment.size(); i++) {
                    if(arrayListPlotPayment.get(i).getAmount()!=null && !(arrayListPlotPayment.get(i).getAmount().equals(""))){
                        finalTotalAmount=finalTotalAmount+Integer.parseInt(arrayListPlotPayment.get(i).getAmount());
                    }
                }

                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                path = Environment.getExternalStorageDirectory().toString() + "/" + ts + "_payment_list.pdf";
                System.out.println("========path  " + path);
                try {
                    PDFUtility.createPdf(v.getContext(), Activity_Plot_Details.this, getSampleData(), path, true,plotDetailsForPDF,finalTotalAmount);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Error", "Error Creating Pdf");
                    Toast.makeText(v.getContext(), "Error Creating Pdf", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ivEditPlotNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlotUpdateDialog();
            }
        });

        ivEditPlotSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showPlotUpdateDialog();
                addPayment();
            }
        });

        ll_plot = findViewById(R.id.ll_plot);
        view_change_customer = findViewById(R.id.view_change_customer);
        view_remove_customer = findViewById(R.id.view_remove_customer);

        view_change_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_plot_change_customer, null);

                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);

                final TextView txt_plot_no = (TextView) promptsView.findViewById(R.id.txt_plot_no);
                final TextView txt_old_cusomer = (TextView) promptsView.findViewById(R.id.txt_old_cusomer);
                final TextView spn_customer = (TextView) promptsView.findViewById(R.id.spn_customer);

                TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                Button btn_change = (Button) promptsView.findViewById(R.id.btn_change_customer);

                txt_plot_no.setText(plot_no);
                txt_old_cusomer.setText(txt_purchased_by.getText().toString());
                txt_old_cusomer.setTag(txt_purchased_by.getTag().toString());

                spn_customer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getCustomerList(spn_customer, txt_purchased_by.getTag().toString());

                    }
                });

                try {
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


                btn_change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (spn_customer.getTag().toString().trim().equals("")) {
                            new CToast(mActivity).simpleToast("Select Customer", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            return;
                        } else {
                            alertDialog.dismiss();
                            changeCustomer(txt_old_cusomer.getTag().toString(), spn_customer.getTag().toString(), Aditya.ID);
                        }
                    }
                });

                alertDialog.show();
            }
        });

        view_remove_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                        .setTitle("Remove Customer")
                        .setMessage("Are you sure you want to remove this customer")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                removeCustomer(Aditya.ID, txt_purchased_by.getTag().toString());
                            }
                        }).setNegativeButton("NO", null).show();
            }
        });

        rec_purchased_plot_list = (RecyclerView) findViewById(R.id.rec_purchased_plot_list);
        rec_purchased_plot_list.setLayoutManager(new LinearLayoutManager(mActivity));
        rec_purchased_plot_list.setHasFixedSize(true);
        rec_purchased_plot_list.setNestedScrollingEnabled(false);
        mAdapterPlot = new Adapter_Plot_Payment_List(mActivity, arrayListPlotPayment, true);
        rec_purchased_plot_list.setAdapter(mAdapterPlot);


        mAdapterPlot.setOnItemListener(new Adapter_Plot_Payment_List.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //Toast.makeText(getApplicationContext(),arrayListPlotPayment.get(position).getAmount(),Toast.LENGTH_LONG).show();
                openUpdateDialog(position);
            }
        });


        getPlotDetail();
    }

    @Override
    public void onPDFDocumentClose(File file) {
       // Toast.makeText(this, "Sample Pdf Created", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Activity_Plot_Details.this, ViewPdfForAll.class);
        //intent.putExtra("SiteDetails", plotDetailsForPDF);
        //intent.putExtra("paymentList", arrayListPlotPayment);
        intent.putExtra("path",path);
        startActivity(intent);
    }

    private List<String[]> getSampleData() {

        List<String[]> temp = new ArrayList<>();
        int finalTotalAmount=0;
        for (int i = 0; i < arrayListPlotPayment.size(); i++) {

            String data1="";
            String data2="";
            String data3="";
            String data4="";

            arrayListPlotPayment.get(i).getPayment_time();

            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat inputT = new SimpleDateFormat("hh:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("dd MMMM yyyy");
            SimpleDateFormat outputT = new SimpleDateFormat("hh:mm a");
            try {
                Date oneWayTripDate;
                Date oneWayTripDateT;
                oneWayTripDate = input.parse(arrayListPlotPayment.get(i).getPayment_date());  // parse input
                oneWayTripDateT = inputT.parse(arrayListPlotPayment.get(i).getPayment_time());  // parse input
                data1=output.format(oneWayTripDate) + " " + outputT.format(oneWayTripDateT);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(arrayListPlotPayment.get(i).getCustomer_name()!=null){
                data2=arrayListPlotPayment.get(i).getCustomer_name();
            }

            data3=arrayListPlotPayment.get(i).getRemarks();
            data4=Helper.getFormatPrice(Integer.parseInt(arrayListPlotPayment.get(i).getAmount()));

            temp.add(new String[] {data1,data2,data3,data4});
        }
        return temp;

       /* int count = 20;
        if(!TextUtils.isEmpty("20"))
        {
            count = Integer.parseInt("20");
        }

        List<String[]> temp = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            temp.add(new String[] {"C1-R"+ (i+1),"C2-R"+ (i+1)});
        }
        return  temp;*/

    }


    private void addPayment() {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view1 = inflater.inflate(R.layout.dialog_payment_add, null);

        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(mActivity);
        builder1.setView(view1);
        final androidx.appcompat.app.AlertDialog dialog = builder1.create();

        final EditText edt_paid_amount = (EditText) view1.findViewById(R.id.edt_paid_amount);
        final EditText edt_remark = (EditText) view1.findViewById(R.id.edt_remark);
        final TextView txt_date = (TextView) view1.findViewById(R.id.txt_date);
        final TextView txt_time = (TextView) view1.findViewById(R.id.txt_time);
        final LinearLayout viewUploadFile = (LinearLayout) view1.findViewById(R.id.viewUploadFile);
        TextView txt_next_date = (TextView) view1.findViewById(R.id.txt_next_date);

        ivUploadFile = (ImageView) view1.findViewById(R.id.ivUploadFile);
        ivUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAttachImageOption();
            }
        });
        tvUploadFile = (TextView) view1.findViewById(R.id.tvUploadFile);

        tvUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAttachImageOption();
            }
        });
        viewUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAttachImageOption();
            }
        });

        TextView btn_cancel = (TextView) view1.findViewById(R.id.btn_cancel);
        Button btn_add_payment = (Button) view1.findViewById(R.id.btn_add_payment);
        Button btn_add_share_payment = (Button) view1.findViewById(R.id.btn_add_share_payment);

        //edt_paid_amount.setText(txt_pending_amount.getTag().toString());

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

       /* Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);*/

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        txt_time.setText(currentTime);

        img_delete = (ImageView) view1.findViewById(R.id.img_delete);

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
                } else if (txt_next_date.getText().toString().equals("")) {
                    new CToast(mActivity).simpleToast("Select Next Payment Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else {
                    dialog.dismiss();
                    addPaymentforPlot(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString()
                            , Aditya.ID, txt_purchased_by.getTag().toString(), edt_remark.getText().toString(), txt_next_date.getText().toString(),0);
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
                } else if (Integer.parseInt(edt_paid_amount.getText().toString()) > Integer.parseInt(txt_pending_amount.getTag().toString())) {
                    edt_paid_amount.setError("You enter more then pending amount");
                    edt_paid_amount.requestFocus();
                    return;
                } else if (txt_next_date.getText().toString().equals("")) {
                    new CToast(mActivity).simpleToast("Select Next Payment Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else {
                    dialog.dismiss();
                    addPaymentforPlot(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString()
                            , Aditya.ID, txt_purchased_by.getTag().toString(), edt_remark.getText().toString(), txt_next_date.getText().toString(),1);
                }
            }
        });

        dialog.show();
    }


    private void openUpdateDialog(final int position) {
        LayoutInflater inflater = LayoutInflater.from(Activity_Plot_Details.this);
        View view = inflater.inflate(R.layout.dialog_update_payment, null);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Activity_Plot_Details.this);
        builder.setView(view);
        final androidx.appcompat.app.AlertDialog dialog_main = builder.create();
        dialog_main.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final EditText edt_paid_amount = (EditText) view.findViewById(R.id.edt_paid_amount);
        edt_paid_amount.setText(arrayListPlotPayment.get(position).getAmount());
        final EditText edt_remark = (EditText) view.findViewById(R.id.edt_remark);
        edt_remark.setText(arrayListPlotPayment.get(position).getRemarks());
        Button btn_update_payment = (Button) view.findViewById(R.id.btn_update_payment);
        Button btn_delete_payment = (Button) view.findViewById(R.id.btn_delete_payment);

        tvUploadFile_update = (TextView) view.findViewById(R.id.tvUploadFile);
        ivUploadFile_update = (ImageView) view.findViewById(R.id.ivUploadFile);
        img_delete_update = (ImageView) view.findViewById(R.id.img_delete_update);
        viewUploadFile = (LinearLayout) view.findViewById(R.id.viewUploadFile);

        tvUploadFile_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_file = true;
                showAttachImageOption();
            }
        });

        img_delete_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageEncode = "";
                capturedImageURI = Uri.parse("");
                mime = "";

                int newHeight = 50; // New height in pixels
                int newWidth = 50; //

                ivUploadFile_update.getLayoutParams().height = newHeight;

                // Apply the new width for ImageView programmatically
                ivUploadFile_update.getLayoutParams().width = newWidth;


                ivUploadFile_update.requestLayout();

                ivUploadFile_update.setImageResource(R.drawable.ic_add_image);
                ivUploadFile_update.setVisibility(View.VISIBLE);
                tvUploadFile_update.setVisibility(View.VISIBLE);
                tvUploadFile_update.setText("Upload File");
                img_delete_update.setVisibility(View.GONE);
            }
        });

        ivUploadFile_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_file = true;
                showAttachImageOption();
            }
        });

        viewUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_file = true;
                showAttachImageOption();
            }
        });

        ImageView btn_cancel = (ImageView) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_main.dismiss();
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

                    dialog_main.dismiss();
                }

            }
        });

        btn_delete_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openDeleteDialog(position);

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Activity_Plot_Details.this);
                builder.setMessage("Delete this Payment ?");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteEntryAPI(position);


                                dialog.dismiss();
                                dialog_main.dismiss();

                            }
                        });

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });

                androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        dialog_main.show();
    }


    private void updatePaymentAPI(final int position, final String amount, final String remarks) {
        showPrd();

        if (String.valueOf(capturedImageURI).equals("")) {
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

                                if (dataSnapshot.child("id").getValue().toString().equals(arrayListPlotPayment.get(position).getId())) {

                                    DatabaseReference cineIndustryRef = databaseReference.child("Payments").child(dataSnapshot.getKey());
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("amount", amount);
                                    map.put("remarks", remarks);

                                    Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            hidePrd();
                                            new CToast(Activity_Plot_Details.this).simpleToast("Payment updated successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                            getPlotDetail();
                                            capturedImageURI = Uri.parse("");
                                            mime = "";
                                            imageEncode = "";
                                            attachmentType = "";
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

                                                        if (dataSnapshot.child("id").getValue().toString().equals(arrayListPlotPayment.get(position).getId())) {

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
                                                                    new CToast(Activity_Plot_Details.this).simpleToast("Payment updated successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                                    getPlotDetail();
                                                                    capturedImageURI = Uri.parse("");
                                                                    mime = "";
                                                                    imageEncode = "";
                                                                    attachmentType = "";
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

                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            hidePrd();
                            Toast.makeText(Activity_Plot_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
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
        Log.w("url", API.UPDATE_PAYMENT);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.UPDATE_PAYMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {
                        arrayListPlotPayment.get(position).setAmount(amount);
                        arrayListPlotPayment.get(position).setRemarks(remarks);
                        mAdapterPlot.notifyDataSetChanged();
                        new CToast(Activity_Plot_Details.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        getPlotDetail();
                        imageEncode = "";
                        attachmentType = "";
                    } else {
                        new CToast(Activity_Plot_Details.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("id", arrayListPlotPayment.get(position).getId());
                params.put("amount", amount);
                params.put("remarks", remarks);
                params.put("payment_attachment", imageEncode);
                params.put("attachment_type", attachmentType);
                Log.w("params-ravi", String.valueOf(params));
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(Activity_Plot_Details.this);
        queue.add(stringRequest);
    }


    private void openDeleteDialog(final int position) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Activity_Plot_Details.this);
        builder.setMessage("Delete this Payment ?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteEntryAPI(position);
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteEntryAPI(final int position) {

        if (arrayListPlotPayment.get(position).getPayment_attachment().equals("")) {
            showPrd();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Payments").child(arrayListPlotPayment.get(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null) {
                        databaseReference.child("Payments").child(arrayListPlotPayment.get(position).getKey()).removeValue().addOnCompleteListener(Activity_Plot_Details.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hidePrd();
                                if (task.isSuccessful()) {
                                    mAdapterPlot.notifyDataSetChanged();
                                    new CToast(Activity_Plot_Details.this).simpleToast("Payment Delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                    getPlotDetail();
                                } else {
                                    new CToast(Activity_Plot_Details.this).simpleToast(task.getException().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                }

                            }
                        }).addOnFailureListener(Activity_Plot_Details.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hidePrd();
                                new CToast(Activity_Plot_Details.this).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }
                        });
                    } else {
                        hidePrd();
                        new CToast(Activity_Plot_Details.this).simpleToast("Payment not exist", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    hidePrd();
                    new CToast(Activity_Plot_Details.this).simpleToast(error.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }
            });
        } else {
            showPrd();
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(arrayListPlotPayment.get(position).getPayment_attachment());

            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Payments").child(arrayListPlotPayment.get(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getValue() != null) {
                                databaseReference.child("Payments").child(arrayListPlotPayment.get(position).getKey()).removeValue().addOnCompleteListener(Activity_Plot_Details.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        hidePrd();
                                        if (task.isSuccessful()) {
                                            mAdapterPlot.notifyDataSetChanged();
                                            new CToast(Activity_Plot_Details.this).simpleToast("Payment Delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                            getPlotDetail();
                                        } else {
                                            new CToast(Activity_Plot_Details.this).simpleToast(task.getException().toString(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                        }

                                    }
                                }).addOnFailureListener(Activity_Plot_Details.this, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hidePrd();
                                        new CToast(Activity_Plot_Details.this).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    }
                                });
                            } else {
                                hidePrd();
                                new CToast(Activity_Plot_Details.this).simpleToast("Payment not exist", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            hidePrd();
                            new CToast(Activity_Plot_Details.this).simpleToast(error.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                        arrayListPlotPayment.remove(position);
                        mAdapterPlot.notifyDataSetChanged();
                        new CToast(Activity_Plot_Details.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        getPlotDetail();
                    } else {
                        new CToast(Activity_Plot_Details.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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

                Utils.showLog("===" + arrayListPlotPayment.get(position).getId());
                Map<String, String> params = new HashMap<>();
                params.put("id", arrayListPlotPayment.get(position).getId());

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(Activity_Plot_Details.this);
        queue.add(stringRequest);
    }

    private void showPlotUpdateDialog() {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_edit_plot_detail, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);

        final EditText edt_plot_no = (EditText) promptsView.findViewById(R.id.edt_plot_no);
        final EditText edt_plot_size = (EditText) promptsView.findViewById(R.id.edt_plot_size);

        TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
        Button btn_update_plot = (Button) promptsView.findViewById(R.id.btn_update_plot);

        edt_plot_no.setText(plot_no);
        edt_plot_size.setText(plot_size);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btn_update_plot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_plot_no.getText().toString().equals("")) {
                    edt_plot_no.setError("require");
                } else if (edt_plot_size.getText().toString().equals("")) {
                    edt_plot_size.setError("require");
                } else {


                    updatePlotDetailAPI(edt_plot_no.getText().toString(), edt_plot_size.getText().toString());
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.show();
    }

    private void updatePlotDetailAPI(final String plot_no, final String plot_size) {
        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Plots").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(plot_id)) {

                                DatabaseReference cineIndustryRef = databaseReference.child("Plots").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("plot_no", plot_no);
                                map.put("size", plot_size);
                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        new CToast(Activity_Plot_Details.this).simpleToast("Plot update successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        getPlotDetail();

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

    private void updatePlotDetailAPI1(final String plot_no, final String plot_size) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.EDIT_PLOT_DETAIL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Plot Add", response);

                    if (jsonObject.getInt("result") == 1) {
                        getPlotDetail();
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
                params.put("plot_id", plot_id);
                params.put("plot_no", plot_no);
                params.put("size", plot_size);
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

    public void getPlotDetail() {
        showPrd();

        purchase_price = "";
        arrayListPlotPayment.clear();
        plotDetailsForPDF = new PlotDetailsForPDF();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Plots").orderByKey();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if (npsnapshot.child("id").getValue().toString().equals(Aditya.ID)) {

                                getSupportActionBar().setTitle(Html.fromHtml("Plot No : " + npsnapshot.child("plot_no").getValue().toString()));
                                plot_id = npsnapshot.child("id").getValue().toString();
                                String site_id = npsnapshot.child("site_id").getValue().toString();

                                Query querySite = databaseReference.child("Sites").orderByKey();
                                querySite.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot npsnapshotSite : dataSnapshot.getChildren()) {
                                                    if (npsnapshotSite.child("id").getValue().toString().equals(site_id)) {
                                                        String siteNname = npsnapshotSite.child("site_name").getValue().toString();
                                                        plotDetailsForPDF.setSiteName(siteNname);
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            hidePrd();
                                            Log.e("Ex getPlotDetail  ", e.toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        hidePrd();
                                        Log.e("error ", error.getMessage());
                                    }
                                });

                                plot_no = npsnapshot.child("plot_no").getValue().toString();
                                plotDetailsForPDF.setPlotNo(plot_no);
                                tvPlotNo.setText(plot_no);
                                plot_size = npsnapshot.child("size").getValue().toString();
                                plotDetailsForPDF.setSize(plot_size);
                                tvPlotSize.setText(plot_size);
                                txt_purchased_by.setTag(npsnapshot.child("customer_id").getValue().toString());
                                String customerID = npsnapshot.child("customer_id").getValue().toString();

                                Query query = databaseReference.child("Customers").orderByKey();
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                    if (npsnapshot1.child("id").getValue().toString().equals(customerID)) {
                                                        String name = npsnapshot1.child("name").getValue().toString();
                                                        txt_purchased_by.setText(name);
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            hidePrd();
                                            Log.e("Ex getPlotDetail  ", e.toString());
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        hidePrd();
                                        Log.e("error ", error.getMessage());
                                    }
                                });

                                if (!npsnapshot.child("date_of_purchase").getValue().toString().equals("")) {
                                    String[] date = npsnapshot.child("date_of_purchase").getValue().toString().split("-");
                                    String month = date[1];
                                    String mm = Helper.getMonth(month);
                                    txt_purchased_date.setText(date[2] + " " + mm + " " + date[0]);
                                    plotDetailsForPDF.setDOP(date[0] + " " + mm + " " + date[2]);
                                }
                                purchase_price = npsnapshot.child("purchase_price").getValue().toString();
                                plotDetailsForPDF.setPurchasePrice(purchase_price);
                                txt_purchased_price.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(npsnapshot.child("purchase_price").getValue().toString())));


                            }
                        }
                    }
                } catch (Exception e) {
                    hidePrd();
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


        //  arrayListPlotPayment.clear();
        Query queryPayment = databaseReference.child("Payments").orderByKey();
        // databaseReference.keepSynced(true);
        queryPayment.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                try {
                    if (dataSnapshot1.exists()) {
                        int payeeAmount = 0;
                        for (DataSnapshot npsnapshot2 : dataSnapshot1.getChildren()) {
                            if (npsnapshot2.child("plot_id").getValue().toString().equals(Aditya.ID)) {
                                if(active_deActive.equalsIgnoreCase("DEACTIVE")){
                                    if (npsnapshot2.child("payment_status").getValue().toString().equals("1")) {

                                        Item_Plot_Payment_List payment = new Item_Plot_Payment_List();
                                        payment.setId(npsnapshot2.child("id").getValue().toString());
                                        payment.setKey(npsnapshot2.getKey());
                                        payment.setAmount(npsnapshot2.child("amount").getValue().toString());
                                        String amount = npsnapshot2.child("amount").getValue().toString();
                                        payment.setRemarks(npsnapshot2.child("remarks").getValue().toString());
                                        payment.setCustomer_id(npsnapshot2.child("customer_id").getValue().toString());

                                        try {
                                            DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                            Date d = f.parse(npsnapshot2.child("payment_date").getValue().toString());
                                            DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                            DateFormat time = new SimpleDateFormat("hh:mm:ss");
                                            System.out.println("=====Date: " + date.format(d));
                                            System.out.println("======Time: " + time.format(d));
                                            payment.setPayment_date(date.format(d));
                                            payment.setPayment_time(time.format(d));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        payment.setPayment_attachment(npsnapshot2.child("payment_attachment").getValue().toString());

                                        if (npsnapshot2.hasChild("file_type")) {
                                            payment.setFileType(npsnapshot2.child("file_type").getValue().toString());
                                        } else {
                                            payment.setFileType("");
                                        }

                                        payment.setNext_payment_date(npsnapshot2.child("next_payment_date").getValue().toString());

                                        Query queryCustomer = databaseReference.child("Customers").orderByKey();
                                        queryCustomer.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                try {
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                            if (npsnapshot1.child("id").getValue().toString().equals(npsnapshot2.child("customer_id").getValue().toString())) {
                                                                String name = npsnapshot1.child("name").getValue().toString();
                                                                payment.setCustomer_name(npsnapshot1.child("name").getValue().toString());
                                                            }
                                                        }
                                                    }
                                                    Collections.sort(arrayListPlotPayment, new Comparator<Item_Plot_Payment_List>() {
                                                        @Override
                                                        public int compare(Item_Plot_Payment_List o1, Item_Plot_Payment_List o2) {
                                                            return o1.getPayment_date().compareTo(o2.getPayment_date());
                                                        }
                                                    }.reversed());

                                                    mAdapterPlot.notifyDataSetChanged();

                                                } catch (Exception e) {
                                                    hidePrd();
                                                    Log.e("Ex  mid ", e.toString());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                hidePrd();
                                                Log.e("error ", error.getMessage());
                                            }
                                        });

                                        arrayListPlotPayment.add(payment);

                                        payeeAmount = payeeAmount + Integer.parseInt(amount);
                                    }
                                }else if(active_deActive.equalsIgnoreCase("ACTIVE")){
                                    if (npsnapshot2.child("payment_status").getValue().toString().equals("0")) {

                                        Item_Plot_Payment_List payment = new Item_Plot_Payment_List();
                                        payment.setId(npsnapshot2.child("id").getValue().toString());
                                        payment.setKey(npsnapshot2.getKey());
                                        payment.setAmount(npsnapshot2.child("amount").getValue().toString());
                                        String amount = npsnapshot2.child("amount").getValue().toString();
                                        payment.setRemarks(npsnapshot2.child("remarks").getValue().toString());
                                        payment.setCustomer_id(npsnapshot2.child("customer_id").getValue().toString());

                                        try {
                                            DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                            Date d = f.parse(npsnapshot2.child("payment_date").getValue().toString());
                                            DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                            DateFormat time = new SimpleDateFormat("hh:mm:ss");
                                            System.out.println("=====Date: " + date.format(d));
                                            System.out.println("======Time: " + time.format(d));
                                            payment.setPayment_date(date.format(d));
                                            payment.setPayment_time(time.format(d));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        payment.setPayment_attachment(npsnapshot2.child("payment_attachment").getValue().toString());

                                        if (npsnapshot2.hasChild("file_type")) {
                                            payment.setFileType(npsnapshot2.child("file_type").getValue().toString());
                                        } else {
                                            payment.setFileType("");
                                        }

                                        payment.setNext_payment_date(npsnapshot2.child("next_payment_date").getValue().toString());

                                        Query queryCustomer = databaseReference.child("Customers").orderByKey();
                                        queryCustomer.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                try {
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot npsnapshot1 : dataSnapshot.getChildren()) {
                                                            if (npsnapshot1.child("id").getValue().toString().equals(npsnapshot2.child("customer_id").getValue().toString())) {
                                                                String name = npsnapshot1.child("name").getValue().toString();
                                                                payment.setCustomer_name(npsnapshot1.child("name").getValue().toString());
                                                            }
                                                        }
                                                    }
                                                    Collections.sort(arrayListPlotPayment, new Comparator<Item_Plot_Payment_List>() {
                                                        @Override
                                                        public int compare(Item_Plot_Payment_List o1, Item_Plot_Payment_List o2) {
                                                            return o1.getPayment_date().compareTo(o2.getPayment_date());
                                                        }
                                                    }.reversed());

                                                    mAdapterPlot.notifyDataSetChanged();

                                                } catch (Exception e) {
                                                    hidePrd();
                                                    Log.e("Ex  mid ", e.toString());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                hidePrd();
                                                Log.e("error ", error.getMessage());
                                            }
                                        });

                                        arrayListPlotPayment.add(payment);

                                        payeeAmount = payeeAmount + Integer.parseInt(amount);
                                    }
                                }
                            }
                        }

                        if (arrayListPlotPayment.size() > 0) {
                            ll_plot.setVisibility(View.VISIBLE);
                        } else {
                            ll_plot.setVisibility(View.GONE);
                        }

                        Collections.sort(arrayListPlotPayment, new Comparator<Item_Plot_Payment_List>() {
                            @Override
                            public int compare(Item_Plot_Payment_List o1, Item_Plot_Payment_List o2) {
                                return o1.getPayment_date().compareTo(o2.getPayment_date());
                            }
                        }.reversed());
                        mAdapterPlot.notifyDataSetChanged();

                        int purchasePrice = Integer.parseInt(purchase_price);
                        int pendingAmount = purchasePrice - payeeAmount;

                        plotDetailsForPDF.setPendingAmount(String.valueOf(pendingAmount));


                        if (!(String.valueOf(pendingAmount)).equals("0")) {
                            txt_pending_amount.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(pendingAmount));
                            txt_pending_amount.setTag(String.valueOf(pendingAmount));
                            ivEditPlotSize.setVisibility(View.VISIBLE);
                        } else {
                            txt_pending_amount.setText(" - ");
                            ivEditPlotSize.setVisibility(View.GONE);
                        }

                        if (!(String.valueOf(payeeAmount)).equals("0")) {
                            txt_paid_amount.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(payeeAmount));
                            txt_paid_amount.setTag(String.valueOf(payeeAmount));
                        } else {
                            txt_paid_amount.setText(" - ");
                        }
                    }
                } catch (Exception e) {
                    hidePrd();
                    Log.e("Ex Payment  ", e.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hidePrd();
                Log.e("error ", error.getMessage());
            }
        });
    }

    public void getPlotDetail1() {
        showPrd();
        Utils.showLog("===" + API.PLOT_DETAILS + "?plot_id=" + Aditya.ID);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.PLOT_DETAILS + "?plot_id=" + Aditya.ID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Response Customer List", response);


                    if (jsonObject.getInt("result") == 1) {

                        getSupportActionBar().setTitle(Html.fromHtml("Plot No : " + jsonObject.getString("plot_no")));
                        plot_id = jsonObject.getString("id");
                        plot_no = jsonObject.getString("plot_no");
                        tvPlotNo.setText(plot_no);
                        plot_size = jsonObject.getString("size");
                        tvPlotSize.setText(plot_size);
                        txt_purchased_by.setTag(jsonObject.getString("customer_id"));
                        txt_purchased_by.setText(jsonObject.getString("customer_name"));

                        if (!jsonObject.getString("date_of_purchase").equals("")) {
                            String[] date = jsonObject.getString("date_of_purchase").split("-");
                            String month = date[1];
                            String mm = Helper.getMonth(month);
                            txt_purchased_date.setText(date[0] + " " + mm + " " + date[2]);
                        }

                        txt_purchased_price.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(jsonObject.getString("purchase_price"))));

                        if (!jsonObject.getString("pending_amount").equals("0")) {
                            txt_pending_amount.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(jsonObject.getString("pending_amount"))));
                            txt_pending_amount.setTag(jsonObject.getString("pending_amount"));
                            ivEditPlotSize.setVisibility(View.VISIBLE);
                        } else {
                            txt_pending_amount.setText(" - ");
                            ivEditPlotSize.setVisibility(View.GONE);
                        }

                        if (!jsonObject.getString("payee_amount").equals("0")) {
                            txt_paid_amount.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(jsonObject.getString("payee_amount"))));
                            txt_paid_amount.setTag(jsonObject.getString("payee_amount"));
                        } else {
                            txt_paid_amount.setText(" - ");
                        }

                        arrayListPlotPayment.clear();

                        JSONArray jsonArrayPayment = jsonObject.getJSONArray("payments_list");
                        for (int j = 0; j < jsonArrayPayment.length(); j++) {
                            JSONObject object2 = jsonArrayPayment.getJSONObject(j);

                            Item_Plot_Payment_List payment = new Item_Plot_Payment_List();
                            payment.setId(object2.getString("id"));
                            payment.setAmount(object2.getString("amount"));
                            payment.setRemarks(object2.getString("remarks"));
                            payment.setCustomer_id(object2.getString("customer_id"));
                            payment.setCustomer_name(object2.getString("customer_name"));
                            payment.setPayment_date(object2.getString("payment_date"));
                            payment.setPayment_time(object2.getString("payment_time"));
                            payment.setPayment_attachment(object2.getString("payment_attachment"));
                            payment.setNext_payment_date(object2.getString("next_payment_date"));

                            arrayListPlotPayment.add(payment);
                        }

                        if (arrayListPlotPayment.size() > 0) {
                            ll_plot.setVisibility(View.VISIBLE);
                        } else {
                            ll_plot.setVisibility(View.GONE);
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


    public void getCustomerList(final TextView view, final String id) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Customers").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemListCustomer.clear();
                itemListCustomerTemp.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if (npsnapshot.child("status").getValue().toString().equals("0")) {
                                // for (int i = 0; i < jsonArray.length(); i++) {

                                Item_Customer_List item = new Item_Customer_List();
                                item.setId(npsnapshot.child("id").getValue().toString());
                                item.setName(npsnapshot.child("name").getValue().toString());
                                item.setCity(npsnapshot.child("city").getValue().toString());
                                item.setContact_no(npsnapshot.child("contact_no").getValue().toString());
                                item.setAnother_no(npsnapshot.child("contact_no1").getValue().toString());
                                item.setEmail(npsnapshot.child("email").getValue().toString());
                                item.setAddress(npsnapshot.child("address").getValue().toString());

                                itemListCustomer.add(item);
                                itemListCustomerTemp.add(item);
                                // }
                            }
                        }
                        if (!id.equals("")) {

                            itemListCustomer.clear();

                            for (int i = 0; i < itemListCustomerTemp.size(); i++) {
                                if (!id.equals(itemListCustomerTemp.get(i).getId())) {
                                    itemListCustomer.add(itemListCustomerTemp.get(i));
                                }
                            }

                            customerDialog(view);
                            Log.w("ravi_cus", "customerDialog 1");
                        } else {
                            customerDialog(view);
                            Log.w("ravi_cus", "customerDialog 2");
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
            }
        });
    }

    public void getCustomerList1(final TextView view, final String id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.CUSTOMER + "?type=List", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                hidePrd();
                try {
                    itemListCustomer.clear();
                    itemListCustomerTemp.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Customer List", response);

                    if (jsonObject.getInt("result") == 1) {
                        JSONArray jsonArray = jsonObject.getJSONArray("customers");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Customer_List item = new Item_Customer_List();
                            item.setId(object.getString("id"));
                            item.setName(object.getString("name"));
                            item.setCity(object.getString("city"));
                            item.setContact_no(object.getString("contact_no"));
                            item.setEmail(object.getString("email"));
                            item.setAddress(object.getString("address"));

                            itemListCustomer.add(item);
                            itemListCustomerTemp.add(item);
                        }

                        if (!id.equals("")) {

                            itemListCustomer.clear();

                            for (int i = 0; i < itemListCustomerTemp.size(); i++) {
                                if (!id.equals(itemListCustomerTemp.get(i).getId())) {
                                    itemListCustomer.add(itemListCustomerTemp.get(i));
                                }
                            }

                            customerDialog(view);

                        } else {
                            customerDialog(view);
                        }

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
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }

    public void customerDialog(final TextView textView) {
        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dialog_spinner, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText edtSearchLocation = (EditText) promptsView.findViewById(R.id.edt_spn_search);
        final ListView list_location = (ListView) promptsView.findViewById(R.id.list_spn);


        final ArrayList<String> arrayListTemp = new ArrayList<>();
        final ArrayList<String> arrayListId = new ArrayList<>();

        edtSearchLocation.setHint("Search Customer");
        for (int i = 0; i < itemListCustomer.size(); i++) {
            arrayListTemp.add(itemListCustomer.get(i).getName());
            arrayListId.add(itemListCustomer.get(i).getId());
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

                    for (int j = 0; j < itemListCustomer.size(); j++) {
                        String word = edtSearchLocation.getText().toString().toLowerCase();
                        if (itemListCustomer.get(j).getName().toLowerCase().contains(word)) {
                            arrayListTemp.add(itemListCustomer.get(j).getName());
                            arrayListId.add(itemListCustomer.get(j).getId());
                        }
                    }
                    list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));
                } else {
                    arrayListTemp.clear();
                    arrayListId.clear();
                    for (int i = 0; i < itemListCustomer.size(); i++) {
                        arrayListTemp.add(itemListCustomer.get(i).getName());
                        arrayListId.add(itemListCustomer.get(i).getId());
                    }
                    list_location.setAdapter(new Spn_Adapter(mActivity, arrayListTemp));
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

     /*   list_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                OpenAddNewCustomer(textView);
                alertDialog.dismiss();
            }

        });
        */


        list_location.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // OpenAddNewCustomer(textView);
                textView.setText(arrayListTemp.get(position));
                textView.setTag(arrayListId.get(position));
                cust_id = arrayListId.get(position);
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

    /*private void OpenAddNewCustomer() {

        LayoutInflater localView = LayoutInflater.from(mActivity);
        View promptsView = localView.inflate(R.layout.dailog_add_customer, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setCanceledOnTouchOutside(false);

        final EditText edt_add_company_name = (EditText) promptsView.findViewById(R.id.edt_add_company_name);
        final EditText edt_add_mobile = (EditText) promptsView.findViewById(R.id.edt_add_mobile);
        final EditText edt_add_email = (EditText) promptsView.findViewById(R.id.edt_add_email);
        final EditText edt_add_address = (EditText) promptsView.findViewById(R.id.edt_add_address);


        Button btn_add_invoice_cancel = (Button) promptsView.findViewById(R.id.btn_add_invoice_cancel);
        Button btn_add_invoice_add = (Button) promptsView.findViewById(R.id.btn_add_invoice_add);

        btn_add_invoice_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        alertDialog.show();

    } */

    public void changeCustomer(final String old_cusomer_id, final String new_cusomer_id, final String id) {
        showPrd();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Plots").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(id)) {

                                DatabaseReference cineIndustryRef = databaseReference.child("Plots").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("customer_id", new_cusomer_id);
                                map.put("date_of_purchase", Helper.getCurrentDate("yyyy-MM-dd"));

                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        new CToast(mActivity).simpleToast("Plot transfer successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        getPlotDetail();
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

    public void changeCustomer1(final String old_cusomer_id, final String new_cusomer_id, final String id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.UNASSIGN_CUSTOMER_PLOT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Plot change", response);

                    if (jsonObject.getInt("result") == 1) {

                        getPlotDetail();
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
                params.put("plot_id", id);
                params.put("old_customer_id", old_cusomer_id);
                params.put("new_customer_id", new_cusomer_id);
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

    public void removeCustomer(final String id, final String cusomer_id) {
        showPrd();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
        Query query1 = databaseReference1.child("Payments").orderByKey();
        databaseReference1.keepSynced(true);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("plot_id").getValue().toString().equals(id)) {

                                DatabaseReference cineIndustryRef = databaseReference1.child("Payments").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("payment_status", 1);

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
                        }
                    }
                } catch (Exception e) {
                    hidePrd();
                    Log.e("exception s  ", e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
                Log.e("databaseError   ", databaseError.getMessage());
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Plots").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                            if (dataSnapshot.child("id").getValue().toString().equals(id)) {

                                DatabaseReference cineIndustryRef = databaseReference.child("Plots").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("customer_id", 0);
                                map.put("date_of_purchase", "0000-00-00");
                                map.put("purchase_price", "");

                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        new CToast(mActivity).simpleToast("Customer remove successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        finish();
                                        overridePendingTransition(0, 0);
                                        //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
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
                    Log.e("exception  remove ", e.toString());
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

    public void removeCustomer1(final String id, final String cusomer_id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.REMOVE_CUSTOMER_PLOT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    //Log.e("Response Plot remove ", response);

                    if (jsonObject.getInt("result") == 1) {
                        //getTaskDetail();
                        finish();
                        overridePendingTransition(0, 0);
                        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
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
                params.put("plot_id", id);
                params.put("customer_id", cusomer_id);
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

    public void addPaymentforPlot(final String date, final String time, final String amount, final String id, final String customer_id, final String remark, final String next_date,int checkButton) {
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
            map.put("file_type", mime);
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

            //TODO
            map.put("rent_status", 0);
            if(active_deActive.equalsIgnoreCase("DEACTIVE")){
                map.put("payment_status", 1);
            }else if(active_deActive.equalsIgnoreCase("ACTIVE")){
                map.put("payment_status", 0);
            }


            rootRef.child("Payments").child(key).setValue(map).addOnCompleteListener(Activity_Plot_Details.this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hidePrd();
                    getPlotDetail();
                    imageEncode = "";
                    capturedImageURI = Uri.parse("");
                    mime = "";
                    attachmentType = "";
                    new CToast(mActivity).simpleToast("Payment add successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
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
                }

            }).addOnFailureListener(Activity_Plot_Details.this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hidePrd();
                    Toast.makeText(Activity_Plot_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
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
                                    map.put("file_type", mime);
                                    map.put("customer_status", 0);


                                    //TODO
                                    map.put("rent_status", 0);
                                    map.put("payment_status", 0);

                                    rootRef.child("Payments").child(key).setValue(map).addOnCompleteListener(Activity_Plot_Details.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            hidePrd();
                                            getPlotDetail();
                                            imageEncode = "";
                                            capturedImageURI = Uri.parse("");
                                            mime = "";
                                            attachmentType = "";
                                            new CToast(mActivity).simpleToast("Payment add successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
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
                                        }

                                    }).addOnFailureListener(Activity_Plot_Details.this, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            hidePrd();
                                            Toast.makeText(Activity_Plot_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Activity_Plot_Details.this, "Please try later...", Toast.LENGTH_SHORT).show();
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
        if(txt_purchased_by.getText().toString()!=null && !txt_purchased_by.getText().toString().isEmpty()){
            text = "Dear  '"+txt_purchased_by.getText().toString().trim()+"'\n"+"Your payment has been credited "+getResources().getString(R.string.rupee)+amount+" to "+getResources().getString(R.string.app_name)+".\n"+"\nThanks";
        }else {
            text = "Dear  'customer"+"'\n"+"Your payment has been credited "+getResources().getString(R.string.rupee)+amount+" to "+getResources().getString(R.string.app_name)+".\n"+"\nThanks";
        }

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

    public void addPaymentforPlot1(final String date, final String time, final String amount, final String id, final String customer_id, final String remark, final String next_date) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.PAYMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.w("url", API.PAYMENTS);
                hidePrd();
                try {

                    //Log.e("Response Plot Add", response);
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getInt("result") == 1) {
                        getPlotDetail();
                        new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        attachmentType = "";
                        imageEncode = "";
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
                params.put("customer_id", customer_id);
                params.put("remarks", remark);
                params.put("payment_attachment", imageEncode);
                params.put("attachment_type", attachmentType);
                params.put("next_payment_date", next_date);
                Log.w("url", String.valueOf(params));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       /* MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_payment, menu);
        this.menu = menu;
        item = menu.findItem(R.id.add_plot_payment);*/

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_site_edit_delete, menu);
        this.menu = menu;
        item = menu.findItem(R.id.edit_site);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_plot_payment:

                LayoutInflater inflater = LayoutInflater.from(mActivity);
                View view1 = inflater.inflate(R.layout.dialog_payment_add, null);

                androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(mActivity);
                builder1.setView(view1);
                final androidx.appcompat.app.AlertDialog dialog = builder1.create();

                final EditText edt_paid_amount = (EditText) view1.findViewById(R.id.edt_paid_amount);
                final EditText edt_remark = (EditText) view1.findViewById(R.id.edt_remark);
                final TextView txt_date = (TextView) view1.findViewById(R.id.txt_date);
                final TextView txt_time = (TextView) view1.findViewById(R.id.txt_time);
                final LinearLayout viewUploadFile = (LinearLayout) view1.findViewById(R.id.viewUploadFile);
                TextView txt_next_date = (TextView) view1.findViewById(R.id.txt_next_date);

                ivUploadFile = (ImageView) view1.findViewById(R.id.ivUploadFile);
                ivUploadFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAttachImageOption();
                    }
                });
                tvUploadFile = (TextView) view1.findViewById(R.id.tvUploadFile);

                tvUploadFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAttachImageOption();
                    }
                });
                viewUploadFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAttachImageOption();
                    }
                });

                TextView btn_cancel = (TextView) view1.findViewById(R.id.btn_cancel);
                Button btn_add_payment = (Button) view1.findViewById(R.id.btn_add_payment);

                //edt_paid_amount.setText(txt_pending_amount.getTag().toString());

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

               *//* Calendar mcurrentTime = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);*//*

                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                txt_time.setText(currentTime);

                img_delete = (ImageView) view1.findViewById(R.id.img_delete);

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
                        } else if (txt_next_date.getText().toString().equals("")) {
                            new CToast(mActivity).simpleToast("Select Next Payment Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            return;
                        } else {
                            dialog.dismiss();
                            addPaymentforPlot(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString()
                                    , Aditya.ID, txt_purchased_by.getTag().toString(), edt_remark.getText().toString(), txt_next_date.getText().toString());
                        }
                    }
                });

                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_site:
                showPlotUpdateDialog();
                return true;

            case R.id.delete_site:
                deletePlot();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deletePlot() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Activity_Plot_Details.this);
        builder.setMessage("Are you sure you want to delete this Plot ?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deletePlotAPI();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePlotAPI() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("Plots").orderByChild("id").equalTo(plot_id);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("Payments").orderByChild("plot_id").equalTo(plot_id);

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Cancel", "onCancelled", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Cancel", "onCancelled", databaseError.toException());
            }
        });
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
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }


    public void showAttachImageOption() {

        CharSequence[] items_name = {"Capture Image", "Select From Gallery", "Select PDF"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mActivity);
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
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(getApplicationContext(), FilePickerActivity.class);
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                if (update_file == true) {
                    update_file = false;
                    capturedImageURI = data.getData();

                    ContentResolver cR = getContentResolver();
                    mime = cR.getType(capturedImageURI);

                    Uri selectedImageUri = data.getData();


                    //tvUploadFile.setText("Upload " + data.getData().getLastPathSegment().substring(data.getData().getLastPathSegment().lastIndexOf("/")).replace("/", ""));
                    tvUploadFile_update.setText("Upload File");

                    new ImageCompression(mActivity).execute(FileUtils.getPath(mActivity, selectedImageUri));


                    int newHeight = 500; // New height in pixels
                    int newWidth = 800; //
                    ivUploadFile_update.requestLayout();

                /*
                    getLayoutParams()
                        Get the LayoutParams associated with this view.
                */

                    // Apply the new height for ImageView programmatically
                    ivUploadFile_update.getLayoutParams().height = newHeight;

                    // Apply the new width for ImageView programmatically
                    ivUploadFile_update.getLayoutParams().width = newWidth;

                    // Set the scale type for ImageView image scaling
                    // ivUploadFile.setScaleType(ImageView.ScaleType.FIT_XY);


                    ivUploadFile_update.setImageURI(selectedImageUri);
                    img_delete_update.setVisibility(View.VISIBLE);

                    //Toast.makeText(getApplicationContext(),"update",Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getApplicationContext(),"add",Toast.LENGTH_LONG).show();
                    Uri selectedImageUri = data.getData();
                    capturedImageURI = data.getData();

                    ContentResolver cR = getContentResolver();
                    mime = cR.getType(capturedImageURI);

                    //tvUploadFile.setText("Upload " + data.getData().getLastPathSegment().substring(data.getData().getLastPathSegment().lastIndexOf("/")).replace("/", ""));
                    tvUploadFile.setText("Upload File");

                    new ImageCompression(mActivity).execute(FileUtils.getPath(mActivity, selectedImageUri));


                    int newHeight = 500; // New height in pixels
                    int newWidth = 800; //
                    ivUploadFile.requestLayout();

                /*
                    getLayoutParams()
                        Get the LayoutParams associated with this view.
                */

                    // Apply the new height for ImageView programmatically
                    ivUploadFile.getLayoutParams().height = newHeight;

                    // Apply the new width for ImageView programmatically
                    ivUploadFile.getLayoutParams().width = newWidth;

                    // Set the scale type for ImageView image scaling
                    // ivUploadFile.setScaleType(ImageView.ScaleType.FIT_XY);


                    ivUploadFile.setImageURI(selectedImageUri);
                    img_delete.setVisibility(View.VISIBLE);
                }


            } else if (requestCode == CAMERA_REQUEST) {
                // new ImageCompression(mActivity).execute(FileUtils.getPath(mActivity, capturedImageURI));

                if (update_file == false) {


                    new ImageCompression(mActivity).execute(FileUtils.getPath(mActivity, capturedImageURI));
                    int newHeight = 500; // New height in pixels
                    int newWidth = 800; //

                    ivUploadFile.getLayoutParams().height = newHeight;

                    // Apply the new width for ImageView programmatically
                    ivUploadFile.getLayoutParams().width = newWidth;
                    ivUploadFile.setImageURI(capturedImageURI);
                    tvUploadFile.setVisibility(View.GONE);
                    img_delete.setVisibility(View.VISIBLE);

                } else {

                    update_file = false;
                    new ImageCompression(mActivity).execute(FileUtils.getPath(mActivity, capturedImageURI));
                    int newHeight = 500; // New height in pixels
                    int newWidth = 800; //

                    ivUploadFile_update.getLayoutParams().height = newHeight;

                    // Apply the new width for ImageView programmatically
                    ivUploadFile_update.getLayoutParams().width = newWidth;
                    ivUploadFile_update.setImageURI(capturedImageURI);
                    tvUploadFile_update.setVisibility(View.GONE);
                    img_delete_update.setVisibility(View.VISIBLE);

                }

                ContentResolver cR = getContentResolver();
                mime = cR.getType(capturedImageURI);

            }

        }

        if (requestCode == 1) {

            if (update_file == true) {
                update_file = false;
                img_delete_update.setVisibility(View.VISIBLE);
                try {


                    ArrayList<MediaFile> mediaFiles = new ArrayList<>();


                    mediaFiles.clear();
                    mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));

                    MediaFile mediaFile = mediaFiles.get(0);
                    Log.w("path_new", String.valueOf(mediaFile.getUri()));


                    //  String path1 = mediaFile.getUri().toString(); // "file:///mnt/sdcard/FileName.mp3"
                    // File file = new File(new URI(path1));


                    String id = DocumentsContract.getDocumentId(mediaFile.getUri());
                    InputStream inputStream = Activity_Plot_Details.this.getContentResolver().openInputStream(mediaFile.getUri());

                    File file = new File(Activity_Plot_Details.this.getCacheDir().getAbsolutePath() + "/" + id);
                    writeFile(inputStream, file);
                    String filePath1 = file.getAbsolutePath();
                    capturedImageURI = mediaFile.getUri();

                    ContentResolver cR = getContentResolver();
                    mime = cR.getType(capturedImageURI);

                    Log.w("path_new_file", filePath1);

                    /*String path = MyFilePath.getPath(Activity_Rent_Details.this, mediaFile.getUri());
                    Log.w("path_new", String.valueOf(path));*/
                    imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(filePath1);

                    Log.w("pdf", imageEncode);
                    if (imageEncode != null && !imageEncode.equalsIgnoreCase("")) {

                        attachmentType = ".pdf";
                        ivUploadFile_update.setImageResource(R.drawable.ic_add_image);
                        tvUploadFile_update.setText("Upload " + mediaFile.getName());
                    } else {
                        try {
                            // ArrayList<MediaFile> mediaFiles = new ArrayList<>();


                            mediaFiles.clear();
                            mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));

                            //MediaFile mediaFile = mediaFiles.get(0);
                            Log.w("path_new_e", String.valueOf(mediaFile.getUri()));

                            String path = MyFilePath.getPath(Activity_Plot_Details.this, mediaFile.getUri());

                            //ivUploadFile.setImageResource(R.drawable.ic_add_image);
                            //tvUploadFile.setText("Upload " + data.getData().getLastPathSegment().substring(data.getData().getLastPathSegment().lastIndexOf("/")).replace("/", ""));
                            //Toast.makeText(getApplicationgetApplicationContext()(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                            ivUploadFile_update.setImageResource(R.drawable.ic_add_image);
                            tvUploadFile_update.setText("Upload " + mediaFile.getName());
                            //Toast.makeText(getApplicationgetApplicationContext()(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                            imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);
                            attachmentType = "pdf";

                            Log.w("pdf_ex", imageEncode);

                            if (imageEncode != null && !imageEncode.equalsIgnoreCase("")) {
                                //uploadpdf();
                            } else {
                                Toast.makeText(Activity_Plot_Details.this, "Can't upload wih drive", Toast.LENGTH_LONG).show();
                            }

                        } catch (IOException es) {
                            es.printStackTrace();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();


                }
                //Toast.makeText(getApplicationContext(),"update",Toast.LENGTH_LONG).show();
            } else {
                img_delete.setVisibility(View.VISIBLE);
                //Toast.makeText(getApplicationContext(),"add",Toast.LENGTH_LONG).show();
                try {


                    ArrayList<MediaFile> mediaFiles = new ArrayList<>();


                    mediaFiles.clear();
                    mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));

                    MediaFile mediaFile = mediaFiles.get(0);
                    Log.w("path_new", String.valueOf(mediaFile.getUri()));

                    capturedImageURI = mediaFile.getUri();

                    ContentResolver cR = getContentResolver();
                    mime = cR.getType(capturedImageURI);


                    //  String path1 = mediaFile.getUri().toString(); // "file:///mnt/sdcard/FileName.mp3"
                    // File file = new File(new URI(path1));


                    String id = DocumentsContract.getDocumentId(mediaFile.getUri());
                    InputStream inputStream = Activity_Plot_Details.this.getContentResolver().openInputStream(mediaFile.getUri());

                    File file = new File(Activity_Plot_Details.this.getCacheDir().getAbsolutePath() + "/" + id);
                    writeFile(inputStream, file);
                    String filePath1 = file.getAbsolutePath();
                    Log.w("path_new_file", filePath1);

                    /*String path = MyFilePath.getPath(Activity_Rent_Details.this, mediaFile.getUri());
                    Log.w("path_new", String.valueOf(path));*/
                    imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(filePath1);
                    Log.w("pdf", imageEncode);
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

                            String path = MyFilePath.getPath(Activity_Plot_Details.this, mediaFile.getUri());

                            //ivUploadFile.setImageResource(R.drawable.ic_add_image);
                            //tvUploadFile.setText("Upload " + data.getData().getLastPathSegment().substring(data.getData().getLastPathSegment().lastIndexOf("/")).replace("/", ""));
                            //Toast.makeText(getApplicationgetApplicationContext()(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                            ivUploadFile.setImageResource(R.drawable.ic_add_image);
                            tvUploadFile.setText("Upload " + mediaFile.getName());
                            //Toast.makeText(getApplicationgetApplicationContext()(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                            imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);
                            attachmentType = "pdf";

                            Log.w("pdf_ex", imageEncode);

                            if (imageEncode != null && !imageEncode.equalsIgnoreCase("")) {
                                //uploadpdf();
                            } else {
                                Toast.makeText(Activity_Plot_Details.this, "Can't upload wih drive", Toast.LENGTH_LONG).show();
                            }

                        } catch (IOException es) {
                            es.printStackTrace();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();


                }


            }


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

    private void uriToBitmap(Uri filePath) {
        try {
            Bitmap bitmap = Helper.decodeUri(filePath, mActivity);
            if (bitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                imageEncode = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                attachmentType = "png";
                Log.w("image", imageEncode);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            new CToast(mActivity).simpleToast("Fail to attach image try again", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
        }
    }


}

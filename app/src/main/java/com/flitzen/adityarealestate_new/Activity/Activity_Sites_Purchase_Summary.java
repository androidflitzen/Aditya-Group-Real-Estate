package com.flitzen.adityarealestate_new.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.flitzen.adityarealestate_new.Adapter.Adapter_Site_Payment_List;
import com.flitzen.adityarealestate_new.Apiutils;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.EncodeBased64Binary;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.WebAPI;
import com.flitzen.adityarealestate_new.CommonModel;
import com.flitzen.adityarealestate_new.Fragment.CashPaymentFragment;
import com.flitzen.adityarealestate_new.Fragment.CashReceiveFragment;
import com.flitzen.adityarealestate_new.Items.Item_Plot_List;
import com.flitzen.adityarealestate_new.Items.Item_Site_Payment_List;
import com.flitzen.adityarealestate_new.MyFilePath;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import retrofit2.Call;
import retrofit2.Callback;

public class Activity_Sites_Purchase_Summary extends AppCompatActivity {

    ProgressDialog prd;
    Activity mActivity;

    String site_id = "", file_url = "", payment_pdf_file_url = "",name = "",type="",address="",size="";
    TextView txt_site_name, txt_site_address, txt_purchase_price, txt_size, txt_total_paid, txt_income_cust, txt_pending_amount;
    FloatingActionButton fab_add_sites_payment;
    // SwipeRefreshLayout swipe_refresh;
    //RecyclerView rec_sites_list;
    CardView btnAddPDF;
    String imageEncode;
    Adapter_Site_Payment_List adapter_site_payment_list;
    ArrayList<Item_Site_Payment_List> itemList = new ArrayList<>();
    ArrayList<Item_Site_Payment_List> itemListTemp = new ArrayList<>();
    public final int FILE_REQUEST_CODE = 101;
    LinearLayout linAddFileView, linPdfView;
    ImageView ivDeleteFile, ivShareFile;
    boolean isFileAttached;
    ProgressDialog progressDialog;
    TabLayout tabs;
    private static final String TAG = "Google drive";
    private static final String SIGN_IN = "Sign In";
    private static final String DOWNLOAD_FILE = "Download file";

    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_OPEN_ITEM = 1;
    private GoogleSignInAccount signInAccount;
    private Set<Scope> requiredScopes;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    private OpenFileActivityOptions openOptions;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private File storageDir;
    GoogleApiClient mGoogleApiClient;
    TextView txt_agreement_status,tvtitle;
    RelativeLayout ivEdit;
    ImageView ivEdit1;
    int purchase_price=0;
    int total_received=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sites_purchase_summary);

       // getSupportActionBar().setTitle(Html.fromHtml("Site Purchase Summary"));
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = Activity_Sites_Purchase_Summary.this;

        site_id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        size = getIntent().getStringExtra("size");
        type = getIntent().getStringExtra("type");

        initialize();
        requestPermission();
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        }


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("File downloading...");
        txt_site_name = (TextView) findViewById(R.id.txt_site_name);
        txt_site_address = (TextView) findViewById(R.id.txt_site_address);
        txt_purchase_price = (TextView) findViewById(R.id.txt_purchase_price);
        txt_size = (TextView) findViewById(R.id.txt_size);
        txt_total_paid = (TextView) findViewById(R.id.txt_total_paid);
        txt_income_cust = (TextView) findViewById(R.id.txt_income_cust);
        txt_pending_amount = (TextView) findViewById(R.id.txt_pending_amount);
        linAddFileView = (LinearLayout) findViewById(R.id.linAddFileView);
        ivDeleteFile = (ImageView) findViewById(R.id.ivDeleteFile);
        ivShareFile = (ImageView) findViewById(R.id.ivShareFile);
        linPdfView = (LinearLayout) findViewById(R.id.linPdfView);
        btnAddPDF = (CardView) findViewById(R.id.btnAddPDF);
        btnAddPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFileAttached) {
                    /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(file_url));
                    startActivity(browserIntent);*/
                    startActivity(new Intent(Activity_Sites_Purchase_Summary.this, PdfViewActivity.class)
                            .putExtra("pdf_url", file_url)
                            .putExtra("only_load", true)
                            .putExtra("id", site_id)
                            .putExtra("site_name", txt_site_name.getText().toString()));
                } else {
                    showUploadOptionsDialog();
                }

            }
        });

        txt_agreement_status = (TextView) findViewById(R.id.txt_agreement_status);
        txt_agreement_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_agreement_status.getText().equals("Add PDF")) {
                    //openFileChooser_new();
                    Intent intent = new Intent(Activity_Sites_Purchase_Summary.this,GlobalListPDFActivity.class);
                    intent.putExtra("site_id",site_id);
                    intent.putExtra("name",name);
                    intent.putExtra("type",type);
                    startActivity(intent);
                } else if (txt_agreement_status.getText().equals("View PDF")) {
                    openUpdateDialog_view();
                }
            }
        });

        ivShareFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File myFile = new File(new File(Utils.getItemDir()), "IV-" + site_id + ".pdf");
                new DownloadFile(file_url, myFile).execute();
            }
        });
        ivDeleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog();
            }
        });

        ivEdit1 = (ImageView)findViewById(R.id.ivEdit1);
        ivEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ivEdit = (RelativeLayout)findViewById(R.id.ivEdit);

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_sites_edit, null);

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

                edt_site_name.setText(txt_site_name.getText().toString());
                edt_site_address.setText(txt_site_address.getTag().toString());
                edt_site_purchase_price.setText(txt_purchase_price.getTag().toString());
                edt_site_size.setText(txt_size.getText().toString());

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
                            editSite(edt_site_name.getText().toString().trim(), edt_site_address.getText().toString().trim(), edt_site_purchase_price.getText().toString().trim(), edt_site_size.getText().toString().trim());
                        }
                    }
                });

                alertDialog.show();
            }
        });

        tabs = findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Cash Payment"));
        tabs.addTab(tabs.newTab().setText("Cash Receive"));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        Fragment fragment = new CashPaymentFragment();
        Bundle args = new Bundle();
        args.putString("site_id", site_id);
        args.putString("site_address",address);
        args.putString("site_name",name);
        args.putString("site_size", size);
        fragment.setArguments(args);
        pushFragment(fragment);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        });

        /*swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
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
        });*/

        /*rec_sites_list = (RecyclerView) findViewById(R.id.rec_sites_list);
        rec_sites_list.setLayoutManager(new LinearLayoutManager(this));
        rec_sites_list.setHasFixedSize(true);
        rec_sites_list.setNestedScrollingEnabled(false);*/
        adapter_site_payment_list = new Adapter_Site_Payment_List(mActivity, itemList);
        //rec_sites_list.setAdapter(adapter_site_payment_list);

        fab_add_sites_payment = (FloatingActionButton) findViewById(R.id.fab_add_sites_payment);

        fab_add_sites_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_sites_payment_add, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);

                final EditText edt_paid_amount = (EditText) promptsView.findViewById(R.id.edt_paid_amount);
                final EditText edt_remark = (EditText) promptsView.findViewById(R.id.edt_remark);
                final TextView txt_date = (TextView) promptsView.findViewById(R.id.txt_date);

                TextView btn_cancel = (TextView) promptsView.findViewById(R.id.btn_cancel);
                Button btn_add_payment = (Button) promptsView.findViewById(R.id.btn_add_payment);

                txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
                txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

                txt_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Helper.pick_Date(mActivity, txt_date);
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_add_payment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (txt_date.getText().toString().equals("")) {
                            new CToast(mActivity).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            return;
                        } else if (edt_paid_amount.getText().toString().equals("")) {
                            edt_paid_amount.setError("Enter pending amount");
                            edt_paid_amount.requestFocus();
                            return;
                        } else if (Integer.parseInt(edt_paid_amount.getText().toString()) > Integer.parseInt(txt_pending_amount.getTag().toString())) {
                            edt_paid_amount.setError("You enter more then pending amount");
                            edt_paid_amount.requestFocus();
                            return;
                        } else {
                            alertDialog.dismiss();
                            addSitePayment(site_id, txt_date.getTag().toString().trim(), edt_paid_amount.getText().toString().trim(), edt_remark.getText().toString().trim());
                        }
                    }
                });

                alertDialog.show();

            }
        });

        adapter_site_payment_list.OnItemLongClickListener(new Adapter_Site_Payment_List.OnItemLongClickListener() {
            @Override
            public void onItemClick(final int position) {
                new androidx.appcompat.app.AlertDialog.Builder(mActivity, R.style.AppCompatAlertDialogStyle)
                        .setTitle(Html.fromHtml("<b> Delete </b>"))
                        .setMessage("Are you sure you want to delete this payment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                remove_Payment(itemList.get(position).getId());
                            }
                        }).setNegativeButton("No", null).show();
            }
        });
        getSitePaymentList();

    }

    private void showUploadOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Sites_Purchase_Summary.this);
        builder.setTitle("Choose");

// add a list
        String[] animals = {"Storage", "Google Drive"};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openFileChooser();
                        break;
                    case 1:
                        /*if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
                            signIn();
                        } else {*/
                        onDriveClientReady();
                        // }
                        break;
                }
            }
        });

// create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void openUpdateDialog_view() {
        LayoutInflater inflater = LayoutInflater.from(Activity_Sites_Purchase_Summary.this);
        View view = inflater.inflate(R.layout.agreement_dialog, null);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Activity_Sites_Purchase_Summary.this);
        builder.setView(view);
        final androidx.appcompat.app.AlertDialog dialog_new = builder.create();
        LinearLayout layout_view = (LinearLayout) view.findViewById(R.id.layout_view);
        LinearLayout layou_delete = (LinearLayout) view.findViewById(R.id.layou_delete);
        ImageView img_close = (ImageView) view.findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_new.dismiss();
            }
        });
        layout_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_new.dismiss();


                try {

                    Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
                    defaultBrowser.setData(Uri.parse(file_url));
                    startActivity(defaultBrowser);

                   /* Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(file_url));
                    startActivity(browserIntent);*/

                    /*Intent intent = new Intent(Activity_Sites_Purchase_Summary.this, PdfViewActivity.class);
                    intent.putExtra("pdf_url", file_url);
                    intent.putExtra("only_load", true);
                    intent.putExtra("id", site_id);
                    intent.putExtra("site_name", txt_site_name.getText().toString());
                    intent.putExtra("only_load", true);

*//*
                    startActivity(browserIntent);
                    startActivity(new Intent(Activity_Sites_Purchase_Summary.this, PdfViewActivity.class)
                            .putExtra("pdf_url", file_url)
                            .putExtra("only_load", true)
                            .putExtra("id", site_id)
                            .putExtra("site_name", txt_site_name.getText().toString()));*//*

                *//*    getApplicationContext().startActivity(new Intent(getApplicationContext(), PdfViewActivity.class)
                            .putExtra("pdf_url", agreement_pdf_url)
                            .putExtra("only_load", true));*//*

                    startActivity(intent);*/
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(Activity_Sites_Purchase_Summary.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                }


            }
        });

        layou_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Sites_Purchase_Summary.this);
                builder.setMessage("Delete this Attachment ?");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteSiteFile();
                                dialog.dismiss();
                                dialog_new.dismiss();
                            }
                        });

                builder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder.create();
                alert11.show();
                //Toast.makeText(getApplicationContext(),"delete",Toast.LENGTH_SHORT).show();
            }
        });
        dialog_new.show();
    }

    private void openFileChooser_new() {
        try {

            Intent intent = new Intent(Activity_Sites_Purchase_Summary.this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .setSuffixes("pdf")
                    .setShowFiles(true)
                    .setShowImages(false)
                    .setShowVideos(false)
                    .setMaxSelection(1)
                    .setRootPath(Environment.getExternalStorageDirectory().getPath() + "/Download")
                    .build());
            startActivityForResult(intent, 07);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openFileChooser() {
        Intent intent = new Intent(Activity_Sites_Purchase_Summary.this, FilePickerActivity.class);
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
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    public void getSitePaymentList() {

        showPrd();
        purchase_price=0;
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
        Query querySite = databaseReference1.child("Sites").orderByKey();
        querySite.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshotSite : dataSnapshot.getChildren()) {
                            if(npsnapshotSite.child("id").getValue().toString().equals(site_id)){

                                txt_site_name.setText(npsnapshotSite.child("site_name").getValue().toString());
                                Log.e("Site address  ", npsnapshotSite.child("site_address").getValue().toString());

                                txt_site_address.setText("(" + npsnapshotSite.child("site_address").getValue().toString() + ")" + "(" + npsnapshotSite.child("size").getValue().toString() + "Sq yard)");
                                txt_site_address.setTag(npsnapshotSite.child("site_address").getValue().toString());
                                address=npsnapshotSite.child("site_address").getValue().toString();
                                size=npsnapshotSite.child("size").getValue().toString();
                                purchase_price=Integer.parseInt(npsnapshotSite.child("purchase_price").getValue().toString());

                                txt_purchase_price.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(npsnapshotSite.child("purchase_price").getValue().toString())));
                                txt_purchase_price.setTag(npsnapshotSite.child("purchase_price").getValue().toString());
                                txt_size.setText(npsnapshotSite.child("size").getValue().toString());


                                if (npsnapshotSite.child("file").getValue().toString().equals("")) {
                                    linAddFileView.setVisibility(View.VISIBLE);
                                } else {
                                    isFileAttached = true;
                                    file_url = npsnapshotSite.child("file").getValue().toString();
                                    linPdfView.setVisibility(View.VISIBLE);

                                    txt_agreement_status.setText("View PDF");
                                    txt_agreement_status.setBackgroundResource(R.drawable.round_feel_button);
                                    txt_agreement_status.setTextColor(getResources().getColor(R.color.white));

                                }

                            }
                            // }
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


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("SitePayments").orderByKey();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                itemList.clear();
                itemListTemp.clear();
                try {
                    if (dataSnapshot.exists()) {
                        int total_payments=0;
                        int remaining_amount=0;
                        for (DataSnapshot npsnapshotPay : dataSnapshot.getChildren()) {
                            if(npsnapshotPay.child("site_id").getValue().toString().equals(site_id)){

                                Item_Site_Payment_List item = new Item_Site_Payment_List();
                                item.setId(npsnapshotPay.child("id").getValue().toString());
                                item.setSite_id(npsnapshotPay.child("site_id").getValue().toString());
                                item.setAmount(npsnapshotPay.child("amount").getValue().toString());
                                item.setRemarks(npsnapshotPay.child("remarks").getValue().toString());

                                total_payments=total_payments+Integer.parseInt(npsnapshotPay.child("amount").getValue().toString());

                                try {
                                    DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    Date d = f.parse(npsnapshotPay.child("payment_date").getValue().toString());
                                    DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat time = new SimpleDateFormat("hh:mm:ss");
                                    System.out.println("=====Date: " + date.format(d));
                                    System.out.println("======Time: " + time.format(d));
                                    item.setPayment_date(date.format(d));
                                    item.setPayment_time(time.format(d));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                itemList.add(item);
                                itemListTemp.add(item);
                            }
                        }

                        if (total_payments!=0)
                            txt_total_paid.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(total_payments));
                        else
                            txt_total_paid.setText("-");

                        remaining_amount=purchase_price-total_payments;

                        if (remaining_amount!=0) {
                            txt_pending_amount.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(remaining_amount));
                            txt_pending_amount.setTag(String.valueOf(remaining_amount));
                        } else
                            txt_pending_amount.setText("-");

                        adapter_site_payment_list.notifyDataSetChanged();

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

        Query queryPlots = databaseReference.child("Plots").orderByKey();

        queryPlots.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                try {
                    if (dataSnapshot.exists()) {
                        total_received=0;
                        for (DataSnapshot npsnapshotPlot : dataSnapshot.getChildren()) {
                            if(npsnapshotPlot.child("site_id").getValue().toString().equals(site_id)){

                                System.out.println("============site_id  "+site_id);
                                Query queryPayments = databaseReference.child("Payments").orderByKey();
                                queryPayments.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot npsnapshotPay : dataSnapshot.getChildren()) {
                                                    if(npsnapshotPay.child("plot_id").getValue().toString().equals(npsnapshotPlot.child("id").getValue().toString())){
                                                        total_received=total_received+Integer.parseInt(npsnapshotPay.child("amount").getValue().toString());
                                                        System.out.println("========amount  "+npsnapshotPay.child("amount").getValue().toString());
                                                    }
                                                }
                                                if (total_received!=0)
                                                    txt_income_cust.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(total_received));
                                                else
                                                    txt_income_cust.setText("-");

                                                adapter_site_payment_list.notifyDataSetChanged();

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

                            }
                        }

                        adapter_site_payment_list.notifyDataSetChanged();
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
    }

    public void getSitePaymentList1() {
        showPrd();
        //swipe_refresh.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, API.SITE_PAYMENT + "?type=List&site_id=" + site_id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                //swipe_refresh.setRefreshing(false);
                try {
                    itemList.clear();
                    itemListTemp.clear();

                    JSONObject jsonObject = new JSONObject(response);
                     Log.e("Response Sites List", response);

                    if (jsonObject.getInt("result") == 1) {

                        txt_site_name.setText(jsonObject.getString("site_name"));
                        txt_site_address.setText("(" + jsonObject.getString("site_address") + ")" + "(" + jsonObject.getString("size") + "Sq yard)");
                        txt_site_address.setTag(jsonObject.getString("site_address"));

                        txt_purchase_price.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(jsonObject.getString("purchase_price"))));
                        txt_purchase_price.setTag(jsonObject.getString("purchase_price"));
                        txt_size.setText(jsonObject.getString("size"));

                        if (!jsonObject.getString("total_payments").equals("0"))
                            txt_total_paid.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(jsonObject.getString("total_payments"))));
                        else
                            txt_total_paid.setText("-");

                        if (!jsonObject.getString("remaining_amount").equals("0")) {
                            txt_pending_amount.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(jsonObject.getString("remaining_amount"))));
                            txt_pending_amount.setTag(jsonObject.getString("remaining_amount"));
                        } else
                            txt_pending_amount.setText("-");

                        if (!jsonObject.getString("total_received").equals("0"))
                            txt_income_cust.setText(getResources().getString(R.string.rupee) + Helper.getFormatPrice(Integer.parseInt(jsonObject.getString("total_received"))));
                        else
                            txt_income_cust.setText("-");

                        if (jsonObject.getString("file").equals("")) {
                            linAddFileView.setVisibility(View.VISIBLE);
                        } else {
                            isFileAttached = true;
                            file_url = jsonObject.getString("file");
                            linPdfView.setVisibility(View.VISIBLE);

                            txt_agreement_status.setText("View PDF");
                            txt_agreement_status.setBackgroundResource(R.drawable.round_feel_button);
                            txt_agreement_status.setTextColor(getResources().getColor(R.color.white));

                        }
                        JSONArray jsonArray = jsonObject.getJSONArray("payment");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);

                            Item_Site_Payment_List item = new Item_Site_Payment_List();
                            item.setId(object.getString("id"));
                            item.setSite_id(object.getString("site_id"));
                            item.setAmount(object.getString("amount"));
                            item.setRemarks(object.getString("remarks"));
                            item.setPayment_date(object.getString("payment_date"));
                            item.setPayment_time(object.getString("payment_time"));

                            itemList.add(item);
                            itemListTemp.add(item);
                        }
                        adapter_site_payment_list.notifyDataSetChanged();

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
                //swipe_refresh.setRefreshing(false);
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        Log.e("Site   ",stringRequest.getUrl());
        queue.add(stringRequest);
    }

    public void addSitePayment(final String id, final String date, final String amount, final String remarks) {

        showPrd();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String key = rootRef.child("SitePayments").push().getKey();
        Map<String, Object> map = new HashMap<>();
        map.put("site_id", id);
        map.put("amount", amount);
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        map.put("payment_date", date + " " + currentTime);
        map.put("remarks", remarks);
        map.put("id", key);

        rootRef.child("SitePayments").child(key).setValue(map).addOnCompleteListener(Activity_Sites_Purchase_Summary.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hidePrd();
                new CToast(Activity_Sites_Purchase_Summary.this).simpleToast("Payment added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                getSitePaymentList();
            }

        }).addOnFailureListener(Activity_Sites_Purchase_Summary.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hidePrd();
                Toast.makeText(Activity_Sites_Purchase_Summary.this, "Please try later...", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void remove_Payment(final String id) {

         showPrd();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("SitePayments").orderByChild("id").equalTo(id);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hidePrd();
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                    new CToast(Activity_Sites_Purchase_Summary.this).simpleToast("Delete payment successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                    getSitePaymentList();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hidePrd();
                Log.e("Cancel ", "onCancelled", databaseError.toException());
            }
        });
    }

    private void remove_Payment1(final String id) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.DELETE_SITE_PAYMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log.e("Delete payment", response);

                        hidePrd();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("result") == 1) {

                                new CToast(mActivity).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                getSitePaymentList();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_site_edit, menu);
        menu.findItem(R.id.share).setVisible(false);
        menu.findItem(R.id.delete).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_site:

                LayoutInflater localView = LayoutInflater.from(mActivity);
                View promptsView = localView.inflate(R.layout.dialog_sites_edit, null);

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

                edt_site_name.setText(txt_site_name.getText().toString());
                edt_site_address.setText(txt_site_address.getTag().toString());
                edt_site_purchase_price.setText(txt_purchase_price.getTag().toString());
                edt_site_size.setText(txt_size.getText().toString());

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
                            editSite(edt_site_name.getText().toString().trim(), edt_site_address.getText().toString().trim(), edt_site_purchase_price.getText().toString().trim(), edt_site_size.getText().toString().trim());
                        }
                    }
                });

                alertDialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void editSite(final String site_name, final String site_Address, final String site_Price, final String site_Size) {
        showPrd();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("Sites").orderByKey();
        databaseReference.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot npsnapshot) {
                hidePrd();
                try {
                    if (npsnapshot.exists()) {
                        for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {
                            if (dataSnapshot.child("id").getValue().toString().equals(site_id)) {
                                DatabaseReference cineIndustryRef = databaseReference.child("Sites").child(dataSnapshot.getKey());
                                Map<String, Object> map = new HashMap<>();
                                map.put("site_name", site_name);
                                map.put("site_address", site_Address);
                                map.put("purchase_price", site_Price);
                                map.put("size", site_Size);

                                Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        hidePrd();
                                        new CToast(Activity_Sites_Purchase_Summary.this).simpleToast("Site updated successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        getSitePaymentList();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hidePrd();
                                        new CToast(Activity_Sites_Purchase_Summary.this).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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

    public void editSite1(final String site_name, final String site_Address, final String site_Price, final String site_Size) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.EDIT_SITE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Sites Edit", response);

                    if (jsonObject.getInt("result") == 1) {

                        getSitePaymentList();
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

      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        Intent intent = new Intent(Activity_Sites_Purchase_Summary.this,Activity_Plots_List.class);
        intent.putExtra("id",site_id);
        intent.putExtra("name", name);
        intent.putExtra("TYPE", "ADD");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FILE_REQUEST_CODE) {
                ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
                if (files != null && files.size() > 0) {
                    File attachmentFile = new File(files.get(0).getPath());
                    addSiteFileAPI(Helper.getStringFromFile(attachmentFile));
                }
            } else if (requestCode == REQUEST_CODE_SIGN_IN) {
                Task<GoogleSignInAccount> getAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());
                    showMessage("Sign in successfully.");
                } else {
                    showMessage("Sign in failed.");
                }
            } else if (requestCode == REQUEST_CODE_OPEN_ITEM) {
                DriveId driveId = data.getParcelableExtra(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                mOpenItemTaskSource.setResult(driveId);
            } else if (requestCode == 07) {
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
                    //addSiteFileAPI(imageEncode);
                    if (imageEncode.equalsIgnoreCase("")) {
                        try {


                            mediaFiles.clear();
                            mediaFiles.addAll(data.<MediaFile>getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES));

                            mediaFile = mediaFiles.get(0);
                            Log.w("path_new_e_imageEncode", String.valueOf(mediaFile.getUri()));

                            String path = MyFilePath.getPath(getApplicationContext(), mediaFile.getUri());

                            //ivUploadFile.setImageResource(R.drawable.ic_add_image);
                            //tvUploadFile.setText("Upload " + data.getData().getLastPathSegment().substring(data.getData().getLastPathSegment().lastIndexOf("/")).replace("/", ""));
                            //Toast.makeText(getApplicationContext(),imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path),Toast.LENGTH_SHORT).show();
                            imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);


                            if (imageEncode != null && !imageEncode.equalsIgnoreCase("")) {
                                addSiteFileAPI(imageEncode);
                            } else {
                                Toast.makeText(getApplicationContext(), "Can't upload wih drive", Toast.LENGTH_LONG).show();
                            }

                        } catch (IOException es) {
                            es.printStackTrace();
                        }


                        //addSiteFileAPI(imageEncode);
                    }else {
                        addSiteFileAPI(imageEncode);
                    }
                    //uploadpdf();


                } catch (Exception e) {

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


                        if (imageEncode != null && !imageEncode.equalsIgnoreCase("")) {
                            addSiteFileAPI(imageEncode);
                        } else {
                            Toast.makeText(getApplicationContext(), "Can't upload wih drive", Toast.LENGTH_LONG).show();
                        }

                    } catch (IOException es) {
                        es.printStackTrace();
                    }

                    e.printStackTrace();


                }
            } else {
                mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
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


    private void addSiteFileAPI(final String file_content) {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.ADD_SITE_FILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                      Log.e("Response Tasks Add", response);
                    if (jsonObject.getInt("result") == 1) {
                        linAddFileView.setVisibility(View.GONE);
                        linPdfView.setVisibility(View.VISIBLE);
                        isFileAttached = true;
                        txt_agreement_status.setText("View PDF");
                        txt_agreement_status.setBackgroundResource(R.drawable.round_feel_button);
                        txt_agreement_status.setTextColor(getResources().getColor(R.color.white));
                        new CToast(Activity_Sites_Purchase_Summary.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                        getSitePaymentList();
                    } else {
                        txt_agreement_status.setText("Add PDF");
                        txt_agreement_status.setBackgroundResource(R.drawable.round_simple_button);
                        txt_agreement_status.setTextColor(getResources().getColor(R.color.black));
                        new CToast(Activity_Sites_Purchase_Summary.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("site_id", site_id);
                params.put("file_content", file_content);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(Activity_Sites_Purchase_Summary.this);
        queue.add(stringRequest);
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Sites_Purchase_Summary.this);
        builder.setMessage("Delete this Attachment ?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteSiteFile();
                    }
                });

        builder.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    private void deleteSiteFile() {

        showPrd();
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(file_url);

        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hidePrd();
                // File deleted successfully

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                Query query = databaseReference.child("Sites").orderByKey();
                databaseReference.keepSynced(true);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot npsnapshot) {
                        hidePrd();
                        try {
                            if (npsnapshot.exists()) {
                                for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                                    if (dataSnapshot.child("id").getValue().toString().equals(site_id)) {
                                        DatabaseReference cineIndustryRef = databaseReference.child("Sites").child(dataSnapshot.getKey());
                                        Map<String, Object> map = new HashMap<>();
                                            map.put("file", "");

                                        Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                new CToast(mActivity).simpleToast("File delete successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                linAddFileView.setVisibility(View.VISIBLE);
                                                linPdfView.setVisibility(View.GONE);
                                                isFileAttached = false;

                                                txt_agreement_status.setText("Add PDF");
                                                txt_agreement_status.setBackgroundResource(R.drawable.round_simple_button);
                                                txt_agreement_status.setTextColor(getResources().getColor(R.color.black));
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

    private void deleteSiteFile1() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.DELETE_SITE_FILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                hidePrd();
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    // Log.e("Response Task complete", response);

                    if (jsonObject.getInt("result") == 1) {
                        linAddFileView.setVisibility(View.VISIBLE);
                        linPdfView.setVisibility(View.GONE);
                        isFileAttached = false;

                        txt_agreement_status.setText("Add PDF");
                        txt_agreement_status.setBackgroundResource(R.drawable.round_simple_button);
                        txt_agreement_status.setTextColor(getResources().getColor(R.color.black));
                        new CToast(Activity_Sites_Purchase_Summary.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();

                    } else {
                        new CToast(Activity_Sites_Purchase_Summary.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                params.put("site_id", site_id);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(Activity_Sites_Purchase_Summary.this);
        queue.add(stringRequest);
    }

    public void downloadFile(String fileURL, File directory) {
        try {

            FileOutputStream f = new FileOutputStream(directory);
            URL u = new URL(fileURL);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            InputStream in = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
            progressDialog.dismiss();
            shareFile(directory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareFile(File file) {

        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM,
                Uri.parse("file://" + file.getAbsolutePath()));

        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");

        startActivity(Intent.createChooser(intentShareFile, "Share File"));

    }

    class DownloadFile extends AsyncTask<Void, Void, String> {

        String url;
        File file;
        int action;

        public DownloadFile(String url, File file) {
            this.url = url;
            this.file = file;
            this.action = action;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            downloadFile(url, file);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            shareFile(file);
        }
    }

    private void selectFragment(int position) {
        if (position == 0) {
            Fragment fragment = new CashPaymentFragment();
            Bundle args = new Bundle();
            args.putString("site_id", site_id);
            args.putString("site_address",address);
            args.putString("site_name",name);
            args.putString("site_size", size);
            fragment.setArguments(args);
            pushFragment(fragment);
        } else if (position == 1) {
            Fragment fragment = new CashReceiveFragment();
            Bundle args = new Bundle();
            args.putString("site_id", site_id);
            args.putString("site_address",address);
            args.putString("site_name",name);
            args.putString("site_size", size);
            fragment.setArguments(args);
            pushFragment(fragment);
        }
    }

    private boolean pushFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    //.setCustomAnimations(R.anim.feed_in, R.anim.feed_out)
                    .setCustomAnimations(0,0)
                    .replace(R.id.cash_fragment_container, fragment)
                    //.addToBackStack("fragment")
                    .commit();
            return true;
        }
        return false;
    }

    private void onDriveClientReady() {
        mOpenItemTaskSource = new TaskCompletionSource<>();
        mDriveClient.newOpenFileActivityIntentSender(openOptions)
                .continueWith(new Continuation<IntentSender, Void>() {
                    @Override
                    public Void then(@NonNull Task<IntentSender> task) throws Exception {
                        startIntentSenderForResult(task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                        return null;
                    }
                });

        Task<DriveId> tasks = mOpenItemTaskSource.getTask();
        tasks.addOnSuccessListener(this,
                new OnSuccessListener<DriveId>() {
                    @Override
                    public void onSuccess(DriveId driveId) {
                        retrieveContents(driveId.asDriveFile());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("File not selected.");
                    }
                });
    }

    private void retrieveContents(final DriveFile file) {
        // [START open_file]
        final Task<DriveContents> openFileTask = mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
        // [END open_file]
        // [START read_contents]
        openFileTask.continueWithTask(new Continuation<DriveContents, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                DriveContents contents = task.getResult();
                File driveFile = null;
                Log.v(TAG, "File name : " + contents.getDriveId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    InputStream input = contents.getInputStream();

                    try {
                        driveFile = new File(Utils.getItemDir(), "drive.pdf");
                        Log.v(TAG, storageDir + "");
                        OutputStream output = new FileOutputStream(driveFile);
                        try {
                            try {
                                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                int read;

                                while ((read = input.read(buffer)) != -1) {
                                    output.write(buffer, 0, read);
                                }
                                output.flush();
                            } finally {
                                output.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (driveFile != null) {
                    //showMessage("Download file successfully.");
                    addSiteFileAPI(Helper.getStringFromFile(driveFile));
                }
                return mDriveResourceClient.discardContents(contents);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Unable to download file.");
                    }
                });
        // [END read_contents]
    }

    private void requestPermission() {
        String dirPath = getFilesDir().getAbsolutePath() + File.separator + "PDF";
        storageDir = new File(dirPath);
        if (!storageDir.exists())
            storageDir.mkdirs();
    }

    private void initialize() {

        requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);

        openOptions = new OpenFileActivityOptions.Builder()
                .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "application/pdf"))
                .setActivityTitle("Select file")
                .build();
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void signIn() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE)
                .requestScopes(Drive.SCOPE_APPFOLDER)
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
        startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }
}

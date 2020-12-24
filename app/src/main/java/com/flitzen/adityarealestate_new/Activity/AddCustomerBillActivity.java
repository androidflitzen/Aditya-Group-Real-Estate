package com.flitzen.adityarealestate_new.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.Permission;
import com.flitzen.adityarealestate_new.Classes.Validation;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AddCustomerBillActivity extends AppCompatActivity {
    LinearLayout layoutImgUpload;
    EditText etBillMonth/*, etBillPrice, etBillNote*/;
    ImageView ivUploadImg;
    TextView lblAddBill;
    CardView btnSave;
    private Uri capturedImageURI;
    int SELECT_PICTURE = 002, CAMERA_REQUEST = 003;
    String imageEncode = "", property_id = "", file_type = "";
    ProgressDialog prd;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer_bill);
        initUI();
    }

    private void initUI() {
        property_id = getIntent().getStringExtra("property_id");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add New Bill");
        layoutImgUpload = findViewById(R.id.layoutImgUpload);
        etBillMonth = findViewById(R.id.etBillMonth);
        /*etBillPrice = findViewById(R.id.etBillPrice);
        etBillNote = findViewById(R.id.etBillNote);*/
        ivUploadImg = findViewById(R.id.ivUploadImg);
        lblAddBill = findViewById(R.id.lblAddBill);
        btnSave = findViewById(R.id.btnSave);

        ivUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Permission.hasPermissions(AddCustomerBillActivity.this, permissions)) {
                    showAttachImageOption();
                } else {
                    Permission.requestPermissions(AddCustomerBillActivity.this, permissions, 111);
                }

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid()) {
                    if (imageEncode != null && !imageEncode.equals("")) {
                        AddBillAPI();
                    } else {
                        new CToast(AddCustomerBillActivity.this).simpleToast("Please Select Bill Image...", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }
                }
            }
        });
    }

    private boolean isValid() {
        if (Validation.isEmpty(etBillMonth)) return false;
        //if (Validation.isEmpty(etBillPrice)) return false;
        return true;
    }

    private void AddBillAPI() {
        showPrd();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        System.out.println("=======capturedImageURI  "+capturedImageURI);

        final StorageReference sRef = storageReference.child("/light_bills" + "/" + UUID.randomUUID().toString());
        sRef.putFile(capturedImageURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                        task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onSuccess(Uri uri) {
                                String key = rootRef.child("Light_bill").push().getKey();
                                Map<String, Object> map = new HashMap<>();
                                map.put("Bill_id", key);
                                map.put("Bill_month", etBillMonth.getText().toString());
                                map.put("Bill_photo", uri.toString());
                                map.put("Properties_id", property_id);

                                String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

                                map.put("created_date", currentDate + " " + currentTime);

                                rootRef.child("Light_bill").child(key).setValue(map).addOnCompleteListener(AddCustomerBillActivity.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        hidePrd();
                                        new CToast(AddCustomerBillActivity.this).simpleToast("Light bill added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                        overridePendingTransition(0, 0);
                                        finish();
                                    }

                                }).addOnFailureListener(AddCustomerBillActivity.this, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        hidePrd();
                                        Toast.makeText(AddCustomerBillActivity.this, "Please try later...", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddCustomerBillActivity.this, "Please try later...", Toast.LENGTH_SHORT).show();
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

    private void AddBillAPI1() {
        showPrd();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, API.CUSTOMER_BILL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.e("Upload image", response);

                        hidePrd();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("result") == 1) {

                                new CToast(AddCustomerBillActivity.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                                overridePendingTransition(0, 0);
                                finish();
                            } else {
                                new CToast(AddCustomerBillActivity.this).simpleToast(jsonObject.getString("message"), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
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
                        new CToast(AddCustomerBillActivity.this).simpleToast("Something went wrong...", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "Add");
                params.put("Properties_id", property_id);
                params.put("Bill_month", etBillMonth.getText().toString());
                /* params.put("Bill_Rs", etBillPrice.getText().toString());
                params.put("Other_Notes", etBillNote.getText().toString());*/
                params.put("file_content", imageEncode);
                params.put("file_type", file_type);
                return params;
            }

        };

        try {
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(AddCustomerBillActivity.this);
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }

    public void showAttachImageOption() {
        CharSequence[] items_name = {"Capture Image", "Select From Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddCustomerBillActivity.this);
        builder.setTitle("Select Option");
        builder.setItems(items_name, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    capturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);
                    startActivityForResult(intent, CAMERA_REQUEST);
                    overridePendingTransition(0, 0);
                    //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);

                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                capturedImageURI = data.getData();
                file_type = Utils.getMimeType(AddCustomerBillActivity.this, capturedImageURI);
                try {
                    Bitmap bitmap = Helper.decodeUri(capturedImageURI, AddCustomerBillActivity.this);
                    //addImage(bitmap);

                    if (bitmap != null) {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        imageEncode = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                        ivUploadImg.setImageBitmap(bitmap);
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    new CToast(this).simpleToast("Fail to attach image try again", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }
            } else if (requestCode == CAMERA_REQUEST) {
                //capturedImageURI = data.getData();
                file_type = Utils.getMimeType(AddCustomerBillActivity.this, capturedImageURI);
                try {
                    Bitmap bitmap = Helper.decodeUri(capturedImageURI, this);
                    if (bitmap != null) {

                        //addImage(bitmap);

                        if (bitmap != null) {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                            imageEncode = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                            ivUploadImg.setImageBitmap(bitmap);
                        }

                    } else {
                        new CToast(this).simpleToast("Fail to attach image try again", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    new CToast(this).simpleToast("Fail attach image\n" + e.getMessage().toString().trim(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }

                /*Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                addImage(bitmap);*/
            }
        }
    }

    public void showPrd() {
        prd = new ProgressDialog(AddCustomerBillActivity.this);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (Permission.hasPermissions(AddCustomerBillActivity.this, permissions)) {
                showAttachImageOption();
            }
        }
    }
}

package com.flitzen.adityarealestate_new.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flitzen.adityarealestate_new.Apiutils;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.EncodeBased64Binary;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Classes.WebAPI;
import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.UploadPdfModel;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
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
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;

public class PdfActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {
    public static final String SAMPLE_FILE = "android_tutorial.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    String TAG = "PdfActivity";
    int position = -1;
    RelativeLayout btn_code;
    String property_name = "", property_id = "", customer_id = "", position_new_o = "";
    TextView tvtitle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        init();

        property_name = getIntent().getStringExtra("property_name");
        property_id = getIntent().getStringExtra("property_id");
        customer_id = getIntent().getStringExtra("customer_id");
        position_new_o = getIntent().getStringExtra("position");

        tvtitle = (TextView)findViewById(R.id.tvtitle);
        tvtitle.setText( ListPDFActivity.fileList.get(position).getName());
        btn_code = (RelativeLayout) findViewById(R.id.btn_code);
        btn_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = ListPDFActivity.fileList.get(position).getPath();
                try {
                    String imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);
                    //Toast.makeText(getApplicationContext(),path, Toast.LENGTH_LONG).show();


                    final ProgressDialog progressDialog = new ProgressDialog(PdfActivity.this);
                    progressDialog.setTitle("Aditya Group");
                    progressDialog.setMessage("Please wait..!");
                    progressDialog.show();
                    WebAPI webAPI = Apiutils.getClient().create(WebAPI.class);
                    Log.w("ravi_nimavat", imageEncode);

                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                    final StorageReference sRef = storageReference.child("/files" + "/" + UUID.randomUUID().toString());
                    sRef.putFile(Uri.fromFile(new File(path)))
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @SuppressLint("LongLogTag")
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            progressDialog.dismiss();
                                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                            String key = rootRef.child("RantDocument").push().getKey();
                                            Map<String, Object> map = new HashMap<>();
                                            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                                            map.put("created_at", currentDate+" "+currentTime);
                                            map.put("customer_id", customer_id);
                                            map.put("document", uri.toString());
                                            map.put("p_id", property_id);
                                            map.put("status", 1);
                                            map.put("id",key);

                                            rootRef.child("RantDocument").child(key).setValue(map).addOnCompleteListener(PdfActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    new CToast(PdfActivity.this).simpleToast("File uploaded successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                    Intent intent = new Intent(PdfActivity.this, Activity_Rent_Details.class);
                                                    intent.putExtra("property_name", property_name);
                                                    intent.putExtra("property_id", property_id);
                                                    intent.putExtra("customer_id", customer_id);
                                                    intent.putExtra("position", position_new_o);
                                                    intent.putExtra("pdf_code", "1");
                                                    intent.putExtra("status", "0");
                                                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);

                                                }

                                            }).addOnFailureListener(PdfActivity.this, new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(PdfActivity.this, "Please try later...", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(PdfActivity.this, Activity_Rent_Details.class);
                                                    intent.putExtra("property_name", property_name);
                                                    intent.putExtra("property_id", property_id);
                                                    intent.putExtra("customer_id", customer_id);
                                                    intent.putExtra("position", position_new_o);
                                                    intent.putExtra("pdf_code", "0");
                                                    intent.putExtra("status", "0");
                                                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }

                                            });
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    progressDialog.dismiss();
                                    Toast.makeText(PdfActivity.this, "Please try later...", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                    Log.e("progress  ", String.valueOf(progress));
                                }
                            });

                    //Log.w("ravi",imageEncode);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    private void init() {
        pdfView = (PDFView) findViewById(R.id.pdfView);
        position = getIntent().getIntExtra("position_new", -1);
        displayFromSdcard();
    }

    private void displayFromSdcard() {
        pdfFileName = ListPDFActivity.fileList.get(position).getName();

        pdfView.fromFile(ListPDFActivity.fileList.get(position))
                .defaultPage(pageNumber)
                .enableSwipe(true)

                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }


    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
}

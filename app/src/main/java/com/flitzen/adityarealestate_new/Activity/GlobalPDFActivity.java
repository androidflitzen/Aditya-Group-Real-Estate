package com.flitzen.adityarealestate_new.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Apiutils;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.EncodeBased64Binary;
import com.flitzen.adityarealestate_new.Classes.SharePref;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.Classes.WebAPI;
import com.flitzen.adityarealestate_new.R;
import com.flitzen.adityarealestate_new.UploadPdfModel;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
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

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;

public class GlobalPDFActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {
    public static final String SAMPLE_FILE = "android_tutorial.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;
    String TAG = "PdfActivity";
    int position = -1;
    RelativeLayout btn_code;
    String site_id = "", file_content = "", name = "", type = "";
    ;
    TextView tvtitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        site_id = getIntent().getStringExtra("site_id");
        name = getIntent().getStringExtra("name");
        type = getIntent().getStringExtra("type");
        init();


        tvtitle = (TextView) findViewById(R.id.tvtitle);
        tvtitle.setText(GlobalListPDFActivity.fileList.get(position).getName());
        btn_code = (RelativeLayout) findViewById(R.id.btn_code);
        btn_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = GlobalListPDFActivity.fileList.get(position).getPath();
                Log.e("Path  ",path);
                try {
                    String imageEncode = EncodeBased64Binary.encodeFileToBase64Binary(path);
                    //Toast.makeText(getApplicationContext(),path, Toast.LENGTH_LONG).show();
                    file_content = imageEncode;
                    ProgressDialog progressDialog = new ProgressDialog(GlobalPDFActivity.this);
                    progressDialog.setTitle("Aditya Group");
                    progressDialog.setMessage("Please wait..!!");
                    progressDialog.show();

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

                                            //if (uriList.size() == multiplefirebasePath.size()) {
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                            Query query = databaseReference.child("Sites").orderByKey();
                                            databaseReference.keepSynced(true);
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot npsnapshot) {
                                                    try {
                                                        if (npsnapshot.exists()) {
                                                            for (DataSnapshot dataSnapshot : npsnapshot.getChildren()) {

                                                                if (dataSnapshot.child("id").getValue().toString().equals(site_id)) {

                                                                    DatabaseReference cineIndustryRef = databaseReference.child("Sites").child(dataSnapshot.getKey());
                                                                    Map<String, Object> map = new HashMap<>();
                                                                    map.put("file", uri.toString());
                                                                    Task<Void> voidTask = cineIndustryRef.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            new CToast(GlobalPDFActivity.this).simpleToast("File added successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                                                            Intent intent = new Intent(GlobalPDFActivity.this, Activity_Sites_Purchase_Summary.class);
                                                                            intent.putExtra("id", site_id);
                                                                            intent.putExtra("name", name);
                                                                            intent.putExtra("type", type);
                                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                            startActivity(intent);

                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            progressDialog.dismiss();
                                                                            new CToast(GlobalPDFActivity.this).simpleToast(e.getMessage(), Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        progressDialog.dismiss();
                                                        Log.e("exception   ", e.toString());
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    progressDialog.dismiss();
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
                                    progressDialog.dismiss();
                                    Toast.makeText(GlobalPDFActivity.this, "Please try later...", Toast.LENGTH_SHORT).show();
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
        pdfFileName = GlobalListPDFActivity.fileList.get(position).getName();

        pdfView.fromFile(GlobalListPDFActivity.fileList.get(position))
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

package com.flitzen.adityarealestate_new.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Permission;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class ViewAgreementPDF extends AppCompatActivity {

    String PDF_URL = "", PDF_PATH = "", site_name = "";
    Activity mActivity;
    ProgressDialog progressDialog;
    ImageView ivShare;
    @BindView(R.id.viewContent)
    LinearLayout viewContent;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    Toolbar toolbar;
    boolean onlyLoad = false;
    RemotePDFViewPager remotePDFViewPager;
    PDFPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_agreement_p_d_f);
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PDF_URL = getIntent().getStringExtra("pdf_url");
        Utils.showLog("====PDF_URL " + PDF_URL);
        mActivity = ViewAgreementPDF.this;

        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...");
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.whiteText1), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        if (getIntent().hasExtra("only_load")) {
            onlyLoad = true;
            progressDialog.show();
            remotePDFViewPager = new RemotePDFViewPager(ViewAgreementPDF.this, PDF_URL, new DownloadFile.Listener() {
                @Override
                public void onSuccess(String url, String destinationPath) {
                    try {
                        adapter = new PDFPagerAdapter(ViewAgreementPDF.this, FileUtil.extractFileNameFromURL(url));
                        remotePDFViewPager.setAdapter(adapter);
                        viewContent.removeAllViewsInLayout();
                        viewContent.addView(remotePDFViewPager, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        progressDialog.dismiss();
                    }catch (Exception e){
                        Utils.showToast(ViewAgreementPDF.this, "Something went wrong", R.color.msg_fail);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    progressDialog.dismiss();
                    Utils.showToast(ViewAgreementPDF.this, "Something went wrong", R.color.msg_fail);
                }

                @Override
                public void onProgressUpdate(int progress, int total) {

                }
            });
        } else {
            printStatementAPI();
            progressDialog.show();
        }

        //webView = findViewById(R.id.webView);

        ivShare = findViewById(R.id.ivShare);
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onlyLoad) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Permission.hasPermissions(mActivity, permissions)) {
                            downloadFile(PDF_URL);
                        } else {
                            Permission.requestPermissions(mActivity, permissions, 101);
                        }
                    } else {
                        downloadFile(PDF_URL);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Permission.hasPermissions(mActivity, permissions)) {
                            downloadFile(PDF_PATH);
                        } else {
                            Permission.requestPermissions(mActivity, permissions, 101);
                        }
                    } else {
                        downloadFile(PDF_PATH);
                    }
                }

            }
        });


        if (getIntent().hasExtra("name")) {
            site_name = getIntent().getStringExtra("name");
        }

    }

    private void printStatementAPI() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PDF_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("result") == 1) {
                        PDF_PATH = jsonObject.getString("file");

                        remotePDFViewPager = new RemotePDFViewPager(ViewAgreementPDF.this, PDF_PATH, new DownloadFile.Listener() {
                            @Override
                            public void onSuccess(String url, String destinationPath) {
                                try {
                                    adapter = new PDFPagerAdapter(ViewAgreementPDF.this, FileUtil.extractFileNameFromURL(url));
                                    remotePDFViewPager.setAdapter(adapter);
                                    viewContent.removeAllViewsInLayout();
                                    viewContent.addView(remotePDFViewPager, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    progressDialog.dismiss();
                                }catch (Exception e){
                                    System.out.println("========e view "+e.getMessage());
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                progressDialog.dismiss();
                                Utils.showToast(ViewAgreementPDF.this, "Something went wrong", R.color.msg_fail);
                            }

                            @Override
                            public void onProgressUpdate(int progress, int total) {

                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        new CToast(mActivity).simpleToast("Something went wrong!", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    new CToast(mActivity).simpleToast("Something went wrong!", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(mActivity);
        queue.add(stringRequest);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if (requestCode == 101) {
            downloadFile(PDF_URL);
        }*/
    }

    public void downloadFile(String fileURL) {

        try {
            File myFile = new File(new File(Utils.getItemDir()), "IV-" + site_name + ".pdf");
            myFile.createNewFile();
            new DownloadFileFromURL(myFile).execute(fileURL);
        } catch (Exception e) {
            System.out.println("==========e e View ");
            Utils.showToast(ViewAgreementPDF.this, "Something went wrong", R.color.msg_fail);
            e.printStackTrace();
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        File myFile;

        public DownloadFileFromURL(File myFile) {
            this.myFile = myFile;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);
                OutputStream output = new FileOutputStream(myFile);
                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
            /*progressDialog.dismiss();
            openPDFFile(myFile);*/
            shareFile(myFile);
           // shareFileWhatsApp(myFile);
        }
    }

    private void shareFileWhatsApp(File file) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
        intentShareFile.setPackage("com.whatsapp");
        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");
        startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }

    private void shareFile(File file) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
        //if you need
        //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
        //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");
        startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
        //overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }
}
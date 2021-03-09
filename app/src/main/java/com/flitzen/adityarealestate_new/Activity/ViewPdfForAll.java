package com.flitzen.adityarealestate_new.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flitzen.adityarealestate_new.Classes.Permission;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import java.io.File;
import java.net.URLConnection;
import java.util.List;

public class ViewPdfForAll extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    PDFView pdfView;
    Integer pageNumber = 0;
    String path;
    ImageView ivShare,ivEdit1;
    RelativeLayout btn_code;
    boolean onlyLoad = false;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf_for_all);
        path=getIntent().getStringExtra("path");
        init();
    }

    private void init() {
        pdfView = (PDFView) findViewById(R.id.pdfView);
        ivShare =  findViewById(R.id.ivShare);
        btn_code =  findViewById(R.id.btn_code);
        ivEdit1 =  findViewById(R.id.ivEdit1);
       // position = getIntent().getIntExtra("position_new", -1);

        ivEdit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File myFile = new File(path);
                if (onlyLoad) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Permission.hasPermissions(ViewPdfForAll.this, permissions)) {
                            shareFile(myFile);
                           // shareFileWhatsApp(myFile);
                        } else {
                            Permission.requestPermissions(ViewPdfForAll.this, permissions, 101);
                        }
                    } else {
                        shareFile(myFile);
                        //shareFileWhatsApp(myFile);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Permission.hasPermissions(ViewPdfForAll.this, permissions)) {
                            shareFile(myFile);
                         //   shareFileWhatsApp(myFile);
                        } else {
                            Permission.requestPermissions(ViewPdfForAll.this, permissions, 101);
                        }
                    } else {
                        shareFile(myFile);
                        //shareFileWhatsApp(myFile);
                    }
                }
            }
        });

        displayFromSdcard();

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

    private void displayFromSdcard() {
       // pdfFileName = ListPDFActivity.fileList.get(position).getName();


        pdfView.fromUri(Uri.parse(path))
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
        //setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }


    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e("File", String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
}
package com.flitzen.adityarealestate_new.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.flitzen.adityarealestate_new.Classes.TouchImageView;
import com.flitzen.adityarealestate_new.R;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class Activity_ImageViewer extends AppCompatActivity {
    TouchImageView ivGallery;
    Toolbar toolbar;
    ImageView ivShare;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        initUI();
    }

    private void initUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.whiteText1), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        ivShare = findViewById(R.id.ivShare);
        ivGallery = (TouchImageView) findViewById(R.id.ivGallery);
        final String url = getIntent().getStringExtra("img_url");
        Picasso.with(this)
                .load(url)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(ivGallery);
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImg(url);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(0, 0);
      //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
        return true;
    }

    private void shareImg(String img_url) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    Bitmap image = null;
                    try {
                        URL url = new URL(img_url);
                        image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    } catch(IOException e) {
                        System.out.println(e);
                    }
                    try {
                        File file = new File(getExternalCacheDir(),"share.png");
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }
}

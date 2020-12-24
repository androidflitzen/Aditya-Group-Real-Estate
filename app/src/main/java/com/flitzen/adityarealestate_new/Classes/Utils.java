package com.flitzen.adityarealestate_new.Classes;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.flitzen.adityarealestate_new.R;

import java.io.File;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

    public static final String APP_DIRECTORY = ".Aditya";
    public static final String MEDIA_FILE_PATH="Media";
    public static final String MEDIA_IMGS_SENT_FILE_PATH="Sent";
    public static final boolean SHOULD_USE_PALETTE = true;
    public static String getItemDir() {
        File dirReports = new File(Environment.getExternalStorageDirectory(),
                Utils.APP_DIRECTORY);
        if (!dirReports.exists()) {
            if (!dirReports.mkdirs()) {
                return null;
            }
        }
        return dirReports.getAbsolutePath();
    }


    public static boolean isNullOrEmpty(String value) {
        if (value == null)
            return true;
        value = value.trim();
        if (value.isEmpty())
            return true;
        if (value.equals("null"))
            return true;
        return false;
    }


    public static void showToast(Context context, String msg) {
        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        new CToast(context).simpleToast(msg, Toast.LENGTH_SHORT)
                .setBackgroundColor(R.color.colorPrimary)
                .show();
    }

    public static void showToast(Context context, String msg, int color) {
        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        new CToast(context).simpleToast(msg, Toast.LENGTH_SHORT)
                .setBackgroundColor(color)
                .show();
    }

    public static void showErrorToast(Context context) {
        //Toast.makeText(context, context.getString(R.string.msg_failed), Toast.LENGTH_SHORT).show();
        new CToast(context).simpleToast("Something went wrong ! Please try again.", Toast.LENGTH_SHORT)
                .setBackgroundColor(R.color.msg_fail)
                .show();
    }


    public static Retrofit getRetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebAPI.BASE_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    public static WebAPI getRetrofitClient(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(WebAPI.BASE_URL1)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WebAPI.class);
    }



    public static void showLog(String msg){
        Log.v("Aditya",msg);
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }
}

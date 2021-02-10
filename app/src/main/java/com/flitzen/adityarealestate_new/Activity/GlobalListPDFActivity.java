package com.flitzen.adityarealestate_new.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.flitzen.adityarealestate_new.Adapter.PDFAdapter;
import com.flitzen.adityarealestate_new.Adapter.PDFGlobleAdapter;
import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GlobalListPDFActivity extends AppCompatActivity {

    ListView lv_pdf;
    public static ArrayList<File> fileList = new ArrayList<File>();
    PDFGlobleAdapter obj_adapter;
    public static int REQUEST_PERMISSIONS = 1;
    boolean boolean_permission;
    File dir;
    String site_id = "",name = "",type = "";
    EditText edtSearch;
    ImageView imgClearSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_activity);
        site_id = getIntent().getStringExtra("site_id");
        name = getIntent().getStringExtra("name");
        type = getIntent().getStringExtra("type");
        init();

    }

    private void init() {

        lv_pdf = (ListView) findViewById(R.id.lv_pdf);
        edtSearch =  findViewById(R.id.edt_search);
        imgClearSearch =  findViewById(R.id.img_clear_search);
        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        fn_permission();


        lv_pdf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), GlobalPDFActivity.class);
                intent.putExtra("position_new", i);
                intent.putExtra("site_id",site_id);
                intent.putExtra("type",type);
                intent.putExtra("name",name);
                startActivity(intent);

                Log.e("Position", i + "");
            }
        });

        edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                    imgClearSearch.setVisibility(View.VISIBLE);
                else
                    imgClearSearch.setVisibility(View.GONE);

            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //filter(s.toString());
                obj_adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imgClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText(null);
                edtSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });
    }

    @SuppressLint("NewApi")
    public ArrayList<File> getfile(File dir) {
        try {
            File listFile[] = dir.listFiles();
            Arrays.sort(listFile, Comparator.comparingLong(File::lastModified).reversed());
            if (listFile != null && listFile.length > 0) {
                for (int i = 0; i < listFile.length; i++) {

                    if (listFile[i].isDirectory()) {
                        getfile(listFile[i]);

                    } else {

                        boolean booleanpdf = false;
                        if (listFile[i].getName().endsWith(".pdf")) {

                            for (int j = 0; j < fileList.size(); j++) {
                                if (fileList.get(j).getName().equals(listFile[i].getName())) {
                                    booleanpdf = true;
                                } else {

                                }
                            }

                            if (booleanpdf) {
                                booleanpdf = false;
                            } else {
                                fileList.add(listFile[i]);

                            }
                        }
                    }
                }
            }
            return fileList;
        }catch (Exception e){
            Utils.showToast(GlobalListPDFActivity.this, "Something went wrong", R.color.msg_fail);
        }
       return fileList;
    }


    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(GlobalListPDFActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {


            } else {
                ActivityCompat.requestPermissions(GlobalListPDFActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;

            getfile(dir);

            obj_adapter = new PDFGlobleAdapter(getApplicationContext(), fileList);
            lv_pdf.setAdapter(obj_adapter);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;
                getfile(dir);

                obj_adapter = new PDFGlobleAdapter(getApplicationContext(), fileList);
                lv_pdf.setAdapter(obj_adapter);

            } else {
                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

}

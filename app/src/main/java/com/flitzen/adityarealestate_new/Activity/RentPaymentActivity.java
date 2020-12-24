package com.flitzen.adityarealestate_new.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.flitzen.adityarealestate_new.Aditya;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.R;

import java.util.Calendar;

public class RentPaymentActivity extends AppCompatActivity {
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    int ATTACH_ITEM_POS = 0;
    int UPLOAD_REQUEST = 001, SELECT_PICTURE = 002, CAMERA_REQUEST = 003, SELECT_AUDIO = 004, CAMERA_PERMISSION_CODE = 005, FILE_REQUEST_CODE = 101;
    private Uri capturedImageURI;


    TextView tvUploadFile;
    ImageView ivUploadFile,uploadded_image;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);




        final EditText edt_paid_amount = (EditText) findViewById(R.id.edt_paid_amount);
        final EditText edt_remark = (EditText) findViewById(R.id.edt_remark);
        final TextView txt_date = (TextView) findViewById(R.id.txt_date);
        final TextView txt_time = (TextView) findViewById(R.id.txt_time);
        TextView txt_next_date = (TextView)findViewById(R.id.txt_next_date);
        final LinearLayout viewUploadFile = (LinearLayout) findViewById(R.id.viewUploadFile);
        viewUploadFile.setVisibility(View.VISIBLE);
        ImageView img_close = (ImageView) findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // dialog.dismiss();
            }
        });
        viewUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CharSequence[] items_name = {"Capture Image", "Select From Gallery", "Select PDF"};
                AlertDialog.Builder builder = new AlertDialog.Builder(RentPaymentActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(items_name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        ATTACH_ITEM_POS = item;

                        if (item == 0) {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, "New Picture");
                            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                            capturedImageURI = RentPaymentActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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

                        }


                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        ivUploadFile = (ImageView) findViewById(R.id.ivUploadFile);
        uploadded_image = (ImageView)findViewById(R.id.uploadded_image);
        ivUploadFile.setVisibility(View.VISIBLE);


        ivUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence[] items_name = {"Capture Image", "Select From Gallery", "Select PDF"};
                AlertDialog.Builder builder = new AlertDialog.Builder(RentPaymentActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(items_name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        ATTACH_ITEM_POS = item;

                        if (item == 0) {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, "New Picture");
                            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                            capturedImageURI = RentPaymentActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
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

                        }


                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        tvUploadFile = (TextView) findViewById(R.id.tvUploadFile);
        tvUploadFile.setVisibility(View.VISIBLE);
        tvUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence[] items_name = {"Capture Image", "Select From Gallery", "Select PDF"};
                AlertDialog.Builder builder = new AlertDialog.Builder(RentPaymentActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(items_name, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        ATTACH_ITEM_POS = item;

                        if (item == 0) {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, "New Picture");
                            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                            capturedImageURI = RentPaymentActivity.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageURI);
                            startActivityForResult(intent, CAMERA_REQUEST);

                        } else if (item == 1) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

                         /*   Intent intent = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, SELECT_PICTURE);
*/
                        }


                    }
                });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });
        TextView btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        Button btn_add_payment = (Button) findViewById(R.id.btn_add_payment);

        // edt_paid_amount.setText(rent_amount);
        txt_date.setText(Helper.getCurrentDate("dd/MM/yyyy"));
        txt_date.setTag(Helper.getCurrentDate("yyyy-MM-dd"));

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.pick_Date(RentPaymentActivity.this, txt_date);
            }
        });

        txt_next_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.pick_Date(RentPaymentActivity.this, txt_next_date);

            }
        });
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        txt_time.setText(hour + ":" + minute + ":00");

        txt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(RentPaymentActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                //dialog.dismiss();
            }
        });

        btn_add_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edt_paid_amount.getText().toString().equals("")) {
                    edt_paid_amount.setError("Enter paid amount");
                    edt_paid_amount.requestFocus();
                    return;
                } else if (txt_date.getText().toString().equals("")) {
                    new CToast(RentPaymentActivity.this).simpleToast("Select Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                } else if (txt_time.getText().toString().equals("")) {
                    new CToast(RentPaymentActivity.this).simpleToast("Select Time", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                }  else if (txt_next_date.getText().toString().equals("")) {
                    new CToast(RentPaymentActivity.this).simpleToast("Select Next Date", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                    return;
                }else {
                   // dialog.dismiss();
                    //addPaymentForRent(txt_date.getTag().toString(), txt_time.getText().toString(), edt_paid_amount.getText().toString(), Aditya.ID, edt_remark.getText().toString(),txt_next_date.getText().toString());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri selectedImageUri = data.getData();

                uploadded_image.setImageURI(selectedImageUri);



            }


        } else if (requestCode == CAMERA_REQUEST) {
            Uri selectedImageUri = data.getData();

            uploadded_image.setImageURI(selectedImageUri);
        }


    }
}

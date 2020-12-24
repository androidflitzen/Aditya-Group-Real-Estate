package com.flitzen.adityarealestate_new.Classes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.flitzen.adityarealestate_new.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    public static String LOAN_TYPE_EMI = "2";
    public static String LOAN_TYPE_SIMPLE = "1";
    public static String LOAN_TYPE_DAILY = "3";
    public static String RUPEE = "\u20B9";
    public static String DECIMAL_FORMATE = "#,##,###";

    public static int getRoundValue(double value) {
        double result = value + 0.5;
        return (int) result;
    }

    public static double GetEMI(double lAMOUNT, double lRATE, double lTENURE) {
        lRATE = lRATE / 100;
        double formula_calcultion_start = lAMOUNT * lRATE;
        double formula_calculate_emi = 1 + lRATE;
        double formula_calc_power = Math.pow(formula_calculate_emi, lTENURE);
        double formula_power = formula_calc_power - 1;

        double half_res = formula_calc_power / formula_power;
        return formula_calcultion_start * half_res;
        /*EMI = [P x R x (1+R)^N]/[(1+R)^N-1]*/
        /*return Math.pow(lAMOUNT*lRATE*(lRATE+1),lTENURE) / (Math.pow((lRATE+1),lTENURE)-1);*/
    }

    public static void Vibrate(Context context,int milis){
        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(milis);
    }

    public static String getFormatPrice(int price){
        DecimalFormat formatter = new DecimalFormat("#,##,###");
        return formatter.format(price);
    }

    public static String getMonth(String month) {
        String mm = "";
        if (month.equals("01")) {
            mm = "Jan";
        } else if (month.equals("02")) {
            mm = "Feb";
        } else if (month.equals("03")) {
            mm = "Mar";
        } else if (month.equals("04")) {
            mm = "Apr";
        } else if (month.equals("05")) {
            mm = "May";
        } else if (month.equals("06")) {
            mm = "Jun";
        } else if (month.equals("07")) {
            mm = "Jul";
        } else if (month.equals("08")) {
            mm = "Aug";
        } else if (month.equals("09")) {
            mm = "Sep";
        } else if (month.equals("10")) {
            mm = "Oct";
        } else if (month.equals("11")) {
            mm = "Nov";
        } else if (month.equals("12")) {
            mm = "Dec";
        }
        return mm;
    }

    public static String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String getCurrentDate(String formate) {
        return new SimpleDateFormat(formate).format(new Date());
    }

    public static void pick_Date(Activity activity, final TextView txt) {

        LayoutInflater localView = LayoutInflater.from(activity);
        View promptsView = localView.inflate(R.layout.dialog_datepick, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity);
        alertDialogBuilder.setView(promptsView);
        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        final DatePicker datePicker = (DatePicker) promptsView.findViewById(R.id.datePicker);
        //datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        //datePicker.init(1970, 0, 1, null);

        final Button btn_cancel = (Button) promptsView.findViewById(R.id.btn_cancel);
        final Button btn_ok = (Button) promptsView.findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dmonth = String.valueOf(datePicker.getMonth() + 1);
                String dday = String.valueOf(datePicker.getDayOfMonth());
                String dyear = String.valueOf(datePicker.getYear());
                alertDialog.dismiss();
                if (dmonth.length() == 1) {
                    dmonth = "0" + dmonth;
                }
                if (dday.length() == 1) {
                    dday = "0" + dday;
                }

                txt.setTag(dyear + "-" + dmonth + "-" + dday);
                txt.setText(dday + "-" + dmonth + "-" + dyear);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    public static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }

  /*  public static void getLogcatFromNameValuePair(List<NameValuePair> list){
        for (NameValuePair nvp : list){
            Log.e(nvp.getName(),"-"+nvp.getValue());
        }
    }*/

    public static Bitmap decodeUri(Uri selectedImage, Context context) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 512;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2);
    }

    public static String getFileExtension(File file) {
        if (file==null){
            return "";
        }
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    public static String getStringFromFile(File file){
        if (file==null){
            return "";
        }
        InputStream inputStream = null;//You can get an inputStream using any IO API
        ByteArrayOutputStream output = null;
        try {
            inputStream = new FileInputStream(file.getAbsolutePath());
            byte[] buffer = new byte[8192];
            int bytesRead;
            output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output64.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            output64.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (output!=null){
            return output.toString();
        }else {
            return "";
        }

    }
}

package com.flitzen.adityarealestate_new.Classes;

import android.content.Context;

import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.flitzen.adityarealestate_new.R;

public class CToast {

    Context context;
    Toast toast;
    LayoutInflater inflater;
    View toastRoot;
    CardView toastBg;

    public CToast(Context con) {
        this.context = con;
    }

    public CToast simpleToast(String message, int length) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        toastRoot = inflater.inflate(R.layout.simple_toast, null);

        try {
            TextView txtMessage = (TextView) toastRoot.findViewById(R.id.txt_simple_toast_message);
            toastBg = (CardView) toastRoot.findViewById(R.id.view_toast_bg);
            txtMessage.setText(message);

            toast = new Toast(context);
            toast.setView(toastRoot);
            toast.setDuration(length);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return this;
    }

    public CToast simpleToast(Spanned message, int length) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        toastRoot = inflater.inflate(R.layout.simple_toast, null);

        TextView txtMessage = (TextView) toastRoot.findViewById(R.id.txt_simple_toast_message);
        toastBg = (CardView) toastRoot.findViewById(R.id.view_toast_bg);
        txtMessage.setText(message);

        toast = new Toast(context);
        toast.setView(toastRoot);
        toast.setDuration(length);

        return this;
    }

    public CToast setBackgroundColor(int color) {
        toastBg.setCardBackgroundColor(context.getResources().getColor(color));
        return this;
    }

    public CToast setGravityCenter() {
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        return this;
    }

    public CToast show() {
        toast.show();
        return this;
    }
}


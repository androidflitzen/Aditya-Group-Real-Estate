package com.flitzen.adityarealestate_new.Classes;

import android.util.Patterns;
import android.widget.EditText;

public class Validation {
    private final static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public static boolean isEmpty(EditText editText) {
        if (editText.getText().toString().trim().length() < 1) {
            editText.setError("required");
            return true;
        }
        return false;
    }

    public static boolean isValidEmail(EditText editText) {
        if (Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches()) {
            return true;
        }
        editText.setError("please enter valid email");
        return false;
    }

    public static boolean checkPassword(EditText editText1, EditText editText2) {
        if (editText1.getText().toString().equals(editText2.getText().toString())) {
            return true;
        }
        editText2.setError("please enter same password");
        return false;
    }
}

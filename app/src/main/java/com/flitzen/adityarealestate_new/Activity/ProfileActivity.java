package com.flitzen.adityarealestate_new.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.SharePref;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.ivback)
    ImageView ivback;
    @BindView(R.id.txt_my_profile_name)
    TextView txtName;
    @BindView(R.id.txt_my_profile_password)
    TextView txtPassword;
    @BindView(R.id.txt_my_profile_email)
    TextView txtEmail;
    @BindView(R.id.cardChangePassword)
    CardView cardChangePassword;
    @BindView(R.id.relEditPass)
    RelativeLayout relEditPass;
    LinearLayout linPassword;

    SharedPreferences sharedPreferences;
    ProgressDialog prd;

    AlertDialog dialog_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtPassword = findViewById(R.id.txt_my_profile_password);
        ivback = findViewById(R.id.ivback);
        txtName = findViewById(R.id.txt_my_profile_name);
        cardChangePassword = findViewById(R.id.cardChangePassword);
        txtEmail = findViewById(R.id.txt_my_profile_email);
        linPassword = findViewById(R.id.linPassword);
        relEditPass = findViewById(R.id.relEditPass);

        relEditPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePasswordDialog();
            }
        });

        sharedPreferences = SharePref.getSharePref(ProfileActivity.this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (sharedPreferences.getString(SharePref.gstType, "").equals(getResources().getString(R.string.simple_user))) {
            linPassword.setVisibility(View.VISIBLE);
            //cardChangePassword.setVisibility(View.VISIBLE);
            relEditPass.setVisibility(View.VISIBLE);
            txtPassword.setText(sharedPreferences.getString(SharePref.password, ""));
        } else {
            linPassword.setVisibility(View.GONE);
            cardChangePassword.setVisibility(View.GONE);
            relEditPass.setVisibility(View.GONE);
        }

        if (user != null) {
            txtEmail.setText(user.getEmail());
        }


        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updatePasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
        View view = inflater.inflate(R.layout.dialog_update_password, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setView(view);
        dialog_main = builder.create();
        dialog_main.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        final EditText edtCurrentPass=(EditText)  view.findViewById(R.id.edtCurrentPass);
        edtCurrentPass.setText(sharedPreferences.getString(SharePref.password, ""));
        final EditText edtNewPass=(EditText)  view.findViewById(R.id.edtNewPass);

        Button btn_update_payment=(Button)  view.findViewById(R.id.btn_update_payment);

        ImageView btn_cancel=(ImageView)  view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_main.dismiss();
            }
        });
        btn_update_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtCurrentPass.getText().toString().equals("")){
                    edtCurrentPass.setError("required");
                }else if (edtNewPass.getText().toString().equals("")) {
                    edtNewPass.setError("required");
                }else {
                    updatePassword(edtNewPass.getText().toString());
                    dialog_main.dismiss();
                }

            }
        });

        dialog_main.show();
    }

    private void updatePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       // String newPassword = txtPassword.getText().toString().trim();

        if (newPassword.equals("")) {
            new CToast(ProfileActivity.this).simpleToast("Please enter password", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
        } else {
            showPrd();

            user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            hidePrd();
                            if (task.isSuccessful()) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(SharePref.password, newPassword);
                                editor.commit();
                                txtPassword.setText(sharedPreferences.getString(SharePref.password, ""));
                                new CToast(ProfileActivity.this).simpleToast("Password update successfully", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_success).show();
                                dialog_main.dismiss();
                            } else {
                                task.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();

                                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                        switch (errorCode) {

                                            case "ERROR_INVALID_CUSTOM_TOKEN":
                                                new CToast(ProfileActivity.this).simpleToast("The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                                                new CToast(ProfileActivity.this).simpleToast("The custom token corresponds to a different audience.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_INVALID_CREDENTIAL":
                                                new CToast(ProfileActivity.this).simpleToast("The supplied auth credential is malformed or has expired.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_INVALID_EMAIL":
                                                new CToast(ProfileActivity.this).simpleToast("The email address is badly formatted.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_WRONG_PASSWORD":
                                                new CToast(ProfileActivity.this).simpleToast("The password is invalid or the user does not have a password.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_USER_MISMATCH":
                                                new CToast(ProfileActivity.this).simpleToast("The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_REQUIRES_RECENT_LOGIN":
                                                // new CToast(ProfileActivity.this).simpleToast("This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                new CToast(ProfileActivity.this).simpleToast("Please Log in again before change this password.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                                                new CToast(ProfileActivity.this).simpleToast("An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_EMAIL_ALREADY_IN_USE":
                                                new CToast(ProfileActivity.this).simpleToast("The email address is already in use by another account.   ", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                                                new CToast(ProfileActivity.this).simpleToast("This credential is already associated with a different user account.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_USER_DISABLED":
                                                new CToast(ProfileActivity.this).simpleToast("The user account has been disabled by an administrator.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_USER_TOKEN_EXPIRED":
                                                new CToast(ProfileActivity.this).simpleToast("The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_USER_NOT_FOUND":

                                                new CToast(ProfileActivity.this).simpleToast("There is no user record corresponding to this identifier. The user may have been deleted.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_INVALID_USER_TOKEN":
                                                new CToast(ProfileActivity.this).simpleToast("The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_OPERATION_NOT_ALLOWED":
                                                new CToast(ProfileActivity.this).simpleToast("This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                            case "ERROR_WEAK_PASSWORD":
                                                new CToast(ProfileActivity.this).simpleToast("Password should be at least 6 characters", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                                break;

                                        }
                                    }
                                });
                            }
                        }
                    });
        }
    }

    public void showPrd() {
        prd = new ProgressDialog(ProfileActivity.this);
        prd.setMessage("Please wait...");
        prd.setCancelable(false);
        prd.show();
    }

    public void hidePrd() {
        prd.dismiss();
    }

}
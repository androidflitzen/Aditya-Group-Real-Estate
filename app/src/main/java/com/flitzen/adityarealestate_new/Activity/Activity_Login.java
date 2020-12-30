package com.flitzen.adityarealestate_new.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flitzen.adityarealestate_new.Classes.API;
import com.flitzen.adityarealestate_new.Classes.CToast;
import com.flitzen.adityarealestate_new.Classes.Helper;
import com.flitzen.adityarealestate_new.Classes.SharePref;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class Activity_Login extends AppCompatActivity {

    EditText edtUserName;
    EditText edtPassword;
    TextView txtLogin;
    ProgressBar progressBar,progress_bar_google;
    ImageView imgVisiblePassword;
    RelativeLayout relGoogleSignin,relGoogleSignin1;
    SharedPreferences sharedPreferences;
    private static final int RC_SIGN_IN = 123;
    boolean isPasswordChecked = false;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth mAuthNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuthNew = FirebaseAuth.getInstance();
        edtUserName = (EditText) findViewById(R.id.edt_login_username);
        edtPassword = (EditText) findViewById(R.id.edt_login_password);
        txtLogin = (TextView) findViewById(R.id.txt_login_btn);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        imgVisiblePassword = (ImageView) findViewById(R.id.img_login_visible_password1);
        sharedPreferences = SharePref.getSharePref(Activity_Login.this);
        relGoogleSignin = findViewById(R.id.relGoogleSignin);
        relGoogleSignin1 = findViewById(R.id.relGoogleSignin1);
        progress_bar_google = findViewById(R.id.progress_bar_google);
        relGoogleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relGoogleSignin1.setVisibility(View.GONE);
                progress_bar_google.setVisibility(View.VISIBLE);
                signIn();

            }
        });



        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtPassword.getText().toString().trim().length() == 0)
                    imgVisiblePassword.setVisibility(View.GONE);
                else
                    imgVisiblePassword.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgVisiblePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPasswordChecked) {
                    isPasswordChecked = true;
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgVisiblePassword.setImageResource(R.drawable.ic_visiblity);
                } else {
                    isPasswordChecked = false;
                    edtPassword.setInputType(129);
                    imgVisiblePassword.setImageResource(R.drawable.ic_visibility_off);
                }
            }
        });

        edtPassword.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edtPassword.getWindowToken(), 0);
                            return true;
                        }
                        return false;
                    }
                });



       /* edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtPassword.getText().toString().trim().length() == 0)
                    imgVisiblePassword.setVisibility(View.GONE);
                else
                    imgVisiblePassword.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgVisiblePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPasswordChecked) {
                    isPasswordChecked = true;
                    edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imgVisiblePassword.setImageResource(R.drawable.ic_visiblity);
                } else {
                    isPasswordChecked = false;
                    edtPassword.setInputType(129);
                    imgVisiblePassword.setImageResource(R.drawable.ic_visibility_off);
                }
            }
        });*/

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginClick();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }


    public void onLoginClick() {
        if (edtUserName.getText().toString().trim().isEmpty()) {
            edtUserName.setError("Enter Email");
            edtUserName.requestFocus();
            return;
        } else if (!edtUserName.getText().toString().trim().matches(Helper.emailPattern)) {
            edtUserName.setError("Enter Valid Email");
            edtUserName.requestFocus();
            return;
        } else if (edtPassword.getText().toString().trim().isEmpty()) {
            edtPassword.setError("Enter password");
            edtPassword.requestFocus();
            return;
        } else {

            progressBar.setVisibility(View.VISIBLE);
            txtLogin.setVisibility(View.GONE);

            mAuthNew.signInWithEmailAndPassword(edtUserName.getText().toString(),edtPassword.getText().toString())
                    .addOnCompleteListener(Activity_Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.e("task   ",task.toString());

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean(SharePref.isLoggedIn, true);
                                editor.putString(SharePref.userName, edtUserName.getText().toString());
                                editor.putString(SharePref.password, edtPassword.getText().toString());
                                editor.putString(SharePref.gstType, getString(R.string.simple_user));
                                editor.commit();

                                startActivity(new Intent(Activity_Login.this, Activity_Home.class));
                                finish();
                                overridePendingTransition(0, 0);
                              //  overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                            }
                            else {
                                new CToast(Activity_Login.this).simpleToast("Invalid Email or Password", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                progressBar.setVisibility(View.GONE);
                                txtLogin.setVisibility(View.VISIBLE);

                            }
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
        else {
            progress_bar_google.setVisibility(View.GONE);
            relGoogleSignin1.setVisibility(View.VISIBLE);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                progress_bar_google.setVisibility(View.GONE);
                                if (user.getEmail().equals("sanjaypatel989891@gmail.com") || user.getEmail().equals("bhavin.solanki@flitzen.in") || user.getEmail().equals("darshan@flitzen.co.uk")
                                        || user.getEmail().equals("dhruv.flitzen@gmail.com") || user.getEmail().equals("android@flitzen.co.uk")) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean(SharePref.isLoggedIn, true);
                                    editor.putString(SharePref.userName, user.getDisplayName());
                                    editor.putString(SharePref.gstType, getString(R.string.google));

                                    editor.commit();
                                        startActivity(new Intent(Activity_Login.this, Activity_Home.class));
                                    finish();
                                    overridePendingTransition(0, 0);
                                 //   overridePendingTransition(R.anim.feed_in, R.anim.feed_out);
                                } else {
                                    new CToast(Activity_Login.this).simpleToast("Invalid User", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                                    relGoogleSignin1.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            new CToast(Activity_Login.this).simpleToast("Something went wrong! Please try again", Toast.LENGTH_SHORT).setBackgroundColor(R.color.msg_fail).show();
                            relGoogleSignin1.setVisibility(View.VISIBLE);
                            progress_bar_google.setVisibility(View.GONE);
                        }

                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
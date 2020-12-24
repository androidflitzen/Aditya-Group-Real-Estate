package com.flitzen.adityarealestate_new.Activity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.flitzen.adityarealestate_new.Classes.Utils;
import com.flitzen.adityarealestate_new.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoogleDriveUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Google drive";
    private static final String SIGN_IN = "Sign In";
    private static final String DOWNLOAD_FILE = "Download file";

    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_OPEN_ITEM = 1;
    private static final int REQUEST_WRITE_STORAGE = 112;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;


    private GoogleSignInAccount signInAccount;
    private Set<Scope> requiredScopes;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    private OpenFileActivityOptions openOptions;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private File storageDir;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_drive);
        ButterKnife.bind(this);

        initialize();
        requestPermission();
        signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        btnSubmit.setOnClickListener(this);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
            btnSubmit.setText(DOWNLOAD_FILE);
        } else {
            btnSubmit.setText(SIGN_IN);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .build();
        mGoogleApiClient.connect();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.showLog("== data "+data.getData());
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    Task<GoogleSignInAccount> getAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                    if (getAccountTask.isSuccessful()) {
                        initializeDriveClient(getAccountTask.getResult());
                        showMessage("Sign in successfully.");
                        btnSubmit.setText(DOWNLOAD_FILE);
                    } else {
                        showMessage("Sign in failed.");
                    }
                } else {
                    showMessage("Sign in failed.");
                }
                break;
            case REQUEST_CODE_OPEN_ITEM:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    mOpenItemTaskSource.setResult(driveId);
                } else {
                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }
                break;
        }
    }


    private void initialize() {

        requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);

        openOptions = new OpenFileActivityOptions.Builder()
                .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "application/pdf"))
                .setActivityTitle("Select file")
                .build();
    }

    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSubmit) {
            String text = (String) ((Button) view).getText();
            if (text.equals(SIGN_IN)) {
                signIn();
            } else {
                onDriveClientReady();
            }
        }
    }

    private void signIn() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE)
                .requestScopes(Drive.SCOPE_APPFOLDER)
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
        startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private void onDriveClientReady() {
        mOpenItemTaskSource = new TaskCompletionSource<>();
        mDriveClient.newOpenFileActivityIntentSender(openOptions)
                .continueWith(new Continuation<IntentSender, Void>() {
                    @Override
                    public Void then(@NonNull Task<IntentSender> task) throws Exception {
                        startIntentSenderForResult(
                                task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                        return null;
                    }
                });

        Task<DriveId> tasks = mOpenItemTaskSource.getTask();
        tasks.addOnSuccessListener(this,
                new OnSuccessListener<DriveId>() {
                    @Override
                    public void onSuccess(DriveId driveId) {
                        retrieveContents(driveId.asDriveFile());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("File not selected.");
                    }
                });
    }

    private void retrieveContents(final DriveFile file) {
        // [START open_file]
        final Task<DriveContents> openFileTask = mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
        // [END open_file]
        // [START read_contents]
        openFileTask.continueWithTask(new Continuation<DriveContents, Task<Void>>() {
            @Override
            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                DriveContents contents = task.getResult();
                Log.v(TAG, "File name : " + contents.getDriveId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    InputStream input = contents.getInputStream();

                    try {
                        File file = new File(Utils.getItemDir(), "drive.pdf");
                        Log.v(TAG, storageDir + "");
                        OutputStream output = new FileOutputStream(file);
                        try {
                            try {
                                byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                int read;

                                while ((read = input.read(buffer)) != -1) {
                                    output.write(buffer, 0, read);
                                }
                                output.flush();
                            } finally {
                                output.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                showMessage("Download file successfully.");
                return mDriveResourceClient.discardContents(contents);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Unable to download file.");
                    }
                });
        // [END read_contents]
    }

    private void requestPermission() {
        String dirPath = getFilesDir().getAbsolutePath() + File.separator + "PDF";
        storageDir = new File(dirPath);
        if (!storageDir.exists())
            storageDir.mkdirs();
    }
}

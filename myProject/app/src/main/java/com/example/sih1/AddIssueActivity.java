package com.example.sih1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddIssueActivity extends AppCompatActivity {

    String categoryName, Description;
    ImageView ivIssue;
    Button btnAddIssue;
    EditText etIssueDescription;
    private static final int galleryPick = 1;
    private Uri ImageUri;
    private String saveCurrDate, saveCurrTime;
    private String IssueRandomKey;
    private StorageReference IssueImagesRef;
    private String downloadImageUrl;
    private DatabaseReference issueRef, userRef;
    private ProgressDialog loadingBar;

    private String uName, uPhone, uId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_issue);

        btnAddIssue = findViewById(R.id.btnAddIssue);
        ivIssue = findViewById(R.id.ivIssue);
        etIssueDescription = findViewById(R.id.etIssueDescription);

        loadingBar = new ProgressDialog(this);

        IssueImagesRef = FirebaseStorage.getInstance().getReference().child("Issue Images");

        issueRef = FirebaseDatabase.getInstance().getReference().child("Issues");

        userRef = FirebaseDatabase.getInstance().getReference().child("User");

        ivIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnAddIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateIssueData();
            }
        });

    }

    private void validateIssueData() {
        Description = etIssueDescription.getText().toString();

        if(TextUtils.isEmpty(Description)){
            Toast.makeText(this, "Add product description", Toast.LENGTH_SHORT).show();
        }
        else{
            StoreIssueInfo();
        }
    }

    private void StoreIssueInfo() {
        loadingBar.setTitle("Reporting New Issue");
        loadingBar.setMessage("Please wait while we are reporting the issue");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrDate = currDate.format(calendar.getTime());

        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrTime = currTime.format(calendar.getTime());

        IssueRandomKey = saveCurrDate + saveCurrTime;

        final StorageReference filePath = IssueImagesRef.child(ImageUri.getLastPathSegment() + IssueRandomKey
                + ".jpg");

        final UploadTask uploadTask= filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg = e.toString();
                Toast.makeText(AddIssueActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();

                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddIssueActivity.this, "Issue image uploaded successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AddIssueActivity.this, "got image url successfully", Toast.LENGTH_SHORT).show();
                            saveIssueInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == galleryPick && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            ivIssue.setImageURI(ImageUri);
        }
    }

    private void saveIssueInfoToDatabase(){
        HashMap<String, Object> issueMap = new HashMap<>();
        issueMap.put("issueID", IssueRandomKey);
        issueMap.put("date", saveCurrDate);
        issueMap.put("time", saveCurrTime);
        issueMap.put("description", Description);
        issueMap.put("image", downloadImageUrl);
        issueMap.put("category", categoryName);
        issueMap.put("issueState", "Not solved");
        issueMap.put("userName", uName);
        issueMap.put("userPhone", uPhone);

        issueRef.child(IssueRandomKey).updateChildren(issueMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Intent intent = new Intent(AddIssueActivity.this, UserHomeActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AddIssueActivity.this, "Issue Reported successfully", Toast.LENGTH_SHORT).show();

                        }
                        else {

                            loadingBar.dismiss();
                            String msg = task.getException().toString();
                            Toast.makeText(AddIssueActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}

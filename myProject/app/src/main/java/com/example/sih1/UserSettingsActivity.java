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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;


import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserSettingsActivity extends AppCompatActivity {

    private Button btnSettingsSecurityQuestions;
    CircleImageView settingProfileImage;
    EditText edSettingsPhoneNo,edSettingsFullName,edSettingAddress;
    TextView tvProfileImageChange,tvCloseSettings,tvUpdateSettings;
    private Uri imageUri;
    private String myUrl =  "";     // in this we gonna store image url
    private StorageTask uploadTask;
    private StorageReference storageProfilePictureRef;
    private  String checker = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile picture");

        settingProfileImage = findViewById(R.id.settingProfileImage);

        edSettingAddress = findViewById(R.id.edSettingAddress);
        edSettingsFullName = findViewById(R.id.edSettingsFullName);
        edSettingsPhoneNo = findViewById(R.id.edSettingsPhoneNo);

        tvProfileImageChange = findViewById(R.id.tvProfileImageChange);
        tvCloseSettings = findViewById(R.id.tvCloseSettings);
        tvUpdateSettings = findViewById(R.id.tvUpdateSettings);

        btnSettingsSecurityQuestions = findViewById(R.id.btnSettingsSecurityQuestions);
        btnSettingsSecurityQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettingsActivity.this, PasswordResetActivity.class);
                intent.putExtra("check","settings");
                startActivity(intent);
            }
        });


        userInfoDisplay(settingProfileImage,edSettingsFullName,edSettingsPhoneNo,edSettingAddress);

        tvCloseSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });

        tvUpdateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("clicked")){
                    //if this btn is clicked then we gonna call a method userInfoSaved
                    userInfoSaved();

                }else{
                    updateOnlyUserInfo();
                }
            }
        });

        tvProfileImageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // in this we gonna allow the user to change the profile pic
                checker = "clicked";

                // in this we gonna allow the user to use crop image feature
                // so we gonna use the autherHub library
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(UserSettingsActivity.this);
            }
        });

    }

    private void updateOnlyUserInfo() {
        // in this we are not going to update the profile image of the user because the user has not clicked on it

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("name",edSettingsFullName.getText().toString());
        userMap.put("address",edSettingAddress.getText().toString());
        userMap.put("phone",edSettingsPhoneNo.getText().toString());
        ref.child(CurrentUser.currentOnlineUser.getPhone()).updateChildren(userMap);


        startActivity(new Intent(UserSettingsActivity.this, UserHomeActivity.class));
        Toast.makeText(UserSettingsActivity.this, "Profile Info updated Successfully", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data !=null){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            // now we have to store this result which is basically a image uri to String variable imageUri
            imageUri = result.getUri();

            // now we have to display it to the imageView also so the user can see which image they has selected
            settingProfileImage.setImageURI(imageUri);
        }else{
            // if there any error occurs now we gonna display a tosat to the user'
            Toast.makeText(this, "Error : try again", Toast.LENGTH_SHORT).show();
            //refreshing the setting Activity
            startActivity(new Intent(UserSettingsActivity.this,UserSettingsActivity.class));
            finish();

        }
    }

    private void userInfoSaved() {
        // in this we gonna upddate the whole info of the settings Activity including image
        if(TextUtils.isEmpty(edSettingsFullName.getText().toString())){
            Toast.makeText(this, "Name is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(edSettingAddress.getText().toString())){
            Toast.makeText(this, "Address is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(edSettingsPhoneNo.getText().toString())){
            Toast.makeText(this, "Phone Number is Mandatory", Toast.LENGTH_SHORT).show();
        }else if(checker.equals("clicked")){
            // now we gonna create a method to upload the image
            uploadImage();
        }


    }

    private void uploadImage() {
        //creating progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please Wait,while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){
            final StorageReference fileRef =  storageProfilePictureRef.child(CurrentUser.currentOnlineUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        myUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");

                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("name",edSettingsFullName.getText().toString());
                        userMap.put("address",edSettingAddress.getText().toString());
                        userMap.put("phone",edSettingsPhoneNo.getText().toString());
                        userMap.put("image",myUrl);

                        ref.child(CurrentUser.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(UserSettingsActivity.this,UserHomeActivity.class));

                        Toast.makeText(UserSettingsActivity.this, "Profile updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();

                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(UserSettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }
    }



    private void userInfoDisplay(final CircleImageView settingProfileImage, final EditText edSettingsFullName, final EditText edSettingsPhoneNo, final EditText edSettingAddress) {
        // now we need to create a database ref for the specific user who id logged in
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(CurrentUser.currentOnlineUser.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    // if the user exists
                    if(dataSnapshot.child("image").exists()){
                        // if the image exist then fetch and display the info of the user on the settings Activity
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(settingProfileImage);
                        edSettingsFullName.setText(name);
                        edSettingsPhoneNo.setText(phone);
                        edSettingAddress.setText(address);




                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}

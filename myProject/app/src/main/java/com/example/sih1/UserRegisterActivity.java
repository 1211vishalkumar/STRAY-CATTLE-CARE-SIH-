package com.example.sih1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserRegisterActivity extends AppCompatActivity {

    EditText etUsername, etPhnNUmber, etPassword;

    Button btnRegister;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        etUsername = findViewById(R.id.etUsername);
        etPhnNUmber = findViewById(R.id.etPhnNumber);
        etPassword = findViewById(R.id.etPassword);

        btnRegister = findViewById(R.id.btnRegister);

        mProgressDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }

    private void registerUser(){

        String name = etUsername.getText().toString();
        String phone = etPhnNUmber.getText().toString();
        String password = etPassword.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please write your name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please write your Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password not provided", Toast.LENGTH_SHORT).show();
        }
        else{
            mProgressDialog.setTitle("Creating Account");
            mProgressDialog.setMessage("Please wait while we are checking the credentials.");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            validatePhoneNumber(name, phone, password);
        }
    }


    private void validatePhoneNumber(final String name, final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("User").child(phone).exists())){

                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", password);
                    userdataMap.put("name", name);

                    RootRef.child("User").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(UserRegisterActivity.this, "Your account has been created",
                                                Toast.LENGTH_SHORT).show();
                                        mProgressDialog.dismiss();

                                        Intent intent = new Intent(UserRegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }else{
                                        mProgressDialog.dismiss();
                                        Toast.makeText(UserRegisterActivity.this, "Network error!!! Please Try again",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else{
                    Toast.makeText(UserRegisterActivity.this, "This" + phone + " number already exists.", Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();
                    Toast.makeText(UserRegisterActivity.this, "Try again using another phone number.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(UserRegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

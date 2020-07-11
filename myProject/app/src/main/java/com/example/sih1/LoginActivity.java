package com.example.sih1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sih1.Admin.AdminHomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    EditText etPhnNumber, etPassword;

    Button btnLogin;

    ProgressDialog mProgressDialog;

    TextView tvAdmin, tvNotAdmin, tvForgotPassword;

    CheckBox checkRememberMe;

     String parentDBName = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPhnNumber = findViewById(R.id.etPhnNumber);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);

        tvAdmin = findViewById(R.id.tvAdmin);
        tvNotAdmin = findViewById(R.id.tvNotAdmin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        checkRememberMe = findViewById(R.id.ckeckRememberMe);

        mProgressDialog = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });

        tvAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setText("Login Admin");
                tvAdmin.setVisibility(View.INVISIBLE);
                tvNotAdmin.setVisibility(View.VISIBLE);
                parentDBName = "Admins";
            }
        });

        tvNotAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLogin.setText("Login");
                tvAdmin.setVisibility(View.VISIBLE);
                tvNotAdmin.setVisibility(View.INVISIBLE);
                parentDBName = "User";
            }
        });
    }
    private void LoginUser(){
        String phone = etPhnNumber.getText().toString();
        String password = etPassword.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please write your Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Password not provided", Toast.LENGTH_SHORT).show();
        }
        else {
            mProgressDialog.setTitle("Login Account");
            mProgressDialog.setMessage("Please wait while we are checking the credentials.");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if(checkRememberMe.isChecked()){
            Paper.book().write(CurrentUser.UserPhoneKey, phone);
            Paper.book().write(CurrentUser.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDBName).child(phone).exists()){
                    User usersData = dataSnapshot.child(parentDBName).child(phone).getValue(User.class);

                    if(usersData.getPhone().equals(phone)){
                        if(usersData.getPassword().equals(password)){
                            if(parentDBName.equals("Admins")){

                                Toast.makeText(LoginActivity.this, "Welcome Admin, you are logged in successfully", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else if(parentDBName.equals("User")){
                                Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, UserHomeActivity.class);

                                CurrentUser.currentOnlineUser = usersData;
                                startActivity(intent);
                                finish();

                            }
                        }
                        else{
                            mProgressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Oops!!! Password is incorrext", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + "number do not exist",
                            Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

package com.example.sih1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class PasswordResetActivity extends AppCompatActivity {

    private String check =""; // in order to receive he intent
    private TextView tvPageTitleResetPassword,tvTitleQuestionsResetPassword;
    private EditText edPhoneNoResetActivity,edQuestionResetPassword1,edQuestionResetPassword2;
    private Button btnVerifyResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        check = getIntent().getStringExtra("check");

        tvPageTitleResetPassword = findViewById(R.id.tvPageTitleResetPassword);
        tvTitleQuestionsResetPassword = findViewById(R.id.tvTitleQuestionsResetPassword);

        edPhoneNoResetActivity = findViewById(R.id.edPhoneNoResetActivity);
        edQuestionResetPassword1 = findViewById(R.id.edQuestionResetPassword1);
        edQuestionResetPassword2 = findViewById(R.id.edQuestionResetPassword2);

        btnVerifyResetPassword = findViewById(R.id.btnVerifyResetPassword);

    }

    @Override
    protected void onStart() {
        super.onStart();

        edPhoneNoResetActivity.setVisibility(View.GONE);

        // here we gonna differntiate whether the user has come here from the SignIn Activity or the Setting Activity
        if(check.equals("settings")){

            //then we goona change the fields accordingly
            tvPageTitleResetPassword.setText("Set Security Questions");
            tvTitleQuestionsResetPassword.setText("Please Answer the following Security Questions");
            btnVerifyResetPassword.setText("Save ");

            displayPreviousAnswers();

            btnVerifyResetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setAnswers();

                }
            });


        }else if(check.equals("login")){
            // if the user forget the password

            edPhoneNoResetActivity.setVisibility(View.VISIBLE);

            btnVerifyResetPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // here we gonna create a method to verify the user
                    verifyUser();
                }
            });

        }
    }

    private void setAnswers(){
        // here the user will anwer question1 and q 2
        String answer1 = edQuestionResetPassword1.getText().toString().toLowerCase();
        String answer2 = edQuestionResetPassword2.getText().toString().toLowerCase();

        if(edQuestionResetPassword1.equals("")){

            Toast.makeText(PasswordResetActivity.this, "Please answer this Q .", Toast.LENGTH_SHORT).show();

        }else if(edQuestionResetPassword2.equals("")){

            Toast.makeText(PasswordResetActivity.this, "Please answer this Q .", Toast.LENGTH_SHORT).show();

        }else{
            // here we gonna do our task
            //here we need to create a database reference
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("User")
                    .child(CurrentUser.currentOnlineUser.getPhone());

            HashMap<String,Object> userDataMap = new HashMap<>();
            userDataMap.put("answer1",answer1);
            userDataMap.put("answer2",answer2);

            ref.child("Security Questions").updateChildren(userDataMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                // now we goona tell the user that security questions set successfully
                                Toast.makeText(PasswordResetActivity.this, "Security questions saved successfully", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(PasswordResetActivity.this, UserHomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        }
    }

    // in this method we retrive the previous Answers answered bythe user
    private void displayPreviousAnswers(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("User")
                .child(CurrentUser.currentOnlineUser.getPhone());

        ref.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    String ans1 = dataSnapshot.child("answer1").getValue().toString();
                    String ans2 = dataSnapshot.child("answer2").getValue().toString();

                    edQuestionResetPassword1.setText(ans1);
                    edQuestionResetPassword2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void verifyUser() {

        final String phone = edPhoneNoResetActivity.getText().toString();
        final String answer1 = edQuestionResetPassword1.getText().toString().toLowerCase();
        final String answer2 = edQuestionResetPassword2.getText().toString().toLowerCase();

        if(!phone.equals("") && !answer1.equals("") && !answer2.equals("")){

            // now we gonna verfy the phone no ans and the 2 answers
            //ref
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("User")
                    .child(phone);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        // now we gonna retrive the phone no which inside the database
                        String mPhone = dataSnapshot.child("phone").getValue().toString();

                        // now we gonna check whether the user has already set the security questions or not
                        if(dataSnapshot.hasChild("Security Questions")){
                            //  now we gonna retrive both the answers
                            String ans1 = dataSnapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2 = dataSnapshot.child("Security Questions").child("answer2").getValue().toString();

                            if(!ans1.equals(answer1)){
                                Toast.makeText(PasswordResetActivity.this, "Your Answer to this Q.1 is incorrect ", Toast.LENGTH_SHORT).show();
                            }
                            else if(!ans2.equals(answer2)){
                                Toast.makeText(PasswordResetActivity.this, "Your Answer to this Q.2 is incorrect", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                // now we gonna allow the user to change the password
                                AlertDialog.Builder builder = new AlertDialog.Builder(PasswordResetActivity.this,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                                builder.setTitle(" New password");

                                final EditText newPassword = new EditText(PasswordResetActivity.this);
                                newPassword.setHint("Write new Password here");
                                builder.setView(newPassword);

                                // now we need 2 button
                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        if(!newPassword.getText().toString().equals("")){
                                            ref.child("password").setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(PasswordResetActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();

                                                                Intent intent = new Intent(PasswordResetActivity.this, LoginActivity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        dialog.cancel();

                                        Intent intent = new Intent(PasswordResetActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                });

                                builder.show();

                            }


                        }
                        else{

                            Toast.makeText(PasswordResetActivity.this, "You have not set the security question. please contact us", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(PasswordResetActivity.this, "This Phone is not registered", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{

            Toast.makeText(this, "Please Complete the form", Toast.LENGTH_SHORT).show();
        }

    }



}

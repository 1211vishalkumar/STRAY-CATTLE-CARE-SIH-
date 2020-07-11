package com.example.sih1.Authority;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sih1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthorityLogIn extends AppCompatActivity {

    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth ;
    Button btnLogIn;
    EditText etEmail,etPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_log_in);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);

        etEmail = findViewById(R.id.editTextTextEmailAddress2);
        etPass = findViewById(R.id.editTextTextPassword2);

        btnLogIn = findViewById(R.id.button3);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInAuthority();
            }
        });

    }

    private void logInAuthority() {
        final String email = etEmail.getText().toString();
        final String password = etPass.getText().toString();

        if(!email.equals("") && !password.equals("") ) {

            loadingBar.setTitle(" Authority Account SignIn");
            loadingBar.setMessage("please wait while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Task<AuthResult> enter_the_correct_credentials = mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                // now we gonna send the seller to the Authority Home Activity

                                Intent intent = new Intent(AuthorityLogIn.this, AuthorityHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                loadingBar.dismiss();
                                Toast.makeText(AuthorityLogIn.this, "Enter the correct credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
        else{

            Toast.makeText(this, " Please fill the required fields !!", Toast.LENGTH_SHORT).show();

        }
    }
}
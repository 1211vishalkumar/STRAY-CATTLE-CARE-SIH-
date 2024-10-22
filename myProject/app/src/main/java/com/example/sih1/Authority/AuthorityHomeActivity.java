package com.example.sih1.Authority;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sih1.MainActivity;
import com.example.sih1.R;
import com.google.firebase.auth.FirebaseAuth;

public class AuthorityHomeActivity extends AppCompatActivity {

    Button btnAuthLogout, btnAuthNewIssue, btnAuthSolvedIssue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_home);

        btnAuthLogout = findViewById(R.id.btnAuthLogout);

        btnAuthNewIssue = findViewById(R.id.btnAuthNewIssues);

        btnAuthSolvedIssue = findViewById(R.id.btnAuthSolvedIssues);



        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        btnAuthLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                Intent intent = new Intent(AuthorityHomeActivity.this, MainActivity.class);

                startActivity(intent);
                finish();
            }
        });

        btnAuthNewIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthorityHomeActivity.this, AuthNewIssueActivity.class);

                startActivity(intent);
            }
        });
    }
}

package com.example.sih1.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sih1.AdminSolvedIssuesActivity;
import com.example.sih1.MainActivity;
import com.example.sih1.R;

public class AdminHomeActivity extends AppCompatActivity {

    Button btnAdminLogout, btnAdminSolvedIssues, btnAdminNewIssues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        btnAdminLogout = findViewById(R.id.btnAdminLogout);
        btnAdminNewIssues = findViewById(R.id.btnAdminNewIssues);

        btnAdminSolvedIssues = findViewById(R.id.btnAdminSolvedIssues);

        btnAdminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnAdminNewIssues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminNewIssuesActivity.class);
                startActivity(intent);
            }
        });


        btnAdminSolvedIssues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, AdminSolvedIssuesActivity.class);
                startActivity(intent);
            }
        });
    }
}

package com.example.sih1.Authority;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sih1.R;
import com.google.firebase.database.DatabaseReference;

public class AuthNewIssueActivity extends AppCompatActivity {

    DatabaseReference newIssueRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_new_issue);
    }
}
package com.example.sih1.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.example.sih1.CurrentUser;
import com.example.sih1.Issue;
import com.example.sih1.IssueViewHolder;
import com.example.sih1.NewIssueViewHolder;
import com.example.sih1.R;
import com.example.sih1.UserHomeActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class AdminNewIssuesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference newIssueRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_issues);

        recyclerView = findViewById(R.id.rvNewIssues);

        newIssueRef = FirebaseDatabase.getInstance().getReference().child("Issues");

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Issue> options =
                new FirebaseRecyclerOptions.Builder<Issue>()
                        .setQuery(newIssueRef.orderByChild("issueState").equalTo("Not solved"), Issue.class)
                        .build();

        FirebaseRecyclerAdapter<Issue, NewIssueViewHolder> adapter =
                new FirebaseRecyclerAdapter<Issue, NewIssueViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull NewIssueViewHolder holder, int i, @NonNull final Issue model) {
                        holder.tvIssueUserName.setText(model.getUserName());
                        holder.tvIssueUserPhone.setText(model.getUserPhone());
                        holder.tvNewIssueState.setText(model.getIssueState());
                        holder.tvIssueAddSate.setText(model.getState());
                        holder.tvDateNTime.setText(model.getDate() + " " + model.getTime());
                        Picasso.get().load(model.getImage()).into(holder.ivNewIssueImage);
                    }

                    @NonNull
                    @Override
                    public NewIssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_issue, parent, false);
                        NewIssueViewHolder holder = new NewIssueViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
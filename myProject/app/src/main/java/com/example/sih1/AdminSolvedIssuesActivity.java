package com.example.sih1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminSolvedIssuesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private String type = "";

    DatabaseReference newIssueRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_solved_issues);

        recyclerView = findViewById(R.id.rvSolvedIssues);

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
                        .setQuery(newIssueRef.orderByChild("issueState").equalTo("Solved"), Issue.class)
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
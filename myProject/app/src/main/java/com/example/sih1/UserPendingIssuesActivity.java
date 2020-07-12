package com.example.sih1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class UserPendingIssuesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DatabaseReference newIssueRef;

    String str="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pending_issues);

        recyclerView = findViewById(R.id.rvUserNewIssues);

        str=CurrentUser.currentOnlineUser.getPhone();

        newIssueRef = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Issues");

        newIssueRef = newIssueRef.getRoot();

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

        FirebaseRecyclerAdapter<Issue, IssueViewHolder> adapter =
                new FirebaseRecyclerAdapter<Issue, IssueViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull IssueViewHolder holder, int i, @NonNull final Issue model) {
                        holder.tvIssueDescription.setText(model.getDescription());
                        holder.tvIssueState.setText(model.getIssueState());
                        Picasso.get().load(model.getImage()).into(holder.ivIssueImage);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String issueId = model.getIssueID();
                                CharSequence options[] = new CharSequence []{
                                        "Yes",
                                        "No"
                                };

                                AlertDialog.Builder builder= new AlertDialog.Builder(UserPendingIssuesActivity.this);
                                builder.setTitle("Want to Delete this issue ?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            deleteIssue(issueId);
                                        }
                                        else {

                                        }
                                    }
                                });

                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.issue, parent, false);
                        IssueViewHolder holder = new IssueViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteIssue(String productId){
        newIssueRef.child(productId).removeValue().addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UserPendingIssuesActivity.this, "This issue is deleted", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
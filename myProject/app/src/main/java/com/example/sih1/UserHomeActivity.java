package com.example.sih1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class UserHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private String type = "";

    DatabaseReference newIssueRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        Intent intent = getIntent();
        Bundle bundle= intent.getExtras();
        if(bundle != null){
            type = getIntent().getExtras().get("Admin").toString();
        }

        newIssueRef = FirebaseDatabase.getInstance().getReference().child("Issues");

        Paper.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Home");

        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!type.equals("Admin")){
                    Intent intent = new Intent(UserHomeActivity.this, AddIssueActivity.class);
                    startActivity(intent);

                }

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.user_name);
        CircleImageView profileImgView = headerView.findViewById(R.id.profile_image);

        if(!type.equals("Admin")){
            tvUserName.setText(CurrentUser.currentOnlineUser.getName());
            Picasso.get().load(CurrentUser.currentOnlineUser.getImage()).placeholder(R.drawable.registerimg).into(profileImgView);

        }

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Issue> options =
                new FirebaseRecyclerOptions.Builder<Issue>()
                        .setQuery(newIssueRef.orderByChild("userPhone").equalTo(CurrentUser.currentOnlineUser.getPhone()), Issue.class)
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

                                AlertDialog.Builder builder= new AlertDialog.Builder(UserHomeActivity.this);
                                builder.setTitle("Want to Delete this product ?");
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

    @Override
    public void onBackPressed(){
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("statementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.pendingIssues){
            if(!type.equals("Admin")){

                Intent intent = new Intent(UserHomeActivity.this, MainActivity.class);
                startActivity(intent);
            }


        }
        else if(id == R.id.logout){
            if(!type.equals("Admin")){

                Paper.book().destroy();

                Intent intent = new Intent(UserHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

        }
        else if(id == R.id.categories){
            if(!type.equals("Admin")){

                Intent intent = new Intent(UserHomeActivity.this, IssueCategoryActivity.class);
                startActivity(intent);
            }
        }
        else if(id == R.id.settings){
            if(!type.equals("Admin")){

                Intent intent = new Intent(UserHomeActivity.this, UserSettingsActivity.class);
                startActivity(intent);
            }


        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void deleteIssue(String productId){
        newIssueRef.child(productId).removeValue().addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(UserHomeActivity.this, "This issue is deleted", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
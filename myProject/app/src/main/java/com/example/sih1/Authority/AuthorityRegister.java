package com.example.sih1.Authority;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sih1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class AuthorityRegister extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    EditText etName,etPhone,etEmail,etPassword,etAddress;
    Button btnRegister,btnLogIn;

    Spinner spinner1, spinner2;

    private FirebaseAuth mAuth ;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_register);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);
        etName = findViewById(R.id.editTextTextPersonName);
        etPhone = findViewById(R.id.editTextPhone);
        etEmail = findViewById(R.id.editTextTextEmailAddress);
        etPassword = findViewById(R.id.editTextTextPassword);
        etAddress = findViewById(R.id.editTextTextPersonName2);

        btnRegister = findViewById(R.id.button1);
        btnLogIn = findViewById(R.id.button2);

        spinner1 = findViewById(R.id.spinnerType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);

        spinner1.setOnItemSelectedListener(this);

        spinner2 = findViewById(R.id.spinnerLocality);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.locality,
                android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner2.setOnItemSelectedListener(this);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthorityRegister.this, AuthorityLogIn.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //here we gonna create a method
                registerAuthority();
            }
        });


    }

    private void registerAuthority() {

        final String name = etName.getText().toString();
        final String phone = etPhone.getText().toString();
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String address = etAddress.getText().toString();

        final String type = String.valueOf(spinner1.getSelectedItem());

        final String locality = String.valueOf(spinner2.getSelectedItem());

        if(!name.equals("") && !phone.equals("") && !email.equals("") && !password.equals("") && !address.equals("")
        && !type.equals("") && !locality.equals("")){

            loadingBar.setTitle("Creating Authority Account");
            loadingBar.setMessage("please wait while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                // now we goona save the info in the firebase realtime database
                                final DatabaseReference rootRef;
                                rootRef = FirebaseDatabase.getInstance().getReference();

                                String aid = mAuth.getCurrentUser().getUid();

                                //creating a hashmap for storing the info in the databse
                                HashMap<String,Object> AuthorityMap = new HashMap<>();
                                AuthorityMap.put("aid",aid);
                                AuthorityMap.put("phone",phone);
                                AuthorityMap.put("email",email);
                                AuthorityMap.put("address",address);
                                AuthorityMap.put("name",name);
                                AuthorityMap.put("password",password);

                                rootRef.child("Authority").child(locality).child(type).updateChildren(AuthorityMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    loadingBar.dismiss();
                                                    Toast.makeText(AuthorityRegister.this, "You have registered as Authority Successfully", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(AuthorityRegister.this, AuthorityHomeActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });

                            }


                        }
                    });

        }
        else{
            Toast.makeText(this, "Please enter the required fields correctly", Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String text = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

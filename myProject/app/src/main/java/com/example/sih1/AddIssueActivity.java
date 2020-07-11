package com.example.sih1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddIssueActivity extends AppCompatActivity {

    private Spinner country_Spinner;
    private Spinner state_Spinner;
    private Spinner city_Spinner;

    private String status="Not solved";

    private ArrayAdapter<Country> countryArrayAdapter;
    private ArrayAdapter<State> stateArrayAdapter;
    private ArrayAdapter<City> cityArrayAdapter;

    private ArrayList<Country> countries;
    private ArrayList<State> states;
    private ArrayList<City> cities;

    String country="";
    String state="";
    String city="";

    String categoryName, Description;
    ImageView ivIssue;
    Button btnAddIssue;
    EditText etIssueDescription;
    private static final int galleryPick = 1;
    private Uri ImageUri;
    private String saveCurrDate, saveCurrTime;
    private String IssueRandomKey;
    private StorageReference IssueImagesRef;
    private String downloadImageUrl;
    private DatabaseReference issueRef, userRef;
    private ProgressDialog loadingBar;

    private String uName, uPhone, uId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_issue);

        btnAddIssue = findViewById(R.id.button5);
        ivIssue = findViewById(R.id.ivIssue);
        etIssueDescription = findViewById(R.id.etIssueDescription);

        loadingBar = new ProgressDialog(this);

        IssueImagesRef = FirebaseStorage.getInstance().getReference().child("Issue Images");

        issueRef = FirebaseDatabase.getInstance().getReference().child("Issues");

        userRef = FirebaseDatabase.getInstance().getReference().child("User");

        uName= CurrentUser.currentOnlineUser.getName();
        uPhone = CurrentUser.currentOnlineUser.getPhone();

        initializeUI();

        ivIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });



        btnAddIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateIssueData();
            }
        });

    }



    private void validateIssueData() {
        Description = etIssueDescription.getText().toString();

        if(TextUtils.isEmpty(Description) || TextUtils.isEmpty(country) || TextUtils.isEmpty(state) || TextUtils.isEmpty(city)){
            Toast.makeText(this, "Add all  description", Toast.LENGTH_SHORT).show();
        }
        else{
            StoreIssueInfo();
        }
    }

    private void StoreIssueInfo() {
        loadingBar.setTitle("Reporting New Issue");
        loadingBar.setMessage("Please wait while we are reporting the issue");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrDate = currDate.format(calendar.getTime());

        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrTime = currTime.format(calendar.getTime());

        IssueRandomKey = saveCurrDate + saveCurrTime;

        final StorageReference filePath = IssueImagesRef.child(ImageUri.getLastPathSegment() + IssueRandomKey
                + ".jpg");

        final UploadTask uploadTask= filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String msg = e.toString();
                Toast.makeText(AddIssueActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();

                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddIssueActivity.this, "Issue image uploaded successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if(!task.isSuccessful()){
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AddIssueActivity.this, "got image url successfully", Toast.LENGTH_SHORT).show();
                            saveIssueInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == galleryPick && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            ivIssue.setImageURI(ImageUri);
        }
    }

    private void saveIssueInfoToDatabase(){
        HashMap<String, Object> issueMap = new HashMap<>();
        issueMap.put("issueID", IssueRandomKey);
        issueMap.put("date", saveCurrDate);
        issueMap.put("time", saveCurrTime);
        issueMap.put("description", Description);
        issueMap.put("image", downloadImageUrl);
        issueMap.put("category", categoryName);
        issueMap.put("issueState", status);
        issueMap.put("userName", uName);
        issueMap.put("userPhone", uPhone);
        issueMap.put("country",country);
        issueMap.put("state",state);
        issueMap.put("city",city);

        issueRef.child(IssueRandomKey).updateChildren(issueMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Intent intent = new Intent(AddIssueActivity.this, UserHomeActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AddIssueActivity.this, "Issue Reported successfully", Toast.LENGTH_SHORT).show();

                        }
                        else {

                            loadingBar.dismiss();
                            String msg = task.getException().toString();
                            Toast.makeText(AddIssueActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void initializeUI() {
        country_Spinner =  findViewById(R.id.spinner2);
        state_Spinner =  findViewById(R.id.spinner3);
        city_Spinner = findViewById(R.id.spinner);

        countries = new ArrayList<>();
        states = new ArrayList<>();
        cities = new ArrayList<>();

        createLists();

        countryArrayAdapter = new ArrayAdapter<Country>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,countries);
        countryArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        country_Spinner.setAdapter(countryArrayAdapter);

        stateArrayAdapter = new ArrayAdapter<State>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, states);
        stateArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        state_Spinner.setAdapter(stateArrayAdapter);

        cityArrayAdapter = new ArrayAdapter<City>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, cities);
        cityArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        city_Spinner.setAdapter(cityArrayAdapter);

        country_Spinner.setOnItemSelectedListener(country_listener);
        state_Spinner.setOnItemSelectedListener(state_listener);
        city_Spinner.setOnItemSelectedListener(city_listener);
    }

    private AdapterView.OnItemSelectedListener country_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {

                country = parent.getItemAtPosition(position).toString();

                final Country country = (Country) country_Spinner.getItemAtPosition(position);
                Log.d("SpinnerCountry", "onItemSelected: country: "+country.getCountryID());
                ArrayList<State> tempStates = new ArrayList<>();

                tempStates.add(new State(0, new Country(0, "Choose your Country"), "Choose your State"));

                for (State singleState : states) {
                    if (singleState.getCountry().getCountryID() == country.getCountryID()) {
                        tempStates.add(singleState);
                    }
                }

                stateArrayAdapter = new ArrayAdapter<State>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, tempStates);
                stateArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                state_Spinner.setAdapter(stateArrayAdapter);
            }

            cityArrayAdapter = new ArrayAdapter<City>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, new ArrayList<City>());
            cityArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            city_Spinner.setAdapter(cityArrayAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener state_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {

                state = parent.getItemAtPosition(position).toString();

                final State state = (State) state_Spinner.getItemAtPosition(position);
                Log.d("SpinnerCountry", "onItemSelected: state: "+state.getStateID());
                ArrayList<City> tempCities = new ArrayList<>();

                Country country = new Country(0, "Choose your Country");
                State firstState = new State(0, country, "Choose your State");
                tempCities.add(new City(0, country, firstState, "Choose your City"));

                for (City singleCity : cities) {
                    if (singleCity.getState().getStateID() == state.getStateID()) {
                        tempCities.add(singleCity);
                    }
                }

                cityArrayAdapter = new ArrayAdapter<City>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, tempCities);
                cityArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                city_Spinner.setAdapter(cityArrayAdapter);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener city_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            city = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    private void createLists() {
        Country country0 = new Country(0, "Choose your Country");
        Country country1 = new Country(1, "Country1");
        Country country2 = new Country(2, "Country2");

        countries.add(new Country(0, "Choose your Country"));
        countries.add(new Country(1, "Country1"));
        countries.add(new Country(2, "Country2"));

        State state0 = new State(0, country0, "Choose your state");
        State state1 = new State(1, country1, "state1");
        State state2 = new State(2, country1, "state2");
        State state3 = new State(3, country2, "state3");
        State state4 = new State(4, country2, "state4");

        states.add(state0);
        states.add(state1);
        states.add(state2);
        states.add(state3);
        states.add(state4);

        cities.add(new City(0, country0, state0, "Choose your City"));
        cities.add(new City(1, country1, state1, "City1"));
        cities.add(new City(2, country1, state1, "City2"));
        cities.add(new City(3, country1, state2, "City3"));
        cities.add(new City(4, country2, state2, "City4"));
        cities.add(new City(5, country2, state3, "City5"));
        cities.add(new City(6, country2, state3, "City6"));
        cities.add(new City(7, country2, state4, "City7"));
        cities.add(new City(8, country1, state4, "City8"));

    }

    private class Country implements Comparable<Country> {

        private int countryID;
        private String countryName;


        public Country(int countryID, String countryName) {
            this.countryID = countryID;
            this.countryName = countryName;
        }

        public int getCountryID() {
            return countryID;
        }

        public String getCountryName() {
            return countryName;
        }

        @Override
        public String toString() {
            return countryName;
        }


        @Override
        public int compareTo(Country another) {
            return this.getCountryID() - another.getCountryID();//ascending order
//            return another.getCountryID()-this.getCountryID();//descending  order
        }
    }

    private class State implements Comparable<State> {

        private int stateID;
        private Country country;
        private String stateName;

        public State(int stateID, Country country, String stateName) {
            this.stateID = stateID;
            this.country = country;
            this.stateName = stateName;
        }

        public int getStateID() {
            return stateID;
        }

        public Country getCountry() {
            return country;
        }

        public String getStateName() {
            return stateName;
        }

        @Override
        public String toString() {
            return stateName;
        }

        @Override
        public int compareTo(State another) {
            return this.getStateID() - another.getStateID();//ascending order
//            return another.getStateID()-this.getStateID();//descending order
        }
    }

    private class City implements Comparable<City> {

        private int cityID;
        private Country country;
        private State state;
        private String cityName;

        public City(int cityID, Country country, State state, String cityName) {
            this.cityID = cityID;
            this.country = country;
            this.state = state;
            this.cityName = cityName;
        }

        public int getCityID() {
            return cityID;
        }

        public Country getCountry() {
            return country;
        }

        public State getState() {
            return state;
        }

        public String getCityName() {
            return cityName;
        }

        @Override
        public String toString() {
            return cityName;
        }

        @Override
        public int compareTo(City another) {
            return this.cityID - another.getCityID();//ascending order
//            return another.getCityID() - this.cityID;//descending order
        }
    }


}

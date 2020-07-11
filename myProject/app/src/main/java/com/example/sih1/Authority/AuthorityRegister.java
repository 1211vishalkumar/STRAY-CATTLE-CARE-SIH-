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

public class AuthorityRegister extends AppCompatActivity {

    private Spinner country_Spinner;
    private Spinner state_Spinner;
    private Spinner city_Spinner;

    private ArrayAdapter<Country> countryArrayAdapter;
    private ArrayAdapter<State> stateArrayAdapter;
    private ArrayAdapter<City> cityArrayAdapter;

    private ArrayList<Country> countries;
    private ArrayList<State> states;
    private ArrayList<City> cities;


    EditText etName,etPhone,etEmail,etPassword,etAddress;
    Button btnRegister,btnLogIn;

    private FirebaseAuth mAuth ;
    private ProgressDialog loadingBar;

    String country="";
    String state="";
    String city="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_register);

        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);

        initializeUI();

//        country_Spinner = findViewById(R.id.spinner4);
//        state_Spinner = findViewById(R.id.spinner5);
//        city_Spinner = findViewById(R.id.spinner6);

        etName = findViewById(R.id.editTextTextPersonName);
        etPhone = findViewById(R.id.editTextPhone);
        etEmail = findViewById(R.id.editTextTextEmailAddress);
        etPassword = findViewById(R.id.editTextTextPassword);
        etAddress = findViewById(R.id.editTextTextPersonName2);

        btnRegister = findViewById(R.id.button);
        btnLogIn = findViewById(R.id.button2);

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
                //here we goona create a method
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
        final String COUNTRY = country;
        final String STATE = state;
        final String CITY = city;

        if(!name.equals("") && !phone.equals("") && !email.equals("") && !password.equals("") && !address.equals("")
          && !COUNTRY.equals("") && !STATE.equals("") && !CITY.equals("") ){

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
                                AuthorityMap.put("country",COUNTRY);
                                AuthorityMap.put("state",STATE);
                                AuthorityMap.put("city",CITY);

                                rootRef.child("Authority").child(aid).updateChildren(AuthorityMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    loadingBar.dismiss();
                                                    Toast.makeText(AuthorityRegister.this, "You have registered as Seller Successfully", Toast.LENGTH_SHORT).show();

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

    private void initializeUI() {
        country_Spinner =  findViewById(R.id.spinner4);
        state_Spinner =  findViewById(R.id.spinner5);
        city_Spinner = findViewById(R.id.spinner6);

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

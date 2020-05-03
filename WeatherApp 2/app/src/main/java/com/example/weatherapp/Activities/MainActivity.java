package com.example.weatherapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.R;
import com.example.weatherapp.Other.Weather;
import com.example.weatherapp.Adaptors.WeatherAdaptor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements WeatherAdaptor.itemClicked {

    private static final String SEARCH_QUERY = "searchQuery";

    // our adaptor class variable
    private WeatherAdaptor myAdaptor;

    private int weatherListIndex;
    // array list of person objects
    private ArrayList<Weather> weather;
    // button to add another city and country
    private Button addBtn, shareButton;
    private EditText tvAddCountry, tvAddCity;

    private String searchQuery;

    // public in case other class's need to refer to shared preferences
    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // load current shared pref data
        loadData();
        // build recycler view based on data
        buildRecyclerView();

        addBtn = (Button) findViewById(R.id.addButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvAddCity = (EditText) findViewById(R.id.tvAddCity);
                tvAddCountry = (EditText) findViewById(R.id.tvAddCountry);

                String city = tvAddCity.getText().toString();
                String country = tvAddCountry.getText().toString().toUpperCase();
                city = city.substring(0, 1).toUpperCase() + city.substring(1);


                if (!TextUtils.isEmpty(city)) {

                    if (!TextUtils.isEmpty(country)) {

                        insertItem(city, country);
                        saveData();

                        buildRecyclerView();
                        myAdaptor.notifyDataSetChanged();

                        // set both fields as empty
                        tvAddCity.setText(null);
                        tvAddCountry.setText(null);

                        // get application context (we want this activity as the context)
                        Context context = getApplicationContext();
                        CharSequence text = "Added!";
                        int duration = Toast.LENGTH_SHORT;
                        // make our new Toast and display it
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();


                    } else {
                        tvAddCountry.setError("Please enter a Country!");
                    }
                } else {
                    tvAddCity.setError("Please enter a City!");
                }

            }
        });

        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvAddCity = (EditText) findViewById(R.id.tvAddCity);
                tvAddCountry = (EditText) findViewById(R.id.tvAddCountry);

                String city = tvAddCity.getText().toString();
                String country = tvAddCountry.getText().toString().toUpperCase();

                if (!TextUtils.isEmpty(city)) {
                    if (!TextUtils.isEmpty(country)) {
                        // create URI from intent string -> we are going to use this as our query
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+city+","+country.toUpperCase());
                        // Create an intent for the gmmIntentUri, set action to ACTION_VIEW
                        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        // make the implicit explicit by setting the google maps package
                        intent.setPackage("com.google.android.apps.maps");
                        // start intent
                        startActivity(intent);

                    } else {
                        tvAddCountry.setError("Please enter a Country!");
                    }
                } else {

                    tvAddCity.setError("Please enter a City!");
                }

            }
        });


    }

    @Override
    public void onItemClicked(int index) {
        weatherListIndex = index;
        // we now have passed the index through to the main activity
        // so we can get the persons surname for that index - creating a short popup using a toast
        Intent startIntent = new Intent(getApplicationContext(), CurrentWeather.class);
        startIntent.putExtra("com.example.weatherapp.CITY", weather.get(index).getCity());
        startIntent.putExtra("com.example.weatherapp.COUNTRY", weather.get(index).getCountry());
        startActivity(startIntent);
    }

    @Override
    public void onDeleteClicked(int index) {

        // create new sharedPreferences object
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        // create a new editor so we can modify data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // create a new Gson object
        Gson gson = new Gson();
        // create a json string with our Arraylist<Weather> list
        String json = gson.toJson(weather);
        // add this list to our shared preferences
        editor.putString("weather list", json);
        // commit this change
        editor.apply();

    }

    public void saveData() {

        // saving our shared preference data
        // which is the list of the user weather locations
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(weather);
        editor.putString("weather list", json);
        editor.apply();

    }

    public void loadData() {

        // loading our shared preference data
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("weather list", null);
        Type type = new TypeToken<ArrayList<Weather>>() {}.getType();
        weather = gson.fromJson(json, type);

        if (weather == null) {
            weather = new ArrayList<Weather>();
        }
    }

    private void buildRecyclerView() {

        // this method get our recycler view, populates it using our weather list

        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        myAdaptor = new WeatherAdaptor(this, weather);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdaptor);

    }

    private void insertItem(String city, String country) {
        weather.add(new Weather(city, country));
        myAdaptor.notifyItemInserted(weather.size());
        myAdaptor.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // save our search query when the device rotates or user goes to another activity
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_QUERY, searchQuery);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // get our saved data
        super.onRestoreInstanceState(savedInstanceState);
        searchQuery = savedInstanceState.getString(SEARCH_QUERY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // create the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_weather, menu);

        // reference to action_search menu item
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchQuery = null;
                myAdaptor.getFilter().filter("");
                return true;
            }
        });

        // set up a Query text listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchQuery = newText;
                // as we did all the filtering we only need to get the results
                // populate adaptor with new filter query
                myAdaptor.getFilter().filter(searchQuery);

                return true;
            }
        });

        if (searchQuery == null){
            return true;
        }

        searchItem.expandActionView();
        searchView.setQuery(searchQuery, false);

        return true;
    }
}


package com.example.weatherapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androdocs.httprequest.HttpRequest;
import com.example.weatherapp.Other.BackgroundChanger;
import com.example.weatherapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class CurrentWeather extends AppCompatActivity {

    private static final String FILE_NAME = "current_weather.txt";
    String saveWeatherInfo;

    // this will be what ever the list of places to show is

    String CITY;
    String API = "6870c284a4c7b597bcc7f0240f877672";
    String city, country;

    // Create all the text view objects I need to populate my activity

    TextView addressTxt, updated_atTxt, statusTxt, tempTxt, temp_minTxt, temp_maxTxt, sunriseTxt,
            sunsetTxt, windTxt, pressureTxt, humidityTxt, aboutTxt;
    LinearLayout forcast;
    Button btnBackButton, btnGoBackToMainActivity;
    RelativeLayout currentWeatherBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hide the activity bar
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException ex) {
            System.out.println(ex);
        }

        saveWeatherInfo = readFromFile();

        setContentView(R.layout.activity_current_weather);

        // see if extra info is passed or not
        if (getIntent().hasExtra("com.example.weatherapp.CITY")) {
            try {
                // get the string using our key from our intent
                city = getIntent().getExtras().getString("com.example.weatherapp.CITY");
                if (getIntent().hasExtra("com.example.weatherapp.COUNTRY")) {
                    country = getIntent().getExtras().getString("com.example.weatherapp.COUNTRY");
                    CITY = city + ", " + country;
                } else {
                    CITY = city;
                }
                // set the textView to that text
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }


        // find all the textViews in activity main

        addressTxt = findViewById(R.id.address);
        updated_atTxt = findViewById(R.id.updated_at);
        statusTxt = findViewById(R.id.status);
        tempTxt = findViewById(R.id.temp);
        temp_minTxt = findViewById(R.id.temp_min);
        temp_maxTxt = findViewById(R.id.temp_max);
        sunriseTxt = findViewById(R.id.sunrise);
        sunsetTxt = findViewById(R.id.sunset);
        windTxt = findViewById(R.id.wind);
        pressureTxt = findViewById(R.id.pressure);
        humidityTxt = findViewById(R.id.humidity);
        aboutTxt = findViewById(R.id.about);

        // get background
        currentWeatherBackground = findViewById(R.id.currentWeatherBackground);

        // execute weather task
        new weatherTask().execute();

        // set onclick listener for info
        forcast = (LinearLayout) findViewById(R.id.weatherForecast);
        forcast.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // we are going to a new page -> this will hold the 5 day forecast
                Intent weatherInfoIntent = new Intent(getApplicationContext(), WeatherInfo.class);
                weatherInfoIntent.putExtra("com.example.weatherapp.CITY1", city);
                weatherInfoIntent.putExtra("com.example.weatherapp.COUNTRY1", country);
                startActivity(weatherInfoIntent);

            }
        });

        btnBackButton = (Button) findViewById(R.id.btnBackButton);
        btnBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainApp = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainApp);
            }
        });

        btnGoBackToMainActivity = (Button) findViewById(R.id.btnGoBackToActivity);

        btnGoBackToMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent currentWeatherApp = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(currentWeatherApp);
            }
        });



    }

    class weatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // set loader to visible and make the main container and error text invisible
            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.mainContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {

            String res = readFromFile();

            if (res.isEmpty() && !isNetworkAvailable()) {
                return "";

            } else if (!res.isEmpty() && !isNetworkAvailable()) {
                String response = res;
                Log.e("database", response);
                return response;
            } else {
                String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&units=metric&appid=" + API);
                Log.e("api call", response);
                return response;
            }

        }

        @Override
        protected void onPostExecute(String result) {

            // we can now use this response to get data from our JSON object and set our textViews.
            saveWeatherInfo = result;

            if (saveWeatherInfo.isEmpty()) {

                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
                findViewById(R.id.btnGoBackToActivity).setVisibility(View.VISIBLE);

                return;

            }

            try {

                // make our returned string a JSONObject and get the information we want out of it
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(new Date(updatedAt * 1000));

                int tempInt = (int) Double.parseDouble(main.getString("temp"));
                String temp = tempInt + "°C";

                String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
                String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");

                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");

                Log.e("Weather", weatherDescription);

                // Populating extracted data into our views
                addressTxt.setText(address);
                updated_atTxt.setText(updatedAtText);
                statusTxt.setText(weatherDescription.toUpperCase());
                tempTxt.setText(temp);
                temp_minTxt.setText(tempMin);
                temp_maxTxt.setText(tempMax);
                sunriseTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                sunsetTxt.setText(new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(sunset * 1000)));
                windTxt.setText(windSpeed);
                pressureTxt.setText(pressure);
                humidityTxt.setText(humidity);


                // this is my custom class that figures out what weather condition it is
                BackgroundChanger changer = new BackgroundChanger(weatherDescription);
                // returns a string of a certain type of weather ie - snow, rain, cloudy
                String weatherType = changer.changeBackground();
                // using that string we can set our background to match the weather
                if (weatherType.equals("clear")) {

                    currentWeatherBackground.setBackgroundResource(R.drawable.sunnyimage);

                } else if (weatherType.equals("thunder")) {

                    currentWeatherBackground.setBackgroundResource(R.drawable.thunderimage);

                } else if (weatherType.equals("drizzle")) {

                    currentWeatherBackground.setBackgroundResource(R.drawable.rainyimage);

                    addressTxt.setTextColor(Color.BLACK);
                    updated_atTxt.setTextColor(Color.BLACK);
                    statusTxt.setTextColor(Color.BLACK);
                    tempTxt.setTextColor(Color.WHITE);
                    temp_maxTxt.setTextColor(Color.WHITE);
                    temp_minTxt.setTextColor(Color.WHITE);
                    sunsetTxt.setTextColor(Color.WHITE);
                    sunriseTxt.setTextColor(Color.WHITE);
                    pressureTxt.setTextColor(Color.WHITE);
                    humidityTxt.setTextColor(Color.WHITE);
                    pressureTxt.setTextColor(Color.WHITE);
                    windTxt.setTextColor(Color.WHITE);
                    aboutTxt.setTextColor(Color.WHITE);


                } else if (weatherType.equals("rain")) {

                    addressTxt.setTextColor(Color.BLACK);
                    updated_atTxt.setTextColor(Color.BLACK);
                    statusTxt.setTextColor(Color.BLACK);
                    tempTxt.setTextColor(Color.WHITE);
                    temp_maxTxt.setTextColor(Color.WHITE);
                    temp_minTxt.setTextColor(Color.WHITE);
                    sunsetTxt.setTextColor(Color.WHITE);
                    sunriseTxt.setTextColor(Color.WHITE);
                    pressureTxt.setTextColor(Color.WHITE);
                    humidityTxt.setTextColor(Color.WHITE);
                    pressureTxt.setTextColor(Color.WHITE);
                    windTxt.setTextColor(Color.WHITE);
                    aboutTxt.setTextColor(Color.WHITE);




                    currentWeatherBackground.setBackgroundResource(R.drawable.rainyimage);

                } else if (weatherType.equals("snow")) {

                    currentWeatherBackground.setBackgroundResource(R.drawable.snowyimage);

                } else if (weatherType.equals("atmosphere")) {

                    currentWeatherBackground.setBackgroundResource(R.drawable.foggyimage);

                } else if (weatherType.equals("clouds")) {

                    currentWeatherBackground.setBackgroundResource(R.drawable.cloudimage);

                } else {
                    currentWeatherBackground.setBackgroundColor(R.drawable.bg_main_gradient);
                }


                /* Views populated, Hiding the loader, Showing the main design */
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.mainContainer).setVisibility(View.VISIBLE);




            } catch (JSONException e) {

                // if there is a problem - we handle the JSON exception and display error message
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
                findViewById(R.id.btnGoBackToActivity).setVisibility(View.VISIBLE);

            }
        }
    }



    private void writeToFile(String data) {

        try {
            
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FILE_NAME, Context.MODE_PRIVATE | Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {

            InputStream inputStream = getApplicationContext().openFileInput(FILE_NAME);

            if ( inputStream != null ) {
                // create new input stream reader
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                // create new buffered reader
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                // this will store hold our file data (for each iteration)
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                // iterate over our bufferedReader contents, set our recieved string to each line
                // checking if it is null (the end of the file) every iteration
                // add the non-empty string to our string builder
                while ((receiveString = bufferedReader.readLine()) != null ) {
                    Log.e("String", receiveString);
                    // checks if the line contains the city I want to load
                    if (receiveString.contains(getIntent().getExtras().getString(("com.example.weatherapp.CITY")))) {
                        stringBuilder.append(receiveString);

                    }
                }

                // close our input stream
                inputStream.close();
                // convert our string-builder to a string of the data we want to return
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        Log.e("ResultFile", ret);
        // return our file's contents
        return ret;
    }

    private boolean isNetworkAvailable() {
        // set up connectivity manager
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        // get active network info
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        // return true if active network else return false
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // not sure what to do: either check if response is empty - then search database for the data
    // if no - then display error

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        // when user leaves activity save the weather info
        super.onSaveInstanceState(outState);
        writeToFile(saveWeatherInfo);
    }



}

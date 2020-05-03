package com.example.weatherapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.AsyncTask;
import android.util.Log;

import com.androdocs.httprequest.HttpRequest;
import com.example.weatherapp.Other.BackgroundChanger;
import com.example.weatherapp.Fragments.ListFrag;
import com.example.weatherapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Arrays;
import java.util.List;

public class WeatherInfo extends AppCompatActivity implements ListFrag.ItemSelected {


    private static final String WEATHER_INDEX = "weatherIndex";

    // internal storage file name

    String city, country;
    int weatherIndex = 0;

    // getting CITY and API
    String CITY;
    String API = "6870c284a4c7b597bcc7f0240f877672";

    // getting textViews and other views
    Button btnBackBtn, btnGoBack;
    TextView weatherCondition, currentTemp, minTemp, maxTemp, address;
    TextView tvHour1, tvTimes, wind, pressure, humidity;
    String[] times = {"12am ", "3am ", "6am ", "9am ", "12pm ", "3pm ", "6pm ", "9pm "};
    StringBuilder timesSB = new StringBuilder();
    RelativeLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException ex) {
            setContentView(R.layout.activity_main);
        }

        setContentView(R.layout.activity_weather_info);



        if (getIntent().hasExtra("com.example.weatherapp.CITY1")) {
            try {
                // get the string using our key from our intent
                city = getIntent().getExtras().getString("com.example.weatherapp.CITY1");
                if (getIntent().hasExtra("com.example.weatherapp.COUNTRY1")) {
                    country = getIntent().getExtras().getString("com.example.weatherapp.COUNTRY1");
                    CITY = city + ", " + country;
                } else {
                    CITY = city;
                }
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }


        // link our views to the the correct R.id's
        btnBackBtn = findViewById(R.id.btnBackBtn);
        weatherCondition = findViewById(R.id.weatherCondition);
        currentTemp = findViewById(R.id.currentTemp);
        minTemp = findViewById(R.id.min_temp);
        maxTemp = findViewById(R.id.max_temp);
        address = findViewById(R.id.tvcity);

        tvHour1 = findViewById(R.id.tvHour1);
        tvTimes = findViewById(R.id.tvTimes);


        // getting wind/pressure/humidity

        pressure = findViewById(R.id.pressure);
        wind = findViewById(R.id.wind);
        humidity = findViewById(R.id.humidity);


        // get background
        background = findViewById(R.id.detailFrag);

        // go back to weather page when back button is clicked
        btnBackBtn = (Button) findViewById(R.id.btnBackBtn);
        btnBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent currentWeatherApp = new Intent(getApplicationContext(), CurrentWeather.class);
                currentWeatherApp.putExtra("com.example.weatherapp.CITY", city);
                currentWeatherApp.putExtra("com.example.weatherapp.COUNTRY", country);

                startActivity(currentWeatherApp);
            }
        });

        // set onClick listener for the error message back button

        btnGoBack = (Button) findViewById(R.id.btnGoBack);

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent currentWeatherApp = new Intent(getApplicationContext(), CurrentWeather.class);
                currentWeatherApp.putExtra("com.example.weatherapp.CITY", city);
                currentWeatherApp.putExtra("com.example.weatherapp.COUNTRY", country);

                startActivity(currentWeatherApp);
            }
        });


        new weatherInfo().execute();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(WEATHER_INDEX, weatherIndex);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        weatherIndex = savedInstanceState.getInt(WEATHER_INDEX);
    }

    // creating another async inner class
    class weatherInfo extends AsyncTask<String, Void, String> {

        // before we do you HTTP req -> we are setting our load and hiding our content
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // im setting the progress bar to visible and hiding the main container
            // until the HTTP req is complete
            findViewById(R.id.loader1).setVisibility(View.VISIBLE);
            findViewById(R.id.weatherForcastContainer).setVisibility(View.GONE);
            findViewById(R.id.errorText1).setVisibility(View.GONE);
        }


        // making HTTP req -> the response is used onPostExecute
        @Override
        protected String doInBackground(String... strings) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/forecast?q=" + CITY + "&units=metric&appid=" + API);
            return response;
        }

        // here we use the response (JSON object) to populate our textViews ect...
        @Override
        protected void onPostExecute(String result) {


            if (result == null) {

                findViewById(R.id.loader1).setVisibility(View.GONE);
                findViewById(R.id.weatherForcastContainer).setVisibility(View.GONE);
                findViewById(R.id.errorText1).setVisibility(View.VISIBLE);
                findViewById(R.id.btnGoBack).setVisibility(View.VISIBLE);



                return;

            }

            try {

                // write to file

                // we now have our JSON object with the data from our API request
                JSONObject jsonObject = new JSONObject(result);
                // getting list of weather for each day
                JSONArray weatherList = jsonObject.getJSONArray("list");
                // we need correct INDEX
                // convert JSON object to an array

                JSONObject main;
                JSONObject weather;


                String windSpeed;

                JSONObject mainTemp;

                int counter = 0;

                // cycle through all the weather info
                for (int i = 0; i < weatherList.length(); i++) {

                    // check that we get a weather object with a certain time
                    if (weatherList.getJSONObject(i).getString("dt_txt").contains("12:00:00")) {

                        // counter is essentially the index the person has clicked on
                        // so we are checking the object we found is of the correct date
                        if (weatherIndex == counter) {


                            windSpeed = weatherList.getJSONObject(i).getJSONObject("wind").getString("speed") + "Kph";

                            // getting the current date
                            String date = weatherList.getJSONObject(i).getString("dt_txt").substring(0, 11);
                            String weatherD = "";
                            int numberOfTimes = -1;

                            // we cycle through again this time we are looking for all the temperatures
                            // recorded on that day -> normally every 3 hours
                            for (int j = 0; j < weatherList.length(); j++) {

                                // check that the temp object we found is of the correct data
                                if (weatherList.getJSONObject(j).getString("dt_txt").contains(date)){

                                    // getting the temperature reading
                                    JSONObject weatherTemps = weatherList.getJSONObject(j).getJSONObject("main");
                                    // casting it to an integer (we don't want decimal points)
                                    int tempInt = (int) Double.parseDouble(weatherTemps.getString("temp"));
                                    // adding it to our string
                                    weatherD += tempInt + "°C" + "  ";
                                    // this is for the index (number of items in our array list)
                                    numberOfTimes += 1;


                                }
                            }

                            // getting our info for our JSON object
                            main = weatherList.getJSONObject(i).getJSONObject("main");
                            weather = weatherList.getJSONObject(i).getJSONArray("weather").getJSONObject(0);

                            int tempInt = (int) Double.parseDouble(main.getString("temp"));
                            String temp = tempInt + "°C";

                            Log.e("Main", main.toString());
                            String tempMin = "Min Temp: " + main.getString("temp_min") + "°C";
                            String tempMax = "Max Temp: " + main.getString("temp_max") + "°C";
                            String weatherDescription = weather.getString("description");


                            // setting pressure, wind, humidity

                            String pressureString = main.getString("pressure").substring(0, 2) + "mBar";
                            String humidityString = main.getString("humidity") + "%";

                            wind.setText(windSpeed);
                            pressure.setText(pressureString);
                            humidity.setText(humidityString);

                            // setting our weather description

                            tvHour1.setText(weatherD);

                            // sometimes we only get part of the current days weather
                            // so we will only get times that are past our min index (arr length - num of times)
                            // so if it was 6am we want 6am -> 9pm times as the other times before current time

                            List<String> subArr = Arrays.asList(times).subList(times.length - numberOfTimes - 1, times.length);
                            timesSB.append(subArr);
                            tvTimes.setText(timesSB.substring(1, (timesSB.length() - 1)));
                            timesSB = new StringBuilder();


                            // set weather data
                            weatherCondition.setText(weatherDescription);
                            currentTemp.setText(temp);
                            maxTemp.setText(tempMax);
                            minTemp.setText(tempMin);
                            String cityFormat = city.substring(0, 1).toUpperCase() + city.substring(1);
                            String addressString = cityFormat + ", " + country.toUpperCase();
                            address.setText(addressString);

                            // this is my custom class that figures out what weather condition it is
                            BackgroundChanger changer = new BackgroundChanger(weatherDescription);
                            // returns a string of a certain type of weather ie - snow, rain, cloudy
                            String weatherType = changer.changeBackground();

                            Log.e("TESTING", weatherType);
                            // using that string we can set our background to match the weather
                            if (weatherType.equals("clear")) {

                                background.setBackgroundResource(R.drawable.sunnyimage);

                            } else if (weatherType.equals("thunder")) {

                                background.setBackgroundResource(R.drawable.thunderimage);

                            } else if (weatherType.equals("drizzle")) {

                                address.setTextColor(Color.BLACK);
                                tvHour1.setTextColor(Color.BLACK);
                                tvTimes.setTextColor(Color.BLACK);
                                wind.setTextColor(Color.WHITE);
                                pressure.setTextColor(Color.WHITE);
                                humidity.setTextColor(Color.WHITE);

                                background.setBackgroundResource(R.drawable.rainyimage);

                            } else if (weatherType.equals("rain")) {


                                address.setTextColor(Color.BLACK);
                                tvHour1.setTextColor(Color.BLACK);
                                tvTimes.setTextColor(Color.BLACK);
                                wind.setTextColor(Color.WHITE);
                                pressure.setTextColor(Color.WHITE);
                                humidity.setTextColor(Color.WHITE);


                                background.setBackgroundResource(R.drawable.rainyimage);

                            } else if (weatherType.equals("snow")) {

                                background.setBackgroundResource(R.drawable.snowyimage);

                            } else if (weatherType.equals("atmosphere")) {

                                background.setBackgroundResource(R.drawable.foggyimage);

                            } else if (weatherType.equals("clouds")) {

                                background.setBackgroundResource(R.drawable.cloudimage);

                            } else {
                                background.setBackgroundColor(R.drawable.bg_main_gradient);
                            }

                            // now make the main container visible
                            findViewById(R.id.loader1).setVisibility(View.GONE);
                            findViewById(R.id.weatherForcastContainer).setVisibility(View.VISIBLE);
                            break;

                        } else {
                            counter += 1;
                        }
                    }
                }


            } catch (JSONException e) {

                // hide the content, and show error message

                findViewById(R.id.loader1).setVisibility(View.GONE);
                findViewById(R.id.weatherForcastContainer).setVisibility(View.VISIBLE);
                findViewById(R.id.errorText1).setVisibility(View.VISIBLE);
                findViewById(R.id.btnGoBack).setVisibility(View.VISIBLE);

                e.printStackTrace();
            }

        }

    }

    @Override
    public void onItemSelected(int index) {
        // on every list item, if clicked call our async class
        // which will load the information for that item.
        weatherIndex = index;
        // execute our Async class
        // this will populate our our list detail fragment
        new weatherInfo().execute();
    }

}

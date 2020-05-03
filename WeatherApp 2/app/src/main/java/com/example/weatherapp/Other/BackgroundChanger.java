package com.example.weatherapp.Other;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class BackgroundChanger extends AppCompatActivity {

    private String weatherType;

    private ArrayList<String> thunderStorm = new ArrayList<>();

    private ArrayList<String> drizzle = new ArrayList<>();
    private ArrayList<String> rain = new ArrayList<>();
    private ArrayList<String> snow = new ArrayList<>();
    private ArrayList<String> atmosphere = new ArrayList<>();
    private ArrayList<String> clear = new ArrayList<>();
    private ArrayList<String> clouds = new ArrayList<>();



    public BackgroundChanger(String weatherType) {
        this.weatherType = weatherType;
    }

    public String changeBackground() {

        Log.e("Background", weatherType);

        thunderStorm.add("thunderstorm with light rain");
        thunderStorm.add("thunderstorm with rain");
        thunderStorm.add("thunderstorm with heavy rain");
        thunderStorm.add("light thunderstorm");
        thunderStorm.add("thunderstorm");
        thunderStorm.add("heavy thunderstorm");
        thunderStorm.add("ragged thunderstorm");
        thunderStorm.add("thunderstorm with light drizzle");
        thunderStorm.add("thunderstorm with drizzle");
        thunderStorm.add("thunderstorm with heavy drizzle");

        drizzle.add("light intensity drizzle");
        drizzle.add("drizzle");
        drizzle.add("heavy intensity drizzle");
        drizzle.add("light intensity drizzle rain");
        drizzle.add("drizzle rain");
        drizzle.add("heavy intensity drizzle rain");
        drizzle.add("shower rain and drizzle");
        drizzle.add("heavy shower rain and drizzle");
        drizzle.add("shower drizzle");

        rain.add("light rain");
        rain.add("moderate rain");
        rain.add("heavy intensity rain");
        rain.add("very heavy rain");
        rain.add("extreme rain");
        rain.add("freezing rain");
        rain.add("light intensity shower rain");
        rain.add("shower rain");
        rain.add("heavy intensity shower rain");
        rain.add("ragged shower rain");

        snow.add("light snow");
        snow.add("snow");
        snow.add("heavy snow");
        snow.add("Sleet");
        snow.add("light shower sleet");
        snow.add("shower sleet");
        snow.add("light rain and snow");
        snow.add("rain and snow");
        snow.add("light shower snow");
        snow.add("shower snow");
        snow.add("heavy shower snow");

        atmosphere.add("mist");
        atmosphere.add("smoke");
        atmosphere.add("haze");
        atmosphere.add("sand/ dust whirls");
        atmosphere.add("fog");
        atmosphere.add("sand");
        atmosphere.add("dust");
        atmosphere.add("squalls");
        atmosphere.add("tornado");

        clear.add("clear sky");
        clear.add("few clouds: 11-25%");
        clear.add("scattered clouds: 25-50%");

        clouds.add("broken clouds: 51-84%");
        clouds.add("overcast clouds: 85-100%");

        if (thunderStorm.contains(weatherType)) {
            return "thunder";
        } else if (drizzle.contains(weatherType)) {
            return "drizzle";
        } else if (rain.contains(weatherType)){
            return "rain";
        } else if (snow.contains(weatherType)) {
            return "snow";
        } else if (atmosphere.contains(weatherType)) {
            return "atmosphere";
        } else if (clear.contains(weatherType)) {
            return "clear";
        } else if (clouds.contains(weatherType)) {
            return "clouds";
        } else {
            return "clear";
        }

    }



}

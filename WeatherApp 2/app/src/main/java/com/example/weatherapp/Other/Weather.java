package com.example.weatherapp.Other;

// class to store each location person wants.
public class Weather {

    // we just want to store a CITY and COUNTRY for each location
    private String city;
    private String country;

    public Weather (String city, String country) {
        this.city = city;
        this.country = country;

    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


}

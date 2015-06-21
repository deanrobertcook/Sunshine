package com.example.android.sunshine.app.api;

/**
 * Created by deancook on 20/06/15.
 */
public class Weather {
    public final String main;
    public final String description;


    public Weather(String main, String description) {
        this.main = main;
        this.description = description;
    }
}

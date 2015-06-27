package com.example.android.sunshine.app.data;

public class Forecast {
    public final String day;
    public final String description;
    public final int max;
    public final int min;


    public Forecast(String day, String description, int max, int min) {
        this.day = day;
        this.description = description;
        this.max = max;
        this.min = min;
    }
}

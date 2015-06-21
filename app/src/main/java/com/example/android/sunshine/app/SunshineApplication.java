package com.example.android.sunshine.app;

import android.app.Application;

import com.example.android.sunshine.app.stores.ForecastStore;

/**
 * Created by deancook on 20/06/15.
 */
public class SunshineApplication extends Application {

    private ForecastStore forecastStore;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

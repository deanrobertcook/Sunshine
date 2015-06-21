package com.example.android.sunshine.app.api;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * Created by deancook on 20/06/15.
 */
public interface ForecastService {

    @GET("/forecast/daily")
    WeeklyForecast forecastList(@QueryMap Map<String, String> options);
}

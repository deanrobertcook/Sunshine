package com.example.android.sunshine.app.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deancook on 20/06/15.
 */
public class WeeklyForecast {
    public final List<DailyForecast> list;

    public WeeklyForecast(List<DailyForecast> list) {
        this.list = list;
    }

    public List<String> getForecasts () {
        List<String> forecasts = new ArrayList<>();

        for (DailyForecast forecast: list) {
            forecasts.add(forecast.toString());
        }
        return forecasts;
    }
}

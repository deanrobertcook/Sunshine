package com.example.android.sunshine.app.api;

import java.util.List;

/**
 * Created by deancook on 20/06/15.
 */
public class DailyForecast {
    public final Temperature temp;
    public final List<Weather> weather;

    public DailyForecast(Temperature temp, List<Weather> weather) {
        this.temp = temp;
        this.weather = weather;
    }

    @Override
    public String toString() {
        return weather.get(0).main  + " - " + formatHighLows(temp.max, temp.min);
    }

    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }
}

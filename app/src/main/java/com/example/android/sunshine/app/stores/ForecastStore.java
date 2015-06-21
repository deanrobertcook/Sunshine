package com.example.android.sunshine.app.stores;

import android.os.AsyncTask;
import android.text.format.Time;

import com.example.android.sunshine.app.api.ForecastService;
import com.example.android.sunshine.app.api.WeeklyForecast;
import com.example.android.sunshine.app.pages.ForecastFragment;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit.RestAdapter;

/**
 * Created by deancook on 20/06/15.
 */
public class ForecastStore {

    private static final String TAG = ForecastFragment.class.getSimpleName();

    public Set<Observer> observers = new HashSet<>();

    public List<String> forecasts;

    public void registerObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void unregisterObserver(Observer observer) {
        this.observers.remove(observer);
    }

    private void notifyObservers() {
        for (Observer observer : observers) {
            observer.onForecastChange(forecasts);
        }
    }

    public void fetchForecast(String postcode, String countryCode) {
        new FetchForecastTask().execute(postcode, countryCode);
    }

    public interface Observer {
        void onForecastChange(List<String> forecasts);
    }

    class FetchForecastTask extends AsyncTask<String, Void, WeeklyForecast> {

        private final String BASE_URL = "http://api.openweathermap.org/data/2.5";

        private final String POSTCODE_PARAM = "q";

        private final String UNITS_PARAM = "units";
        private final String UNITS_DEFAULT = "metric";

        private final String FORECAST_DAYS_PARAM = "cnt";
        private final int FORECAST_DAYS_DEFAULT = 7;

        @Override
        protected WeeklyForecast doInBackground(String... params) {

            final String postCode = params[0];

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
                    .build();

            ForecastService service = restAdapter.create(ForecastService.class);

            Map<String, String> optionsMap = new HashMap<>();
            optionsMap.put(POSTCODE_PARAM, postCode);
            optionsMap.put(UNITS_PARAM, UNITS_DEFAULT);
            optionsMap.put(FORECAST_DAYS_PARAM, Integer.toString(FORECAST_DAYS_DEFAULT));

            return service.forecastList(optionsMap);
        }

        @Override
        protected void onPostExecute(WeeklyForecast forecast) {
            forecasts = forecast.getForecasts();
            appendDays(forecasts);
            notifyObservers();
        }

        private void appendDays(List<String> forecasts) {

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            for (String forecast : forecasts) {
                int i = forecasts.indexOf(forecast);
                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                String day = getReadableDateString(dateTime);

                forecasts.set(i, day + " - " + forecast);
            }
        }

        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }
    }

}

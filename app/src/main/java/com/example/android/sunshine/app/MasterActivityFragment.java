package com.example.android.sunshine.app;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.sunshine.app.data.Forecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MasterActivityFragment extends Fragment {

    public final String TAG = MasterActivityFragment.class.getSimpleName();

    private ForecastAdapter forecastAdapter;
    private final String DEFAULT_POSTCODE = "14055";
    private final String DEFAULT_COUNTRY = "de";
    private final String DEFAULT_UNITS = "metric";

    private String selectedUnits = DEFAULT_UNITS;

    public static MasterActivityFragment newInstance() {
        MasterActivityFragment fragment = new MasterActivityFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    private void updateWeather() {
        String postcode = getSettingValue(R.string.pref_postcode_key, DEFAULT_POSTCODE);
        String countryCode = getSettingValue(R.string.pref_country_key, DEFAULT_COUNTRY);

        String units = getSettingValue(R.string.pref_units_key, DEFAULT_UNITS);
        selectedUnits = units;

        new FetchForecastTask().execute(postcode, countryCode);
    }

    private String getSettingValue(int keyResourceId, String defaultValue) {
        String value = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(
                        getString(keyResourceId),
                        defaultValue);
        return value;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            updateWeather();
        } else if (item.getItemId() == R.id.action_view_location) {
            sendUserToMaps();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMaps() {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        String postcode = getSettingValue(R.string.pref_postcode_key, DEFAULT_POSTCODE);
        String countryCode = getSettingValue(R.string.pref_country_key, DEFAULT_COUNTRY);

        intent.setData(buildLocationUri(postcode, countryCode));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    private Uri buildLocationUri(String postcode, String countryCode) {
        return Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", postcode + "+" + countryCode)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        forecastAdapter = new ForecastAdapter(
                getActivity(),
                new ArrayList<Forecast>()
        );

        View rootView = inflater.inflate(R.layout.fragment_master, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

        listView.setAdapter(forecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Forecast forecast = forecastAdapter.getItem(position);
//                Intent intent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, text);
//
//                startActivity(intent);
            }
        });

        return rootView;
    }

    class FetchForecastTask extends AsyncTask<String, Void, ArrayList<Forecast>> {

        private final String SCHEME = "http";
        private final String BASE_URL = "//api.openweathermap.org/data/2.5/forecast/daily";

        private final String POSTCODE_PARAM = "q";

        private final String UNITS_PARAM = "units";

        private final String FORECAST_DAYS_PARAM = "cnt";
        private final int FORECAST_DAYS_DEFAULT = 7;

        @Override
        protected ArrayList<Forecast> doInBackground(String... params) {

            final String postCode = params[0];
            final String countryCode = params[0];

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            // Contains the parsed JSON to hand back to the adapter
            ArrayList<Forecast> forecasts = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(buildURL(params));

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                forecasts = getWeatherDataFromJson(forecastJsonStr, 7);
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return forecasts;
        }

        private String buildURL (String[] params) {
            String postCode = params[0];
            String countryCode = params[1];

            String url = new Uri.Builder()
                    .scheme(SCHEME)
                    .path(BASE_URL)
                    .appendQueryParameter(POSTCODE_PARAM, postCode + ", " + countryCode)
                    .appendQueryParameter(UNITS_PARAM, DEFAULT_UNITS)
                    .appendQueryParameter(FORECAST_DAYS_PARAM, Integer.toString(FORECAST_DAYS_DEFAULT))
                    .toString();

            Log.v(TAG, url);

            return url;
        }

        @Override
        protected void onPostExecute(ArrayList<Forecast> strings) {
            forecastAdapter.clear();
            forecastAdapter.addAll(strings);
            //ArrayAdapter automatically notifies on change (unless that behaviour
            //is explicitly disabled)
        }

        /* The date/time conversion code is going to be moved outside the asynctask later,
                 * so for convenience we're breaking it out into its own method now.
                 */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private ArrayList<Forecast> getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            // Names of some other interesting elements in the JSON string
            final String OWM_CITY = "city";


            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            String city = forecastJson.get(OWM_CITY).toString();
            Log.v(TAG, "City: " + city);

            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            ArrayList<Forecast> forecasts = new ArrayList<>();
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                high = convertToSelectedUnits(high);
                double low = temperatureObject.getDouble(OWM_MIN);
                low = convertToSelectedUnits(low);

                Forecast forecast = new Forecast(
                        day,
                        description,
                        (int) high,
                        (int) low
                );
                forecasts.add(forecast);
            }
            return forecasts;

        }

        private double convertToSelectedUnits(double tempValue) {
            if (selectedUnits.equals(DEFAULT_UNITS)) {
                return tempValue;
            }

            return tempValue * 9.00 / 5.00 + 32.00;
        }
    }

}

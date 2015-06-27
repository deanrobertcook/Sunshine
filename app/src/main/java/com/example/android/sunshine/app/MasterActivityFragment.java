package com.example.android.sunshine.app;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MasterActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public final String TAG = MasterActivityFragment.class.getSimpleName();

    private ForecastAdapter forecastAdapter;
    private ArrayAdapter<String> tempAdapter;
    private final String DEFAULT_POSTCODE = "14055";
    private final String DEFAULT_COUNTRY = "de";
    private final String DEFAULT_UNITS = "metric";


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
        String location = Utility.getPreferredLocation(getActivity());

        new FetchWeatherTask(getActivity()).execute(location);
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

        String location = Utility.getPreferredLocation(getActivity());
        Uri geoLocationResource = buildLocationUri(location);


        intent.setData(geoLocationResource);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    private Uri buildLocationUri(String location) {
        return Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", location)
                .build();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        Cursor cur = getActivity().getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
        forecastAdapter = new ForecastAdapter(getActivity(), cur, 0);

        View rootView = inflater.inflate(R.layout.fragment_master, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);


        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

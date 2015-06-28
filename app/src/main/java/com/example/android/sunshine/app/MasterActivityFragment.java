package com.example.android.sunshine.app;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MasterActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    //IDs for each loader have to be unique within a given activity (or fragment?)
    private static final int FORECAST_LOADER_ID = 0;
    public final String TAG = MasterActivityFragment.class.getSimpleName();

    private ForecastAdapter forecastAdapter;
    private ArrayAdapter<String> tempAdapter;
    private final String DEFAULT_POSTCODE = "14055";
    private final String DEFAULT_COUNTRY = "de";
    private final String DEFAULT_UNITS = "metric";

    public static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_TABLE_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_API_ID = 5;


    public static MasterActivityFragment newInstance() {
        MasterActivityFragment fragment = new MasterActivityFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void updateWeather() {
        String location = Utility.getPreferredLocation(getActivity());

        new FetchWeatherTask(getActivity()).execute(location);
    }

    public void onLocationChanged() {
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            updateWeather();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        forecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_master, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = forecastAdapter.getCursor();
                cursor.moveToPosition(position);
                long date = cursor.getLong(COL_WEATHER_DATE);

                Uri itemUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        Utility.getPreferredLocation(getActivity()), date
                );

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .setData(itemUri);

                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //Here's where we create the CursorLoader
        Uri resourceUri = WeatherContract.WeatherEntry.buildWeatherLocation(
                Utility.getPreferredLocation(getActivity()));

        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        return new CursorLoader(
                getActivity(),
                resourceUri,
                FORECAST_COLUMNS,
                null, null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        forecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        forecastAdapter.swapCursor(null);
    }
}

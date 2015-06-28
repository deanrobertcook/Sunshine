package com.example.android.sunshine.app;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_FORECAST_LOADER_ID = 0;
    private final String HASHTAG = "#SunshineApp";
    private String forecastString;

    private ShareActionProvider shareActionProvider;

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
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE
    };


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_HUMIDITY = 5;
    public static final int COL_WIND_SPEED = 6;
    public static final int COL_WIND_DEGREES = 7;
    public static final int COL_PRESSURE = 8;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem shareButton = menu.findItem(R.id.menu_item_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareButton);

        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(createShareIntent());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        return rootView;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, forecastString + HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        Uri itemUri = intent.getData();

        return new CursorLoader(
                getActivity(),
                itemUri,
                FORECAST_COLUMNS,
                null, null, null
        );
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_FORECAST_LOADER_ID, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst(); //needed?

        TextView day = (TextView) getView().findViewById(R.id.tv__detail_day);
        String dayStr = Utility.getDayName(getActivity(),
                data.getLong(COL_WEATHER_DATE));
        day.setText(dayStr);

        TextView date = (TextView) getView().findViewById(R.id.tv__detail_date);
        String dateStr = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
        date.setText(dateStr);

        TextView max = (TextView) getView().findViewById(R.id.tv__detail_max);
        String maxStr = Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MAX_TEMP));
        max.setText(maxStr);

        TextView min = (TextView) getView().findViewById(R.id.tv__detail_min);
        String minStr = Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MIN_TEMP));
        min.setText(minStr);

        TextView humidity = (TextView) getView().findViewById(R.id.tv__detail_humidity);
        humidity.setText(data.getString(COL_HUMIDITY));

        TextView wind = (TextView) getView().findViewById(R.id.tv__detail_wind);
        wind.setText(data.getString(COL_WIND_SPEED));

        TextView pressure = (TextView) getView().findViewById(R.id.tv__detail_pressure);
        pressure.setText(data.getString(COL_PRESSURE));

        TextView description = (TextView) getView().findViewById(R.id.tv__detail_description);
        description.setText(data.getString(COL_WEATHER_DESC));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}

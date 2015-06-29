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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_FORECAST_LOADER_ID = 0;
    private static final String CURRENT_URI = "CURRENT_URI";
    private final String HASHTAG = "#SunshineApp";
    private Uri currentItemUri;

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
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_TABLE_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_HUMIDITY = 5;
    public static final int COL_WIND_SPEED = 6;
    public static final int COL_WIND_DEGREES = 7;
    public static final int COL_PRESSURE = 8;
    public static final int COL_WEATHER_API_ID = 9;


    private TextView day;
    private TextView date;
    private TextView max;
    private TextView min;
    private TextView humidity;
    private TextView wind;
    private TextView pressure;
    private TextView description;
    private ImageView image;


    public static DetailFragment newInstance(Uri itemUri) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_URI, itemUri.toString());
        detailFragment.setArguments(args);
        return detailFragment;
    }

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    public Uri getCurrentItemUri() {
        if (currentItemUri == null) {
            return Uri.parse(getArguments().getString(CURRENT_URI));
        }
        return currentItemUri;

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
        Bundle arguments = getArguments();
        if (arguments != null) {
            currentItemUri = arguments.getParcelable(CURRENT_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        day = (TextView) rootView.findViewById(R.id.tv__detail_day);
        date = (TextView) rootView.findViewById(R.id.tv__detail_date);
        max = (TextView) rootView.findViewById(R.id.tv__detail_max);
        min = (TextView) rootView.findViewById(R.id.tv__detail_min);
        humidity = (TextView) rootView.findViewById(R.id.tv__detail_humidity);
        wind = (TextView) rootView.findViewById(R.id.tv__detail_wind);
        pressure = (TextView) rootView.findViewById(R.id.tv__detail_pressure);
        description = (TextView) rootView.findViewById(R.id.tv__detail_description);
        image = (ImageView) rootView.findViewById(R.id.iv__detail_image);

        return rootView;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri itemUri = getCurrentItemUri();

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
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst(); //needed? - YES

        String dayStr = Utility.getDayName(getActivity(),
                cursor.getLong(COL_WEATHER_DATE));
        day.setText(dayStr);

        String dateStr = Utility.formatDate(cursor.getLong(COL_WEATHER_DATE));
        date.setText(dateStr);

        String maxStr = Utility.formatTemperature(getActivity(),
                cursor.getDouble(COL_WEATHER_MAX_TEMP));
        max.setText(maxStr);

        String minStr = Utility.formatTemperature(getActivity(),
                cursor.getDouble(COL_WEATHER_MIN_TEMP));
        min.setText(minStr);

        humidity.setText(cursor.getString(COL_HUMIDITY));

        wind.setText(cursor.getString(COL_WIND_SPEED));

        pressure.setText(cursor.getString(COL_PRESSURE));

        description.setText(cursor.getString(COL_WEATHER_DESC));

        int weatherId = cursor.getInt(COL_WEATHER_API_ID);
        image.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void onLocationChanged(String locationSetting) {
        long date = WeatherContract.WeatherEntry.getDateFromUri(getCurrentItemUri());
        currentItemUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting, date);
        getLoaderManager().restartLoader(DETAIL_FORECAST_LOADER_ID, null, this);
    }
}

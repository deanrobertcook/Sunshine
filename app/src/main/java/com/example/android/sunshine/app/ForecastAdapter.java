package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherProjection;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        String highAndLow = formatHighLows(
                cursor.getDouble(WeatherProjection.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(WeatherProjection.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(WeatherProjection.COL_WEATHER_DATE)) +
                " - " + cursor.getString(WeatherProjection.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView dayTextView = (TextView) view.findViewById(R.id.tv__list_forecast_day);
        dayTextView.setText(cursor.getString(WeatherProjection.COL_WEATHER_DATE));

        TextView descTextView = (TextView) view.findViewById(R.id.tv__list_forecast_description);
        descTextView.setText(cursor.getString(WeatherProjection.COL_WEATHER_DESC));

        TextView maxTextView = (TextView) view.findViewById(R.id.tv__list_forecast_max);
        maxTextView.setText(cursor.getString(WeatherProjection.COL_WEATHER_MAX_TEMP));

        TextView minTextView = (TextView) view.findViewById(R.id.tv__list_forecast_min);
        minTextView.setText(cursor.getString(WeatherProjection.COL_WEATHER_MIN_TEMP));
    }
}
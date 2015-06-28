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


    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_OTHER = 1;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare a temperature for presentation.
     */
    private String formatTemp(double temp) {
        boolean isMetric = Utility.isMetric(mContext);
        String tempString = Utility.formatTemperature(temp, isMetric);
        return tempString;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_OTHER;
    }

    /*
        We have two different types of views
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else { //if (viewType == VIEW_TYPE_OTHER) {
            layoutId = R.layout.list_item_forecast;
        }

        return LayoutInflater.from(context).inflate(layoutId, parent, false);
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView dayTextView = (TextView) view.findViewById(R.id.tv__list_forecast_day);
        String date = Utility.getFriendlyDayString(
                context,
                cursor.getLong(WeatherProjection.COL_WEATHER_DATE));
        dayTextView.setText(date);

        TextView descTextView = (TextView) view.findViewById(R.id.tv__list_forecast_description);
        descTextView.setText(cursor.getString(WeatherProjection.COL_WEATHER_DESC));

        TextView maxTextView = (TextView) view.findViewById(R.id.tv__list_forecast_max);
        double max = cursor.getDouble(WeatherProjection.COL_WEATHER_MAX_TEMP);
        maxTextView.setText(formatTemp(max));

        TextView minTextView = (TextView) view.findViewById(R.id.tv__list_forecast_min);
        double min = cursor.getDouble(WeatherProjection.COL_WEATHER_MIN_TEMP);
        minTextView.setText(formatTemp(min));
    }
}
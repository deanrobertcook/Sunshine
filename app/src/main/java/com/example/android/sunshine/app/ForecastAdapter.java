package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_OTHER = 1;
    private static final String TAG = ForecastAdapter.class.getName();

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
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

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String date = Utility.getFriendlyDayString(
                context,
                cursor.getLong(MasterActivityFragment.COL_WEATHER_DATE));
        viewHolder.day.setText(date);

        String description = cursor.getString(MasterActivityFragment.COL_WEATHER_DESC);
        viewHolder.description.setText(description);

        double max = cursor.getDouble(MasterActivityFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.max.setText(Utility.formatTemperature(mContext, max));

        double min = cursor.getDouble(MasterActivityFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.min.setText(Utility.formatTemperature(mContext, min));

        int weatherId = cursor.getInt(MasterActivityFragment.COL_WEATHER_API_ID);
        int drawableResId = -1;
        int cursorPos = cursor.getPosition();
        if (getItemViewType(cursorPos) == VIEW_TYPE_TODAY) {
            drawableResId = Utility.getArtResourceForWeatherCondition(weatherId);
        } else {// if (getItemViewType(cursorPos) == VIEW_TYPE_OTHER) {
            drawableResId = Utility.getIconResourceForWeatherCondition(weatherId);
        }
        viewHolder.image.setImageResource(drawableResId);
    }

    public static class ViewHolder {
        public TextView day;
        public TextView description;
        public TextView max;
        public TextView min;
        public ImageView image;

        public ViewHolder(View view) {
            day = (TextView) view.findViewById(R.id.tv__list_forecast_day);
            description = (TextView) view.findViewById(R.id.tv__list_forecast_description);
            max = (TextView) view.findViewById(R.id.tv__list_forecast_max);
            min = (TextView) view.findViewById(R.id.tv__list_forecast_min);
            image = (ImageView) view.findViewById(R.id.iv__list_forecast_image);
        }
    }
}
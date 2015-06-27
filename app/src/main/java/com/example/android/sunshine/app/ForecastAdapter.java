package com.example.android.sunshine.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.sunshine.app.data.Forecast;

import java.util.List;

public class ForecastAdapter extends ArrayAdapter<Forecast> {

    private List<Forecast> forecasts;
    private static final int LAYOUT_ID = R.layout.list_item_forecast;

    public ForecastAdapter(Context context, List<Forecast> forecasts) {
        super(context, LAYOUT_ID, forecasts);
        this.forecasts = forecasts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(LAYOUT_ID, null);
        }

        Forecast forecast = forecasts.get(position);
        if (forecast != null) {
            TextView dayView = (TextView) view.findViewById(R.id.tv__list_forecast_day);
            dayView.setText(forecast.day);

            TextView descriptionView = (TextView) view.findViewById(R.id.tv__list_forecast_description);
            descriptionView.setText(forecast.description);

            TextView maxView = (TextView) view.findViewById(R.id.tv__list_forecast_max);
            maxView.setText(forecast.max + "°");

            TextView minView = (TextView) view.findViewById(R.id.tv__list_forecast_min);
            minView.setText(forecast.min + "°");
        }

        return view;
    }
}

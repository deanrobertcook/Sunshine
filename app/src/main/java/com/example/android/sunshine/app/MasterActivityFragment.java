package com.example.android.sunshine.app;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.sunshine.app.data.Forecast;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MasterActivityFragment extends Fragment {

    public final String TAG = MasterActivityFragment.class.getSimpleName();

    private ForecastAdapter forecastAdapter;
    private ArrayAdapter<String> tempAdapter;
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

        new FetchWeatherTask(getActivity(), tempAdapter).execute(postcode, countryCode);
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
}

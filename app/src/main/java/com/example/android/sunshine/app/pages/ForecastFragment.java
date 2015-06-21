package com.example.android.sunshine.app.pages;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.app.R;
import com.example.android.sunshine.app.stores.ForecastStore;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements ForecastStore.Observer {

    private Container container;
    private ArrayAdapter<String> forecastAdapter;
    public final String TAG = ForecastFragment.class.getSimpleName();

    public static ForecastFragment newInstance() {
        ForecastFragment fragment = new ForecastFragment();

        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.container.getForecastStore().registerObserver(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.container = (Container) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            container.refreshButtonSelected();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        forecastAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_view,
                new ArrayList<String>()
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

        listView.setAdapter(forecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = forecastAdapter.getItem(position)
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    @Override
    public void onForecastChange(List<String> forecasts) {
        forecastAdapter.clear();
        forecastAdapter.addAll(forecasts);
        forecastAdapter.notifyDataSetChanged();
    }

    public interface Container {

        ForecastStore getForecastStore();

        void refreshButtonSelected();
    }

}

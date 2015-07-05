package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;

import timber.log.Timber;


public class MasterActivity extends ActionBarActivity implements MasterFragment.ContainingActivity {

    public static final String TAG = MasterActivity.class.getName();
    public static final String USE_SPECIAL_DAY_KEY = "USE SPECIAL DAY";

    private static final String DETAIL_FRAGMENT_TAG = DetailFragment.class.getName();

    private String currentLocation;
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SunshineSyncAdapter.initializeSyncAdapter(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        setContentView(R.layout.activity_master);
        currentLocation = Utility.getPreferredLocation(this);

        if (findViewById(R.id.weather_detail_container) != null) {
            twoPane = true;
            Uri firstItemUri = WeatherContract.WeatherEntry
                    .buildWeatherLocationWithDate(currentLocation, System.currentTimeMillis());
            if (savedInstanceState == null) {
                setDetailFragment(firstItemUri);
            }
        } else {
            twoPane = false;
            MasterFragment masterFragment = (MasterFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_master);
            masterFragment.setUseSpecialTodayLayout(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String newLocation = Utility.getPreferredLocation(this);
        if (currentLocation != null && !currentLocation.equals(newLocation)) {
            Uri currentItemUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                    currentLocation, System.currentTimeMillis());
            updateWeather();
            setDetailFragment(currentItemUri);
            currentLocation = newLocation;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_view_location:
                sendUserToMaps();
                break;
            case R.id.action_refresh:
                Log.d(TAG, "Refresh clicked");
                updateWeather();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        SunshineSyncAdapter.syncImmediately(this);
    }

    private void sendUserToMaps() {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        String location = Utility.getPreferredLocation(this);
        Uri geoLocationResource = buildLocationUri(location);

        intent.setData(geoLocationResource);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private Uri buildLocationUri(String location) {
        return Uri.parse("geo:0,0").buildUpon()
                .appendQueryParameter("q", location)
                .build();
    }

    @Override
    public void onItemSelected(Uri itemUri) {
        if (twoPane) { //switch out the detail fragment
            setDetailFragment(itemUri);
        } else { //or start the new activity
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(itemUri);

            startActivity(intent);
        }
    }

    private void setDetailFragment(Uri itemUri) {

        DetailFragment detailFragment = (DetailFragment)
                getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if (detailFragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container,
                            DetailFragment.newInstance(itemUri),
                            DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            detailFragment.onLocationChanged(itemUri);
        }
    }
}

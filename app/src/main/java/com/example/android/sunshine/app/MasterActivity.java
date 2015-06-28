package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import timber.log.Timber;


public class MasterActivity extends ActionBarActivity implements MasterFragment.ContainingActivity {

    public static final String TAG = MasterActivity.class.getName();

    private static final String DETAIL_FRAGMENT_TAG = DetailFragment.class.getName();
    private String currentLocation;
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        setContentView(R.layout.activity_master);
        currentLocation = Utility.getPreferredLocation(this);

        if (findViewById(R.id.weather_detail_container) != null) {
            twoPane = true;
        } else {
            twoPane = false;
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
        if (currentLocation != newLocation) {
            MasterFragment masterFragment = (MasterFragment)
                    getFragmentManager().findFragmentById(R.id.fragment_master);

            currentLocation = newLocation;
            masterFragment.onLocationChanged();
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_view_location) {
            sendUserToMaps();
        }

        return super.onOptionsItemSelected(item);
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
    public void onFirstItemLoaded(Uri itemUri) {
        //callback for getting the first loaded forecast item as a default first
        //view for tablets
        if (twoPane) {
            setDetailFragment(itemUri);
        }
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
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.weather_detail_container, DetailFragment.newInstance(itemUri))
                .commit();
    }
}

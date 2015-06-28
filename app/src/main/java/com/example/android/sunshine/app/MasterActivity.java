package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import timber.log.Timber;


public class MasterActivity extends ActionBarActivity {

    public static final String TAG = MasterActivity.class.getName();

    private static final String MASTER_FRAGMENT_TAG = MasterActivityFragment.class.getName();
    private String currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        setContentView(R.layout.activity_master);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, MasterActivityFragment.newInstance(), MASTER_FRAGMENT_TAG)
                    .commit();
        }

        currentLocation = Utility.getPreferredLocation(this);
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
            MasterActivityFragment masterFragment = (MasterActivityFragment)
                    getFragmentManager().findFragmentByTag(MASTER_FRAGMENT_TAG);

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
}

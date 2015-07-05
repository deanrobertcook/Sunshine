package com.example.android.sunshine.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;


public class SettingsActivity extends Activity {

    private static final String TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .add(android.R.id.content, new SettingsFragment(), TAG)
                .commit();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}

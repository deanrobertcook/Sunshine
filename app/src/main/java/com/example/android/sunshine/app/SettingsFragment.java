package com.example.android.sunshine.app;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment
    implements Preference.OnPreferenceChangeListener {

    private static final String TAG = SettingsFragment.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_postcode_key)));
    }

    /**
     * Sets the SettingsFragment as a listener to the preference whose summary (the small
     * grey summary) we want to bind to it's setting menu element. It then triggers the
     * change so that the text appears on the first glance at the settings list.
     * @param preference
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(
                preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), "")
        );
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = newValue.toString();

        if (preference instanceof ListPreference) {
            //TODO logic for listPreferences
        } else if (preference instanceof EditTextPreference) {
            preference.setSummary(value);
        }
        return true;
    }
}

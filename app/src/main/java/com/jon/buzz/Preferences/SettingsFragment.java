package com.jon.buzz.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.jon.buzz.R;
import com.jon.buzz.utils.TimeConverter;

public class SettingsFragment extends PreferenceFragment
		implements SharedPreferences.OnSharedPreferenceChangeListener {

	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		prefs = getPreferenceScreen().getSharedPreferences();

		// Update number picker preference.
		int defaultTimer = prefs.getInt(getString(R.string.pref_key_time_picker), 0);
		findPreference(getString(R.string.pref_key_time_picker))
				.setSummary(new TimeConverter(defaultTimer).toString());

		int prefCount = getPreferenceScreen().getPreferenceCount();
		for (int i = 0; i < prefCount; i++) {
			Preference pref = getPreferenceScreen().getPreference(i);
			onSharedPreferenceChanged(prefs, pref.getKey());
		}
	}

	@Override
	public void onResume() {

		super.onResume();
		prefs.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {

		super.onPause();
		prefs.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

		// If there's no key, do nothing
		if (s == null) {
			return;
		}

		// Get the preference.
		Preference preference = findPreference(s);

		if (s.equals(getString(R.string.pref_key_time_picker))) {

			// For the time picker, populate the summary with the current time selected.
			int defaultTimer = sharedPreferences.getInt(s, 0);
			preference.setSummary(new TimeConverter(defaultTimer).toString());
		} else if (s.equals(getString(R.string.pref_key_notification))) {

			// For the checkboxes set summary to correct string value.
			boolean checked = sharedPreferences.getBoolean(s, true);
			if (checked) {
				preference.setSummary(R.string.pref_notification_true);
			} else {
				preference.setSummary(R.string.pref_notification_false);
			}
		} else if (s.equals(getString(R.string.pref_key_default_timer))) {
			boolean checked = sharedPreferences.getBoolean(s, true);
			if (checked) {
				preference.setSummary(R.string.pref_default_timer_true);
			} else {
				preference.setSummary(R.string.pref_default_timer_false);
			}
		} else if (s.equals(getString(R.string.pref_key_replace_timer))) {
			boolean checked = sharedPreferences.getBoolean(s, true);
			if (checked) {
				preference.setSummary(R.string.pref_replace_timer_true);
			} else {
				preference.setSummary(R.string.pref_replace_timer_false);
			}
		}
	}
}
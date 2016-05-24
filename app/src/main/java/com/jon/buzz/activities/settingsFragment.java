package com.jon.buzz.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.jon.buzz.R;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
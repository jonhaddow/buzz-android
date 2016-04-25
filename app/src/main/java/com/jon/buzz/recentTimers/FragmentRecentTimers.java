package com.jon.buzz.recentTimers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jon.buzz.R;

import java.util.ArrayList;

public class FragmentRecentTimers extends Fragment {

	private final ArrayList<String> mTimers = new ArrayList<>();
	private ArrayAdapter mListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		// Inflate the layout containing a title and body text.
		ViewGroup rootView = (ViewGroup) inflater
				.inflate(R.layout.fragment_recent_timer_list, container, false);

		// Get list view and populate with list adapter
		ListView timer_list = (ListView) rootView.findViewById(R.id.timer_list);
		mListAdapter = new ArrayAdapter<>(
				getContext(),
				android.R.layout.simple_list_item_1,
				mTimers);
		timer_list.setAdapter(mListAdapter);
		return rootView;
	}

	public void addTimerToList(int seconds) {

		// Adds timer to the top of list
		mTimers.add(0, String.valueOf(seconds));
		mListAdapter.notifyDataSetChanged();
	}
}

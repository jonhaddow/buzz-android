package com.jon.buzz.recentTimers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jon.buzz.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FragmentRecentTimers extends Fragment {

	// File name to save current state of list
	private static final String FILE_NAME = "RecentTimers.txt";

	// Max number of items to add to list
	private static final int LIST_LIMIT = 8;

	private ArrayList<String> mTimers = new ArrayList<>();
	private ArrayAdapter mListAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		// Inflate the layout containing a title and body text.
		ViewGroup rootView = (ViewGroup) inflater
				.inflate(R.layout.fragment_recent_timer_list, container, false);

		// Load list from local directory
		LoadList();

		// Get list view and populate with list adapter
		ListView timer_list = (ListView) rootView.findViewById(R.id.timer_list);
		mListAdapter = new ArrayAdapter<>(
				getContext(),
				android.R.layout.simple_list_item_1,
				mTimers);
		timer_list.setAdapter(mListAdapter);
		return rootView;
	}

	private void LoadList() {

		// Load list items from local directory
		File file = new File(getContext().getFilesDir(), FILE_NAME);
		try {
			mTimers = new ArrayList<>(FileUtils.readLines(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addTimerToList(int seconds) {

		// Adds timer to the top of list
		mTimers.add(0, String.valueOf(seconds));

		// Limit list to a set number of items
		if (mTimers.size() == LIST_LIMIT) {
			mTimers.remove(LIST_LIMIT - 1);
		}

		mListAdapter.notifyDataSetChanged();
		saveList();
	}

	private void saveList() {

		// Saved List items in local directory
		File file = new File(getContext().getFilesDir(), FILE_NAME);
		try {
			FileUtils.writeLines(file, mTimers);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

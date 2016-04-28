package com.jon.buzz.recentTimers;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jon.buzz.R;
import com.jon.buzz.interfaces.StartNewTimerListener;
import com.jon.buzz.services.BackgroundCountdown;
import com.jon.buzz.utils.TimeConverter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FragmentRecentTimers extends Fragment implements AdapterView.OnItemClickListener {

	// File name to save current state of list
	private static final String FILE_NAME = "RecentTimers.txt";

	// Max number of items to add to list
	private static final int LIST_LIMIT = 8;

	private ArrayList<String> mTimers = new ArrayList<>();
	private ArrayAdapter mListAdapter;
	private StartNewTimerListener mMainActivityCallback;

	@Override
	public void onAttach(Context context) {

		super.onAttach(context);
		mMainActivityCallback = (StartNewTimerListener) context;
	}

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
		timer_list.setOnItemClickListener(this);
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

	public void addTimerToList(TimeConverter myTimer) {

		// Adds timer to the top of list
		mTimers.add(0, myTimer.toString());

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

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

		// Extract text from list view clicked
		String textFromList = mListAdapter.getItem(i).toString();
		String[] splitText = textFromList.split(":", 3);

		// Separate into hours, minutes, seconds
		final int hours = Integer.parseInt(splitText[0]);
		final int minutes = Integer.parseInt(splitText[1]);
		final int seconds = Integer.parseInt(splitText[2]);

		// Create user query
		String userQuery = "Would you like to start a new ";
		if (hours == 0) {
			if (minutes == 0) {
				userQuery += seconds + " second timer?";
			} else {
				userQuery += minutes + " minute and " + seconds + " second timer?";
			}
		} else {
			userQuery += hours + " hour, " + minutes + " minute and " + seconds + " second timer?";
		}

		// Confirm query with user
		new AlertDialog.Builder(getActivity())
				.setTitle("New Timer")
				.setMessage(userQuery)
				.setPositiveButton("Start Timer", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {

						// Check that service isn't already running
						if (BackgroundCountdown.isRunning) {
							Toast.makeText(getContext(), "Timer already running", Toast.LENGTH_SHORT).show();
							return;
						}

						TimeConverter myTimer = new TimeConverter(hours, minutes, seconds);

						// Pass seconds to main activity to create new timer
						mMainActivityCallback.startNewTimer(myTimer);
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						// Do nothing
					}
				})
				.setInverseBackgroundForced(true).show();

	}
}

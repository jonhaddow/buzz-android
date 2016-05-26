package com.jon.buzz.activities.main_activity_fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jon.buzz.R;
import com.jon.buzz.interfaces.StartNewTimerListener;
import com.jon.buzz.services.BackgroundCountdown;
import com.jon.buzz.utils.TimeConverter;

public class FragmentSetTimer extends Fragment implements View.OnClickListener, View.OnLongClickListener {

	// References id of all digit buttons
	private final int[] mDigitButtons = {
			R.id.b_digit0,
			R.id.b_digit1,
			R.id.b_digit2,
			R.id.b_digit3,
			R.id.b_digit4,
			R.id.b_digit5,
			R.id.b_digit6,
			R.id.b_digit7,
			R.id.b_digit8,
			R.id.b_digit9
	};
	// Holds the Start Timer listener from main activity
	private StartNewTimerListener mMainActivityCallback;
	private View mRootView;

	@Override
	public void onAttach(Context context) {

		super.onAttach(context);
		mMainActivityCallback = (StartNewTimerListener) context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// Get base view of fragment
		mRootView = inflater.inflate(R.layout.fragment_set_timer, container, false);

		// Implement on Long click listener and on click listener for all elements in view
		mRootView.findViewById(R.id.ib_delete).setOnLongClickListener(this);
		mRootView.findViewById(R.id.ib_delete).setOnClickListener(this);
		mRootView.findViewById(R.id.fab_start_timer).setOnClickListener(this);
		for (int digitButton : mDigitButtons) {
			mRootView.findViewById(digitButton).setOnClickListener(this);
		}

		return mRootView;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.ib_delete:
				// When delete button is clicked, collect current display data and shift all digits to the right.
				UISetTimer.removeNumberFromDisplay(UISetTimer.collectDisplayData(mRootView));
				break;
			case R.id.fab_start_timer:
				// When FAB button is clicked.
				onStartTimer();
				break;
			default:
				//When a digit is selected, the value is added to the display.
				UISetTimer.addNumberToDisplay(
						String.valueOf(((Button) v).getText()),
						UISetTimer.collectDisplayData(mRootView.findViewById(R.id.content)));
		}
	}

	/**
	 * Called when the FAB start timer button is selected.
	 */
	private void onStartTimer() {

		// Check if user wants to replace current running timer.
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean replaceTimer = prefs.getBoolean(getString(R.string.pref_key_replace_timer), false);

		// Check that service isn't already running.
		if (BackgroundCountdown.isRunning && !replaceTimer) {
			Toast.makeText(getActivity(), R.string.toast_timer_running, Toast.LENGTH_SHORT).show();
			return;
		}

		// Collect current display values and convert to milliseconds.
		TextView[] displayNumbers = UISetTimer.collectDisplayData(mRootView);
		int hours = Integer.parseInt(String.valueOf(displayNumbers[0].getText()) + String.valueOf(displayNumbers[1].getText()));
		int minutes = Integer.parseInt(String.valueOf(displayNumbers[2].getText() + String.valueOf(displayNumbers[3].getText())));
		int seconds = Integer.parseInt(String.valueOf(displayNumbers[4].getText() + String.valueOf(displayNumbers[5].getText())));
		TimeConverter myTimer = new TimeConverter(hours,minutes,seconds);
		int milliseconds = myTimer.getMilli();

		// Clear Display.
		UISetTimer.clearDisplay(UISetTimer.collectDisplayData(mRootView));

		// Check if user wants to run default timer.
		boolean runDefault = prefs.getBoolean(getString(R.string.pref_key_default_timer), false);
		if (milliseconds == 0) {
			if (!runDefault) {

				// No input and no default timer set. Send toast to inform user.
				Toast.makeText(getActivity(), R.string.toast_no_input, Toast.LENGTH_SHORT).show();
			} else {

				// No input but default timer is set. Get default timer value from preferences
				// and start timer.
				int defaultMilli = prefs.getInt(getString(R.string.pref_key_time_picker), 0);
				if (defaultMilli == 0) return;
				TimeConverter defaultTimer = new TimeConverter(defaultMilli);
				mMainActivityCallback.startNewTimer(defaultTimer);
			}
		} else {

			// Pass time to main activity to create new timer
			mMainActivityCallback.startNewTimer(myTimer);
		}
	}

	@Override
	public boolean onLongClick(View v) {

		UISetTimer.clearDisplay(UISetTimer.collectDisplayData(mRootView));
		return true;
	}
}

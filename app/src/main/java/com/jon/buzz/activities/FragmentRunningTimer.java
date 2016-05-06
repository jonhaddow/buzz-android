package com.jon.buzz.activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jon.buzz.R;
import com.jon.buzz.services.BackgroundCountdown;
import com.jon.buzz.utils.CustomBroadcasts;
import com.jon.buzz.utils.TimeConverter;

public class FragmentRunningTimer extends Fragment implements View.OnClickListener {

	// Views in layout
	private TextView mTvTimeRemaining;
	private ImageView mIvPauseTimer;
	private Context mContext;
	private TextView mTvTimeRemainingLabel;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mContext = getContext();
		View mRootView = inflater.inflate(R.layout.fragment_running_timer, container, false);

		// Get reference to view and set on click listeners
		mTvTimeRemainingLabel = (TextView) mRootView.findViewById(R.id.timeRemainingLabel);
		mTvTimeRemaining = (TextView) mRootView.findViewById(R.id.timeRemaining);
		ImageView mIvAddMin = (ImageView) mRootView.findViewById(R.id.iv_add_min);
		mIvPauseTimer = (ImageView) mRootView.findViewById(R.id.iv_pause_timer);
		ImageView mIvCancelTimer = (ImageView) mRootView.findViewById(R.id.iv_cancel_timer);
		mIvAddMin.setOnClickListener(this);
		mIvPauseTimer.setOnClickListener(this);
		mIvCancelTimer.setOnClickListener(this);

		return mRootView;
	}

	@Override
	public void onResume() {

		super.onResume();

		// Update interface
		updateUI();
	}

	private void updateUI() {

		// Set correct pause/play drawable.
		if (BackgroundCountdown.isPaused) {
			mIvPauseTimer.setImageDrawable(mContext.getDrawable(R.drawable.ic_play_circle));
		} else {
			mIvPauseTimer.setImageDrawable(mContext.getDrawable(R.drawable.ic_pause_circle));
		}

		// Clear time remaining text when no timer is running.
		if (!BackgroundCountdown.isRunning) {
			mTvTimeRemainingLabel.setVisibility(View.INVISIBLE);
			mTvTimeRemaining.setText("");
		}
	}

	public void updateTimeRemaining(int milliseconds) {

		// Convert milliseconds to Timer class
		TimeConverter myTimer = new TimeConverter(milliseconds);

		if (milliseconds < 1) {
			mTvTimeRemainingLabel.setVisibility(View.INVISIBLE);
			mTvTimeRemaining.setText("");
		}
		mTvTimeRemaining.setText(myTimer.toString());
	}

	public void stopTimer() {

		// Cancel all notifications
		((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

		mTvTimeRemainingLabel.setVisibility(View.INVISIBLE);
		mTvTimeRemaining.setText("");
	}

	public void pauseTimer() {

		// Change to play drawable
		mIvPauseTimer.setImageDrawable(mContext.getDrawable(R.drawable.animated_pause2play));
		Animatable temp = (Animatable) mIvPauseTimer.getDrawable();
		temp.start();

	}

	public void resumeTimer() {

		// Change to pause drawable
		mIvPauseTimer.setImageDrawable(mContext.getDrawable(R.drawable.animated_play2pause));
		Animatable temp = (Animatable) mIvPauseTimer.getDrawable();
				temp.start();
	}

	@Override
	public void onClick(View v) {

		// When a button is clicked, send the appropriate broadcast to handle it.
		Intent broadcastIntent = new Intent(CustomBroadcasts.BROADCAST);
		switch (v.getId()) {
			case R.id.iv_add_min:
				broadcastIntent.putExtra("type", CustomBroadcasts.ADD_MIN);
				break;
			case R.id.iv_pause_timer:
				// If it's paused, play. Else, pause.
				if (BackgroundCountdown.isPaused) {
					broadcastIntent.putExtra("type", CustomBroadcasts.PLAY_TIMER);
				} else {
					broadcastIntent.putExtra("type", CustomBroadcasts.PAUSE_TIMER);
				}
				break;
			case R.id.iv_cancel_timer:
				broadcastIntent.putExtra("type", CustomBroadcasts.STOP_TIMER);
		}
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(broadcastIntent);
	}

	public void startNewTimer() {

		mTvTimeRemainingLabel.setVisibility(View.VISIBLE);

	}
}

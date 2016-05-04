package com.jon.buzz.activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jon.buzz.R;
import com.jon.buzz.services.BackgroundCountdown;
import com.jon.buzz.utils.CustomBroadcasts;
import com.jon.buzz.utils.TimeConverter;

public class RunningTimer extends AppCompatActivity implements View.OnClickListener {

	// Manage broadcasts
	private LocalBroadcastManager mBroadcastManager;
	private BroadcastReceiver mBroadcastReceiver;

	// Views in layout
	private TextView mTvTimeRemaining;
	private ImageView mIvPauseTimer;
	private ImageView mIvCancelTimer;
	private ImageView mIvAddMin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_running_timer);

		// Support toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.runningToolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		// Get reference to view and set on click listeners
		mTvTimeRemaining = (TextView) findViewById(R.id.timeRemaining);
		mIvAddMin = (ImageView) findViewById(R.id.iv_add_min);
		mIvPauseTimer = (ImageView) findViewById(R.id.iv_pause_timer);
		mIvCancelTimer = (ImageView) findViewById(R.id.iv_cancel_timer);
		mIvAddMin.setOnClickListener(this);
		mIvPauseTimer.setOnClickListener(this);
		mIvCancelTimer.setOnClickListener(this);

		mBroadcastManager = LocalBroadcastManager.getInstance(this);

		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				// Get type of broadcast.
				String type = intent.getStringExtra("type");

				// Deal with broadcast depending on the type.
				switch (type) {
					case CustomBroadcasts.TIME_REMAINING:
						updateTimeRemaining(intent.getIntExtra(CustomBroadcasts.TIME_REMAINING, 0));
						break;
					case CustomBroadcasts.STOP_TIMER:
						stopTimer();
						break;
					case CustomBroadcasts.PAUSE_TIMER:
						pauseTimer();
						break;
					case CustomBroadcasts.PLAY_TIMER:
						resumeTimer();
						break;
				}
			}
		};
	}

	private void updateTimeRemaining(int milliseconds) {

		// Convert milliseconds to Timer class
		TimeConverter myTimer = new TimeConverter(milliseconds);

		if (milliseconds < 1) {
			return2Main();
		}
		mTvTimeRemaining.setText(myTimer.toString());

	}

	private void stopTimer() {

		// Cancel all notifications
		((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

		return2Main();

	}

	private void pauseTimer() {

		// Change to play drawable
		mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_play_circle));

	}

	private void resumeTimer() {

		// Change to pause drawable
		mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_pause_circle));

	}

	private void return2Main() {

		// Go back to main activity
		Intent back2MainActivity = new Intent(this, MainActivity.class);
		startActivity(back2MainActivity);

	}

	@Override
	protected void onPause() {

		super.onPause();

		// Unregister receiver
		mBroadcastManager.unregisterReceiver(mBroadcastReceiver);
	}

	@Override
	protected void onResume() {

		super.onResume();

		// Register receiver
		mBroadcastManager.registerReceiver(mBroadcastReceiver,
				new IntentFilter(CustomBroadcasts.BROADCAST));

		// Update interface
		updateUI();
	}

	private void updateUI() {

		// Set correct pause/play drawable
		if (BackgroundCountdown.isPaused) {
			mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_play_circle));
		} else {
			mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_pause_circle));
		}


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
		mBroadcastManager.sendBroadcast(broadcastIntent);
	}
}

package com.jon.buzz.activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jon.buzz.R;
import com.jon.buzz.adapters.MyPagerAdapter;
import com.jon.buzz.interfaces.StartTimerListener;
import com.jon.buzz.services.BackgroundCountdown;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements StartTimerListener, View.OnClickListener {

	public static final String STOP_TIMER = "com.jon.buzz.activities.MainActivity.STOP_TIMER";
	public final ArrayList<String> mTimers = new ArrayList<>();
	public ArrayAdapter<String> mListAdapter;

	private TextView mTvTimeRemaining;
	private BroadcastReceiver receiver;
	private int mSeconds;
	private LocalBroadcastManager broadcastManager;

	@Override
	protected void onPause() {

		// Unregister receiver
		broadcastManager.unregisterReceiver(receiver);

		super.onPause();
	}

	@Override
	protected void onResume() {

		// When broadcast is received, update time remaining
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				if (intent != null) {
					updateTimeRemaining(intent.getIntExtra(BackgroundCountdown.SECONDS_REMAINING, 0));
				}
			}
		};

		// Register receiver
		broadcastManager = LocalBroadcastManager.getInstance(this);
		broadcastManager.registerReceiver((receiver),
				new IntentFilter(BackgroundCountdown.TIME_REMAINING));

		super.onResume();
	}

	private void updateTimeRemaining(int timeRemaining) {

		String textToDisplay;
		if (timeRemaining != 0) {
			textToDisplay = "Time remaining... " + timeRemaining + " seconds";
		} else {
			textToDisplay = mSeconds + " second timer complete!";
		}
		mTvTimeRemaining.setText(textToDisplay);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		mTvTimeRemaining = (TextView) findViewById(R.id.tv_time_remaining);
		ImageView mIvStopTimer = (ImageView) findViewById(R.id.iv_stop_timer);
		if (mIvStopTimer != null) {
			mIvStopTimer.setOnClickListener(this);
		}


		// Instantiate view pager and pager adapter
		ViewPager mPager = (ViewPager) findViewById(R.id.pager);
		MyPagerAdapter mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), this);
		if (mPager != null) {
			mPager.setAdapter(mPagerAdapter);
		}
	}

	/**
	 * This method is called from set timer to add a timer to the list adapter on timer list fragment
	 *
	 * @param seconds number of seconds to set timer for
	 */
	@Override
	public void startTimer(int seconds) {

		// Save current timer seconds
		mSeconds = seconds;

		// Start a new countdown service
		Intent countdownIntent = new Intent(this, BackgroundCountdown.class);
		countdownIntent.putExtra("Seconds", seconds);
		startService(countdownIntent);
	}

	@Override
	public void onClick(View view) {

		// Cancel all notifications
		((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

		// Clear time remaining text on bottom bar
		mTvTimeRemaining.setText("");

		// Stop Timer service
		broadcastManager.sendBroadcast(new Intent(STOP_TIMER));
	}
}
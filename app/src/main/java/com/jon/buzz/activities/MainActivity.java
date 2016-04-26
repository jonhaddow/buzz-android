package com.jon.buzz.activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jon.buzz.R;
import com.jon.buzz.adapters.MyPagerAdapter;
import com.jon.buzz.interfaces.StartNewTimerListener;
import com.jon.buzz.recentTimers.FragmentRecentTimers;
import com.jon.buzz.services.BackgroundCountdown;
import com.jon.buzz.utils.CustomBroadcasts;

public class MainActivity extends AppCompatActivity implements StartNewTimerListener, View.OnClickListener {

	private TextView mTvTimeRemaining;
	private BroadcastReceiver mTimeRemainingReceiver;
	private int mSeconds;
	private LocalBroadcastManager broadcastManager;
	private MyPagerAdapter mPagerAdapter;
	private ImageView ivStopTimer;
	private ImageView ivPauseTimer;
	private BroadcastReceiver mStopTimerReceiver;
	private BroadcastReceiver mPauseTimerReceiver;
	private BroadcastReceiver mPlayTimerReceiver;

	@Override
	protected void onPause() {

		// Unregister receiver
		broadcastManager.unregisterReceiver(mTimeRemainingReceiver);
		broadcastManager.unregisterReceiver(mPauseTimerReceiver);
		broadcastManager.unregisterReceiver(mPlayTimerReceiver);
		broadcastManager.unregisterReceiver(mStopTimerReceiver);

		super.onPause();
	}

	@Override
	protected void onResume() {

		// When broadcast is received, update time remaining
		mTimeRemainingReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				if (intent != null) {
					updateTimeRemaining(intent.getIntExtra(CustomBroadcasts.TIME_REMAINING, 0));
				}
			}
		};

		// When a STOP TIMER broadcast is received, stop service
		mStopTimerReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				stopTimer();
			}
		};

		// When a PAUSE TIMER broadcast is received, pause service
		mPauseTimerReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				pauseTimer();
			}
		};

		// When a PLAY TIMER broadcast is received, resume countdown
		mPlayTimerReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				resumeTimer();
			}
		};

		// Register receivers
		broadcastManager = LocalBroadcastManager.getInstance(this);
		broadcastManager.registerReceiver((mTimeRemainingReceiver),
				new IntentFilter(CustomBroadcasts.TIME_REMAINING));
		broadcastManager.registerReceiver(mStopTimerReceiver,
				new IntentFilter(CustomBroadcasts.STOP_TIMER));
		broadcastManager.registerReceiver(mPauseTimerReceiver,
				new IntentFilter(CustomBroadcasts.PAUSE_TIMER));
		broadcastManager.registerReceiver(mPlayTimerReceiver,
				new IntentFilter(CustomBroadcasts.PLAY_TIMER));

		if (BackgroundCountdown.isPaused) {
			ivPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_action_play_timer));
		} else {
			ivPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_action_pause_timer));
		}

		super.onResume();
	}

	private void updateTimeRemaining(int timeRemaining) {

		String mTextToDisplay;
		if (timeRemaining != 0) {
			mTextToDisplay = "Time remaining... " + timeRemaining + " seconds";
		} else {
			mTextToDisplay = mSeconds + " second timer complete!";
		}
		mTvTimeRemaining.setText(mTextToDisplay);
	}

	private void stopTimer() {

		// Cancel all notifications
		((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

		// Clear time remaining text on bottom bar
		mTvTimeRemaining.setText("");

		ivStopTimer.setVisibility(View.INVISIBLE);
		ivPauseTimer.setVisibility(View.INVISIBLE);
	}

	private void pauseTimer() {

		// Change to play drawable
		ivPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_action_play_timer));
	}

	private void resumeTimer() {

		ivPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_action_pause_timer));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Support toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Instantiate view pager and pager adapter
		final ViewPager mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), this);
		if (mPager != null) {
			mPager.setAdapter(mPagerAdapter);
		}

		// Set up Tabbed layout
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		if (tabLayout != null) {
			tabLayout.addTab(tabLayout.newTab().setText(R.string.page_0));
			tabLayout.addTab(tabLayout.newTab().setText(R.string.page_1));
			tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
			tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
				@Override
				public void onTabSelected(TabLayout.Tab tab) {

					if (mPager != null) {
						mPager.setCurrentItem(tab.getPosition());
					}
				}

				@Override
				public void onTabUnselected(TabLayout.Tab tab) {

				}

				@Override
				public void onTabReselected(TabLayout.Tab tab) {

				}
			});
		}

		if (mPager != null) {
			mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
		}

		mTvTimeRemaining = (TextView) findViewById(R.id.tv_time_remaining);

		ivPauseTimer = (ImageView) findViewById(R.id.iv_pause_timer);
		if (ivPauseTimer != null) {
			ivPauseTimer.setOnClickListener(this);
		}

		ivStopTimer = (ImageView) findViewById(R.id.iv_stop_timer);
		if (ivStopTimer != null) {
			ivStopTimer.setOnClickListener(this);
		}

		// If countdown is running enable stop timer and pause timer button
		if (BackgroundCountdown.isMyServiceRunning(this, BackgroundCountdown.class)) {
			ivStopTimer.setVisibility(View.VISIBLE);
			ivPauseTimer.setVisibility(View.VISIBLE);
		} else {
			ivStopTimer.setVisibility(View.INVISIBLE);
			ivPauseTimer.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * This method is called from set timer to add a timer to the list adapter on timer list fragment
	 *
	 * @param seconds number of seconds to set timer for
	 */
	@Override
	public void startNewTimer(int seconds) {

		// Save current timer seconds
		mSeconds = seconds;

		// Start a new countdown service
		Intent countdownIntent = new Intent(this, BackgroundCountdown.class);
		countdownIntent.putExtra("Seconds", seconds);
		startService(countdownIntent);

		// Add timer to recent timers list
		FragmentRecentTimers recentTimers = ((FragmentRecentTimers) mPagerAdapter.getFragment(1));
		if (recentTimers != null) {
			recentTimers.addTimerToList(seconds);
		}

		ivStopTimer.setVisibility(View.VISIBLE);
		ivPauseTimer.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.iv_stop_timer:

				// Send stop timer broadcast to service
				broadcastManager.sendBroadcast(new Intent(CustomBroadcasts.STOP_TIMER));

				break;

			case R.id.iv_pause_timer:

				if (BackgroundCountdown.isPaused) {

					// Send play timer broadcast to service
					broadcastManager.sendBroadcast(new Intent(CustomBroadcasts.PLAY_TIMER));
				} else {

					// Send pause timer broadcast to service
					broadcastManager.sendBroadcast(new Intent(CustomBroadcasts.PAUSE_TIMER));
				}
		}
	}
}
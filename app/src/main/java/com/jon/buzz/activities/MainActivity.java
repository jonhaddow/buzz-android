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

public class MainActivity extends AppCompatActivity implements StartNewTimerListener, View.OnClickListener {

	public static final String STOP_TIMER = "com.jon.buzz.activities.MainActivity.STOP_TIMER";

	private TextView mTvTimeRemaining;
	private BroadcastReceiver receiver;
	private int mSeconds;
	private LocalBroadcastManager broadcastManager;
	private MyPagerAdapter mPagerAdapter;

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

		// Support toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Get time remaining reference
		mTvTimeRemaining = (TextView) findViewById(R.id.tv_time_remaining);

		// Set on click listener for stop timer image.
		ImageView ivStopTimer = (ImageView) findViewById(R.id.iv_stop_timer);
		if (ivStopTimer != null) {
			ivStopTimer.setOnClickListener(this);
		}

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
				public void onTabUnselected(TabLayout.Tab tab) {}

				@Override
				public void onTabReselected(TabLayout.Tab tab) {}
			});
		}

		if(mPager!=null){
			mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
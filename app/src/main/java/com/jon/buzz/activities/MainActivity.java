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
import com.jon.buzz.utils.TimeConverter;

public class MainActivity extends AppCompatActivity implements StartNewTimerListener, View.OnClickListener {

	private TextView mTvTimeRemaining;
	private LocalBroadcastManager broadcastManager;
	private MyPagerAdapter mPagerAdapter;
	private ImageView mIvStopTimer;
	private ImageView mIvPauseTimer;
	private BroadcastReceiver mBroadcastReceiver;

	@Override
	protected void onPause() {

		// Unregister receiver
		broadcastManager.unregisterReceiver(mBroadcastReceiver);

		super.onPause();
	}

	@Override
	protected void onResume() {

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
				}
			}
		};

		// Register receivers
		broadcastManager = LocalBroadcastManager.getInstance(this);
		broadcastManager.registerReceiver((mBroadcastReceiver),
				new IntentFilter(CustomBroadcasts.BROADCAST));

		if (BackgroundCountdown.isPaused) {
			mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_action_play));
		} else {
			mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_action_pause));
		}

		super.onResume();
	}

	private void updateTimeRemaining(int milliRemaining) {

		TimeConverter myTimer = new TimeConverter(milliRemaining);

		String mTextToDisplay;
		if (milliRemaining != 0) {
			mTextToDisplay = "Time remaining: " + myTimer.toString();
		} else {
			mTextToDisplay = "Timer complete!";
			mIvPauseTimer.setVisibility(View.INVISIBLE);
			mIvStopTimer.setVisibility(View.INVISIBLE);
		}
		mTvTimeRemaining.setText(mTextToDisplay);
	}

	private void stopTimer() {

		// Cancel all notifications
		((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

		// Clear time remaining text on bottom bar
		mTvTimeRemaining.setText("");

		mIvStopTimer.setVisibility(View.INVISIBLE);
		mIvPauseTimer.setVisibility(View.INVISIBLE);
	}

	private void pauseTimer() {

		// Change to play drawable
		mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_action_play));
	}

	private void resumeTimer() {

		// Change to pause drawable
		mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_action_pause));
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

		mIvPauseTimer = (ImageView) findViewById(R.id.iv_pause_timer);
		if (mIvPauseTimer != null) {
			mIvPauseTimer.setOnClickListener(this);
		}

		mIvStopTimer = (ImageView) findViewById(R.id.iv_stop_timer);
		if (mIvStopTimer != null) {
			mIvStopTimer.setOnClickListener(this);
		}

		// If countdown is running enable stop timer and pause timer button
		if (BackgroundCountdown.isRunning) {
			if (mIvStopTimer != null) {
				mIvStopTimer.setVisibility(View.VISIBLE);
			}
			mIvPauseTimer.setVisibility(View.VISIBLE);
		} else {
			if (mIvStopTimer != null) {
				mIvStopTimer.setVisibility(View.INVISIBLE);
			}
			mIvPauseTimer.setVisibility(View.INVISIBLE);
		}

	}

	/**
	 * This method is called from set timer to add a timer to the list adapter on timer list fragment
	 *
	 * @param myTimer number of seconds to set timer for
	 */
	@Override
	public void startNewTimer(TimeConverter myTimer) {

		// Start a new countdown service
		Intent countdownIntent = new Intent(this, BackgroundCountdown.class);
		countdownIntent.putExtra("Milli", myTimer.getMilli());
		startService(countdownIntent);

		// Add timer to recent timers list
		FragmentRecentTimers recentTimers = ((FragmentRecentTimers) mPagerAdapter.getFragment(1));
		if (recentTimers != null) {
			recentTimers.addTimerToList(myTimer);
		}

		mIvStopTimer.setVisibility(View.VISIBLE);
		mIvPauseTimer.setVisibility(View.VISIBLE);
		mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_action_pause));
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.iv_stop_timer:

				// Send stop timer broadcast to service
				Intent stopIntent = new Intent(CustomBroadcasts.BROADCAST);
				stopIntent.putExtra("type", CustomBroadcasts.STOP_TIMER);
				broadcastManager.sendBroadcast(stopIntent);

				break;

			case R.id.iv_pause_timer:

				if (BackgroundCountdown.isPaused) {

					// Send play timer broadcast to service
					Intent playIntent = new Intent(CustomBroadcasts.BROADCAST);
					playIntent.putExtra("type", CustomBroadcasts.PLAY_TIMER);
					broadcastManager.sendBroadcast(playIntent);
				} else {

					// Send pause timer broadcast to service
					Intent pauseIntent = new Intent(CustomBroadcasts.BROADCAST);
					pauseIntent.putExtra("type", CustomBroadcasts.PAUSE_TIMER);
					broadcastManager.sendBroadcast(pauseIntent);
				}
		}
	}
}
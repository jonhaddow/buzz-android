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
	private View[] mBottomBarElements = new View[4];

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
						break;
				}
			}
		};

		// Register receivers
		broadcastManager = LocalBroadcastManager.getInstance(this);
		broadcastManager.registerReceiver((mBroadcastReceiver),
				new IntentFilter(CustomBroadcasts.BROADCAST));

		updateBottomBar();

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

		// Make Bottom bar elements invisible
		for (View mBottomBarElement : mBottomBarElements) {
			mBottomBarElement.setVisibility(View.INVISIBLE);
		}
	}

	private void pauseTimer() {

		// Change to play drawable
		mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_play_circle));
	}

	private void resumeTimer() {

		// Change to pause drawable
		mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_pause_circle));
	}

	private void updateBottomBar() {

		// Set correct pause/play drawable
		if (BackgroundCountdown.isPaused) {
			mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_play_circle));
		} else {
			mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_pause_circle));
		}

		// If countdown is running enable stop timer and pause timer button
		for (View mBottomBarElement : mBottomBarElements) {
			if (BackgroundCountdown.isRunning) {
				mBottomBarElement.setVisibility(View.VISIBLE);
			} else {
				mBottomBarElement.setVisibility(View.INVISIBLE);
			}
		}
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

		// Get bottom bar elements
		mBottomBarElements[0] = findViewById(R.id.tv_time_remaining);
		mBottomBarElements[1] = findViewById(R.id.iv_pause_timer);
		mBottomBarElements[2] = findViewById(R.id.iv_stop_timer);
		mBottomBarElements[3] = findViewById(R.id.iv_add_min);
		mTvTimeRemaining = (TextView) mBottomBarElements[0];
		mIvPauseTimer = (ImageView) mBottomBarElements[1];
		mIvStopTimer = (ImageView) mBottomBarElements[2];

		// Set on Click listeners
		for (int i = 1; i < mBottomBarElements.length; i++) {
			mBottomBarElements[i].setOnClickListener(this);
		}

		updateBottomBar();
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

		for (View mBottomBarElement : mBottomBarElements) {
			mBottomBarElement.setVisibility(View.VISIBLE);
		}
		mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_pause_circle));
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

				break;
			case R.id.iv_add_min:

				// Send add minute to timer broadcast to service
				Intent addMinIntent = new Intent(CustomBroadcasts.BROADCAST);
				addMinIntent.putExtra("type", CustomBroadcasts.ADD_MIN);
				broadcastManager.sendBroadcast(addMinIntent);
		}
	}
}
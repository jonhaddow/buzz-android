package com.jon.buzz.activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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

	// Manage broadcasts
	private LocalBroadcastManager broadcastManager;
	private BroadcastReceiver mBroadcastReceiver;

	// Reference to pages
	private MyPagerAdapter mPagerAdapter;

	// Reference to views
	private TextView mTvTimeRemaining;
	private ImageView mIvPauseTimer;
	private ImageView mIvAddMin;
	private ImageView mIvCancelTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Support toolbar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
		setSupportActionBar(toolbar);

		// Instantiate view pager and pager adapter.
		final ViewPager mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), this);
		if (mPager != null) {
			mPager.setAdapter(mPagerAdapter);
		}

		// Set up Tabbed layout.
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

		mTvTimeRemaining = (TextView) findViewById(R.id.timeRemaining);
		mIvAddMin = (ImageView) findViewById(R.id.iv_add_min);
		mIvPauseTimer = (ImageView) findViewById(R.id.iv_pause_timer);
		mIvCancelTimer = (ImageView) findViewById(R.id.iv_cancel_timer);
		mIvAddMin.setOnClickListener(this);
		mIvPauseTimer.setOnClickListener(this);
		mIvCancelTimer.setOnClickListener(this);

		// Manage local broadcasts from this activity.
		broadcastManager = LocalBroadcastManager.getInstance(this);
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				// Get type of broadcast.
				String type = intent.getStringExtra("type");

				// Deal with broadcast depending on the type.
				switch (type) {
					case CustomBroadcasts.TIME_REMAINING:
						int timeRemaining = intent.getIntExtra(CustomBroadcasts.TIME_REMAINING, 0);
						if (timeRemaining < 1) {
							hideBottomBar();
						}
						updateTimeRemaining(timeRemaining);
						break;
					case CustomBroadcasts.CANCEL_TIMER:
						hideBottomBar();
						cancelTimer();
						break;
					case CustomBroadcasts.PAUSE_TIMER:
						pauseTimer();
						break;
					case CustomBroadcasts.PLAY_TIMER:
						resumeTimer();
						break;
					case CustomBroadcasts.REPLAY_TIMER:
						showBottomBar();
				}
			}

		};

	}

	private void hideBottomBar() {

		mTvTimeRemaining.setText("");
		mIvAddMin.setVisibility(View.INVISIBLE);
		mIvPauseTimer.setVisibility(View.INVISIBLE);
		mIvCancelTimer.setVisibility(View.INVISIBLE);
	}

	public void updateTimeRemaining(int milliseconds) {

		// Convert milliseconds to Timer class
		TimeConverter myTimer = new TimeConverter(milliseconds);

		if (milliseconds < 1) {
			mTvTimeRemaining.setText("");
		} else {
			mTvTimeRemaining.setText(myTimer.toString());
		}
	}

	public void cancelTimer() {

		// Cancel all notifications
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

		mTvTimeRemaining.setText("");

		hideBottomBar();
	}

	public void pauseTimer() {

		// Change to play drawable
		mIvPauseTimer.setImageResource(R.drawable.ic_play_circle);
	}

	public void resumeTimer() {

		// Change to pause drawable
		mIvPauseTimer.setImageResource(R.drawable.ic_pause_circle);
	}

	private void showBottomBar() {

		mIvAddMin.setVisibility(View.VISIBLE);
		mIvPauseTimer.setVisibility(View.VISIBLE);
		mIvCancelTimer.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main_activity_menu, menu);
		return true;
	}

	@Override
	protected void onPause() {

		// Unregister receiver
		broadcastManager.unregisterReceiver(mBroadcastReceiver);

		super.onPause();
	}

	@Override
	protected void onResume() {

		super.onResume();

		// Register receivers
		broadcastManager.registerReceiver((mBroadcastReceiver),
				new IntentFilter(CustomBroadcasts.BROADCAST));


		// Set correct pause/play drawable.
		if (BackgroundCountdown.isPaused) {
			mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_play_circle));
		} else {
			mIvPauseTimer.setImageDrawable(getDrawable(R.drawable.ic_pause_circle));
		}

		// Clear time remaining text when no timer is running.
		if (!BackgroundCountdown.isRunning) {
			hideBottomBar();
		} else {
			showBottomBar();
		}

	}

	/**
	 * This method is called from set timer to add a timer to the list adapter on timer list fragment
	 *
	 * @param myTimer number of seconds to set timer for
	 */
	@Override
	public void startNewTimer(TimeConverter myTimer) {

		// Start a new countdown service.
		Intent countdownIntent = new Intent(this, BackgroundCountdown.class);
		countdownIntent.putExtra("Milli", myTimer.getMilli());
		startService(countdownIntent);

		// Add timer to recent timers list.
		FragmentRecentTimers recentTimers = ((FragmentRecentTimers) mPagerAdapter.getFragment(1));
		if (recentTimers != null) {
			recentTimers.addTimerToList(myTimer);
		}

		showBottomBar();
	}

	@Override
	public void onClick(View view) {

		Intent broadcastIntent = new Intent(CustomBroadcasts.BROADCAST);
		switch (view.getId()) {
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
				broadcastIntent.putExtra("type", CustomBroadcasts.CANCEL_TIMER);
		}
		broadcastManager.sendBroadcast(broadcastIntent);
	}
}
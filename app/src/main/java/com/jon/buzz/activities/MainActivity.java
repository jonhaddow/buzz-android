package com.jon.buzz.activities;

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
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

	// text on bottom bar
	private TextView mTvShowRunningTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Support toolbar.
		Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
		if (toolbar != null) {
			toolbar.setTitle("Create a Timer");
		}
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

		// Get bottom bar text view and set on click listener.
		mTvShowRunningTimer = (TextView) findViewById(R.id.showRunningTimer);
		if (mTvShowRunningTimer != null) {
			mTvShowRunningTimer.setOnClickListener(this);
		}

		// Manage local broadcasts from this activity.
		broadcastManager = LocalBroadcastManager.getInstance(this);
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				// Get type of broadcast.
				String type = intent.getStringExtra("type");

				// Deal with broadcast depending on the type.
				if (type.equals(CustomBroadcasts.TIME_REMAINING)) {
					updateTimeRemaining(intent.getIntExtra(CustomBroadcasts.TIME_REMAINING, 0));
				}
			}

		};
	}

	/**
	 * Update current time on bottom bar.
	 *
	 * @param milliRemaining milliseconds remaining
	 */
	private void updateTimeRemaining(int milliRemaining) {

		// Convert milliseconds into timer class.
		TimeConverter myTimer = new TimeConverter(milliRemaining);

		// Show time remaining and allow bar to be clickable, unless time is up.
		String text2Display;
		if (milliRemaining < 1) {
			text2Display = "";
			mTvShowRunningTimer.setClickable(false);
		} else {
			text2Display = "Current Timer: " + myTimer.toString();
			mTvShowRunningTimer.setClickable(true);
		}
		mTvShowRunningTimer.setText(text2Display);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main_activity_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
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

		if (!BackgroundCountdown.isRunning) {
			updateTimeRemaining(0);
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

		// Go to running timer activity.
		Intent newActivity = new Intent(getApplication(), RunningTimer.class);
		startActivity(newActivity);
	}

	@Override
	public void onClick(View view) {

		Intent newActivity = new Intent(getApplication(), RunningTimer.class);
		startActivity(newActivity);
	}
}
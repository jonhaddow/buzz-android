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
import android.view.Menu;
import android.view.MenuItem;

import com.jon.buzz.R;
import com.jon.buzz.adapters.MyPagerAdapter;
import com.jon.buzz.interfaces.StartNewTimerListener;
import com.jon.buzz.recentTimers.FragmentRecentTimers;
import com.jon.buzz.services.BackgroundCountdown;
import com.jon.buzz.utils.CustomBroadcasts;
import com.jon.buzz.utils.TimeConverter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity implements StartNewTimerListener {

	// Manage broadcasts
	private LocalBroadcastManager broadcastManager;
	private BroadcastReceiver mBroadcastReceiver;

	// Reference to pages
	private MyPagerAdapter mPagerAdapter;
	private SlidingUpPanelLayout mBottomPanel;
	private FragmentRunningTimer runningTimerFragment;

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

		mBottomPanel = (SlidingUpPanelLayout) findViewById(R.id.slidingPanelLayout);

		runningTimerFragment = (FragmentRunningTimer) getSupportFragmentManager().findFragmentById(R.id.fragmentRunningTimer);

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
						if (timeRemaining == 0) {
							stopTimer();
						}
						runningTimerFragment.updateTimeRemaining(timeRemaining);
						break;
					case CustomBroadcasts.STOP_TIMER:
						stopTimer();
						runningTimerFragment.stopTimer();
						break;
					case CustomBroadcasts.PAUSE_TIMER:
						runningTimerFragment.pauseTimer();
						break;
					case CustomBroadcasts.PLAY_TIMER:
						runningTimerFragment.resumeTimer();
						break;
				}
			}

		};
	}

	/**
	 * Disable bottom bar.
	 */
	private void stopTimer() {

		// Show time remaining and allow bottom bar to be clickable, unless time is up.
		mBottomPanel.setTouchEnabled(false);
		mBottomPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
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
			stopTimer();
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

		// Update bottom bar fragment.
		runningTimerFragment.startNewTimer();
		mBottomPanel.setTouchEnabled(true);
	}
}
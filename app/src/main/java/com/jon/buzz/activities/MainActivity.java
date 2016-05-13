package com.jon.buzz.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.jon.buzz.R;
import com.jon.buzz.adapters.MyPagerAdapter;
import com.jon.buzz.interfaces.StartNewTimerListener;
import com.jon.buzz.recentTimers.FragmentRecentTimers;
import com.jon.buzz.services.BackgroundCountdown;
import com.jon.buzz.utils.CustomBroadcasts;
import com.jon.buzz.utils.TimeConverter;

public class MainActivity extends AppCompatActivity implements StartNewTimerListener {

	// Manage broadcasts
	private LocalBroadcastManager broadcastManager;
	private BroadcastReceiver mBroadcastReceiver;

	// Reference to pages
	private MyPagerAdapter mPagerAdapter;
	private View mBottomSheet;
	private FragmentRunningTimer runningTimerFragment;
	private BottomSheetBehavior mBottomSheetBehavior;

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

		mBottomSheet = findViewById(R.id.fragmentRunningTimer);
		if (mBottomSheet != null) {
			mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
		}

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
							hidePanel();
						}
						runningTimerFragment.updateTimeRemaining(timeRemaining);
						break;
					case CustomBroadcasts.CANCEL_TIMER:
						hidePanel();
						runningTimerFragment.cancelTimer();
						break;
					case CustomBroadcasts.PAUSE_TIMER:
						runningTimerFragment.pauseTimer();
						break;
					case CustomBroadcasts.PLAY_TIMER:
						runningTimerFragment.resumeTimer();
						break;
					case CustomBroadcasts.REPLAY_TIMER:
						showPanel();
				}
			}

		};
	}

	/**
	 * Disable bottom bar.
	 */
	private void hidePanel() {

		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	}

	private void showPanel() {

		mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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

		if (!BackgroundCountdown.isRunning) {
			hidePanel();
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
		BottomSheetDialogFragment bottomSheetDialogFragment = new FragmentRunningTimer();
		bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
	}
}
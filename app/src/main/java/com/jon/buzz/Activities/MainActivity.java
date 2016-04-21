package com.jon.buzz.Activities;

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

import com.jon.buzz.Adapters.MyPagerAdapter;
import com.jon.buzz.Interfaces.StartTimerListener;
import com.jon.buzz.R;
import com.jon.buzz.Services.BackgroundCountdown;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements StartTimerListener, View.OnClickListener {

	public static final String STOP_COUNTDOWN = "STOP_COUNTDOWN";
	public ArrayList<String> mTimers = new ArrayList<>();
	public ArrayAdapter<String> mListAdapter;
	ViewPager mPager;
	private TextView mTvTimeRemaining;
	private ImageView mIvStopTimer;
	private BroadcastReceiver receiver;
	private int mSeconds;

	@Override
	protected void onStart() {

		LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
				new IntentFilter(BackgroundCountdown.SECONDS_REMAINING)
		);
		super.onStart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				if (intent != null) {
					mSeconds = intent.getIntExtra(BackgroundCountdown.SECONDS_REMAINING, 0);
					mTvTimeRemaining.setText(String.valueOf(mSeconds));
				}

			}
		};

		mTvTimeRemaining = (TextView) findViewById(R.id.tv_time_remaining);
		mIvStopTimer = (ImageView) findViewById(R.id.iv_stop_timer);
		if (mIvStopTimer != null) {
			mIvStopTimer.setOnClickListener(this);
		}


		// Instantiate view pager and pager adapter
		mPager = (ViewPager) findViewById(R.id.pager);
		MyPagerAdapter mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), this);
		if (mPager != null) {
			mPager.setAdapter(mPagerAdapter);
		}
	}

	@Override
	protected void onStop() {

		LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
		super.onStop();
	}

	/**
	 * This method is called from set timer to add a timer to the list adapter on timer list fragment
	 *
	 * @param seconds
	 */
	@Override
	public void startTimer(int seconds) {

		// Start a new countdown service
		Intent countdownIntent = new Intent(this, BackgroundCountdown.class);
		countdownIntent.putExtra("Seconds", seconds);
		startService(countdownIntent);


	}

	@Override
	public void onClick(View view) {

	}
}
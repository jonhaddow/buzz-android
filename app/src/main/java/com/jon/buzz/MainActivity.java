package com.jon.buzz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements StartTimerListener, View.OnClickListener {

    public static final String STOP_COUNTDOWN = "STOP_COUNTDOWN";
    protected ArrayList<String> mTimers = new ArrayList<>();
    protected ArrayAdapter<String> mListAdapter;
    ViewPager mPager;
	private TextView mTvTimeRemaining;
	private ImageView mIvStopTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
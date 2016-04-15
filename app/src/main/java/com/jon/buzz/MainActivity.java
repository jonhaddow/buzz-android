package com.jon.buzz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SetTimer.AddTimerListener {

    public static final String STOP_COUNTDOWN = "STOP_COUNTDOWN";
    /**
     * This array adapter holds the list of all timers
     */
    protected ArrayList<String> mTimers = new ArrayList<>();
    protected ArrayAdapter<String> mListAdapter;
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readTimers();

        // Instantiate view pager and pager adapter
        mPager = (ViewPager) findViewById(R.id.pager);
        MyPagerAdapter mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        if (mPager != null) {
            mPager.setAdapter(mPagerAdapter);
        }

    }

    /**
     * This method reads the current set of timers from a file
     */
    private void readTimers() {
        // Go to app directory
        File filesDir = getFilesDir();

        // Go to timers file
        File timers = new File(filesDir, "Timers.txt");
        try {
            mTimers = new ArrayList<>(FileUtils.readLines(timers));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onStart() {


        // If requested go straight to the timerList page in app
        String type = getIntent().getStringExtra("type");
        if (type != null) {
            if (type.equals("TimerList")) {
                mPager.setCurrentItem(1);
            } else {
                int seconds = getIntent().getIntExtra("seconds", 0);
                mTimers.remove(mTimers.size()-1);

                this.sendBroadcast(new Intent(STOP_COUNTDOWN));
            }
        }
        super.onStart();
    }

    /**
     * This method is called from set timer to add a timer to the list adapter on timer list fragment
     *
     * @param myTimer
     */
    @Override
    public void addTimerToList(int myTimer) {
        mPager.setCurrentItem(1, true);
        mTimers.add("Timer " + +myTimer + " second timer");
        mListAdapter.notifyDataSetChanged();
        saveTimers();
    }

    /**
     * This method saves the current set of timers to a file
     */
    public void saveTimers() {

        // Go to app directory
        File filesDir = getFilesDir();

        // Go to timers file
        File timers = new File(filesDir, "Timers.txt");
        try {
            // Write items to file
            FileUtils.writeLines(timers, mTimers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
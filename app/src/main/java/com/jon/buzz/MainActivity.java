package com.jon.buzz;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SetTimer.AddTimerListener {

    /**
     * This array adapter holds the list of all timers
     */
    protected ArrayAdapter<String> mListAdapter;
    protected ArrayList<String> mTimers = new ArrayList<>();
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

    @Override
    protected void onStart() {

        // If requested go straight to the timerList page in app
        String page = getIntent().getStringExtra("setPage");
        if (page != null) {
            if (page.equals("timerList")) {
                mPager.setCurrentItem(1);
            }
        }
        super.onStart();
    }

    /**
     * When a digit is selected, the value is added to the display
     *
     * @param view This is the digit selected
     */
    @SuppressWarnings("unused")
    public void onDigitClick(View view) {

        addNumberToDisplay(
                String.valueOf(((Button) view).getText()),
                SetTimer.collectDisplayData(findViewById(R.id.content)));
    }

    /**
     * Add the given digit value to the array of display numbers
     * and shift all number one to the left
     *
     * @param digitValue     Digit to be added to the display
     * @param displayNumbers Current state of display
     */
    private void addNumberToDisplay(String digitValue, TextView[] displayNumbers) {

        // If display is not full...
        if (displayNumbers[0].getText().equals("0")) {
            // Go through array and shift values to left one space
            for (int i = 0; i < displayNumbers.length; i++) {
                if (i == displayNumbers.length - 1) {
                    // Add digit value to the end
                    displayNumbers[i].setText(digitValue);
                } else {
                    displayNumbers[i].setText(displayNumbers[i + 1].getText());
                }
            }
        }
    }

    /**
     * This method is called from set timer to add a timer to the list adapter on timer list fragment
     *
     * @param myTimer
     */
    @Override
    public void addTimerToList(int myTimer) {
        mPager.setCurrentItem(1, true);
        mTimers.add(myTimer + " second timer");
        mListAdapter.notifyDataSetChanged();
        saveTimers();
    }

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
}
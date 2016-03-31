package com.jon.buzz;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SetTimer.AddTimerListener {

    /**
     * This array adapter holds the list of all timers
     */
    protected ArrayAdapter<String> mListAdapter;

    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate view pager and pager adapter
        mPager = (ViewPager) findViewById(R.id.pager);
        MyPagerAdapter mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        if (mPager != null) {
            mPager.setAdapter(mPagerAdapter);
        }
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
        mListAdapter.add(myTimer + " second timer");
    }
}
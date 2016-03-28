package com.jon.buzz;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.TaskStackBuilder;
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
    protected ArrayAdapter<String> listAdapter;

    ViewPager mPager;

    /**
     * This method is called when the activity is created
     *
     * @param savedInstanceState The previous state of the activity
     */
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
     * @param digitValue Digit to be added to the display
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
     * @param myTimer
     */
    @Override
    public void addTimerToList(MyTimer myTimer) {
        mPager.setCurrentItem(1,true);
        startService(new Intent(this,BackgroundCountdown.class));
        listAdapter.add(myTimer.getLength() + " second timer");
    }

    protected void showNotification(String title, String content, boolean autoCancel) {

        // Tell notification manager to notify user
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // TODO: 24/03/2016 Set delete intent when notification is cleared, to delete timer

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        // Set stack as pending intent for when the notification is clicked
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        final Notification.Builder notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setColor(getResources().getColor(R.color.colorPrimary, null))
                .addAction(new Notification.Action.Builder(
                        Icon.createWithResource(this,R.drawable.ic_stop_timer),
                        "Stop timer",
                        pendingIntent).build())
                .setAutoCancel(autoCancel)
                .setOngoing(true);

        notificationManager.notify(0,notification.build());



    }


}
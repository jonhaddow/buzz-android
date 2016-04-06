package com.jon.buzz;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.support.v4.app.TaskStackBuilder;

import static com.jon.buzz.R.drawable.ic_stat_name;

public class BackgroundCountdown extends IntentService {

    protected BroadcastReceiver stopServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }
    };
    private NotificationManager mNotificationManager;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public BackgroundCountdown() {
        super("BackgroundCountdown Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Get the length of the timer in seconds
        int seconds = intent.getIntExtra("Seconds",0);

        // Initialise Notification Manager
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent onNotificationClickIntent = createOnNotificationClickIntent();

        PendingIntent onStopTimerClickIntent = createOnStopTimerClickIntent();

        // Countdown from number of seconds to 0
        for (int i = seconds; i > 0; i--) {
            try {
                // Create notification that shows while countdown is running
                Notification.Builder runningNotification = new Notification.Builder(this)
                        .setSmallIcon(ic_stat_name)
                        .setContentTitle("Buzz")
                        .setContentText(i + " seconds remaining...")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setAutoCancel(false)
                        .addAction(new Notification.Action.Builder(
                                Icon.createWithResource(this, R.drawable.ic_action_stop_timer),
                                "Stop Timer",
                                onNotificationClickIntent).build()
                        )
                        .setOngoing(true);
                startForeground(2, runningNotification.build());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        stopForeground(true);

        // Show final notification to indicate that the countdown has finished.
        final Notification.Builder endNotification = new Notification.Builder(this)
                .setSmallIcon(ic_stat_name)
                .setContentTitle("Buzz")
                .setContentText(seconds + " second timer is complete")
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(onNotificationClickIntent)
                .setAutoCancel(true);
        mNotificationManager.notify(1, endNotification.build());



    }

    private PendingIntent createOnStopTimerClickIntent() {

        // Creates an intent for MainActivity to load timerList fragment
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("setPage", "timerList");

        // Create artificial back stack for the intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);

        // Set stack as pending intent for when the notification is clicked
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        return pendingIntent;
    }

    private PendingIntent createOnNotificationClickIntent() {

        // Creates an intent for MainActivity to load timerList fragment
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("setPage", "timerList");

        // Create artificial back stack for the intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);

        // Set stack as pending intent for when the notification is clicked
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        return pendingIntent;
    }

}

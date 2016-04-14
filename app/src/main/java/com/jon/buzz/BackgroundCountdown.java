package com.jon.buzz;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;

import static com.jon.buzz.R.drawable.ic_stat_name;

public class BackgroundCountdown extends Service {

    private NotificationManager mNotificationManager;
    private int mSeconds;
    private PendingIntent onNotificationClickIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get the length of the timer in seconds
        mSeconds = intent.getIntExtra("Seconds", 0);

        // Initialise Notification Manager
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        onNotificationClickIntent = createOnNotificationClickIntent();

        PendingIntent onStopTimerClickIntent = createOnStopTimerClickIntent();

        new CountDownTimer(mSeconds * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                // Create notification that shows while countdown is running
                Notification.Builder runningNotification = new Notification.Builder(getApplicationContext())
                        .setSmallIcon(ic_stat_name)
                        .setContentTitle("Buzz")
                        .setContentText(millisUntilFinished / 1000 + " seconds remaining...")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setAutoCancel(false)
                        .addAction(new Notification.Action.Builder(
                                Icon.createWithResource(getApplicationContext(), R.drawable.ic_action_stop_timer),
                                "Stop Timer",
                                onNotificationClickIntent).build()
                        )
                        .setOngoing(true);
                startForeground(2, runningNotification.build());
            }

            @Override
            public void onFinish() {
                stopForeground(true);

                // Show final notification to indicate that the countdown has finished.
                final Notification.Builder endNotification = new Notification.Builder(getApplicationContext())
                        .setSmallIcon(ic_stat_name)
                        .setContentTitle("Buzz")
                        .setContentText(mSeconds + " second timer is complete")
                        .setPriority(Notification.PRIORITY_MAX)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setContentIntent(onNotificationClickIntent)
                        .setAutoCancel(true);
                mNotificationManager.notify(1, endNotification.build());
                System.out.println("FINISH!");
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
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

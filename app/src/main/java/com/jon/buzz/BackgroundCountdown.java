package com.jon.buzz;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;

import static com.jon.buzz.R.drawable.ic_stat_name;

public class BackgroundCountdown extends Service {

    private int mSeconds;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager mNotificationManager;
    private PendingIntent onNotificationClickIntent;
    private PendingIntent onStopTimerClickIntent;

     @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return super.registerReceiver(receiver, filter);
    }

    public class UserStopServiceReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            stopSelf();
        }
    }

    @Override
    public void onCreate() {

        registerReceiver(new BackgroundCountdown.UserStopServiceReceiver(),  new IntentFilter(MainActivity.STOP_COUNTDOWN));

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Get the length of the timer in seconds
        mSeconds = intent.getIntExtra("Seconds", 0);

        // Initialise Notification Manager
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create pending intents for notifications
        onNotificationClickIntent = createOnNotificationClickIntent();
        onStopTimerClickIntent = createOnNotificationStopClickIntent();

        // Start countdown service
        new CountDownTimer(mSeconds * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                // Create notification that shows while countdown is running
                Notification.Builder runningNotification = new Notification.Builder(getApplicationContext())
                        .setSmallIcon(ic_stat_name)
                        .setContentTitle(getString(R.string.notification_header))
                        .setContentText(millisUntilFinished / 1000 + getString(R.string.notification_message_running))
                        .setPriority(Notification.PRIORITY_MAX)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setAutoCancel(false)
                        .addAction(new Notification.Action.Builder(
                                Icon.createWithResource(getApplicationContext(), R.drawable.ic_action_stop_timer),
                                "Stop Timer",
                                onStopTimerClickIntent).build()
                        )
                        .setContentIntent(onNotificationClickIntent)
                        .setOngoing(true);
                startForeground(2, runningNotification.build());
            }

            @Override
            public void onFinish() {
                stopForeground(true);

                // Show final notification to indicate that the countdown has finished.
                final Notification.Builder endNotification = new Notification.Builder(getApplicationContext())
                        .setSmallIcon(ic_stat_name)
                        .setContentTitle(getString(R.string.notification_header))
                        .setContentText(mSeconds + getString(R.string.notification_message_finished))
                        .setPriority(Notification.PRIORITY_MAX)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .setContentIntent(onNotificationClickIntent)
                        .setAutoCancel(true);
                mNotificationManager.notify(1, endNotification.build());
            }
        }.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        wakeLock.release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private PendingIntent createOnNotificationClickIntent() {

        // Creates an intent for MainActivity to load timerList fragment
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("type", "TimerList");
        notificationIntent.setAction("onNotification");

        // Create artificial back stack for the intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);

        // Set stack as pending intent for when the notification is clicked
        return stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_ONE_SHOT
        );
    }

    private PendingIntent createOnNotificationStopClickIntent() {

        // Creates an intent for MainActivity to load timerList fragment
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("type", "StopTimer");
        notificationIntent.putExtra("seconds",mSeconds);
        notificationIntent.setAction("onNotificationStop");

        // Create artificial back stack for the intent
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);

        // Set stack as pending intent for when the notification is clicked
        return stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_ONE_SHOT
        );
    }
}

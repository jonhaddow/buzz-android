package com.jon.buzz.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.jon.buzz.utils.CustomBroadcasts;
import com.jon.buzz.utils.Notifications;
import com.jon.buzz.utils.TimeConverter;

public class BackgroundCountdown extends Service {

    private static final int COUNT_DOWN_INTERVAL = 100;
    public static boolean isPaused = false;
    public static boolean isRunning = false;
    private NotificationManager mNotificationManager;
    private PowerManager.WakeLock mWakeLock;
    private LocalBroadcastManager broadcastManager;
    private CountDownTimer mCountDown;
    private int mMilliRemaining;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    public void onCreate() {

        isRunning = true;
        isPaused = false;

        // Get notification manager
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // Get type of broadcast.
                String type = intent.getStringExtra("type");

                // Deal with broadcast depending on type.
                switch (type) {
                    case CustomBroadcasts.STOP_TIMER:
                        stopTimer();
                        break;
                    case CustomBroadcasts.PAUSE_TIMER:
                        pauseTimer();
                        break;
                    case CustomBroadcasts.PLAY_TIMER:
                        resumeTimer();
                        break;
                    case CustomBroadcasts.ADD_MIN:
                        addMin();
                }
            }
        };

        // Register receivers
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(mBroadcastReceiver,
                new IntentFilter(CustomBroadcasts.BROADCAST));

        // Acquire wake lock
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        mWakeLock.acquire();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Get the length of the timer and start countdown.
        int mMilliseconds = intent.getIntExtra("Milli", 0);
        startCountdownTimer(mMilliseconds);

        return super.onStartCommand(intent, flags, startId);
    }

    private void startCountdownTimer(int milli) {

        // Start countdown service
        mCountDown = new CountDownTimer(milli, COUNT_DOWN_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {

                mMilliRemaining = (int) millisUntilFinished;

                TimeConverter myTimer = new TimeConverter(mMilliRemaining);

                // Start foreground notification with time remaining
                startForeground(1, Notifications.setupRunningNotification(getApplicationContext(), myTimer).build());

                // Send remaining milliseconds to main activity to update UI
                sendResult(mMilliRemaining);
            }

            @Override
            public void onFinish() {

                // Send broadcast to main activity to update UI
                sendResult(0);

                stopForeground(true);

                // Notify user that timer has stopped
                mNotificationManager.notify(1, Notifications.setupFinishedNotification(getApplicationContext()).build());

                stopSelf();
            }
        }.start();
    }

    /**
     * Send broadcast to main activity with the seconds remaining.
     *
     * @param milliRemaining seconds remaining
     */
    private void sendResult(int milliRemaining) {

        Intent intent = new Intent(CustomBroadcasts.BROADCAST);
        intent.putExtra("type", CustomBroadcasts.TIME_REMAINING);
        intent.putExtra(CustomBroadcasts.TIME_REMAINING, milliRemaining);
        broadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        // unregister mStopTimerReceiver
        broadcastManager.unregisterReceiver(mBroadcastReceiver);

        // Release wake lock
        mWakeLock.release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void addMin() {
        System.out.println("One minute added");

        // Cancel previous countdown.
        mCountDown.cancel();

        // Start new countdown with current time remaining plus 1 min.
        TimeConverter myTimer = new TimeConverter(mMilliRemaining + (1000 * 60));
        startCountdownTimer(myTimer.getMilli());
    }

    private void stopTimer() {

        System.out.println("Timer Stopped");

        // Cancel notifications, countdown and stop service
        mNotificationManager.cancelAll();
        if (mCountDown != null) {
            System.out.println("Countdown cancelled");
            mCountDown.cancel();
        }
        stopSelf();
    }

    private void pauseTimer() {
        System.out.println("Timer Paused");

        isPaused = true;

        mCountDown.cancel();

        TimeConverter myTimer = new TimeConverter(mMilliRemaining);

        startForeground(1, Notifications.setupPausedNotification(getApplicationContext(), myTimer).build());

        sendResult(mMilliRemaining);
    }

    private void resumeTimer() {
        System.out.println("Timer Resumed");

        isPaused = false;

        // Restart countdown service
        startCountdownTimer(mMilliRemaining);
    }
}


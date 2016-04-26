package com.jon.buzz.services;

import android.app.ActivityManager;
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

	public static final int COUNT_DOWN_INTERVAL = 1000;
	public static boolean isPaused = false;

	private int mMilliseconds;
	private NotificationManager mNotificationManager;
	private PowerManager.WakeLock mWakeLock;
	private BroadcastReceiver mStopTimerReceiver;
	private LocalBroadcastManager broadcastManager;
	private CountDownTimer mCountDown;
	private BroadcastReceiver mPauseTimerReceiver;
	private int mMilliRemaining;
	private BroadcastReceiver mPlayTimerReceiver;

	/**
	 * Checks that a service is currently running
	 *
	 * @param context      Application context
	 * @param serviceClass Service which is checked to see if running
	 * @return true if service is running.
	 */
	public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {

		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onCreate() {

		isPaused = false;

		// When a STOP TIMER broadcast is received, stop service
		mStopTimerReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				// Cancel notifications, countdown and stop service
				mNotificationManager.cancelAll();
				if (mCountDown != null) {
					mCountDown.cancel();
				}
				stopSelf();
			}
		};

		// When a PAUSE TIMER broadcast is received, pause service
		mPauseTimerReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				pauseTimer();
			}
		};

		// When a PLAY TIMER broadcast is received, resume countdown
		mPlayTimerReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				resumeTimer();
			}
		};

		// Register receivers
		broadcastManager = LocalBroadcastManager.getInstance(this);
		broadcastManager.registerReceiver(mStopTimerReceiver,
				new IntentFilter(CustomBroadcasts.STOP_TIMER));
		broadcastManager.registerReceiver(mPauseTimerReceiver,
				new IntentFilter(CustomBroadcasts.PAUSE_TIMER));
		broadcastManager.registerReceiver(mPlayTimerReceiver,
				new IntentFilter(CustomBroadcasts.PLAY_TIMER));

		// Acquire wake lock
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"MyWakelockTag");
		mWakeLock.acquire();

		super.onCreate();
	}

	private void pauseTimer() {

		isPaused = true;

		mCountDown.cancel();

		TimeConverter myTimer = new TimeConverter(mMilliRemaining);

		startForeground(1, Notifications.setupPausedNotification(getApplicationContext(), myTimer).build());

		sendResult(mMilliRemaining);
	}

	private void resumeTimer() {

		isPaused = false;

		// Restart countdown service
		startCountdownTimer(mMilliRemaining);
	}

	/**
	 * Send broadcast to main activity with the seconds remaining.
	 *
	 * @param milliRemaining seconds remaining
	 */
	private void sendResult(int milliRemaining) {

		Intent intent = new Intent(CustomBroadcasts.TIME_REMAINING);
		intent.putExtra(CustomBroadcasts.TIME_REMAINING, milliRemaining);
		broadcastManager.sendBroadcast(intent);
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

				// Send remaining seconds to main activity to update UI
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

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Get the length of the timer in milliseconds
		mMilliseconds = intent.getIntExtra("Milli", 0);

		// Get notification manager
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		startCountdownTimer(mMilliseconds);

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {

		// unregister mStopTimerReceiver
		broadcastManager.unregisterReceiver(mStopTimerReceiver);
		broadcastManager.unregisterReceiver(mPauseTimerReceiver);
		broadcastManager.unregisterReceiver(mPlayTimerReceiver);

		// Release wake lock
		mWakeLock.release();
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}
}


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

public class BackgroundCountdown extends Service {

	private int mSeconds;
	private NotificationManager mNotificationManager;
	private PowerManager.WakeLock mWakeLock;
	private BroadcastReceiver mStopTimerReceiver;
	private LocalBroadcastManager broadcastManager;
	private CountDownTimer mCountDown;
	private BroadcastReceiver mPauseTimerReceiver;
	private int mTimeRemaining;
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
		broadcastManager =  LocalBroadcastManager.getInstance(this);
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

	private void resumeTimer() {

		// Restart countdown service
		startCountdownTimer(mTimeRemaining);
	}

	private void pauseTimer() {

		mCountDown.cancel();

		startForeground(1, Notifications.setupPausedNotification(getApplicationContext(),mTimeRemaining).build());

		sendResult(mTimeRemaining);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Get the length of the timer in seconds
		mSeconds = intent.getIntExtra("Seconds", 0);

		// Get notification manager
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		startCountdownTimer(mSeconds);

		return super.onStartCommand(intent, flags, startId);
	}

	private void startCountdownTimer(int seconds) {

		// Start countdown service
		mCountDown = new CountDownTimer(seconds * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {

				// Convert milliseconds to seconds
				mTimeRemaining = (int) millisUntilFinished / 1000;

				// Start foreground notification with time remaining
				startForeground(1, Notifications.setupRunningNotification(getApplicationContext(), mTimeRemaining).build());

				// Send remaining seconds to main activity to update UI
				sendResult(mTimeRemaining);
			}

			@Override
			public void onFinish() {

				// Send broadcast to main activity to update UI
				sendResult(0);

				stopForeground(true);

				// Notify user that timer has stopped
				mNotificationManager.notify(1, Notifications.setupFinishedNotification(getApplicationContext(), mSeconds).build());

				stopSelf();
			}
		}.start();
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

	/**
	 * Send broadcast to main activity with the seconds remaining.
	 *
	 * @param timeRemaining seconds remaining
	 */
	private void sendResult(int timeRemaining) {

		Intent intent = new Intent(CustomBroadcasts.TIME_REMAINING);
		intent.putExtra(CustomBroadcasts.TIME_REMAINING, timeRemaining);
		broadcastManager.sendBroadcast(intent);
	}
}


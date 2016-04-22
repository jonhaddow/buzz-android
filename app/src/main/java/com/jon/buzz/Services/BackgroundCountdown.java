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

import com.jon.buzz.activities.MainActivity;
import com.jon.buzz.utils.Notifications;

public class BackgroundCountdown extends Service {

	public static final String SECONDS_REMAINING = "com.jon.buzz.services.SECONDS_REMAINING";
	public static final String TIME_REMAINING = "com.jon.buzz.services.TIME_REMAINING";
	private int mSeconds;
	private NotificationManager mNotificationManager;
	private PowerManager.WakeLock mWakeLock;
	private BroadcastReceiver receiver;
	private LocalBroadcastManager broadcastManager;
	private CountDownTimer mCountDown;

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

		// When a broadcast is received, stop service
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				mNotificationManager.cancelAll();
				mCountDown.cancel();
				stopSelf();
			}
		};

		// Register receiver
		broadcastManager = LocalBroadcastManager.getInstance(this);
		broadcastManager.registerReceiver(receiver,
				new IntentFilter(MainActivity.STOP_TIMER));

		// Acquire wake lock
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"MyWakelockTag");
		mWakeLock.acquire();

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Get the length of the timer in seconds
		mSeconds = intent.getIntExtra("Seconds", 0);

		// Get notification manager
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		// Start countdown service
		mCountDown = new CountDownTimer(mSeconds * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {

				// Convert milliseconds to seconds
				int timeRemaining = (int) millisUntilFinished / 1000;

				//
				startForeground(1, Notifications.setupRunningNotification(getApplicationContext(), timeRemaining).build());

				// Send remaining seconds to main activity to update UI
				sendResult(timeRemaining);
			}

			@Override
			public void onFinish() {

				//
				sendResult(0);
				stopForeground(true);
				mNotificationManager.notify(1, Notifications.setupFinishedNotification(getApplicationContext(), mSeconds).build());
				stopSelf();
			}
		}.start();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {

		// unregister receiver
		broadcastManager.unregisterReceiver(receiver);

		// Release wake lock
		mWakeLock.release();
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	public void sendResult(int timeRemaining) {

		Intent intent = new Intent(TIME_REMAINING);
		intent.putExtra(SECONDS_REMAINING, timeRemaining);
		broadcastManager.sendBroadcast(intent);
	}
}


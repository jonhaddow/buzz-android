package com.jon.buzz.Services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.jon.buzz.Utils.Notifications;

public class BackgroundCountdown extends Service {

	static final public String SECONDS_REMAINING = "com.jon.buzz.SECONDS_REMAINING";
	static final public String REQUEST = "";
	private int mSeconds;
	private NotificationManager mNotificationManager;
	private PowerManager.WakeLock mWakeLock;
	private LocalBroadcastManager broadcaster;

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

		broadcaster = LocalBroadcastManager.getInstance(this);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Acquire wake lock
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"MyWakelockTag");
		mWakeLock.acquire();

		// Get the length of the timer in seconds
		mSeconds = intent.getIntExtra("Seconds", 0);

		// Get notification manager
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		// Start countdown service

		new CountDownTimer(mSeconds * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {

				int timeRemaining = (int) millisUntilFinished / 1000;

				startForeground(2, Notifications.setupRunningNotification(getApplicationContext(), timeRemaining).build());

				sendResult(timeRemaining);
			}

			@Override
			public void onFinish() {

				stopForeground(true);
				mNotificationManager.notify(1, Notifications.setupFinishedNotification(getApplicationContext(), mSeconds).build());
				mWakeLock.release();
				stopSelf();
			}
		}.start();

		return super.onStartCommand(intent, flags, startId);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	public void sendResult(int timeRemaining) {

		Intent intent = new Intent(SECONDS_REMAINING);
		intent.putExtra(SECONDS_REMAINING, timeRemaining);
		broadcaster.sendBroadcast(intent);
	}
}


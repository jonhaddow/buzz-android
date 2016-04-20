package com.jon.buzz;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.support.v4.app.TaskStackBuilder;

import static com.jon.buzz.R.drawable.ic_stat_name;

/**
 * Created by Jon Haddow on 20/04/2016
 */
public class Notifications {

	private static Context mContext;
	private static PendingIntent onNotificationClickIntent;
	private static PendingIntent onStopTimerClickIntent;

	public static Notification.Builder setupRunningNotification(Context context, long timeRemaining) {

		mContext = context;

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// Create pending intents for notifications
		onNotificationClickIntent = createOnNotificationClickIntent();
		onStopTimerClickIntent = createOnNotificationStopClickIntent();

		// Create notification that shows while countdown is running
		Notification.Builder runningNotification = new Notification.Builder(mContext)
				.setSmallIcon(ic_stat_name)
				.setContentTitle(mContext.getString(R.string.notification_header))
				.setContentText(timeRemaining / 1000 + mContext.getString(R.string.notification_message_running))
				.setPriority(Notification.PRIORITY_MAX)
				.setVisibility(Notification.VISIBILITY_PUBLIC)
				.setAutoCancel(false)
				.addAction(new Notification.Action.Builder(
						Icon.createWithResource(mContext, R.drawable.ic_action_stop_timer),
						"Stop Timer",
						onStopTimerClickIntent).build()
				)
				.setContentIntent(onNotificationClickIntent)
				.setOngoing(true);

		return runningNotification;
	}

	private static PendingIntent createOnNotificationClickIntent() {

		// Creates an intent for MainActivity to load timerList fragment
		Intent notificationIntent = new Intent(mContext, MainActivity.class);
		notificationIntent.putExtra("type", "TimerList");
		notificationIntent.setAction("onNotification");

		// Create artificial back stack for the intent
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		stackBuilder.addParentStack(MainActivity.class);

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(notificationIntent);

		// Set stack as pending intent for when the notification is clicked
		return stackBuilder.getPendingIntent(
				0,
				PendingIntent.FLAG_ONE_SHOT
		);
	}

	private static PendingIntent createOnNotificationStopClickIntent() {

		// Creates an intent for MainActivity to load timerList fragment
		Intent notificationIntent = new Intent(mContext, MainActivity.class);
		notificationIntent.putExtra("type", "StopTimer");
		notificationIntent.setAction("onNotificationStop");

		// Create artificial back stack for the intent
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
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

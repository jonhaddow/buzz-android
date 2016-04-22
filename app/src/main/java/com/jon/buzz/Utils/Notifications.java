package com.jon.buzz.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.support.v4.app.TaskStackBuilder;

import com.jon.buzz.R;
import com.jon.buzz.activities.MainActivity;

/**
 * Created by Jon Haddow on 20/04/2016
 */
public class Notifications {

	public static Notification.Builder setupRunningNotification(Context context, int timeRemaining) {

		// Create notification that shows while countdown is running
		return new Notification.Builder(context)
				.setSmallIcon(R.drawable.ic_alarm)
				.setContentTitle(context.getString(R.string.notification_header))
				.setContentText(timeRemaining + context.getString(R.string.notification_message_running))
				.setPriority(Notification.PRIORITY_MAX)
				.setVisibility(Notification.VISIBILITY_PUBLIC)
				.setAutoCancel(false)
				.addAction(new Notification.Action.Builder(
						Icon.createWithResource(context, R.drawable.ic_action_stop_timer),
						"Stop Timer",
						createOnNotificationStopClickIntent(context)).build()
				)
				.setContentIntent(createOnNotificationClickIntent(context))
				.setOngoing(true);
	}

	private static PendingIntent createOnNotificationStopClickIntent(Context context) {

		// Creates an intent for MainActivity to load timerList fragment
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.putExtra("type", "StopTimer");
		notificationIntent.setAction("onNotificationStop");

		// Create artificial back stack for the intent
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(notificationIntent);

		// Set stack as pending intent for when the notification is clicked
		return stackBuilder.getPendingIntent(
				0,
				PendingIntent.FLAG_ONE_SHOT
		);
	}

	private static PendingIntent createOnNotificationClickIntent(Context context) {

		// Creates an intent for MainActivity to load timerList fragment
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.putExtra("type", "TimerList");
		notificationIntent.setAction("onNotification");

		// Create artificial back stack for the intent
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(notificationIntent);

		// Set stack as pending intent for when the notification is clicked
		return stackBuilder.getPendingIntent(
				0,
				PendingIntent.FLAG_ONE_SHOT
		);
	}

	public static Notification.Builder setupFinishedNotification(Context context, int seconds) {

		// Show final notification to indicate that the countdown has finished.
		return new Notification.Builder(context)
				.setSmallIcon(R.drawable.ic_alarm)
				.setContentTitle(context.getString(R.string.notification_header))
				.setContentText(seconds + context.getString(R.string.notification_message_finished))
				.setPriority(Notification.PRIORITY_MAX)
				.setVisibility(Notification.VISIBILITY_PUBLIC)
				.setContentIntent(createOnNotificationClickIntent(context))
				.setAutoCancel(true);
	}
}

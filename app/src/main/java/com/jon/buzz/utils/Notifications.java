package com.jon.buzz.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;

import com.jon.buzz.R;
import com.jon.buzz.activities.MainActivity;

/**
 * Created by Jon Haddow on 20/04/2016
 */
public class Notifications {

	public static final String STOP_TIMER = "com.jon.buzz.utils.Notifications.STOP_TIMER";

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
						Icon.createWithResource(context, R.drawable.ic_action_pause_timer),
						"Pause",
						createPauseTimerIntent(context)).build()
				)
				.addAction(new Notification.Action.Builder(
						Icon.createWithResource(context, R.drawable.ic_action_cancel_timer),
						"Stop",
						createStopTimerIntent(context)).build()
				)
				.setContentIntent(createRegularIntent(context))
				.setOngoing(true);
	}

	private static PendingIntent createPauseTimerIntent(Context context) {

		// Creates an intent for MainActivity to pause timer
		Intent notificationIntent = new Intent(CustomBroadcasts.PAUSE_TIMER);
		return PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private static PendingIntent createStopTimerIntent(Context context) {

		// Creates an intent for MainActivity to stop timer
		Intent notificationIntent = new Intent(CustomBroadcasts.STOP_TIMER);
		return PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	private static PendingIntent createRegularIntent(Context context) {

		// Creates an intent for MainActivity
		Intent notificationIntent = new Intent(context, MainActivity.class);
		return PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
	}

	public static Notification.Builder setupFinishedNotification(Context context, int seconds) {

		// Show final notification to indicate that the countdown has finished.
		return new Notification.Builder(context)
				.setSmallIcon(R.drawable.ic_alarm)
				.setContentTitle(context.getString(R.string.notification_header))
				.setContentText(seconds + context.getString(R.string.notification_message_finished))
				.setPriority(Notification.PRIORITY_MAX)
				.setVisibility(Notification.VISIBILITY_PUBLIC)
				.setContentIntent(createRegularIntent(context))
				.setAutoCancel(true);
	}

	public static Notification.Builder setupPausedNotification(Context context, int timeRemaining) {

		// Show Paused notification
		return new Notification.Builder(context)
				.setSmallIcon(R.drawable.ic_alarm)
				.setContentTitle(context.getString(R.string.notification_header))
				.setContentText(context.getString(R.string.notification_message_paused) + timeRemaining + context.getString(R.string.notification_message_running))
				.setPriority(Notification.PRIORITY_MAX)
				.setVisibility(Notification.VISIBILITY_PUBLIC)
				.setAutoCancel(false)
				.addAction(new Notification.Action.Builder(
						Icon.createWithResource(context, R.drawable.ic_action_play_timer),
						"Play",
						createPlayTimerIntent(context)).build()
				)
				.addAction(new Notification.Action.Builder(
						Icon.createWithResource(context, R.drawable.ic_action_cancel_timer),
						"Stop",
						createStopTimerIntent(context)).build()
				)
				.setContentIntent(createRegularIntent(context))
				.setOngoing(true);
	}

	private static PendingIntent createPlayTimerIntent(Context context) {

		// Creates an intent for MainActivity to play timer
		Intent notificationIntent = new Intent(CustomBroadcasts.PLAY_TIMER);
		return PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	}
}

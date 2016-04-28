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

	public static Notification.Builder setupRunningNotification(Context context, TimeConverter myTimer) {

		// Create notification that shows while countdown is running
		return new Notification.Builder(context)
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle(context.getString(R.string.notification_header))
				.setContentText(context.getString(R.string.notification_message_running) + myTimer.toString())
				.setPriority(Notification.PRIORITY_MAX)
				.setVisibility(Notification.VISIBILITY_PUBLIC)
				.setAutoCancel(false)
				.addAction(new Notification.Action.Builder(
						Icon.createWithResource(context, R.drawable.ic_pause_circle),
						context.getString(R.string.pause_timer),
						createPauseTimerIntent(context)).build()
				)
				.addAction(new Notification.Action.Builder(
						Icon.createWithResource(context, R.drawable.ic_cancel_circle),
						context.getString(R.string.cancel_timer),
						createStopTimerIntent(context)).build()
				)
				.setContentIntent(createRegularIntent(context))
				.setOngoing(true);
	}

	private static PendingIntent createPauseTimerIntent(Context context) {

		// Creates an intent for MainActivity to pause timer
		Intent notificationIntent = new Intent(CustomBroadcasts.BROADCAST);
		notificationIntent.putExtra("type", CustomBroadcasts.PAUSE_TIMER);
		return PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private static PendingIntent createStopTimerIntent(Context context) {

		// Creates an intent for MainActivity to stop timer
		Intent notificationIntent = new Intent(CustomBroadcasts.BROADCAST);
		notificationIntent.putExtra("type", CustomBroadcasts.STOP_TIMER);
		return PendingIntent.getBroadcast(context, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	private static PendingIntent createRegularIntent(Context context) {

		// Creates an intent for MainActivity
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
	}

	public static Notification.Builder setupFinishedNotification(Context context) {

		// Show final notification to indicate that the countdown has finished.
		return new Notification.Builder(context)
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle(context.getString(R.string.notification_header))
				.setContentText(context.getString(R.string.notification_message_finished))
				.setPriority(Notification.PRIORITY_MAX)
				.setVisibility(Notification.VISIBILITY_PUBLIC)
				.setContentIntent(createRegularIntent(context))
				.setAutoCancel(true);
	}

	public static Notification.Builder setupPausedNotification(Context context, TimeConverter myTimer) {

		// Show Paused notification
		return new Notification.Builder(context)
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle(context.getString(R.string.notification_header))
				.setContentText(context.getString(R.string.notification_message_paused) + myTimer.toString())
				.setPriority(Notification.PRIORITY_MAX)
				.setVisibility(Notification.VISIBILITY_PUBLIC)
				.setAutoCancel(false)
				.addAction(new Notification.Action.Builder(
						Icon.createWithResource(context, R.drawable.ic_play_circle),
						context.getString(R.string.play_timer),
						createPlayTimerIntent(context)).build()
				)
				.addAction(new Notification.Action.Builder(
						Icon.createWithResource(context, R.drawable.ic_cancel_circle),
						context.getString(R.string.cancel_timer),
						createStopTimerIntent(context)).build()
				)
				.setContentIntent(createRegularIntent(context))
				.setOngoing(true);
	}

	private static PendingIntent createPlayTimerIntent(Context context) {

		// Creates an intent to play timer.
		Intent notificationIntent = new Intent(CustomBroadcasts.BROADCAST);
		notificationIntent.putExtra("type", CustomBroadcasts.PLAY_TIMER);
		return PendingIntent.getBroadcast(context, 2, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}

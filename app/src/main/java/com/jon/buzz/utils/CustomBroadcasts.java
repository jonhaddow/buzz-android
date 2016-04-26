package com.jon.buzz.utils;

/**
 * Created by Jon Haddow on 25/04/2016
 */
public class CustomBroadcasts {

	// Broadcast from Main activity and notification to background service to stop current timer.
	public static String STOP_TIMER = "com.jon.buzz.utils.CustomBroadcasts.STOP_TIMER";

	// Broadcast from Background service to main activity to update UI.
	public static String TIME_REMAINING = "com.jon.buzz.utils.CustomBroadcasts.TIME_REMAINING";

}

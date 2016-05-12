package com.jon.buzz.utils;

/**
 * Created by Jon Haddow on 25/04/2016
 */
public class CustomBroadcasts {

	// Broadcast to pause timer.
	public static final String PAUSE_TIMER = "com.jon.buzz.utils.CustomBroadcasts.PAUSE_TIMER";

	// Broadcast to cancel timer.
	public static final String CANCEL_TIMER = "com.jon.buzz.utils.CustomBroadcasts.CANCEL_TIMER";

	// Broadcast from Background service to main activity to update UI.
	public static final String TIME_REMAINING = "com.jon.buzz.utils.CustomBroadcasts.TIME_REMAINING";

	// Broadcast to resume timer.
	public static final String PLAY_TIMER = "com.jon.buzz.utils.CustomBroadcasts.PLAY_TIMER";

	// Broadcast to receive pending intents from notifications.
	public static final String BROADCAST = "com.jon.buzz.utils.CustomBroadcasts.BROADCAST";

	// Broadcast to add min to timer.
	public static final String ADD_MIN = "com.jon.buz.utils.CustomBroadcasts.ADD_MIN";

	// Broadcast to replay timer.
	public static final String REPLAY_TIMER = "com.jon.buzz.utils.CustomBroadcasts.REPLAY_TIMER";
}

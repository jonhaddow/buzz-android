package com.jon.buzz.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.jon.buzz.utils.CustomBroadcasts;

/**
 * Created by Jon Haddow on 26/04/2016
 */
public class PlayTimerBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		// Send broadcast to BackgroundCountdown
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
		localBroadcastManager.sendBroadcast(new Intent(CustomBroadcasts.PLAY_TIMER));
	}
}


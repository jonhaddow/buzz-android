package com.jon.buzz.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Jon Haddow on 27/04/2016
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
		broadcastManager.sendBroadcast(intent);
	}
}

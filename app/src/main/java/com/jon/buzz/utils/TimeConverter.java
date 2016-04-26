package com.jon.buzz.utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jon Haddow on 26/04/2016
 */
public class TimeConverter {

	private int milli;
	private long hours;
	private long minutes;
	private long seconds;

	public TimeConverter(int milli) {

		this.milli = milli;
		hours = TimeUnit.MILLISECONDS.toHours(milli);
		minutes = TimeUnit.MILLISECONDS.toMinutes(milli) - TimeUnit.HOURS.toMinutes(hours);
		seconds = TimeUnit.MILLISECONDS.toSeconds(milli) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
	}

	public int getMilli() {

		return milli;
	}

	@Override
	public String toString() {

		return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
	}
}

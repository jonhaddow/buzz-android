package com.jon.buzz.utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Jon Haddow on 26/04/2016
 */
public class TimeConverter {

	private final int milli;
	private final long hours;
	private final long minutes;
	private final long seconds;

	public TimeConverter(int milli) {

		this.milli = milli;
		hours = TimeUnit.MILLISECONDS.toHours(milli);
		minutes = TimeUnit.MILLISECONDS.toMinutes(milli) - TimeUnit.HOURS.toMinutes(hours);
		seconds = TimeUnit.MILLISECONDS.toSeconds(milli) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
	}

	public TimeConverter(int hours, int minutes, int seconds) {

		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;

		milli = 1000 * (seconds + (minutes * 60) + (hours * 3600));
	}

	public int getHours() {

		return (int) hours;
	}

	public int getMinutes() {

		return (int) minutes;
	}

	public int getSeconds() {

		return (int) seconds;
	}

	public int getMilli() {

		return milli;
	}

	@Override
	public String toString() {

		return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
	}


}

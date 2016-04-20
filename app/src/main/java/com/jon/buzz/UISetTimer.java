package com.jon.buzz;

import android.view.View;
import android.widget.TextView;

/**
 * Created by Jon Haddow on 20/04/2016
 */
public class UISetTimer {

	/**
	 * Collect text views which make up the display
	 * and add to an TextView array
	 *
	 * @return the Text View Array
	 */
	public static TextView[] collectDisplayData(View view) {

		// Store views in array
		return new TextView[]{
				(TextView) view.findViewById(R.id.tv_hours),
				(TextView) view.findViewById(R.id.tv_hours2),
				(TextView) view.findViewById(R.id.tv_mins),
				(TextView) view.findViewById(R.id.tv_mins2),
				(TextView) view.findViewById(R.id.tv_secs),
				(TextView) view.findViewById(R.id.tv_secs2),
		};
	}

	/**
	 * Add the given digit value to the array of display numbers
	 * and shift all number one to the left
	 *
	 * @param digitValue     Digit to be added to the display
	 * @param displayNumbers Current state of display
	 */
	public static void addNumberToDisplay(String digitValue, TextView[] displayNumbers) {

		// If display is not full...
		if (displayNumbers[0].getText().equals("0")) {
			// Go through array and shift values to left one space
			for (int i = 0; i < displayNumbers.length; i++) {
				if (i == displayNumbers.length - 1) {
					// Add digit value to the end
					displayNumbers[i].setText(digitValue);
				} else {
					displayNumbers[i].setText(displayNumbers[i + 1].getText());
				}
			}
		}
	}

	/**
	 * Remove right most digit by shifting all display digits one space to the right
	 *
	 * @param displayNumbers Current state of display numbers
	 */
	public static void removeNumberFromDisplay(TextView[] displayNumbers) {

		for (int i = 5; i >= 0; i--) {
			if (i == 0) {
				displayNumbers[i].setText("0");
			} else {
				displayNumbers[i].setText(displayNumbers[i - 1].getText());
			}
		}
	}

	/**
	 * Set every display digit to 0
	 *
	 * @param displayData Current state of display
	 */
	public static void clearDisplay(TextView[] displayData) {

		for (TextView aDisplayData : displayData) {
			aDisplayData.setText("0");
		}
	}


}

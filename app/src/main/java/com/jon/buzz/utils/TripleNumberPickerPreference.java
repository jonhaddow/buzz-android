package com.jon.buzz.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import com.jon.buzz.R;

public class TripleNumberPickerPreference extends DialogPreference {

	// Default to one minute
	private static final int DEFAULT_MILLI = 60000;

	private int mCurrentTime;
	private NumberPicker[] pickers;

	public TripleNumberPickerPreference(Context context, AttributeSet attrs) {

		super(context, attrs);

		// Set up dialog layout.
		setDialogLayoutResource(R.layout.triple_number_picker);
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
		setDialogTitle(null);
	}

	@Override
	protected View onCreateDialogView() {
		// Set up pickers
		View dialog = super.onCreateDialogView();
		pickers = new NumberPicker[]{
				(NumberPicker) dialog.findViewById(R.id.pickerHours),
				(NumberPicker) dialog.findViewById(R.id.pickerMinutes),
				(NumberPicker) dialog.findViewById(R.id.pickerSeconds)
		};

		for (NumberPicker picker : pickers) {
			picker.setMinValue(0);
			picker.setMaxValue(24);
		}

		TimeConverter myTimer = new TimeConverter(mCurrentTime);
		pickers[0].setValue(myTimer.getHours());
		pickers[1].setValue(myTimer.getMinutes());
		pickers[2].setValue(myTimer.getSeconds());
		return dialog;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {

		super.onDialogClosed(positiveResult);
		if (positiveResult) {

			// TODO: 25/05/2016 Get value from pickers and set as new mCurrentTime.
			persistInt(mCurrentTime);
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {

		return a.getInteger(index, DEFAULT_MILLI);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {

		super.onSetInitialValue(restorePersistedValue, defaultValue);
		if (restorePersistedValue) {
			mCurrentTime = getPersistedInt(DEFAULT_MILLI);
		}
	}
}
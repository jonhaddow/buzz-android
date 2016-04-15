package com.jon.buzz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SetTimer extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    AddTimerListener callBack;
    private View rootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callBack = (AddTimerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Get base view of fragment
        rootView = inflater.inflate(R.layout.set_timer, container, false);

        // Implement on Long click listener and on click listener for all elements in view
        rootView.findViewById(R.id.deleteButton).setOnLongClickListener(this);
        rootView.findViewById(R.id.deleteButton).setOnClickListener(this);
        rootView.findViewById(R.id.fab).setOnClickListener(this);
        rootView.findViewById(R.id.digit0).setOnClickListener(this);
        rootView.findViewById(R.id.digit1).setOnClickListener(this);
        rootView.findViewById(R.id.digit2).setOnClickListener(this);
        rootView.findViewById(R.id.digit3).setOnClickListener(this);
        rootView.findViewById(R.id.digit4).setOnClickListener(this);
        rootView.findViewById(R.id.digit5).setOnClickListener(this);
        rootView.findViewById(R.id.digit6).setOnClickListener(this);
        rootView.findViewById(R.id.digit7).setOnClickListener(this);
        rootView.findViewById(R.id.digit8).setOnClickListener(this);
        rootView.findViewById(R.id.digit9).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteButton:
                // If delete button is clicked, collect current display data and shift all digits to the right
                removeNumberFromDisplay(collectDisplayData(getView()));
                break;
            case R.id.fab:
                // If FAB button is clicked
                onStartTimer(getView());
                clearDisplay(collectDisplayData(getView()));
                break;
            default:
                //When a digit is selected, the value is added to the display
                addNumberToDisplay(
                        String.valueOf(((Button) v).getText()),
                        SetTimer.collectDisplayData(rootView.findViewById(R.id.content)));
        }
    }

    /**
     * Shift all display digits one space to the right
     *
     * @param displayNumbers Current state of display numbers
     */
    private static void removeNumberFromDisplay(TextView[] displayNumbers) {
        for (int i = 5; i >= 0; i--) {
            if (i == 0) {
                displayNumbers[i].setText("0");
            } else {
                displayNumbers[i].setText(displayNumbers[i - 1].getText());
            }
        }
    }

    /**
     * Collect text views which make up the display
     * and add to an TextView array
     *
     * @return the Text View Array
     */
    public static TextView[] collectDisplayData(View view) {

        // Store views in array
        return new TextView[]{
                (TextView) view.findViewById(R.id.hours),
                (TextView) view.findViewById(R.id.hours2),
                (TextView) view.findViewById(R.id.mins),
                (TextView) view.findViewById(R.id.mins2),
                (TextView) view.findViewById(R.id.secs),
                (TextView) view.findViewById(R.id.secs2),
        };
    }

    /**
     * Called when the FAB start timer button is selected
     *
     * @param view Fab button
     */
    public void onStartTimer(View view) {

        // Collect current display values and convert to int[]
        TextView[] displayNumbers = SetTimer.collectDisplayData(view);
        int[] displayIntegers = new int[6];
        for (int i = 0; i < 6; i++) {
            displayIntegers[i] = Integer.parseInt(String.valueOf(displayNumbers[i].getText()));
        }

        // Calculate length of timer in seconds
        int overallSeconds = displayIntegers[5]
                + displayIntegers[4] * 10
                + displayIntegers[3] * 60
                + displayIntegers[2] * 600
                + displayIntegers[1] * 6000
                + displayIntegers[0] * 60000;

        // Pass seconds to main activity to create new timer
        callBack.addTimerToList(overallSeconds);

        // Start a new countdown service
        Intent countdownIntent = new Intent(getActivity(), BackgroundCountdown.class);
        countdownIntent.putExtra("Seconds", overallSeconds);
        getActivity().startService(countdownIntent);

    }

    /**
     * Set every display digit to 0
     *
     * @param displayData Current state of display
     */
    private static void clearDisplay(TextView[] displayData) {
        for (TextView aDisplayData : displayData) {
            aDisplayData.setText("0");
        }
    }

    /**
     * Add the given digit value to the array of display numbers
     * and shift all number one to the left
     *
     * @param digitValue     Digit to be added to the display
     * @param displayNumbers Current state of display
     */
    private void addNumberToDisplay(String digitValue, TextView[] displayNumbers) {

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

    @Override
    public boolean onLongClick(View v) {
        clearDisplay(collectDisplayData(getView()));
        return true;
    }

    public interface AddTimerListener {
        void addTimerToList(int myTimer);
    }
}

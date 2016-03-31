package com.jon.buzz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class SetTimer extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    AddTimerListener callBack;

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
        View rootView = inflater.inflate(R.layout.set_timer, container, false);

        // Implement on Long click listener and on click listener for delete button and FAB
        ((ImageButton) rootView.findViewById(R.id.deleteButton)).setOnLongClickListener(this);
        ((ImageButton) rootView.findViewById(R.id.deleteButton)).setOnClickListener(this);
        ((FloatingActionButton) rootView.findViewById(R.id.fab)).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.deleteButton) {
            // If delete button is clicked, collect current display data and shift all digits to the right
            removeNumberFromDisplay(collectDisplayData(getView()));
        } else {
            // If FAB button is clicked
            onStartTimer(getView());
            clearDisplay(collectDisplayData(getView()));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        clearDisplay(collectDisplayData(getView()));
        return true;
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
        Intent countdownIntent = new Intent(getActivity(),BackgroundCountdown.class);
        countdownIntent.putExtra("Seconds", overallSeconds);
        getActivity().startService(countdownIntent);

    }

    public interface AddTimerListener {
        void addTimerToList(int myTimer);
    }
}

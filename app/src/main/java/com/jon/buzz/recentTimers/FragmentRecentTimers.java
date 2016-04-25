package com.jon.buzz.recentTimers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jon.buzz.R;
import com.jon.buzz.activities.MainActivity;

import java.util.ArrayList;


public class FragmentRecentTimers extends Fragment implements AdapterView.OnItemLongClickListener {

    private MainActivity mainActivity;
    private BroadcastReceiver mReceiver;
    private LocalBroadcastManager mBroadcastManager;
    public static final String NEW_TIMER = "com.jon.buzz.recentTimers.FragmentRecentTimers.NEW_TIMER";
	private ArrayAdapter mListAdapter;
	private ArrayList<String> mTimers = new ArrayList<>();

	@Override
    public void onResume() {

        // When new timer broadcast is received, add new timer to list
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

	            // Add most recent timer to list
	            int seconds = intent.getIntExtra("Seconds", 0);
	            mTimers.add(0,String.valueOf(seconds));
	            mListAdapter.notifyDataSetChanged();
            }
        };

        // Register mReceiver
        mBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        mBroadcastManager.registerReceiver((mReceiver),
                new IntentFilter(NEW_TIMER));
        super.onResume();
    }

    @Override
    public void onPause() {

        // Unregister Receiver
        mBroadcastManager.unregisterReceiver(mReceiver);

        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_recent_timer_list, container, false);

        // Get list view and populate with list adapter
        ListView timer_list = (ListView) rootView.findViewById(R.id.timer_list);
        mListAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                mTimers);
        timer_list.setAdapter(mListAdapter);
        timer_list.setOnItemLongClickListener(this);
        return rootView;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mainActivity.mTimers.remove(mainActivity.mListAdapter.getItem(position));
        mainActivity.mListAdapter.notifyDataSetChanged();
        return true;
    }
}

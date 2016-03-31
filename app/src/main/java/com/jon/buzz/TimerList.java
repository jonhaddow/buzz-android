package com.jon.buzz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class TimerList extends Fragment implements AdapterView.OnItemClickListener{

    ListView timer_list;
    MainActivity mainActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.timer_list, container, false);

        // Get list view and populate with list adapter
        timer_list = (ListView) rootView.findViewById(R.id.timer_list);
        mainActivity = ((MainActivity) getActivity());
        mainActivity.mListAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1);
        timer_list.setAdapter(mainActivity.mListAdapter);
        timer_list.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mainActivity.mListAdapter.remove(mainActivity.mListAdapter.getItem(position));
    }
}

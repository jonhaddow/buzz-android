package com.jon.buzz;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * This class gives the right pages to the view pager widget
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

	// Number of pages in Main Activity Pager
	private static final int NUMBER_OF_PAGES = 2;

	// Main activity context
	private final Context mContext;

	public MyPagerAdapter(FragmentManager fm, Context context) {

        super(fm);
		mContext = context;
	}

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
	            return new FragmentSetTimer();
	        default:
	            return new FragmentTimerList();
        }
    }

    @Override
    public int getCount() {

	    return NUMBER_OF_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
	            return mContext.getString(R.string.page_0);
	        default:
	            return mContext.getString(R.string.page_1);
        }
    }
}


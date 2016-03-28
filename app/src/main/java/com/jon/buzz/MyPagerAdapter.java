package com.jon.buzz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * This class gives the right pages to the view pager widget
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_OF_PAGES = 2;

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Get the right page dependant on the position number
     *
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SetTimer();
            default:
                return new TimerList();
        }
    }

    /**
     * Get the number of pages in the adapter
     *
     * @return
     */
    @Override
    public int getCount() {
        return NUM_OF_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Set Timer";
            default:
                return "Timer List";
        }
    }
}


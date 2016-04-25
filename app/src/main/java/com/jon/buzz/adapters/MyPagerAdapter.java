package com.jon.buzz.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.jon.buzz.R;
import com.jon.buzz.recentTimers.FragmentRecentTimers;
import com.jon.buzz.setTimer.FragmentSetTimer;

import java.lang.ref.WeakReference;

/**
 * This class gives the right pages to the view pager widget
 */
public class MyPagerAdapter extends FragmentPagerAdapter {

	// Number of pages in Main Activity Pager
	private static final int NUMBER_OF_PAGES = 2;

	// Main activity context
	private final Context mContext;

	// List of fragment instances
	private final SparseArray<WeakReference<Fragment>> registeredFragments = new SparseArray<>();

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
				return new FragmentRecentTimers();
		}
	}

	@Override
	public int getCount() {

		return NUMBER_OF_PAGES;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		Fragment fragment = (Fragment) super.instantiateItem(container, position);
		registeredFragments.put(position, new WeakReference<>(fragment));
		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		registeredFragments.remove(position);
		super.destroyItem(container, position, object);
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

	@Nullable
	public Fragment getFragment(final int position) {
		final WeakReference<Fragment> wr = registeredFragments.get(position);
		if (wr != null) {
			return wr.get();
		} else {
			return null;
		}
	}
}


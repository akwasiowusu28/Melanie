package com.melanie.androidactivities.support;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

public class MelanieTabsAdapter extends FragmentPagerAdapter {

	private SparseArray<Class<? extends Fragment>> fragments;

	public <T> MelanieTabsAdapter(FragmentManager fm,
			SparseArray<Class<? extends Fragment>> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int index) {

		Fragment fragment = null;
		Class<? extends Fragment> klass = fragments.get(index);
		try {
			fragment = klass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO: log it
		}
		return fragment;
	}

	@Override
	public int getCount() {

		return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "Sales Report " + (position + 1);
	}

}

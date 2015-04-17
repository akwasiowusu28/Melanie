package com.melanie.androidactivities.support;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.melanie.androidactivities.SalesReportTableFragment;

public class MelanieTabsAdapter extends FragmentPagerAdapter {

	public MelanieTabsAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		return new SalesReportTableFragment();
	}

	@Override
	public int getCount() {

		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "Sales Report " + (position + 1);
	}

}

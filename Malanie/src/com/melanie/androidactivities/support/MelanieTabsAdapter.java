package com.melanie.androidactivities.support;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.melanie.androidactivities.fragments.SalesReportLineChartFragment;
import com.melanie.androidactivities.fragments.SalesReportTableFragment;

public class MelanieTabsAdapter extends FragmentPagerAdapter {

	public <T> MelanieTabsAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		return index == 0 ? new SalesReportTableFragment()
				: new SalesReportLineChartFragment();
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

package com.melanie.androidactivities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.melanie.androidactivities.support.MelanieTabsAdapter;
import com.melanie.androidactivities.support.NavigationHelper;
import com.melanie.androidactivities.support.SlidingTabLayout;

public class MonthlySalesReportActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_report);

		ViewPager viewPager = (ViewPager) findViewById(R.id.reportsViewPager);
		viewPager.setAdapter(new MelanieTabsAdapter(
				getSupportFragmentManager(), NavigationHelper
						.getMonthlySalesReportFragments()));

		SlidingTabLayout tabLayout = (SlidingTabLayout) findViewById(R.id.melanieSlidingTabs);
		tabLayout.setSelectedIndicatorColors(Color.rgb(255, 117, 25));
		tabLayout.setDistributeEvenly(true);
		tabLayout.setViewPager(viewPager);

	}
}

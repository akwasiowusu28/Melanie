package com.melanie.support;

import com.melanie.androidactivities.R;

public class Common {

	public static Integer[] getMainPageImages() {
		return new Integer[] { R.drawable.shoe, R.drawable.sale,
				R.drawable.customer, R.drawable.report, R.drawable.settings };
	}

	public static Integer[] getMainPageNavigationItems() {
		return new Integer[] { R.string.products, R.string.sales,
				R.string.customers, R.string.reports, R.string.Settings };
	}

	public static Integer[] getMainPageNavigationDescription() {
		return new Integer[] { R.string.productsDesc, R.string.salesDesc,
				R.string.customersDesc, R.string.reportsDesc,
				R.string.SettingsDesc };
	}

}

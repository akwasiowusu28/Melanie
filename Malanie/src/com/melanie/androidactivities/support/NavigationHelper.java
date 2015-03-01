package com.melanie.androidactivities.support;

import android.app.Activity;
import android.util.SparseArray;

import com.melanie.androidactivities.AddProductActivity;
import com.melanie.androidactivities.CategoriesActivity;
import com.melanie.androidactivities.ProductsMainActivity;

import com.melanie.androidactivities.R;

public class NavigationHelper {

	/**
	 * This class provides the activities and other items used in navigation
	 * NOTE: The order of items returned by the methods below, especially those
	 * that return arrays[], are important
	 **/
	public static Integer[] getMainPageIcons() {
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

	public static SparseArray<Class<? extends Activity>> getMelanieMainActivities() {

		return new SparseArray<Class<? extends Activity>>() {
			{
				append(0, ProductsMainActivity.class);
			}
		};
	}

	public static SparseArray<Class<? extends Activity>> getProductActivities() {

		return new SparseArray<Class<? extends Activity>>() {
			{
				append(R.id.addCategories, CategoriesActivity.class);
				append(R.id.viewCategories, CategoriesActivity.class);
				append(R.id.addProducts, AddProductActivity.class);
			}
		};
	}
}

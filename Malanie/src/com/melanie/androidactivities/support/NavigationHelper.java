package com.melanie.androidactivities.support;

import android.app.Activity;
import android.util.SparseArray;

import com.melanie.androidactivities.AddProductActivity;
import com.melanie.androidactivities.CategoriesActivity;
import com.melanie.androidactivities.CustomerListActivity;
import com.melanie.androidactivities.CustomersActivity;
import com.melanie.androidactivities.MelanieInventoryActivity;
import com.melanie.androidactivities.PaymentActivity;
import com.melanie.androidactivities.ProductsMainActivity;
import com.melanie.androidactivities.R;
import com.melanie.androidactivities.ReportsActivity;
import com.melanie.androidactivities.SalesActivity;
import com.melanie.androidactivities.SettingsActivity;

/**
 * @author Akwasi Owusu
 * 
 *         This class provides the activities and other items used in navigation
 *         NOTE: The order of items returned by the methods below, especially
 *         those that return arrays[], are important
 */

public class NavigationHelper {

	/**
	 * Returns the navigation icons used in the main page
	 * 
	 * @return Ids for Icons used in the main page
	 **/
	public static Integer[] getMainPageIcons() {
		return new Integer[] { R.drawable.product2, R.drawable.sale,
				R.drawable.customer, R.drawable.report, R.drawable.settings,

				/*****/
				R.drawable.emptycart };
	}

	/**
	 * Defines the main navigation items on the main page. Add to this if you
	 * want to add another item on the navigation page
	 * 
	 * @return String ids of the navigation items
	 */
	public static Integer[] getMainPageNavigationItems() {
		return new Integer[] { R.string.products, R.string.sales,
				R.string.customers, R.string.reports,

				R.string.Settings,

				/*******/
				R.string.title_activity_payment, };
	}

	/**
	 * returns the small text descriptions under each navigation item on the
	 * main page
	 * 
	 * @return Ids of the descriptions
	 */
	public static Integer[] getMainPageNavigationDescription() {
		return new Integer[] { R.string.productsDesc, R.string.salesDesc,
				R.string.customersDesc, R.string.reportsDesc,
				R.string.SettingsDesc,

				/*****/
				R.string.title_activity_payment };
	}

	/**
	 * A SparseArray containing the main Activities
	 * 
	 * @return the main activities
	 */
	public static SparseArray<Class<? extends Activity>> getMelanieMainActivities() {

		return new SparseArray<Class<? extends Activity>>() {
			{
				append(0, ProductsMainActivity.class);
				append(1, SalesActivity.class);
				append(2, CustomersActivity.class);
				append(3, ReportsActivity.class);
				append(4, SettingsActivity.class);
				append(5, PaymentActivity.class);
			}
		};
	}

	/**
	 * A SparseArray containing the activities in the Products navigation item
	 * 
	 * @return the activities of the Product navigation item
	 */
	public static SparseArray<Class<? extends Activity>> getProductActivities() {

		return new SparseArray<Class<? extends Activity>>() {
			{
				append(R.id.addCategories, CategoriesActivity.class);
				append(R.id.viewCategories, CustomerListActivity.class);
				append(R.id.addProducts, AddProductActivity.class);
				append(R.id.viewProducts, MelanieInventoryActivity.class);
			}
		};
	}
}

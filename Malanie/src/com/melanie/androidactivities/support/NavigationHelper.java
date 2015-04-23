package com.melanie.androidactivities.support;

import android.app.Activity;
import android.util.SparseArray;

import com.melanie.androidactivities.AddProductActivity;
import com.melanie.androidactivities.CategoriesActivity;
import com.melanie.androidactivities.CustomerListActivity;
import com.melanie.androidactivities.CustomerMainActivity;
import com.melanie.androidactivities.CustomersActivity;
import com.melanie.androidactivities.MelanieInventoryActivity;
import com.melanie.androidactivities.PaymentActivity;
import com.melanie.androidactivities.ProductsMainActivity;
import com.melanie.androidactivities.R;
import com.melanie.androidactivities.SalesActivity;
import com.melanie.androidactivities.SalesReportActivity;
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
		return new Integer[] { R.drawable.product, R.drawable.sale,
				R.drawable.customer, R.drawable.report, R.drawable.settings };
	}

	/**
	 * Defines the main navigation items on the main page. Add to this if you
	 * want to add another item on the navigation page
	 * 
	 * @return String ids of the navigation items
	 */
	public static Integer[] getMainPageNavigationItems() {
		return new Integer[] { R.string.products, R.string.sales,
				R.string.customers, R.string.reports, R.string.Settings };
	}

	/**
	 * Defines the main navigation items on the products main page. Add to this
	 * if you want to add another item on the navigation page
	 * 
	 * @return String ids of the navigation items
	 */
	public static Integer[] getProductMainNavigationItems() {
		return new Integer[] { R.string.categories, R.string.addProducts,
				R.string.melanie_inventory };
	}

	/**
	 * Returns the navigation icons used in the products main page
	 * 
	 * @return Ids for Icons used in the product main page
	 **/
	public static Integer[] getProductMainIcons() {
		return new Integer[] { R.drawable.category, R.drawable.product,
				R.drawable.inventory };
	}

	/**
	 * returns the small text descriptions under each navigation item on the
	 * product main page
	 * 
	 * @return Ids of the descriptions
	 */
	public static Integer[] getProductMainNavigationDescription() {
		return new Integer[] { R.string.addcategorydescription,
				R.string.addproductdescription, R.string.inventoryDescription };
	}

	/**
	 * Defines the main navigation items on the Customer main page. Add to this
	 * if you want to add another item on the customer navigation page
	 * 
	 * @return String ids of the customers navigation items
	 */
	public static Integer[] getCustomerMainNavigationItems() {
		return new Integer[] { R.string.customers, R.string.melanie_payment,
				R.string.customerlist };
	}

	/**
	 * Returns the navigation icons used in the Customer main page
	 * 
	 * @return Ids for Icons used in the Customers main page
	 **/
	public static Integer[] getCustomerMainIcons() {
		return new Integer[] { R.drawable.customer, R.drawable.wallet,
				R.drawable.customerlist };
	}

	/**
	 * returns the small text descriptions under each navigation item on the
	 * product main page
	 * 
	 * @return Ids of the descriptions
	 */
	public static Integer[] getCustomerMainNavigationDescription() {
		return new Integer[] { R.string.customersdescription,
				R.string.paymentdescription, R.string.customerlistdescription };
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
				R.string.SettingsDesc };
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
				append(2, CustomerMainActivity.class);
				append(3, SalesReportActivity.class);
				append(4, SettingsActivity.class);
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
				append(0, CategoriesActivity.class);
				append(1, AddProductActivity.class);
				append(2, MelanieInventoryActivity.class);
			}
		};
	}

	/**
	 * A SparseArray containing the activities in the Customer navigation item
	 * 
	 * @return the activities of the Product navigation item
	 */
	public static SparseArray<Class<? extends Activity>> getCustomerActivities() {

		return new SparseArray<Class<? extends Activity>>() {
			{
				append(0, CustomersActivity.class);
				append(1, PaymentActivity.class);
				append(2, CustomerListActivity.class);
			}
		};
	}
}

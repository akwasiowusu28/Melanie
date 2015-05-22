package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.melanie.androidactivities.support.CustomersListViewAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.CustomersController;
import com.melanie.entities.Customer;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class CustomerListActivity extends AppCompatActivity {

	private List<Customer> customers;
	private CustomersController customersController;
	private CustomersListViewAdapter customersAdapter;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_list);
		initializeFields();
		getAllCustomers();
		setupCustomersListView();
	}

	private void initializeFields() {
		handler = new Handler(getMainLooper());
		customersController = MelanieBusinessFactory.makeCustomersController();
	}

	private void setupCustomersListView() {
		customersAdapter = new CustomersListViewAdapter(this, customers);
		ListView listView = (ListView) findViewById(R.id.customersListView);
		View headerView = getLayoutInflater().inflate(
				R.layout.layout_customerlist_header, listView, false);

		listView.addHeaderView(headerView);
		listView.setAdapter(customersAdapter);
	}

	private void getAllCustomers() {
		customers = new ArrayList<Customer>();
		try {
			List<Customer> tempCustomers = null;
			tempCustomers = customersController
					.getAllCustomers(new MelanieOperationCallBack<Customer>() {

						@Override
						public void onCollectionOperationSuccessful(
								List<Customer> results) {

							Utils.mergeItems(results, customers);
							Utils.notifyListUpdate(customersAdapter, handler);
						}
					});
			if (tempCustomers != null && !tempCustomers.isEmpty())
				customers.addAll(tempCustomers);

		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // TODO: log it
		}
	}
}

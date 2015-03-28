package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.melanie.androidactivities.support.SalesListViewAdapter;
import com.melanie.business.SalesController;
import com.melanie.business.concrete.SalesControllerImpl;
import com.melanie.entities.Sale;

public class PaymentActivity extends Activity {

	private List<Sale> sales;
	private SalesController salesController;
	private SalesListViewAdapter salesListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		initializeFields();
		setupSalesListView();
	}
	
	private void setupSalesListView() {

		ListView listView = (ListView) findViewById(R.id.salesListView);
		View headerView = getLayoutInflater().inflate(
				R.layout.layout_saleitems_header, listView, false);
		View emptyView = findViewById(R.id.emptyView);

		listView.setEmptyView(emptyView);

		listView.addHeaderView(headerView);
		listView.setAdapter(salesListAdapter);
	}

	private void initializeFields() {
		salesController = new SalesControllerImpl();
		sales = new ArrayList<Sale>();
		salesListAdapter = new SalesListViewAdapter(this, sales);
	}

}

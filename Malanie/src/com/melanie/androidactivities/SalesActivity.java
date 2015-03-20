package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.melanie.androidactivities.support.SalesListViewAdapter;
import com.melanie.business.SalesController;
import com.melanie.business.concrete.SalesControllerImpl;
import com.melanie.entities.Sale;
import com.melanie.support.exceptions.MelanieArgumentException;

public class SalesActivity extends Activity {

	private static final int SCAN_REQUEST_CODE = 28;
	private static final String BARCODE_LIST = "barcodes";
	private List<Sale> sales;
	private SalesController salesController;
	private ScheduledExecutorService executorService;
	private SalesListViewAdapter<Sale> salesListAdapter;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales);

		initializeFields();
		startBarcodeScanning();
	}

	private void initializeFields() {
		executorService = Executors.newScheduledThreadPool(2);
		salesController = new SalesControllerImpl();
		sales = new ArrayList<Sale>();
		salesListAdapter = new SalesListViewAdapter<Sale>(this, sales);
		handler = new Handler();
	}

	private void startBarcodeScanning() {
		if (!executorService.isShutdown()) {
			executorService.schedule(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(SalesActivity.this,
							ScanBarcodeActivity.class);
					startActivityForResult(intent, SCAN_REQUEST_CODE);
				}
			}, 50, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SCAN_REQUEST_CODE && resultCode == RESULT_OK
				&& data != null) {
			List<String> barcodes = data.getStringArrayListExtra(BARCODE_LIST);
			recordSalesFromBarcodes(barcodes);
		}
	}

	private void recordSalesFromBarcodes(List<String> barcodes) {

		try {
			sales = salesController.addSales(barcodes);
			notifySalesListUpdate();
		} catch (MelanieArgumentException e) {
			e.printStackTrace(); // log it
		}

	}

	private void notifySalesListUpdate() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				salesListAdapter.notifyDataSetChanged();
			}
		}, 1000);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (executorService != null)
			executorService.shutdown();
	}
}

package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.melanie.androidactivities.support.SalesListViewAdapter;
import com.melanie.business.controllers.ProductEntryController;
import com.melanie.business.controllers.ProductEntryControllerImpl;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;

public class SalesActivity extends Activity {

	private static final int SCAN_REQUEST_CODE = 28;
	private static final String BARCODE_LIST = "barcodes";
	private List<Sale> sales;
	private ProductEntryController productController;
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
		productController = new ProductEntryControllerImpl();
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

		for (String barcode : barcodes) {

			// first check if the list contains a product with the same barcode
			// and just increase the quantity sold
			Sale sale = getExistingSale(barcode);
			if (sale == null)
				addNewSale(barcode);
			else {
				int quantity = sale.getQuantitySold();
				sale.setQuantitySold(++quantity);
			}
			notifySalesListUpdate();
		}
	}

	private void addNewSale(String barcode) {
		Sale sale = new Sale();
		Product product = productController.findProductByBarcode(barcode);
		if (product != null) {
			sale.setProduct(product);
			sale.setSaleDate(new Date());
			sale.setQuantitySold(1);
			sales.add(sale);
		}
	}

	private Sale getExistingSale(String barcode) {
		Sale sale = null;
		for (Sale existingSale : sales) {
			if (existingSale.getProduct().getBarcodeNumber().equals(barcode)) {
				sale = existingSale;
				break;
			}
		}
		return sale;
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

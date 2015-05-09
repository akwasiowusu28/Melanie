package com.melanie.androidactivities.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.androidactivities.R;
import com.melanie.entities.Sale;

public class ReceiptPrintingHelper {

	private Context context;

	public ReceiptPrintingHelper(Context context) {
		super();
		this.context = context;
	}

	public void printReceipt(double totalValue, double discount,
			double amountReceived, double balance,
			ProductsAndSalesListViewAdapter<Sale> salesAdapter) {

		View receiptView = buildReceiptView(totalValue, discount,
				amountReceived, balance, salesAdapter);
		Bitmap bitmap = buildReceiptBitmap(receiptView);
	}

	private View buildReceiptView(double totalValue, double discount,
			double amountReceived, double balance,
			ProductsAndSalesListViewAdapter<Sale> salesAdapter) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View receiptView = inflater.inflate(R.layout.layout_receipt, null);

		ListView listView = (ListView) receiptView
				.findViewById(R.id.receiptsalesListView);
		listView.setAdapter(salesAdapter);

		((TextView) receiptView.findViewById(R.id.receipttotalValue))
				.setText(String.valueOf(totalValue));
		((TextView) receiptView.findViewById(R.id.receiptdiscountValue))
				.setText(String.valueOf(discount));
		((TextView) receiptView.findViewById(R.id.receiptamountReceived))
				.setText(String.valueOf(amountReceived));
		((TextView) receiptView.findViewById(R.id.receiptbalanceDue))
				.setText(String.valueOf(balance));

		return receiptView;
	}

	private Bitmap buildReceiptBitmap(View receiptView) {
		receiptView.setDrawingCacheEnabled(true);
		receiptView.buildDrawingCache();
		return receiptView.getDrawingCache();
	}
}

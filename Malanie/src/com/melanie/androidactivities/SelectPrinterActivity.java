package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.melanie.androidactivities.support.MelaniePrinterDiscoverer;
import com.melanie.androidactivities.support.MelanieSingleTextListAdapter;
import com.melanie.androidactivities.support.PrinterType;
import com.melanie.androidactivities.support.Utils;
import com.melanie.support.OperationCallBack;

public class SelectPrinterActivity extends Activity {

	private List<String> printers;
	private HashMap<String, String> printerInfo;
	private Handler handler;
	private MelanieSingleTextListAdapter<String> printersAdapter;
	private MelaniePrinterDiscoverer printerDiscoverer;
	private Intent callerActivityIntent;
	private PrinterType printerType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_printer);

		initializeFields();

		callerActivityIntent = getIntent();
		printerType = PrinterType.valueOf(callerActivityIntent
				.getStringExtra(Utils.Constants.PRINTER_TYPE));
		setupListView();
		setupPrinterDiscoverer();
		discoverPrinter();
	}

	private void initializeFields() {
		printerInfo = new HashMap<String, String>();
		printers = new ArrayList<>();
		handler = new Handler(getMainLooper());
		printersAdapter = new MelanieSingleTextListAdapter<>(this, printers);
	}

	private void setupPrinterDiscoverer() {

		printerDiscoverer = new MelaniePrinterDiscoverer(this,
				new OperationCallBack<Map<String, String>>() {
					@Override
					public void onOperationSuccessful(Map<String, String> result) {
						printerInfo.putAll(result);
						printers.add(printerInfo.keySet().iterator().next());
						Utils.notifyListUpdate(printersAdapter, handler);
					}

				}, printerType);

	}

	private void discoverPrinter() {
		if (printerType.equals(PrinterType.Barcode))
			printerDiscoverer.discoverBarcodePrinter();
		else if (printerType.equals(PrinterType.Receipt))
			printerDiscoverer.discoverReceiptPrinter();
	}

	private void setupListView() {
		ListView listView = (ListView) findViewById(R.id.printerSelectionListView);
		listView.setAdapter(printersAdapter);
		listView.setOnItemClickListener(itemClickListener);
	}

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			if (printerType.equals(PrinterType.Barcode)) {
				Bundle bundle = new Bundle();
				bundle.putSerializable(Utils.Constants.PRINTER_INFO, printerInfo);
				callerActivityIntent.putExtras(bundle);
			} else
				callerActivityIntent.putExtra(Utils.Constants.PRINTER_INFO,
						printerInfo.get(printers.get(position)));

			setResult(RESULT_OK, callerActivityIntent);
			finish();
		}
	};
}

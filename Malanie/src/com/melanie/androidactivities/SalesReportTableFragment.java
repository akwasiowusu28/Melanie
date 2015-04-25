package com.melanie.androidactivities;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import com.melanie.androidactivities.support.MelanieDatePicker;
import com.melanie.androidactivities.support.MelanieGroupAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.SalesController;
import com.melanie.entities.Sale;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class SalesReportTableFragment extends Fragment {

	private static final String DATEFORMAT = "dd-MM-yyyy";

	private MelanieGroupAdapter<String> displayItemsAdapter;
	private List<Entry<String, Integer>> displayItems;
	private SalesController salesController;
	private SimpleDateFormat dateformater;
	private LayoutInflater layoutInflater;
	private List<Sale> sales;
	private Button startDateButton;
	private Button endDateButton;
	private Date startDate;
	private Date endDate;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeFields();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		layoutInflater = inflater;
		return inflater.inflate(R.layout.layout_sales_table_report_fragment,
				container, false);
	}

	private void initializeFields() {
		displayItems = new ArrayList<Map.Entry<String, Integer>>();
		sales = new ArrayList<Sale>();
		salesController = MelanieBusinessFactory.makeSalesController();
		dateformater = new SimpleDateFormat(DATEFORMAT, Locale.getDefault());
		initializeDates();
		displayItemsAdapter = new MelanieGroupAdapter<String>(getActivity(),
				displayItems);
	}

	private void getGroupedSales() {
		try {
			sales = salesController.getSalesBetween(startDate, endDate,
					new MelanieOperationCallBack<Sale>(this.getClass()
							.toString()) {

						@Override
						public void onCollectionOperationSuccessful(
								List<Sale> results) {
							Utils.mergeItems(results, sales);
							updateDisplayItems(sales);
						}

					});
			updateDisplayItems(sales);

		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // TODO log it
		}
	}

	private void updateDisplayItems(List<Sale> newSales) {
		List<String> displayDates = new ArrayList<String>();

		for (Sale sale : newSales)
			displayDates.add(dateformater.format(sale.getSaleDate()));

		Map<String, Integer> displayDateGroup = Utils.groupItems(displayDates);

		for (Entry<String, Integer> entry : displayDateGroup.entrySet())
			displayItems.add(new AbstractMap.SimpleEntry<String, Integer>(entry
					.getKey(), entry.getValue()));

		Utils.notifyListUpdate(displayItemsAdapter, new Handler(getActivity()
				.getMainLooper()));
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		setupDateButtons();
		getGroupedSales();
		setupListView();
	}

	private void setupListView() {
		ListView listView = (ListView) getView().findViewById(
				R.id.salesListView);
		View headerView = layoutInflater.inflate(
				R.layout.layout_two_item_view_header, listView, false);

		listView.addHeaderView(headerView);
		listView.setAdapter(displayItemsAdapter);
	}

	private void initializeDates() {
		Calendar calendar = Calendar.getInstance();
		startDate = Utils.getDateOnly(calendar);
		endDate = Utils.getDateOnly(calendar);
	}

	private void setupDateButtons() {
		startDateButton = (Button) getView().findViewById(R.id.startDate);
		endDateButton = (Button) getView().findViewById(R.id.endDate);

		if (startDate != null && endDate != null) {
			startDateButton.setText(dateformater.format(startDate));
			endDateButton.setText(dateformater.format(endDate));
		}

		startDateButton.setOnClickListener(dateButtonsClickListener);
		endDateButton.setOnClickListener(dateButtonsClickListener);
	}

	private OnClickListener dateButtonsClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Button button = (Button) view;
			getDatePicker(button).show(getFragmentManager(), "datepicker");
		}
	};

	private MelanieDatePicker getDatePicker(final Button pickerButton) {

		return new MelanieDatePicker(getActivity(), new OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int month, int day) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month, day);

				Date newDate = Utils.getDateOnly(calendar);
				int buttonId = pickerButton.getId();
				boolean isStartDate = buttonId == R.id.startDate;
				if (isValidDate(newDate, buttonId)) {
					if (isStartDate)
						startDate = newDate;
					else
						endDate = newDate;
					pickerButton.setText(dateformater.format(newDate));
					getGroupedSales();
				} else
					Utils.makeToast(getActivity(),
							isStartDate ? R.string.startDateError
									: R.string.endDateError);
			}
		});
	}

	private boolean isValidDate(Date date, int buttonId) {
		boolean isValid = false;

		if (buttonId == R.id.startDate)
			isValid = endDate != null && endDate.compareTo(date) >= 0;
		else
			isValid = startDate != null && startDate.compareTo(date) <= 0;

		return isValid;
	}

}

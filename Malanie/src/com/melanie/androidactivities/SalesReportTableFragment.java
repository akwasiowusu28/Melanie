package com.melanie.androidactivities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.melanie.androidactivities.support.MelanieDatePicker;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.SalesController;
import com.melanie.entities.Sale;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class SalesReportTableFragment extends Fragment {

	private Map<Sale, Integer> sales;
	private SalesController salesController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeFields();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_sales_table_report_fragment,
				container, false);
	}

	private void initializeFields() {
		sales = new HashMap<Sale, Integer>();
		salesController = MelanieBusinessFactory.makeSalesController();

	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		setupDateButtonsListeners();
	}

	private void setupDateButtonsListeners() {
		Button startDateButton = (Button) getView()
				.findViewById(R.id.startDate);
		Button endDateButton = (Button) getView().findViewById(R.id.endDate);
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
				Calendar c = Calendar.getInstance();
				c.set(year, month, day);

				SimpleDateFormat dateformater = new SimpleDateFormat(
						"dd-MM-yyyy", Locale.getDefault());
				pickerButton.setText(dateformater.format(c.getTime()));
			}

		});
	}

	private Map<Sale, Integer> getSalesBetweenDates(Date fromDate, Date toDate) {
		List<Sale> salesBetween = new ArrayList<Sale>();
		try {
			salesBetween = salesController.getSalesBetween(fromDate, toDate,
					new MelanieOperationCallBack<Sale>(this.getClass()
							.getSimpleName()) {

						@Override
						public void onCollectionOperationSuccessful(
								List<Sale> results) {
							Map<Sale, Integer> newSales = Utils
									.groupItems(results);
							sales.putAll(newSales);
						}

					});
		} catch (MelanieBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Utils.groupItems(salesBetween);
	}
}

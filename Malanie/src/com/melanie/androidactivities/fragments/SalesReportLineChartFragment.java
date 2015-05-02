package com.melanie.androidactivities.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.melanie.androidactivities.R;
import com.melanie.androidactivities.support.MelanieDatePicker;
import com.melanie.androidactivities.support.MelanieGroupAdapter;
import com.melanie.androidactivities.support.ObservablePropertyChangedListener;
import com.melanie.androidactivities.support.ReportSession;
import com.melanie.androidactivities.support.Utils;

public class SalesReportLineChartFragment extends Fragment implements
		ObservablePropertyChangedListener {

	private static final String DATEFORMAT = "dd-MM-yyyy";

	private MelanieGroupAdapter<String> displayItemsAdapter;
	private List<Entry<String, Integer>> displayItems;
	private SimpleDateFormat dateformater;
	private Button startDateButton;
	private Button endDateButton;
	private Date startDate;
	private Date endDate;
	private Looper looper;
	private Handler handler;
	private ReportSession reportSession;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeFields();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_sales_chart_fragment,
				container, false);
	}

	private void initializeFields() {
		reportSession = ReportSession.getInstance(this);
		displayItems = new ArrayList<Map.Entry<String, Integer>>();
		displayItems.addAll(reportSession.getDisplayItems());
		dateformater = new SimpleDateFormat(DATEFORMAT, Locale.getDefault());
		initializeDates();
		displayItemsAdapter = new MelanieGroupAdapter<String>(getActivity(),
				displayItems);
	}

	private void initializeDates() {

		startDate = reportSession.getStartDate();
		endDate = reportSession.getEndDate();
	}

	private void updateDisplayItems() {
		displayItems.clear();
		displayItems.addAll(reportSession.getDisplayItems());

		Utils.notifyListUpdate(displayItemsAdapter, handler);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		looper = getActivity().getMainLooper();
		handler = new Handler(looper);
		setupDateButtons();
		reportSession.getGroupedSales();
	}

	private void setupDateButtons() {
		startDateButton = (Button) getView().findViewById(R.id.startDate);
		endDateButton = (Button) getView().findViewById(R.id.endDate);

		startDateButton.setText(dateformater.format(reportSession
				.getStartDate()));
		endDateButton.setText(dateformater.format(reportSession.getEndDate()));

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

				Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
				int buttonId = pickerButton.getId();
				boolean isStartDate = buttonId == R.id.startDate;

				calendar.set(year, month, day);

				Date newDate = calendar.getTime();

				if (isValidDate(newDate, buttonId)) {
					if (isStartDate)
						reportSession.setStartDate(Utils
								.getStartOfDay(calendar));
					else
						reportSession.setEndDate(Utils.getEndOfDay(calendar));

					pickerButton.setText(dateformater.format(newDate));
					reportSession.getGroupedSales();
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

	@Override
	public void onObservablePropertyChanged(String propertyName) {
		switch (propertyName) {
		case ReportSession.PropertyNames.DISPLAYITEMS:
			updateDisplayItems();
			break;
		case ReportSession.PropertyNames.STARTDATE:
			startDate = reportSession.getStartDate();
			startDateButton.setText(dateformater.format(startDate));
			break;
		case ReportSession.PropertyNames.ENDDATE:
			endDate = reportSession.getEndDate();
			endDateButton.setText(dateformater.format(endDate));
		}
	}
}

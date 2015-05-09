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
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.melanie.androidactivities.R;
import com.melanie.androidactivities.support.ChartEntry;
import com.melanie.androidactivities.support.MelanieDatePicker;
import com.melanie.androidactivities.support.ObservablePropertyChangedListener;
import com.melanie.androidactivities.support.ReportSession;
import com.melanie.androidactivities.support.Utils;

public class MonthlySalesChartFragment extends Fragment implements
		ObservablePropertyChangedListener {

	private static final String DAILY_SALES = "Daily Sales";

	private List<Entry<String, Integer>> displayItems;
	private SimpleDateFormat dateformater;
	private Button startDateButton;
	private Button endDateButton;
	private Date startDate;
	private Date endDate;
	private ReportSession reportSession;
	private ArrayList<com.github.mikephil.charting.data.Entry> chartEntries;
	private ArrayList<String> chartLabels;
	private LineChart salesChart;
	private LineDataSet dataSet;
	private boolean isDaily = false;

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
		displayItems.addAll(reportSession.getDisplayItems(isDaily));
		dateformater = new SimpleDateFormat(Utils.Costants.DATEFORMAT,
				Locale.getDefault());
		initializeDates();
		chartEntries = new ArrayList<>();
		chartLabels = new ArrayList<>();
	}

	private void initializeDates() {
		startDate = Utils.getDateForFirstMonthDay(reportSession.getStartDate());
		endDate = Utils.getDateForLastMonthDay(reportSession.getEndDate());
	}

	private void updateDisplayItems() {
		if (!isInLayout()) {
			displayItems.clear();
			displayItems.addAll(reportSession.getDisplayItems(isDaily));
			refreshChart();
		}
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		setupDateButtons();
		setUpLineChart();
		reportSession.getGroupedSales(isDaily);
	}

	private void setUpLineChart() {
		salesChart = (LineChart) getView().findViewById(R.id.salesChart);
		configureAxis();
		salesChart.setDescription(Utils.Costants.EMPTY_STRING);
	}

	private void configureAxis() {
		salesChart.getAxisRight().setEnabled(false);
		XAxis xAxis = salesChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setDrawGridLines(false);
		xAxis.setAvoidFirstLastClipping(true);
		YAxis yAxis = salesChart.getAxisLeft();
		yAxis.setDrawGridLines(false);
	}

	private void refreshDataSet() {
		dataSet = new LineDataSet(chartEntries, DAILY_SALES);
		dataSet.setColor(Color.MAGENTA);
		dataSet.setCircleColor(Color.BLUE);
		dataSet.setLineWidth(1f);
		dataSet.setCircleSize(3f);
		dataSet.setDrawCircleHole(false);
		dataSet.setValueTextSize(9f);
		dataSet.setFillColor(Color.BLUE);
	}

	private void setupDateButtons() {
		startDateButton = (Button) getView().findViewById(R.id.startDate);
		endDateButton = (Button) getView().findViewById(R.id.endDate);

		startDateButton.setText(dateformater.format(startDate));
		endDateButton.setText(dateformater.format(endDate));

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
					if (isStartDate) {
						newDate = Utils.getDateForFirstMonthDay(Utils
								.getDateToStartOfDay(calendar));
						reportSession.setStartDate(newDate);
					} else {
						newDate = Utils.getDateForLastMonthDay(Utils
								.getDateToEndOfDay(calendar));
						reportSession.setEndDate(newDate);
					}

					pickerButton.setText(dateformater.format(newDate));
					reportSession.getGroupedSales(isDaily);
					refreshChart();
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

	private void refreshLineChartEntries() {
		chartEntries.clear();
		chartLabels.clear();
		Entry<String, Integer> entry;
		for (int i = 0; i < displayItems.size(); i++) {
			entry = displayItems.get(i);
			chartEntries.add(new ChartEntry(entry.getValue(), i));
			chartLabels.add(entry.getKey());
		}

	}

	private void refreshChart() {
		if (salesChart != null) {
			if (!salesChart.isEmpty())
				salesChart.clearValues();
			refreshLineChartEntries();
			refreshDataSet();
			LineData lineData = new LineData(chartLabels, dataSet);
			salesChart.setData(lineData);
			salesChart.invalidate();
		}
	}

}

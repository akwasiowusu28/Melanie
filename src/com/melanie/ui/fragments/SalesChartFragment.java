package com.melanie.ui.fragments;

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
import android.widget.RadioButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.melanie.ui.R;
import com.melanie.ui.support.ChartEntry;
import com.melanie.ui.support.MelanieDatePicker;
import com.melanie.ui.support.ObservablePropertyChangedListener;
import com.melanie.ui.support.ReportSession;
import com.melanie.ui.support.SalesReportItem;
import com.melanie.ui.support.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SalesChartFragment extends Fragment implements
        ObservablePropertyChangedListener {

    private List<SalesReportItem> displayItems;
    private SimpleDateFormat dateFormatter;
    private Button startDateButton;
    private Button endDateButton;
    private Date startDate;
    private Date endDate;
    private ReportSession reportSession;
    private ArrayList<com.github.mikephil.charting.data.Entry> lineEntries;
    private ArrayList<String> chartLabels;
    private LineChart lineChart;
    private LineDataSet lineDataSet;
    private BarDataSet barDataSet;

    private boolean isDaily = true;
    private BarChart barChart;
    private ArrayList<BarEntry> barEntries;

    private OnClickListener dateButtonsClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            Button button = (Button) view;
            getDatePicker(button).show(getFragmentManager(), "datepicker");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            isDaily = bundle.getBoolean(Utils.Constants.IS_DAILY);
        }
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
        displayItems = new ArrayList<>();
        displayItems.addAll(reportSession.getDisplayItems(isDaily));
        dateFormatter = new SimpleDateFormat(LocalConstants.DATEFORMAT,
                Locale.getDefault());
        initializeDates();
        lineEntries = new ArrayList<>();
        barEntries = new ArrayList<>();
        chartLabels = new ArrayList<>();
    }

    private void initializeDates() {

        if (isDaily) {
            startDate = reportSession.getStartDate();
            endDate = reportSession.getEndDate();
        } else {
            startDate = Utils.getDateForFirstMonthDay(reportSession.getStartDate());
            endDate = Utils.getDateForLastMonthDay(reportSession.getEndDate());
        }
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
        setUpCharts();
        setupRadioButtons();
        reportSession.initializeData(isDaily);
    }

    private void setupRadioButtons() {
        View view = getView();
        if (view != null) {
            RadioButton lineRadioButton = (RadioButton) view.findViewById(R.id.lineRadio);
            lineRadioButton.setOnClickListener(onRadioButtonClicked);

            RadioButton barRadioButton = (RadioButton) view.findViewById(R.id.barRadio);
            barRadioButton.setOnClickListener(onRadioButtonClicked);
        }
    }

    private OnClickListener onRadioButtonClicked = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!(lineChart.isEmpty() && barChart.isEmpty())) {
                switch (view.getId()) {
                    case R.id.lineRadio:
                        Utils.switchViewVisibitlity(true, lineChart);
                        Utils.switchViewVisibitlity(false, barChart);
                        break;
                    case R.id.barRadio:
                        Utils.switchViewVisibitlity(true, barChart);
                        Utils.switchViewVisibitlity(false, lineChart);
                        break;
                }
            }
        }
    };

    private void setUpCharts() {
        View view = getView();
        if (view != null) {
            lineChart = (LineChart) view.findViewById(R.id.salesLineChart);
            configureLineChartAxis();
            lineChart.setDescription(LocalConstants.EMPTY_STRING);

            barChart = (BarChart) view.findViewById(R.id.salesBarChart);
            configureBarChartAxis();
            barChart.setDescription(LocalConstants.EMPTY_STRING);
        }
    }

    private void configureLineChartAxis() {
        lineChart.getAxisRight().setEnabled(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisLineWidth(4f);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setAxisLineWidth(4f);
    }

    private void configureBarChartAxis() {
        barChart.getAxisRight().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAxisLineWidth(4f);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setAxisLineWidth(4f);
    }

    private void refreshLineDataSet() {
        lineDataSet = new LineDataSet(lineEntries, LocalConstants.DAILY_SALES);
        lineDataSet.setColor(Color.rgb(51, 153, 255));
        lineDataSet.setCircleColor(Color.rgb(255, 102, 0));
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleSize(7f);
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setValueTextSize(9f);
        lineDataSet.setFillColor(Color.rgb(255, 102, 0));
    }

    private void refreshBarDataSet() {
        barDataSet = new BarDataSet(barEntries, LocalConstants.DAILY_SALES);
        barDataSet.setColor(Color.rgb(51, 153, 255));
        barDataSet.setValueTextSize(9f);
    }

    private void setupDateButtons() {
        View view = getView();
        if (view != null) {
            startDateButton = (Button) view.findViewById(R.id.startDate);
            endDateButton = (Button) view.findViewById(R.id.endDate);

            startDateButton.setText(dateFormatter.format(startDate));
            endDateButton.setText(dateFormatter.format(endDate));

            startDateButton.setOnClickListener(dateButtonsClickListener);
            endDateButton.setOnClickListener(dateButtonsClickListener);
        }

    }

    private MelanieDatePicker getDatePicker(final Button pickerButton) {

        return MelanieDatePicker.createInstance(getActivity(), new OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                int buttonId = pickerButton.getId();
                boolean isStartDate = buttonId == R.id.startDate;

                calendar.set(year, month, day);

                Date newDate = calendar.getTime();

                if (isValidDate(newDate, buttonId)) {
                    if (isStartDate) {
                        if (isDaily) {
                            reportSession.setStartDate(Utils
                                    .getDateToStartOfDay(calendar));
                        } else {
                            newDate = Utils.getDateForFirstMonthDay(Utils
                                    .getDateToStartOfDay(calendar));
                            reportSession.setStartDate(newDate);
                        }
                    } else {
                        if (isDaily) {
                            reportSession.setEndDate(Utils
                                    .getDateToEndOfDay(calendar));
                        } else {
                            newDate = Utils.getDateForLastMonthDay(Utils
                                    .getDateToEndOfDay(calendar));
                            reportSession.setEndDate(newDate);
                        }
                    }

                    pickerButton.setText(dateFormatter.format(newDate));
                    reportSession.initializeData(isDaily);
                    refreshChart();
                } else {
                    Utils.makeToast(getActivity(),
                            isStartDate ? R.string.startDateError
                                    : R.string.endDateError);
                }
            }
        });
    }

    private boolean isValidDate(Date date, int buttonId) {
        boolean isValid;

        if (buttonId == R.id.startDate) {
            isValid = endDate != null && endDate.compareTo(date) >= 0;
        } else {
            isValid = startDate != null && startDate.compareTo(date) <= 0;
        }

        return isValid;
    }

    @Override
    public void onObservablePropertyChanged(String propertyName) {
        View view = getView();
        if (view != null) {
            switch (propertyName) {
                case ReportSession.PropertyNames.DAILY_SALES_DISPLAY_ITEMS:
                    updateDisplayItems();
                    break;
                case ReportSession.PropertyNames.START_DATE:
                    startDate = reportSession.getStartDate();
                    startDateButton.setText(dateFormatter.format(startDate));
                    break;
                case ReportSession.PropertyNames.END_DATE:
                    endDate = reportSession.getEndDate();
                    endDateButton.setText(dateFormatter.format(endDate));
            }
        }
    }

    private void refreshChartEntries() {
        lineEntries.clear();
        barEntries.clear();
        chartLabels.clear();
        SalesReportItem reportItem;
        for (int i = 0; i < displayItems.size(); i++) {
            reportItem = displayItems.get(i);
            int quantity = reportItem.getQuantity();
            lineEntries.add(new ChartEntry(quantity, i));
            barEntries.add(new BarEntry(quantity, i));
            chartLabels.add(reportItem.getDescription());
        }
    }

    private void refreshChart() {
        if (lineChart != null) {
            if (!lineChart.isEmpty()) {
                lineChart.clearValues();
            }
            if (!barChart.isEmpty()) {
                barChart.clearValues();
            }
            refreshChartEntries();
            refreshLineDataSet();
            refreshBarDataSet();

            if (!chartLabels.isEmpty()) {

                if (!lineEntries.isEmpty()) {
                    LineData lineData = new LineData(chartLabels, lineDataSet);
                    lineChart.setData(lineData);
                    lineChart.invalidate();
                }

                if (!barEntries.isEmpty()) {
                    BarData barData = new BarData(chartLabels, barDataSet);
                    barChart.setData(barData);
                    barChart.invalidate();
                }
            }
        }
    }

    private class LocalConstants {
        public static final String EMPTY_STRING = "";
        public static final String DATEFORMAT = "MMM dd, yyyy";
        private static final String DAILY_SALES = "Daily Sales";
    }

    @Override
    public void onDetach() {
        reportSession.removeListener(this);
        super.onDetach();
    }
}

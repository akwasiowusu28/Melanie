package com.melanie.androidactivities.fragments;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.androidactivities.R;
import com.melanie.androidactivities.ViewSalesActivity;
import com.melanie.androidactivities.support.ReportListAdapter;
import com.melanie.androidactivities.support.MelanieDatePicker;
import com.melanie.androidactivities.support.ObservablePropertyChangedListener;
import com.melanie.androidactivities.support.ReportSession;
import com.melanie.androidactivities.support.SalesReportItem;
import com.melanie.androidactivities.support.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SalesTableFragment extends Fragment implements
        ObservablePropertyChangedListener {

    private ReportListAdapter displayItemsAdapter;
    private List<SalesReportItem> displayItems;
    private SimpleDateFormat dateformater;
    private LayoutInflater layoutInflater;
    private Button startDateButton;
    private Button endDateButton;
    private Date startDate;
    private Date endDate;
    private Handler handler;
    private ReportSession reportSession;
    private Context context;
    private boolean isDaily = true;
    private OnItemClickListener listItemListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            reportSession.setSelectedDate(displayItems.get(position - 1)
                    .getSaleDate());
            Intent intent = new Intent(context, ViewSalesActivity.class);
            intent.putExtra(Utils.Constants.IS_DAILY,true);
            startActivity(intent);
        }
    };
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
        if(bundle != null){
            isDaily = bundle.getBoolean(Utils.Constants.IS_DAILY);
        }
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
        context = getActivity();
        reportSession = ReportSession.getInstance(this);
        displayItems = new ArrayList<>();
        displayItems.addAll(reportSession.getDisplayItems(isDaily));
        dateformater = new SimpleDateFormat(LocalConstants.MMM_DD_YYYY,
                Locale.getDefault());
        initializeDates();
        displayItemsAdapter = new ReportListAdapter(getActivity(),
                displayItems);
    }

    private void updateDisplayItems() {
        displayItems.clear();
        displayItems.addAll(reportSession.getDisplayItems(isDaily));

        Utils.notifyListUpdate(displayItemsAdapter, handler);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Looper looper = getActivity().getMainLooper();
        handler = new Handler(looper);
        setupDateButtons();
        setupListView();
        reportSession.setStartDate(startDate);
        reportSession.setEndDate(endDate);
        reportSession.loadSales(isDaily);
    }

    private void setupListView() {
        View view = getView();

        if(view != null){
            ListView listView = (ListView) view.findViewById(
                    R.id.salesListView);
            View headerView = layoutInflater.inflate(
                    R.layout.layout_sale_report_header, listView, false);

            TextView textView = (TextView)headerView.findViewById(R.id.firstItemTextView);
            textView.setText(getText(R.string.day));

            listView.addHeaderView(headerView);
            listView.setAdapter(displayItemsAdapter);
            listView.setOnItemClickListener(listItemListener);
        }
    }

    private void initializeDates() {

        if(isDaily) {
            startDate = reportSession.getStartDate();
            endDate = reportSession.getEndDate();
        }
        else{
            startDate = Utils.getDateForFirstMonthDay(reportSession.getStartDate());
            endDate = Utils.getDateForLastMonthDay(reportSession.getEndDate());
        }
    }

    private void setupDateButtons() {
        View view = getView();

        if(view != null){
            startDateButton = (Button) view.findViewById(R.id.startDate);
            endDateButton = (Button) view.findViewById(R.id.endDate);

            startDateButton.setText(dateformater.format(startDate));
            endDateButton.setText(dateformater.format(endDate));

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
                        if(isDaily) {
                            reportSession.setStartDate(Utils
                                    .getDateToStartOfDay(calendar));
                        }
                         else {
                            newDate = Utils.getDateForFirstMonthDay(Utils
                                    .getDateToStartOfDay(calendar));
                            reportSession.setStartDate(newDate);
                        }
                    } else {
                        if(isDaily) {
                            reportSession.setEndDate(Utils
                                    .getDateToEndOfDay(calendar));
                        }
                        else{
                            newDate = Utils.getDateForLastMonthDay(Utils
                                    .getDateToEndOfDay(calendar));
                            reportSession.setEndDate(newDate);
                        }
                    }

                    pickerButton.setText(dateformater.format(newDate));
                    reportSession.loadSales(isDaily);
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
        switch (propertyName) {
            case ReportSession.PropertyNames.DAILY_SALES_DISPLAY_ITEMS:
                updateDisplayItems();
                break;
            case ReportSession.PropertyNames.START_DATE:
                startDate = reportSession.getStartDate();
                startDateButton.setText(dateformater.format(startDate));
                break;
            case ReportSession.PropertyNames.END_DATE:
                endDate = reportSession.getEndDate();
                endDateButton.setText(dateformater.format(endDate));
        }
    }

    private class LocalConstants {
        public static final String MMM_DD_YYYY = "MMM dd, yyyy";
    }
}

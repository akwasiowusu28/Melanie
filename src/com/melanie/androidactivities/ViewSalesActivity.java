package com.melanie.androidactivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.melanie.androidactivities.adapters.ProductsAndSalesListViewAdapter;
import com.melanie.androidactivities.support.ReportSession;
import com.melanie.androidactivities.support.SectionHeader;
import com.melanie.androidactivities.support.Utils;
import com.melanie.entities.Sale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewSalesActivity extends Activity {

    private class LocalConstants {
        public static final String TIME_FORMAT = "h:mm a";
        public static final String MMM_DD_YYYY = "MMM dd, yyyy";
        public static final String MMMM = "MMMM";
        public static final String SALES_ON = "Sales made on ";
        public static final String SALES_IN = "Sales made in ";
    }

    private List<Object> sales;
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    private SimpleDateFormat monthFormatter;

    private ReportSession reportSession = ReportSession.getInstance(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sales);

        Intent intent = getIntent();
        boolean isDaily = intent.getBooleanExtra(Utils.Constants.IS_DAILY, false);

        initializeFields();
        if (isDaily) {

            loadSelectedSales();
            setTitle(LocalConstants.SALES_ON + dateFormatter.format(reportSession.getSelectedDate()));

        } else {

            loadSalesForSelectedMonth();
            setTitle(LocalConstants.SALES_IN + monthFormatter.format(reportSession.getSelectedDate()));
        }
        setupSalesListView();
    }

    private void initializeFields() {
        dateFormatter = new SimpleDateFormat(LocalConstants.MMM_DD_YYYY);
        monthFormatter = new SimpleDateFormat(LocalConstants.MMMM);
        timeFormatter = new SimpleDateFormat(LocalConstants.TIME_FORMAT);
    }

    private void loadSelectedSales() {
        sales = new ArrayList<>();
        List<Sale> reportSales = reportSession.getSales();

        Date selectedDate = reportSession.getSelectedDate();
        String currentTime = "";

        for (Sale sale : reportSales) {
            Date salesDate = sale.getSaleDate();
            if (salesDate.equals(selectedDate)) {
                String salesTime = timeFormatter.format(salesDate);
                if (!salesTime.equals(currentTime)) {
                    currentTime = salesTime;
                    sales.add(new SectionHeader(currentTime));
                }
                sales.add(sale);
            }
        }
    }

    private void loadSalesForSelectedMonth() {
        sales = new ArrayList<>();
        List<Sale> reportSales = reportSession.getSales();

        Date selectedDate = reportSession.getSelectedDate();
        String currentDay = "";

        for (Sale sale : reportSales) {
            Date salesDate = sale.getSaleDate();
            if (monthFormatter.format(salesDate).equals(monthFormatter.format(selectedDate))) {
                String saleDay = dateFormatter.format(salesDate);
                if (!saleDay.equals(currentDay)) {
                    currentDay = saleDay;
                    sales.add(new SectionHeader(currentDay));
                }
                sales.add(sale);
            }
        }
    }

    private void setupSalesListView() {

        ListView listView = (ListView) findViewById(R.id.salesListView);
        View headerView = getLayoutInflater().inflate(
                R.layout.layout_saleitems_header, listView, false);

        listView.addHeaderView(headerView);
        listView.setAdapter(new ProductsAndSalesListViewAdapter<>(this,
                sales, true));
    }
}

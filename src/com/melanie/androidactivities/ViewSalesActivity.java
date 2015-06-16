package com.melanie.androidactivities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.melanie.androidactivities.support.ProductsAndSalesListViewAdapter;
import com.melanie.androidactivities.support.ReportSession;
import com.melanie.androidactivities.support.SectionHeader;
import com.melanie.entities.Sale;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewSalesActivity extends Activity {

    private static final String TIMEFORMAT = "h:mm a";
    private static final String DATEFORMAT = "dd-MM-yyyy";
    private static final String SALES_ON = "Sales made on ";

    private List<Object> sales;
    private SimpleDateFormat dateformater = new SimpleDateFormat(DATEFORMAT);
    private SimpleDateFormat timeFormatter = new SimpleDateFormat(TIMEFORMAT);

    private ReportSession reportSession = ReportSession.getInstance(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sales);
        loadSelectedSales();
        setupSalesListView();
        setTitle(SALES_ON + reportSession.getSelectedDateString());
    }

    private void loadSelectedSales() {
        sales = new ArrayList<>();
        List<Sale> reportSales = reportSession.getSales();
        String selectedDateString = reportSession.getSelectedDateString();
        String currentTime = "";
        for (Sale sale : reportSales) {
            Date salesDate = sale.getSaleDate();
            if (dateformater.format(salesDate).equals(selectedDateString)) {
                String salesTime = timeFormatter.format(salesDate);
                if (!salesTime.equals(currentTime)) {
                    currentTime = salesTime;
                    sales.add(new SectionHeader(currentTime));
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
        listView.setAdapter(new ProductsAndSalesListViewAdapter<Object>(this,
                sales, true));
    }
}

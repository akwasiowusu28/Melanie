package com.melanie.androidactivities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.melanie.androidactivities.R;
import com.melanie.androidactivities.support.SalesReportItem;

import java.util.List;

public class ReportListAdapter extends
        ArrayAdapter<SalesReportItem> {
    private Context context;
    private int resource;
    private List<SalesReportItem> items;

    public ReportListAdapter(Context context,
                             List<SalesReportItem> items) {

        super(context, R.layout.layout_report_item_view, items);
        this.context = context;
        resource = R.layout.layout_report_item_view;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = convertView;

        if (rowView == null) {
            ViewHolder viewHolder;
            viewHolder = new ViewHolder();
            rowView = inflater.inflate(resource, parent, false);

            viewHolder.dateTextView = (TextView) rowView
                    .findViewById(R.id.firstItemTextView);
            viewHolder.quantityTextView = (TextView) rowView
                    .findViewById(R.id.secondItemTextView);
            viewHolder.totalTextView = (TextView)rowView.findViewById(R.id.thirdItemTextView);

            rowView.setTag(viewHolder);

        }

        SalesReportItem reportItem = items.get(position);

        if (reportItem != null) {
            ViewHolder viewHolder = (ViewHolder) rowView.getTag();
            viewHolder.dateTextView.setText(reportItem.getDescription());
            viewHolder.quantityTextView.setText(Integer.toString(reportItem
                    .getQuantity()));
            viewHolder.totalTextView.setText(Double.toString(reportItem.getTotal()));
        }

        return rowView;
    }

    private static class ViewHolder {
        public TextView dateTextView;
        public TextView quantityTextView;
        public TextView totalTextView;
    }

}

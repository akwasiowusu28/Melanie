package com.melanie.androidactivities.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.melanie.androidactivities.R;

import java.util.ArrayList;
import java.util.List;

public class SingleTextListAdapter<T> extends ArrayAdapter<T> {

    private Context context;
    private int resource;
    private List<T> items;
    private List<T> originalItems;

    public SingleTextListAdapter(Context context, List<T> items) {
        super(context, R.layout.layout_single_textview, items);
        this.context = context;
        this.resource = R.layout.layout_single_textview;
        this.items = items;
        originalItems = new ArrayList<>(items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = convertView;

        if (rowView == null) {
            ViewHolder viewHolder = null;
            viewHolder = new ViewHolder();
            rowView = inflater.inflate(resource, parent, false);

            viewHolder.itemTextView = (TextView) rowView
                    .findViewById(R.id.singleTextViewForListView);
            rowView.setTag(viewHolder);
        }

        if (items != null && !items.isEmpty()) {
            T item = items.get(position);
            if (item != null) {
                ViewHolder viewHolder = (ViewHolder) rowView.getTag();
                viewHolder.itemTextView.setText(item.toString());
            }
        }
        return rowView;
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                items.clear();
                if (results.values != null) {
                    items.addAll((List<T>) results.values);
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResult = new FilterResults();
                List<T> filteredItems = new ArrayList<>();

                if (constraint != null) {

                    for (T item : originalItems) {
                        String pattern = ".*" + constraint.toString().toLowerCase() + ".*";
                        if (item.toString().toLowerCase().matches(pattern)) {
                            filteredItems.add(item);
                        }
                    }
                    filterResult.values = filteredItems;
                } else {
                    filterResult.values = originalItems;
                }
                return filterResult;
            }
        };
        return filter;
    }


    private static class ViewHolder {
        public TextView itemTextView;
    }

}

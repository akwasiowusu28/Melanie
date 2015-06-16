package com.melanie.androidactivities.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.melanie.androidactivities.R;

import java.util.List;
import java.util.Map.Entry;

public class GroupAdapter<T> extends
        ArrayAdapter<Entry<T, Integer>> {
    private Context context;
    private int resource;
    private List<Entry<T, Integer>> items;

    public GroupAdapter(Context context,
                        List<Entry<T, Integer>> items) {

        super(context, R.layout.layout_two_item_view, items);
        this.context = context;
        resource = R.layout.layout_two_item_view;
        this.items = items;
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

            viewHolder.groupNameTextView = (TextView) rowView
                    .findViewById(R.id.firstItemTextView);
            viewHolder.groupValueTextView = (TextView) rowView
                    .findViewById(R.id.secondItemTextView);
            rowView.setTag(viewHolder);
        }

        Entry<T, Integer> entry = items.get(position);

        if (entry != null) {
            ViewHolder viewHolder = (ViewHolder) rowView.getTag();
            viewHolder.groupNameTextView.setText(entry.getKey().toString());
            viewHolder.groupValueTextView.setText(Integer.toString(entry
                    .getValue()));
        }

        return rowView;
    }

    private static class ViewHolder {
        public TextView groupNameTextView;
        public TextView groupValueTextView;
    }

}

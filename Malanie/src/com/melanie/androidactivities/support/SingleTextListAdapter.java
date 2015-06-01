package com.melanie.androidactivities.support;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.melanie.androidactivities.R;

public class SingleTextListAdapter<T> extends ArrayAdapter<T> {

	private Context context;
	private int resource;
	private List<T> items;

	public SingleTextListAdapter(Context context, List<T> items) {
		super(context, R.layout.layout_single_textview, items);
		this.context = context;
		this.resource = R.layout.layout_single_textview;
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

			viewHolder.itemTextView = (TextView) rowView
					.findViewById(R.id.singleTextViewForListView);
			rowView.setTag(viewHolder);
		}

		T item = items.get(position);
		if (item != null) {
			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			viewHolder.itemTextView.setText(item.toString());
		}

		return rowView;
	}

	private static class ViewHolder {
		public TextView itemTextView;
	}
}

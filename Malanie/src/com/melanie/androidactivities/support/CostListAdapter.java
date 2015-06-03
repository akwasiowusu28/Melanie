package com.melanie.androidactivities.support;

import java.util.List;

import android.content.Context;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.melanie.androidactivities.R;
import com.melanie.entities.CostItem;

public class CostListAdapter extends ArrayAdapter<CostItem> {

	private List<CostItem> costItems;
	private Context context;
	private int resource;
	private TextWatcher textListener;

	public CostListAdapter(Context context, List<CostItem> costItems, TextWatcher textListener) {
		super(context, R.layout.layout_cost_listview,costItems);
		resource = R.layout.layout_cost_listview;
		this.costItems = costItems;
		this.context = context;
		this.textListener = textListener;
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

			viewHolder.costNameTextView = (TextView) rowView
					.findViewById(R.id.costNameTextView);
			viewHolder.costValueEditText = (EditText) rowView
					.findViewById(R.id.costValue);

			rowView.setTag(viewHolder);
		}

		CostItem costItem = costItems.get(position);
		if (costItem != null) {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			viewHolder.costNameTextView.setText(costItem.getName());
			viewHolder.costValueEditText.addTextChangedListener(textListener);
		}

		return rowView;
	}


	private static class ViewHolder {
		public TextView costNameTextView;
		public EditText costValueEditText;

	}
}

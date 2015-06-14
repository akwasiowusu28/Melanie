package com.melanie.androidactivities.support;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.melanie.androidactivities.R;
import com.melanie.entities.Customer;

public class CustomersListViewAdapter extends ArrayAdapter<Customer> {

	private Context context;
	private int resource;
	private List<Customer> customers;

	public CustomersListViewAdapter(Context context, List<Customer> customers) {
		super(context, R.layout.layout_customerlist, customers);
		this.context = context;
		resource = R.layout.layout_customerlist;
		this.customers = customers;
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

			viewHolder.customerNameTextView = (TextView) rowView
					.findViewById(R.id.customerNameTextView);
			viewHolder.phoneNumberTextView = (TextView) rowView
					.findViewById(R.id.customerPhoneTextView);
			viewHolder.amountOwedTextView = (TextView) rowView
					.findViewById(R.id.amountDueTextView);
			rowView.setTag(viewHolder);
		}

		Customer customer = customers.get(position);
		if (customer != null) {

			ViewHolder viewHolder = (ViewHolder) rowView.getTag();
			viewHolder.customerNameTextView.setText(customer.getName());
			viewHolder.phoneNumberTextView.setText(customer.getPhoneNumber());
			viewHolder.amountOwedTextView.setText(String.valueOf(customer
					.getAmountOwed()));
		}

		return rowView;
	}

	private static class ViewHolder {
		public TextView customerNameTextView;
		public TextView phoneNumberTextView;
		public TextView amountOwedTextView;
	}

}

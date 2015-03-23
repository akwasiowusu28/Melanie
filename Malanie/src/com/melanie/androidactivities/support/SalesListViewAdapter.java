package com.melanie.androidactivities.support;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.melanie.androidactivities.R;
import com.melanie.entities.Sale;


public class SalesListViewAdapter extends ArrayAdapter<Sale>{

	private Context context;
	private int resource;
	private List<Sale> sales;
	
	public SalesListViewAdapter(Context context, List<Sale> sales){
		super(context, R.layout.layout_sales_list,sales);
		this.context = context;
		this.sales = sales;
		this.resource = R.layout.layout_sales_list;
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

			viewHolder.productNameTextView = (TextView) rowView
					.findViewById(R.id.productNameTextView);
			
			viewHolder.quantityTextView = (TextView) rowView
					.findViewById(R.id.qtyTextView);
			
			viewHolder.priceTextView = (TextView) rowView
					.findViewById(R.id.unitPriceTextView);

			viewHolder.totalPriceTextView = (TextView) rowView
					.findViewById(R.id.totalTextView);
			
			rowView.setTag(viewHolder);
		}
		
		Sale sale = sales.get(position);
		
		if(sale != null){
			ViewHolder viewHolder = (ViewHolder)rowView.getTag();
			int quantity = sale.getQuantitySold();
			double price = sale.getProduct().getPrice();
			viewHolder.productNameTextView.setText(sale.getProduct().getProductName());
			viewHolder.quantityTextView.setText(String.valueOf(quantity));
			viewHolder.priceTextView.setText(String.valueOf(price));
			viewHolder.totalPriceTextView.setText(String.valueOf(quantity * price));
		}
		return rowView;
	}
	
	private static class ViewHolder {
		public TextView productNameTextView;
		public TextView priceTextView;
		public TextView quantityTextView;
		public TextView totalPriceTextView;
	}
}

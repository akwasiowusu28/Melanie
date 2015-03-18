package com.melanie.androidactivities.support;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.melanie.androidactivities.R;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;


public class SalesListViewAdapter<T> extends ArrayAdapter<T>{

	private Context context;
	private int resource;
	private List<Sale> sales;
	
	public SalesListViewAdapter(Context context, List<Sale> sales){
		super(context, R.layout.layout_sales_list);
		
		this.sales = sales;
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
					.findViewById(R.id.saleProduct);
			viewHolder.priceTextView = (TextView) rowView
					.findViewById(R.id.salePrice);

			rowView.setTag(viewHolder);
		}
		
		Product product = sales.get(position).getProduct();
		
		if(product != null){
			ViewHolder viewHolder = (ViewHolder)rowView.getTag();
			viewHolder.productNameTextView.setText(product.getProductName());
			viewHolder.priceTextView.setText(String.valueOf(product.getPrice()));
		}
		
		return rowView;
	}
	
	private static class ViewHolder {
		public TextView productNameTextView;
		public TextView priceTextView;
	}
}

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

/**
 * The is the adapter used for any listview whose contents are of the form,
 * Name, qty, price, total.
 * 
 * @author Akwasi Owusu
 * 
 * @param <T>
 *            In this case, T is either product or sale
 */
public class ProductsAndSalesListViewAdapter<T> extends ArrayAdapter<T> {

	private Context context;
	private int resource;
	private List<T> items;

	public ProductsAndSalesListViewAdapter(Context context, List<T> items) {
		super(context, R.layout.layout_sales_list, items);
		this.context = context;
		this.items = items;
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

		T item = items.get(position);

		int quantity = 0;
		double price = 0;
		String name = "";

		if (item != null)
			if (item instanceof Sale) {

				Sale sale = (Sale) item;
				quantity = sale.getQuantitySold();
				price = sale.getProduct().getPrice();
				name = sale.getProduct().getProductName();

			} else if (item instanceof Product) {

				Product product = (Product) item;
				quantity = product.getQuantity();
				price = product.getPrice();
				name = product.getProductName();

			}

		ViewHolder viewHolder = (ViewHolder) rowView.getTag();

		viewHolder.productNameTextView.setText(name);
		viewHolder.quantityTextView.setText(String.valueOf(quantity));
		viewHolder.priceTextView.setText(String.valueOf(price));
		viewHolder.totalPriceTextView.setText(String.valueOf(quantity * price));

		return rowView;
	}

	private static class ViewHolder {
		public TextView productNameTextView;
		public TextView priceTextView;
		public TextView quantityTextView;
		public TextView totalPriceTextView;
	}
}

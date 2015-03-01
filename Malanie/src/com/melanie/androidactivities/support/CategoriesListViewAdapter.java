package com.melanie.androidactivities.support;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.melanie.androidactivities.R;
import com.melanie.entities.Category;

public class CategoriesListViewAdapter extends ArrayAdapter<Category> {

	private Context context;
	private int resource;
	private List<Category> categories;
	private int[] productsCount;

	private static class ViewHolder {
		public TextView categoryNametextView;
		public TextView productCounttextView;
	}

	public CategoriesListViewAdapter(Context context, List<Category> categories,
			int[] productsCount) {
		super(context, R.layout.layout_categorieslist, categories);
		this.context = context;
		this.resource = R.layout.layout_categorieslist;
		this.categories = categories;
		this.productsCount = productsCount;
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

			viewHolder.categoryNametextView = (TextView) rowView
					.findViewById(R.id.categoryNameInList);
			viewHolder.productCounttextView = (TextView) rowView
					.findViewById(R.id.productsCount);
			
			rowView.setTag(viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) rowView.getTag();

		viewHolder.categoryNametextView.setText(categories.get(position).getCategoryName());
		//viewHolder.productCounttextView.setText(productsCount[position]);

		return rowView;
	}
}

package com.melanie.androidactivities.support;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.melanie.androidactivities.R;

public class MelanieListViewAdapter extends ArrayAdapter<Integer> {

	private Context context;
	private int resource;
	private Integer[] navigationIcons;
	private Integer[] navigationItems;
	private Integer[] navigationItemsDesc;

	private class ViewHolder {
		public TextView firstLinetextView;
		public TextView secondLinetextView;
		public ImageView mainIconImageView;
	}

	public MelanieListViewAdapter(Context context, Integer[] navigationIcons,
			Integer[] navigationItems, Integer[] navigationItemsDesc) {
		super(context, R.layout.listview_main_page, navigationItems);
		this.context = context;
		this.resource = R.layout.listview_main_page;
		this.navigationItems = navigationItems;
		this.navigationIcons = navigationIcons;
		this.navigationItemsDesc = navigationItemsDesc;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ViewHolder viewHolder = null;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(resource, parent, false);
		}
		
		viewHolder.firstLinetextView = (TextView) convertView
				.findViewById(R.id.firstLine);
		viewHolder.secondLinetextView = (TextView) convertView
				.findViewById(R.id.secondLine);

		viewHolder.mainIconImageView = (ImageView) convertView
				.findViewById(R.id.icon);
		viewHolder.mainIconImageView
				.setImageResource(navigationIcons[position]);

		viewHolder.firstLinetextView.setText(context
				.getString(navigationItems[position]));
		viewHolder.secondLinetextView.setText(context
				.getString(navigationItemsDesc[position]));
		
		
		return convertView;
	}
}

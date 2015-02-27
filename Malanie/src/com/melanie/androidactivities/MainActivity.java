package com.melanie.androidactivities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.melanie.androidactivities.support.Common;
import com.melanie.androidactivities.support.MelanieListViewAdapter;

public class MainActivity extends ListActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setListAdapter(new MelanieListViewAdapter(getBaseContext(),
				Common.getMainPageImages(),
				Common.getMainPageNavigationItems(),
				Common.getMainPageNavigationDescription()));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this,Common.getMelanieActivities().get(position));
		startActivity(intent);
	}

}

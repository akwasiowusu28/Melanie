package com.melanie.androidactivities;

import com.melanie.androidactivities.support.MelanieListViewAdapter;
import com.melanie.support.Common;

import android.app.ListActivity;
import android.os.Bundle;

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

}

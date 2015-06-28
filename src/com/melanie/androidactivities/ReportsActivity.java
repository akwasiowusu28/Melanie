package com.melanie.androidactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.melanie.androidactivities.support.NavigationHelper;
import com.melanie.androidactivities.adapters.NavigationListViewAdapter;

public class ReportsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        ListView reportsListView = (ListView) findViewById(R.id.reportsMainListView);

        reportsListView.setAdapter(new NavigationListViewAdapter(this,
                NavigationHelper.getReportsMainIcons(), NavigationHelper
                .getReportsMainNavigationItems(), NavigationHelper
                .getReportsMainNavigationDescription()));

        reportsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(ReportsActivity.this,
                        NavigationHelper.getReportsActivities().get(position));
                startActivity(intent);

            }

        });
    }
}

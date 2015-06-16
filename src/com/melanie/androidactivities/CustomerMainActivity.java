package com.melanie.androidactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.melanie.androidactivities.support.NavigationHelper;
import com.melanie.androidactivities.support.NavigationListViewAdapter;

public class CustomerMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        ListView mainListView = (ListView) findViewById(R.id.customerMainListview);
        mainListView.setAdapter(new NavigationListViewAdapter(this,
                NavigationHelper.getCustomerMainIcons(), NavigationHelper
                .getCustomerMainNavigationItems(), NavigationHelper
                .getCustomerMainNavigationDescription()));

        mainListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(CustomerMainActivity.this,
                        NavigationHelper.getCustomerActivities().get(position));
                startActivity(intent);
            }

        });
    }
}

package com.melanie.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.melanie.ui.R;
import com.melanie.ui.support.NavigationHelper;
import com.melanie.ui.adapters.NavigationListViewAdapter;

public class ProductsMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productsmain);

        ListView mainListView = (ListView) findViewById(R.id.productMainListview);
        mainListView.setAdapter(new NavigationListViewAdapter(this,
                NavigationHelper.getProductMainIcons(), NavigationHelper
                .getProductMainNavigationItems(), NavigationHelper
                .getProductMainNavigationDescription()));

        mainListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(ProductsMainActivity.this,
                        NavigationHelper.getProductActivities().get(position));
                startActivity(intent);

            }

        });
    }
}

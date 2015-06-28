package com.melanie.androidactivities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.androidactivities.adapters.SingleTextListAdapter;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.entities.Category;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private final ProductEntryController productController;
    private ArrayList<Category> categories;
    private ArrayAdapter<Category> listAdapter;
    private Handler handler;

    public CategoriesActivity() {
        super();
        productController = BusinessFactory.makeProductEntryController();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        } else {
            getAllCategories();
        }
        setupAddCategoryButtonListener();
        setupListView();
        handler = new Handler(getMainLooper());
    }

    private void getAllCategories() {
        categories = new ArrayList<Category>();
        try {
            List<Category> tempCategories = null;
            tempCategories = productController
                    .getAllCategories(new OperationCallBack<Category>() {
                        @Override
                        public void onCollectionOperationSuccessful(
                                List<Category> results) {
                            Utils.mergeItems(results, categories, false);
                            Utils.notifyListUpdate(listAdapter, handler);
                        }
                    });
            if (tempCategories != null && !tempCategories.isEmpty()) {
                categories.addAll(tempCategories);
            }

        } catch (MelanieBusinessException e) {
            e.printStackTrace(); // TODO: log it
        }
    }

    private void setupAddCategoryButtonListener() {
        Button addButton = (Button) findViewById(R.id.addCategoryButton);
        addButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText categoryNameView = (EditText) findViewById(R.id.categoryName);
                String categoryName = categoryNameView.getText().toString();

                if (!categoryName.trim().equals(LocalConstants.EMPTY_STRING)) {

                    Category category;
                    try {
                        Utils.switchInvalidFieldsBackColor(true, categoryNameView);
                        category = productController.addCategory(categoryName);
                        categories.add(category);
                        Utils.notifyListUpdate(listAdapter, handler);
                        Utils.clearInputTextFields(categoryNameView);
                    } catch (MelanieBusinessException e) {
                        e.printStackTrace(); // log it
                    }

                } else {
                    Utils.switchInvalidFieldsBackColor(false, categoryNameView);
                }
            }
        });
    }

    private void setupListView() {

        ListView categoriesListView = (ListView) findViewById(R.id.categoryList);

        View headerView = getLayoutInflater().inflate(
                R.layout.single_list_header, categoriesListView, false);

        TextView headerTextView = (TextView) headerView
                .findViewById(R.id.singleListHeader);
        headerTextView.setText(getText(R.string.categoriesList));
        categoriesListView.addHeaderView(headerView);

        listAdapter = new SingleTextListAdapter<Category>(this,
                categories);

        categoriesListView.setAdapter(listAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable(LocalConstants.CATEGORIES, categories);
        super.onSaveInstanceState(bundle);
    }

    @SuppressWarnings("unchecked")
    private void restoreInstanceState(Bundle bundle) {
        if (bundle != null) {
            categories = (ArrayList<Category>) bundle.get(LocalConstants.CATEGORIES);
        }
    }

    private class LocalConstants {
        public static final String CATEGORIES = "categories";
        public static final String EMPTY_STRING = "";
    }
}

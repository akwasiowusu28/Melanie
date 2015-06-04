package com.melanie.androidactivities;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.androidactivities.support.MelanieAlertDialog;
import com.melanie.androidactivities.support.MelanieAlertDialog.ButtonMethods;
import com.melanie.androidactivities.support.MelanieAlertDialog.ButtonModes;
import com.melanie.androidactivities.support.Utils;
import com.melanie.entities.CostItem;
import com.melanie.support.CodeStrings;

public class RecordCostsActivity extends AppCompatActivity {

	private MelanieAlertDialog promptDialog;
	private View promptInputView;
	private ArrayList<CostItem> costItems;
	private Handler handler;
	private CostListAdapter costItemsAdapter;
	private boolean wasInstanceSaved = false;
	private TextView totalTextView;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_costs);

		if (savedInstanceState != null) {
			wasInstanceSaved = true;
			costItems = (ArrayList<CostItem>) savedInstanceState.get(CodeStrings.COST_ITEMS);
		}

		initializeFields();
		getAllCostEntries();
		setupListView();
		setupPromptDialog();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.record_costs, menu);
		return true;
	}

	private void initializeFields() {
		if (!wasInstanceSaved) {
			costItems = new ArrayList<CostItem>();
		}
		handler = new Handler(getMainLooper());
		costItemsAdapter = new CostListAdapter();
		totalTextView = (TextView) findViewById(R.id.costTotalTextView);
	}

	private void getAllCostEntries() {

	}

	private void setupListView() {
		ListView listView = (ListView) findViewById(R.id.costList);
		listView.setAdapter(costItemsAdapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch (id) {
		case R.id.action_add_cost:
			showAddCostItemPrompt();
			return true;
		}

		return super.onContextItemSelected(item);
	}

	private void showAddCostItemPrompt() {
		if (promptDialog != null) {
			if (promptInputView != null) {
				ViewGroup parent = (ViewGroup) promptInputView.getParent();

				if (parent != null) {
					parent.removeView(promptInputView);
				}
			}
			promptDialog.show();
		}
	}

	private void setupPromptDialog() {
		promptDialog = new MelanieAlertDialog(this, ButtonModes.OK_CANCEL, buttonMethods);
		LayoutInflater inflater = LayoutInflater.from(this);
		promptInputView = inflater.inflate(R.layout.layout_single_edit_text, null, false);
		promptDialog.setView(promptInputView);
		promptDialog.setTitle(R.string.addCostItem);
		promptDialog.create();
	}

	private ButtonMethods buttonMethods = new ButtonMethods() {

		@Override
		public void okButtonOperation() {
			EditText costNameTextView = (EditText) promptInputView.findViewById(R.id.costNameEditText);
			String costName = costNameTextView.getText().toString();
			if (!costName.trim().equals(CodeStrings.EMPTY_STRING)) {
				costItems.add(new CostItem(costName));
				Utils.notifyListUpdate(costItemsAdapter, handler);
				costNameTextView.setText(CodeStrings.EMPTY_STRING);
			}
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		bundle.putSerializable(CodeStrings.COST_ITEMS, costItems);
		super.onSaveInstanceState(bundle);
	}

	private class CostListAdapter extends ArrayAdapter<CostItem> {

		private int resource;

		public CostListAdapter() {
			super(RecordCostsActivity.this, R.layout.layout_cost_listview, costItems);
			resource = R.layout.layout_cost_listview;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) RecordCostsActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View rowView = convertView;

			if (rowView == null) {
				ViewHolder viewHolder = null;
				viewHolder = new ViewHolder();
				rowView = inflater.inflate(resource, parent, false);

				viewHolder.costNameTextView = (TextView) rowView.findViewById(R.id.costNameTextView);
				viewHolder.costValueEditText = (EditText) rowView.findViewById(R.id.costValue);

				rowView.setTag(viewHolder);
			}

			CostItem costItem = costItems.get(position);
			if (costItem != null) {

				ViewHolder viewHolder = (ViewHolder) rowView.getTag();
				viewHolder.costNameTextView.setText(costItem.getName());

				EditText valueText = viewHolder.costValueEditText;
				valueText.addTextChangedListener(new CostTextWatcher(valueText, position));
			}

			return rowView;
		}

	}

	private static class ViewHolder {
		public TextView costNameTextView;
		public EditText costValueEditText;

	}

	private class CostTextWatcher implements TextWatcher {

		private int position;
		EditText sender;
		private double oldValue = 0;

		public CostTextWatcher(EditText sender, int position) {
			this.position = position;
			this.sender = sender;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			if (oldValue == 0) {
				String strValue = sender.getText().toString();
				oldValue = strValue.trim().equals(CodeStrings.EMPTY_STRING) ? 0 : Double.parseDouble(strValue);
			}
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			double newValue = Double.parseDouble(sender.getText().toString());
			double adjustedTotal = Double.parseDouble(totalTextView.getText().toString()) - oldValue;
			totalTextView.setText(String.valueOf(adjustedTotal + newValue));
			oldValue = 0;
		}
	}
}

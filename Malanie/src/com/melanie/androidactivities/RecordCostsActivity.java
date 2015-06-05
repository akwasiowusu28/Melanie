package com.melanie.androidactivities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.melanie.androidactivities.support.MelanieAlertDialog;
import com.melanie.androidactivities.support.MelanieAlertDialog.ButtonMethods;
import com.melanie.androidactivities.support.MelanieAlertDialog.ButtonModes;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.entities.CostEntry;
import com.melanie.entities.CostItem;
import com.melanie.support.BusinessFactory;
import com.melanie.support.CodeStrings;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public class RecordCostsActivity extends AppCompatActivity {

	private MelanieAlertDialog promptDialog;
	private View promptInputView;

	private ArrayList<CostEntry> costEntries;

	private Handler handler;
	private CostListAdapter costEntriesAdapter;
	private boolean wasInstanceSaved = false;
	private TextView totalTextView;
	private Button saveButton;
	private double total = 0D;
	private ProductEntryController productEntryController;

	private SimpleDateFormat dateformater;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_costs);

		if (savedInstanceState != null) {
			wasInstanceSaved = true;
			costEntries = (ArrayList<CostEntry>) savedInstanceState.get(CodeStrings.COST_ENTRIES);
			total = savedInstanceState.getDouble(CodeStrings.TOTAL);
		}

		initializeFields();

		setupSaveButton();
		getAllCostItems();
		setupListView();
		setupPromptDialog();

		setTitle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.record_costs, menu);
		return true;
	}

	private void initializeFields() {
		if (!wasInstanceSaved) {
			costEntries = new ArrayList<>();
		}
		handler = new Handler(getMainLooper());
		costEntriesAdapter = new CostListAdapter();
		totalTextView = (TextView) findViewById(R.id.costTotalTextView);
		if (total != 0D) {
			totalTextView.setText(String.valueOf(total));
		}

		productEntryController = BusinessFactory.makeProductEntryController();

		dateformater = new SimpleDateFormat(CodeStrings.DATEFORMAT,
				Locale.getDefault());
	}

	private void setTitle(){
		String currentDate = dateformater.format(Calendar.getInstance().getTime());
		String title = this.getTitle().toString() + " " + currentDate;
		this.setTitle(title);
	}

	private void getAllCostItems() {
		try {
			List<CostItem> costItems = productEntryController.getAllCostItems(new OperationCallBack<CostItem>() {
				@Override
				public void onCollectionOperationSuccessful(List<CostItem> results) {
					addNewCostEntries(results);
					Utils.notifyListUpdate(costEntriesAdapter, handler);
				}
			});

			addNewCostEntries(costItems);

			Utils.notifyListUpdate(costEntriesAdapter, handler);

		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // TODO: log it
		}
	}

	private void addNewCostEntries(List<CostItem> costItems) {
		for (CostItem costItem : costItems) {
			if (!costItemExists(costItem)) {
				costEntries.add(new CostEntry(costItem, 0D));
			}
		}
	}

	private boolean costItemExists(CostItem item) {
		boolean costItemExists = false;

		for (CostEntry costEntry : costEntries) {
			if (costEntry.getCostItem().getId() == item.getId()) {
				costItemExists = true;
				break;
			}
		}
		return costItemExists;
	}

	private void setupSaveButton() {
		saveButton = (Button) findViewById(R.id.costSaveButton);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveCosts();
			}
		});
	}

	private void setupListView() {
		ListView listView = (ListView) findViewById(R.id.costList);
		listView.setAdapter(costEntriesAdapter);
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
				CostItem costItem = new CostItem(costName);
				costEntries.add(new CostEntry(costItem, 0));
				Utils.notifyListUpdate(costEntriesAdapter, handler);
				costNameTextView.setText(CodeStrings.EMPTY_STRING);
			}
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		bundle.putSerializable(CodeStrings.COST_ENTRIES, costEntries);
		bundle.putDouble(CodeStrings.TOTAL, total);
		super.onSaveInstanceState(bundle);
	}

	private class CostListAdapter extends ArrayAdapter<CostEntry> {

		private int resource;

		public CostListAdapter() {
			super(RecordCostsActivity.this, R.layout.layout_cost_listview, costEntries);
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

			CostEntry costEntry = costEntries.get(position);
			if (costEntry != null) {

				ViewHolder viewHolder = (ViewHolder) rowView.getTag();
				viewHolder.costNameTextView.setText(costEntry.getCostItem().getName());

				if (viewHolder.textWatcher != null) {
					viewHolder.costValueEditText.removeTextChangedListener(viewHolder.textWatcher);
				}
				double costEntryValue = costEntry.getValue();
				if (costEntryValue != 0D) {
					viewHolder.costValueEditText.setText(String.valueOf(costEntry.getValue()));
				}else{
					viewHolder.costValueEditText.setText(CodeStrings.EMPTY_STRING);
				}

				viewHolder.textWatcher = new CostTextWatcher(viewHolder.costValueEditText, position);
				viewHolder.costValueEditText.addTextChangedListener(viewHolder.textWatcher);
			}

			return rowView;
		}

	}

	private static class ViewHolder {
		public TextView costNameTextView;
		public EditText costValueEditText;
		private CostTextWatcher textWatcher;
	}

	private class CostTextWatcher implements TextWatcher {

		EditText sender;
		private double oldValue = 0;
		private int position;

		public CostTextWatcher(EditText sender, int position) {
			this.sender = sender;
			this.position = position;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			if (oldValue == 0) {
				String strValue = sender.getText().toString().trim();
				oldValue = strValue.equals(CodeStrings.EMPTY_STRING) ? 0 : Double.parseDouble(strValue);
			}
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String emptyStr = CodeStrings.EMPTY_STRING;

			String newValueStr = sender.getText().toString().trim();
			double newValue = newValueStr.equals(emptyStr) ? 0 : Double.parseDouble(newValueStr);

			String totalStr = totalTextView.getText().toString().trim();
			double currentTotal = totalStr.equals(emptyStr) ? 0 : Double.parseDouble(totalStr);

			double adjustedTotal = currentTotal - oldValue;
			total = adjustedTotal + newValue;
			totalTextView.setText(String.valueOf(total));

			costEntries.get(position).setValue(newValue);

			oldValue = 0;
		}
	}

	private void saveCosts() {
		OperationResult result = OperationResult.FAILED;

		if (productEntryController != null) {
			try {

				result = productEntryController.saveCostEntries(costEntries);

				//reset costs
				for (CostEntry costEntry : costEntries) {
					costEntry.setValue(0d);
				}

				Utils.notifyListUpdate(costEntriesAdapter, handler);

				totalTextView.setText(R.string.amountZeroes);

			} catch (MelanieBusinessException e) {
				// TODO log it
				e.printStackTrace();
			}
		}

		Utils.makeToastBasedOnOperationResult(this, result, R.string.costsSavedSuccessfully, R.string.saveCostsfailed);
	}
}

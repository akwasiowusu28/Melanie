package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.EditText;
import android.widget.ListView;

import com.melanie.androidactivities.support.CostListAdapter;
import com.melanie.androidactivities.support.MelanieAlertDialog;
import com.melanie.androidactivities.support.MelanieAlertDialog.ButtonMethods;
import com.melanie.androidactivities.support.MelanieAlertDialog.ButtonModes;
import com.melanie.androidactivities.support.Utils;
import com.melanie.entities.CostItem;

public class RecordCostsActivity extends AppCompatActivity {

	private MelanieAlertDialog promptDialog;
	private View promptInputView;
	private List<CostItem> costItems;
	private Handler handler;
	private CostListAdapter costItemsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_costs);
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

	private void initializeFields(){
		costItems = new ArrayList<CostItem>();
		handler = new Handler(getMainLooper());
		costItemsAdapter = new CostListAdapter(this, costItems, textListener);
	}

	private void getAllCostEntries(){

	}

	private void setupListView(){
		ListView listView = (ListView)findViewById(R.id.costListView);
		listView.setAdapter(costItemsAdapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();
		switch(id){
		case R.id.action_add_cost:
			showAddCostItemPrompt();
			return true;
		}

		return super.onContextItemSelected(item);
	}

	private void showAddCostItemPrompt(){
		if(promptDialog != null){
			if(promptInputView != null){
				ViewGroup parent = (ViewGroup)promptInputView.getParent();

				if(parent != null){
					parent.removeView(promptInputView);
				}
			}
			promptDialog.show();
		}
	}

	private void setupPromptDialog(){
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
			EditText costNameTextView = (EditText)promptInputView.findViewById(R.id.costNameEditText);
			String costName = costNameTextView.getText().toString();
			costItems.add(new CostItem(costName));
			Utils.notifyListUpdate(costItemsAdapter, handler);
		}
	};

	private TextWatcher textListener = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
		}
	};
}

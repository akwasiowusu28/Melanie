package com.melanie.androidactivities.support;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class MelanieDatePicker extends DialogFragment {

	private Context context;
	private OnDateSetListener dateSetListener;

	public MelanieDatePicker(Context context, OnDateSetListener dateSetListener) {
		super();
		this.context = context;
		this.dateSetListener = dateSetListener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			final Calendar c = Calendar.getInstance(TimeZone.getDefault());
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			return new DatePickerDialog(context, dateSetListener, year, month,
					day);
		}
		return super.onCreateDialog(savedInstanceState);
	}
}

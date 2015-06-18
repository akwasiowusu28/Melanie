package com.melanie.androidactivities.support;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.TimeZone;

public class MelanieDatePicker extends DialogFragment {

    private class LocalConstants{
        public static final  String CONTEXT = "context";
        public static final String DATELISTENER = "dateListener";
    }

    private Context context;
    private OnDateSetListener dateSetListener;

    public MelanieDatePicker(){
        super();
    }

    public static MelanieDatePicker createInstance(Context context, OnDateSetListener dateSetListener){
           MelanieDatePicker picker = new MelanieDatePicker();

        picker.setContext(context);
        picker.setDateSetListener(dateSetListener);

        return picker;
    }

    public OnDateSetListener getDateSetListener() {
        return dateSetListener;
    }

    public void setDateSetListener(OnDateSetListener dateSetListener) {
        this.dateSetListener = dateSetListener;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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

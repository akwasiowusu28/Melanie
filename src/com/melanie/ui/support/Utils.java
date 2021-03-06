package com.melanie.ui.support;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.melanie.ui.activities.MainActivity;
import com.melanie.ui.R;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Akwasi Owusu This class is a utility class that performs various
 *         functions used across multiple activities
 */
public final class Utils {
    private static Drawable defaultEditTextBackground = null;
    private static BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();

            boolean connectionUnavailable = extras.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY);

            BusinessFactory.getSession().setCanConnectToCloud(!connectionUnavailable);
        }
    };

    /**
     * Updates a listview to reflect changes when items are added or deleted
     *
     * @param adapter the adapter whose list should be updated
     * @param handler the handler associated with the UI thread invoking this method
     */
    public static void notifyListUpdate(final BaseAdapter adapter, Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public static String getBarcodePrefix() {
        return "281989";
    }

    /**
     * Clears any number of text fields supplied
     *
     * @param views the editTextFields
     */
    public static void clearInputTextFields(View... views) {
        for (View view : views) {
           if(view instanceof EditText)
               ( (EditText)view).setText("");
        }
    }

    /**
     * Reset the text of a set of EditText and TextView fields to 0.00
     *
     * @param views The views to reset
     */
    public static void resetTextFieldsToZeros(View... views) {
        for (View view : views)
            if (view instanceof EditText) {
                ((EditText) view).setText(R.string.amountZeroes);
            } else if (view instanceof TextView) {
                ((TextView) view).setText(R.string.amountZeroes);
            }
    }

    /**
     * Common Toast maker for all of Melanie based on OperationResults
     *
     * @param context         The activity that wants to make a toast
     * @param result          One of two OperationResult enums based on which toast string
     *                        will be shown
     * @param successStringId The string to display on toast when OperationResult is
     *                        SUCESSFUL
     * @param failureStringId The string to display on toast when OperationResult is FAILED
     */
    public static void makeToastBasedOnOperationResult(Context context, OperationResult result, int successStringId,
                                                       int failureStringId) {

        int toastString = result.equals(OperationResult.FAILED) ? failureStringId : successStringId;

        Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
    }

    /**
     * General Toast maker for all of Melanie
     *
     * @param context  The activity that wants to make a toast
     * @param stringId The string to display on toast
     */
    public static void makeToast(final Context context,final int stringId) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, stringId, Toast.LENGTH_LONG).show();
            }
        });
    }


    public static <T> void mergeItems(List<T> sourceItems, List<T> targetItems, boolean keepTargetFirstItem) {
        for (T item : sourceItems)
            if (!targetItems.contains(item)) {
                targetItems.add(item);
            }

        Iterator<T> iterator = targetItems.iterator();

        if (keepTargetFirstItem) {
            iterator.next();
        }

        while (iterator.hasNext()) {
            T item = iterator.next();
            if (!sourceItems.contains(item)) {
                iterator.remove();
            }
        }
    }

    public static int getTextColor(Context context) {
        return context instanceof MainActivity ? Color.WHITE : Color.BLACK;
    }

    public static <T> Map<T, Integer> groupItems(List<T> items) {
        Map<T, Integer> itemGroup = new HashMap<>();

        for (T item : items)
            if (!itemGroup.containsKey(item)) {
                itemGroup.put(item, 1);
            } else {
                Integer itemCount = itemGroup.get(item);
                itemCount++;
                itemGroup.put(item, itemCount);
            }

        return itemGroup;
    }

    public static Date getDateToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    public static Date getDateToEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getMaximum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    public static Date getDateForFirstMonthDay(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static Date getDateForLastMonthDay(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    public static void registerConnectivityReceiver(Context context) {
        context.registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public static void unregisterConnectivityReceiver(Context context) {
        context.unregisterReceiver(connectivityReceiver);
        connectivityReceiver = null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void switchInvalidFieldsBackColor(boolean isValid, EditText... fields) {
        if (defaultEditTextBackground == null && fields.length > 0) {
            defaultEditTextBackground = fields[0].getBackground();
        }
        if (isValid) {
            for (EditText field : fields) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    field.setBackground(defaultEditTextBackground);
                } else {
                    field.setBackgroundResource(android.R.drawable.edit_text);
                }
            }
        } else {
            for (EditText field : fields) {
                field.setBackgroundColor(Color.rgb(250, 213, 182));
            }
        }
    }

    public static void switchViewVisibitlity(boolean visible, View... views) {
        for (View view : views) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public static int getCheckSumDigit(String barcode) {
        char[] chars = barcode.toCharArray();

        int sum = 0;
        for (int i = chars.length - 1; i >= 0; i--)
            if ((i & 1) == 1) {
                sum += num(chars[i]) * 3;
            } else {
                sum += num(chars[i]);
            }
        int mod10ofSum = sum % 10;

        return  mod10ofSum > 0 ? 10 - mod10ofSum : 0;

    }

    public static int num(char charValue) {
        return Character.getNumericValue(charValue);
    }

    public static boolean isAnyFieldEmpty(EditText... fields) {
        boolean isAnyInValid = false;
        for (EditText field : fields) {
            if (field != null && field.getText().toString().equals(Constants.EMPTY_STRING)) {
                Utils.switchInvalidFieldsBackColor(false, field);
                if (!isAnyInValid) {
                    isAnyInValid = true;
                }
            } else {
                Utils.switchInvalidFieldsBackColor(true, field);
            }
        }
        return isAnyInValid;
    }

    public static class Constants {
        public static final int BLUETOOTH_REQUEST_CODE = 208;
        public static final int PRINTER_SELECT_REQUEST_CODE = 28;
        public static final String EMPTY_STRING = "";
        public static final String IS_DAILY = "isDaily";
        public static final String NONE = "None";
        public static final boolean PRINTING_OFF = false;
    }

    public static void dismissKeyboard(Context context, EditText editText){
        if(editText != null){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }
}

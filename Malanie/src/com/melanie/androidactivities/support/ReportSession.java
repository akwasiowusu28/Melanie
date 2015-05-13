package com.melanie.androidactivities.support;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import com.melanie.business.SalesController;
import com.melanie.entities.Sale;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class ReportSession {

	private static final String DATEFORMAT = "dd-MM-yyyy";

	private static ReportSession instance;

	private List<Entry<String, Integer>> dailySalesDisplayItems;
	private List<Entry<String, Integer>> monthlySalesDisplayItems;
	private SalesController salesController;
	private List<Sale> sales;
	private Date startDate;
	private SimpleDateFormat dateformater;
	private Date endDate;
	private List<ObservablePropertyChangedListener> observablePropertyChangedListeners;
	private String selectedDateString;

	private boolean startDateUnchanged = false;
	private boolean endDateUnchanged = false;

	public static class PropertyNames {
		public static final String DISPLAYITEMS = "dailySalesDisplayItems";
		public static final String STARTDATE = "startDate";
		public static final String ENDDATE = "endDate";
	}

	private ReportSession() {
		initializeFields();
	}

	public static ReportSession getInstance(
			ObservablePropertyChangedListener listener) {
		if (instance == null)
			synchronized (ReportSession.class) {
				if (instance == null)
					instance = new ReportSession();
			}
		if (listener != null)
			instance.observablePropertyChangedListeners.add(listener);
		return instance;
	}

	private void initializeFields() {
		observablePropertyChangedListeners = new ArrayList<ObservablePropertyChangedListener>();
		dailySalesDisplayItems = new ArrayList<Map.Entry<String, Integer>>();
		monthlySalesDisplayItems = new ArrayList<Map.Entry<String, Integer>>();
		sales = new ArrayList<Sale>();
		salesController = MelanieBusinessFactory.makeSalesController();
		dateformater = new SimpleDateFormat(DATEFORMAT, Locale.getDefault());
		initializeDates();
		getGroupedSales(false);
	}

	private void initializeDates() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

		startDate = Utils.getDateToStartOfDay(calendar);
		endDate = Utils.getDateToEndOfDay(calendar);
	}

	public void getGroupedSales(final boolean isDaily) {

		if (!(startDateUnchanged && endDateUnchanged)) {
			if (!sales.isEmpty())
				sales.clear();
			try {
				sales.addAll(salesController.getSalesBetween(startDate,
						endDate, new MelanieOperationCallBack<Sale>() {

							@Override
							public void onCollectionOperationSuccessful(
									List<Sale> results) {
								Utils.mergeItems(results, sales);
								updateDisplayItems(isDaily);
							}
						}));
			} catch (MelanieBusinessException e) {
				e.printStackTrace(); // TODO log it
			}
			updateDisplayItems(isDaily);
		}
	}

	private void updateDailySalesDisplayItems() {

		Map<String, Integer> displayDateGroup = new HashMap<String, Integer>();
		dailySalesDisplayItems.clear();
		for (Sale sale : sales) {
			String date = dateformater.format(sale.getSaleDate());
			int quantity = displayDateGroup.containsKey(date) ? displayDateGroup
					.get(date) : 0;
			quantity += sale.getQuantitySold();
			displayDateGroup.put(date, quantity);
		}

		dailySalesDisplayItems.addAll(displayDateGroup.entrySet());
		notifyPropertyChanged(PropertyNames.DISPLAYITEMS);

	}

	private void updateMonthlySalesDisplayItems() {
		Map<String, Integer> displayMonthGroup = new HashMap<String, Integer>();
		SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM");
		monthlySalesDisplayItems.clear();
		for (Sale sale : sales) {
			String date = monthFormatter.format(sale.getSaleDate());
			int quantity = displayMonthGroup.containsKey(date) ? displayMonthGroup
					.get(date) : 0;
			quantity += sale.getQuantitySold();
			displayMonthGroup.put(date, quantity);
		}

		monthlySalesDisplayItems.addAll(displayMonthGroup.entrySet());
		notifyPropertyChanged(PropertyNames.DISPLAYITEMS);
	}

	private void updateDisplayItems(boolean isDaily) {
		if (isDaily)
			updateDailySalesDisplayItems();
		else
			updateMonthlySalesDisplayItems();
	}

	public List<Entry<String, Integer>> getDisplayItems(boolean isDaily) {
		return isDaily ? dailySalesDisplayItems : monthlySalesDisplayItems;
	}

	public void setDisplayItems(List<Entry<String, Integer>> displayItems) {
		dailySalesDisplayItems = displayItems;
		notifyPropertyChanged(PropertyNames.DISPLAYITEMS);
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		if (!startDate.equals(this.startDate)) {
			this.startDate = startDate;
			startDateUnchanged = false;
			notifyPropertyChanged(PropertyNames.STARTDATE);

		} else
			startDateUnchanged = true;

	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		if (!endDate.equals(this.endDate)) {
			this.endDate = endDate;
			endDateUnchanged = false;
			notifyPropertyChanged(PropertyNames.ENDDATE);
		} else
			endDateUnchanged = true;
	}

	public String getSelectedDateString() {
		return selectedDateString;
	}

	public void setSelectedDate(String selectedDateString) {
		this.selectedDateString = selectedDateString;
	}

	public List<Sale> getSales() {
		return sales;
	}

	public void setSales(List<Sale> sales) {
		this.sales = sales;
	}

	private void notifyPropertyChanged(String propertyName) {
		for (ObservablePropertyChangedListener listener : observablePropertyChangedListeners)
			listener.onObservablePropertyChanged(propertyName);
	}
}

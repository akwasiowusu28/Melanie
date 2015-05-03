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

	private List<Entry<String, Integer>> displayItems;
	private SalesController salesController;
	private List<Sale> sales;
	private Date startDate;
	private SimpleDateFormat dateformater;
	private Date endDate;
	private List<ObservablePropertyChangedListener> observablePropertyChangedListeners;

	public static class PropertyNames {
		public static final String DISPLAYITEMS = "displayItems";
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
		instance.observablePropertyChangedListeners.add(listener);
		return instance;
	}

	private void initializeFields() {
		observablePropertyChangedListeners = new ArrayList<ObservablePropertyChangedListener>();
		displayItems = new ArrayList<Map.Entry<String, Integer>>();
		sales = new ArrayList<Sale>();
		salesController = MelanieBusinessFactory.makeSalesController();
		dateformater = new SimpleDateFormat(DATEFORMAT, Locale.getDefault());
		initializeDates();
		getGroupedSales();
	}

	private void initializeDates() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

		startDate = Utils.getStartOfDay(calendar);
		endDate = Utils.getEndOfDay(calendar);
	}

	public void getGroupedSales() {
		try {
			if (!sales.isEmpty())
				sales.clear();
			sales.addAll(salesController.getSalesBetween(startDate, endDate,
					new MelanieOperationCallBack<Sale>(this.getClass()
							.toString()) {

						@Override
						public void onCollectionOperationSuccessful(
								List<Sale> results) {
							Utils.mergeItems(results, sales);
							updateDisplayItems();
						}
					}));
			updateDisplayItems();

		} catch (MelanieBusinessException e) {
			e.printStackTrace(); // TODO log it
		}
	}

	private void updateDisplayItems() {

		Map<String, Integer> displayDateGroup = new HashMap<String, Integer>();
		displayItems.clear();
		for (Sale sale : sales) {
			String date = dateformater.format(sale.getSaleDate());
			int quantity = displayDateGroup.containsKey(date) ? displayDateGroup
					.get(date) : 0;
			quantity += sale.getQuantitySold();
			displayDateGroup.put(date, quantity);
		}

		displayItems.addAll(displayDateGroup.entrySet());
		notifyPropertyChanged(PropertyNames.DISPLAYITEMS);

	}

	public List<Entry<String, Integer>> getDisplayItems() {
		return displayItems;
	}

	public void setDisplayItems(List<Entry<String, Integer>> displayItems) {
		this.displayItems = displayItems;
		notifyPropertyChanged(PropertyNames.DISPLAYITEMS);
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
		notifyPropertyChanged(PropertyNames.STARTDATE);

	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		notifyPropertyChanged(PropertyNames.ENDDATE);
	}

	private void notifyPropertyChanged(String propertyName) {
		for (ObservablePropertyChangedListener listener : observablePropertyChangedListeners)
			listener.onObservablePropertyChanged(propertyName);
	}
}

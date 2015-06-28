package com.melanie.androidactivities.support;

import com.melanie.business.SalesController;
import com.melanie.business.UserController;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;
import com.melanie.entities.User;
import com.melanie.support.BusinessFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ReportSession {

    private static ReportSession instance;

    private List<SalesReportItem> dailySalesDisplayItems;
    private List<SalesReportItem> monthlySalesDisplayItems;
    private SalesController salesController;
    private List<Sale> sales;
    private Date startDate;
    private Date endDate;
    private List<ObservablePropertyChangedListener> observablePropertyChangedListeners;
    private Date selectedDate;
    private boolean startDateUnchanged = false;
    private boolean endDateUnchanged = false;
    private List<User> users;
    private UserController userController;
    private boolean usersRequestMade = false;

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
        observablePropertyChangedListeners = new ArrayList<>();
        dailySalesDisplayItems = new ArrayList<>();
        monthlySalesDisplayItems = new ArrayList<>();
        sales = new ArrayList<>();
        users = new ArrayList<>();
        salesController = BusinessFactory.makeSalesController();
        userController = BusinessFactory.makeUserController();
        initializeDates();
    }

    private void initializeDates() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        startDate = Utils.getDateToStartOfDay(calendar);
        endDate = Utils.getDateToEndOfDay(calendar);
    }

    public void initializeData(boolean isDaily) {

        loadSales(isDaily);
        if(!usersRequestMade) {
            loadUsers();
            usersRequestMade = true;
        }
    }

    private void loadSales(final boolean isDaily){
        if (!(startDateUnchanged && endDateUnchanged)) {
            if (!sales.isEmpty())
                sales.clear();
            try {
                sales.addAll(salesController.getSalesBetween(startDate,
                        endDate, new OperationCallBack<Sale>() {

                            @Override
                            public void onCollectionOperationSuccessful(
                                    List<Sale> results) {
                                Utils.mergeItems(results, sales, false);
                                updateDisplayItems(isDaily);
                            }
                        }));
            } catch (MelanieBusinessException e) {
                e.printStackTrace(); // TODO log it
            }
        }
        updateDisplayItems(isDaily);
    }

    private void loadUsers(){
        if(users.isEmpty()){
            try {
                userController.getAllUsers(new OperationCallBack<User>() {

                    @Override
                    public void onCollectionOperationSuccessful(List<User> results) {
                        users.addAll(results);
                        notifyPropertyChanged(PropertyNames.USERS);
                    }
                });
            } catch (MelanieBusinessException e) {
                e.printStackTrace(); //TODO log it
            }
        }
    }

    private void updateDisplayItems(boolean isDaily) {
      updateDisplayItems(isDaily, Utils.Constants.NONE);
    }

    private void updateDisplayItems(boolean isDaily, String ownerId) {

        List<SalesReportItem> reportItems = getDisplayItemsGroup(isDaily, ownerId);

            if(isDaily) {
                dailySalesDisplayItems.clear();
                dailySalesDisplayItems.addAll(reportItems);
            }
            else{
                monthlySalesDisplayItems.clear();
                monthlySalesDisplayItems.addAll(reportItems);
            }
            notifyPropertyChanged(PropertyNames.DAILY_SALES_DISPLAY_ITEMS);
    }


    private List<SalesReportItem> getDisplayItemsGroup(boolean isDaily, String ownerId){
        List<SalesReportItem> salesReportItems = new ArrayList<>();
        Map<String, Integer> indexLookUp = new HashMap<>();

        SimpleDateFormat dateFormatter = new SimpleDateFormat( isDaily ? LocalConstants.DAILY_FORMAT : LocalConstants.MONTHLY_FORMAT);
        for (Sale sale : sales) {
            String date = dateFormatter.format(sale.getSaleDate());

            Product product = sale.getProduct();
            if(indexLookUp.containsKey(date)){

                int index = indexLookUp.get(date);
                SalesReportItem reportItem = salesReportItems.get(index);
                int saleQuantity = sale.getQuantitySold();
                int quantity = reportItem.getQuantity() + saleQuantity;
                reportItem.setQuantity(quantity);
                if(product != null){
                    double total = saleQuantity * product.getPrice();
                    reportItem.setTotal(reportItem.getTotal() + total);
                }
            }
            else
            {
                double total = 0D;
                int quantity = sale.getQuantitySold();
                if(product != null) {
                   total = quantity * product.getPrice();
                }
                if(ownerId != Utils.Constants.NONE && !sale.getOwnerId().equals(ownerId)){
                    continue;
                }
                SalesReportItem reportItem = new SalesReportItem(date,quantity,total,sale.getSaleDate());
                salesReportItems.add(reportItem);
                indexLookUp.put(date, salesReportItems.indexOf(reportItem));
            }
        }
        return salesReportItems;
    }

    public List<SalesReportItem> getDisplayItems(boolean isDaily) {
        return isDaily ? dailySalesDisplayItems : monthlySalesDisplayItems;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void filterDisplayItemsByUser(boolean isDaily, String ownerId){
       updateDisplayItems(isDaily, ownerId);
    }

    public void setStartDate(Date startDate) {
        if (!startDate.equals(this.startDate)) {
            this.startDate = startDate;
            startDateUnchanged = false;
            notifyPropertyChanged(PropertyNames.START_DATE);

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
            notifyPropertyChanged(PropertyNames.END_DATE);
        } else
            endDateUnchanged = true;
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public List<User> getUsers(){
        return users;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }

    private void notifyPropertyChanged(String propertyName) {
        for (ObservablePropertyChangedListener listener : observablePropertyChangedListeners)
            listener.onObservablePropertyChanged(propertyName);
    }

    public static class PropertyNames {
        public static final String DAILY_SALES_DISPLAY_ITEMS = "dailySalesDisplayItems";
        public static final String START_DATE = "startDate";
        public static final String END_DATE = "endDate";
        public static final String USERS = "users";
    }

    private static class LocalConstants {
        public static final String DAILY_FORMAT = "MMM dd, yyyy";
        public static final String MONTHLY_FORMAT = "MMMM";
    }

    public void removeListener(ObservablePropertyChangedListener listener){
        observablePropertyChangedListeners.remove(listener);
    }
}

package com.melanie.business.concrete;

import com.melanie.androidactivities.support.Utils;
import com.melanie.business.MelanieSession;
import com.melanie.business.ProductEntryController;
import com.melanie.business.SalesController;
import com.melanie.dataaccesslayer.CloudAccess;
import com.melanie.dataaccesslayer.DataAccessLayer;
import com.melanie.entities.Customer;
import com.melanie.entities.Payment;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;
import com.melanie.entities.SalePayment;
import com.melanie.support.BusinessFactory;
import com.melanie.support.DataFactory;
import com.melanie.support.MelanieArgumentValidator;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.SupportFactory;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TimeZone;

public class SalesControllerImpl implements SalesController {

    private final ProductEntryController productController;
    private final DataAccessLayer dataAccess;
    private final CloudAccess cloudAccess;
    private final Queue<String> notFoundProducts;
    private final MelanieSession session;
    private List<Sale> sales;
    private int operationCount = 0;
    private boolean isSaving = false;
    private SimpleDateFormat dateFormatter;
    MelanieArgumentValidator validator;

    public SalesControllerImpl() {
        productController = BusinessFactory.makeProductEntryController();
        dataAccess = DataFactory.makeDataAccess();
        sales = new ArrayList<Sale>();
        notFoundProducts = new LinkedList<String>();
        session = BusinessFactory.getSession();
        cloudAccess = DataFactory.makeCloudAccess();
        dateFormatter = new SimpleDateFormat(LocalConstants.DATEFORMAT);
        validator = SupportFactory.makeValidator();
    }

    @Override
    public List<Sale> generateSaleItems(List<String> barcodes, OperationCallBack<Sale> uiCallBack)
            throws MelanieBusinessException {
        operationCount = 0;
        Map<String, Integer> itemGroup = Utils.groupItems(barcodes);
        Set<String> groupedBarcodeSet = itemGroup.keySet();
        int totalItems = groupedBarcodeSet.size();

        for (String barcode : groupedBarcodeSet) {
            try {
                String barcodeNoChecksum = getBarcodeNoChecksum(barcode);
                int quantity = itemGroup.get(barcode);
                Sale sale = getExistingSale(barcodeNoChecksum);
                if (sale != null) {
                    sale.setQuantitySold(sale.getQuantitySold() + quantity);
                    updateProductQuantityOfSale(sale, quantity);
                } else if (!notFoundProducts.contains(barcodeNoChecksum)) {
                    addNewSale(barcodeNoChecksum, quantity, totalItems, uiCallBack);
                }
            } catch (MelanieBusinessException e) {
                throw new MelanieBusinessException(e.getMessage(), e); // TODO: log it
            }
        }
        return sales;
    }

    private void addNewSale(final String barcode, final int count, final int total,
                            final OperationCallBack<Sale> uiCallBack) throws MelanieBusinessException {
        Product product;

        product = productController.findProductByBarcode(barcode, new OperationCallBack<Product>() {

            @Override
            public void onOperationSuccessful(Product result) {
                if (result == null) {
                    addBarcodeToNotFoundList(barcode);
                }
                operationCount++;
                addProductToSale(result, count);
                if (uiCallBack != null && operationCount == total) {
                    uiCallBack.onCollectionOperationSuccessful(sales);
                    operationCount = 0;
                }
            }
        });

        if (product != null) {
            addProductToSale(product, count);
        }
    }

    private void addProductToSale(Product product, int count) {
        if (product != null && product.getQuantity() > 0) {
            Sale sale = new Sale();
            sale.setProduct(product);
            sale.setSaleDate(Calendar.getInstance(TimeZone.getDefault()).getTime());
            sale.setQuantitySold(count);
            updateProductQuantityOfSale(sale, count);
            sale.setOwnerId(session.getUser().getObjectId());
            sales.add(sale);
        }
    }

    //The whole setup of recording the sales needs to be revisited...This seems messed up but I'm so tired right now to deal with it
    private void updateProductQuantityOfSale(Sale sale, int justAddedSaleQuantity) {
        Product product = sale.getProduct();
        if (product != null) {
            int productQuantity = product.getQuantity();
            int saleQuantity = sale.getQuantitySold();

            if (saleQuantity <= productQuantity) {
                product.setQuantity(productQuantity - saleQuantity);
            } else {
                sale.setQuantitySold(saleQuantity - justAddedSaleQuantity + productQuantity);
                product.setQuantity(0);
            }
        }
    }

    private Sale getExistingSale(String barcode) {
        Sale sale = null;
        for (Sale existingSale : sales)
            if (existingSale.getProduct().getBarcode().equals(barcode)) {
                sale = existingSale;
                break;
            }
        return sale;
    }

    @Override
    public OperationResult saveCurrentSales(final Customer customer, double amountReceived, double discount,
                                            final double balance) throws MelanieBusinessException {
        OperationResult result = OperationResult.FAILED;
            try {
                isSaving = true;

                // TODO: Figure out a way to do this transactionally
                Payment payment = new Payment(customer, amountReceived, discount, balance);

                payment.setOwnerId(session.getUser().getObjectId());

                if (session.canConnectToCloud() && dataAccess != null) {
                    result = dataAccess.addDataItem(payment, Payment.class, new OperationCallBack<Payment>() {
                        @Override
                        public void onOperationSuccessful(Payment payment) {
                            for (Sale sale : sales) {
                                if (sale.getCustomer() == null && customer != null) {
                                    sale.setCustomer(customer);
                                }

                                if(balance >= 0){
                                    sale.setPaidFor(true);
                                }

                                SalePayment salePayment = new SalePayment(sale, payment);
                                try {
                                    dataAccess.addOrUpdateItemLocalOnly(sale.getProduct(), Product.class);
                                    dataAccess.addDataItem(salePayment, SalePayment.class, null);
                                } catch (MelanieDataLayerException e) {
                                    onOperationFailed(e);
                                }
                            }
                            sales.clear();
                            isSaving = false;
                        }
                    });
                }

            } catch (MelanieDataLayerException e) {
                throw new MelanieBusinessException(e.getMessage(), e);
            }

        return result;
    }

    public String getBarcodeNoChecksum(String barcode) {
        return barcode.substring(0, barcode.length() - 1);
    }

    @Override
    public void findSalesByCustomer(Customer customer, OperationCallBack<SalePayment> operationCallBack)
            throws MelanieBusinessException {

        if (session.canConnectToCloud()) {
            try {
                String where = LocalConstants.CUSTOMEROBJECTID + "='" + customer.getObjectId() + "' and Sale.paidFor=False";
                cloudAccess.findItemsByWhereClause(where, SalePayment.class,
                        operationCallBack);
            } catch (MelanieDataLayerException e) {
                throw new MelanieBusinessException(e.getMessage(), e);
            }
        }
    }

    @Override
    public OperationResult recordPayment(Customer customer, List<Sale> sales, double amountReceived, double discount,
                                         double balance, Map<String, Double> balancesPerSalesDate) throws MelanieBusinessException {
        OperationResult result = OperationResult.FAILED;

        validator.VerifyParamsNonNull(balancesPerSalesDate, sales);

        customer.setAmountOwed(customer.getAmountOwed() - amountReceived);

        for(Entry<String, Double> entry: balancesPerSalesDate.entrySet()){
            double balancePerDate = entry.getValue();
            String date = entry.getKey();
                this.sales.clear();
                for(Sale sale : sales){
                    if(dateFormatter.format(sale.getSaleDate()).equals(date)){
                        this.sales.add(sale);
                    }
                }

                if(amountReceived >= balancePerDate){
                    amountReceived -= balancePerDate;
                   result = saveCurrentSales(customer,balancePerDate,0,0);
                }
                else {
                    if(amountReceived > 0) {
                       result = saveCurrentSales(customer, amountReceived, 0, -(balancePerDate - amountReceived));
                        break; //there is no point moving on in this case;
                    }
                }
        }

       return result;
    }

    private void addBarcodeToNotFoundList(String barcode) {
        if (notFoundProducts.size() >= 20) {
            notFoundProducts.remove();
        }
        notFoundProducts.add(barcode);
    }

    @Override
    public List<Sale> getSalesBetween(Date fromDate, Date toDate, final OperationCallBack<Sale> operationCallBack)
            throws MelanieBusinessException {
        List<Sale> sales = new ArrayList<Sale>();
        try {
            if (session.canConnectToCloud() && dataAccess != null) {
                sales.addAll(dataAccess.findItemsBetween("SaleDate", fromDate, toDate, Sale.class, operationCallBack));
                // refreshSales(sales);
            }
        } catch (MelanieDataLayerException e) {
            throw new MelanieBusinessException(e.getMessage(), e);
        }

        return sales;
    }

    @Override
    public void clear() {
        if (sales != null && !sales.isEmpty() && !isSaving) {
            sales.clear();
        }
    }

    @Override
    public void removeFromTempList(int saleIndex) {
        if (sales != null && saleIndex < sales.size()) {
            sales.remove(saleIndex);
        }
    }

    private class LocalConstants {
        public static final String CUSTOMEROBJECTID = "Sale.Customer.ObjectId";
        public static final String DATEFORMAT = "MMM dd, yyyy";
    }

    // private void refreshSales(List<Sale> sales)
    // throws MelanieDataLayerException {
    // if (dataAccess != null)
    // for (Sale sale : sales)
    // dataAccess.refreshItem(sale.getProduct(), Product.class);
    // }
}

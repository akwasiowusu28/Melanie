package com.melanie.business;

import com.melanie.entities.Customer;
import com.melanie.entities.Payment;
import com.melanie.entities.Sale;
import com.melanie.entities.SalePayment;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Provides methods for recording sales
 *
 * @author Akwasi Owusu
 */
public interface SalesController {

    List<Sale> generateSaleItems(List<String> barcodes,
                                 OperationCallBack<Sale> salesCallBack)
            throws MelanieBusinessException;

    void findSalesByCustomer(Customer customer,
                             OperationCallBack<SalePayment> operationCallBack)
            throws MelanieBusinessException;

    OperationResult recordPayment(Customer customer, List<Sale> sale,
                                  double amount, double discount, double balance, Map<String, Payment> previousPaymentsGroup)
            throws MelanieBusinessException;

    List<Sale> getSalesBetween(Date fromDate, Date toDate,
                               OperationCallBack<Sale> operationCallBack)
            throws MelanieBusinessException;

    OperationResult saveCurrentSales(Customer customer, double amountReceived,
                                     double discount, double balance) throws MelanieBusinessException;

    void removeFromTempList(int saleIndex);

    void clear();
}

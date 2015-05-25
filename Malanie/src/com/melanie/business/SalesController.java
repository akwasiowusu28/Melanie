package com.melanie.business;

import java.util.Date;
import java.util.List;

import com.melanie.entities.Customer;
import com.melanie.entities.Sale;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

/**
 * Provides methods for recording sales
 * 
 * @author Akwasi Owusu
 * 
 */
public interface SalesController {

	List<Sale> generateSaleItems(List<String> barcodes,
			OperationCallBack<Sale> salesCallBack)
			throws MelanieBusinessException;

	List<Sale> findSalesByCustomer(Customer customer,
			OperationCallBack<Sale> operationCallBack)
			throws MelanieBusinessException;

	OperationResult recordPayment(Customer customer, List<Sale> sale,
			double amount, double discount, double balance)
			throws MelanieBusinessException;

	List<Sale> getSalesBetween(Date fromDate, Date toDate,
			OperationCallBack<Sale> operationCallBack)
			throws MelanieBusinessException;

	OperationResult saveCurrentSales(Customer customer, double amountReceived,
			double discount, double balance) throws MelanieBusinessException;

}

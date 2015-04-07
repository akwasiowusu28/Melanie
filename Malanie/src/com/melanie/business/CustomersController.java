package com.melanie.business;

import java.util.List;

import com.melanie.entities.Customer;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

/**
 * Provides methods for recording and retrieval of customer information
 * 
 * @author Akwasi Owusu
 * 
 */

public interface CustomersController {

	Customer cacheAndReturnNewCustomer(String name, String phoneNumber)
			throws MelanieBusinessException;

	OperationResult addOrUpdateCachedCustomer() throws MelanieBusinessException;

	List<Customer> getAllCustomers(
			MelanieOperationCallBack<Customer> operationCallBack)
			throws MelanieBusinessException;

	OperationResult updateCustomer(Customer customer)
			throws MelanieBusinessException;

	public Customer getCachedCustomer();

	Customer findCustomer(int customerId,
			MelanieOperationCallBack<Customer> operationCallBack)
			throws MelanieBusinessException;
}

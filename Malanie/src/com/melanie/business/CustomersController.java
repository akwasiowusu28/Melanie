package com.melanie.business;

import java.util.List;

import com.melanie.entities.Customer;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

/**
 * Provides methods for recording and retrieval of customer information
 * 
 * @author Akwasi Owusu
 * 
 */

public interface CustomersController {

	List<Customer> getAllCustomers(
			OperationCallBack<Customer> operationCallBack)
					throws MelanieBusinessException;

	OperationResult updateCustomer(Customer customer)
			throws MelanieBusinessException;

	OperationResult addCustomer(Customer customer)
			throws MelanieBusinessException;

	Customer findCustomer(int customerId,
			OperationCallBack<Customer> operationCallBack)
					throws MelanieBusinessException;

	void addOrUpdateCustomerLocalOnly(Customer customer)
			throws MelanieBusinessException;

	void getLastInsertedCustomerId(OperationCallBack<Integer> operationCallBack) throws MelanieBusinessException;
}

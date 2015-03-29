package com.melanie.business;

import java.util.List;

import com.melanie.entities.Customer;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public interface CustomersController {

	OperationResult addNewCustomer(String name, String phoneNumber)
			throws MelanieBusinessException;

	List<Customer> getAllCustomers() throws MelanieBusinessException;

	OperationResult updateCustomer(Customer customer)
			throws MelanieBusinessException;

	Customer findCustomer(int customerId) throws MelanieBusinessException;
}

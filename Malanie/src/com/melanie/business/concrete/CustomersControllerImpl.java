package com.melanie.business.concrete;

import java.util.ArrayList;
import java.util.List;

import com.melanie.business.CustomersController;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.Customer;
import com.melanie.support.MelanieDataFactory;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

/**
 * This is the concrete implementation of the Customers subsystem. Use this for
 * all operations pertaining to Customer information
 * 
 * @author Akwasi Owusu
 * 
 */
public class CustomersControllerImpl implements CustomersController {

	private MelanieDataAccessLayer dataAccess;
	private Customer customer;

	public CustomersControllerImpl() {
		dataAccess = MelanieDataFactory.makeDataAccess();
	}

	@Override
	public Customer cacheAndReturnNewCustomer(String name, String phoneNumber)
			throws MelanieBusinessException {
		customer = new Customer(name, phoneNumber);
		return customer;
	}

	/**
	 * Adds a new Customer
	 * 
	 * @param name
	 *            The customer's name
	 * @param phoneNumber
	 *            The Customer's phone number
	 * @return {@link OperationResult}
	 */
	@Override
	public OperationResult addCachedNewCustomer()
			throws MelanieBusinessException {

		OperationResult result = OperationResult.FAILED;

		if (dataAccess != null && customer != null)
			try {
				result = dataAccess.addDataItem(customer, Customer.class);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		return result;
	}

	/**
	 * gets all the Customers
	 * 
	 * @return list of all customers
	 */
	@Override
	public List<Customer> getAllCustomers() throws MelanieBusinessException {

		List<Customer> customers = new ArrayList<Customer>();
		try {
			if (dataAccess != null)
				customers = dataAccess.findAllItems(Customer.class);
		} catch (MelanieDataLayerException e) {
			throw new MelanieBusinessException(e.getMessage(), e);
		}
		return customers;
	}

	@Override
	public OperationResult updateCustomer(Customer customer)
			throws MelanieBusinessException {
		OperationResult result = OperationResult.FAILED;

		if (dataAccess != null)
			try {
				result = dataAccess.updateDataItem(customer, Customer.class);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		return result;
	}

	@Override
	public Customer findCustomer(int customerId)
			throws MelanieBusinessException {

		Customer customer = null;

		try {
			if (dataAccess != null)
				customer = dataAccess.findItemById(customerId, Customer.class);
		} catch (MelanieDataLayerException e) {
			throw new MelanieBusinessException(e.getMessage(), e);
		}

		return customer;
	}
}

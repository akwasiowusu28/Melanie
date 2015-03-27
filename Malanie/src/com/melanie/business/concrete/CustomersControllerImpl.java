package com.melanie.business.concrete;

import com.melanie.business.CustomersController;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.Customer;
import com.melanie.support.MelanieDataFactory;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

public class CustomersControllerImpl implements CustomersController {

	private MelanieDataAccessLayer dataAccess;

	public CustomersControllerImpl() {
		dataAccess = MelanieDataFactory.makeDataAccess();
	}

	@Override
	public OperationResult addNewCustomer(String name, String phoneNumber)
			throws MelanieBusinessException {

		OperationResult result = OperationResult.FAILED;

		if (dataAccess != null) {
			try {
				result = dataAccess.addDataItem(new Customer(name, phoneNumber));
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}

		}
		return result;
	}

}

package com.melanie.business;

import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public interface CustomersController {

	OperationResult addNewCustomer(String name, String phoneNumber) throws MelanieBusinessException;
}

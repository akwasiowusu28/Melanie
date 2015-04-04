package com.melanie.support;

import com.melanie.business.CustomersController;
import com.melanie.business.MelanieBusiness;
import com.melanie.business.ProductEntryController;
import com.melanie.business.SalesController;
import com.melanie.business.concrete.CustomersControllerImpl;
import com.melanie.business.concrete.MelanieBusinessImpl;
import com.melanie.business.concrete.ProductEntryControllerImpl;
import com.melanie.business.concrete.SalesControllerImpl;

public class MelanieBusinessFactory {

	public static MelanieBusiness makeMelanieBusiness() {
		return new MelanieBusinessImpl();
	}

	public static ProductEntryController makeProductEntryController() {
		return new ProductEntryControllerImpl();
	}

	public static SalesController makeSalesController() {
		return new SalesControllerImpl();
	}

	public static CustomersController makeCustomersController() {
		return new CustomersControllerImpl();
	}
}

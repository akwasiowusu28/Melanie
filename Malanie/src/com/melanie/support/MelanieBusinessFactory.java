package com.melanie.support;

import com.melanie.business.ProductEntryController;
import com.melanie.business.SalesController;
import com.melanie.business.concrete.ProductEntryControllerImpl;
import com.melanie.business.concrete.SalesControllerImpl;

public class MelanieBusinessFactory {

	public static ProductEntryController makeProductEntryController(){
		return new ProductEntryControllerImpl();
	}
	
	public static SalesController makeSalesController(){
		return new SalesControllerImpl();
	}
	
}

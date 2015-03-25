package com.melanie.business;

import java.util.List;

import com.melanie.entities.Sale;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

/**
 * Provides methods for recording sales
 * @author Akwasi Owusu
 *
 */
public interface SalesController {

	List<Sale> addSales(List<String> barcodes) throws MelanieBusinessException;
	OperationResult saveCurrentSales() throws MelanieBusinessException;
}

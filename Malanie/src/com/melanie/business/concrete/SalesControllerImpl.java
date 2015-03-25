package com.melanie.business.concrete;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.melanie.business.ProductEntryController;
import com.melanie.business.SalesController;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieDataFactory;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

public class SalesControllerImpl implements SalesController {

	private ProductEntryController productController;
	private MelanieDataAccessLayer dataAccess;
	private List<Sale> sales;

	public SalesControllerImpl() {
		productController = MelanieBusinessFactory.makeProductEntryController();
		dataAccess = MelanieDataFactory.makeDataAccess();
		sales = new ArrayList<Sale>();
	}

	@Override
	public List<Sale> addSales(List<String> barcodes)
			throws MelanieBusinessException {
		for (String barcode : barcodes) {
			// first check if the list contains a product with the same barcode
			// and just increase the quantity sold
			Sale sale = getExistingSale(barcode);
			if (sale == null)
				try {
					addNewSale(barcode.substring(0, barcode.length() - 1));
				} catch (MelanieBusinessException e) {
					throw new MelanieBusinessException(e.getMessage(), e);
				}
			else {
				int quantity = sale.getQuantitySold();
				sale.setQuantitySold(++quantity);
			}
		}
		return sales;
	}

	private void addNewSale(String barcode) throws MelanieBusinessException {
		Sale sale = new Sale();
		Product product;

		product = productController.findProductByBarcode(barcode);

		if (product != null) {
			sale.setProduct(product);
			sale.setSaleDate(new Date());
			sale.setQuantitySold(1);
			sales.add(sale);
		}
	}

	private Sale getExistingSale(String barcode) {
		Sale sale = null;
		for (Sale existingSale : sales) {
			if (existingSale.getProduct().getBarcode().equals(barcode)) {
				sale = existingSale;
				break;
			}
		}
		return sale;
	}

	@Override
	public OperationResult saveCurrentSales() throws MelanieBusinessException {
		OperationResult result = OperationResult.FAILED;
		if (dataAccess != null) {
			for (Sale sale : sales) {
				try {
					dataAccess.addDataItem(sale);
				} catch (MelanieDataLayerException e) {
					throw new MelanieBusinessException(e.getMessage(), e);
				}
			}
			result = OperationResult.SUCCESSFUL;
		}

		return result;
	}

}

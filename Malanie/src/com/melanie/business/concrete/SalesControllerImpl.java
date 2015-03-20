package com.melanie.business.concrete;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.melanie.business.ProductEntryController;
import com.melanie.business.SalesController;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;
import com.melanie.support.exceptions.MelanieBusinessException;

public class SalesControllerImpl implements SalesController{

	private ProductEntryController productController;
	private List<Sale> sales;
	
	public SalesControllerImpl(){
		productController = new ProductEntryControllerImpl();
		sales = new ArrayList<Sale>();
	}
	
	@Override
	public List<Sale> addSales(List<String> barcodes) throws MelanieBusinessException{
		for (String barcode : barcodes) {
			// first check if the list contains a product with the same barcode
			// and just increase the quantity sold
			Sale sale = getExistingSale(barcode);
			if (sale == null)
				addNewSale(barcode);
			else {
				int quantity = sale.getQuantitySold();
				sale.setQuantitySold(++quantity);
			}
		}
		return sales;
	}

	private void addNewSale(String barcode) {
		Sale sale = new Sale();
		Product product = productController.findProductByBarcode(barcode);
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
			if (existingSale.getProduct().getBarcodeNumber().equals(barcode)) {
				sale = existingSale;
				break;
			}
		}
		return sale;
	}
}

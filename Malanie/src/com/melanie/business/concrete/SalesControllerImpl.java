package com.melanie.business.concrete;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.melanie.business.ProductEntryController;
import com.melanie.business.SalesController;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.exceptions.MelanieBusinessException;

public class SalesControllerImpl implements SalesController{

	private ProductEntryController productController;
	private List<Sale> sales;
	
	public SalesControllerImpl(){
		productController = MelanieBusinessFactory.makeProductEntryController();
		sales = new ArrayList<Sale>();
	}
	
	@Override
	public List<Sale> addSales(List<String> barcodes) throws MelanieBusinessException{
		for (String barcode : barcodes) {
			// first check if the list contains a product with the same barcode
			// and just increase the quantity sold
			Sale sale = getExistingSale(barcode);
			if (sale == null)
				addNewSale(barcode.substring(0, barcode.length()-1));
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
			if (existingSale.getProduct().getBarcode().equals(barcode)) {
				sale = existingSale;
				break;
			}
		}
		return sale;
	}
	
	//@SuppressWarnings("serial")
//	private List<Sale> stub(){
//		return new ArrayList<Sale>(){{
//			for(int i=0; i<6; i++){
//				Sale sale = new Sale();
//				sale.setProduct(new Product("Shoe" + i, i*2, i*3, new Category()));
//				sale.setQuantitySold(i*4);
//				add(sale);
//			}
//		}};
//	}
}

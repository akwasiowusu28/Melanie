package com.melanie.business.concrete;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.melanie.business.ProductEntryController;
import com.melanie.business.SalesController;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.Customer;
import com.melanie.entities.Payment;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieDataFactory;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

public class SalesControllerImpl implements SalesController {

	private static final String CUSTOMER = "CustomerId";

	private ProductEntryController productController;
	private MelanieDataAccessLayer dataAccess;
	private List<Sale> sales;

	public SalesControllerImpl() {
		productController = MelanieBusinessFactory.makeProductEntryController();
		dataAccess = MelanieDataFactory.makeDataAccess();
		sales = new ArrayList<Sale>();
	}

	@Override
	public List<Sale> generateSaleItems(List<String> barcodes)
			throws MelanieBusinessException {
		for (String barcode : barcodes) {
			// first check if the list contains a product with the same barcode
			// and just increase the quantity sold
			Sale sale = getExistingSale(barcode);
			if (sale == null)
				try {
					addNewSale(parseBarcodeNoChecksum(barcode));
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
		String barcodeNoChecksum = parseBarcodeNoChecksum(barcode);
		for (Sale existingSale : sales)
			if (existingSale.getProduct().getBarcode()
					.equals(barcodeNoChecksum)) {
				sale = existingSale;
				break;
			}
		return sale;
	}

	@Override
	public OperationResult saveCurrentSales(Customer customer)
			throws MelanieBusinessException {
		OperationResult result = OperationResult.FAILED;
		if (dataAccess != null) {
			for (Sale sale : sales)
				try {
					sale.setCustomer(customer);
					dataAccess.addDataItem(sale);
				} catch (MelanieDataLayerException e) {
					throw new MelanieBusinessException(e.getMessage(), e);
				}
			result = OperationResult.SUCCESSFUL;
		}

		return result;
	}

	private String parseBarcodeNoChecksum(String barcode) {
		return barcode.substring(0, barcode.length() - 1);
	}

	@Override
	public List<Sale> findSalesByCustomer(Customer customer)
			throws MelanieBusinessException {

		List<Sale> customerSales = new ArrayList<Sale>();
		if (dataAccess != null)
			try {
				customerSales = dataAccess.findItemsByFieldName(CUSTOMER,
						String.valueOf(customer.getId()), Sale.class);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		return customerSales;
	}

	@Override
	public OperationResult recordPayment(Customer customer, List<Sale> sale,
			double amountReceived, double discount, double balance)
			throws MelanieBusinessException {
		OperationResult result = OperationResult.FAILED;
		try {
			Payment payment = new Payment(customer, sales, amountReceived,
					discount, balance);
			if (dataAccess != null)
				result = dataAccess.addDataItem(payment);
		} catch (MelanieDataLayerException e) {
			throw new MelanieBusinessException(e.getMessage(), e);
		}
		return result;
	}
	// @SuppressWarnings("serial")
	// private List<Sale> stub(){
	// return new ArrayList<Sale>(){{
	// for(int i=0; i<6; i++){
	// Sale sale = new Sale();
	// sale.setProduct(new Product("Shoe" + i, i*2, i*3, new Category(), null));
	// sale.setQuantitySold(i*4);
	// add(sale);
	// }
	// }};
	// }
}

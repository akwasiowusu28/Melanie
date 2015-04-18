package com.melanie.business.concrete;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.melanie.androidactivities.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.business.SalesController;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.Customer;
import com.melanie.entities.Payment;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieDataFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

public class SalesControllerImpl implements SalesController {

	private static final String CUSTOMEROBJECTID = "Customer.ObjectId";
	private static final String ADDNEWSALE = "addNewSale";

	private ProductEntryController productController;
	private MelanieDataAccessLayer dataAccess;
	private List<Sale> sales;
	private Payment payment;
	private Queue<String> notFoundProducts;
	private int operationCount = 0;

	public SalesControllerImpl() {
		productController = MelanieBusinessFactory.makeProductEntryController();
		dataAccess = MelanieDataFactory.makeDataAccess();
		sales = new ArrayList<Sale>();
		notFoundProducts = new LinkedList<String>();
	}

	@Override
	public List<Sale> generateSaleItems(List<String> barcodes,
			MelanieOperationCallBack<Sale> uiCallBack)
			throws MelanieBusinessException {
		operationCount = 0;
		Map<String, Integer> itemGroup = Utils.groupItems(barcodes);
		for (String barcode : itemGroup.keySet())
			try {
				String parsedBarcode = parseBarcodeNoChecksum(barcode);
				int quantity = itemGroup.get(barcode);
				Sale sale = getExistingSale(parsedBarcode);
				if (sale != null)
					sale.setQuantitySold(sale.getQuantitySold() + quantity);
				else if (!notFoundProducts.contains(parsedBarcode))
					addNewSale(parsedBarcode, quantity, itemGroup.keySet()
							.size(), uiCallBack);
			} catch (MelanieBusinessException e) {
				throw new MelanieBusinessException(e.getMessage(), e); // TODO:
																		// log
																		// it
			}
		return sales;

	}

	private void addNewSale(final String barcode, final int count,
			final int total, final MelanieOperationCallBack<Sale> uiCallBack)
			throws MelanieBusinessException {
		Product product;

		product = productController.findProductByBarcode(barcode,
				new MelanieOperationCallBack<Product>(this.getClass()
						.getSimpleName() + ADDNEWSALE) {

					@Override
					public void onOperationSuccessful(Product result) {
						if (result == null)
							addBarcodeToNotFoundList(barcode);
						operationCount++;
						addProductToSale(result, count);
						if (uiCallBack != null && operationCount == total) {
							uiCallBack.onCollectionOperationSuccessful(sales);
							operationCount = 0;
						}
					}
				});

		addProductToSale(product, count);
	}

	private void addProductToSale(Product product, int count) {
		if (product != null) {
			Sale sale = new Sale();
			sale.setProduct(product);
			sale.setSaleDate(new Date());
			sale.setQuantitySold(count);
			sales.add(sale);
		}
	}

	private Sale getExistingSale(String barcode) {
		Sale sale = null;
		for (Sale existingSale : sales)
			if (existingSale.getProduct().getBarcode().equals(barcode)) {
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
			try {
				for (Sale sale : sales) {
					sale.setPayment(payment);
					sale.setCustomer(customer);
					dataAccess.addDataItem(sale, Sale.class);
				}
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
	public List<Sale> findSalesByCustomer(Customer customer,
			MelanieOperationCallBack<Sale> operationCallBack)
			throws MelanieBusinessException {

		List<Sale> customerSales = new ArrayList<Sale>();
		if (dataAccess != null)
			try {
				customerSales = dataAccess.findItemsByFieldName(
						CUSTOMEROBJECTID,
						String.valueOf(customer.getObjectId()), Sale.class,
						operationCallBack);
				for (Sale sale : customerSales) {
					dataAccess.refreshItem(sale.getProduct(), Product.class);
					dataAccess.refreshItem(sale.getCustomer(), Customer.class);
					dataAccess.refreshItem(sale.getPayment(), Payment.class);
				}
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
			payment = new Payment(customer, sales, amountReceived, discount,
					balance);
			if (dataAccess != null)
				result = dataAccess.addDataItem(payment, Payment.class);
		} catch (MelanieDataLayerException e) {
			throw new MelanieBusinessException(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public void createNewPayment(Customer customer, List<Sale> sale,
			double amountReceived, double discount, double balance) {
		payment = new Payment(customer, sales, amountReceived, discount,
				balance);
	}

	private void addBarcodeToNotFoundList(String barcode) {
		if (notFoundProducts.size() >= 20)
			notFoundProducts.remove();
		notFoundProducts.add(barcode);
	}

	@Override
	public List<Sale> getSalesBetween(Date fromDate, Date toDate,
			MelanieOperationCallBack<Sale> operationCallBack)
			throws MelanieBusinessException {
		List<Sale> sales = new ArrayList<Sale>();
		try {
			if (dataAccess != null)
				sales.addAll(dataAccess.findItemsBetween("SaleDate",
						fromDate.toString(), toDate.toString(), Sale.class,
						operationCallBack));
		} catch (MelanieDataLayerException e) {
			throw new MelanieBusinessException(e.getMessage(), e);
		}
		return sales;
	}
}

package com.melanie.business.concrete;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TimeZone;

import com.melanie.androidactivities.support.Utils;
import com.melanie.business.ProductEntryController;
import com.melanie.business.SalesController;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.Customer;
import com.melanie.entities.Payment;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;
import com.melanie.entities.SalePayment;
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
		Set<String> groupedBarcodeSet = itemGroup.keySet();
		int totalItems = groupedBarcodeSet.size();

		for (String barcode : groupedBarcodeSet)
			try {
				String parsedBarcode = parseBarcodeNoChecksum(barcode);
				int quantity = itemGroup.get(barcode);
				Sale sale = getExistingSale(parsedBarcode);
				if (sale != null)
					sale.setQuantitySold(sale.getQuantitySold() + quantity);
				else if (!notFoundProducts.contains(parsedBarcode))
					addNewSale(parsedBarcode, quantity, totalItems, uiCallBack);
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
				new MelanieOperationCallBack<Product>() {

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
			sale.setSaleDate(Calendar.getInstance(TimeZone.getDefault())
					.getTime());
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
	public OperationResult saveCurrentSales(Customer customer,
			double amountReceived, double discount, double balance)
			throws MelanieBusinessException {
		OperationResult result = OperationResult.FAILED;
		if (dataAccess != null) {
			try {

				// TODO: Figure out a way to do this transactionally
				Payment payment = new Payment(customer, amountReceived,
						discount, balance);

				dataAccess.addDataItem(payment, Payment.class,
						new MelanieOperationCallBack<Payment>() {
							@Override
							public void onOperationSuccessful(Payment payment) {
								for (Sale sale : sales) {
									SalePayment salePayment = new SalePayment(
											sale, payment);
									try {
										dataAccess.addDataItem(salePayment,
												SalePayment.class, null);
									} catch (MelanieDataLayerException e) {
										onOperationFailed(e);
									}
								}
								sales.clear();

							}
						});

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
				}
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		return customerSales;
	}

	@Override
	public OperationResult recordPayment(Customer customer, List<Sale> sales,
			double amountReceived, double discount, double balance)
			throws MelanieBusinessException {
		this.sales = new ArrayList<Sale>(sales);
		return saveCurrentSales(customer, amountReceived, discount, balance);
	}

	private void addBarcodeToNotFoundList(String barcode) {
		if (notFoundProducts.size() >= 20)
			notFoundProducts.remove();
		notFoundProducts.add(barcode);
	}

	@Override
	public List<Sale> getSalesBetween(Date fromDate, Date toDate,
			final MelanieOperationCallBack<Sale> operationCallBack)
			throws MelanieBusinessException {
		List<Sale> sales = new ArrayList<Sale>();
		try {
			if (dataAccess != null)
				sales.addAll(dataAccess.findItemsBetween("SaleDate", fromDate,
						toDate, Sale.class, operationCallBack));
			// refreshSales(sales);
		} catch (MelanieDataLayerException e) {
			throw new MelanieBusinessException(e.getMessage(), e);
		}

		return sales;
	}

	// private void refreshSales(List<Sale> sales)
	// throws MelanieDataLayerException {
	// if (dataAccess != null)
	// for (Sale sale : sales)
	// dataAccess.refreshItem(sale.getProduct(), Product.class);
	// }
}

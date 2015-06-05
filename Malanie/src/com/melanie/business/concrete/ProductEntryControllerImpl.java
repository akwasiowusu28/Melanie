package com.melanie.business.concrete;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.melanie.business.MelanieSession;
import com.melanie.business.ProductEntryController;
import com.melanie.dataaccesslayer.DataAccessLayer;
import com.melanie.entities.Category;
import com.melanie.entities.CostEntry;
import com.melanie.entities.CostItem;
import com.melanie.entities.Product;
import com.melanie.support.BusinessFactory;
import com.melanie.support.DataFactory;
import com.melanie.support.MelanieArgumentValidator;
import com.melanie.support.MelanieArgumentValidatorImpl;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.SupportFactory;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

/**
 * This class is the main business controller for the Products subsystem. It
 * provides methods for all operations regarding products and product categories
 * 
 * @author Akwasi Owusu
 * 
 */
public class ProductEntryControllerImpl implements ProductEntryController {

	private class LocalConstants {
		public static final String CATEGORYNAME = "CategoryName";
		public static final String PRODUCTNAME = "CategoryName";
		public static final String BARCODE = "Barcode";
	}

	private final MelanieArgumentValidator argumentValidator;
	private final DataAccessLayer dataAccess;

	private final MelanieSession session;

	public ProductEntryControllerImpl() {
		argumentValidator = SupportFactory.makeValidator();
		dataAccess = DataFactory.makeDataAccess();
		session = BusinessFactory.getSession();
	}

	/**
	 * Use this to add a product category
	 * 
	 * @param categoryName
	 *            the name of the specified category
	 * @throws MelanieBusinessException
	 */
	@Override
	public Category addCategory(String categoryName) throws MelanieBusinessException {
		argumentValidator.VerifyNotEmptyString(categoryName);
		Category category = null;
		if (session.canConnectToCloud() && dataAccess != null) {
			category = new Category(categoryName);
			category.setOwnerId(session.getUser().getObjectId());
			OperationResult result;
			try {
				result = dataAccess.addDataItem(category, Category.class, null);
				if (result != OperationResult.SUCCESSFUL) {
					category = null;
				}
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage());
			}

		}
		return category;
	}

	/**
	 * Use this to find a product category by Id
	 * 
	 * @param id
	 *            the name of the specified category
	 * @return Category the found category or null
	 */
	@Override
	public Category findCategory(int id, OperationCallBack<Category> operationCallBack) throws MelanieBusinessException {
		Category category = null;
		if (session.canConnectToCloud() && dataAccess != null) {
			try {
				category = dataAccess.findItemById(id, Category.class, operationCallBack);

			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
		return category;
	}

	/**
	 * Use this to add a product category by name
	 * 
	 * @param categoryName
	 *            the name of the specified category
	 * @return Category the found category or null
	 * @throws MelanieBusinessException
	 *             , MelanieArgumentException
	 */

	@Override
	public Category findCategory(String categoryName, OperationCallBack<Category> operationCallBack)
			throws MelanieBusinessException {
		new MelanieArgumentValidatorImpl().VerifyNotEmptyString(categoryName);
		Category category = null;
		if (session.canConnectToCloud() && dataAccess != null) {
			try {
				category = dataAccess.findItemByFieldName(LocalConstants.CATEGORYNAME, categoryName, Category.class,
						operationCallBack);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
		return category;
	}

	/**
	 * Use this to find all product categories
	 * 
	 * @return All product categories
	 * @throws MelanieBusinessException
	 */
	@Override
	public List<Category> getAllCategories(OperationCallBack<Category> operationCallBack)
			throws MelanieBusinessException {
		List<Category> allCategories = new ArrayList<Category>();
		if (session.canConnectToCloud() && dataAccess != null) {
			try {
				allCategories.addAll(getAllItemsOf(Category.class, operationCallBack));
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
		return allCategories;
	}

	/**
	 * Use this to add a product
	 * 
	 * @param productName
	 *            the specified name of the product
	 * @param quantity
	 *            the quantity of the product
	 * @param price
	 *            the price of the product
	 * @param category
	 *            the selected category of the product
	 * @throws MelanieBusinessException
	 */
	@Override
	public OperationResult addProduct(String productName, int quantity, double price, Category category, String barcode)
			throws MelanieBusinessException {
		OperationResult result = OperationResult.FAILED;
		argumentValidator.VerifyNonNull(category);
		argumentValidator.VerifyNotEmptyString(productName);

		if (session.canConnectToCloud() && dataAccess != null) {
			Product product = new Product(productName, quantity, price, category, barcode);
			product.setOwnerId(session.getUser().getObjectId());
			try {
				result = dataAccess.addDataItem(product, Product.class, null); //fire and forget. No callBack. In the future, this has to change
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
		return result;
	}

	/**
	 * Returns a list of all products
	 */
	@Override
	public List<Product> findAllProducts(OperationCallBack<Product> operationCallBack) throws MelanieBusinessException {

		List<Product> allProducts = new ArrayList<Product>();
		if (session.canConnectToCloud() && dataAccess != null) {
			try {
				allProducts.addAll(getAllItemsOf(Product.class, operationCallBack));
				for (Product product : allProducts) {
					dataAccess.refreshItem(product.getCategory(), Category.class);
				}
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
		return allProducts;
	}

	/**
	 * Use this to remove a product by id
	 * 
	 * @param productId
	 *            the id of the product to delete
	 * @throws MelanieBusinessException
	 */
	@Override
	public OperationResult removeProduct(int productId) throws MelanieBusinessException {
		try {
			findProduct(productId, new OperationCallBack<Product>() {

				@Override
				public void onOperationSuccessful(Product result) {
					try {
						if (session.canConnectToCloud() && dataAccess != null) {
							dataAccess.deleteDataItem(result, Product.class);
						}
					} catch (MelanieDataLayerException e) {
						e.printStackTrace(); // TODO: log it
					}
				}

			});
		} finally {
		}
		return OperationResult.SUCCESSFUL;
	}

	/**
	 * Use this to find a product by id
	 * 
	 * @param productId
	 *            the id of the product to find
	 * @throws MelanieBusinessException
	 */
	@Override
	public Product findProduct(int productId, OperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException {
		Product product = null;
		if (session.canConnectToCloud() && dataAccess != null) {
			try {
				product = dataAccess.findItemById(productId, Product.class, operationCallBack);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
		return product;
	}

	/**
	 * Use this to remove a product by name
	 * 
	 * @param productName
	 *            the name of the product to find
	 * @throws MelanieBusinessException
	 */
	@Override
	public Product findProduct(String productName, OperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException {
		Product product = null;
		if (session.canConnectToCloud() && dataAccess != null) {
			try {
				product = dataAccess.findItemByFieldName(LocalConstants.PRODUCTNAME, productName, Product.class,
						operationCallBack);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
		return product;
	}

	/**
	 * Use this to update the quantity of a product
	 * 
	 * @param productName
	 *            the name of the product to update
	 * @throws MelanieBusinessException
	 */
	@Override
	public OperationResult updateProduct(Product product) throws MelanieBusinessException {
		OperationResult result = OperationResult.FAILED;
		if (product != null) {
			if (session.canConnectToCloud() && dataAccess != null) {
				try {
					result = dataAccess.updateDataItem(product, Product.class);

				} catch (MelanieDataLayerException e) {
					throw new MelanieBusinessException(e.getMessage(), e);
				}
			}
		}
		return result;
	}

	/**
	 * Use this to get the id of the last inserted product
	 * 
	 * @throws MelanieBusinessException
	 */
	@Override
	public void getLastInsertedProductId(OperationCallBack<Integer> operationCallBack) throws MelanieBusinessException {

		if (session.canConnectToCloud() && dataAccess != null) {
			try {
				dataAccess.getLastInsertedId(Product.class, operationCallBack);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Use this to get find a product by its barcode
	 * 
	 * @param barcodDigits
	 *            the barcode to search for
	 * @return the found product or null
	 * @throws MelanieBusinessException
	 */
	@Override
	public Product findProductByBarcode(String barcode, OperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException {
		Product product = null;
		argumentValidator.VerifyNotEmptyString(barcode);
		try {
			if (session.canConnectToCloud() && dataAccess != null) {

				product = dataAccess.findItemByFieldName(LocalConstants.BARCODE, barcode, Product.class,
						operationCallBack);

			}
		} catch (MelanieDataLayerException e) {
			throw new MelanieBusinessException(e.getMessage(), e);
		}
		return product;
	}

	/**
	 * Save cost entries
	 * 
	 * @param costEntries
	 *            the cost entries to save
	 * @throws MelanieBusinessException
	 */
	@Override
	public OperationResult saveCostEntries(List<CostEntry> costEntries) throws MelanieBusinessException {
		OperationResult result = OperationResult.FAILED;

		try {
			if (session.canConnectToCloud() && dataAccess != null) {
				for (CostEntry costEntry : costEntries) {
					if (costEntry.getValue() != 0D) {
						performSaveCostEntry(costEntry);
					}
				}
				result = OperationResult.SUCCESSFUL;
			}
		} catch (MelanieDataLayerException e) {

		}
		return result;
	}

	private void performSaveCostEntry(final CostEntry costEntry) throws MelanieDataLayerException {
		final OperationCallBack<CostEntry> operationCallBack = new OperationCallBack<>(); // this is for logging

		costEntry.setEntryDate(Calendar.getInstance().getTime());

		if (dataAccess != null) {
			if (dataAccess.itemExists(costEntry.getCostItem(), CostItem.class)) {

				dataAccess.addDataItem(costEntry, CostEntry.class, operationCallBack);

			} else {
				dataAccess.addDataItem(costEntry.getCostItem(), CostItem.class, new OperationCallBack<CostItem>() {

					@Override
					public void onOperationSuccessful(CostItem result) {
						costEntry.setCostItem(result);
						try {
							dataAccess.addDataItem(costEntry, CostEntry.class, operationCallBack);
						} catch (MelanieDataLayerException e) {
							operationCallBack.onOperationFailed(e);
						}
					}

				});
			}
		}
	}

	@Override
	public List<CostEntry> getAllCostEntries(OperationCallBack<CostEntry> operationCallBack)
			throws MelanieBusinessException {
		List<CostEntry> allCostEntries = new ArrayList<CostEntry>();
		try {
			allCostEntries.addAll(getAllItemsOf(CostEntry.class, operationCallBack));
		} catch (MelanieDataLayerException e) {
			throw new MelanieBusinessException(e.getMessage(), e);
		}
		return allCostEntries;
	}

	@Override
	public List<CostItem> getAllCostItems(OperationCallBack<CostItem> operationCallBack)
			throws MelanieBusinessException {
		List<CostItem> allCostItems = new ArrayList<CostItem>();
		try {
			allCostItems.addAll(getAllItemsOf(CostItem.class, operationCallBack));
		} catch (MelanieDataLayerException e) {
			throw new MelanieBusinessException(e.getMessage(), e);
		}
		return allCostItems;
	}

	private <T> List<T> getAllItemsOf(Class<T> itemClass, OperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException {
		List<T> items = new ArrayList<T>();

		if (session.canConnectToCloud() && dataAccess != null) {

			items.addAll(dataAccess.findAllItems(itemClass, operationCallBack));
		}
		return items;
	}
}

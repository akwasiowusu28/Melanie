package com.melanie.business.concrete;

import java.util.ArrayList;
import java.util.List;

import com.melanie.business.ProductEntryController;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.Category;
import com.melanie.entities.Product;
import com.melanie.support.MelanieArgumentValidator;
import com.melanie.support.MelanieArgumentValidatorImpl;
import com.melanie.support.MelanieDataFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.MelanieSupportFactory;
import com.melanie.support.OperationResult;
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

	private MelanieArgumentValidator argumentValidator;
	private MelanieDataAccessLayer dataAccess;

	public ProductEntryControllerImpl() {
		argumentValidator = MelanieSupportFactory.makeValidator();
		dataAccess = MelanieDataFactory.makeDataAccess();
	}

	/**
	 * Use this to add a product category
	 * 
	 * @param categoryName
	 *            the name of the specified category
	 * @throws MelanieBusinessException
	 */
	@Override
	public Category addCategory(String categoryName)
			throws MelanieBusinessException {
		argumentValidator.VerifyNotEmptyString(categoryName);
		Category category = null;
		if (dataAccess != null) {
			category = new Category(categoryName);
			OperationResult result;
			try {
				result = dataAccess.addDataItem(category, Category.class);
				if (result != OperationResult.SUCCESSFUL)
					category = null;
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
	public Category findCategory(int id,
			MelanieOperationCallBack<Category> operationCallBack)
			throws MelanieBusinessException {
		Category category = null;
		if (dataAccess != null)
			try {
				category = dataAccess.findItemById(id, Category.class,
						operationCallBack);

			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
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
	public Category findCategory(String categoryName,
			MelanieOperationCallBack<Category> operationCallBack)
			throws MelanieBusinessException {
		new MelanieArgumentValidatorImpl().VerifyNotEmptyString(categoryName);
		Category category = null;
		if (dataAccess != null)
			try {
				category = dataAccess.findItemByFieldName(
						LocalConstants.CATEGORYNAME, categoryName,
						Category.class, operationCallBack);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
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
	public List<Category> getAllCategories(
			MelanieOperationCallBack<Category> operationCallBack)
			throws MelanieBusinessException {
		List<Category> allCategories = new ArrayList<Category>();
		if (dataAccess != null)
			try {
				allCategories = dataAccess.findAllItems(Category.class,
						operationCallBack);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
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
	public OperationResult addProduct(String productName, int quantity,
			double price, Category category, String barcode)
			throws MelanieBusinessException {
		OperationResult result = OperationResult.FAILED;
		argumentValidator.VerifyNonNull(category);
		argumentValidator.VerifyNotEmptyString(productName);

		if (dataAccess != null) {
			Product product = new Product(productName, quantity, price,
					category, barcode);
			try {
				result = dataAccess.addDataItem(product, Product.class);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
		return result;
	}

	/**
	 * Use this to remove a product by id
	 * 
	 * @param productId
	 *            the id of the product to delete
	 * @throws MelanieBusinessException
	 */
	@Override
	public OperationResult removeProduct(int productId)
			throws MelanieBusinessException {
		try {
			findProduct(productId, new MelanieOperationCallBack<Product>(this
					.getClass().getSimpleName()) {

				@Override
				public void onOperationSuccessful(Product result) {
					try {
						dataAccess.deleteDataItem(result, Product.class);
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
	public Product findProduct(int productId,
			MelanieOperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException {
		Product product = null;
		if (dataAccess != null)
			try {
				product = dataAccess.findItemById(productId, Product.class,
						operationCallBack);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
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
	public Product findProduct(String productName,
			MelanieOperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException {
		Product product = null;
		if (dataAccess != null)
			try {
				product = dataAccess.findItemByFieldName(
						LocalConstants.PRODUCTNAME, productName, Product.class,
						operationCallBack);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		return product;
	}

	/**
	 * Use this to update the quantity of a product
	 * 
	 * @param productName
	 *            the name of the product to update
	 * @param updateQuantity
	 *            the new quantity of the product
	 * @throws MelanieBusinessException
	 */
	@Override
	public OperationResult updateProductQuantity(Product product,
			int updateQuantity) throws MelanieBusinessException {
		OperationResult result = OperationResult.FAILED;
		if (product != null) {
			product.setQuantity(updateQuantity);
			if (dataAccess != null)
				try {
					result = dataAccess.updateDataItem(product, Product.class);

				} catch (MelanieDataLayerException e) {
					throw new MelanieBusinessException(e.getMessage(), e);
				}
		}
		return result;
	}

	/**
	 * Use this to get the id of the last inserted product
	 * 
	 * @return the id of the last product or 1 if it's the first product
	 * @throws MelanieBusinessException
	 */
	@Override
	public int getLastInsertedProductId() throws MelanieBusinessException {
		int id = 0;
		if (dataAccess != null)
			try {
				id = dataAccess.getLastInsertedId(Product.class);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		return id;
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
	public Product findProductByBarcode(String barcode,
			MelanieOperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException {
		Product product = null;
		argumentValidator.VerifyNotEmptyString(barcode);
		try {
			product = dataAccess.findItemByFieldName(LocalConstants.BARCODE,
					barcode, Product.class, operationCallBack);
		} catch (MelanieDataLayerException e) {
			throw new MelanieBusinessException(e.getMessage(), e);
		}
		return product;
	}
}

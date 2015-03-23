package com.melanie.business.concrete;

import java.util.ArrayList;
import java.util.List;

import com.melanie.business.ProductEntryController;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.Category;
import com.melanie.entities.Product;
import com.melanie.support.MelanieArgumentValidator;
import com.melanie.support.MelanieDataFactory;
import com.melanie.support.exceptions.MelanieArgumentException;

/**
 * This class is the main business controller for the Products subsystem. It
 * provides methods for all operations regarding products and product categories
 * 
 * @author Akwasi Owusu
 * 
 */
public class ProductEntryControllerImpl implements ProductEntryController {

	private class LocalConstants {
		public static final String EMPTY_PRODUCT_NAME_MSG = "Product name cannot be empty";
		public static final String EMPTY_CATEGORY_NAME_MSG = "Category name cannot be empty";
		public static final String CATEGORYNAME = "CategoryName";
		public static final String PRODUCTNAME = "CategoryName";
		public static final String BARCODE_NUMBER = "barcode";
	}

	private MelanieArgumentValidator argumentValidator;
	private MelanieDataAccessLayer dataAccess;

	public ProductEntryControllerImpl() {
		argumentValidator = new MelanieArgumentValidator();
		dataAccess = MelanieDataFactory.makeDataAccessLayer();
	}

	/**
	 * Use this to add a product category
	 * 
	 * @param categoryName
	 *            the name of the specified category
	 */
	@Override
	public Category addCategory(String categoryName)
			throws MelanieArgumentException {
		argumentValidator.VerifyNotEmptyString(categoryName,
				LocalConstants.EMPTY_CATEGORY_NAME_MSG);
		Category category = null;
		if (dataAccess != null)
		{
			category = new Category(categoryName);
			dataAccess.addDataItem(category);
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
	public Category findCategory(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Use this to add a product category by name
	 * 
	 * @param categoryName
	 *            the name of the specified category
	 * @return Category the found category or null
	 */

	@Override
	public Category findCategory(String categoryName) {
		Category category = null;
		if (dataAccess != null)
			category = dataAccess.findItemByFieldName(
					LocalConstants.CATEGORYNAME, categoryName, Category.class);
		return category;
	}

	/**
	 * Use this to find all product categories
	 * 
	 * @return All product categories
	 */
	@Override
	public List<Category> getAllCategories() {
		List<Category> allCategories = new ArrayList<Category>();
		if (dataAccess != null)
			allCategories = dataAccess.findAllItems(Category.class);
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
	 */
	@Override
	public void addProduct(String productName, int quantity, double price,
			Category category, String barcode) throws MelanieArgumentException {

		argumentValidator.VerifyNonNull(category);
		argumentValidator.VerifyNotEmptyString(productName,
				LocalConstants.EMPTY_PRODUCT_NAME_MSG);

		if (dataAccess != null) {
			Product product = new Product(productName, quantity, price,
					category, barcode);
			dataAccess.addDataItem(product);
		}

	}

	/**
	 * Use this to remove a product by id
	 * 
	 * @param productId
	 *            the id of the product to delete
	 */
	@Override
	public void removeProduct(int productId) {
		dataAccess.deleteDataItem(findProduct(productId));
	}

	/**
	 * Use this to find a product by id
	 * 
	 * @param productId
	 *            the id of the product to find
	 */
	@Override
	public Product findProduct(int productId) {
		Product product = null;
		if (dataAccess != null)
			product = dataAccess.findItemById(productId, Product.class);
		return product;
	}

	/**
	 * Use this to remove a product by name
	 * 
	 * @param productName
	 *            the name of the product to find
	 */
	@Override
	public Product findProduct(String productName) {
		Product product = null;
		if (dataAccess != null)
			product = dataAccess.findItemByFieldName(
					LocalConstants.PRODUCTNAME, productName, Product.class);
		return product;
	}

	/**
	 * Use this to update the quantity of a product
	 * 
	 * @param productName
	 *            the name of the product to update
	 * @param updateQuantity
	 *            the new quantity of the product
	 */
	@Override
	public void updateProductQuantity(String productName, int updateQuantity) {
		// TODO Auto-generated method stub
	}

	/**
	 * Use this to get the id of the last inserted product
	 * 
	 * @return the id of the last product or 1 if it's the first product
	 */
	@Override
	public int getLastInsertedProductId() {
		int id = 0;
		if(dataAccess != null)
		id= dataAccess.getLastInsertedId(Product.class);
		return id;
	}

	/**
	 * Use this to get find a product by its barcode
	 * 
	 * @param barcodDigits
	 *            the barcode to search for
	 * @return the found product or null
	 */
	@Override
	public Product findProductByBarcode(String barcodDigits) {
		return dataAccess.findItemByFieldName(LocalConstants.BARCODE_NUMBER,
				barcodDigits, Product.class);
	}
}

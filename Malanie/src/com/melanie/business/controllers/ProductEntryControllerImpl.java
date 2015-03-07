package com.melanie.business.controllers;

import java.util.List;

import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.dataaccesslayer.MelanieDataAccessLayerImpl;
import com.melanie.entities.ProductCategory;
import com.melanie.entities.Product;
import com.melanie.support.MelanieArgumentValidator;
import com.melanie.support.exceptions.MelanieArgumentException;

/**
 * 
 * @author Akwasi Owusu This class is the main business controller for the
 *         Products subsystem. It provides methods for all operations regarding
 *         products and product categories
 * 
 */
public class ProductEntryControllerImpl implements ProductEntryController {

	private class LocalConstants {
		public static final String EMPTY_PRODUCT_NAME_MSG = "Product name cannot be empty";
		public static final String EMPTY_CATEGORY_NAME_MSG = "Category name cannot be empty";
		public static final String CATEGORYNAME = "CategoryName";
	}

	private MelanieArgumentValidator argumentValidator;
	private MelanieDataAccessLayer dataAccess;

	public ProductEntryControllerImpl() {
		argumentValidator = new MelanieArgumentValidator();
		dataAccess = new MelanieDataAccessLayerImpl();
	}

	/**
	 * Use this to add a product category
	 * 
	 * @param categoryName
	 *            the name of the specified category
	 */
	@Override
	public void addCategory(String categoryName)
			throws MelanieArgumentException {
		argumentValidator.VerifyNotEmptyString(categoryName,
				LocalConstants.EMPTY_CATEGORY_NAME_MSG);
		dataAccess.addDataItem(new ProductCategory(categoryName));
	}

	/**
	 * Use this to find a product category by Id
	 * 
	 * @param id
	 *            the name of the specified category
	 * @return Category the found category or null
	 */
	@Override
	public ProductCategory findCategory(int id) {
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
	public ProductCategory findCategory(String categoryName) {
		return dataAccess.findItemByFieldName(LocalConstants.CATEGORYNAME,
				categoryName, ProductCategory.class);
	}

	/**
	 * Use this to find all product categories
	 * 
	 * @return All product categories
	 */
	@Override
	public List<ProductCategory> getAllCategories() {
		return dataAccess.findAllItems(ProductCategory.class);
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
	 * @param productCategory
	 *            the selected category of the product
	 */
	@Override
	public void addProduct(String productName, int quantity, double price,
			ProductCategory productCategory) throws MelanieArgumentException {

		argumentValidator.VerifyNonNull(productCategory);
		argumentValidator.VerifyNotEmptyString(productName,
				LocalConstants.EMPTY_PRODUCT_NAME_MSG);

		Product product = new Product(productName, quantity, price,
				productCategory);
		dataAccess.addDataItem(product);
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
		return dataAccess.findItemById(productId, Product.class);
	}

	/**
	 * Use this to remove a product by name
	 * 
	 * @param productName
	 *            the name of the product to find
	 */
	@Override
	public Product findProduct(String productName) {
		return null;
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
		return dataAccess.getLastInsertedId(Product.class);
	}
}

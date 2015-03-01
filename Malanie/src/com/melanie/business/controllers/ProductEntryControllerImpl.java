package com.melanie.business.controllers;


import java.util.List;

import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.dataaccesslayer.MelanieDataAccessLayerImpl;
import com.melanie.entities.Category;
import com.melanie.entities.Product;
import com.melanie.support.MelanieArgumentValidator;
import com.melanie.support.exceptions.MelanieArgumentException;

public class ProductEntryControllerImpl implements ProductEntryController{

	private class LocalConstants
	{
		public static final String EMPTY_PRODUCT_NAME_MSG = "Product name cannot be empty";
		public static final String EMPTY_CATEGORY_NAME_MSG = "Category name cannot be empty";
		public static final String CATEGORYNAME = "CategoryName";
	}
	
	private MelanieArgumentValidator argumentValidator;
	private MelanieDataAccessLayer dataAccess;
	
	public ProductEntryControllerImpl(){
		argumentValidator = new MelanieArgumentValidator();
		dataAccess = new MelanieDataAccessLayerImpl();
	}
	
	@Override
	public void addCategory(String categoryName) throws MelanieArgumentException {
		argumentValidator.VerifyNotEmptyString(categoryName, LocalConstants.EMPTY_CATEGORY_NAME_MSG);
		dataAccess.addDataItem(new Category(categoryName));
	}
	
	@Override
	public Category findCategory(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Category findCategory(String categoryName) {
		return dataAccess.findItemByFieldName(LocalConstants.CATEGORYNAME, categoryName, Category.class);
	}
	
	@Override
	public List<Category> getAllCategories() {
		return dataAccess.findAllItems(Category.class);
	}
	
	@Override
	public void addProduct(String productName, int quantity, double price,
			Category productCategory) throws MelanieArgumentException {
		
		argumentValidator.VerifyNonNull(productCategory);
		argumentValidator.VerifyNotEmptyString(productName, LocalConstants.EMPTY_PRODUCT_NAME_MSG);
		
		Product product = new Product(productName,quantity,price,productCategory);
	    dataAccess.addDataItem(product);
	}

	@Override
	public void removeProduct(int productId) {
		dataAccess.addDataItem(findProduct(productId));
	}

	@Override
	public Product findProduct(int productId) {
		return dataAccess.findItemById(productId, Product.class);	
	}

	@Override
	public Product findProduct(String productName) {
		return null;
	}

	@Override
	public void updateProductQuantity(String productName, int updateQuantity) {
		// TODO Auto-generated method stub
	}
}

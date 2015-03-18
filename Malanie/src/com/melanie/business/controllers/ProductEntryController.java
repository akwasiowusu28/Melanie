package com.melanie.business.controllers;


import java.util.List;

import com.melanie.entities.Category;
import com.melanie.entities.Product;
import com.melanie.support.exceptions.MelanieArgumentException;

public interface ProductEntryController {

	public void addCategory(String categoryName) throws MelanieArgumentException;
	public Category findCategory(int id);
	public Category findCategory(String categoryName);
	public List<Category> getAllCategories();
	public void addProduct(String productName, int quantity, double price, Category category) throws MelanieArgumentException;
	public void removeProduct(int productId);
	public Product findProduct(int productId);
	public Product findProductByBarcode(String barcodDigits);
	public Product findProduct(String productName)throws MelanieArgumentException;
	public void updateProductQuantity(String productName, int updateQuantity) throws MelanieArgumentException;
	public int getLastInsertedProductId();
}

package com.melanie.business.controllers;


import java.util.List;

import com.melanie.entities.ProductCategory;
import com.melanie.entities.Product;
import com.melanie.support.exceptions.MelanieArgumentException;

public interface ProductEntryController {

	public void addCategory(String categoryName) throws MelanieArgumentException;
	public ProductCategory findCategory(int id);
	public ProductCategory findCategory(String categoryName);
	public List<ProductCategory> getAllCategories();
	public void addProduct(String productName, int quantity, double price, ProductCategory productCategory) throws MelanieArgumentException;
	public void removeProduct(int productId);
	public Product findProduct(int productId);
	public Product findProduct(String productName)throws MelanieArgumentException;
	public void updateProductQuantity(String productName, int updateQuantity) throws MelanieArgumentException;
	public int getLastInsertedProductId();
}

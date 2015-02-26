package com.melanie.business.controllers;


import com.melanie.entities.Category;
import com.melanie.entities.Product;
import com.melanie.support.exceptions.MelanieArgumentException;

public interface ProductEntryController {

	public void addCategory(String categoryName) throws MelanieArgumentException;
	public Category findCategory(int id);
	public Category findCategory(String categoryName);
	public void addProduct(String productName, int quantity, double price, Category productCategory) throws MelanieArgumentException;
	public void removeProduct(int productId);
	public Product findProduct(int productId);
	public Product findProduct(String productName)throws MelanieArgumentException;
	public void updateProductQuantity(String productName, int updateQuantity) throws MelanieArgumentException;
}

package com.melanie.business;

import java.util.List;

import com.melanie.entities.Category;
import com.melanie.entities.Product;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

/**
 * Provides methods for adding product categories and products
 * 
 * @author Akwasi Owusu
 * 
 */
public interface ProductEntryController {

	Category addCategory(String categoryName) throws MelanieBusinessException;

	Category findCategory(int id);

	Category findCategory(String categoryName) throws MelanieBusinessException;

	List<Category> getAllCategories() throws MelanieBusinessException;

	OperationResult addProduct(String productName, int quantity, double price,
			Category category, String barcode) throws MelanieBusinessException;

	OperationResult removeProduct(int productId)
			throws MelanieBusinessException;

	Product findProduct(int productId) throws MelanieBusinessException;

	Product findProductByBarcode(String barcodDigits)
			throws MelanieBusinessException;

	Product findProduct(String productName) throws MelanieBusinessException;

	OperationResult updateProductQuantity(String productName, int updateQuantity)
			throws MelanieBusinessException;

	int getLastInsertedProductId() throws MelanieBusinessException;
}

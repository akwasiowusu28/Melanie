package com.melanie.business;

import java.util.List;

import com.melanie.entities.Category;
import com.melanie.entities.Product;
import com.melanie.support.MelanieOperationCallBack;
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

	Category findCategory(int id,
			MelanieOperationCallBack<Category> operationCallBack)
			throws MelanieBusinessException;

	Category findCategory(String categoryName,
			MelanieOperationCallBack<Category> operationCallBack)
			throws MelanieBusinessException;

	List<Category> getAllCategories(
			MelanieOperationCallBack<Category> operationCallBack)
			throws MelanieBusinessException;

	OperationResult addProduct(String productName, int quantity, double price,
			Category category, String barcode) throws MelanieBusinessException;

	OperationResult removeProduct(int productId)
			throws MelanieBusinessException;

	Product findProduct(int productId,
			MelanieOperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException;

	Product findProductByBarcode(String barcodDigits,
			MelanieOperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException;

	Product findProduct(String productName,
			MelanieOperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException;

	OperationResult updateProductQuantity(Product productName,
			int updateQuantity) throws MelanieBusinessException;

	int getLastInsertedProductId() throws MelanieBusinessException;
}

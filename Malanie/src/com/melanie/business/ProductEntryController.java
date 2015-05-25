package com.melanie.business;

import java.util.List;

import com.melanie.entities.Category;
import com.melanie.entities.Product;
import com.melanie.support.OperationCallBack;
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
			OperationCallBack<Category> operationCallBack)
			throws MelanieBusinessException;

	Category findCategory(String categoryName,
			OperationCallBack<Category> operationCallBack)
			throws MelanieBusinessException;

	List<Category> getAllCategories(
			OperationCallBack<Category> operationCallBack)
			throws MelanieBusinessException;

	OperationResult addProduct(String productName, int quantity, double price,
			Category category, String barcode) throws MelanieBusinessException;

	OperationResult removeProduct(int productId)
			throws MelanieBusinessException;

	List<Product> findAllProducts(
			OperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException;

	Product findProduct(int productId,
			OperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException;

	Product findProductByBarcode(String barcodDigits,
			OperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException;

	Product findProduct(String productName,
			OperationCallBack<Product> operationCallBack)
			throws MelanieBusinessException;

	OperationResult updateProductQuantity(Product productName,
			int updateQuantity) throws MelanieBusinessException;

	int getLastInsertedProductId() throws MelanieBusinessException;
}

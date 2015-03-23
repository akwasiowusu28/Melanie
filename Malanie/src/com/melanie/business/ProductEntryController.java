package com.melanie.business;

import java.util.List;

import com.melanie.entities.Category;
import com.melanie.entities.Product;
import com.melanie.support.exceptions.MelanieArgumentException;

/**
 * Provides methods for adding product categories and products
 * @author Akwasi Owusu
 *
 */
 public interface ProductEntryController {

	 Category addCategory(String categoryName)
			throws MelanieArgumentException;

	 Category findCategory(int id);

	 Category findCategory(String categoryName);

	 List<Category> getAllCategories();

	 void addProduct(String productName, int quantity, double price,
			Category category, String barcode) throws MelanieArgumentException;

	 void removeProduct(int productId);

	 Product findProduct(int productId);

	 Product findProductByBarcode(String barcodDigits);

	 Product findProduct(String productName)
			throws MelanieArgumentException;

	 void updateProductQuantity(String productName, int updateQuantity)
			throws MelanieArgumentException;

	 int getLastInsertedProductId();
}

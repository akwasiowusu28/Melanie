package com.melanie.business;

import com.melanie.entities.Category;
import com.melanie.entities.CostEntry;
import com.melanie.entities.CostItem;
import com.melanie.entities.Product;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

import java.util.List;

/**
 * Provides methods for adding product categories and products
 *
 * @author Akwasi Owusu
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

    OperationResult updateProduct(Product product) throws MelanieBusinessException;

    void getLastInsertedProductId(OperationCallBack<Integer> operationCallBack) throws MelanieBusinessException;

    OperationResult saveCostEntries(List<CostEntry> costEntries) throws MelanieBusinessException;

    List<CostEntry> getAllCostEntries(
            OperationCallBack<CostEntry> operationCallBack)
            throws MelanieBusinessException;

    List<CostItem> getAllCostItems(
            OperationCallBack<CostItem> operationCallBack)
            throws MelanieBusinessException;
}

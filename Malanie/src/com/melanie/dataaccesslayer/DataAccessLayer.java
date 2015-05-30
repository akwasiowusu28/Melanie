package com.melanie.dataaccesslayer;

import java.util.List;

import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieDataLayerException;

public interface DataAccessLayer {

	<T> void initialize(T dataContext);

	<T> OperationResult addDataItem(T dataItem, Class<T> itemClass,
			OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException;

	public <T> OperationResult addDataItemSync(T dataItem, Class<T> itemClass)
			throws MelanieDataLayerException;

	<T> OperationResult updateDataItem(T dataItem, Class<T> itemClass)
			throws MelanieDataLayerException;

	<T> OperationResult deleteDataItem(T dataItem, Class<T> itemClass)
			throws MelanieDataLayerException;

	<T> T findItemById(int itemId, Class<T> itemClass,
			OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException;

	<T> T findItemByFieldName(String fieldName, String searchValue,
			Class<T> itemClass, OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException;

	<T> List<T> findAllItems(Class<T> itemClass,
			OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException;

	<T> void getLastInsertedId(Class<T> itemClass, OperationCallBack<Integer> operationCallBack)
			throws MelanieDataLayerException;

	<T> List<T> findItemsByFieldName(String fieldName, String searchValue,
			Class<T> itemClass, OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException;

	<T> OperationResult refreshItem(T dataItem, Class<T> itemClass)
			throws MelanieDataLayerException;

	<T> OperationResult addOrUpdateDataItemInLocalDataStoreOnly(T dataItem,
			Class<T> itemClass) throws MelanieDataLayerException;

	<T, E> List<T> findItemsBetween(String fieldName, E lowerBound,
			E upperBound, Class<T> itemClass,
			OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException;

	void clearResources();
}
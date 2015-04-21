package com.melanie.dataaccesslayer;

import java.util.List;

import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieDataLayerException;

public interface MelanieDataAccessLayer {

	<T> void initialize(T dataContext);

	<T> OperationResult addDataItem(T dataItem, Class<T> itemClass,
			MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException;

	public <T> OperationResult addDataItemSync(T dataItem, Class<T> itemClass)
			throws MelanieDataLayerException;

	<T> OperationResult updateDataItem(T dataItem, Class<T> itemClass)
			throws MelanieDataLayerException;

	<T> OperationResult deleteDataItem(T dataItem, Class<T> itemClass)
			throws MelanieDataLayerException;

	<T> T findItemById(int itemId, Class<T> itemClass,
			MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException;

	<T> T findItemByFieldName(String fieldName, String searchValue,
			Class<T> itemClass, MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException;

	<T> List<T> findAllItems(Class<T> itemClass,
			MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException;

	<T> int getLastInsertedId(Class<T> itemClass)
			throws MelanieDataLayerException;

	<T> List<T> findItemsByFieldName(String fieldName, String searchValue,
			Class<T> itemClass, MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException;

	<T> OperationResult refreshItem(T dataItem, Class<T> itemClass)
			throws MelanieDataLayerException;

	<T> OperationResult addDataItemToLocalDataStoreOnly(T dataItem,
			Class<T> itemClass) throws MelanieDataLayerException;

	<T> List<T> findItemsBetween(String fieldName, String lowerBound,
			String upperBound, Class<T> itemClass,
			MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException;
}

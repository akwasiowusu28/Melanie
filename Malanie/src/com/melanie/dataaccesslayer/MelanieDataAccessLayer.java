package com.melanie.dataaccesslayer;

import java.util.List;

import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieDataLayerException;

public interface MelanieDataAccessLayer {
	void initialize(DataSource dataSource);

	<T> OperationResult addDataItem(T dataItem)
			throws MelanieDataLayerException;

	<T> OperationResult updateDataItem(T dataItem)
			throws MelanieDataLayerException;

	<T> OperationResult deleteDataItem(T dataItem)
			throws MelanieDataLayerException;

	<T> T findItemById(int itemId, Class<?> itemClass)
			throws MelanieDataLayerException;

	<T> T findItemByFieldName(String fieldName, String searchValue,
			Class<?> itemClass) throws MelanieDataLayerException;

	<T> List<T> findAllItems(Class<?> itemClass)
			throws MelanieDataLayerException;

	<T> int getLastInsertedId(Class<?> itemClass)
			throws MelanieDataLayerException;

	<T> List<T> findItemsByFieldName(String fieldName, String searchValue,
			Class<?> itemClass) throws MelanieDataLayerException;
}

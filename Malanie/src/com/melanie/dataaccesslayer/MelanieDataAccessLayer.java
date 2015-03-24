package com.melanie.dataaccesslayer;

import java.util.List;

import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.support.OperationResult;

 public interface MelanieDataAccessLayer {
	 void initialize(DataSource dataSource);

	 <T> OperationResult addDataItem(T dataItem);

	 <T> OperationResult updateDataItem(T dataItem);

	 <T> OperationResult deleteDataItem(T dataItem);

	 <T> T findItemById(int itemId, Class<?> itemClass);

	 <T> T findItemByFieldName(String fieldName, String searchValue,
			Class<?> itemClass);

	 <T> List<T> findAllItems(Class<?> itemClass);

	 <T> int getLastInsertedId(Class<?> itemClass);
}

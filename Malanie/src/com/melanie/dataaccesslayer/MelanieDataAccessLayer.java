package com.melanie.dataaccesslayer;

import java.util.List;

import com.melanie.dataaccesslayer.datasource.DataSource;

 public interface MelanieDataAccessLayer {
	 void initialize(DataSource dataSource);

	 <T> void addDataItem(T dataItem);

	 <T> boolean updateDataItem(T dataItem);

	 <T> boolean deleteDataItem(T dataItem);

	 <T> T findItemById(int itemId, Class<?> itemClass);

	 <T> T findItemByFieldName(String fieldName, String searchValue,
			Class<?> itemClass);

	 <T> List<T> findAllItems(Class<?> itemClass);

	 <T> int getLastInsertedId(Class<?> itemClass);
}

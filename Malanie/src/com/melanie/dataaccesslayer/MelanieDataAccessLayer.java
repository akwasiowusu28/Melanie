package com.melanie.dataaccesslayer;

import com.melanie.dataaccesslayer.datasource.DataSource;

public interface MelanieDataAccessLayer {
	public void initialize(DataSource dataSource);
	public <T> void addDataItem(T dataItem);
	public <T> void removeDataItem(T dataItem);
	public <T> boolean updateDataItem(T dataItem);
	public <T> boolean deleteDataItem(T dataItem);
	public <T> T findItemById(int itemId,Class<?> itemClass);
	public <T> T findItemByFieldName(String fieldName, String searchValue, Class<?> itemClass);
}

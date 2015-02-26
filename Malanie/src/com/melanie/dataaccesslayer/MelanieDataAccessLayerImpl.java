package com.melanie.dataaccesslayer;

import java.sql.SQLException;
import com.j256.ormlite.dao.Dao;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.dataaccesslayer.datasource.DataSourceManager;
import com.melanie.entities.BaseEntity;
import com.melanie.support.exceptions.MelanieDataLayerException;

public class MelanieDataAccessLayerImpl implements MelanieDataAccessLayer {	
	
	@Override
	public void initialize(DataSource dataSource) {
		DataSourceManager.initialize(dataSource);
	}

	@Override
	public <T> void addDataItem(T dataItem) {
		
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(dataItem.getClass());
	    try {
	    	dao.create(dataItem);
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}	
	}

	@Override
	public <T> void removeDataItem(T dataItem) {
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(dataItem.getClass());
	    try {
	    	dao.delete(dataItem);
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}	
	}

	@Override
	public <T> boolean updateDataItem(T dataItem) {
		boolean updateSuccess = false;
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(dataItem.getClass());
	    try {
	    	if(dao.idExists(((BaseEntity)dataItem).getId())) {
	    		updateSuccess = true;
	    		dao.update(dataItem);
	    	}
	    	
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}	
		return updateSuccess;
	}

	@Override
	public <T> boolean deleteDataItem(T dataItem) {
		boolean deleteSuccess = false;
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(dataItem.getClass());
	    try {
	    	if(dao.idExists(((BaseEntity)dataItem).getId())) {
	    		deleteSuccess = true;
	    		dao.delete(dataItem);
	    	}
	    	
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}	
		return deleteSuccess;
	}

	//TODO: change the class argument to something better
	@SuppressWarnings("unchecked")
	@Override
	public <T> T findItemById(int itemId, Class<?> itemClass) {
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
		T item = null;
	    try {
	    	if(dao.idExists(itemId)) {
	    		
	    		item = (T) dao.queryForId(itemId);
	    	}
	    	
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}	
		return item;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T findItemByFieldName(String fieldName, String searchValue, Class<?> itemClass) {
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
		T item = null;
	    try {
	    	
	    	item =  (T) dao.queryForEq(fieldName, searchValue).get(0);
	    	
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}	
		return item;
	}

}

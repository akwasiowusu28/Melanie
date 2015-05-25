package com.melanie.dataaccesslayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.melanie.dataaccesslayer.datasource.DataSourceManager;
import com.melanie.entities.BaseEntity;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieDataLayerException;

@SuppressWarnings("unchecked")
final class DataUtil {

	public static <T> void updateItemRecentUse(T dataItem) {
		((BaseEntity) dataItem).setRecentUse(new Date());
	}

	public static <T> List<T> findAllItemsFromCache(Class<T> itemClass)
			throws MelanieDataLayerException {
		List<T> items = new ArrayList<T>();
		try {
			Dao<Object, Integer> dao = DataSourceManager
					.getCachedDaoFor(itemClass);
			if (dao != null)
				items.addAll((List<T>) dao.queryForAll());

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return items;
	}

	private static <T> void removeLeastRecentlyUsedItem(
			Dao<Object, Integer> dao, Class<T> itemClass)
			throws MelanieDataLayerException {
		List<BaseEntity> items = (List<BaseEntity>) findAllItemsFromCache(itemClass);
		int size = items.size();
		if (size > 0) {
			Collections.sort(items);
			try {
				dao.delete(items.remove(0));
			} catch (SQLException e) {
				throw new MelanieDataLayerException(e.getMessage(), e);
			}
		}
	}

	public static <T> OperationResult updateDataCache(T dataItem)
			throws MelanieDataLayerException {
		OperationResult result = OperationResult.FAILED;
		try {
			if (dataItem != null) {
				Class<?> itemClass = dataItem.getClass();
				Dao<Object, Integer> dao = DataSourceManager
						.getCachedDaoFor(itemClass);
				if (dao != null && dao.countOf() >= 3)
					DataUtil.removeLeastRecentlyUsedItem(dao, itemClass);
				  updateItemRecentUse(dataItem);
	
					result = addOrUpdateItem(dao,dataItem);
			}
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return result;
	}

	public static <T> OperationResult addOrUpdateItem(Dao<Object, Integer> dao, T dataItem) throws SQLException{
		 dao.createOrUpdate(dataItem);
		return OperationResult.SUCCESSFUL;
	}
	
	public static class DataCallBack<T> extends OperationCallBack<T> {

		private final OperationCallBack<T> businessCallBack;

		public DataCallBack(OperationCallBack<T> businessCallback) {
			super();
			businessCallBack = businessCallback;
		}

		@Override
		public void onCollectionOperationSuccessful(List<T> results) {

			try {
				for (T result : results)
					updateDataCache(result);
			} catch (MelanieDataLayerException e) {
				e.printStackTrace(); // TODO: log it
			}
			if (businessCallBack != null)
				businessCallBack.onCollectionOperationSuccessful(results);
		}

		@Override
		public void onOperationSuccessful(T result) {

			try {
				updateDataCache(result);
			} catch (MelanieDataLayerException e) {
				e.printStackTrace(); // TODO: log it
			}

			if (businessCallBack != null)
				businessCallBack.onOperationSuccessful(result);
		}
	}
}

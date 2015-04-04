package com.melanie.dataaccesslayer;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.melanie.dataaccesslayer.datasource.DataSourceManager;
import com.melanie.entities.BaseEntity;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieDataLayerException;

@SuppressWarnings("unchecked")
public final class DataUtil {

	public static <T> void updateItemRecentUse(T dataItem) {
		((BaseEntity) dataItem).setRecentUse(new Date());
	}

	public static <T> List<T> findAllItemsFromCache(Class<T> itemClass)
			throws MelanieDataLayerException {
		List<T> items = null;
		try {
			Dao<Object, Integer> dao = DataSourceManager
					.getCachedDaoFor(itemClass);
			items = (List<T>) dao.queryForAll();
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return items;
	}

	public static <T> void removeLeastRecentlyUsedItem(
			Dao<Object, Integer> dao, Class<T> itemClass)
			throws MelanieDataLayerException {
		List<BaseEntity> items = (List<BaseEntity>) findAllItemsFromCache(itemClass);
		int size = items.size();
		if (size > 0) {
			Collections.sort(items);
			try {
				dao.delete(items.get(0));
			} catch (SQLException e) {
				throw new MelanieDataLayerException(e.getMessage(), e);
			}
		}
	}

	public static <T> void upDataCache(T dataItem)
			throws MelanieDataLayerException {
		try {
			if (dataItem != null) {
				Dao<Object, Integer> dao = DataSourceManager
						.getCachedDaoFor(dataItem.getClass());
				updateItemRecentUse(dataItem);
				dao.create(dataItem);
			}
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public static class DataCallBack<T> extends MelanieOperationCallBack {

		private MelanieOperationCallBack businessCallBack;
		private Class<T> itemClass;

		public DataCallBack(MelanieOperationCallBack businessCallback,
				Class<T> itemClass) {
			businessCallBack = businessCallback;
			this.itemClass = itemClass;
		}

		@Override
		public <E> void onOperationSuccessful(List<E> results) {
			if (businessCallBack != null)
				businessCallBack.onOperationSuccessful(results);
			try {
				removeLeastRecentlyUsedItem(
						DataSourceManager.getCachedDaoFor(itemClass), itemClass);
				for (E result : results)
					upDataCache(result);
			} catch (MelanieDataLayerException e) {
				e.printStackTrace(); // TODO: log it
			}
		}

		@Override
		public <E> void onOperationSuccessful(E result) {
			if (businessCallBack != null)
				businessCallBack.onOperationSuccessful(result);
			try {
				removeLeastRecentlyUsedItem(
						DataSourceManager.getCachedDaoFor(itemClass), itemClass);
				upDataCache(result);
			} catch (MelanieDataLayerException e) {
				e.printStackTrace(); // TODO: log it
			}
		}
	}
}

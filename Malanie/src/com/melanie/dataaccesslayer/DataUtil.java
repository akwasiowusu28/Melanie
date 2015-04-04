package com.melanie.dataaccesslayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.melanie.dataaccesslayer.datasource.DataSourceManager;
import com.melanie.entities.BaseEntity;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieDataLayerException;

@SuppressWarnings("unchecked")
public final class DataUtil {

	private static <T> void updateItemRecentUse(T dataItem) {
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
				if (dao.countOf() >= 3)
					DataUtil.removeLeastRecentlyUsedItem(dao, itemClass);
				updateItemRecentUse(dataItem);
				CreateOrUpdateStatus status = dao.createOrUpdate(dataItem);
				if (status.isCreated() || status.isUpdated())
					result = OperationResult.SUCCESSFUL;
			}
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return result;
	}

	public static class DataCallBack<T> extends MelanieOperationCallBack {

		private MelanieOperationCallBack businessCallBack;

		public DataCallBack(MelanieOperationCallBack businessCallback) {
			businessCallBack = businessCallback;
		}

		@Override
		public <E> void onOperationSuccessful(List<E> results) {
			if (businessCallBack != null)
				businessCallBack.onOperationSuccessful(results);
			try {
				for (E result : results)
					updateDataCache(result);
			} catch (MelanieDataLayerException e) {
				e.printStackTrace(); // TODO: log it
			}
		}

		@Override
		public <E> void onOperationSuccessful(E result) {
			if (businessCallBack != null)
				businessCallBack.onOperationSuccessful(result);
			try {
				updateDataCache(result);
			} catch (MelanieDataLayerException e) {
				e.printStackTrace(); // TODO: log it
			}
		}
	}
}
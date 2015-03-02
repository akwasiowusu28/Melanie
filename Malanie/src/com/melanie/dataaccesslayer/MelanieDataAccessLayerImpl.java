package com.melanie.dataaccesslayer;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.dataaccesslayer.datasource.DataSourceManager;
import com.melanie.entities.BaseEntity;
import com.melanie.support.exceptions.MelanieDataLayerException;

@SuppressWarnings("unchecked")
public class MelanieDataAccessLayerImpl implements MelanieDataAccessLayer {

	@Override
	public void initialize(DataSource dataSource) {
		DataSourceManager.initialize(dataSource);
	}

	@Override
	public <T> void addDataItem(T dataItem) {

		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(dataItem
				.getClass());
		try {
			if (dao != null)
				dao.create(dataItem);
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}
	}

	@Override
	public <T> void removeDataItem(T dataItem) {
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(dataItem
				.getClass());
		try {
			if (dao != null)
				dao.delete(dataItem);
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}
	}

	@Override
	public <T> boolean updateDataItem(T dataItem) {
		boolean updateSuccess = false;
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(dataItem
				.getClass());
		try {
			if (dao != null && dao.idExists(((BaseEntity) dataItem).getId())) {
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
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(dataItem
				.getClass());
		try {
			if (dao != null && dao.idExists(((BaseEntity) dataItem).getId())) {
				deleteSuccess = true;
				dao.delete(dataItem);
			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}
		return deleteSuccess;
	}

	// TODO: change the class argument to something better
	@Override
	public <T> T findItemById(int itemId, Class<?> itemClass) {
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
		T item = null;
		try {
			if (dao != null && dao.idExists(itemId))

				item = (T) dao.queryForId(itemId);

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}
		return item;
	}

	@Override
	public <T> T findItemByFieldName(String fieldName, String searchValue,
			Class<?> itemClass) {
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
		T item = null;
		try {
			if (dao != null)
				item = (T) dao.queryForEq(fieldName, searchValue).get(0);

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}
		return item;
	}

	@Override
	public <T> List<T> findAllItems(Class<?> itemClass) {
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
		List<T> items = null;
		try {
			items = (List<T>) dao.queryForAll();
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}
		return items;
	}

	@Override
	public <T> int getLastInsertedId(Class<?> itemClass) {
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
		int id = -1;
		try {
			if (dao != null && dao.queryBuilder() != null) {
				QueryBuilder<Object, Integer> queryBuilder = dao.queryBuilder();
				queryBuilder.selectRaw("max(id)");
				String[] result = dao.queryRaw(
						queryBuilder.prepareStatementString()).getFirstResult();
				id = result[0] != null ? Integer.parseInt(result[0]) : 1;
			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}
		return id;
	}

}

package com.melanie.dataaccesslayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.dataaccesslayer.datasource.DataSourceManager;
import com.melanie.entities.BaseEntity;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieDataLayerException;

/**
 * 
 * @author Akwasi Owusu A class that serves as a facade to the Database access
 *         for all clients
 */
@SuppressWarnings("unchecked")
public class MelanieDataAccessLayerImpl implements MelanieDataAccessLayer {

	/**
	 * @param dataSource
	 *            the ORM datasource helper pushed from the UI
	 */
	@Override
	public void initialize(DataSource dataSource) {
		DataSourceManager.initialize(dataSource);
	}

	/**
	 * 
	 * Use this to add an item to the database. Item should typically be
	 * anything that extends BaseEntity
	 * 
	 * @param dataItem
	 *            The item to persist
	 * @throws MelanieDataLayerException
	 */
	@Override
	public <T> OperationResult addDataItem(T dataItem)
			throws MelanieDataLayerException {
		OperationResult result = OperationResult.FAILED;
		try {
			Dao<Object, Integer> dao = DataSourceManager
					.getCachedDaoFor(dataItem.getClass());
			if (dao != null) {
				int insertReturn = dao.create(dataItem);
				if (insertReturn == 1)
					result = OperationResult.SUCCESSFUL;
			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 
	 * Use this to update an item in the database. Item should typically be
	 * anything that extends BaseEntity
	 * 
	 * @param dataItem
	 *            The item to update
	 * @throws MelanieDataLayerException
	 */
	@Override
	public <T> OperationResult updateDataItem(T dataItem)
			throws MelanieDataLayerException {
		OperationResult result = OperationResult.FAILED;

		try {
			Dao<Object, Integer> dao = DataSourceManager
					.getCachedDaoFor(dataItem.getClass());
			if (dao != null && dao.idExists(((BaseEntity) dataItem).getId())) {
				int updateReturn = dao.update(dataItem);
				if (updateReturn == 1)
					result = OperationResult.SUCCESSFUL;
			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 
	 * @param dataItem
	 *            The dataItem to delete. Returns true on success
	 * @throws MelanieDataLayerException
	 */
	@Override
	public <T> OperationResult deleteDataItem(T dataItem)
			throws MelanieDataLayerException {
		OperationResult result = OperationResult.FAILED;

		try {
			Dao<Object, Integer> dao = DataSourceManager
					.getCachedDaoFor(dataItem.getClass());
			if (dao != null && dao.idExists(((BaseEntity) dataItem).getId())) {

				int deleteReturn = dao.delete(dataItem);
				if (deleteReturn == 1)
					result = OperationResult.SUCCESSFUL;
			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * Returns an item based on its id
	 * 
	 * @return Item of type T, the type of the specified class
	 * @throws MelanieDataLayerException
	 */
	@Override
	public <T> T findItemById(int itemId, Class<?> itemClass)
			throws MelanieDataLayerException {
		T item = null;
		try {
			Dao<Object, Integer> dao = DataSourceManager
					.getCachedDaoFor(itemClass);
			if (dao != null && dao.idExists(itemId))

				item = (T) dao.queryForId(itemId);

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return item;
	}

	/**
	 * Returns an item based on it's field name
	 * 
	 * @param fieldName
	 *            The name of the field
	 * @param searchValue
	 *            The value to search for in that field
	 * @param itemClass
	 *            The class of the return item
	 * @return The item searched for by name
	 * @throws MelanieDataLayerException
	 */
	@Override
	public <T> T findItemByFieldName(String fieldName, String searchValue,
			Class<?> itemClass) throws MelanieDataLayerException {
		T item = null;
		try {
			Dao<Object, Integer> dao = DataSourceManager
					.getCachedDaoFor(itemClass);
			if (dao != null) {
				List<Object> results = dao.queryForEq(fieldName, searchValue);
				if (results != null && results.size() > 0)
					item = (T) results.get(0);
			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return item;
	}

	/**
	 * Find all items
	 * 
	 * @param itemClass
	 *            The class of the return item
	 * @return all Items of itemClass
	 * @throws MelanieDataLayerException
	 */
	@Override
	public <T> List<T> findAllItems(Class<?> itemClass)
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

	/**
	 * 
	 * Returns the id of the last inserted item
	 * 
	 * @param itemClass
	 *            the class of the return item
	 * @return the id of the last insertedItem
	 * @throws MelanieDataLayerException
	 */
	@Override
	public <T> int getLastInsertedId(Class<?> itemClass)
			throws MelanieDataLayerException {
		int id = -1;
		try {
			Dao<Object, Integer> dao = DataSourceManager
					.getCachedDaoFor(itemClass);
			if (dao != null && dao.queryBuilder() != null) {
				QueryBuilder<Object, Integer> queryBuilder = dao.queryBuilder();
				queryBuilder.selectRaw("max(id)");
				String[] result = dao.queryRaw(
						queryBuilder.prepareStatementString()).getFirstResult();
				id = result[0] != null ? Integer.parseInt(result[0]) : 1;
			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return id;
	}

	@Override
	public <T> List<T> findItemsByFieldName(String fieldName,
			String searchValue, Class<?> itemClass)
			throws MelanieDataLayerException {
		List<T> items = new ArrayList<T>();

		try {
			Dao<Object, Integer> dao = DataSourceManager
					.getCachedDaoFor(itemClass);
			if (dao != null) {
				List<Object> results = dao.queryForEq(fieldName, searchValue);
				if (results != null && results.size() > 0)
					items = (List<T>) results;
			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return items;
	}

}

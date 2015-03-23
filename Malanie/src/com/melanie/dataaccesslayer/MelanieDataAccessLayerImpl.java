package com.melanie.dataaccesslayer;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.dataaccesslayer.datasource.DataSourceManager;
import com.melanie.entities.BaseEntity;
import com.melanie.support.exceptions.MelanieDataLayerException;

/**
 * 
 * @author Akwasi Owusu
 * A class that serves as a facade to the Database access for all clients
 */
@SuppressWarnings("unchecked")
public class MelanieDataAccessLayerImpl implements MelanieDataAccessLayer {

	/**
	 * @param dataSource the ORM datasource helper pushed from the UI
	 */
	@Override
	public void initialize(DataSource dataSource) {
		DataSourceManager.initialize(dataSource);
	}

	/**
	 * 
	 * Use this to add an item to the database. Item should typically be anything that extends BaseEntity
	 * @param dataItem The item to persist
	 */
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

	/**
	 * 
	 * Use this to update an item in the database. Item should typically be anything that extends BaseEntity
	 * @param dataItem The item to update
	 */
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

	/**
	 * 
	 * @param dataItem The dataItem to delete. Returns true on success
	 */
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


/**
 * Returns an item based on its id
 * @return Item of type T, the type of the specified class
 */
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

	/**
	 * Returns an item based on it's field name
	 * @param fieldName The name of the field
	 * @param searchValue The value to search for in that field
	 * @param itemClass The class of the return item
	 * @return The item searched for by name
	 */
	@Override
	public <T> T findItemByFieldName(String fieldName, String searchValue,
			Class<?> itemClass) {
		Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
		T item = null;
		try {
			if (dao != null)
			{
				List<Object> results = dao.queryForEq(fieldName, searchValue);
				if(results != null && results.size() > 0)
					item = (T) results.get(0);
			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}
		return item;
	}

	/**
	 * Find all items
	 * @param itemClass The class of the return item
	 * @return all Items of itemClass
	 */
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

	/**
	 * 
	 * Returns the id of the last inserted item
	 * @param itemClass the class of the return item
	 * @return the id of the last insertedItem
	 */
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

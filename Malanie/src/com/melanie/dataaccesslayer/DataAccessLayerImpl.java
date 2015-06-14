package com.melanie.dataaccesslayer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.backendless.BackendlessUser;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.dataaccesslayer.datasource.DataSourceManager;
import com.melanie.entities.BaseEntity;
import com.melanie.entities.User;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieDataLayerException;

/**
 * A class that serves as a facade to Database access for all clients
 * <b>Note:</b> Any call to the MelanieCloudAccess must have the
 * operationCallBack argument wrapped in a DataUtil.DataCallBack instance. This
 * is used to effectively maintain the cache
 * 
 * @author Akwasi Owusu
 */

@SuppressWarnings("unchecked")
public class DataAccessLayerImpl implements DataAccessLayer {

	private class LocalConstants{
		public static final String EMPTY = "empty";
		public static final String OBJECTID = "objectId";
	}

	private final CloudAccess cloudAccess;

	public DataAccessLayerImpl() {
		super();
		cloudAccess = new CloudAccess();
	}

	/**
	 * @param dataSource
	 *            the ORM datasource helper pushed from the UI
	 */
	@Override
	public <T> void initialize(T dataContext) {
		DataSourceManager.initialize((DataSource) dataContext);
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
	public <T> OperationResult addDataItem(T dataItem, Class<T> itemClass, OperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException {
		if (dataItem instanceof User) {
			addUser(dataItem, operationCallBack);
			return OperationResult.SUCCESSFUL;
		}
		OperationResult result = OperationResult.FAILED;
		try {
			Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
			if (dao != null) {
				int addReturn = dao.create(dataItem);
				if (addReturn == 1) {
					DataUtil.updateItemRecentUse(dataItem);
					if (cloudAccess != null) {
						cloudAccess.addDataItem(dataItem, itemClass, new DataUtil.DataCallBack<T>(operationCallBack));
					}
					result = OperationResult.SUCCESSFUL;
				}
			}
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * Use this to add items that has to be added synchronously
	 * 
	 * @param dataItem
	 *            The item to persist
	 * @throws MelanieDataLayerException
	 */
	@Override
	public <T> OperationResult addDataItemSync(T dataItem, Class<T> itemClass) throws MelanieDataLayerException {
		OperationResult result = OperationResult.FAILED;
		try {
			if (cloudAccess != null) {
				cloudAccess.addDataItemSync(dataItem, itemClass);
			}
			Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
			if (dao != null) {
				int addReturn = dao.create(dataItem);
				if (addReturn == 1) {
					DataUtil.updateItemRecentUse(dataItem);
					result = OperationResult.SUCCESSFUL;
				}
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
	public <T> OperationResult updateDataItem(T dataItem, Class<T> itemClass) throws MelanieDataLayerException {
		if (cloudAccess != null) {
			cloudAccess.updateDataItem(dataItem, itemClass, new DataUtil.DataCallBack<T>(null));
		}

		return OperationResult.SUCCESSFUL; // very optimistic :-D
	}

	/**
	 * 
	 * @param dataItem
	 *            The dataItem to delete. Returns true on success
	 * @throws MelanieDataLayerException
	 */
	@Override
	public <T> OperationResult deleteDataItem(T dataItem, Class<T> itemClass) throws MelanieDataLayerException {
		OperationResult result = OperationResult.FAILED;

		try {
			if (dataItem != null) {
				Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
				if (dao != null && dao.idExists(((BaseEntity) dataItem).getId())) {
					int deleteReturn = dao.delete(dataItem);
					if (deleteReturn == 1) {
						result = OperationResult.SUCCESSFUL;
					}
				}
				if (cloudAccess != null) {
					cloudAccess.deleteDataItem(dataItem, itemClass, null);
				}
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
	public <T> T findItemById(int itemId, Class<T> itemClass, OperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException {
		T item = null;
		try {
			Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
			if (dao != null && dao.idExists(itemId)) {
				item = itemClass.cast(dao.queryForId(itemId));
				if (itemClass != User.class) {
					DataUtil.updateDataCache(item);
				}
			} else if (operationCallBack != null && cloudAccess != null) {
				cloudAccess.findItemById(itemId, itemClass, new DataUtil.DataCallBack<T>(operationCallBack));
			}

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
	public <T> T findItemByFieldName(String fieldName, String searchValue, Class<T> itemClass,
			OperationCallBack<T> operationCallBack) throws MelanieDataLayerException {
		T item = null;
		try {
			Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
			if (dao != null) {
				List<Object> results = dao.queryForEq(getFieldNameWithoutClassNamePrefix(fieldName), searchValue);
				if (results != null && results.size() > 0) {
					item = itemClass.cast(results.get(0));
					DataUtil.updateDataCache(item);
				} else if (operationCallBack != null && cloudAccess != null) {
					cloudAccess.findItemByFieldName(fieldName, searchValue, itemClass, new DataUtil.DataCallBack<T>(
							operationCallBack));
				}
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
	public <T> List<T> findAllItems(Class<T> itemClass, OperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException {

		List<T> items = DataUtil.findAllItemsFromCache(itemClass);
		for (T item : items) {
			DataUtil.updateDataCache(item);
		}
		if (cloudAccess != null) {
			cloudAccess.findAllItems(itemClass, new DataUtil.DataCallBack<T>(operationCallBack));
		}

		return items;
	}

	/**
	 * 
	 * Returns the id of the last inserted item
	 * 
	 * @param itemClass
	 *            the class of the return item
	 * @throws MelanieDataLayerException
	 */
	@Override
	public <T> void getLastInsertedId(Class<T> itemClass, final OperationCallBack<Integer> operationCallBack)
			throws MelanieDataLayerException {

		cloudAccess.getLastInsertedItem(itemClass, new OperationCallBack<T>() {

			@Override
			public void onOperationSuccessful(T result) {
				operationCallBack.onOperationSuccessful(((BaseEntity) result).getId());
			}

			@Override
			public void onOperationFailed(Throwable e) {
				if (e.getMessage().contains(LocalConstants.EMPTY)) {
					operationCallBack.onOperationSuccessful(0);
				} else {
					operationCallBack.onOperationFailed(e);
				}
			}
		});
	}

	@Override
	public <T> List<T> findItemsByFieldName(String fieldName, String searchValue, Class<T> itemClass,
			OperationCallBack<T> operationCallBack) throws MelanieDataLayerException {
		List<T> items = new ArrayList<T>();
		try {
			Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
			if (dao != null) {

				List<Object> results = dao.queryForEq(getFieldNameWithoutClassNamePrefix(fieldName), searchValue);
				if (results != null && results.size() > 0) {
					items = (List<T>) results;
					for (T item : items) {
						DataUtil.updateDataCache(item);
					}
				}
				if (operationCallBack != null && cloudAccess != null) {
					cloudAccess.findItemsByFieldName(fieldName, searchValue, itemClass, new DataUtil.DataCallBack<T>(
							operationCallBack));
				}
			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return items;
	}

	/**
	 * When finding items based of field names of composed objects, the
	 * fieldname might be preceded by the classname of the related object for
	 * use in the cloud retrieval. Because of that, use the field name without
	 * the class name prefix for the ormlite retrieval
	 * */
	private String getFieldNameWithoutClassNamePrefix(String fieldName) {
		String result = fieldName;
		if (fieldName.contains(".")) {
			result = fieldName.substring(fieldName.lastIndexOf(".") + 1);
		}
		return result;
	}

	@Override
	public <T> OperationResult refreshItem(T dataItem, Class<T> itemClass) throws MelanieDataLayerException {
		OperationResult result = OperationResult.FAILED;
		try {
			if (dataItem != null) {
				Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
				if (dao != null && dao.idExists(((BaseEntity) dataItem).getId())) {
					int updateReturn = dao.refresh(dataItem);
					if (updateReturn == 1) {
						DataUtil.updateDataCache(dataItem);
						result = OperationResult.SUCCESSFUL;
					}
				}
			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return result;
	}

	@Override
	public <T> OperationResult addOrUpdateItemLocalOnly(T dataItem, Class<T> itemClass)
			throws MelanieDataLayerException {
		OperationResult result = OperationResult.FAILED;
		if (dataItem instanceof User) {
			Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
			try {
				result = DataUtil.addOrUpdateItem(dao, dataItem);
			} catch (SQLException e) {
				throw new MelanieDataLayerException(e.getMessage(), e);
			}
		} else {
			result = DataUtil.updateDataCache(dataItem);
		}
		return result;
	}

	@Override
	public <T, E> List<T> findItemsBetween(String fieldName, E lowerBound, E upperBound, Class<T> itemClass,
			OperationCallBack<T> operationCallBack) throws MelanieDataLayerException {

		List<T> result = new ArrayList<T>();
		try {
			Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
			if (dao != null && dao.queryBuilder() != null) {
				QueryBuilder<Object, Integer> queryBuilder = dao.queryBuilder();
				Where<Object, Integer> where = queryBuilder.where();
				where.between(fieldName, lowerBound, upperBound);

				result.addAll((List<T>) dao.query(queryBuilder.prepare()));
				cloudAccess.findItemsBetween(fieldName, lowerBound, upperBound, itemClass,
						new DataUtil.DataCallBack<T>(operationCallBack));
			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return result;
	}

	private <T> void addUser(T user, final OperationCallBack<T> operationCallBack) throws MelanieDataLayerException {
		try {
			Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(User.class);
			DataUtil.addOrUpdateItem(dao, user);
			addUserToCloud((User) user, dao, (OperationCallBack<User>) operationCallBack);
		} catch (MelanieDataLayerException | SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	private void addUserToCloud(User user, final Dao<Object, Integer> dao,
			final OperationCallBack<User> operationCallBack) {
		final User userInstance = user;
		cloudAccess.addUser(userInstance, new OperationCallBack<BackendlessUser>() {
			@Override
			public void onOperationSuccessful(BackendlessUser result) {
				try {
					userInstance.setObjectId(result.getProperty(LocalConstants.OBJECTID).toString());
					userInstance.setProperties(result.getProperties());
					operationCallBack.onOperationSuccessful(userInstance);
					DataUtil.addOrUpdateItem(dao, userInstance);
				} catch (SQLException e) {
					operationCallBack.onOperationFailed(e);
				}
			}
		});
	}

	@Override
	public <T> boolean itemExists(T dataItem, Class<T> itemClass) throws MelanieDataLayerException {
		boolean itemExists = false;
		try {
			if (dataItem != null) {
				Dao<Object, Integer> dao = DataSourceManager.getCachedDaoFor(itemClass);
				itemExists = dao != null && dao.idExists(((BaseEntity) dataItem).getId());

			}

		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return itemExists;
	}

	@Override
	public void clearResources() {
		DataSourceManager.clearDataSource();
	}
}

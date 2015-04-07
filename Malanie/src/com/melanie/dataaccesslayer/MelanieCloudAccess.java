package com.melanie.dataaccesslayer;

import java.util.List;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.melanie.dataaccesslayer.datasource.DataSourceManager;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieDataLayerException;

@SuppressWarnings("unchecked")
public class MelanieCloudAccess {

	private static final String ID = "Id";
	private boolean isCollectionSearch;

	public MelanieCloudAccess() {
		isCollectionSearch = false;
	}

	public static <T> void initialize(T dataContext) {
		DataSourceManager.initializeBackendless(dataContext);
	}

	public <T> void addDataItem(T dataItem, Class<T> itemClass,
			MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException {
		try {
			// Backendless.Persistence.of(itemClass).save(dataItem,
			// new BackendAsynCallBack<T>(operationCallBack));
			Backendless.Persistence.of(itemClass).save(dataItem,
					new AsyncCallback<T>() {

						@Override
						public void handleFault(BackendlessFault arg0) {

						}

						@Override
						public void handleResponse(T arg0) {
							// TODO Auto-generated method stub

						}
					});
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public <T> void updateDataItem(T dataItem, Class<T> itemClass,
			MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException {
		addDataItem(dataItem, itemClass, operationCallBack);
	}

	public <T> void deleteDataItem(T dataItem, Class<T> itemClass,
			MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException {
		try {
			Backendless.Persistence.of(itemClass).remove(dataItem,
					AsyncCallback.class.cast(operationCallBack)); // currently
																	// passing
																	// in null
																	// from
																	// DataAccessLayerImpl.
																	// Not sure
																	// of the
																	// behavior
																	// yet
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}

	}

	public <T> void findItemById(int itemId, Class<T> itemClass,
			MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException {
		try {
			findItemByFieldName(ID, String.valueOf(itemId), itemClass,
					operationCallBack);
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public <T> void findItemByFieldName(String fieldName, String searchValue,
			Class<T> itemClass, MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException {
		try {
			String whereClause = fieldName + "='" + searchValue + "'";
			BackendlessDataQuery query = new BackendlessDataQuery(whereClause);
			Backendless.Persistence
					.of(itemClass)
					.find(query,
							(AsyncCallback<BackendlessCollection<T>>) new BackendAsynCallBack<T>(
									operationCallBack));
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}

	}

	public <T> void findAllItems(Class<T> itemClass,
			MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException {
		try {
			isCollectionSearch = true;
			Backendless.Persistence
					.of(itemClass)
					.find((AsyncCallback<BackendlessCollection<T>>) new BackendAsynCallBack<T>(
							operationCallBack));
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public <T> void getLastInsertedItem(Class<T> itemClass,
			MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException {
		try {
			Backendless.Persistence.of(itemClass).findLast(
					new BackendAsynCallBack<T>(operationCallBack));

		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public <T> void findItemsByFieldName(String fieldName, String searchValue,
			Class<T> itemClass, MelanieOperationCallBack<T> operationCallBack)
			throws MelanieDataLayerException {
		try {
			isCollectionSearch = true;

			String whereClause = fieldName + "='" + searchValue + "'";
			BackendlessDataQuery query = new BackendlessDataQuery(whereClause);
			Backendless.Persistence
					.of(itemClass)
					.find(query,
							(AsyncCallback<BackendlessCollection<T>>) new BackendAsynCallBack<T>(
									operationCallBack));
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	private class BackendAsynCallBack<T> extends BackendlessCallback<T> {

		private MelanieOperationCallBack<T> operationCallBack;

		public BackendAsynCallBack(MelanieOperationCallBack<T> operationCallBack) {
			this.operationCallBack = operationCallBack;
		}

		@Override
		public void handleResponse(Object responseObject) {
			if (operationCallBack != null)
				if (responseObject instanceof BackendlessCollection) {
					List<T> responseData = ((BackendlessCollection<T>) responseObject)
							.getData();
					if (!isCollectionSearch) {
						T item = responseData.size() > 0 ? responseData.get(0)
								: null;
						operationCallBack.onOperationSuccessful(item);
					} else
						operationCallBack
								.onCollectionOperationSuccessful(responseData);
				} else
					operationCallBack.onOperationSuccessful((T) responseObject);
			isCollectionSearch = false;
		}
	}

}

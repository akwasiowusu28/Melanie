package com.melanie.dataaccesslayer;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.persistence.BackendlessDataQuery;
import com.melanie.dataaccesslayer.datasource.DataSourceManager;
import com.melanie.entities.BaseEntity;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieDataLayerException;

// Consider doing the Async Callback thingy for this class
@SuppressWarnings("unchecked")
public class MelanieDataAccessLayerImplCloud {

	public <T> void initialize(T dataContext) {
		DataSourceManager.initializeBackendless(dataContext);
	}

	public <T> void addDataItem(T dataItem, Class<T> itemClass,
			MelanieOperationCallBack operationCallBack)
			throws MelanieDataLayerException {
		try {
			Backendless.Persistence.of(itemClass).save(dataItem,
					new BackendAsynCallBack<T>(operationCallBack));
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public <T> void updateDataItem(T dataItem, Class<T> itemClass,
			MelanieOperationCallBack operationCallBack)
			throws MelanieDataLayerException {
		addDataItem(dataItem, itemClass, operationCallBack);
	}

	public <T> void deleteDataItem(T dataItem, Class<T> itemClass,
			MelanieOperationCallBack operationCallBack)
			throws MelanieDataLayerException {
		try {
			Backendless.Persistence.of(itemClass).remove(
					dataItem,
					(AsyncCallback<Long>) new BackendAsynCallBack<T>(
							operationCallBack));
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}

	}

	public <T> T findItemById(String itemId, Class<T> itemClass,
			MelanieOperationCallBack operationCallBack)
			throws MelanieDataLayerException {
		T item = null;
		try {
			Backendless.Persistence.of(itemClass).findById(itemId,
					new BackendAsynCallBack<T>(operationCallBack));
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return item;
	}

	public <T> T findItemByFieldName(String fieldName, String searchValue,
			Class<T> itemClass, MelanieOperationCallBack operationCallBack)
			throws MelanieDataLayerException {

		T item = null;
		try {
			String whereClause = fieldName + "='" + searchValue + "'";
			BackendlessDataQuery query = new BackendlessDataQuery(whereClause);
			BackendlessCollection<T> results = Backendless.Persistence.of(
					itemClass).find(query);

			if (results != null && results.getData() != null)
				item = results.getData().get(0);
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}

		return item;
	}

	public <T> void findAllItems(Class<T> itemClass,
			MelanieOperationCallBack operationCallBack)
			throws MelanieDataLayerException {
		try {
			Backendless.Persistence
					.of(itemClass)
					.find((AsyncCallback<BackendlessCollection<T>>) new BackendAsynCallBack<T>(
							operationCallBack));
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public <T> void getLastInsertedItem(Class<T> itemClass,
			MelanieOperationCallBack operationCallBack)
			throws MelanieDataLayerException {
		try {
			Backendless.Persistence.of(itemClass).findLast(
					new BackendAsynCallBack<T>(operationCallBack));

		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public <T> void findItemsByFieldName(String fieldName, String searchValue,
			Class<T> itemClass, MelanieOperationCallBack operationCallBack)
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

	public <T> void refreshItem(T dataItem, Class<T> itemClass,
			MelanieOperationCallBack operationCallBack) {

	}

	private class BackendAsynCallBack<T> extends BackendlessCallback<T> {

		private MelanieOperationCallBack operationCallBack;

		public <E> BackendAsynCallBack(
				MelanieOperationCallBack operationCallBack) {
			this.operationCallBack = operationCallBack;
		}

		@Override
		public void handleResponse(Object responseObject) {
			if (responseObject instanceof BackendlessCollection)
				operationCallBack
						.onOperationSuccessful(((BackendlessCollection<?>) responseObject)
								.getData());
			else if (responseObject instanceof BaseEntity)
				operationCallBack
						.onOperationSuccessful((BaseEntity) responseObject);
		}
	}
}

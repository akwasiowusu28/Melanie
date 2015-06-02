package com.melanie.dataaccesslayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.melanie.dataaccesslayer.datasource.DataSourceManager;
import com.melanie.entities.User;
import com.melanie.support.CodeStrings;
import com.melanie.support.OperationCallBack;
import com.melanie.support.exceptions.MelanieDataLayerException;

@SuppressWarnings("unchecked")
public class CloudAccess {

	private boolean isCollectionOperation;

	private int collectionRequestsCount = 0;

	public CloudAccess() {
		isCollectionOperation = false;
	}

	public static <T> void initialize(T dataContext) {
		DataSourceManager.initializeBackendless(dataContext);
	}

	public <T> void addDataItem(T dataItem, Class<T> itemClass,
			OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException {
		try {
			Backendless.Persistence.of(itemClass).save(dataItem,
					new BackendAsynCallBack<T>(operationCallBack));
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public <T> void addDataItemSync(T dataItem, Class<T> itemClass)
			throws MelanieDataLayerException {
		try {
			Backendless.Persistence.of(itemClass).save(dataItem);
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public <T> void updateDataItem(T dataItem, Class<T> itemClass,
			OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException {
		addDataItem(dataItem, itemClass, operationCallBack);
	}

	public <T> void deleteDataItem(T dataItem, Class<T> itemClass,
			OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException {
		try {
			Backendless.Persistence.of(itemClass).remove(dataItem,
					AsyncCallback.class.cast(operationCallBack)); // currently passing in null fromDataAccessLayerImpl.
			// Not sure of the behavior yet
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}

	}

	public <T> void findItemById(int itemId, Class<T> itemClass,
			OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException {
		try {
			findItemByFieldName(CodeStrings.ID, String.valueOf(itemId), itemClass,
					operationCallBack);
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public <T> void findItemByFieldName(String fieldName, String searchValue,
			Class<T> itemClass, OperationCallBack<T> operationCallBack)
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
			OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException {
		try {
			isCollectionOperation = true;
			collectionRequestsCount ++;
			Backendless.Persistence
			.of(itemClass)
			.find((AsyncCallback<BackendlessCollection<T>>) new BackendAsynCallBack<T>(
					operationCallBack));
		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public <T> void getLastInsertedItem(Class<T> itemClass,
			OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException {
		try {
			Backendless.Persistence.of(itemClass).findLast(
					new BackendAsynCallBack<T>(operationCallBack));

		} catch (Exception e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
	}

	public <T> void findItemsByFieldName(String fieldName, String searchValue,
			Class<T> itemClass, OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException {
		try {
			isCollectionOperation = true;
			collectionRequestsCount ++;
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

	public <T, E> void findItemsBetween(String fieldName, E lowerBound,
			E upperBound, Class<T> itemClass,
			OperationCallBack<T> operationCallBack)
					throws MelanieDataLayerException {
		try {
			isCollectionOperation = true;
			collectionRequestsCount ++;

			// Find a way to remove the date formatting thingy to a different
			// method. I'm programming under the influence of a 9.5% Alc double
			// IPA beer. I know I'm not thinking straight right now
			// 4/24/2015 10:18 PM

			String lowerBoundString = "";
			String upperBoundString = "";

			if (lowerBound instanceof Date && upperBound instanceof Date) {
				SimpleDateFormat dateFormater = new SimpleDateFormat(CodeStrings.CLOUD_DATEFORMAT);
				lowerBoundString = dateFormater.format((Date) lowerBound);
				upperBoundString = dateFormater.format((Date) upperBound);
			}

			String whereClause = fieldName + CodeStrings.IS_GREATER_OR_EQUAL_TO
					+ lowerBoundString + CodeStrings.AND + fieldName + CodeStrings.IS_LESS_OR_EQUAL_TO
					+ upperBoundString + CodeStrings.SINGLE_QUOTE;
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

	public void addUser(User user, OperationCallBack<BackendlessUser> operationCallBack) {

		Backendless.UserService.register(user,
				new BackendAsynCallBack<BackendlessUser>(operationCallBack));
	}

	public void updateUser(User user, OperationCallBack<BackendlessUser> operationCallBack){
		Backendless.UserService.update(user, new BackendAsynCallBack<>(operationCallBack));
	}

	public void login(final User user, OperationCallBack<BackendlessUser> operationCallBack){

		Backendless.UserService.login(user.getDeviceId(), user.getPassword(),
				new BackendAsynCallBack<>(operationCallBack));

	}

	private class BackendAsynCallBack<T> implements AsyncCallback<T> {

		private final OperationCallBack<T> operationCallBack;

		public BackendAsynCallBack(OperationCallBack<T> operationCallBack) {
			this.operationCallBack = operationCallBack;
		}

		@Override
		public void handleFault(BackendlessFault fault) {
			if(operationCallBack !=null) {
				operationCallBack.onOperationFailed(new MelanieDataLayerException(
						fault.getMessage()));
			}
		}

		@Override
		public void handleResponse(T responseObject) {
			if (operationCallBack != null)
				if (responseObject instanceof BackendlessCollection) {
					List<T> responseData = ((BackendlessCollection<T>) responseObject)
							.getData();
					if (!isCollectionOperation) {
						T item = responseData.size() > 0 ? responseData.get(0)
								: null;
						operationCallBack.onOperationSuccessful(item);
					} else {
						collectionRequestsCount --;
						operationCallBack
						.onCollectionOperationSuccessful(responseData);
					}
				} else {
					operationCallBack.onOperationSuccessful(responseObject);
				}
			isCollectionOperation = collectionRequestsCount > 0 ? isCollectionOperation : false;
		}
	}
}

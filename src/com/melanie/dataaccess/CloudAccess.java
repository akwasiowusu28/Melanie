package com.melanie.dataaccess;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.melanie.dataaccess.datasource.DataSourceManager;
import com.melanie.entities.User;
import com.melanie.support.OperationCallBack;
import com.melanie.support.exceptions.MelanieDataLayerException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unchecked")
public class CloudAccess {

    private boolean isCollectionOperation;
    //This is to make sure the right objects are passed to the callback method in case there are multiple request that return collections.
    // Increment this any time you run a request that has a potential of returning a collection
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
                    new BackendAsynCallBack<>(operationCallBack));
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
            findItemByFieldName(LocalConstants.ID, String.valueOf(itemId), itemClass,
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
                            (AsyncCallback<BackendlessCollection<T>>) new BackendAsynCallBack<>(
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
            collectionRequestsCount++;
            BackendlessDataQuery query = new BackendlessDataQuery();

            if (itemClass != BackendlessUser.class) {

                QueryOptions queryOptions = new QueryOptions();
                queryOptions.addSortByOption("created desc");
                queryOptions.setPageSize(100);
                query.setQueryOptions(queryOptions);
            }

            Backendless.Persistence
                    .of(itemClass)
                    .find(query, (AsyncCallback<BackendlessCollection<T>>) new BackendAsynCallBack<>(
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
                    new BackendAsynCallBack<>(operationCallBack));

        } catch (Exception e) {
            throw new MelanieDataLayerException(e.getMessage(), e);
        }
    }

    public <T> void findItemsByFieldName(String fieldName, String searchValue,
                                         Class<T> itemClass, OperationCallBack<T> operationCallBack)
            throws MelanieDataLayerException {
        try {
            isCollectionOperation = true;
            collectionRequestsCount++;
            String whereClause = fieldName + "='" + searchValue + "'";
            BackendlessDataQuery query = new BackendlessDataQuery(whereClause);

            QueryOptions queryOptions = new QueryOptions();
            queryOptions.addSortByOption("created desc");
            queryOptions.setPageSize(100);
            query.setQueryOptions(queryOptions);

            Backendless.Persistence
                    .of(itemClass)
                    .find(query,
                            (AsyncCallback<BackendlessCollection<T>>) new BackendAsynCallBack<>(
                                    operationCallBack));
        } catch (Exception e) {
            throw new MelanieDataLayerException(e.getMessage(), e);
        }
    }

    public <T> void findItemsByWhereClause(String whereClause,
                                           Class<T> itemClass, OperationCallBack<T> operationCallBack)
            throws MelanieDataLayerException {
        try {
            isCollectionOperation = true;
            collectionRequestsCount++;
            BackendlessDataQuery query = new BackendlessDataQuery(whereClause);

            QueryOptions queryOptions = new QueryOptions();
            queryOptions.addSortByOption("created desc");
            queryOptions.setPageSize(100);
            query.setQueryOptions(queryOptions);

            Backendless.Persistence
                    .of(itemClass)
                    .find(query,
                            (AsyncCallback<BackendlessCollection<T>>) new BackendAsynCallBack<>(
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
            collectionRequestsCount++;

            // Find a way to remove the date formatting thingy to a different
            // method. I'm programming under the influence of a 9.5% Alc double
            // IPA beer. I know I'm not thinking straight right now
            // 4/24/2015 10:18 PM

            String lowerBoundString = "";
            String upperBoundString = "";

            if (lowerBound instanceof Date && upperBound instanceof Date) {
                SimpleDateFormat dateFormater = new SimpleDateFormat(LocalConstants.DATEFORMAT);
                lowerBoundString = dateFormater.format((Date) lowerBound);
                upperBoundString = dateFormater.format((Date) upperBound);
            }

            String whereClause = fieldName + LocalConstants.IS_GREATER_OR_EQUAL_TO
                    + lowerBoundString + LocalConstants.AND + fieldName + LocalConstants.IS_LESS_OR_EQUAL_TO
                    + upperBoundString + LocalConstants.SINGLE_QUOTE;
            BackendlessDataQuery query = new BackendlessDataQuery(whereClause);

            QueryOptions queryOptions = new QueryOptions();
            queryOptions.addSortByOption("created desc");
            queryOptions.setPageSize(100);
            query.setQueryOptions(queryOptions);

            Backendless.Persistence
                    .of(itemClass)
                    .find(query,
                            (AsyncCallback<BackendlessCollection<T>>) new BackendAsynCallBack<>(
                                    operationCallBack));
        } catch (Exception e) {
            throw new MelanieDataLayerException(e.getMessage(), e);
        }

    }

    public void addUser(User user, OperationCallBack<BackendlessUser> operationCallBack) {

        Backendless.UserService.register(user,
                new BackendAsynCallBack<>(operationCallBack));
    }

    public void updateUser(User user, OperationCallBack<BackendlessUser> operationCallBack) {
        Backendless.UserService.update(user, new BackendAsynCallBack<>(operationCallBack));
    }

    public void login(final User user, OperationCallBack<BackendlessUser> operationCallBack) {

        Backendless.UserService.login(user.getDeviceId(), user.getPassword(),
                new BackendAsynCallBack<>(operationCallBack));

    }

    private class LocalConstants {
        public static final String ID = "Id";
        public static final String DATEFORMAT = "yyyyMMddHHmmss";
        public static final String IS_GREATER_OR_EQUAL_TO = ">= '";
        public static final String AND = "' and ";
        public static final String IS_LESS_OR_EQUAL_TO = "<='";
        public static final String SINGLE_QUOTE = "'";
    }

    private class BackendAsynCallBack<T> implements AsyncCallback<T> {

        private final OperationCallBack<T> operationCallBack;

        public BackendAsynCallBack(OperationCallBack<T> operationCallBack) {
            this.operationCallBack = operationCallBack;
        }

        @Override
        public void handleFault(BackendlessFault fault) {
            if (operationCallBack != null) {
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
                        collectionRequestsCount--;
                        operationCallBack
                                .onCollectionOperationSuccessful(responseData);
                    }
                } else {
                    operationCallBack.onOperationSuccessful(responseObject);
                }
            isCollectionOperation = collectionRequestsCount > 0 && isCollectionOperation;
        }
    }
}

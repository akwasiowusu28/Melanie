package com.melanie.business.concrete;

import com.backendless.BackendlessUser;
import com.melanie.business.MelanieSession;
import com.melanie.business.UserController;
import com.melanie.dataaccess.CloudAccess;
import com.melanie.dataaccess.DataAccessLayer;
import com.melanie.entities.User;
import com.melanie.support.BusinessFactory;
import com.melanie.support.DataFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

import java.util.ArrayList;
import java.util.List;

public class UserControllerImpl implements UserController {

    private final DataAccessLayer dataAccess;
    private final CloudAccess cloudAccess;
    private final MelanieSession session;

    public UserControllerImpl() {
        session = BusinessFactory.getSession();
        dataAccess = DataFactory.makeDataAccess();
        cloudAccess = DataFactory.makeCloudAccess();
    }

    @Override
    public void createUser(String name, String phone, String password, String deviceId,
                           final OperationCallBack<User> operationCallBack) throws MelanieBusinessException {
        User user = new User(name, password, phone, deviceId, false);
        if (session.canConnectToCloud()) {
            try {
                dataAccess.addDataItem(user, User.class, operationCallBack);
            } catch (MelanieDataLayerException e) {
                throw new MelanieBusinessException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void loginSavedUser(final OperationCallBack<User> operationCallBack) throws MelanieBusinessException {
        // Since there's should be only one user on this device, find the user
        // with id 1 from cache
        if (session.canConnectToCloud()) {
            try {
                final User user = dataAccess.findItemById(1, User.class, null);
                if (user != null) {
                    cloudAccess.login(user, new OperationCallBack<BackendlessUser>() {

                        @Override
                        public void onOperationSuccessful(BackendlessUser result) {
                            user.setProperties(result.getProperties());
                            BusinessFactory.getSession().setUser(user);
                            operationCallBack.onOperationSuccessful(user);
                        }
                    });
                }
            } catch (MelanieDataLayerException e) {
                throw new MelanieBusinessException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void updateUser(User user, String field, Object value,
                           final OperationCallBack<OperationResult> operationCallBack) throws MelanieBusinessException {

        if (session.canConnectToCloud()) {
            try {
                if (user == null) {
                    user = getLocalUser();
                }
                user.setProperty(field, value);
                performUpdate(user, new OperationCallBack<BackendlessUser>() {

                    @Override
                    public void onOperationSuccessful(BackendlessUser result) {
                        if (operationCallBack != null) {
                            operationCallBack.onOperationSuccessful(OperationResult.SUCCESSFUL);
                        }
                    }

                    @Override
                    public void onOperationFailed(Throwable e) {
                        // for logging purposes
                        operationCallBack.onOperationFailed(e);
                        operationCallBack.onOperationSuccessful(OperationResult.FAILED);
                    }
                });

            } catch (MelanieDataLayerException e) {
                throw new MelanieBusinessException(e.getMessage(), e);
            }
        }
    }

    private void performUpdate(final User user, OperationCallBack<BackendlessUser> operationCallBack)
            throws MelanieDataLayerException {
        if (session.canConnectToCloud() && user != null) {
            addUserToLocalDataStore(user);
            cloudAccess.updateUser(user, operationCallBack);
        }
    }

    @Override
    public boolean localUserExists() throws MelanieBusinessException {

        return getLocalUser() != null;

    }

    @Override
    public User getLocalUser() throws MelanieBusinessException {
        User user = null;
        try {
            if (session.canConnectToCloud()) {
                user = dataAccess.findItemById(1, User.class, null);
            }
        } catch (MelanieDataLayerException e) {
            throw new MelanieBusinessException(e.getMessage(), e);
        }
        return user;
    }

    @Override
    public void login(final User user, final OperationCallBack<OperationResult> operationCallBack) {
        if (session.canConnectToCloud()) {
            cloudAccess.login(user, new OperationCallBack<BackendlessUser>() {

                @Override
                public void onOperationSuccessful(BackendlessUser result) {
                    user.setProperties(result.getProperties());
                    MelanieSessionImpl.getInstance().setUser(user);

                    try {
                        if (!localUserExists()) {
                            user.setConfirmed((boolean) user.getProperty(LocalConstants.IS_CONFIRMED));
                            addUserToLocalDataStore(user);
                        }
                    } catch (MelanieDataLayerException | MelanieBusinessException e) {
                        operationCallBack.onOperationFailed(e);
                    }

                    operationCallBack.onOperationSuccessful(OperationResult.SUCCESSFUL);
                }

                @Override
                public void onOperationFailed(Throwable e) {
                    // for logging purposes
                    operationCallBack.onOperationFailed(e);
                    operationCallBack.onOperationSuccessful(OperationResult.FAILED);
                }
            });
        }
    }

    @Override
    public void checkPhoneExistOnCloud(String phone, final OperationCallBack<User> operationCallBack)
            throws MelanieBusinessException {
        if (session.canConnectToCloud()) {
            try {
                cloudAccess.findItemByFieldName(LocalConstants.PHONE, phone, BackendlessUser.class,
                        new OperationCallBack<BackendlessUser>() {

                            @Override
                            public void onOperationSuccessful(BackendlessUser result) {
                                User user = constructUserFromBackendless(result);
                                operationCallBack.onOperationSuccessful(user);
                            }

                            @Override
                            public void onOperationFailed(Throwable e) {
                                // for logging purposes
                                operationCallBack.onOperationFailed(e);
                                // return null to caller if error occured
                                operationCallBack.onOperationSuccessful(null);
                            }
                        });
            } catch (MelanieDataLayerException e) {
                throw new MelanieBusinessException(e.getMessage(), e);
            }
        }

    }

    private void addUserToLocalDataStore(User user) throws MelanieDataLayerException {
        dataAccess.addOrUpdateItemLocalOnly(user, User.class);
    }

    private User constructUserFromBackendless(BackendlessUser backendlessUser) {
        User user = null;

        if (backendlessUser != null) {
            String objectId = backendlessUser.getProperty(LocalConstants.OBJECT_ID).toString();
            String name = backendlessUser.getProperty(LocalConstants.NAME).toString();
            String phone = backendlessUser.getProperty(LocalConstants.PHONE).toString();
            String deviceId = backendlessUser.getProperty(LocalConstants.DEVICE_ID).toString();
            boolean isConfirmed = Boolean.getBoolean(backendlessUser.getProperty(LocalConstants.IS_CONFIRMED).toString());

            user = new User(name, null, phone, deviceId, isConfirmed);
            user.setObjectId(objectId);
            user.setProperties(backendlessUser.getProperties());

        }

        return user;
    }

    @Override
    public void getAllUsers(final OperationCallBack<User> operationCallBack) throws MelanieBusinessException {
        try {

            cloudAccess.findAllItems(BackendlessUser.class, new OperationCallBack<BackendlessUser>(){
                @Override
                public void onCollectionOperationSuccessful(List<BackendlessUser> results) {
                    List<User> users = new ArrayList<User>();
                    for(BackendlessUser backendlessUser : results){
                        users.add(constructUserFromBackendless(backendlessUser));
                    }
                   operationCallBack.onCollectionOperationSuccessful(users);
                }
            });

        } catch (MelanieDataLayerException ex) {
            throw new MelanieBusinessException(ex.getMessage(), ex);
        }
    }

    private class LocalConstants {
        public static final String NAME = "name";
        public static final String OBJECT_ID = "objectId";
        public static final String DEVICE_ID = "deviceid";
        public static final String IS_CONFIRMED = "isconfirmed";
        public static final String PHONE = "phone";
    }
}

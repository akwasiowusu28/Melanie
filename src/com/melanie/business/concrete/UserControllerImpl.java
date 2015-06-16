package com.melanie.business.concrete;

import com.backendless.BackendlessUser;
import com.melanie.business.MelanieSession;
import com.melanie.business.UserController;
import com.melanie.dataaccesslayer.CloudAccess;
import com.melanie.dataaccesslayer.DataAccessLayer;
import com.melanie.entities.User;
import com.melanie.support.BusinessFactory;
import com.melanie.support.DataFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

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
        if (session.canConnectToCloud() && dataAccess != null) {
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
        if (session.canConnectToCloud() && dataAccess != null) {
            try {
                final User user = dataAccess.findItemById(1, User.class, null);
                if (user != null && cloudAccess != null) {
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

        if (session.canConnectToCloud() && dataAccess != null) {
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
        if (session.canConnectToCloud() && user != null && cloudAccess != null) {
            addUserToLocalDataStore(user);
            cloudAccess.updateUser(user, operationCallBack);
        }
    }

    @Override
    public boolean localUserExists() throws MelanieBusinessException {
        boolean userExists = false;

        userExists = getLocalUser() != null;

        return userExists;
    }

    @Override
    public User getLocalUser() throws MelanieBusinessException {
        User user = null;
        try {
            if (session.canConnectToCloud() && dataAccess != null) {
                user = dataAccess.findItemById(1, User.class, null);
            }
        } catch (MelanieDataLayerException e) {
            throw new MelanieBusinessException(e.getMessage(), e);
        }
        return user;
    }

    @Override
    public void login(final User user, final OperationCallBack<OperationResult> operationCallBack) {
        if (session.canConnectToCloud() && cloudAccess != null) {
            cloudAccess.login(user, new OperationCallBack<BackendlessUser>() {

                @Override
                public void onOperationSuccessful(BackendlessUser result) {
                    user.setProperties(result.getProperties());
                    MelanieSessionImpl.getInstance().setUser(user);

                    try {
                        if (!localUserExists()) {
                            user.setConfirmed((boolean) user.getProperty(LocalConstants.ISCONFIRMED));
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
        if (session.canConnectToCloud() && cloudAccess != null) {
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
        if (dataAccess != null) {
            dataAccess.addOrUpdateItemLocalOnly(user, User.class);
        }
    }

    private User constructUserFromBackendless(BackendlessUser backendlessUser) {
        User user = null;

        if (backendlessUser != null) {
            String objectId = backendlessUser.getProperty(LocalConstants.OBJECTID).toString();
            String name = backendlessUser.getProperty(LocalConstants.NAME).toString();
            String phone = backendlessUser.getProperty(LocalConstants.PHONE).toString();
            String deviceId = backendlessUser.getProperty(LocalConstants.DEVICEID).toString();
            boolean isConfirmed = Boolean.getBoolean(backendlessUser.getProperty(LocalConstants.ISCONFIRMED).toString());

            user = new User(name, null, phone, deviceId, isConfirmed);
            user.setObjectId(objectId);
            user.setProperties(backendlessUser.getProperties());

        }

        return user;
    }

    private class LocalConstants {
        public static final String NAME = "name";
        public static final String OBJECTID = "objectId";
        public static final String DEVICEID = "deviceid";
        public static final String ISCONFIRMED = "isconfirmed";
        public static final String PHONE = "phone";
    }
}

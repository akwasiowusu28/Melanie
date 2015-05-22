package com.melanie.business.concrete;

import com.backendless.BackendlessUser;
import com.melanie.business.UserController;
import com.melanie.dataaccesslayer.MelanieCloudAccess;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.User;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieDataFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

public class UserControllerImpl implements UserController {

	private static final String NAME = "name";
	private static final String DEVICEID = "deviceid";
	private static final String ISCONFIRMED = "isconfirmed";
	private static final String PHONE = "phone";
	private static final String OBJECTID = "objectid";

	private final MelanieDataAccessLayer dataAccess;
	private final MelanieCloudAccess cloudAccess;

	public UserControllerImpl() {
		dataAccess = MelanieDataFactory.makeDataAccess();
		cloudAccess = MelanieDataFactory.makeCloudAccess();
	}

	@Override
	public void createUser(String name, String phone, String password,
			String deviceId,
			final MelanieOperationCallBack<User> operationCallBack)
			throws MelanieBusinessException {
		User user = new User(name, password, phone, deviceId, false);
		if (dataAccess != null) {
			try {
				dataAccess.addDataItem(user, User.class, operationCallBack);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}

	}

	@Override
	public void loginSavedUser() throws MelanieBusinessException {
		// Since there's should be only one user on this device, find the user
		// with id 1 from cache
		if (dataAccess != null) {
			try {
				final User user = dataAccess.findItemById(1, User.class, null);
				if (user != null && cloudAccess != null) {
					cloudAccess.login(user,
							new MelanieOperationCallBack<BackendlessUser>() {

								@Override
								public void onOperationSuccessful(
										BackendlessUser result) {
									MelanieBusinessFactory.getSession()
											.setUser(user);
								}
							});
				}
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
	}

	@Override
	public void updateUser(boolean isConfirmed) throws MelanieBusinessException {

		if (dataAccess != null) {
			final User user;
			try {
				user = getLocalUser();
				if (user != null && cloudAccess != null) {
					cloudAccess.login(user,
							new MelanieOperationCallBack<BackendlessUser>() {

								@Override
								public void onOperationSuccessful(
										BackendlessUser result) {
									user.setProperties(result.getProperties());
									user.setProperty(ISCONFIRMED, true);
									cloudAccess
											.updateUser(
													user,
													new MelanieOperationCallBack<BackendlessUser>());
								}
							});
				}
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
	}

	@Override
	public boolean localUserExists() throws MelanieBusinessException {
		boolean userExists = false;
		try {
			userExists = getLocalUser() != null;
		} catch (MelanieDataLayerException e) {
			throw new MelanieBusinessException(e.getMessage(), e);
		}
		return userExists;
	}

	private User getLocalUser() throws MelanieDataLayerException {
		User user = null;
		try {
			if (dataAccess != null) {
				user = dataAccess.findItemById(1, User.class, null);
			}
		} catch (MelanieDataLayerException e) {
			throw new MelanieDataLayerException(e.getMessage(), e);
		}
		return user;
	}

	@Override
	public void login(final User user, final MelanieOperationCallBack<OperationResult> operationCallBack) {
       if(cloudAccess != null){
    	   cloudAccess.login(user, new MelanieOperationCallBack<BackendlessUser>(){

			@Override
			public void onOperationSuccessful(BackendlessUser result) {
				MelanieSessionImpl.getInstance().setUser(user);
				operationCallBack.onOperationSuccessful(OperationResult.SUCCESSFUL);
			   }

			@Override
			public void onOperationFailed(Throwable e) {
				operationCallBack.onOperationFailed(e); // for logging purposes
				operationCallBack.onOperationSuccessful(OperationResult.FAILED);
			}
    	   });
       }
	}

	@Override
	public void checkUserExistOnCloud(String phone,
			final MelanieOperationCallBack<User> operationCallBack)
			throws MelanieBusinessException {
		if (cloudAccess != null) {
			try {
				cloudAccess.findItemByFieldName(PHONE, phone,
						BackendlessUser.class,
						new MelanieOperationCallBack<BackendlessUser>() {

							@Override
							public void onOperationSuccessful(
									BackendlessUser result) {

								operationCallBack
										.onOperationSuccessful(constructUserFromBackendless(result));
							}

							@Override
							public void onOperationFailed(Throwable e) {
								//TODO log it
								operationCallBack.onOperationSuccessful(null); // return null to caller if user not found or any error occured
							}
						});
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}

	}

	private User constructUserFromBackendless(BackendlessUser backendlessUser) {
		String objectId = backendlessUser.getProperty(OBJECTID).toString();
		String name = backendlessUser.getProperty(NAME).toString();
		String phone = backendlessUser.getProperty(PHONE).toString();
		String deviceId = backendlessUser.getProperty(DEVICEID).toString();
		boolean isConfirmed = Boolean.getBoolean(backendlessUser.getProperty(
				ISCONFIRMED).toString());
		User user = new User(name, null, phone, deviceId, isConfirmed);
		user.setObjectId(objectId);
		return user;
	}
}

package com.melanie.business.concrete;

import com.backendless.BackendlessUser;
import com.melanie.business.UserController;
import com.melanie.dataaccesslayer.MelanieCloudAccess;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.User;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieDataFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

public class UserControllerImpl implements UserController {

	private static final String IS_CONFIRMED = "isconfirmed";
	 private static final String PHONE = "phone";
	 private static final String NONE ="none";
	 
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
									user.setProperty(IS_CONFIRMED, true);
									cloudAccess.updateUser(user, new MelanieOperationCallBack<BackendlessUser>());
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
	public void login(String phone, String deviceId, String password) {
		
	}

	@Override
	public void checkUserExistOnCloud(String deviceId,
			final MelanieOperationCallBack<String> operationCallBack) {
		if(cloudAccess != null){
			cloudAccess.checkUserExistOnCloud(deviceId, new MelanieOperationCallBack<BackendlessUser>(){

				@Override
				public void onOperationSuccessful(BackendlessUser result) {
					operationCallBack.onOperationSuccessful(result.getProperty(PHONE).toString());
				}

				@Override
				public void onOperationFailed(Throwable e) {
					operationCallBack.onOperationSuccessful(NONE);
				}
			});
		}
		
	}
	
}

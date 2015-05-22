package com.melanie.business.concrete;

import com.backendless.BackendlessUser;
import com.melanie.business.UserController;
import com.melanie.dataaccesslayer.MelanieCloudAccess;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.User;
import com.melanie.support.CodeStrings;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieDataFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

public class UserControllerImpl implements UserController {

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
	public void loginSavedUser(
			final MelanieOperationCallBack<User> operationCallBack)
			throws MelanieBusinessException {
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
									operationCallBack
											.onOperationSuccessful(user);
								}
							});
				}
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
	}

	@Override
	public void updateUser(User user, String field, Object value)
			throws MelanieBusinessException {

		if (dataAccess != null) {
			try {
				if (user == null) {
					user = getLocalUser();
				}
				performUpdate(user, field, value);

			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
	}

	private void performUpdate(final User user, String field, Object value) {
		if (user != null && cloudAccess != null) {

			user.setProperty(field, value);
			cloudAccess.updateUser(user,
					new MelanieOperationCallBack<BackendlessUser>());
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
	public void login(final User user,
			final MelanieOperationCallBack<OperationResult> operationCallBack) {
		if (cloudAccess != null) {
			cloudAccess.login(user,
					new MelanieOperationCallBack<BackendlessUser>() {

						@Override
						public void onOperationSuccessful(BackendlessUser result) {
							MelanieSessionImpl.getInstance().setUser(user);
							operationCallBack
									.onOperationSuccessful(OperationResult.SUCCESSFUL);
						}

						@Override
						public void onOperationFailed(Throwable e) {
							operationCallBack.onOperationFailed(e); // for
																	// logging
																	// purposes
							operationCallBack
									.onOperationSuccessful(OperationResult.FAILED);
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
				cloudAccess.findItemByFieldName(CodeStrings.PHONE, phone,
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
								// TODO log it
								// return to call if error occured
								operationCallBack.onOperationSuccessful(null);
							}
						});
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}

	}

	private User constructUserFromBackendless(BackendlessUser backendlessUser) {
		String objectId = backendlessUser.getProperty(CodeStrings.OBJECTID)
				.toString();
		String name = backendlessUser.getProperty(CodeStrings.NAME).toString();
		String phone = backendlessUser.getProperty(CodeStrings.PHONE)
				.toString();
		String deviceId = backendlessUser.getProperty(CodeStrings.DEVICEID)
				.toString();
		boolean isConfirmed = Boolean.getBoolean(backendlessUser.getProperty(
				CodeStrings.ISCONFIRMED).toString());
		User user = new User(name, null, phone, deviceId, isConfirmed);
		user.setObjectId(objectId);
		return user;
	}
}

package com.melanie.business.concrete;

import com.backendless.BackendlessUser;
import com.melanie.business.UserController;
import com.melanie.dataaccesslayer.MelanieCloudAccess;
import com.melanie.entities.User;
import com.melanie.support.MelanieOperationCallBack;

public class UserControllerImpl implements UserController {

	@Override
	public void createUser(String name, String phone, String password,
			final MelanieOperationCallBack<User> operationCallBack) {
		User user = new User(name, password, phone);
		new MelanieCloudAccess().addUser(user,
				new MelanieOperationCallBack<BackendlessUser>() {

					@Override
					public void onOperationSuccessful(BackendlessUser result) {
						operationCallBack.onOperationSuccessful((User) result);
					}

					@Override
					public void onOperationFailed(Throwable e) {
						operationCallBack.onOperationFailed(e);
					}
				});
	}

	@Override
	public void login(String name, String password,
			MelanieOperationCallBack<User> operationCallBack) {
		// TODO Auto-generated method stub

	}

}

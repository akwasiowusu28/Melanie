package com.melanie.business;

import com.melanie.entities.User;
import com.melanie.support.MelanieOperationCallBack;

public interface UserController {

	void createUser(String name, String phone, String password,
			MelanieOperationCallBack<User> operationCallBack);

	void login(String name, String password,
			MelanieOperationCallBack<User> operationCallBack);

}

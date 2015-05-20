package com.melanie.business;

import com.melanie.entities.User;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public interface UserController {

	void createUser(String name, String phone, String password, String deviceId,
			MelanieOperationCallBack<User> operationCallBack) throws MelanieBusinessException;

	OperationResult login()  throws MelanieBusinessException;

	void updateUser(String phone, boolean isConfirmed) throws MelanieBusinessException;
}

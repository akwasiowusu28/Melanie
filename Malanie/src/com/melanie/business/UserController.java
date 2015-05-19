package com.melanie.business;

import com.melanie.entities.User;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

public interface UserController {

	void createUser(String name, String phone, String password,
			MelanieOperationCallBack<User> operationCallBack) throws MelanieBusinessException;

	OperationResult login(MelanieOperationCallBack<User> operationCallBack)  throws MelanieBusinessException;

	void updateUser(String phone, boolean isConfirmed) throws MelanieBusinessException;
}

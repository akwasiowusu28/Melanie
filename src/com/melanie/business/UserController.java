package com.melanie.business;

import com.melanie.entities.User;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;

public interface UserController {

    void createUser(String name, String phone, String password, String deviceId,
                    OperationCallBack<User> operationCallBack) throws MelanieBusinessException;

    void checkPhoneExistOnCloud(String phone, OperationCallBack<User> operationCallBack) throws MelanieBusinessException;

    void login(User user, OperationCallBack<OperationResult> operationCallBack);

    void loginSavedUser(OperationCallBack<User> operationCallBack) throws MelanieBusinessException;

    boolean localUserExists() throws MelanieBusinessException;

    void updateUser(User user, String field, Object value, OperationCallBack<OperationResult> operationCallBack) throws MelanieBusinessException;

    User getLocalUser() throws MelanieBusinessException;
}

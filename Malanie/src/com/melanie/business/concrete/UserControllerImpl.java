package com.melanie.business.concrete;

import com.melanie.business.UserController;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.User;
import com.melanie.support.MelanieDataFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

public class UserControllerImpl implements UserController {

	private MelanieDataAccessLayer dataAccess;
	
	public UserControllerImpl() {
		dataAccess = MelanieDataFactory.makeDataAccess();
	}
	
	@Override
	public void createUser(String name, String phone, String password,
			final MelanieOperationCallBack<User> operationCallBack) throws MelanieBusinessException {
		User user = new User(name, password, phone, false);
		if(dataAccess !=null){
			try {
				dataAccess.addDataItem(user, User.class, operationCallBack);
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
		
	}

	@Override
	public void login(String name, String password,
			MelanieOperationCallBack<User> operationCallBack) throws MelanieBusinessException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUser(String phone, boolean isConfirmed) throws MelanieBusinessException {
		// TODO Auto-generated method stub
		
	}

}

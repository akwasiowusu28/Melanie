package com.melanie.business.concrete;

import com.backendless.BackendlessUser;
import com.melanie.business.UserController;
import com.melanie.dataaccesslayer.MelanieCloudAccess;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.entities.User;
import com.melanie.support.MelanieDataFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

public class UserControllerImpl implements UserController {

	private MelanieDataAccessLayer dataAccess;
	private MelanieCloudAccess cloudAccess;
	
	public UserControllerImpl() {
		dataAccess = MelanieDataFactory.makeDataAccess();
		cloudAccess = MelanieDataFactory.makeCloudAccess();
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
	public OperationResult login(MelanieOperationCallBack<User> operationCallBack) throws MelanieBusinessException {
		OperationResult result = OperationResult.FAILED;
		//Since there's should be only one user on this device, find the user with id 1 from cache
		if(dataAccess !=null){
			try {
				User user = dataAccess.findItemById(1, User.class, null);
				if(user !=null && cloudAccess != null){
					cloudAccess.login(user, operationCallBack);
				   result = OperationResult.SUCCESSFUL;
				}
			} catch (MelanieDataLayerException e) {
				throw new MelanieBusinessException(e.getMessage(), e);
			}
		}
		return result;
	}

	@Override
	public void updateUser(String phone, boolean isConfirmed) throws MelanieBusinessException {
		try {
			if(dataAccess != null){
				User user = dataAccess.findItemById(1, User.class, null);
				user.setConfirmed(true);
				if(user != null && cloudAccess != null){
					cloudAccess.updateUser(user);
				}	
			}
		} catch (MelanieDataLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

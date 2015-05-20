package com.melanie.business.concrete;

import com.melanie.business.MelanieBusiness;
import com.melanie.dataaccesslayer.MelanieCloudAccess;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.support.MelanieDataFactory;

public class MelanieBusinessImpl implements MelanieBusiness {

	private static MelanieDataAccessLayer dataAccess = MelanieDataFactory.makeDataAccess();
	private static boolean isInitialized = false;
	
	@Override
	public <T> void initialize(T dataContext) {
		if (dataAccess != null)
			dataAccess.initialize(dataContext);
		isInitialized = true;
	}

	@Override
	public <T> void initializeAlternate(T dataContext) {
		MelanieCloudAccess.initialize(dataContext);
	}
	
	public static void clearResources() {
		if (isInitialized && dataAccess != null) {
			dataAccess.clearResources();
		}
	}

}

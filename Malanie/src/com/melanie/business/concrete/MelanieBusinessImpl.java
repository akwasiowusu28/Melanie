package com.melanie.business.concrete;

import com.melanie.business.MelanieBusiness;
import com.melanie.dataaccesslayer.MelanieCloudAccess;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.support.MelanieDataFactory;

public class MelanieBusinessImpl implements MelanieBusiness {

	private MelanieDataAccessLayer dataAccess = MelanieDataFactory.makeDataAccess();
	@Override
	public <T> void initialize(T dataContext) {
		if (dataAccess != null)
			dataAccess.initialize(dataContext);
	}

	@Override
	public <T> void initializeAlternate(T dataContext) {
		MelanieCloudAccess.initialize(dataContext);
	}

	@Override
	public void clearResources() {
		if (dataAccess != null) {
			dataAccess.clearResources();
		}
	}

}

package com.melanie.business.concrete;

import com.melanie.business.MelanieBusiness;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.support.MelanieDataFactory;

public class MelanieBusinessImpl implements MelanieBusiness {

	@Override
	public <T> void initialize(T dataContext) {
		MelanieDataAccessLayer dataAccess = MelanieDataFactory.makeDataAccess();
		if (dataAccess != null)
			dataAccess.initialize(dataContext);
	}

}

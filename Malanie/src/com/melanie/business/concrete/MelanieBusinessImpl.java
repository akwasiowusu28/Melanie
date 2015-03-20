package com.melanie.business.concrete;

import com.melanie.business.MelanieBusiness;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.dataaccesslayer.datasource.DataSource;
import com.melanie.support.MelanieDataFactory;

public class MelanieBusinessImpl implements MelanieBusiness{
	@Override
	public void initialize(DataSource dataSource) {
		MelanieDataAccessLayer dataAccess = MelanieDataFactory.makeDataAccessLayer();
		if(dataAccess != null){
			dataAccess.initialize(dataSource);
		}
		
	}

}

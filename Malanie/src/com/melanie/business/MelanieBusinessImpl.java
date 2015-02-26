package com.melanie.business;

import com.melanie.dataaccesslayer.MelanieDataAccessLayerImpl;
import com.melanie.dataaccesslayer.datasource.DataSource;

public class MelanieBusinessImpl implements MelanieBusiness{
	@Override
	public void initialize(DataSource dataSource) {
		new MelanieDataAccessLayerImpl().initialize(dataSource);
	}

}

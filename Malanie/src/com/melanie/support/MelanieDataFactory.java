package com.melanie.support;

import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.dataaccesslayer.MelanieDataAccessLayerImpl;

public class MelanieDataFactory {

	public static MelanieDataAccessLayer makeDataAccess(){
		return new MelanieDataAccessLayerImpl();
	}
}

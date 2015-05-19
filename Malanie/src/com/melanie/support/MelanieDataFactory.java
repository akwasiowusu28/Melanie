package com.melanie.support;

import com.melanie.dataaccesslayer.MelanieCloudAccess;
import com.melanie.dataaccesslayer.MelanieDataAccessLayer;
import com.melanie.dataaccesslayer.MelanieDataAccessLayerImpl;

public class MelanieDataFactory {

	public static MelanieDataAccessLayer makeDataAccess() {
		return new MelanieDataAccessLayerImpl();
	}
    
	public static MelanieCloudAccess makeCloudAccess(){
		return new MelanieCloudAccess();
	}
}

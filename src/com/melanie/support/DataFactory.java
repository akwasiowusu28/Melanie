package com.melanie.support;

import com.melanie.dataaccess.CloudAccess;
import com.melanie.dataaccess.DataAccessLayer;
import com.melanie.dataaccess.DataAccessLayerImpl;

public class DataFactory {

    public static DataAccessLayer makeDataAccess() {
        return new DataAccessLayerImpl();
    }

    public static CloudAccess makeCloudAccess() {
        return new CloudAccess();
    }
}

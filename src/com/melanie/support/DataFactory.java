package com.melanie.support;

import com.melanie.dataaccesslayer.CloudAccess;
import com.melanie.dataaccesslayer.DataAccessLayer;
import com.melanie.dataaccesslayer.DataAccessLayerImpl;

public class DataFactory {

    public static DataAccessLayer makeDataAccess() {
        return new DataAccessLayerImpl();
    }

    public static CloudAccess makeCloudAccess() {
        return new CloudAccess();
    }
}

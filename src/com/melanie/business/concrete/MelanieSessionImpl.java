package com.melanie.business.concrete;

import com.melanie.business.MelanieSession;
import com.melanie.dataaccess.CloudAccess;
import com.melanie.dataaccess.DataAccessLayer;
import com.melanie.entities.User;
import com.melanie.support.BusinessFactory;
import com.melanie.support.DataFactory;
import com.melanie.support.exceptions.MelanieBusinessException;

public class MelanieSessionImpl implements MelanieSession {

    private static MelanieSession instance;

    private static DataAccessLayer dataAccess;
    private static boolean isCacheInitialized;
    private User user;
    private boolean isInternetServiceAvailable;

    private MelanieSessionImpl() {
        initializeMelanieSession();
    }

    public static MelanieSession getInstance() {

        if (instance == null) {
            synchronized (MelanieSessionImpl.class) {
                if (instance == null) {
                    instance = new MelanieSessionImpl();
                }
            }
        }
        return instance;
    }

    private void initializeMelanieSession() {
        dataAccess = DataFactory.makeDataAccess();
        isCacheInitialized = false;
        isInternetServiceAvailable = true;
        user = null;
    }

    @Override
    public <T> void initializeLocal(T dataContext) {
        if (dataAccess != null) {
            dataAccess.initialize(dataContext);
        }
        isCacheInitialized = true;
    }

    @Override
    public <T> void initializeCloud(T dataContext) {
        CloudAccess.initialize(dataContext);
    }

    @Override
    public boolean isInitialized() {
        return isCacheInitialized;
    }

    @Override
    public boolean isUserLoggedIn() {
        return user != null;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean isUserRegisteredOnDevice() {
        boolean localUserExists = false;

        try {
            localUserExists = BusinessFactory.makeUserController().localUserExists();
        } catch (MelanieBusinessException e) {
            e.printStackTrace(); // TODO log it
        }

        return localUserExists;
    }

    @Override
    public void clearResources() {
        if (isCacheInitialized && dataAccess != null) {
            dataAccess.clearResources();
            isCacheInitialized = false;
        }
    }

    @Override
    public boolean canConnectToCloud() {
        return isInternetServiceAvailable;
    }

    @Override
    public void setCanConnectToCloud(boolean isInternetServiceAvailable) {
        this.isInternetServiceAvailable = isInternetServiceAvailable;
    }

}

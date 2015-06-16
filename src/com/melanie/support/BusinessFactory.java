package com.melanie.support;

import com.melanie.business.CustomersController;
import com.melanie.business.MelanieSession;
import com.melanie.business.ProductEntryController;
import com.melanie.business.SalesController;
import com.melanie.business.UserController;
import com.melanie.business.concrete.CustomersControllerImpl;
import com.melanie.business.concrete.MelanieSessionImpl;
import com.melanie.business.concrete.ProductEntryControllerImpl;
import com.melanie.business.concrete.SalesControllerImpl;
import com.melanie.business.concrete.UserControllerImpl;

public class BusinessFactory {

    public static MelanieSession getSession() {
        return MelanieSessionImpl.getInstance();
    }

    public static ProductEntryController makeProductEntryController() {
        return new ProductEntryControllerImpl();
    }

    public static SalesController makeSalesController() {
        return new SalesControllerImpl();
    }

    public static CustomersController makeCustomersController() {
        return new CustomersControllerImpl();
    }

    public static UserController makeUserController() {
        return new UserControllerImpl();
    }
}

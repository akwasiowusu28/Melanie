package com.melanie.business.concrete;

import com.melanie.business.CustomersController;
import com.melanie.business.MelanieSession;
import com.melanie.dataaccesslayer.DataAccessLayer;
import com.melanie.entities.Customer;
import com.melanie.support.BusinessFactory;
import com.melanie.support.DataFactory;
import com.melanie.support.OperationCallBack;
import com.melanie.support.OperationResult;
import com.melanie.support.exceptions.MelanieBusinessException;
import com.melanie.support.exceptions.MelanieDataLayerException;

import java.util.List;

/**
 * This is the concrete implementation of the Customers subsystem. Use this for
 * all operations pertaining to Customer information
 *
 * @author Akwasi Owusu
 */
public class CustomersControllerImpl implements CustomersController {

    private final DataAccessLayer dataAccess;
    private final MelanieSession session;

    public CustomersControllerImpl() {
        dataAccess = DataFactory.makeDataAccess();
        session = BusinessFactory.getSession();
    }

    @Override
    public OperationResult addCustomer(Customer customer) throws MelanieBusinessException {
        OperationResult result;
        try {
            result = dataAccess.addDataItem(customer, Customer.class, null);
        } catch (MelanieDataLayerException e) {
            throw new MelanieBusinessException(e.getMessage(), e);
        }
        return result;
    }

    /**
     * gets all the Customers
     *
     * @return list of all customers
     */
    @Override
    public List<Customer> getAllCustomers(OperationCallBack<Customer> operationCallBack)
            throws MelanieBusinessException {

        List<Customer> customers;
        try {

            customers = dataAccess.findAllItems(Customer.class, operationCallBack);

        } catch (MelanieDataLayerException e) {
            throw new MelanieBusinessException(e.getMessage(), e);
        }
        return customers;
    }

    @Override
    public OperationResult updateCustomer(Customer customer) throws MelanieBusinessException {
        OperationResult result = OperationResult.FAILED;

        if (session.canConnectToCloud()) {
            try {
                result = dataAccess.updateDataItem(customer, Customer.class);
            } catch (MelanieDataLayerException e) {
                throw new MelanieBusinessException(e.getMessage(), e);
            }
        }
        return result;
    }

    @Override
    public Customer findCustomer(int customerId, OperationCallBack<Customer> operationCallBack)
            throws MelanieBusinessException {

        Customer customer = null;

        try {
            if (session.canConnectToCloud()) {
                customer = dataAccess.findItemById(customerId, Customer.class, operationCallBack);
            }
        } catch (MelanieDataLayerException e) {
            throw new MelanieBusinessException(e.getMessage(), e);
        }

        return customer;
    }

    @Override
    public void addOrUpdateCustomerLocalOnly(Customer customer) throws MelanieBusinessException {
            try {
                dataAccess.addOrUpdateItemLocalOnly(customer, Customer.class);
            } catch (MelanieDataLayerException e) {
                throw new MelanieBusinessException(e.getMessage(), e);
            }
    }

    @Override
    public void getLastInsertedCustomerId(OperationCallBack<Integer> operationCallBack) throws MelanieBusinessException {

        if (session.canConnectToCloud()) {
            try {
                dataAccess.getLastInsertedId(Customer.class, operationCallBack);
            } catch (MelanieDataLayerException e) {
                throw new MelanieBusinessException(e.getMessage(), e);
            }
        }
    }

}

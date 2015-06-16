package com.melanie.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The Customer entity
 *
 * @author Akwasi Owusu
 */

@DatabaseTable
public class Customer extends BaseEntity {

    private static final long serialVersionUID = -3116268865194178178L;

    @DatabaseField
    private String name;

    @DatabaseField
    private String phoneNumber;

    @DatabaseField
    private double amountOwed;

    public Customer() {
    }

    public Customer(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getAmountOwed() {
        return amountOwed;
    }

    public void setAmountOwed(double amountOwed) {
        this.amountOwed = amountOwed;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object another) {
        boolean equal = false;

        if (this == another) {
            equal = true;
        } else if (another != null && another instanceof Customer) {
            Customer anotherCustomer = (Customer) another;
            equal = anotherCustomer.name.equals(name)
                    && anotherCustomer.phoneNumber.equals(phoneNumber);
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return name.hashCode() * phoneNumber.hashCode();
    }

}

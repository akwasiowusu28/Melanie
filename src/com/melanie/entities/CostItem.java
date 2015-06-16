package com.melanie.entities;

import com.j256.ormlite.field.DatabaseField;

public class CostItem extends BaseEntity {

    private static final long serialVersionUID = 8437795923968524879L;

    @DatabaseField
    private String name;

    public CostItem() {
        super();
    }

    public CostItem(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

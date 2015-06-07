package com.melanie.entities;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

public class CostEntry extends BaseEntity {

	private static final long serialVersionUID = -7952396841777977852L;

	@DatabaseField(columnName = "CostItemId", foreignAutoRefresh = true, canBeNull = true, foreign = true)
	private CostItem costItem;

	@DatabaseField
	private double value;

	@DatabaseField
	private Date entryDate;

	public CostEntry() {
		super();
	}

	public CostEntry(CostItem costItem, double value) {
		super();
		this.costItem = costItem;
		this.value = value;
	}

	public CostItem getCostItem() {
		return costItem;
	}

	public void setCostItem(CostItem costItem) {
		this.costItem = costItem;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}
}

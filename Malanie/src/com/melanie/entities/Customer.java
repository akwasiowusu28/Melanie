package com.melanie.entities;

import com.j256.ormlite.table.DatabaseTable;

/**
 * The Customer entity
 * @author Akwasi Owusu
 *
 */

@DatabaseTable
public class Customer extends BaseEntity {
	
	private String name;
	
	private String phoneNumber;
	
	private double amountOwed;

	public Customer(){}
	
	public Customer(String name, String phoneNumber){
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

}

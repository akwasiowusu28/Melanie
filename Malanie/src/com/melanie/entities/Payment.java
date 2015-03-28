package com.melanie.entities;

import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *  The payment entity
 * @author Akwasi Owusu
 *
 */

@DatabaseTable
public class Payment extends BaseEntity {

	
	@DatabaseField
	private int paymentDate;
	
	@DatabaseField
	private double amountReceived;
	
	@DatabaseField
	private double balance;
	
	@DatabaseField(canBeNull= true, foreign = true)
	private Customer customer;
	
	public Payment() {
		super();
	}
	
	public int getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(int paymentDate) {
		this.paymentDate = paymentDate;
	}
	public double getAmountReceived() {
		return amountReceived;
	}
	public void setAmountReceived(double amountReceived) {
		this.amountReceived = amountReceived;
	}
	
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
}

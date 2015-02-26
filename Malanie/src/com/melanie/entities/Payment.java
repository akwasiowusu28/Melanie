package com.melanie.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Payment extends BaseEntity {

	@DatabaseField
	private int paymentType;
	
	@DatabaseField
	private int paymentDate;
	
	@DatabaseField
	private double amountReceived;
	
	@DatabaseField
	private double amountOfChange;
	
	@DatabaseField
	private double balance;
	
	@DatabaseField(canBeNull= true, foreign = true)
	private Sale sale;
	
	public Payment() {
		super();
	}
	
	public int getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
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
	public double getAmountOfChange() {
		return amountOfChange;
	}
	public void setAmountOfChange(double amountOfChange) {
		this.amountOfChange = amountOfChange;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public Sale getSale() {
		return sale;
	}
	public void setSale(Sale sale) {
		this.sale = sale;
	}
	
}

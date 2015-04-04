package com.melanie.entities;

import java.util.Collection;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The payment entity
 * 
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

	@DatabaseField
	private double discount;

	@DatabaseField(columnName = "CustomerId", canBeNull = true, foreign = true)
	private Customer customer;

	@ForeignCollectionField
	Collection<Sale> sales; // Change to List<Sale> when using with backendless

	public Payment() {
		super();
	}

	public Payment(Customer customer, List<Sale> sales, double amountReceived,
			double discount, double balance) {
		super();
		this.customer = customer;
		this.sales = sales;
		this.amountReceived = amountReceived;
		this.discount = discount;
		this.balance = balance;
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

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Collection<Sale> getSales() {
		return sales;
	}

	public void setSales(Collection<Sale> sales) {
		this.sales = sales;
	}

}

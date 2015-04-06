package com.melanie.entities;

import java.util.Collection;
import java.util.Date;
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
	private Date paymentDate;

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
		paymentDate = new Date();
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
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

	@Override
	public boolean equals(Object another) {
		boolean equals = false;
		if (this == another)
			equals = true;
		else if (another instanceof Payment) {
			Payment anotherPayment = (Payment) another;
			boolean dateEquals = anotherPayment.paymentDate.equals(paymentDate);
			boolean amountEquals = anotherPayment.amountReceived == amountReceived;
			boolean customerEquals = anotherPayment.customer.equals(customer);
			boolean salesEquals = true;
			if (anotherPayment.sales.size() == sales.size()) {
				Sale[] thisPaymentSales = (Sale[]) sales.toArray();
				Sale[] anotherPaymentSales = (Sale[]) anotherPayment.sales
						.toArray();
				for (int i = 0; i < thisPaymentSales.length; i++)
					if (!thisPaymentSales[i].equals(anotherPaymentSales[i])) {
						salesEquals = false;
						break;
					}

			} else
				salesEquals = false;

			equals = dateEquals && amountEquals && salesEquals
					&& customerEquals;
		}
		return equals;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + paymentDate.hashCode();
		hash = hash * 31 + (customer == null ? 0 : customer.hashCode());
		return hash;
	}

}

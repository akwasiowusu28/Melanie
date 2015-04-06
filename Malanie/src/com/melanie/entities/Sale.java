package com.melanie.entities;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The Sale entity
 * 
 * @author Akwasi Owusu
 * 
 */
@DatabaseTable
public class Sale extends BaseEntity {

	@DatabaseField
	private Date saleDate;

	@DatabaseField(canBeNull = true, foreign = true)
	private Product product;

	@DatabaseField
	private int quantitySold;

	@DatabaseField(columnName = "CustomerId", canBeNull = true, foreign = true)
	private Customer customer;

	@DatabaseField(columnName = "PaymentId", canBeNull = true, foreign = true)
	private Payment payment;

	public Sale() {
		super();
	}

	public Date getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(Date saleDate) {
		this.saleDate = saleDate;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantitySold() {
		return quantitySold;
	}

	public void setQuantitySold(int quantitySold) {
		this.quantitySold = quantitySold;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	@Override
	public boolean equals(Object another) {
		boolean equals = false;
		if (this == another)
			equals = true;
		else if (another instanceof Product) {
			Sale anotherSale = (Sale) another;
			equals = anotherSale.customer.equals(customer)
					&& anotherSale.product.equals(product)
					&& anotherSale.quantitySold == quantitySold
					&& anotherSale.saleDate.equals(saleDate);
		}
		return equals;
	}

	@Override
	public int hashCode() {
		int hashCode = 1;
		hashCode = hashCode * 31 + saleDate.hashCode();
		hashCode = hashCode * 31 + (customer == null ? 0 : customer.hashCode());
		return hashCode;
	}

}

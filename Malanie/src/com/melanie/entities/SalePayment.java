package com.melanie.entities;

import com.j256.ormlite.field.DatabaseField;

public class SalePayment extends BaseEntity {

	private static final String SALE_ID = "SaleId";
	private static final String PAYMENT_ID = "PaymentId";

	@DatabaseField(columnName = SALE_ID, foreign = true, foreignAutoCreate = true)
	private Sale sale;

	@DatabaseField(columnName = PAYMENT_ID, foreign = true, foreignAutoCreate = true)
	private Payment payment;

	public SalePayment() {
		super();
	}

	public SalePayment(Sale sale, Payment payment) {
		super();
		this.sale = sale;
		this.payment = payment;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

}

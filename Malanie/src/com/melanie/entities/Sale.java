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

	@DatabaseField
	private double discount;

	public Sale() {
		super();
	}

	public Date getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(Date saleDate) {
		this.saleDate = saleDate;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
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

}

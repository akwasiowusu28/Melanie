package com.melanie.entities;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

/**
 * The Sale entity
 * @author Akwasi Owusu
 *
 */
public class Sale extends BaseEntity {

	@DatabaseField
	private Date saleDate;
	
	@DatabaseField(canBeNull= true, foreign = true)
	private Product product;
	
	@DatabaseField
	private int quantitySold;
	
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
	
	
}

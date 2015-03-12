package com.melanie.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The Product entity 
 * @author Akwasi Owusu
 *
 */

@DatabaseTable
public class Product extends BaseEntity {

	@DatabaseField
	private String productName;
	
	@DatabaseField
	private int quantity;
	
	@DatabaseField
	private double price;
	
	@DatabaseField
	private long barcodeNumber;
	
	@DatabaseField(canBeNull= true, foreign = true)
	private Category category;
	
	public Product() {
		super();
	}
	
	public Product(String productName, int quantity, double price,
			Category category) {
		super();
		this.productName = productName;
		this.quantity = quantity;
		this.price = price;
		this.category = category;
	}

	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public long getBarcodeNumber() {
		return barcodeNumber;
	}
	public void setBarcodeNumber(long barcodeNumber) {
		this.barcodeNumber = barcodeNumber;
	}
	public Category getProductCategory() {
		return category;
	}
	public void setProductCategory(Category category) {
		this.category = category;
	}
	
	
}

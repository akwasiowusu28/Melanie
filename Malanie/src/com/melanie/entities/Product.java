package com.melanie.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The Product entity
 * 
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
	private String barcode;

	@DatabaseField(columnName = "CategoryId", canBeNull = true, foreign = true)
	private Category category;

	public Product() {
		super();
	}

	public Product(String productName, int quantity, double price,
			Category category, String barcode) {
		super();
		this.productName = productName;
		this.quantity = quantity;
		this.price = price;
		this.category = category;
		this.barcode = barcode;
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

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}

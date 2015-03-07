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
	private ProductCategory productCategory;
	
	public Product() {
		super();
	}
	
	public Product(String productName, int quantity, double price,
			ProductCategory productCategory) {
		super();
		this.productName = productName;
		this.quantity = quantity;
		this.price = price;
		this.productCategory = productCategory;
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
	public ProductCategory getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}
	
	
}

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
	private String barcode;
	
	@DatabaseField(canBeNull= true, foreign = true)
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
	public String getBarcodeNumber() {
		return barcode;
	}
	public void setBarcodeNumber(String barcodeNumber) {
		this.barcode = barcodeNumber;
	}
	public Category getProductCategory() {
		return category;
	}
	public void setProductCategory(Category category) {
		this.category = category;
	}
	
	@Override
	public boolean equals(Object obj){
		boolean equals = false;
		
		if(obj instanceof Product){
			Product product = (Product)obj;
			equals = product.barcode.equals(this.barcode);
		}
		return equals;
	}
	
	@Override
	public int hashCode(){
		return super.hashCode();
	}
}

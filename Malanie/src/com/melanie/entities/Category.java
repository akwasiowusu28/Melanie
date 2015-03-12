package com.melanie.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The Category entity
 * @author Akwasi Owusu
 *
 */

@DatabaseTable
public class Category extends BaseEntity {

	public Category() {
		super();
	}
    
	public Category(String categoryName) {
		super();
		this.categoryName = categoryName;
	}
	
	@DatabaseField
	private String categoryName;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public String toString(){
		return this.categoryName;
	}
}

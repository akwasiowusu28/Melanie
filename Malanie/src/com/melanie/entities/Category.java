package com.melanie.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The Category entity
 * 
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
	public String toString() {
		return categoryName;
	}

	@Override
	public boolean equals(Object another) {
		boolean equal = false;

		if (this == another)
			equal = true;
		else if (another != null && another instanceof Category) {
			Category anotherCategory = (Category) another;
			equal = anotherCategory.categoryName.equals(categoryName);
		}
		return equal;
	}

	@Override
	public int hashCode() {
		return 31 * categoryName.hashCode();
	}

}

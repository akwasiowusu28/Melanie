package com.melanie.entities;

import com.j256.ormlite.field.DatabaseField;

/**
 * The parent class of all entities
 * 
 * @author Akwasi Owusu
 * 
 */
public abstract class BaseEntity {

	public BaseEntity() {
	}

	@DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
	private int id;

	public int getId() {
		return id;
	}

	private String objectId;

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
}

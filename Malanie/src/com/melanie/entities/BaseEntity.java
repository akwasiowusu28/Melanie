package com.melanie.entities;

import com.j256.ormlite.field.DatabaseField;

public abstract class BaseEntity {

	public BaseEntity(){}
	
	@DatabaseField(generatedId= true, allowGeneratedIdInsert = true)
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

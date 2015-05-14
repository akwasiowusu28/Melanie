package com.melanie.entities;

import com.backendless.BackendlessUser;
import com.j256.ormlite.field.DatabaseField;

public class User extends BackendlessUser {

	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
	private int id;

	@DatabaseField
	private String objectId;

	public User() {
		super();
	}

	public User(String name, String password, String phone) {
		super();
		setPassword(password);
		setProperty("name", name);
		setProperty("phone", phone);
	}

	@Override
	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

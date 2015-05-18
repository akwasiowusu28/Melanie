package com.melanie.entities;

import com.backendless.BackendlessUser;
import com.j256.ormlite.field.DatabaseField;

public class User extends BackendlessUser {

	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
	private int id;

	@DatabaseField
	private String objectId;

	@DatabaseField
	private boolean isConfirmed;
	
	public User() {
		super();
	}

	public User(String name, String password, String phone, boolean isConfirmed) {
		super();
		setPassword(password);
		setProperty("name", name);
		setProperty("phone", phone);
		setProperty("isconfirmed", isConfirmed);
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

	public boolean isConfirmed() {
		return isConfirmed;
	}

	public void setConfirmed(boolean isConfirmed) {
		this.isConfirmed = isConfirmed;
	}
}

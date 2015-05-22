package com.melanie.entities;

import com.backendless.BackendlessUser;
import com.j256.ormlite.field.DatabaseField;
import com.melanie.support.CodeStrings;

public class User extends BackendlessUser {

	private static final long serialVersionUID = 1L;
	
	@DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
	private int id;

	@DatabaseField
	private String objectId;

	@DatabaseField
	private boolean isConfirmed;
	
	@DatabaseField
	private String password;
	
	@DatabaseField
	private String name;
	
	@DatabaseField
	private String deviceId;
	
	public User() {
		super();
	}

	public User(String name, String password, String phone, String deviceId, boolean isConfirmed) {
		super();
		this.password = password;
		this.name = name;
		this.deviceId = deviceId;
		
		setProperty(CodeStrings.NAME, name);
		setProperty(CodeStrings.PASSWORD, password);
		setProperty(CodeStrings.PHONE, phone);
		setProperty(CodeStrings.DEVICEID, deviceId);
		setProperty(CodeStrings.ISCONFIRMED, isConfirmed);
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

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
		setProperty(CodeStrings.PASSWORD, password);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		setProperty(CodeStrings.DEVICEID, deviceId);
	}
	
}

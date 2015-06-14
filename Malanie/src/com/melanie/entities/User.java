package com.melanie.entities;

import com.backendless.BackendlessUser;
import com.j256.ormlite.field.DatabaseField;

public class User extends BackendlessUser {

	private class LocalConstants{
		public static final String PASSWORD = "password";
		public static final String NAME = "name";
		public static final String DEVICEID = "deviceid";
		public static final String ISCONFIRMED = "isconfirmed";
		public static final String PHONE = "phone";
	}

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

	@DatabaseField
	private String phone;

	public User() {
		super();
	}

	public User(String name, String password, String phone, String deviceId, boolean isConfirmed) {
		super();
		this.password = password;
		this.name = name;
		this.deviceId = deviceId;
		this.phone = phone;

		setProperty(LocalConstants.NAME, name);
		setProperty(LocalConstants.PASSWORD, password);
		setProperty(LocalConstants.PHONE, phone);
		setProperty(LocalConstants.DEVICEID, deviceId);
		setProperty(LocalConstants.ISCONFIRMED, isConfirmed);
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
		setProperty(LocalConstants.PASSWORD, password);
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
		setProperty(LocalConstants.DEVICEID, deviceId);
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}

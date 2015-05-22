package com.melanie.entities;

import com.backendless.BackendlessUser;
import com.j256.ormlite.field.DatabaseField;

public class User extends BackendlessUser {

	private static final long serialVersionUID = 1L;
	
    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final String DEVICEID = "deviceid";
    private static final String ISCONFIRMED = "isconfirmed";
    private static final String PHONE = "phone";
    
    
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
		
		setProperty(NAME, name);
		setProperty(PASSWORD, password);
		setProperty(PHONE, phone);
		setProperty(DEVICEID, deviceId);
		setProperty(ISCONFIRMED, isConfirmed);
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
		setProperty(PASSWORD, password);
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
		setProperty(DEVICEID, deviceId);
	}
	
}

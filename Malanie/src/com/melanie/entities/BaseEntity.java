package com.melanie.entities;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

/**
 * The parent class of all entities
 * 
 * @author Akwasi Owusu
 * 
 */
public abstract class BaseEntity implements Comparable<BaseEntity> {

	public BaseEntity() {
	}

	@DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
	private int id;

	@DatabaseField
	private Date recentUse;

	@DatabaseField
	private String objectId;

	@DatabaseField(columnName = "UserId", foreignAutoCreate = true, foreignAutoRefresh = true, canBeNull = false, foreign = true)
	private User user;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getRecentUse() {
		return recentUse;
	}

	public void setRecentUse(Date recentUse) {
		this.recentUse = recentUse;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int compareTo(BaseEntity another) {
		return recentUse != null && another.recentUse != null ? recentUse
				.compareTo(another.recentUse) : 0;
	}
}

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

	private String objectId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Date recentUse() {
		return recentUse;
	}

	public void setRecentUse(Date lastRecentlyUsed) {
		recentUse = lastRecentlyUsed;
	}

	@Override
	public int compareTo(BaseEntity another) {
		return recentUse.compareTo(another.recentUse);
	}
}

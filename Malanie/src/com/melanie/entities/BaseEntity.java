package com.melanie.entities;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

/**
 * The parent class of all entities
 * 
 * @author Akwasi Owusu
 * 
 */
public abstract class BaseEntity implements Comparable<BaseEntity>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 28L;

	public BaseEntity() {
	}

	@DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
	private int id;

	@DatabaseField
	private Date recentUse;

	@DatabaseField
	private String objectId;

	@DatabaseField
	private String ownerId;

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

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public int compareTo(BaseEntity another) {
		return recentUse != null && another.recentUse != null ? recentUse
				.compareTo(another.recentUse) : 0;
	}
}

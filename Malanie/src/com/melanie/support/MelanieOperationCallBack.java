package com.melanie.support;

import java.util.List;

public class MelanieOperationCallBack<T> {

	private String sender;

	public MelanieOperationCallBack(String sender) {
		this.sender = sender;
	}

	public void onCollectionOperationSuccessful(List<T> results) {
	}

	public void onOperationSuccessful(T result) {
	}

	public void onOperationFailed(Throwable e) {

		e.printStackTrace(); // log it
	}

	public String getSender() {
		return sender;
	}
}

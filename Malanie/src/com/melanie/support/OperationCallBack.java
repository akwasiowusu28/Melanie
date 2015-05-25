package com.melanie.support;

import java.util.List;

public class OperationCallBack<T> {

	public void onCollectionOperationSuccessful(List<T> results) {
	}

	public void onOperationSuccessful(T result) {
	}

	public void onOperationFailed(Throwable e) {

		e.printStackTrace(); //TODO log it
	}
}

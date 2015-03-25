package com.melanie.support.exceptions;

public class MelanieBusinessException extends Exception {

	private static final long serialVersionUID = 1L;

	public MelanieBusinessException(String message) {
        super(message);
	}
	
	public MelanieBusinessException(String message, Throwable innerException) {
		super(message, innerException);
	}
}

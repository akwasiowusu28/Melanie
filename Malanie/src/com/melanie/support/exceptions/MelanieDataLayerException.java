package com.melanie.support.exceptions;

public class MelanieDataLayerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MelanieDataLayerException(String message) {
        super(message);
	}
	public MelanieDataLayerException(String message, Throwable innerException) {
		super(message, innerException);
	}

}

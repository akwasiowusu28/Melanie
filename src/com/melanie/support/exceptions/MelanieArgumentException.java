package com.melanie.support.exceptions;

public class MelanieArgumentException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MelanieArgumentException(String message) {
        super(message);
    }

    public MelanieArgumentException(String message, Throwable innerException) {
        super(message, innerException);
    }
}

package com.melanie.support;

import com.melanie.support.exceptions.MelanieArgumentException;

public interface MelanieArgumentValidator {
	public <T> void VerifyNonNull(T... args) throws MelanieArgumentException;

	public <T> void VerifyNonNull(T arg, String message)
			throws MelanieArgumentException;

	public void VerifyParamsNonNull(Object... arguments)
			throws MelanieArgumentException;

	public void VerifyNotEmptyString(String arg)
			throws MelanieArgumentException;

	public void VerifyNotEmptyString(String arg, String message)
			throws MelanieArgumentException;

}

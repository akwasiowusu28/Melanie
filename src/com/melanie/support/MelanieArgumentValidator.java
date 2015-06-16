package com.melanie.support;

import com.melanie.support.exceptions.MelanieArgumentException;

public interface MelanieArgumentValidator {
    <T> void VerifyNonNull(T... args) throws MelanieArgumentException;

    <T> void VerifyNonNull(T arg, String message)
            throws MelanieArgumentException;

    void VerifyParamsNonNull(Object... arguments)
            throws MelanieArgumentException;

    void VerifyNotEmptyString(String arg)
            throws MelanieArgumentException;

    void VerifyNotEmptyString(String arg, String message)
            throws MelanieArgumentException;

}

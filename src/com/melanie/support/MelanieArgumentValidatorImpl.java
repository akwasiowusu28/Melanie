package com.melanie.support;

import com.melanie.support.exceptions.MelanieArgumentException;

public class MelanieArgumentValidatorImpl implements MelanieArgumentValidator {

    private static final String NON_NULL_ARG_MSG = "Argument cannot be null";
    private static final String EMPTY_STRING_ARG_MSG = " cannot be empty";

    public <T> void VerifyNonNull(T... args) throws MelanieArgumentException {
        for (T arg : args)
            VerifyNonNull(arg, NON_NULL_ARG_MSG);
    }

    public <T> void VerifyNonNull(T arg, String message) throws MelanieArgumentException {
        if (arg == null) {
            throw new MelanieArgumentException(NON_NULL_ARG_MSG);
        }
    }

    public void VerifyParamsNonNull(Object... arguments) throws MelanieArgumentException {
        VerifyNonNull(arguments);

        for (Object argument : arguments) {
            VerifyNonNull(argument);
        }
    }

    public void VerifyNotEmptyString(String arg) throws MelanieArgumentException {
        VerifyNotEmptyString(arg, arg + EMPTY_STRING_ARG_MSG);
    }


    public void VerifyNotEmptyString(String arg, String message) throws MelanieArgumentException {
        if (arg.isEmpty()) {
            throw new MelanieArgumentException(message);
        }
    }
}

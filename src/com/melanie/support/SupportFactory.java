package com.melanie.support;

public class SupportFactory {

    public static MelanieArgumentValidator makeValidator() {
        return new MelanieArgumentValidatorImpl();
    }
}

package com.melanie.support;

public class MelanieSupportFactory {

	public static MelanieArgumentValidator makeValidator(){
		return new MelanieArgumentValidatorImpl();
	}
}

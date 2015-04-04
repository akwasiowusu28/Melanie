package com.melanie.business;

public interface MelanieBusiness {

	public <T> void initialize(T dataContext);

	public <T> void initializeAlternate(T dataContext);
}

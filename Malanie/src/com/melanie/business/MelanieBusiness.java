package com.melanie.business;

public interface MelanieBusiness {

	 <T> void initialize(T dataContext);

	 <T> void initializeAlternate(T dataContext);
	
	 void clearResources();
}

package com.melanie.business;

import com.melanie.entities.User;

public interface MelanieSession {

	 <T> void initializeLocal(T dataContext);

	 <T> void initializeCloud(T dataContext);
	
	 User getUser();
	 
	 void setUser(User user);
	 
	 boolean isUserLoggedIn();
	 
	 boolean isInitialized();
	 
	 boolean isUserRegisteredOnDevice();
	 
	 void clearResources();
}

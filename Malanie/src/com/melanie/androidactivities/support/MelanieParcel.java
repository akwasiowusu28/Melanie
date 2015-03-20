package com.melanie.androidactivities.support;

import android.os.Parcel;
import android.os.Parcelable;

public class MelanieParcel<T> implements Parcelable{

	private T parcelData;
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeValue(parcelData);
	}

}

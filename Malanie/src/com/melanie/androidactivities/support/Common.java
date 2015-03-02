package com.melanie.androidactivities.support;

public final class Common {

	public static String generateBarcodeString(int lastItemId,int categoryId){
		lastItemId++;
		int strCount = String.valueOf(lastItemId).length();
		int trailingZeroes = strCount > 5 ? strCount++ : 5;
		String format ="%0" + trailingZeroes + "d";
		return "MEL" + categoryId + trailingZeroes + String.format(format, lastItemId);
	}
}

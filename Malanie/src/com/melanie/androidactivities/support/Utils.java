package com.melanie.androidactivities.support;

public final class Utils {

	public static String generateBarcodeString(int lastItemId, int categoryId) {
		lastItemId++;
		int trailingZeroes = 13 - String.valueOf(lastItemId).length()
				- String.valueOf(categoryId).length();
		String format = "%0" + trailingZeroes + "d";
		String barcodeNumber = categoryId + String.format(format, lastItemId);		
		return barcodeNumber;
	}
}

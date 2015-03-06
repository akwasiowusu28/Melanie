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

//	private static int getCheckSum(String barcodeNumber) {
//
//		char[] chars = barcodeNumber.toCharArray();
//
//		int sum1 = num(chars[1]) + num(chars[3]) + num(chars[5]);
//		int sum2 = 3 * (num(chars[0]) + num(chars[2]) + num(chars[4]) + num(chars[6]));
//
//		int checksum_value = sum1 + sum2;
//		int checksum_digit = 10 - (checksum_value % 10);
//		if (checksum_digit == 10)
//			checksum_value = 0;
//
//		return checksum_value;
//	}

//	private static int num(char charValue) {
//		return Character.getNumericValue(charValue);
//	}
}

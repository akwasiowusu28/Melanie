package com.melanie.androidactivities.support;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.epson.lwprint.sdk.LWPrintDataProvider;

public class MelanieBarcodeDataProvider implements LWPrintDataProvider {

	private static final String BARCODE_CONFIG_FILE = "BarcodeData/Barcode.plist";
	private AssetManager assetManager;
	private String barcodeString;
	
	public MelanieBarcodeDataProvider(AssetManager assetManager, String barcodeString){
		this.assetManager = assetManager;
		this.barcodeString = barcodeString;
	}
	
	private InputStream inputStream;
	@Override
	public void endOfPrint() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endPage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Bitmap getBitmapContentData(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getFormDataForPage(int arg0) {
		System.out.println("AKWASI OWUSU: I'm getFormDataFormDataForPage method and I got hit!!");
			try {
				inputStream = assetManager.open("BarcodeData/Barcode.plist");
			} catch (IOException e) {
				System.out.println(e.getMessage()); //Use logger instead
			}
		
		return inputStream;
	}

	@Override
	public int getNumberOfPages() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public String getStringContentData(String contentName, int pageIndex) {
		System.out.println("AKWASI OWUSU: I'm getStringContentData method and I got hit!!");
		return this.barcodeString;
	}

	@Override
	public void startOfPrint() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startPage() {
		// TODO Auto-generated method stub
		
	}

}

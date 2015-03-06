package com.melanie.androidactivities.support;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.epson.lwprint.sdk.LWPrintDataProvider;

public class MelanieBarcodeDataProvider implements LWPrintDataProvider {

	private static final String BARCODE_CONFIG_FILE = "barcode/Barcode";
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
		
		if(inputStream != null)
			try {
				inputStream.close();
				inputStream = assetManager.open(BARCODE_CONFIG_FILE);
			} catch (IOException e) {
				e.printStackTrace(); //use a logger instead
			}
		
		return inputStream;
	}

	@Override
	public int getNumberOfPages() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getStringContentData(String contentName, int pageIndex) {
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

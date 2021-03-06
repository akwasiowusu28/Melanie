package com.melanie.ui.support;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.epson.lwprint.sdk.LWPrintDataProvider;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Akwasi Owusu Class that provides the barcode data and the barcode
 *         string to the Label Works bluetooth printer
 */

public class MelanieBarcodeDataProvider implements LWPrintDataProvider {

    private static final String BARCODE_CONFIG_FILE = "BarcodeData/Barcode.plist";
    private final AssetManager assetManager;
    private final String barcodeString;
    private final int numberOfPages;
    private InputStream inputStream;

    /**
     * Initilizes the data provider with path to the assets. This is needed to
     * locate the raw file that contains the barcode data
     *
     * @param assetManager  the asset manager
     * @param barcodeString the barcode to send to the printer
     * @param numberOfPages Number of pages
     */
    public MelanieBarcodeDataProvider(AssetManager assetManager,
                                      String barcodeString, int numberOfPages) {
        this.assetManager = assetManager;
        this.barcodeString = barcodeString;
        this.numberOfPages = numberOfPages;
    }

    @Override
    public void endOfPrint() {
        // Do nothing

    }

    @Override
    public void endPage() {
        // Do nothing

    }

    @Override
    public Bitmap getBitmapContentData(String arg0, int arg1) {
        // Do nothing
        return null;
    }

    @Override
    public InputStream getFormDataForPage(int arg0) {
        try {
            inputStream = assetManager.open(BARCODE_CONFIG_FILE);
        } catch (IOException e) {
            System.out.println(e.getMessage()); // Use logger instead
        }

        return inputStream;
    }

    @Override
    public int getNumberOfPages() {
        return numberOfPages;
    }

    @Override
    public String getStringContentData(String contentName, int pageIndex) {
        return this.barcodeString;

    }

    @Override
    public void startOfPrint() {
        // Do nothing

    }

    @Override
    public void startPage() {
        // Do nothing

    }
}

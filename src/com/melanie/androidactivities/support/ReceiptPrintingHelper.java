package com.melanie.androidactivities.support;

import android.content.Context;
import android.os.Handler;

import com.melanie.entities.Sale;
import com.zj.btsdk.BluetoothService;

import java.util.List;

public class ReceiptPrintingHelper {

    private BluetoothService receiptPrinterService;
    private int currentNumberOfLines = 0;

    public ReceiptPrintingHelper(Context context) {
        super();
        receiptPrinterService = new BluetoothService(context, new Handler());
    }

    public void initializePrinterWithPrinterInfo(String macAddress) {
        receiptPrinterService.connect(receiptPrinterService.getDevByMac(macAddress));

        // Hold up! Let it finish connecting, ya punk!
        while (receiptPrinterService.getState() == BluetoothService.STATE_CONNECTING) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace(); // TODO: log it
            }
        }
    }

    public void printReceipt(List<Sale> sales) {

        byte[] command = new byte[3];

        command[0] = 0x1b; // initialize the printer
        //command[0] &= 0x1b2; // print whatever is in the buffer, if any, and do a line feed

        receiptPrinterService.write(command);

        String receiptHeader = getReceiptHeader();
        receiptPrinterService.sendMessage(receiptHeader, "utf-8");

        currentNumberOfLines += 3;

        String mainContent = generateMainReceiptContent(sales);

        receiptPrinterService.sendMessage(mainContent, "utf-8");

        receiptPrinterService.sendMessage("\n\n\n", "utf-8");
        currentNumberOfLines += 3;

        receiptPrinterService.sendMessage(getGreeting(), "utf-8");

        currentNumberOfLines++;

        String lines = "";
        while (currentNumberOfLines != 20) {
            currentNumberOfLines++;
            lines += "\n";
        }
        receiptPrinterService.sendMessage(lines, "utf-8");
        currentNumberOfLines = 0;
    }

    private String generateMainReceiptContent(List<Sale> sales) {
        String message = "";
        String currentSubString = "";

        for (Sale sale : sales) {
            String productName = sale.getProduct().getProductName();
            int quantity = sale.getQuantitySold();
            double price = sale.getProduct().getPrice();

            String saleDescription = productName + " (" + String.valueOf(quantity) + "x" + String.valueOf(price) + ")";
            String productTotalPrice = String.valueOf(quantity * price);

            int len = saleDescription.length();

            for (int start = 0; start < len; start += 25) {
                int end = Math.min(len, start + 25);
                currentSubString = saleDescription.substring(start, end);
                message += currentSubString;

                if (end != len) {
                    message += "\n";
                    currentNumberOfLines++;
                }
            }

            int remaining = 32 - currentSubString.length();
            message += String.format("%" + remaining + "s", productTotalPrice) + "\n\n";
            currentNumberOfLines += 2;
        }
        return message;
    }

    //get this from the preferences *** well, maybe you might wanna store it in the cloud
    private String getReceiptHeader() {
        return "Melanie Couture\nPig farm, Accra\nTel: +1 847 904 0065\n\n";
    }

    private String getGreeting() {
        return "  Thanks for shopping with us   \n          See you soon          ";
    }

    public void clearResources() {
        if (receiptPrinterService.isDiscovering()) {
            receiptPrinterService.cancelDiscovery();
        }

        receiptPrinterService.stop();
        receiptPrinterService = null;
    }
}

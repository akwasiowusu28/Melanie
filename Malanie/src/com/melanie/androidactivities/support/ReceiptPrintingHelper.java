package com.melanie.androidactivities.support;

import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.melanie.entities.Sale;
import com.zj.btsdk.BluetoothService;

public class ReceiptPrintingHelper {

	private Context context;
	private BluetoothService receiptPrinterService;
	private String macAddress;

	public ReceiptPrintingHelper(Context context) {
		super();
		this.context = context;
		receiptPrinterService = new BluetoothService(context, new Handler());
	}

	public void initializePrinterWithPrinterInfo(String macAddress) {
		this.macAddress = macAddress;
		receiptPrinterService.connect(receiptPrinterService
				.getDevByMac(macAddress));

		// Hold up! Let it finish connecting, ya punk!
		while (receiptPrinterService.getState() == BluetoothService.STATE_CONNECTING)
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace(); // TODO: log it
			}
	}

	public void printReceipt(List<Sale> sales) {

		byte[] headerCommand = new byte[1];
		headerCommand[0] = 0x21 | 0x10;
		byte[] command = new byte[3];

		receiptPrinterService.write(headerCommand);
		receiptPrinterService.sendMessage(
				"Melanie Coutoure\nPig farm, Accra\nTel: +1 847 904 0065\n\n",
				"utf-8");
		command[0] = 0x1b; // initialize the printer
		command[0] &= 0x1b2; // print whatever is in the buffer, if any, and do
								// a line feed

		receiptPrinterService.write(command);

		for (Sale sale : sales) {
			String productName = sale.getProduct().getProductName();
			String productTotalPrice = String.valueOf(sale.getQuantitySold()
					* sale.getProduct().getPrice());

			String message = String.format("%-40s%16s%7s", productName, " ",
					productTotalPrice);
			receiptPrinterService.sendMessage(message, "utf-8");
			command[2] = 0x0A; // do a line feed
			receiptPrinterService.write(command);
		}

	}

	public void clearResources() {
		if (receiptPrinterService.isDiscovering())
			receiptPrinterService.cancelDiscovery();

		receiptPrinterService.stop();
		receiptPrinterService = null;
	}
}

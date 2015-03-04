package com.melanie.androidactivities;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.androidGScannerFiles.GZxingEncoder;
import com.melanie.androidactivities.support.Utils;
import com.melanie.business.controllers.ProductEntryController;
import com.melanie.business.controllers.ProductEntryControllerImpl;
import com.melanie.entities.Category;

public class AddProductActivity extends Activity {

	private ProductEntryController productController;
	
	public AddProductActivity() {
		productController = new ProductEntryControllerImpl();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);
		
		List<Category> categories = productController.getAllCategories();
		
		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		categorySpinner.setAdapter(new ArrayAdapter<Category>(this, android.R.layout.simple_spinner_dropdown_item ,categories));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_product, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void addProduct(View view){
		Spinner categorySpinner = (Spinner) findViewById(R.id.categoriesSpinner);
		Category selectedCategory = (Category)categorySpinner.getSelectedItem();
		int lastProductId = productController.getLastInsertedProductId();
		ImageView img = (ImageView)findViewById(R.id.barcodeImage);
		img.setImageBitmap(generateBarcodeBitmap(lastProductId, selectedCategory.getId()));

	}
	
	private Bitmap generateBarcodeBitmap(int lastProductId, int categotyId){
		Bitmap bitmap = null;
		GZxingEncoder 	encoder = GZxingEncoder.getInstance();
		encoder.initalize(this);
		String barcodeString = Utils.generateBarcodeString(lastProductId, categotyId);
		
		 try {
			 Map<EncodeHintType,Object> hints = new EnumMap<EncodeHintType,Object>(EncodeHintType.class);
			 hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			 hints.put(EncodeHintType.MARGIN, 2);
			bitmap = encoder.generate_EAN_13(barcodeString, hints);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		 
		return bitmap;
	}
}

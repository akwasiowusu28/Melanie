package com.melanie.androidactivities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melanie.androidactivities.support.Utils;
import com.melanie.business.SalesController;
import com.melanie.entities.Sale;
import com.melanie.support.MelanieBusinessFactory;
import com.melanie.support.MelanieOperationCallBack;
import com.melanie.support.exceptions.MelanieBusinessException;

public class SalesReportTableFragment extends Fragment {

	private Map<Sale, Integer> sales;
	private SalesController salesController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeFields();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.layout_sales_table_report_fragment,
				container, false);
	}

	private void initializeFields() {
		sales = new HashMap<Sale, Integer>();
		salesController = MelanieBusinessFactory.makeSalesController();

	}

	private Map<Sale, Integer> getSalesBetweenDates(Date fromDate, Date toDate) {
		List<Sale> salesBetween = new ArrayList<Sale>();
		try {
			salesBetween = salesController.getSalesBetween(fromDate, toDate,
					new MelanieOperationCallBack<Sale>(this.getClass()
							.getSimpleName()) {

						@Override
						public void onCollectionOperationSuccessful(
								List<Sale> results) {
							Map<Sale, Integer> newSales = Utils
									.groupItems(results);
							sales.putAll(newSales);
						}

					});
		} catch (MelanieBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Utils.groupItems(salesBetween);
	}
}

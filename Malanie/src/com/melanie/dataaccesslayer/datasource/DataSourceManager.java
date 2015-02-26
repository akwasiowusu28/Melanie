package com.melanie.dataaccesslayer.datasource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.j256.ormlite.dao.Dao;
import com.melanie.entities.Category;
import com.melanie.entities.Customer;
import com.melanie.entities.Payment;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;
import com.melanie.support.exceptions.MelanieDataLayerException;

public class DataSourceManager {
	
	private static List<Class<?>> entityClasses = new ArrayList<Class<?>>(){		
		private static final long serialVersionUID = 1L;
	{
		add(Category.class);
		add(Customer.class);
		add(Payment.class);
		add(Product.class);
		add(Sale.class);
		add(Category.class);
	}};
	
	
	private static DataSource dataSource;
	
	
	public static Dao<Object, Integer> getCachedDaoFor(Class<?> entityClass){
		try {
			return dataSource.getDao(entityClass);
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}
	}
	
	public static List<Class<?>> getEntityClasses(){
		return entityClasses;
	}

	public static void initialize(DataSource dataSourceParam) {
		dataSource = dataSourceParam;
	}

	public static void clearDataSource() {
		dataSource = null;
	}
}

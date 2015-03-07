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

/**
 * Class the manages Data Access Objects and retains a connection to the database
 * @author Akwasi Owusu
 *
 */
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
	
	/**
	 * 
	 * @param entityClass an entity class for which a data access object is requested
	 * @return a data access object
	 */
	public static Dao<Object, Integer> getCachedDaoFor(Class<?> entityClass){
		try {
			return dataSource.getDao(entityClass);
		} catch (SQLException e) {
			throw new MelanieDataLayerException(e.getMessage());
		}
	}
	
	/**
	 * Returns all entity classes
	 * @return all entity classes
	 */
	public static List<Class<?>> getEntityClasses(){
		return entityClasses;
	}

	/**
	 * Initialize the data source. Doing this because ORMlite is wired to keep database connection at the presentation layer 
	 * and I don't want to do that so I push the datasource object down from the presentation layer.
	 * Eeew! Gross! I know! I will find a better alternative 
	 * @param dataSourceParam the data source object obtained from the data access layer
	 */
	public static void initialize(DataSource dataSourceParam) {
		dataSource = dataSourceParam;
	}

	/**
	 * Clears the DataSource object and disconnect from the database
	 */
	public static void clearDataSource() {
		dataSource = null;
	}
}

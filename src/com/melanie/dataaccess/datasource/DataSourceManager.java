package com.melanie.dataaccess.datasource;

import com.backendless.Backendless;
import com.j256.ormlite.dao.Dao;
import com.melanie.entities.Category;
import com.melanie.entities.CostEntry;
import com.melanie.entities.CostItem;
import com.melanie.entities.Customer;
import com.melanie.entities.Payment;
import com.melanie.entities.Product;
import com.melanie.entities.Sale;
import com.melanie.entities.SalePayment;
import com.melanie.entities.User;
import com.melanie.support.exceptions.MelanieDataLayerException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class the manages Data Access Objects and retains a connection to the
 * database
 *
 * @author Akwasi Owusu
 */
public class DataSourceManager {

    private class LocalConstants{

        /*********J Six*******************
        public static final String APPLICATION_ID = "6894887D-385F-BED1-FF9D-A587850AA700";
         public static final String SECRET_KEY = "D1C3D6CE-85FF-6679-FFBB-CB16A400E300";
        /********* ** *******************/

        /******** Melanie ***************/
        public static final String APPLICATION_ID = "09B34DD5-7EA5-294B-FF3A-EB779F8EEF00";
        public static final String SECRET_KEY = "C4580BEA-9513-1DB0-FFF1-98AA763D3300";
        /******** ** ***************/
        public static final String VERSION = "v1";
    }

    private static List<Class<?>> entityClasses = new ArrayList<Class<?>>() {
        private static final long serialVersionUID = 1L;

        {
            add(Category.class);
            add(Customer.class);
            add(Payment.class);
            add(Product.class);
            add(Sale.class);
            add(SalePayment.class);
            add(User.class);
            add(CostItem.class);
            add(CostEntry.class);
        }
    };

    private static DataSource dataSource;

    /**
     * @param entityClass an entity class for which a data access object is requested
     * @return a data access object
     * @throws MelanieDataLayerException
     */
    public static <T>Dao<T, Integer> getCachedDaoFor(Class<T> entityClass)
            throws MelanieDataLayerException {
        Dao<T, Integer> dao = null;
        try {
            if (dataSource != null) {
                dao = dataSource.getDao(entityClass);
            }
        } catch (SQLException e) {
            throw new MelanieDataLayerException(e.getMessage());
        }
        return dao;
    }

    /**
     * Returns all entity classes
     *
     * @return all entity classes
     */
    public static List<Class<?>> getEntityClasses() {
        return entityClasses;
    }

    /**
     * Initialize the data source. Doing this because ORMlite is wired to keep
     * database connection at the presentation layer and I don't want to do that
     * so I push the datasource object down from the presentation layer. Eeew!
     * Gross! I know! I will find a better alternative
     *
     * @param dataSourceParam the data source object obtained from the data access layer
     */
    public static void initialize(DataSource dataSourceParam) {
        dataSource = dataSourceParam;
        //dataSource.
    }

    /**
     * Clears the DataSource object and disconnect from the database
     */
    public static void clearDataSource() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    public static <T> void initializeBackendless(T dataContext) {
        Backendless.initApp(dataContext, LocalConstants.APPLICATION_ID,
                LocalConstants.SECRET_KEY, LocalConstants.VERSION);
    }
}

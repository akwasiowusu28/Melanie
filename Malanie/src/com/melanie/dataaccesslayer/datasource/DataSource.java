package com.melanie.dataaccesslayer.datasource;

import java.sql.SQLException;
import java.util.List;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.melanie.support.exceptions.MelanieDataLayerException;

/**
 * 
 * @author Akwasi Owusu
 *The data source class that inherits from the ORM database helper
 */
public class DataSource extends OrmLiteSqliteOpenHelper  {
	
	private static final String DATABASE_NAME = "melaniedata.db";
	
	private static final int DATABASE_VERSION = 1;
    
	public DataSource(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		createTables(connectionSource);
	}

	private void createTables(ConnectionSource connectionSource){
		
		List<Class<?>> entityClasses = DataSourceManager.getEntityClasses();
		for(Class<?> entityClass: entityClasses){
			try {
				DataSource.this.cancelQueriesEnabled = false;
				TableUtils.createTableIfNotExists(connectionSource, entityClass);
			} catch (SQLException e) {
				throw new MelanieDataLayerException(e.getMessage(), e);
			}
		}
	}
	
	private void dropTables(ConnectionSource connectionSource){
		List<Class<?>> entityClasses = DataSourceManager.getEntityClasses();
		for(Class<?> entityClass: entityClasses){
			try {
				TableUtils.dropTable(connectionSource, entityClass,true);
			} catch (SQLException e) {
				throw new MelanieDataLayerException(e.getMessage(), e);
			}
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion,
			int newVersion) {
		
		dropTables(connectionSource);
		onCreate(db, connectionSource);
	}
	
	
	
	@Override
	public void close() {
		DataSourceManager.clearDataSource();
		super.close();
	}
	
}

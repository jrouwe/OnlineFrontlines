package onlinefrontlines.utils;

import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This is a helper class that simplifies communication with the database server. It supplies
 * connection objects using the database pooling mechanism that tomcat provides 
 * (see context.xml in the META-INF folder)
 * 
 * @author jorrit
 * 
 * Copyright (C) 2009-2013 Jorrit Rouwe
 * 
 * This file is part of Online Frontlines.
 *
 * Online Frontlines is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Online Frontlines is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Online Frontlines.  If not, see <http://www.gnu.org/licenses/>.
 */
public final class DbConnectionPool 
{
	/**
	 * Data sources
	 */
	public static final String DS_DEFAULT = "onlinefrontlines";
	
	/**
	 * Singleton instance
	 */
	private static DbConnectionPool instance = new DbConnectionPool();
	
	/**
	 * Initialized data sources
	 */
	private HashMap<String, DataSource> sources = new HashMap<String, DataSource>();
	
	/**
	 * Singleton instance
	 */
	public static DbConnectionPool getInstance()
	{
		return instance;
	}
	
	/**
	 * Initialize the object
	 * @throws NamingException
	 */
	public void init(String dataSourceName) throws NamingException
	{
        // Get data source
        InitialContext ctx = new InitialContext();
        DataSource dataSource = (DataSource)ctx.lookup("java:comp/env/jdbc/" + dataSourceName);
        sources.put(dataSourceName, dataSource);
	}
	
	/**
	 * Get a new database connection (make sure you close it when done)
	 * @throws SQLException
	 */
	public Connection getConnection(String dataSourceName) throws SQLException
	{
        // Get connection
		DataSource ds = sources.get(dataSourceName);
		return ds.getConnection();
	}
		
	/**
	 * Get all datasources
	 */
	public Map<String, DataSource> getDataSources()
	{
		return sources;
	}
}

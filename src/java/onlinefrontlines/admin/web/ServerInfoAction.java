package onlinefrontlines.admin.web;

import onlinefrontlines.web.*;
import java.util.Date;
import java.util.TreeMap;
import javax.sql.DataSource;
import onlinefrontlines.utils.DbConnectionPool;

/**
 * This action views general server info
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
public class ServerInfoAction extends WebAction
{	
	/**
	 * Output stats
	 */
	public String date;
	public long usedMemory;
	public long committedMemory;
	public long maxMemory;
	public int cpuCount;
	public String os;
	public String jvm;
	public TreeMap<String, String> dataSourceStatus = new TreeMap<String, String>();
	public String servletContainer;
	
	/**
	 * Query data source status
	 * 
	 * @param dataSourceName Name of the data source
	 */
	private String getDataSourceStatus(String dataSourceName)
	{
		DataSource dataSource = (DataSource)DbConnectionPool.getInstance().getDataSources().get(dataSourceName);
		if (dataSource.getClass().getName().equals("org.apache.commons.dbcp.BasicDataSource") 
			&& dataSource instanceof org.apache.commons.dbcp.BasicDataSource)
		{
			org.apache.commons.dbcp.BasicDataSource s = (org.apache.commons.dbcp.BasicDataSource)dataSource;
			return "Busy: " + s.getNumActive() + " (" + (int)(s.getNumActive() * 100 / s.getMaxActive()) + "%), Connections: " + (s.getNumIdle() + s.getNumActive()) + ", Max: " + s.getMaxActive(); 
		}
		else if (dataSource.getClass().getName().equals("org.apache.tomcat.dbcp.dbcp.BasicDataSource") 
				&& dataSource instanceof org.apache.tomcat.dbcp.dbcp.BasicDataSource)
		{
			org.apache.tomcat.dbcp.dbcp.BasicDataSource s = (org.apache.tomcat.dbcp.dbcp.BasicDataSource)dataSource;
			return "Busy: " + s.getNumActive() + " (" + (int)(s.getNumActive() * 100 / s.getMaxActive()) + "%), Connections: " + (s.getNumIdle() + s.getNumActive()) + ", Max: " + s.getMaxActive(); 
		}
		else
		{
			return "Unknown datasource: " + dataSource.getClass().getName();
		}
	}

	/**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	Runtime r = Runtime.getRuntime();
    	
   		// Get stats
    	date = new Date().toString();
   		committedMemory = r.totalMemory();
   		maxMemory = r.maxMemory();
   		usedMemory = committedMemory - r.freeMemory();
   		cpuCount = Runtime.getRuntime().availableProcessors();
   		os = System.getProperty("os.name") + " (" + System.getProperty("sun.os.patch.level") + ") " + System.getProperty("os.arch") + " " + System.getProperty("os.version");
   		jvm = System.getProperty("java.runtime.name") + " " + System.getProperty("java.runtime.version") + " " + System.getProperty("java.vm.name");
   		for (String s : DbConnectionPool.getInstance().getDataSources().keySet())
   			dataSourceStatus.put(s, getDataSourceStatus(s));
   		servletContainer = servletContext.getServerInfo();
   		   		
        return getSuccessView();
    }
}

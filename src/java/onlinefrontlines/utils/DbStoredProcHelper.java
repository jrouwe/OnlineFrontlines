package onlinefrontlines.utils;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import onlinefrontlines.profiler.Profiler;
import onlinefrontlines.profiler.Sampler;

/**
 * This is a helper class that provides an easy interface to the rest of the database code to 
 * call stored procedures without worrying about object cleanup.
 * 
 * Example:
 * 
 * <pre>
 * DbStoredProcHelper helper = new DbStoredProcHelper();
 * try
 * {
 * 		helper.prepareCall("{CALL functionName(?, ?)}");
 * 		helper.setInt(1, param1);
 * 		helper.execute();
 * 		int output = helper.getInt(2);
 * }
 * finally
 * {
 * 		helper.close();
 * }
 * </pre>
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
public final class DbStoredProcHelper 
{
	private String call;
	private Connection con = null;
    private CallableStatement pcall = null;
    private ResultSet results = null;

    /**
     * This is the first call you need to perform on the helper object, it sets up the SQL call.
     * This call is done using a prepared call so you can use ? and set parameters later.
     * 
     * @param call The SQL call string
     * @throws SQLException
     */
	public void prepareCall(String call) throws SQLException
	{
		prepareCall(call, DbConnectionPool.DS_DEFAULT);
	}
	
    /**
     * This is the first call you need to perform on the helper object, it sets up the SQL call.
     * This call is done using a prepared call so you can use ? and set parameters later.
     * 
     * @param call The SQL call string
     * @param dataSourceName Data source to use
     * @throws SQLException
     */
	public void prepareCall(String call, String dataSourceName) throws SQLException
	{
		// Store call
		this.call = call;
		
		// Make sure nothing is lingering
		close();
		
        // Prepare the call
        con = DbConnectionPool.getInstance().getConnection(dataSourceName);
        pcall = con.prepareCall(call);
	}
	
	/**
	 * Sets an int parameter on the call
	 * 
	 * @param p Parameter number starting at 1
	 * @param i Integer value
	 * @throws SQLException
	 */
	public void setInt(int p, int i) throws SQLException
	{
		pcall.setInt(p, i);
	}

	/**
	 * Sets an long parameter on the call
	 * 
	 * @param p Parameter number starting at 1
	 * @param i Long value
	 * @throws SQLException
	 */
	public void setLong(int p, long i) throws SQLException
	{
		pcall.setLong(p, i);
	}

	/**
	 * Sets a string parameter on the call
	 * 
	 * @param p Parameter number starting at 1
	 * @param s String value
	 * @throws SQLException
	 */
	public void setString(int p, String s) throws SQLException
	{
		pcall.setString(p, s);
	}
	
	/**
	 * Set a null value parameter on the call
	 * @param p Parameter number starting at 1
	 * @throws SQLException
	 */
	public void setNull(int p) throws SQLException
	{
		pcall.setNull(p, Types.NULL);
	}

	/**
	 * Executes the call prepared by prepareCall. 
	 * @throws SQLException
	 */
	public boolean execute() throws SQLException
	{
		Sampler sampler = Profiler.getInstance().startSampler(Profiler.CATEGORY_SQL_STORED_PROCEDURE, call);
		try
		{
			return pcall.execute();		
		}
		finally
		{
			sampler.stop();
		}
	}
	
	/**
	 * Executes the call prepared by prepareCall. 
	 * @throws SQLException
	 */
	public ResultSet executeQuery() throws SQLException
	{
		Sampler sampler = Profiler.getInstance().startSampler(Profiler.CATEGORY_SQL_STORED_PROCEDURE, call);
		try
		{
			assert(results == null);		
			results = pcall.executeQuery();
			
			return results;
		}
		finally
		{
			sampler.stop();
		}
	}
	
	/**
	 * Get an integer value output parameter.
	 * @param p Parameter number starting at 1
	 * @return Returns the value of the output parameter
	 * @throws SQLException
	 */
	public int getInt(int p) throws SQLException
	{
		return pcall.getInt(p);
	}

	/**
	 * Get a string value output parameter.
	 * @param p Parameter number starting at 1
	 * @return Returns the value of the output parameter
	 * @throws SQLException
	 */
	public String getString(int p) throws SQLException
	{
		return pcall.getString(p);
	}
	
	/**
	 * Close and clean up the object. After this call you can start
	 * preparing a new call using prepareCall
	 */
	public void close()
	{
		// Close connections
    	try { if (results != null) results.close(); } catch (SQLException exception) { }
    	try { if (pcall != null) pcall.close(); } catch (SQLException exception) { }
    	try { if (con != null) con.close(); } catch (SQLException exception) { }
    	
    	results = null;
    	pcall = null;
    	con = null;
	}
}

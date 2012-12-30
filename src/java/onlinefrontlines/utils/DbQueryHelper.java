package onlinefrontlines.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Timestamp;
import java.util.ArrayList;
import onlinefrontlines.profiler.Profiler;
import onlinefrontlines.profiler.Sampler;

/**
 * This is a helper class that provides an easy interface to the rest of the database code to do 
 * queries without worrying about object cleanup.
 * 
 * Example:
 * 
 * <pre>
 * DbQueryHelper helper = new DbQueryHelper();
 * try
 * {
 * 		helper.prepareQuery("SELECT * FROM users WHERE userId=?");
 * 		helper.setInt(1, userId);
 * 		helper.executeQuery();
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
public final class DbQueryHelper 
{
	private String query;
	private Connection con = null;
    private PreparedStatement pstmt = null;
    private ResultSet results = null;
	
	/**
     * This is the first call you need to perform on the helper object, it sets up the SQL query.
     * This SQL query is done using a prepared statement so you can use ? and set parameters later.
     * 
     * @param query The SQL query string
     * @throws SQLException
     */
	public void prepareQuery(String query) throws SQLException
	{
		prepareQuery(query, DbConnectionPool.DS_DEFAULT);
	}

	/**
     * This is the first call you need to perform on the helper object, it sets up the SQL query.
     * This SQL query is done using a prepared statement so you can use ? and set parameters later.
     * 
     * @param query The SQL query string
     * @param dataSourceName Data source to use
     * @throws SQLException
     */
	public void prepareQuery(String query, String dataSourceName) throws SQLException
	{
		// Store query
		this.query = query;

		// Make sure nothing is lingering
		close();
		
        // Create SQL query
        con = DbConnectionPool.getInstance().getConnection(dataSourceName);
    	pstmt = con.prepareStatement(query);		
	}
	
	/**
	 * Sets an int parameter on the query
	 * 
	 * @param p Parameter number starting at 1
	 * @param i Integer value
	 * @throws SQLException
	 */
	public void setInt(int p, int i) throws SQLException
	{
		pstmt.setInt(p, i);
	}

	/**
	 * Sets an long parameter on the query
	 * 
	 * @param p Parameter number starting at 1
	 * @param l Long value
	 * @throws SQLException
	 */
	public void setLong(int p, long l) throws SQLException
	{
		pstmt.setLong(p, l);
	}

	/**
	 * Sets a string parameter on the query
	 * 
	 * @param p Parameter number starting at 1
	 * @param s String value
	 * @throws SQLException
	 */
	public void setString(int p, String s) throws SQLException
	{
		pstmt.setString(p, s);
	}
	
	/**
	 * Sets a byte array (blob) parameter on the query
	 * 
	 * @param p Parameter number starting at 1
	 * @param b Binary blob value
	 * @throws SQLException
	 */
	public void setBytes(int p, byte[] b) throws SQLException
	{
		pstmt.setBytes(p, b);
	}
	
	/**
	 * Sets a time parameter on the query
	 * 
	 * @param p Parameter number starting at 1
	 * @param t Time value
	 * @throws SQLException
	 */
	public void setTimestamp(int p, Timestamp t) throws SQLException
	{
		pstmt.setTimestamp(p, t);
	}

	/**
	 * Set a null value parameter on the query
	 * @param p Parameter number starting at 1
	 * @throws SQLException
	 */
	public void setNull(int p) throws SQLException
	{
		pstmt.setNull(p, Types.NULL);
	}

	/**
	 * Executes the SQL query prepared by prepareQuery. Call this function for SQL queries that return 
	 * a result set (like SELECT).
	 * 
	 * @throws SQLException
	 */
	public void executeQuery() throws SQLException
	{
		Sampler sampler = Profiler.getInstance().startSampler(Profiler.CATEGORY_SQL_QUERY, query);
		try
		{
			results = pstmt.executeQuery();
		}
		finally
		{
			sampler.stop();
		}
	}
	
	/**
	 * Execute the SQL query prepared by prepareQuery. Call this function for SQL queris that do not return 
	 * a result set (like UPDATE, INSERT).
	 * @return The amount of records that were modified
	 * @throws SQLException
	 */
	public int executeUpdate() throws SQLException
	{
		Sampler sampler = Profiler.getInstance().startSampler(Profiler.CATEGORY_SQL_QUERY, query);
		try
		{
			return pstmt.executeUpdate();
		}
		finally
		{
			sampler.stop();
		}
	}

	/**
	 * Valid when executeUpdate was called. Returns the generated keys in the last update.
	 * 
	 * @return List of generated keys.
	 * @throws SQLException
	 */
	public ArrayList<Integer> getGeneratedKeys() throws SQLException
	{
		ArrayList<Integer> keys = new ArrayList<Integer>();
		ResultSet rs = pstmt.getGeneratedKeys();
	    while (rs.next())
	        keys.add(rs.getInt(1));
	    return keys;
	}
	
	/**
	 * Valid when executeQuery was called. Use nextRecord() to go to the first record and get all the
	 * query results using the get functions. Call nextRecord() again to go to the next result until
	 * the function returns false.
	 * 
	 * @return Returns true if there is a next record.
	 * @throws SQLException
	 */
	public boolean nextRecord() throws SQLException
	{
		return results.next();
	}
	
	/**
	 * Get an integer value from the current result record.
	 * @param p The column number starting at 1
	 * @return Returns the value of the record field
	 * @throws SQLException
	 */
	public int getInt(int p) throws SQLException
	{
		return results.getInt(p);
	}

	/**
	 * Get a long value from the current result record.
	 * @param p The column number starting at 1
	 * @return Returns the value of the record field
	 * @throws SQLException
	 */
	public long getLong(int p) throws SQLException
	{
		return results.getLong(p);
	}

	/**
	 * Get a string value from the current result record.
	 * @param p The column number starting at 1
	 * @return Returns the value of the record field
	 * @throws SQLException
	 */
	public String getString(int p) throws SQLException
	{
		return results.getString(p);
	}
	
	/**
	 * Get a bytes array (blob) from the current result record.
	 * @param p The column number starting at 1
	 * @return Returns the value of the record field
	 * @throws SQLException
	 */
	public byte[] getBytes(int p) throws SQLException
	{
		return results.getBytes(p);
	}
	
	/**
	 * Get a timestamp from the current result record.
	 * @param p The column number starting at 1
	 * @return Returns the value of the record field
	 * @throws SQLException
	 */
	public Timestamp getTimeStamp(int p) throws SQLException
	{
		return results.getTimestamp(p);
	}

	/**
	 * Close and clean up the object. After this call you can start
	 * preparing a new query using prepareQuery
	 */
	public void close()
	{
		// Close connections
    	try { if (results != null) results.close(); } catch (SQLException exception) { }
    	try { if (pstmt != null) pstmt.close(); } catch (SQLException exception) { }
    	try { if (con != null) con.close(); } catch (SQLException exception) { }
    	
    	results = null;
    	pstmt = null;
    	con = null;
	}
}

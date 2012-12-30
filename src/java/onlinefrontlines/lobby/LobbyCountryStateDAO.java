package onlinefrontlines.lobby;

import java.util.*;
import java.sql.SQLException;
import onlinefrontlines.Army;
import onlinefrontlines.utils.DbQueryHelper;

/**
 * This class communicates with the database and manages reading/writing state of lobby countries
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
public class LobbyCountryStateDAO 
{
	public static class State
	{
		public int locationX;
		public int locationY;
		public int ownerUserId;
		public long ownerExclusiveTime;
		public Army army;
	};
	
	/**
	 * Queries the database for all country state for a particular lobby
	 */
	public static ArrayList<State> getCountryStates(int lobbyId) throws SQLException
	{
		ArrayList<State> list = new ArrayList<State>();

    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find all feedback
	    	helper.prepareQuery("SELECT locationX, locationY, ownerUserId, ownerExclusiveTime, army FROM lobby_country_state WHERE lobbyId=?");
	    	helper.setInt(1, lobbyId);
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
		        // Construct object
	    		State s = new State();
	    		s.locationX = helper.getInt(1);
	    		s.locationY = helper.getInt(2);
	    		s.ownerUserId = helper.getInt(3);
	    		s.ownerExclusiveTime = helper.getLong(4);
	    		s.army = Army.fromInt(helper.getInt(5));
		        list.add(s);
	    	}
        }
        finally
        {
        	helper.close();
        }
        
        return list;
	}

	/**
	 * Remove country state from database
	 * 
	 * @param lobbyId Lobby id
	 * @param locationX Location of country
	 * @param locationY Location of country
	 * @throws SQLException
	 */
	public static void removeCountryState(int lobbyId, int locationX, int locationY) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery("DELETE FROM lobby_country_state WHERE lobbyId=? AND locationX=? AND locationY=?");
	    	helper.setInt(1, lobbyId);
	    	helper.setInt(2, locationX);
	    	helper.setInt(3, locationY);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Create country states in database
	 * 
	 * @param lobbyId Lobby id
	 * @param countries List of country states to create
	 * @throws SQLException
	 */
	public static void createCountryStates(int lobbyId, ArrayList<Country> countries) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	String sql = "INSERT INTO lobby_country_state (lobbyId, locationX, locationY, ownerUserId, ownerExclusiveTime, army) VALUES ";
        	boolean first = true;
        	for (Country c : countries)
        	{
        		if (first)
        			first = false;
        		else
        			sql += ", ";

        		sql += "(" + lobbyId + ", " + c.locationX + ", " + c.locationY + ", " + (c.ownerUserId == 0? "NULL" : c.ownerUserId) + ", " + c.ownerExclusiveTime + ", " + Army.toInt(c.army) + ")";
        	}        	
    		helper.prepareQuery(sql);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Update state of country in database
	 * 
	 * @param lobbyId Lobby id
	 * @param c Country state
	 * @throws SQLException
	 */
	public static void updateCountryState(int lobbyId, Country c) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery("UPDATE lobby_country_state SET ownerUserId=?, ownerExclusiveTime=?, army=? WHERE lobbyId=? AND locationX=? and locationY=?");
	    	if (c.ownerUserId == 0)
	    		helper.setNull(1);
	    	else
	    		helper.setInt(1, c.ownerUserId);
	    	helper.setLong(2, c.ownerExclusiveTime);
	    	helper.setInt(3, Army.toInt(c.army));
	    	helper.setInt(4, lobbyId);
	    	helper.setInt(5, c.locationX);
	    	helper.setInt(6, c.locationY);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Balance of countries in a lobby
	 */
	public static class Balance
	{
		/**
		 * Number of countries owned by red
		 */
		private int numCountriesRed;
		
		/**
		 * Number of countries owned by blue
		 */
		private int numCountriesBlue;
		
		/**
		 * Constructor
		 */
		public Balance(int numCountriesRed, int numCountriesBlue)
		{
			this.numCountriesRed = numCountriesRed;
			this.numCountriesBlue = numCountriesBlue;
		}
		
		/**
		 * Number of countries owned by red
		 */
		public int getNumCountriesRed()
		{
			return numCountriesRed;
		}
		
		/**
		 * Number of countries owned by blue
		 */
		public int getNumCountriesBlue()
		{
			return numCountriesBlue;
		}
		
		/**
		 * Fraction of countries owned by red
		 */
		public float getFractionRed()
		{
			int total = numCountriesRed + numCountriesBlue;
			return total > 0? (float)numCountriesRed / total : 0.5f;
		}

		/**
		 * Fraction of countries owned by blue
		 */
		public float getFractionBlue()
		{
			int total = numCountriesRed + numCountriesBlue;
			return total > 0? (float)numCountriesBlue / total : 0.5f;
		}
	}

	/**
	 * Get balance for a lobby
	 * 
	 * @param lobbyId Id of lobby
	 */
	public static Balance getLobbyBalance(int lobbyId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery("SELECT SUM(IF(army=0, 1, 0)) AS army0, SUM(IF(army=1, 1, 0)) AS army1 FROM lobby_country_state WHERE lobbyId=?");
	    	helper.setInt(1, lobbyId);
	    	helper.executeQuery();
	    	
	    	if (helper.nextRecord())
	    		return new Balance(helper.getInt(1), helper.getInt(2));
	    	else
	    		return null;
        }
        finally
        {
        	helper.close();
        }		
	}
}
package onlinefrontlines.userstats;

import java.sql.SQLException;
import java.util.ArrayList;
import onlinefrontlines.utils.DbQueryHelper;

/**
 * This class communicates with the database and manages reading/writing of UnitStats objects
 * 
 * @see onlinefrontlines.userstats.UnitStats
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
public class UnitStatsDAO 
{
	/**
	 * Accumulates unit stats into database for a particular unit and user
	 */
	public static void accumulateUnitStats(int userId, UnitStats stats) throws SQLException	
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	// Update stats
	    	helper.prepareQuery("UPDATE user_stats_units SET numAttacks=numAttacks+?, numDefends=numDefends+?, damageDealt=damageDealt+?, damageReceived=damageReceived+?, kills=kills+?, deaths=deaths+? WHERE userId=? AND unitId=?");
	    	helper.setInt(1, stats.numAttacks);
	    	helper.setInt(2, stats.numDefends);
	    	helper.setInt(3, stats.damageDealt);
	    	helper.setInt(4, stats.damageReceived);
	    	helper.setInt(5, stats.kills);
	    	helper.setInt(6, stats.deaths);
	    	helper.setInt(7, userId);
	    	helper.setInt(8, stats.unitId);
	    	if (helper.executeUpdate() == 0)
	    	{
	    		// Insert new record
	    		helper.prepareQuery("INSERT INTO user_stats_units (userId, unitId, numAttacks, numDefends, damageDealt, damageReceived, kills, deaths) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		    	helper.setInt(1, userId);
		    	helper.setInt(2, stats.unitId);
		    	helper.setInt(3, stats.numAttacks);
		    	helper.setInt(4, stats.numDefends);
		    	helper.setInt(5, stats.damageDealt);
		    	helper.setInt(6, stats.damageReceived);
		    	helper.setInt(7, stats.kills);
		    	helper.setInt(8, stats.deaths);
		    	helper.executeUpdate();
	    	}
        }
        finally
        {
        	helper.close();
        }
	}

	/**
	 * Get list of unit stats for a user sorted on kills (highest amount of kills first)
	 * 
	 * @param userId User id
	 * @return List of stats
	 * @throws SQLException
	 */
	public static ArrayList<UnitStats> getUnitStats(int userId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	ArrayList<UnitStats> list = new ArrayList<UnitStats>();
        	
	    	helper.prepareQuery("SELECT unitId, numAttacks, numDefends, damageDealt, damageReceived, kills, deaths, name FROM user_stats_units JOIN units ON unitId=id WHERE userId=? ORDER BY kills DESC");
	    	helper.setInt(1, userId);
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
	    		UnitStats u = new UnitStats();
	    		u.unitId = helper.getInt(1);
	    		u.numAttacks = helper.getInt(2);
	    		u.numDefends = helper.getInt(3);
	    		u.damageDealt = helper.getInt(4);
	    		u.damageReceived = helper.getInt(5);
	    		u.kills = helper.getInt(6);
	    		u.deaths = helper.getInt(7);
	    		u.unitName = helper.getString(8);
	    		list.add(u);
	    	}
	    	
	    	return list;
        }
        finally
        {
        	helper.close();
        }
	}
}

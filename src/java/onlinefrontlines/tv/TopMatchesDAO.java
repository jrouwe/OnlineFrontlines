package onlinefrontlines.tv;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;

import onlinefrontlines.Constants;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.DbStoredProcHelper;

/**
 * This class communicates with the database and manages reading the top matches
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
public class TopMatchesDAO 
{
	/**
	 * Get list of top matches
	 */
	public static TopMatches getTopMatches() throws SQLException
	{
    	// Get last midnight
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(Calendar.HOUR_OF_DAY, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	long time = calendar.getTime().getTime();
    	
		DbStoredProcHelper helper = new DbStoredProcHelper();
		
        try
        {
    		TopMatches rv = new TopMatches();

    		helper.prepareCall("CALL getTopMatches(?, 1, 2, 500, 5, ?)");
    		helper.setLong(1, time);	    	
    		helper.setInt(2, Constants.USER_ID_AI);
    		ResultSet results = helper.executeQuery();
    		
	    	while (results.next())
	    	{
	    		TopMatches.Match m = new TopMatches.Match();
	    		m.gameId = results.getInt(1);
	    		m.mapId = results.getInt(2);
	    		m.countryConfigName = results.getString(3);
	    		m.mapType = results.getString(4);
	    		m.userId1 = results.getInt(5);
	    		m.userId2 = results.getInt(6);
	    		m.username1 = results.getString(7);
	    		m.username2 = results.getString(8);
	    		m.totalPoints1 = results.getInt(9);
	    		m.totalPoints2 = results.getInt(10);
	    		m.winningFaction = Faction.fromInt(results.getInt(11));
	    		m.turnNumber = results.getInt(12);
	    		m.faction1IsRed = results.getInt(13) != 0;
	    		m.unitsDestroyed = results.getInt(14);
	    		rv.matches.add(m);
	    	}
	    		    	
	    	return rv;
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
}

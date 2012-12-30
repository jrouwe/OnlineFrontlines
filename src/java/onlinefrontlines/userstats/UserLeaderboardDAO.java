package onlinefrontlines.userstats;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import onlinefrontlines.Army;
import onlinefrontlines.utils.DbQueryHelper;

/**
 * This class communicates with the database and reads leaderboards
 * 
 * @see onlinefrontlines.userstats.UserLeaderboard
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
public class UserLeaderboardDAO 
{
	/**
	 * Get leaderboard
	 * 
	 * @param table Table name
	 * @param column Extra column name
	 * @return List of stats
	 * @throws SQLException
	 */
	private static UserLeaderboard getLeaderboardInternal(String table, String column) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
    		UserLeaderboard board = new UserLeaderboard();

    		// Find user record
	    	helper.prepareQuery("SELECT userId, username, army, " + column + " FROM " + table);
	    	helper.executeQuery();
	    	
    		int position = 0;
    		long lastStatValue = -1;
    		
	    	while (helper.nextRecord())
	    	{
	    		UserLeaderboard.Entry s = new UserLeaderboard.Entry();
	    		s.userId = helper.getInt(1);
	    		s.username = helper.getString(2);
	    		s.army = Army.fromInt(helper.getInt(3));
	    		s.statValue = helper.getLong(4);
	    		board.addEntry(s);
	    		
	    		if (s.statValue != lastStatValue)
	    		{
	    			position++;
	    			lastStatValue = s.statValue;
	    		}
	    		s.position = position;
	    	}

	    	return board;
        }
        finally
        {
        	helper.close();
        }
	}
	
	public static UserLeaderboard getLeaderboardTotalPoints() throws SQLException
	{
		return getLeaderboardInternal("user_stats_total_points", "totalPoints");
	}
	
	public static UserLeaderboard getLeaderboardUnitsDestroyed() throws SQLException
	{
		return getLeaderboardInternal("user_stats_units_destroyed", "totalUnitsDestroyed");
	}

	public static UserLeaderboard getLeaderboardWinPercentage() throws SQLException
	{
		return getLeaderboardInternal("user_stats_win_percentage", "winPercentage"); 
	}

	public static UserLeaderboard getLeaderboardTotalCaptures() throws SQLException
	{
		return getLeaderboardInternal("user_stats_total_captures", "totalCaptures");
	}

	public static UserLeaderboard getLeaderboardTotalCountries() throws SQLException
	{
		return getLeaderboardInternal("user_stats_total_countries", "totalCountries"); 
	}
	
	public static class MostActive
	{
		public int position;
		public int userId;
		public String username;
		
		public int getPosition()
		{
			return position;
		}
		
		public int getUserId()
		{
			return userId;
		}
		
		public String getUsername()
		{
			return username;
		}
	}
	
	public static List<MostActive> getLeaderboardMostActive() throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	long time = System.currentTimeMillis();
        	long maxTime = 3L * 31 * 24 * 3600 * 1000;

    		helper.prepareQuery("SELECT tbl.userId, users.username, COUNT(1) AS statValue FROM " +
    								"(SELECT userId1 AS userId FROM games WHERE ? - creationTime < ? UNION ALL SELECT userId2 FROM games WHERE ? - creationTime < ?) " +
    							"AS tbl JOIN users ON users.id = tbl.userId WHERE users.facebookId IS NOT NULL GROUP BY userId ORDER BY statValue DESC LIMIT 10");
    		helper.setLong(1, time); 
    		helper.setLong(2, maxTime); 
    		helper.setLong(3, time); 
    		helper.setLong(4, maxTime);    				
	    	helper.executeQuery();

			int position = 0;
			
			ArrayList<MostActive> mostActive = new ArrayList<MostActive>();
			
	    	while (helper.nextRecord())
	    	{
	    		MostActive s = new MostActive();
	    		s.position = ++position;
	    		s.userId = helper.getInt(1);
	    		s.username = helper.getString(2);
	    		mostActive.add(s);
	    	}

	    	return mostActive;
	    }
        finally
        {
        	helper.close();
        }
	}
}

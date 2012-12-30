package onlinefrontlines.userstats;

import java.sql.SQLException;
import java.util.ArrayList;
import onlinefrontlines.utils.DbQueryHelper;
import onlinefrontlines.lobby.LobbyConfig;

/**
 * This class communicates with the database and manages reading/writing of UserStats objects
 * 
 * @see onlinefrontlines.userstats.UserStats
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
public class UserStatsDAO 
{
	/**
	 * Get stats for particular user
	 * 
	 * @param userId Id of user
	 * @return Stats for user
	 * @throws SQLException
	 */
	public static UserStats getStats(int userId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find user record
	    	helper.prepareQuery("SELECT gamesPlayed, gamesWon, gamesLost, totalPoints, currentVictoryStreak, maxVictoryStreak, creationTime FROM user_stats WHERE userId=?");
	    	helper.setInt(1, userId);
	    	helper.executeQuery();
	    	
	    	// Create empty object
	    	UserStats stats = new UserStats(userId);
	    	
	    	// Validate stats record exists
	    	if (!helper.nextRecord())
	    		return stats;
	    	
	    	// Fill in fields
	    	stats.gamesPlayed = helper.getInt(1);
	    	stats.gamesWon = helper.getInt(2);
	    	stats.gamesLost = helper.getInt(3);
	    	stats.totalPoints = helper.getInt(4);
	    	stats.currentVictoryStreak = helper.getInt(5);
	    	stats.maxVictoryStreak = helper.getInt(6);
	    	stats.creationTime = helper.getLong(7);
	    	
	    	return stats;
        }
        finally
        {
        	helper.close();
        }
	}

	/**
	 * Accumulates stats into database for a particular game
	 * 
	 * @param stats Stats to accumulate (note that these are stats for one game only, not totals)
	 */
	public static void accumulateStats(UserStats stats) throws SQLException	
	{
		assert(stats.gamesWon == 0 || stats.gamesWon == 1);
		assert(stats.gamesLost == 0 || stats.gamesLost == 1);
		assert(stats.gamesWon == 0 || stats.gamesLost == 0);
		
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	// Update stats
    		String vicStreak = stats.gamesWon > 0? "currentVictoryStreak=currentVictoryStreak+1, maxVictoryStreak=IF(currentVictoryStreak>maxVictoryStreak,currentVictoryStreak,maxVictoryStreak)" : "currentVictoryStreak=0";
	    	helper.prepareQuery("UPDATE user_stats SET gamesPlayed=gamesPlayed+?, gamesWon=gamesWon+?, gamesLost=gamesLost+?, totalPoints=totalPoints+?, " + vicStreak + " WHERE userId=?");
	    	helper.setInt(1, stats.gamesPlayed);
	    	helper.setInt(2, stats.gamesWon);
	    	helper.setInt(3, stats.gamesLost);
	    	helper.setInt(4, stats.totalPoints);
	    	helper.setInt(5, stats.userId);
	    	if (helper.executeUpdate() == 0)
	    	{
	    		// Insert new record
	    		helper.prepareQuery("INSERT INTO user_stats (userId, gamesPlayed, gamesWon, gamesLost, totalPoints, currentVictoryStreak, maxVictoryStreak, creationTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		    	helper.setInt(1, stats.userId);
		    	helper.setInt(2, stats.gamesPlayed);
		    	helper.setInt(3, stats.gamesWon);
		    	helper.setInt(4, stats.gamesLost);
		    	helper.setInt(5, stats.totalPoints);
		    	helper.setInt(6, stats.gamesWon);
		    	helper.setInt(7, stats.gamesWon);
		    	helper.setLong(8, stats.creationTime);
		    	helper.executeUpdate();
	    	}
	    	
	    	// Invalidate cache
	    	UserStatsCache.getInstance().remove(stats.userId);
        }
        finally
        {
        	helper.close();
        }
	}

	/**
	 * Stores a number per lobby
	 */
	public static class CountPerLobby
	{
		/**
		 * Lobby id of lobby in which was captured
		 */
		public int lobbyId;
		
		/**
		 * Number of times captured
		 */
		public int count;
		
		/**
		 * Constructor
		 */
		public CountPerLobby(int lobbyId, int count)
		{
			this.lobbyId = lobbyId;
			this.count = count;
		}
		
		/**
		 * Number of times captured
		 */
		public int getCount()
		{
			return count;
		}
		
		/**
		 * Get lobby config for this lobby
		 */
		public LobbyConfig getLobbyConfig()
		{
			return LobbyConfig.allLobbiesMap.get(lobbyId);
		}
	};
	
	/**
	 * Get captures that a user made
	 * 
	 * @param userId User id
	 * @return List of captures
	 * @throws SQLException
	 */
	public static ArrayList<CountPerLobby> getCaptures(int userId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	ArrayList<CountPerLobby> list = new ArrayList<CountPerLobby>();
        	
	    	helper.prepareQuery("SELECT lobbyId, count FROM user_stats_captures WHERE userId=?");
	    	helper.setInt(1, userId);
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    		list.add(new CountPerLobby(helper.getInt(1), helper.getInt(2)));
	    	
	    	return list;
        }
        finally
        {
        	helper.close();
        }
	}

	/**
	 * Increment amount of capture points captured
	 * 
	 * @param userId Id for user
	 * @param lobbyId Lobby id of lobby where point was captured
	 * @throws SQLException
	 */
	public static void addCapture(int userId, int lobbyId) throws SQLException	
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	// Update record
	    	helper.prepareQuery("UPDATE user_stats_captures SET count=count+1 WHERE userId=? AND lobbyId=?");
	    	helper.setInt(1, userId);
	    	helper.setInt(2, lobbyId);
	    	if (helper.executeUpdate() == 0)
	    	{
	    		// Insert new record
	    		helper.prepareQuery("INSERT INTO user_stats_captures (userId, lobbyId, count) VALUES (?, ?, 1)");
		    	helper.setInt(1, userId);
		    	helper.setInt(2, lobbyId);
		    	helper.executeUpdate();
	    	}
        }
        finally
        {
        	helper.close();
        }
	}
	
	/**
	 * Get number of countries a user owns
	 * 
	 * @param userId User id
	 * @return List of owned countries
	 * @throws SQLException
	 */
	public static ArrayList<CountPerLobby> getOwnedCountries(int userId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	ArrayList<CountPerLobby> list = new ArrayList<CountPerLobby>();
        	
	    	helper.prepareQuery("SELECT lobbyId, COUNT(1) FROM lobby_country_state WHERE ownerUserId=? GROUP BY lobbyId");
	    	helper.setInt(1, userId);
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    		list.add(new CountPerLobby(helper.getInt(1), helper.getInt(2)));
	    	
	    	return list;
        }
        finally
        {
        	helper.close();
        }
	}
}

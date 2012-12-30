package onlinefrontlines.userstats;

import onlinefrontlines.utils.CacheException;

/**
 * Names of ranks
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
public class UserRank 
{
	/**
	 * Level needed for a particular rank
	 */
	private static int[] ranks =
	{
		5,
		10,
		20,
		30,
		50,
		75,
		100,
		125,
		150,
		200,
		250,
		300
	};
	
	/**
	 * Position needed for a particular rank
	 */
	private static int[] positions =
	{
		20,
		15,
		10,
		5,
		1,
	};
	
	/**
	 * User friendly names for the ranks
	 */
	private static String[] rankNames =
	{
		"Private", 
		"Private First Class", 
		"Corporal",
		"Sergeant",  
		"Staff Sergeant", 
		"Sergeant First Class",  
		"Master Sergeant", 
		"Second Lieutenant",  
		"First Lieutenant", 
		"Captain", 
		"Major",
		"Lieutenant Colonel", 
		"Colonel", 
		"Brigadier General",  
		"Major General", 
		"Lieutenant General", 
		"General",  
		"General of the Army"	
	};
	
	/**
	 * Get level of user
	 * 
	 * @param totalPoints Total points achieved in user_stats
	 */
	public static int getLevelInternal(long totalPoints)
	{
		return (int)(totalPoints / 1000);
	}
	
	/**
	 * Get points needed to next level
	 * 
	 * @param totalPoints Total points achieved in user_stats
	 */
	public static int getPointsNeededToNextLevelInternal(long totalPoints)
	{
		return 1000 - (int)(totalPoints % 1000);
	}

	/**
	 * Get rank for user
	 * 
	 * @param totalPoints Total points achieved in user_stats
	 */
	public static int getRankInternal(int leaderboardPosition, long totalPoints)
	{
		// Rank based on leaderboard position
		int rank = 0;
		while (rank < positions.length && leaderboardPosition <= positions[rank])
			++rank;
		if (rank > 0)
			return ranks.length + rank + 1;
		
		// Rank based on level
		int level = getLevelInternal(totalPoints);		
		rank = 0;
		while (rank < ranks.length && level >= ranks[rank])
			rank++;
		return rank + 1;		
	}
	
	/**
	 * Get level for a particular user
	 * 
	 * @param userId
	 * @return
	 */
	public static int getLevel(int userId)
	{
		try
		{
			UserStats stats = UserStatsCache.getInstance().get(userId);
			if (stats != null)
				return getLevelInternal(stats.totalPoints);
		}
		catch (CacheException e)
		{			
		}
		
		// No known stats
		return 0;
	}

	/**
	 * Get amount of points needed to reach next level
	 */
	public static int getPointsNeededToNextLevel(int userId)
	{
		try
		{
			UserStats stats = UserStatsCache.getInstance().get(userId);
			if (stats != null)
				return getPointsNeededToNextLevelInternal(stats.totalPoints);
		}
		catch (CacheException e)
		{			
		}
		
		// No known stats
		return 0;
	}
	
	/**
	 * Get rank for a particular user
	 * 
	 * @param userId
	 * @return
	 */
	public static int getRank(int userId)
	{
		try
		{
			// If user is in the total points leaderboard his rank might be boosted
			UserLeaderboard board = UserLeaderboardCache.getInstance().get(UserLeaderboardCache.BOARD_TOTAL_POINTS);
			UserLeaderboard.Entry entry = board.getEntry(userId);		
			if (entry != null)
				return getRankInternal(entry.position, entry.statValue);
	
			// Just base the rank on users stats
			UserStats stats = UserStatsCache.getInstance().get(userId);
			if (stats != null)
				return getRankInternal(Integer.MAX_VALUE, stats.totalPoints);
		}
		catch (CacheException e)
		{			
		}
		
		// No known stats, return first rank
		return 1;		
	}
	
	/**
	 * Get rank name
	 */
	public static String getRankName(int userId)
	{
		int rank = getRank(userId);
		return rankNames[rank - 1];
	}
}

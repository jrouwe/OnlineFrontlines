package onlinefrontlines.userstats;

import java.util.Calendar;

/**
 * Contains all 'progress' for a user
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
public class UserStats
{
	/**
	 * Id of user to whom these stats belong
	 */
	public int userId;
	
	/**
	 * Time these stats were created
	 */
	public long creationTime = Calendar.getInstance().getTime().getTime();
	
	/**
	 * Number of games played
	 */
	public int gamesPlayed = 0;
	
	/**
	 * Number of games won
	 */
	public int gamesWon = 0;
	
	/**
	 * Number of games lost
	 */
	public int gamesLost = 0;
	
	/**
	 * Total number of points
	 */
	public int totalPoints = 0;
	
	/**
	 * Current number of victories in a row
	 */
	public int currentVictoryStreak = 0;
	
	/**
	 * Max number of victories in a row
	 */
	public int maxVictoryStreak = 0;
	
	/**
	 * Constructor
	 * 
	 * @param userId Id of user
	 */
	public UserStats(int userId)
	{
		this.userId = userId;
	}
	
	/**
	 * Get time these stats were created
	 */
	public long getCreationTime()
	{
		return creationTime;
	}
	
	/**
	 * Get user level
	 */
	public int getLevel()
	{
		return UserRank.getLevel(userId);
	}
	
	/**
	 * Get amount of points needed for next level
	 */
	public int getPointsNeededToNextLevel()
	{
		return UserRank.getPointsNeededToNextLevel(userId);
	}
	
	/**
	 * Number of games played
	 */
	public int getGamesPlayed()
	{
		return gamesPlayed;
	}
	
	/**
	 * Number of games won
	 */
	public int getGamesWon()
	{
		return gamesWon;
	}
	
	/**
	 * Number of games lost
	 */
	public int getGamesLost()
	{
		return gamesLost;
	}
	
	/**
	 * Total number of points
	 */
	public int getTotalPoints()
	{
		return totalPoints;
	}
	
	/**
	 * Current number of victories in a row
	 */
	public int getCurrentVictoryStreak()
	{
		return currentVictoryStreak;
	}
	
	/**
	 * Max number of victories in a row
	 */
	public int getMaxVictoryStreak()
	{
		return maxVictoryStreak;
	}

	/**
	 * Get user rank
	 */
	public int getRank() 
	{
		return UserRank.getRank(userId);
	}
	
	/**
	 * Get rank name
	 */
	public String getRankName()
	{
		return UserRank.getRankName(userId);
	}
}

package onlinefrontlines.userstats.web;

import java.util.*;
import java.sql.SQLException;
import onlinefrontlines.auth.User;
import onlinefrontlines.auth.UserCache;
import onlinefrontlines.web.*;
import onlinefrontlines.feedback.FeedbackDAO;
import onlinefrontlines.lobby.*;
import onlinefrontlines.userstats.*;
import onlinefrontlines.utils.*;
import onlinefrontlines.game.*;
import onlinefrontlines.game.GameStateDAO.FinishedGame;

/**
 * This action shows the user profile for a particular user
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
public class UserStatsAction extends WebAction 
{
	/**
	 * User to get stats for
	 */
	public int userId;
	
	/**
	 * User properties
	 */
	public User returnedUser;
	
	/**
	 * Unit stats for the user
	 */
	private ArrayList<UnitStats> unitAttackStats;
	private UnitStats[] unitDefendStats;
	
	/**
	 * Helper class to get medal info to the JSP page
	 */
	public static class MedalInfo
	{
		public String medalImage;
		public String medalToolTip;
		
		/**
		 * Image name for this medal
		 */
		public String getMedalImage()
		{
			return medalImage;
		}
		
		/**
		 * Tool tip for this medal
		 */
		public String getMedalToolTip()
		{
			return medalToolTip;
		}
	}	
	
	/**
	 * Possible medals
	 */
	private static enum Medal
	{
		gold,
		silver,
		bronze,
		none
	}
	
	/**
	 * Comparator for sorting unit stats
	 */
	private static class SortOnDeaths implements Comparator<UnitStats>
	{
		 public int compare(UnitStats o1, UnitStats o2)
		 {
			 return o2.deaths - o1.deaths;
		 }
	}
	
	/**
	 * Get unit stats sorted on most kills
	 */
	public ArrayList<UnitStats> getUnitAttackStats()
	{
		if (unitAttackStats == null)
			determineUnitStats();
		
		return unitAttackStats;
	}

	/**
	 * Get unit stats sorted on most deaths
	 */
	public UnitStats[] getUnitDefendStats()
	{
		if (unitDefendStats == null)
			determineUnitStats();
		
		return unitDefendStats;
	}

	/**
	 * Get unit stats from database
	 */
	public void determineUnitStats()
	{
		try
		{
	    	// Get unit stats
			unitAttackStats = UnitStatsDAO.getUnitStats(userId);
	
			// Get unit defend stats
	    	unitDefendStats = unitAttackStats.toArray(new UnitStats[0]);
	    	Arrays.sort(unitDefendStats, new SortOnDeaths());
		}
		catch (SQLException e)
		{
			unitAttackStats = new ArrayList<UnitStats>();
			unitDefendStats = new UnitStats[0];

			Tools.logException(e);
		}
	}
	
	/**
	 * Get amount of units destroyed
	 */
	public int getTotalKills()
	{
		int total = 0;
		for (UnitStats s : getUnitAttackStats())
			total += s.kills;
		return total;			
	}
	
	/**
	 * Get amount of units lost
	 */
	public int getTotalDeaths()
	{
		int total = 0;
		for (UnitStats s : getUnitAttackStats())
			total += s.deaths;
		return total;			
	}

	/**
	 * Get total damage dealt
	 */
	public int getTotalDamageDealt()
	{
		int total = 0;
		for (UnitStats s : getUnitAttackStats())
			total += s.damageDealt;
		return total;			
	}

	/**
	 * Get total damage received
	 */
	public int getTotalDamageReceived()
	{
		int total = 0;
		for (UnitStats s : getUnitAttackStats())
			total += s.damageReceived;
		return total;			
	}

	/**
	 * Get medal for lobby
	 */
	private Medal getMedalForLobby(ArrayList<UserStatsDAO.CountPerLobby> captures, int lobbyId)
	{
		// Find lobby
		for (UserStatsDAO.CountPerLobby c : captures)
			if (c.lobbyId == lobbyId)
			{
				// Base medal on number of captures
				if (c.count >= 3)
					return Medal.gold;
				else if (c.count >= 2)
					return Medal.silver;
				else if (c.count >= 1)
					return Medal.bronze;
				else
					return Medal.none;
			}
		
		return Medal.none;
	}
	
	/**
	 * Get medal for number of victories in a row
	 */
	private Medal getMedalForVictoryStreak()
	{
		UserStats stats = getStats();
		
		if (stats.maxVictoryStreak >= 10)
			return Medal.gold;
		else if (stats.maxVictoryStreak >= 5)
			return Medal.silver;
		else if (stats.maxVictoryStreak >= 3)
			return Medal.bronze;
		else
			return Medal.none;
	}
	
	/**
	 * Get medal for number of units destroyed
	 */
	private Medal getMedalForUnitsDestroyed()
	{
		int totalUnitsDestroyed = getTotalKills();
		if (totalUnitsDestroyed >= 500)
			return Medal.gold;
		else if (totalUnitsDestroyed >= 250)
			return Medal.silver;
		else if (totalUnitsDestroyed >= 100)
			return Medal.bronze;
		else
			return Medal.none;
	}
	
	/**
	 * Get medal for number of victories 
	 */
	private Medal getMedalForNumVictories()
	{
		UserStats stats = getStats();
		
		if (stats.gamesWon >= 50)
			return Medal.gold;
		else if (stats.gamesWon >= 25)
			return Medal.silver;
		else if (stats.gamesWon >= 10)
			return Medal.bronze;
		else
			return Medal.none;
	}
	
	/**
	 * Get medal image name
	 */
	private MedalInfo getMedalInfo(String prefix, Medal medal)
	{
		MedalInfo info = new MedalInfo();
		if (medal != Medal.none)
			info.medalImage = prefix + "_" + medal.toString();
		else
			info.medalImage = "medal_none";
		info.medalToolTip = getText(prefix + "MedalToolTip");
		return info;
	}
	
	/**
	 * Get all gained medals
	 */
	public ArrayList<MedalInfo> getMedals()
	{
		ArrayList<MedalInfo> medals = new ArrayList<MedalInfo>();
		
		try
		{
			ArrayList<UserStatsDAO.CountPerLobby> captures = UserStatsDAO.getCaptures(userId);
			
			for (LobbyConfig c : LobbyConfig.allLobbies)
	    		medals.add(getMedalInfo(c.getFilenamePrefix(), getMedalForLobby(captures, c.id)));
		}
		catch (SQLException e)
		{
			Tools.logException(e);
		}
		
    	medals.add(getMedalInfo("star", getMedalForVictoryStreak()));
    	medals.add(getMedalInfo("cross", getMedalForUnitsDestroyed()));
    	medals.add(getMedalInfo("sun", getMedalForNumVictories()));
		
		return medals;
	}

	/**
	 * Get number of unlocked continents
	 */
	public int getNumUnlockedContinents()
	{
		// Get user level
		UserStats stats = getStats();
		int level = stats.getLevel();
		
		// Calculate number of lobbies unlocked
		int numUnlocked = 0;
		for (LobbyConfig l : LobbyConfig.allLobbies)
			if (level >= l.minRequiredLevel)
				++numUnlocked;		
		return numUnlocked;
	}
	
	/**
	 * Get all counts of owned countries
	 */
	public ArrayList<UserStatsDAO.CountPerLobby> getOwnedCountries()
	{
		try
		{
	    	// Get number of owned countries
			ArrayList<UserStatsDAO.CountPerLobby> ownedCountries = UserStatsDAO.getOwnedCountries(userId);
	    	
	    	// Make sure all lobbies are represented
	    	for (LobbyConfig c : LobbyConfig.allLobbies)
	    	{
	    		boolean found = false;
	    		for (UserStatsDAO.CountPerLobby o : ownedCountries)
	    			if (o.lobbyId == c.id)
	    			{
	    				found = true;
	    				break;
	    			}	
	    		if (!found)
	    			ownedCountries.add(new UserStatsDAO.CountPerLobby(c.id, 0));
	    	}
	    	
	    	return ownedCountries;
		}
		catch (SQLException e)
		{
			Tools.logException(e);
			
			return new ArrayList<UserStatsDAO.CountPerLobby>();
		}
	}
	
	/**
	 * Get games played by this user
	 */
	public ArrayList<FinishedGame> getGamesPlayed()
	{
		try
		{
			return GameStateDAO.getFinishedGames(userId);
		}
		catch (SQLException e)
		{
			Tools.logException(e);

			return new ArrayList<FinishedGame>();
		}
	}
	
	/**
	 * Get feedback for current user
	 * 
	 * @return Feedback score
	 */
	public int getFeedback()
	{
    	try
    	{
    		return FeedbackDAO.getScore(userId);
    	}
    	catch (SQLException e)
    	{
			Tools.logException(e);
    		
    		return 0;
    	}
	}

	/**
	 * Get path to avatar image
	 * 
	 * @return Path to avatar image
	 */
	public String getUserImage()
	{
		try
		{
			User user = UserCache.getInstance().get(userId);
			return user.getProfileImageURL();
		}
		catch (CacheException e)
		{
			Tools.logException(e);
			
			return "";
		}
	}
	
	private UserStats cachedStats;

	/**
	 * Get stats for user
	 * 
	 * @return Stats
	 */
	public UserStats getStats()
	{
		if (cachedStats == null)
		{
			try
			{
		    	cachedStats = UserStatsCache.getInstance().get(userId);
			}
			catch (CacheException e)
			{
	    		cachedStats = new UserStats(userId);

				Tools.logException(e);
			}
		}
		
		return cachedStats;
	}
	
	/**
     * Execute the action
     */
	protected WebView execute() throws Exception 
    {   
    	// Get user
    	returnedUser = UserCache.getInstance().get(userId);
    	if (returnedUser == null)
    	{
    		addActionError(getText("userDoesNotExist"));
    		return getErrorView();
    	}
    	
    	return getSuccessView();
    }
}

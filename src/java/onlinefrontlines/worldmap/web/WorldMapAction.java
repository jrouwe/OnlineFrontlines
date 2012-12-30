package onlinefrontlines.worldmap.web;

import java.util.ArrayList;
import java.sql.SQLException;
import onlinefrontlines.lobby.*;
import onlinefrontlines.game.GameStateDAO;
import onlinefrontlines.userstats.UserRank;
import onlinefrontlines.utils.Tools;
import onlinefrontlines.web.*;

/**
 * This action displays the world map
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
public class WorldMapAction extends WebAction 
{
	/**
	 * Current level of user
	 */
	public int userLevel;
	
	/**
	 * Summary of a lobby
	 */
	public static class Lobby
	{
		/**
		 * Get lobby configuration
		 */
		public LobbyConfig lobbyConfig;
		
		/**
		 * Lobby balance
		 */
		public LobbyCountryStateDAO.Balance balance;
		
		/**
		 * Number of games active
		 */
		public int gameCount;
		
		/**
		 * Number of challenges active
		 */
		public int challengeCount;
		
		/**
		 * Constructor
		 */
		public Lobby(LobbyConfig lobbyConfig)
		{
			this.lobbyConfig = lobbyConfig;
			
			try
			{
				balance = LobbyCountryStateDAO.getLobbyBalance(lobbyConfig.id);
				gameCount = GameStateDAO.getGameCountForLobby(lobbyConfig.id);
				challengeCount = LobbyUserDAO.getChallengeCountForLobby(lobbyConfig.id);
			}
			catch (SQLException e)
			{
				balance = new LobbyCountryStateDAO.Balance(0, 0);
				gameCount = 0;
				challengeCount = 0;

				Tools.logException(e);
			}
		}
		
		/**
		 * Get lobby configuration
		 */
		public LobbyConfig getLobbyConfig()
		{
			return lobbyConfig;
		}
		
		/**
		 * Lobby balance
		 */
		public LobbyCountryStateDAO.Balance getBalance()
		{
			return balance;
		}
		
		/**
		 * Get number of games in progress
		 */
		public int getGameCount()
		{
			return gameCount;
		}
		
		/**
		 * Return number of open challenges
		 */
		public int getChallengeCount()
		{
			return challengeCount;
		}
	}
	
	/**
	 * All lobbies
	 */
	private ArrayList<Lobby> lobbies;
	
	public ArrayList<Lobby> getLobbies()
	{
		if (lobbies == null)
		{
	    	// Determine lobbies
			lobbies = new ArrayList<Lobby>();
	    	for (LobbyConfig l : LobbyConfig.allLobbies)
	    		lobbies.add(new Lobby(l));
		}
		
		return lobbies;
	}
	
	/**
	 * Get total amount of games
	 */
	public int getTotalGames()
	{
		int count = 0;
		for (Lobby l : getLobbies())
			count += l.gameCount;
		return count;
	}
	
	/**
	 * Get total fraction of countries belonging to red
	 */
	public float getTotalFractionRed()
	{
		int red = 0, blue = 0;
		for (Lobby l : getLobbies())
		{
			red += l.balance.getNumCountriesRed();
			blue += l.balance.getNumCountriesBlue();
		}
		
		int total = red + blue;
		return total > 0? (float)red / total : 0.5f;		
	}
	
	/**
	 * Get total fraction of countries belonging to blue
	 */
	public float getTotalFractionBlue()
	{
		int red = 0, blue = 0;
		for (Lobby l : getLobbies())
		{
			red += l.balance.getNumCountriesRed();
			blue += l.balance.getNumCountriesBlue();
		}
		
		int total = red + blue;
		return total > 0? (float)red / total : 0.5f;		
	}

	/**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	// Determine level
    	userLevel = user != null? UserRank.getLevel(user.id) : 0;
    	
    	return getSuccessView();
    }
}

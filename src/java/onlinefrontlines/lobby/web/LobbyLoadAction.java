package onlinefrontlines.lobby.web;

import org.apache.log4j.Logger;
import onlinefrontlines.game.CountryConfig;
import onlinefrontlines.game.CountryConfigCache;
import onlinefrontlines.lobby.LobbyConfig;
import onlinefrontlines.userstats.UserRank;
import onlinefrontlines.web.*;

/**
 * This action is called from the lobby flash application to load the data from a specified lobby.
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
public class LobbyLoadAction extends WebAction
{
	private static final Logger log = Logger.getLogger(LobbyLoadAction.class);
	
	/**
	 * Id of the lobby to load
	 */
    public int lobbyId;
    
    /**
     * User id of connected user
     */
    public int userId;
    
    /**
     * Comma separated list of user ids for friends of the connected user
     */
    public String friends = "";
        
    /**
     * Background image
     */
    public int backgroundImageNumber;
    
    /**
     * Country config ids
     */
    public String tileCountryConfigIds;
    
    /**
     * All referenced country configs
     */
    public CountryConfig[] usedCountryConfigs;
    
    /**
     * Returned error code
     */
    public int errorCode = -1;
    
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
        // Get lobby
		LobbyConfig lobby = LobbyConfig.allLobbiesMap.get(lobbyId);
		if (lobby == null)
		{
			errorCode = -2;
			return getErrorView();
		}
		
    	// Check required level
		int level = user != null? UserRank.getLevel(user.id) : 0;
    	if (level < lobby.minRequiredLevel
    		|| (lobby.maxLevel >= 0 && level > lobby.maxLevel))
    	{
    		errorCode = -3;
    		return getErrorView();
    	}
    	
    	// Check user or spectator
    	if (user != null)
    	{
	    	// Store user id
	        userId = user.id;
    	}
    	else
    	{
    		// Spectator
    		userId = 0;
    	}
        
		// Image
		backgroundImageNumber = lobby.backgroundImageNumber;
		
		// Get country configs layout
		tileCountryConfigIds = lobby.tileCountryConfigIdsToString();
		
		// Get used country configs
		int[] used = lobby.getUsedCountryConfigIds(); 
		usedCountryConfigs = new CountryConfig[used.length];
		for (int i = 0; i < used.length; ++i)
			usedCountryConfigs[i] = CountryConfigCache.getInstance().get(used[i]);
		
        log.info("User '" + userId + "' loaded lobby '" + lobbyId + "'");

        return getSuccessView();
    }
}

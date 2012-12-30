package onlinefrontlines.lobbyedit.web;

import onlinefrontlines.lobby.*;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.web.*;
import org.apache.log4j.Logger;

/**
 * This action is called from the lobby editor flash application to save changes to a lobby.
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
public class LobbyEditSaveAction extends WebAction  
{
	private static final Logger log = Logger.getLogger(LobbyEditSaveAction.class);
		
	/**
	 * Id of the lobby to save
	 */
    public int lobbyId;
    
    /**
     * Country config ids
     */
    public String tileCountryConfigIds;
    
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	log.info("Saving lobby '" + lobbyId + "'");
    	
    	// Get lobby
		LobbyConfig lobby = LobbyConfig.allLobbiesMap.get(lobbyId);
		if (lobby == null)
			return getErrorView();

		// Modify data
		lobby.tileCountryConfigIdsFromString(tileCountryConfigIds);

		// Save the lobby
		LobbyConfigDAO.save(lobby);
		
    	// Reload all lobbies
    	LobbyConfig.loadAll();
    	LobbyStateCache.getInstance().removeAll();
    	CacheTag.purgeAll();
    	
    	return getSuccessView();
    }
}

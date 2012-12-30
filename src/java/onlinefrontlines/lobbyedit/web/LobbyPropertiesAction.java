package onlinefrontlines.lobbyedit.web;

import onlinefrontlines.lobby.*;
import onlinefrontlines.web.*;

/**
 * This action updates the properties for a lobby
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
public class LobbyPropertiesAction extends WebAction 
{
	/**
	 * Id of the lobby
	 */
	public int lobbyId;
	
	/**
	 * Name of the lobby
	 */
	public String lobbyName;
		
	/**
	 * Image number for the lobby
	 */
	public int backgroundImageNumber;
	
	/**
	 * Location of enter button
	 */
	public int worldMapEnterButtonX;
	public int worldMapEnterButtonY;
	
	/**
	 * Minimal required level to enter
	 */
	public int minRequiredLevel;
	
	/**
	 * Maximum level
	 */
	public int maxLevel;
	
	/**
	 * Maximum amount of users that can enter the lobby
	 */
	public int maxUsers;
	
	/**
	 * Input
	 */
	protected WebView input() throws Exception
	{
		// Get lobby
		LobbyConfig lobby = LobbyConfigDAO.load(lobbyId);
		
		// Check lobby
		if (lobby == null)
		{
			addActionError(getText("lobbyDoesNotExist"));
			return getErrorView();
		}
		
		// Get variables
		lobbyName = lobby.name;
		backgroundImageNumber = lobby.backgroundImageNumber;
		worldMapEnterButtonX = lobby.worldMapEnterButtonX;
		worldMapEnterButtonY = lobby.worldMapEnterButtonY;
		minRequiredLevel = lobby.minRequiredLevel;
		maxLevel = lobby.maxLevel;
		maxUsers = lobby.maxUsers;
		
		return getInputView();
	}
	
    /**
     * Execute the action
     */
	protected WebView execute() throws Exception 
    {
		// Get lobby
		LobbyConfig lobby = LobbyConfigDAO.load(lobbyId);
		
		// Check lobby
		if (lobby == null)
		{
			addActionError(getText("lobbyDoesNotExist"));
			return getErrorView();
		}

		// Set variables
		lobby.backgroundImageNumber = backgroundImageNumber;
		lobby.worldMapEnterButtonX = worldMapEnterButtonX;
		lobby.worldMapEnterButtonY = worldMapEnterButtonY;
		lobby.minRequiredLevel = minRequiredLevel;
		lobby.maxLevel = maxLevel;
		lobby.maxUsers = maxUsers;
		LobbyConfigDAO.save(lobby);			
		
    	// Reload all lobbies
    	LobbyConfig.loadAll();
    	LobbyStateCache.getInstance().removeAll();
    	
    	return getSuccessView();
    }
}

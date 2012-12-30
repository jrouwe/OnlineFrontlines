package onlinefrontlines.lobbyedit.web;

import onlinefrontlines.lobby.*;
import java.sql.SQLException;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.web.*;

/**
 * This action creates a new (empty) lobby in the database based on the name that the user supplied.
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
public class LobbyCreateAction extends WebAction 
{
	/**
	 * Name for the new lobby
	 */
	public String lobbyName;

	/**
     * Execute the action
     */
	protected WebView execute() throws Exception 
    {
		// Validate lobby name
		if (lobbyName == null || lobbyName.isEmpty())
		{
			addFieldError("lobbyName", getText("lobbyNameRequired"));
			return getInputView();
		}
		
		// Validate max length
		if (lobbyName.length() > 32)
		{
			addFieldError("lobbyName", getText("nameTooLong"));
			return getInputView();
		}

		// Create new empty lobby
		LobbyConfig lobby = new LobbyConfig(-1, lobbyName, 0, 37, 43, 0, 0, 0, -1, 100);
		
		// Insert it in the database
		try
		{
			LobbyConfigDAO.create(lobby);
        }
        catch (SQLException e)
        {
        	addActionError(getText("lobbyAlreadyExists"));
        	return getInputView();
        }			
		
    	// Reload all lobbies
    	LobbyConfig.loadAll();
    	LobbyStateCache.getInstance().removeAll();
    	CacheTag.purgeAll();
    	
    	return getSuccessView();
    }
}

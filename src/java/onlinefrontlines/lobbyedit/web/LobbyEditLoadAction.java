package onlinefrontlines.lobbyedit.web;

import org.apache.log4j.Logger;
import java.util.ArrayList;
import onlinefrontlines.game.CountryConfigDAO;
import onlinefrontlines.lobby.*;
import onlinefrontlines.web.*;

/**
 * This action is called from the lobby editor flash application to load the data from a specified lobby.
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
public class LobbyEditLoadAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(LobbyEditLoadAction.class);
	
	/**
	 * Id of the lobby to load
	 */
    public int lobbyId;
    
    /**
     * Background image
     */
    public int backgroundImageNumber;
    
    /**
     * Country config ids
     */
    public String tileCountryConfigIds;
    
    /**
     * Available country configs
     */
    public ArrayList<CountryConfigDAO.Summary> countryConfigs;
    
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	log.info("Loading lobby '" + lobbyId + "'");

    	// Get lobby
		LobbyConfig lobby = LobbyConfig.allLobbiesMap.get(lobbyId);
		if (lobby == null)
			return getErrorView();    			    		
		
		// Get data
		backgroundImageNumber = lobby.backgroundImageNumber;
		tileCountryConfigIds = lobby.tileCountryConfigIdsToString();
		
		// Get country configs
		countryConfigs = CountryConfigDAO.list(0, Integer.MAX_VALUE, true, false, true, false);
		
        return getSuccessView();
    }
}

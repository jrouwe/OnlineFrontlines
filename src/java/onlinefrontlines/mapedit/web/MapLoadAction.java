package onlinefrontlines.mapedit.web;

import org.apache.log4j.Logger;
import onlinefrontlines.game.*;
import onlinefrontlines.web.*;

/**
 * This action is called from the map editor flash application to load the data from a specified map.
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
public class MapLoadAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(MapLoadAction.class);

	/**
	 * Id of the map to load
	 */
    public int mapId;
    
    /**
     * Tile image numbers of the loaded map
     */
    public String tileImageNumbers;

    /**
     * Tile owners of the loaded map
     */
    public String tileOwners;
    
    /**
     * Error code
     */
    public int errorCode = -1;
    
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	log.info("Loading map '" + mapId + "'");

    	// Load map
		MapConfig mapConfig = MapConfigCache.getInstance().get(mapId);
		if (mapConfig == null)
		{
    		errorCode = -2;
			return getErrorView();
		}

    	// Check permissions
    	if (mapConfig.creatorUserId != user.id && !user.isAdmin)
    	{
    		errorCode = -3;
			return getErrorView();
    	}

    	// Check state
    	if (MapConfigDAO.getPublishState(mapConfig.id) != PublishState.unpublished && !user.isAdmin)
    	{
    		errorCode = -4;
    		return getErrorView();
    	}

		// Convert to string
		tileImageNumbers = mapConfig.tileImageNumbersToString();
		tileOwners = mapConfig.tileOwnersToString();
		
        return getSuccessView();
    }
}

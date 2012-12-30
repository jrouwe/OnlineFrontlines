package onlinefrontlines.mapedit.web;

import org.apache.log4j.Logger;
import onlinefrontlines.game.*;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.web.*;

/**
 * This action updates the properties for a map
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
public class MapPropertiesAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(MapPropertiesAction.class);

	/**
	 * Id of the map
	 */
	public int mapId;
	
	/**
	 * Name of the map
	 */
	public String mapName;
	
	/**
	 * Input
	 */
	protected WebView input() throws Exception
	{
		// Load map
		MapConfig mapConfig = MapConfigCache.getInstance().get(mapId);
		if (mapConfig == null)
		{
			addActionError(getText("mapDoesNotExist"));
			return getErrorView();
		}

    	// Check permissions
    	if (mapConfig.creatorUserId != user.id && !user.isAdmin)
    	{
    		addActionError(getText("noRightsToEdit"));
    		return getErrorView();
    	}

    	// Check state
    	if (MapConfigDAO.getPublishState(mapConfig.id) != PublishState.unpublished && !user.isAdmin)
    	{
    		addActionError(getText("onlyEditUnpublished"));
    		return getErrorView();
    	}
		
		// Get variables
		mapName = mapConfig.name;
		
		return getInputView();
	}
	
    /**
     * Execute the action
     */
	protected WebView execute() throws Exception 
    {
		// Load map
		MapConfig mapConfig = MapConfigCache.getInstance().get(mapId);
		if (mapConfig == null)
		{
			addActionError(getText("mapDoesNotExist"));
			return getErrorView();
		}

    	// Check permissions
    	if (mapConfig.creatorUserId != user.id && !user.isAdmin)
    	{
    		addActionError(getText("noRightsToEdit"));
    		return getErrorView();
    	}

    	// Check state
    	if (MapConfigDAO.getPublishState(mapConfig.id) != PublishState.unpublished && !user.isAdmin)
    	{
    		addActionError(getText("onlyEditUnpublished"));
    		return getErrorView();
    	}

		// Validate map name
		if (mapName == null || mapName.isEmpty())
		{
			addFieldError("mapName", getText("mapNameRequired"));
			return getInputView();
		}
		
		// Validate max length
		if (mapName.length() > 32)
		{
			addFieldError("mapName", getText("nameTooLong"));
			return getInputView();
		}

    	log.info("User '" + user.id + "' updating map properties for '" + mapId + "'");

    	// Update properties
		MapConfig newValue = new MapConfig(mapConfig);
		newValue.name = mapName;
		
    	try
    	{
        	MapConfigDAO.save(newValue);
    		MapConfigCache.getInstance().put(newValue.id, newValue);
    		CacheTag.purgeAll();
    	}
    	catch (MySQLIntegrityConstraintViolationException e)
    	{
    		addFieldError("mapName", getText("mapAlreadyExists"));
    		return getInputView();
    	}

    	return getSuccessView();
    }
}

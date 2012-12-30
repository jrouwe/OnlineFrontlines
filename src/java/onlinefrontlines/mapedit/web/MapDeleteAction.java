package onlinefrontlines.mapedit.web;

import org.apache.log4j.Logger;
import onlinefrontlines.game.*;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.web.*;

/**
 * This action deletes a map (user supplied map ID) from the database.
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
public class MapDeleteAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(MapDeleteAction.class);

	/**
	 * Id of the map to remove
	 */
    public int mapId;

    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	// Load config
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

    	log.info("User '" + user.id + "' deleting map '" + mapId + "'");

    	// Delete it
    	MapConfigDAO.delete(mapId);
    	MapConfigCache.getInstance().remove(mapId);
    	CacheTag.purgeAll();
    	
        return getSuccessView();
    }
}

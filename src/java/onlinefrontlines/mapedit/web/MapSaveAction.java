package onlinefrontlines.mapedit.web;

import onlinefrontlines.auth.AuthTools;
import onlinefrontlines.game.*;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.utils.*;
import java.io.ByteArrayInputStream;
import onlinefrontlines.web.*;
import org.apache.log4j.Logger;

/**
 * This action is called from the map editor flash application to save changes to a map.
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
public class MapSaveAction extends WebAction  
{
	private static final Logger log = Logger.getLogger(MapSaveAction.class);
		
	/**
	 * Id of the map to save
	 */
    public int mapId;
    
    /**
     * Tile numbers
     */
    public String tileImageNumbers;
    
    /**
     * Tile ownership
     */
    public String tileOwners;
    
    /**
     * Map image data
     */
    public String mapImage;

    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
        // Load map
		MapConfig mapConfig = MapConfigCache.getInstance().get(mapId);
		if (mapConfig == null)
			return getErrorView();

    	// Check permissions
    	if (mapConfig.creatorUserId != user.id && !user.isAdmin)
			return getErrorView();

    	// Check state
    	if (MapConfigDAO.getPublishState(mapConfig.id) != PublishState.unpublished && !user.isAdmin)
    		return getErrorView();

    	log.info("User '" + user.id + "' saving map '" + mapId + "'");
    	
    	// Update properties
		MapConfig newValue = new MapConfig(mapConfig);
		newValue.tileImageNumbersFromString(tileImageNumbers);
		newValue.tileOwnersFromString(tileOwners);

		// Save the map
		MapConfigDAO.save(newValue);
		
		// Save the image
		ByteArrayInputStream s = new ByteArrayInputStream(AuthTools.hexStringToByteArray(mapImage));
		UploadedImageManager.getInstance().addImage("map", mapId, s, "image/png");
		
		// Update cache
		MapConfigCache.getInstance().put(newValue.id, newValue);

		// Clear cache
		CacheTag.purgeAll();
		
		return getSuccessView();
    }
}

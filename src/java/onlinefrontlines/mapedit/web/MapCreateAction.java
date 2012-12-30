package onlinefrontlines.mapedit.web;

import onlinefrontlines.game.*;
import java.util.HashMap;
import org.apache.log4j.Logger;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.web.*;

/**
 * This action creates a new (empty) map in the database based on the map name that the user supplied.
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
public class MapCreateAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(MapCreateAction.class);

	/**
	 * Name for the new map
	 */
	public String mapName;
	
	/**
	 * Selected map id
	 */
	public int baseOnMapId;

	/**
	 * Maps that can be selected from
	 */
	public HashMap<Integer, String> maps = new HashMap<Integer, String>();

	/**
	 * Fill maps dropdown box
	 */
	private void fillMaps() throws Exception
	{
    	// Get map configs
		maps.put(0, "<Empty>");
    	for (MapConfig mc : MapConfigDAO.list(user.id))
    		maps.put(mc.id, mc.name);
	}
	
	/**
	 * Input action
	 */
	protected WebView input() throws Exception
	{
		fillMaps();
		
		return getInputView();
	}
	
	/**
     * Execute the action
     */
	protected WebView execute() throws Exception 
    {
		fillMaps();
		
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

    	log.info("User '" + user.id + "' created new map");

    	// Create new empty map
		MapConfig map = new MapConfig(-1, mapName, 26, 32);
		map.creatorUserId = user.id;
		
		// Copy info from 'base on map'
		MapConfig baseOnMap = MapConfigCache.getInstance().get(baseOnMapId);
		if (baseOnMap != null)
		{
			map.tileImageNumbersFromString(baseOnMap.tileImageNumbersToString());
			map.tileOwnersFromString(baseOnMap.tileOwnersToString());
		}
		
		// Insert it in the database
		try
		{
			MapConfigDAO.create(map);
			MapConfigCache.getInstance().put(map.id, map);
			CacheTag.purgeAll();
		}
        catch (MySQLIntegrityConstraintViolationException e)
        {
        	addActionError(getText("mapAlreadyExists"));
        	return getInputView();
        }			
		
        return new WebViewRedirect("MapEdit.do?mapId=" + map.id);
    }
}

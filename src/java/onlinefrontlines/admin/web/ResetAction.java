package onlinefrontlines.admin.web;

import net.sf.ehcache.CacheManager;
import org.apache.log4j.Logger;
import onlinefrontlines.web.*;
import onlinefrontlines.game.*;
import onlinefrontlines.help.*;
import onlinefrontlines.lobby.*;

/**
 * This action clears all caches and reloads everything from the database
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
public class ResetAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(ResetAction.class);

	/**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	log.info("User '" + user.id + "' reset the application");
    	
		// Write all pending changes
		GameStateCache.getInstance().updateDbAll();

		// Clear caches
    	CacheManager.getInstance().clearAll();

    	// Load all stuff from DB
    	CountryType.loadAll();
		TerrainConfig.loadAll();
    	UnitConfig.loadAll();
    	LobbyConfig.loadAll();
    	
		// Load tips
		Tips.getInstance().tips = TipsDAO.loadTips();
   		
        return getSuccessView();
    }
}

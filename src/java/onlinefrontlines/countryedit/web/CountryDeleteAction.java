package onlinefrontlines.countryedit.web;

import org.apache.log4j.Logger;
import onlinefrontlines.game.CountryConfig;
import onlinefrontlines.game.CountryConfigCache;
import onlinefrontlines.game.DeploymentConfigCache;
import onlinefrontlines.game.CountryConfigDAO;
import onlinefrontlines.game.PublishState;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.web.*;

/**
 * This action deletes a country config (user supplied ID) from the database.
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
public class CountryDeleteAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(CountryDeleteAction.class);

	/**
	 * Id of the country config remove
	 */
    public int countryConfigId;
    
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
		// Load config
		CountryConfig countryConfig = CountryConfigCache.getInstance().get(countryConfigId);
    	if (countryConfig == null)
    	{
    		addActionError(getText("countryDoesNotExist"));
    		return getErrorView();
    	}
    	
    	// Check permissions
    	if (countryConfig.creatorUserId != user.id && !user.isAdmin)
    	{
    		addActionError(getText("noRightsToEdit"));
    		return getErrorView();
    	}

    	// Check state
    	if (countryConfig.publishState != PublishState.unpublished && !user.isAdmin)
    	{
    		addActionError(getText("onlyEditUnpublished"));
    		return getErrorView();
    	}

    	log.info("User '" + user.id + "' deleting '" + countryConfigId + "'");

    	// Delete it
    	CountryConfigDAO.delete(countryConfigId);
    	DeploymentConfigCache.getInstance().remove(countryConfigId);
    	CacheTag.purgeAll();
    	
        return getSuccessView();
    }
}

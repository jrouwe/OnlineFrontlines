package onlinefrontlines.countryedit.web;

import org.apache.log4j.Logger;
import onlinefrontlines.game.CountryConfig;
import onlinefrontlines.game.CountryConfigCache;
import onlinefrontlines.game.CountryConfigDAO;
import onlinefrontlines.game.PublishState;
import onlinefrontlines.web.*;

/**
 * This action publishes a country config
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
public class CountryPublishAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(CountryPublishAction.class);

	/**
	 * Id of the country config to publish
	 */
    public int countryConfigId;
    
    /**
     * Accept to be published
     */
    public boolean accept = true; 
    
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
    	PublishState newState;
    	if (accept || !user.isAdmin)
    	{
	    	if (countryConfig.publishState != PublishState.unpublished && !user.isAdmin)
	    		return getSuccessView();
	    	if (countryConfig.publishState == PublishState.published)
	    		return getSuccessView();
	    	newState = user.isAdmin? PublishState.published : PublishState.requestToPublish;
    	}
    	else
    	{
    		if (countryConfig.publishState == PublishState.unpublished)
    			return getSuccessView();
    		newState = PublishState.unpublished;    		
    	}

    	log.info("User '" + user.id + "' publishing country '" + countryConfigId + "'");

    	// Update state
    	countryConfig.publishState = newState;
		CountryConfigDAO.save(countryConfig);
		CountryConfigCache.getInstance().put(countryConfig.id, countryConfig);
    	
        return getSuccessView();
    }
}

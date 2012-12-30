package onlinefrontlines.countryedit.web;

import onlinefrontlines.game.*;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.web.*;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

/**
 * This action creates a new (empty) country config in the database based on the name that the user supplied.
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
public class CountryCreateAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(CountryCreateAction.class);

	/**
	 * Name for the country config deployment
	 */
	public String countryConfigName;
	
    /**
     * Execute the action
     */
	protected WebView execute() throws Exception 
    {
		// Validate country config name
		if (countryConfigName == null || countryConfigName.isEmpty())
		{
			addFieldError("countryConfigName", getText("countryConfigNameRequired"));
			return getInputView();
		}
		
		// Validate max length
		if (countryConfigName.length() > 32)
		{
			addFieldError("countryConfigName", getText("nameTooLong"));
			return getInputView();
		}

		// Create new empty country config
		CountryConfig config = new CountryConfig();
		config.creatorUserId = user.id;
		config.name = countryConfigName;
		
		// Select initial map
		ArrayList<MapConfig> m = MapConfigDAO.list(user.id);
		if (m.size() == 0)
		{
			addActionError(getText("noMapConfig"));
			return getInputView();
		}
		config.mapId = m.get(0).id;
		
		// Select initial deployment
		ArrayList<DeploymentConfig> d = DeploymentConfigDAO.list(user.id);
		if (d.size() == 0)
		{
			addActionError(getText("noDeploymentConfig"));
			return getInputView();
		}
		config.deploymentConfigId[0] = d.get(0).id;
		config.deploymentConfigId[1] = d.get(0).id;

    	log.info("User '" + user.id + "' creating country");

    	// Insert it in the database
		try
		{
			CountryConfigDAO.create(config);
			CountryConfigCache.getInstance().put(config.id, config);
			CacheTag.purgeAll();
        }
        catch (MySQLIntegrityConstraintViolationException e)
        {
        	addActionError(getText("countryConfigAlreadyExists"));
        	return getInputView();
        }			
		
        return new WebViewRedirect("CountryEdit.do?countryConfigId=" + config.id);
    }
}

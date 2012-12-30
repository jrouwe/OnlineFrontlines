package onlinefrontlines.countryedit.web;

import java.util.HashMap;
import java.util.Random;
import org.apache.log4j.Logger;
import onlinefrontlines.game.*;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.web.*;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

/**
 * This action starts editing a country config.
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
public class CountryEditAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(CountryEditAction.class);

	/**
	 * Id of the country config to edit
	 */
    public int countryConfigId;
    
    /**
     * Country name
     */
    public String countryConfigName;
    
	/**
	 * Selected map id
	 */
	public int mapId;
	
	/**
	 * Selected country type
	 */
	public int countryTypeId;
		
	/**
	 * Selected deployment config for faction 1
	 */
	public int deploymentConfigId1;
	
	/**
	 * Selected deployment for config faction 2
	 */
	public int deploymentConfigId2;
	
	/**
	 * If fog of war is enabled
	 */
	public boolean fogOfWarEnabled;
	
	/**
	 * Max score to win
	 */
	public int scoreLimit;
	
	/**
	 * If country config is a capture point
	 */
	public boolean isCapturePoint;
	
	/**
	 * Level required for user to be able to play this
	 */
	public int requiredLevel;
	
	/**
	 * If country suitable for AI
	 */
	public boolean suitableForAI;

	/**
	 * Maps that can be selected from
	 */
	public HashMap<Integer, String> maps = new HashMap<Integer, String>();
	
	/**
	 * Country types that can be selected from
	 */
	public HashMap<Integer, String> countryTypes = new HashMap<Integer, String>();
	
	/**
	 * Deployments that can be selected from
	 */
	public HashMap<Integer, String> deployments = new HashMap<Integer, String>();
	
	/**
	 * Fill maps and deployments
	 */
	public void fillLists(int creatorUserId) throws java.sql.SQLException
	{
    	// Get map configs
    	for (MapConfig mc : MapConfigDAO.list(creatorUserId))
    		maps.put(mc.id, mc.name);
    	
    	// Get deployment configs
    	for (DeploymentConfig dc : DeploymentConfigDAO.list(creatorUserId))
    		deployments.put(dc.id, dc.name);
    	
    	// Get country types
    	for (CountryType ct : CountryType.allTypes)
    		countryTypes.put(ct.getId(), ct.getName());
    	countryTypes.put(0, "None");
	}
	
	/**
     * Execute the action
     */
    protected WebView input() throws Exception 
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
    	
		// Fill maps and deployments
		fillLists(countryConfig.creatorUserId);
		
    	// Get properties
    	countryConfigName = countryConfig.name;
    	mapId = countryConfig.mapId;
    	if (countryConfig.countryType != null)
    		countryTypeId = countryConfig.countryType.getId();
    	else
    		countryTypeId = 0;
    	deploymentConfigId1 = countryConfig.deploymentConfigId[0];
    	deploymentConfigId2 = countryConfig.deploymentConfigId[1];
    	scoreLimit = countryConfig.scoreLimit;
    	fogOfWarEnabled = countryConfig.fogOfWarEnabled;
    	isCapturePoint = countryConfig.isCapturePoint;
    	requiredLevel = countryConfig.requiredLevel;
    	suitableForAI = countryConfig.suitableForAI;

    	return getInputView();
    }
    
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
    	
		// Fill maps and deployments
		fillLists(countryConfig.creatorUserId);
		
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

    	// Check score limit
    	if (scoreLimit < 200 || scoreLimit > 10000)
    	{
    		addFieldError("scoreLimit", getText("scoreLimitOutOfRange"));
    		return getInputView();
    	}
    	   	
    	// Load map
    	MapConfig map = MapConfigCache.getInstance().get(mapId);
    	if (map == null)
    	{
    		addActionError(getText("mapDoesNotExist"));
    		return getErrorView();
    	}    	
    	
    	// Load deployment configs
    	DeploymentConfig deploymentConfig1 = DeploymentConfigCache.getInstance().get(deploymentConfigId1);
    	if (deploymentConfig1 == null)
    	{
    		addActionError(getText("deploymentDoesNotExist"));
    		return getErrorView();
    	}    	
    	DeploymentConfig deploymentConfig2 = DeploymentConfigCache.getInstance().get(deploymentConfigId2);
    	if (deploymentConfig2 == null)
    	{
    		addActionError(getText("deploymentDoesNotExist"));
    		return getErrorView();
    	}    	
    	
    	// Get country type
    	CountryType countryType = CountryType.allTypesMap.get(countryTypeId);
    	
    	// Update properties
    	CountryConfig newValue = new CountryConfig(countryConfig);
    	newValue.name = countryConfigName;
    	newValue.mapId = map.id;
    	newValue.deploymentConfigId[0] = deploymentConfig1.id;
    	newValue.deploymentConfigId[1] = deploymentConfig2.id;
    	newValue.countryType = countryType;
    	newValue.scoreLimit = scoreLimit;
    	newValue.fogOfWarEnabled = fogOfWarEnabled;
    	if (user.isAdmin)
    	{
	    	newValue.isCapturePoint = isCapturePoint;
	    	newValue.requiredLevel = requiredLevel;
	    	newValue.suitableForAI = suitableForAI;
    	}
    	else
    	{
    		newValue.isCapturePoint = false;
    		newValue.requiredLevel = 0;
    		newValue.suitableForAI = false;
    	}
    	
    	try
    	{
    		DeploymentHelper helper = new DeploymentHelper(new Random());
    		helper.getDeployment(newValue);
    	}
    	catch (DeploymentFailedException e)
    	{    		
    		addActionError(getText("deploymentFailed", e.getUnit(), e.getFaction()));
			return getInputView();
    	}

    	log.info("User '" + user.id + "' editing country '" + countryConfigId + "'");

    	try
    	{
    		CountryConfigDAO.save(newValue);
    		CountryConfigCache.getInstance().put(newValue.id, newValue);
    		CacheTag.purgeAll();
    	}
    	catch (MySQLIntegrityConstraintViolationException e)
    	{
    		addFieldError("countryConfigName", getText("countryConfigAlreadyExists"));
    		return getInputView();
    	}

    	return getSuccessView();
    }
}
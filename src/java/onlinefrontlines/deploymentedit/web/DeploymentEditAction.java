package onlinefrontlines.deploymentedit.web;

import onlinefrontlines.game.*;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.web.*;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * This action starts editing a deployment.
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
public class DeploymentEditAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(DeploymentEditAction.class);

	/**
	 * Max units in deployment
	 */
	private final static int MAX_UNITS = 60;
	
	/**
	 * Id of the deployment to edit
	 */
    public int deploymentId;
    
    /**
     * Deployment config we're editing
     */
    public DeploymentConfig deploymentConfig;
    
	/**
	 * Name for the deployment
	 */
	public String deploymentName;
	
    /**
     * Contents of the deployment config
     */
    public static class Deployment
    {
    	/**
    	 * Unit id
    	 */
    	public int id;

    	/**
    	 * Unit name
    	 */
    	public String name;

    	/**
    	 * Get amount of units
    	 */
    	public int amount;
    	
    	/**
    	 * Unit id
    	 */
    	public int getId()
    	{
    		return id;
    	}
    	
    	/**
    	 * Unit name
    	 */
    	public String getName()
    	{
    		return name;
    	}
    	
    	/**
    	 * Get amount of units
    	 */
    	public int getAmount()
    	{
    		return amount;
    	}
    }
    
    public ArrayList<Deployment> deployment = new ArrayList<Deployment>();
    
	/**
	 * {@inheritDoc}
	 */
	public boolean setRequestParameter(String name, String value)
	{
		return name.startsWith("unit");
	}

	/**
     * Fill the deployment list
     */
    private void fillDeployment(DeploymentConfig deploymentConfig)
    {
    	for (UnitConfig u : UnitConfig.allUnits)
    		if (u != UnitConfig.unknownUnit)
	    	{
	    		// Find amount for this unit
	    		int amount = deploymentConfig.getAmount(u.id);

	    		// Insert in list
	    		Deployment d = new Deployment();
	    		d.id = u.id;
	    		d.name = u.name;
	    		d.amount = amount;
	    		deployment.add(d);
	    	}
    }
    
    /**
     * Input action
     */
    protected WebView input() throws Exception
    {
		// Load config
    	deploymentConfig = DeploymentConfigCache.getInstance().get(deploymentId);
    	if (deploymentConfig == null)
    	{
    		addActionError(getText("deploymentDoesNotExist"));
    		return getErrorView();
    	}
    	
    	// Check permissions
    	if (deploymentConfig.creatorUserId != user.id && !user.isAdmin)
    	{
    		addActionError(getText("noRightsToEdit"));
    		return getErrorView();
    	}

    	// Check state
    	if (DeploymentConfigDAO.getPublishState(deploymentConfig.id) != PublishState.unpublished && !user.isAdmin)
    	{
    		addActionError(getText("onlyEditUnpublished"));
    		return getErrorView();
    	}

    	// Get properties
    	deploymentName = deploymentConfig.name;
    	fillDeployment(deploymentConfig);   
    	
    	return getInputView();
    }
    
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
		// Load config
    	deploymentConfig = DeploymentConfigCache.getInstance().get(deploymentId);
    	if (deploymentConfig == null)
    	{
    		addActionError(getText("deploymentDoesNotExist"));
    		return getErrorView();
    	}
    	
    	// Check permissions
    	if (deploymentConfig.creatorUserId != user.id && !user.isAdmin)
    	{
    		addActionError(getText("noRightsToEdit"));
    		return getErrorView();
    	}

    	// Check state
    	if (DeploymentConfigDAO.getPublishState(deploymentConfig.id) != PublishState.unpublished && !user.isAdmin)
    	{
    		addActionError(getText("onlyEditUnpublished"));
    		return getErrorView();
    	}

    	// Get changes
    	DeploymentConfig newValue = new DeploymentConfig(deploymentConfig);
    	newValue.name = deploymentName;
    	for (UnitConfig u : UnitConfig.allUnits)
    		if (u != UnitConfig.unknownUnit)
	    	{
	    		// See if request sets this parameter
	    		String p = request.getParameter("unit" + u.id);
	    		if (p != null)
	    		{
	    			try
	    			{
	    				// Convert to int
	    				int tmp = Integer.parseInt(p);
	    				
	    				// Check range
	    				if (tmp < 0 || tmp > MAX_UNITS) 
	    					throw new Exception("Invalid value");
	    				
	    				// Store new value
	    				newValue.setAmount(u.id, tmp);
	    			}
	    			catch (Exception x)
	    			{
	            		addFieldError("unit" + u.id, getText("invalidDeploymentAmount"));
	    			}
	    		}
	    	}

    	// Check total units
    	if (newValue.getTotalUnits() > MAX_UNITS)
    		addActionError(getText("invalidDeploymentAmount"));
    	
    	// Fill list
    	fillDeployment(newValue);   
    	
    	// Replace deployment config for jsp page
    	deploymentConfig = newValue;
    	
    	// Check error
    	if (hasErrors())
    		return getInputView();

		// Validate deployment name
		if (deploymentName == null || deploymentName.isEmpty())
		{
			addFieldError("deploymentName", getText("deploymentNameRequired"));
			return getInputView();
		}
		
		// Validate max length
		if (deploymentName.length() > 32)
		{
			addFieldError("deploymentName", getText("nameTooLong"));
			return getInputView();
		}

    	log.info("User '" + user.id + "' editing deployment '" + deploymentId + "'");

    	try
		{
    		DeploymentConfigDAO.save(newValue);
    		DeploymentConfigCache.getInstance().put(newValue.id, newValue);
    		CacheTag.purgeAll();
    	}
    	catch (MySQLIntegrityConstraintViolationException e)
    	{
    		addFieldError("deploymentName", getText("deploymentAlreadyExists"));
    		return getInputView();
    	}

    	return getSuccessView();
    }
}
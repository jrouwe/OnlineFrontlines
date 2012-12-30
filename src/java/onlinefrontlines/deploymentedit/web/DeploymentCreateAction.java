package onlinefrontlines.deploymentedit.web;

import org.apache.log4j.Logger;
import onlinefrontlines.game.*;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.web.*;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

/**
 * This action creates a new (empty) deployment config in the database based on the name that the user supplied.
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
public class DeploymentCreateAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(DeploymentCreateAction.class);

	/**
	 * Name for the new deployment
	 */
	public String deploymentName;
	
    /**
     * Execute the action
     */
	protected WebView execute() throws Exception 
    {
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

    	log.info("User '" + user.id + "' created new deployment");

    	// Create new empty deployment
		DeploymentConfig deployment = new DeploymentConfig();
		deployment.name = deploymentName;
		deployment.creatorUserId = user.id;
		
		// Insert it in the database
		try
		{
			DeploymentConfigDAO.create(deployment);
			DeploymentConfigCache.getInstance().put(deployment.id, deployment);
			CacheTag.purgeAll();
        }
        catch (MySQLIntegrityConstraintViolationException e)
        {
        	addActionError(getText("deploymentAlreadyExists"));
        	return getInputView();
        }			
		
        return new WebViewRedirect("DeploymentEdit.do?deploymentId=" + deployment.id);
    }
}

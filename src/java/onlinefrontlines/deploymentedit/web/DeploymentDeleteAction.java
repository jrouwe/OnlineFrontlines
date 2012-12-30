package onlinefrontlines.deploymentedit.web;

import org.apache.log4j.Logger;
import onlinefrontlines.game.*;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.web.*;

/**
 * This action deletes a deployment from the database.
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
public class DeploymentDeleteAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(DeploymentDeleteAction.class);

	/**
	 * Id of the deployment to remove
	 */
    public int deploymentId;
    
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
		// Load config
		DeploymentConfig deploymentConfig = DeploymentConfigCache.getInstance().get(deploymentId);
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

    	log.info("User '" + user.id + "' deleting deployment '" + deploymentId + "'");

    	// Delete it
    	DeploymentConfigDAO.delete(deploymentId);
    	DeploymentConfigCache.getInstance().remove(deploymentId);
    	CacheTag.purgeAll();
    	
        return getSuccessView();
    }
}

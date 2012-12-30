package onlinefrontlines.playbymail.web;

import java.util.Random;
import org.apache.log4j.Logger;
import onlinefrontlines.auth.*;
import onlinefrontlines.web.*;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.utils.Tools;
import onlinefrontlines.facebook.Facebook;
import onlinefrontlines.game.*;

/**
 * This action accepts a pending invitation
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
public class PBMAcceptInvitationAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(PBMAcceptInvitationAction.class);
	
	/**
	 * Request id
	 */
	public String requestId;
	
	/**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {    	
    	// Check request id
    	if (requestId == null)
    	{
    		addActionError(getText("missingRequestId"));
    		return getErrorView();
    	}
    	
    	// Get request details
		Facebook.RequestDetails details = Facebook.getRequestDetails(requestId, facebookAccessToken);
		if (details == null)
		{
			addActionError(getText("errorReadingRequest"));
			return getErrorView();
		}
		
		// Get country config
		CountryConfig countryConfig = null;
		String[] data = details.data.split(",");
		if (data.length == 2 && data[0].equals("gameinvite"))
			countryConfig = CountryConfigCache.getInstance().get(Integer.parseInt(data[1])); 
		if (countryConfig == null)
		{
			addActionError(getText("errorReadingRequest"));
			return getErrorView();
		}

    	// Get inviter
    	User inviter = UserCache.getInstance().getByFacebookId(details.senderFacebookId);
    	if (inviter == null)
		{
			addActionError(getText("errorReadingRequest"));
			return getErrorView();
		}
    	
		try
		{
        	// Create game state
        	GameState gameState = countryConfig.createAndRegisterGameState(true, new Random().nextBoolean(), 0, -1, -1, -1, -1, true, -1);

        	// Setup player
        	gameState.joinGame(Faction.f1, user);
        	gameState.joinGame(Faction.f2, inviter);

    		// Delete request
    		Facebook.deleteRequest(requestId, facebookAccessToken);
    		CacheTag.purgeElement("top", null, user.id);

        	// Log result
    		log.info("Invitation '" + requestId + "' accepted by '" + user.id + "', game '" + gameState.id + "' created");
    		return new WebViewRedirect("GamePlay.do?gameId=" + gameState.id);
		}
		catch (DeploymentFailedException e)
		{
			Tools.logException(e);

    		addActionError(getText("deploymentFailed", e.getUnit(), e.getFaction()));
    		return getErrorView();
		}
    }
}

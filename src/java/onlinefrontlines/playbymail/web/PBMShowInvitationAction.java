package onlinefrontlines.playbymail.web;

import java.util.ArrayList;
import onlinefrontlines.facebook.Facebook;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.*;
import onlinefrontlines.web.*;

/**
 * This action shows a pending invitation and allows the user to accept or decline it
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
public class PBMShowInvitationAction extends WebAction 
{
	/**
	 * Invitation to show
	 */
	public String requestIds;
	
	/**
	 * Details for invitations
	 */
	public ArrayList<Facebook.RequestDetails> requestDetails;
	
	/**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	if (requestIds == null)
    	{
    		// No requests specified, get all
    		requestDetails = Facebook.getAllPendingRequests(facebookAccessToken);
    	}
    	else
    	{
	    	// Requests specified, get only them
    		requestDetails = new ArrayList<Facebook.RequestDetails>();
	    	String[] requests = requestIds.split(",");
	    	for (String r : requests)
	    	{
	    		Facebook.RequestDetails d = Facebook.getRequestDetails(r + "_" + user.facebookId, facebookAccessToken);
	    		if (d != null)
	    			requestDetails.add(d);
	    	}	    		
    	}
    	
    	for (Facebook.RequestDetails d : requestDetails)
		{
			// Check if this is a gameinvite 
			String[] data = d.data.split(",");
			if (data.length == 2 && data[0].equals("gameinvite"))
			{
				try
				{
					// Change data into country config name
    				d.data = CountryConfigCache.getInstance().get(Integer.parseInt(data[1])).name; 
				}
				catch (CacheException e)
				{
					d.data = "Error";
					Tools.logException(e);
				}
			}
			else
			{
				d.data = "Error";
			}
		}
    	
    	return getSuccessView();
    }
}

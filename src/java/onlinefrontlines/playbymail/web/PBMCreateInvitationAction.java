package onlinefrontlines.playbymail.web;

import java.util.HashMap;
import java.util.ArrayList;
import onlinefrontlines.web.*;
import onlinefrontlines.auth.User;
import onlinefrontlines.auth.UserCache;
import onlinefrontlines.game.*;
import onlinefrontlines.userstats.UserRank;

/**
 * This action allows a user to create an invitation to play by mail
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
public class PBMCreateInvitationAction extends WebAction 
{
	/**
	 * If a custom game should be played
	 */
	public boolean custom = false;
	
	/**
	 * User to show
	 */
	public int userId = -1;
	
	/**
	 * Target a specific player
	 */
	public boolean targetPlayer;
	
	/**
	 * Facebook id to show
	 */
	public String facebookId;
	
	/**
	 * Name of user that belongs to facebook id
	 */
	public String inviteeName;

	/**
	 * All country configs
	 */
	public ArrayList<CountryConfigDAO.Summary> countryConfigs;

	/**
	 * Country configs that can be selected from (for select html tag)
	 */
	public HashMap<Integer, String> countrySelect = new HashMap<Integer, String>();
	
	/**
	 * Selected country config
	 */
	public int selectedCountryConfig = 0; 
	
	/**
	 * All country types
	 */
	public ArrayList<CountryType> getCountryTypes()
	{
		return CountryType.allTypes;
	}
	
	/**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
		// Get level
		int userLevel = UserRank.getLevel(user.id);
		
    	// Get country configs
		if (custom)
			countryConfigs = CountryConfigDAO.list(user.id, Integer.MAX_VALUE, false, false, false, false);
		else
			countryConfigs = CountryConfigDAO.list(0, userLevel, true, false, false, false);
		for (CountryConfigDAO.Summary c : countryConfigs)
    	{
    		countrySelect.put(c.id, c.name);
    		
    		if (selectedCountryConfig == 0)
    			selectedCountryConfig = c.id;
    	}

		// Check if we want to invite specific user
		if (userId >= 0)
		{
			User invitee = UserCache.getInstance().get(userId);
			if (invitee == null || user.id == invitee.id)
			{
				addActionError(getText("invalidUser"));
				return getErrorView();
			}
			
			if (invitee.facebookId == null)
			{
				addActionError(getText("notConnectedToFacebook"));
				return getErrorView();
			}
			
			targetPlayer = true;
			facebookId = invitee.facebookId;
			inviteeName = invitee.username;
		}
		else
		{
			targetPlayer = false;
		}
		
    	return getSuccessView();
    }
}

package onlinefrontlines.game.web;

import onlinefrontlines.web.*;
import onlinefrontlines.game.CountryConfigDAO;
import onlinefrontlines.game.CountryType;
import onlinefrontlines.userstats.UserRank;
import java.util.*;

/**
 * This action displays the create game page 
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
public class GameCreateAction extends WebAction
{
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
	 * If this is an ai game or not
	 */
	public boolean ai;

	/**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	// Get country configs
    	countryConfigs = CountryConfigDAO.list(0, UserRank.getLevel(user.id), true, false, false, ai);
    	for (CountryConfigDAO.Summary c : countryConfigs)
    	{
    		countrySelect.put(c.id, c.name);
    		
    		if (selectedCountryConfig == 0)
    			selectedCountryConfig = c.id;
    	}
    	
    	return getSuccessView();
    }
}
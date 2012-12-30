package onlinefrontlines.auth.web;

import java.util.HashMap;
import onlinefrontlines.auth.*;
import onlinefrontlines.web.*;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.Army;
import org.apache.log4j.Logger;

/**
 * This action updates the users army
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
public class EditArmyAction extends WebAction
{
	private static final Logger log = Logger.getLogger(EditArmyAction.class);

    /**
	 * Form data
	 */
    public int army;
    
    /**
     * Redirect to action on success
     */
    public String redirect;
      
	/**
	 * Get values for armies combo box
	 */
	public HashMap<Integer, String> getArmies()
	{
		HashMap<Integer, String> armies = new HashMap<Integer, String>();
		armies.put(Army.toInt(Army.none), getText("armyNone"));
		armies.put(Army.toInt(Army.red), getText("armyRed"));
		armies.put(Army.toInt(Army.blue), getText("armyBlue"));
		return armies;
	}
	
	/**
     * Input action
     */
    protected WebView input() throws Exception
    {
    	// Don't allow user to change army twice
    	if (user.army != Army.none)
    	{
    		addActionError(getText("alreadySelectedArmy"));
    		return getErrorView();
    	}

    	// Get army
    	army = Army.toInt(user.army);
    	
    	return getInputView();
    }

    /**
     * Execute the action
     */
	protected WebView execute() throws Exception 
    {
    	// Don't allow user to change army twice
    	if (user.army != Army.none)
    	{
    		addActionError(getText("alreadySelectedArmy"));
    		return getErrorView();
    	}
    	
    	// Set army
    	synchronized (user)
    	{
	    	user.army = Army.fromInt(army);
	    	UserDAO.updateArmy(user);
	    	UserCache.getInstance().put(user.id, user);
    	}

    	// Clear cache
    	CacheTag.purgeGroup("user", null, user.id);

    	log.info("User '" + user.id + "' has changed his army");
    	
    	// Determine next action
        if (redirect != null && !redirect.isEmpty())
			return new WebViewRedirect(redirect);
		else
			return getSuccessView();
    }
}
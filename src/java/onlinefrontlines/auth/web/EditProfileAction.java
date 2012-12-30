package onlinefrontlines.auth.web;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import onlinefrontlines.auth.*;
import onlinefrontlines.web.*;
import onlinefrontlines.taglib.CacheTag;
import org.apache.log4j.Logger;

/**
 * This action updates user properties
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
public class EditProfileAction extends WebAction
{
	private static final Logger log = Logger.getLogger(EditProfileAction.class);
	
    /**
	 * Form data
	 */
	public String email = "";
	public boolean receiveGameEventsByMail;
    public String realname;
    public String country;
    public String city;
    public String website;
    public boolean autoDeclineFriendlyDefender;
    public boolean autoDefendOwnedCountry;
    public boolean showHelpBalloons;
    
    /**
     * Read only
     */
    public String getArmy()
    {
    	return user.army.toString();
    }
      	
	/**
     * Input action
     */
    protected WebView input() throws Exception
    {
    	// Get properties
    	email = user.email;
   		receiveGameEventsByMail = user.receiveGameEventsByMail;
    	realname = user.realname;
    	country = user.country;
    	city = user.city;
    	website = user.website;
    	autoDeclineFriendlyDefender = user.autoDeclineFriendlyDefender;
    	autoDefendOwnedCountry = user.autoDefendOwnedCountry;
    	showHelpBalloons = user.showHelpBalloons;
    	
    	return getInputView();
    }

    /**
     * Execute the action
     */
	protected WebView execute() throws Exception 
    {
		// Validate max length
		if (email.length() > 255)
		{
			addFieldError("email", getText("emailTooLong"));
			return getInputView();
		}
		
    	// Validate inviter email address
    	try
    	{
    		new InternetAddress(email, true);
    	}
    	catch (AddressException e)
    	{
    		addFieldError("email", getText("emailInvalid"));
    		return getInputView();
    	}
    	
		// Validate max length
		if (realname != null && realname.length() > 255)
		{
			addFieldError("realname", getText("nameTooLong"));
			return getInputView();
		}

		// Validate max length
		if (country != null && country.length() > 255)
		{
			addFieldError("country", getText("nameTooLong"));
			return getInputView();
		}

		// Validate max length
		if (city != null && city.length() > 255)
		{
			addFieldError("city", getText("nameTooLong"));
			return getInputView();
		}

		// Validate max length
		if (website != null && website.length() > 255)
		{
			addFieldError("website", getText("websiteTooLong"));
			return getInputView();
		}
		
    	// Store new properties
    	synchronized (user)
    	{
			user.email = email;
			user.receiveGameEventsByMail = receiveGameEventsByMail;
	    	user.realname = realname;
	    	user.country = country;
	    	user.city = city;
	    	user.website = website;
	    	user.autoDeclineFriendlyDefender = autoDeclineFriendlyDefender;
	    	user.autoDefendOwnedCountry = autoDefendOwnedCountry;
	    	user.showHelpBalloons = showHelpBalloons;
	    	UserDAO.updateProfile(user);
	    	UserCache.getInstance().put(user.id, user);
    	}

    	// Clear cache
    	CacheTag.purgeGroup("user", null, user.id);

    	log.info("User '" + user.id + "' has changed his properties");
    	
    	return getSuccessView();
    }
}
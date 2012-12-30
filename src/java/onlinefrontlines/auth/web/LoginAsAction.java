package onlinefrontlines.auth.web;

import onlinefrontlines.Constants;
import onlinefrontlines.auth.AutoAuth;
import onlinefrontlines.auth.UserCache;
import onlinefrontlines.auth.User;
import onlinefrontlines.web.*;
import org.apache.log4j.Logger;

/**
 * This action allows an admin to impersonate another person
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
public class LoginAsAction extends WebAction
{
	private static final Logger log = Logger.getLogger(LoginAsAction.class);

	/**
	 * Form data
	 */
	public String usernameToLoginAs;
	
	/**
	 * Input action, checks if the user is already logged in
	 */
	protected WebView input() throws Exception
	{
		return getInputView();
	}
	
	/**
	 * Execute the action
	 */
	protected WebView execute() throws Exception 
    {
		// Validate username exists
		if (usernameToLoginAs == null || usernameToLoginAs.isEmpty())
		{
			addFieldError("usernameToLoginAs", getText("usernameRequired"));
			return getInputView();
		}

		// Validate max length
		if (usernameToLoginAs.length() > 32)
		{
			addFieldError("usernameToLoginAs", getText("usernameTooLong"));
			return getInputView();
		}
		
    	// Find user to login as
    	User userLoggingInAs = UserCache.getInstance().get(usernameToLoginAs);
    	if (userLoggingInAs == null)
    	{
    		addFieldError("usernameToLoginAs", getText("userDoesNotExist"));
    		return getInputView();
    	}
    	
		// Log action
		log.info("User '" + user.id + "' impersonating user '" + userLoggingInAs.id + "' from '" + request.getRemoteAddr() + "'");
		
    	// Store newly logged in user on action
		user = userLoggingInAs;

		// Set authentication cookie
   		AutoAuth.generateAuthenticationCookies(response, user, Constants.AUTH_TIME_DEFAULT, -1);

		return getSuccessView();
    }
}
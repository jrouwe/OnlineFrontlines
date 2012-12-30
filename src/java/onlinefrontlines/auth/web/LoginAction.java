package onlinefrontlines.auth.web;

import onlinefrontlines.web.*;

/**
 * This action displays login window until user is logged in
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
public class LoginAction extends WebAction
{
	/**
	 * Redirect
	 */
	public String redirect;

	/**
	 * Execute the action
	 */
	protected WebView execute() throws Exception 
    {
    	// Determine next action
		if (user != null)
		{
	        if (redirect != null && !redirect.isEmpty())
				return new WebViewRedirect(redirect);
			else
				return getSuccessView();
		}
		else
			return getInputView();
    }
}
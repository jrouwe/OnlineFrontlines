package onlinefrontlines.web;

import java.net.URLEncoder;

/**
 * Interceptors are run before actions to check access rights
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
public class WebInterceptor 
{
	/**
	 * Main function called to intercept an action
	 */
    public WebView intercept(WebAction action) throws Exception
    {
    	// Make sure the protocol is always https
		if (!action.request.getScheme().toLowerCase().equals("https"))
		{
			StringBuffer url = action.request.getRequestURL();
			
			// Replace http with https
			if (!url.substring(0, 4).toLowerCase().equals("http"))
			{
	        	action.addActionError(action.getText("invalidProtocol"));
	        	return action.getErrorView();
			}
			url.delete(0,  4);
			url.insert(0, "https");
			
			// Append query string
			String query = action.request.getQueryString();
			if (query != null)
			{
				url.append("?");
				url.append(query);
			}
			
			// Redirect
			String fullUrl = url.toString();
			return new WebViewRedirect(fullUrl);
		}

		return action.doAction();
    }

	/**
	 * Redirect user to another action. Passes the current url as a parameter to allow the action to redirect back.
	 * 
	 * @param action The action
	 * @param actionToRedirectTo Action to call
	 * @return Returns the redirect JSP
	 * @throws Exception
	 */
	protected WebView redirect(WebAction action, String actionToRedirectTo) throws Exception
	{
    	// Get default redirect
    	WebView view = action.getRedirectView();
    	if (view != null)
    		return view;
    	
    	// Get redirect url
    	StringBuffer url = action.request.getRequestURL();
    	if (action.request.getQueryString() != null)
    	{
    		url.append("?");
    		url.append(action.request.getQueryString());
    	}
    
    	// Redirect
    	return new WebViewRedirect(actionToRedirectTo + "?redirect=" + URLEncoder.encode(url.toString(), "UTF-8"));
	}
}

package onlinefrontlines.web;

import java.util.HashMap;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import onlinefrontlines.auth.User;

/**
 * Base class for all web requests that the application processes
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
public class WebAction 
{
	/**
	 * Config
	 */
	public WebActionConfig config;
	
	/**
	 * The servlet context
	 */
	public ServletContext servletContext;
	
	/**
	 * The request
	 */
	public HttpServletRequest request;
	
	/**
	 * The response
	 */
	public HttpServletResponse response;
	
	/**
	 * Logged in user
	 */
	public User user;
	
	/**
	 * Facebook access token for this user
	 */
	public String facebookAccessToken;
	
	/**
	 * Action errors
	 */
	public ArrayList<String> actionErrors = null;
	
	/**
	 * Field errors	
	 */
	public HashMap<String, ArrayList<String>> fieldErrors = null;
	
	/**
	 * If parameter cannot be set because there is no accessor or field, this function will be called
	 * 
	 * @param name
	 * @param value
	 * @return True if the parameter was handled
	 */
	public boolean setRequestParameter(String name, String value)
	{
		return false;
	}
	
	/**
	 * Execute the action with the method as specified in the config
	 */
	public final WebView doAction() throws Exception
	{
		switch (config.method)
		{
		case INPUT:
			return input();
			
		case EXECUTE:
			return execute();
		}
		
		throw new Exception("Unknown method");
	}
	
	/**
	 * Input action
	 * 
	 * @return The path to the JSP page to execute
	 */
	protected WebView input() throws Exception
	{
		return getInputView();
	}
	
	/**
	 * Execute action
	 * 
	 * @return The path to the JSP page to execute
	 */
	protected WebView execute() throws Exception
	{
		return getSuccessView();
	}
	
	/**
	 * Get localized text string
	 */
	public final String getText(String key, String ... values)
	{
		// Get string
		String string = WebUtils.getText(key);
	
		// Replace variables
		int param = 1;
		for (String v : values)
		{
			string = string.replace("{" + param + "}", v);
			++param;
		}
		return string;
	}
	
	/**
	 * Add action error
	 * 
	 * @param error Error to add
	 */
	public final void addActionError(String error)
	{
		if (actionErrors == null)
			actionErrors = new ArrayList<String>();
			
		actionErrors.add(error);
	}
	
	/**
	 * Add field error
	 * 
	 * @param field Field name
	 * @param error Error to add
	 */
	public final void addFieldError(String field, String error)
	{
		if (fieldErrors == null)
			fieldErrors = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> l = fieldErrors.get(field);
		if (l == null)
		{
			l = new ArrayList<String>();
			fieldErrors.put(field, l);
		}
		l.add(error);
	}

	/**
	 * Check if any errors have been produced
	 * 
	 * @return True if there are any errors
	 */
	public final boolean hasErrors()
	{
		return (actionErrors != null && !actionErrors.isEmpty()) 
			|| (fieldErrors != null && !fieldErrors.isEmpty()); 
	}
	
	/**
	 * Get input view
	 */
	public final WebView getInputView()
	{
		return config.inputView;
	}
	
	/**
	 * Get success view
	 */
	public final WebView getSuccessView()
	{
		return config.successView;
	}
	
	/**
	 * Get error view
	 */
	public final WebView getErrorView()
	{
		return config.errorView;
	}
	
	/**
	 * Get redirect view
	 */
	public final WebView getRedirectView()
	{
		return config.redirectView;
	}
}

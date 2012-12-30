package onlinefrontlines.utils;

import java.util.Properties;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import java.io.IOException;

/**
 * This class holds all global properties for the application
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
public class GlobalProperties 
{
	/**
	 * Singleton instance
	 */
	private static GlobalProperties instance = new GlobalProperties();
	
	/**
	 * Internal properties object
	 */
	private Properties properties = new Properties();
	
	/**
	 * Get instance
	 */
	public static GlobalProperties getInstance()
	{
		return instance;
	}
		
	/**
	 * Load properties
	 * 
	 * @param context Servlet context
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void load(ServletContext context) throws IOException
	{
		// Create properties
		properties = new Properties();

		// Load default properties 
		properties.load(context.getResourceAsStream("/WEB-INF/config/global.properties"));

		// Override with init parameters from context
		Enumeration<?> e = context.getInitParameterNames();
		while (e.hasMoreElements())
		{
			String name = (String)e.nextElement();
			properties.put(name, context.getInitParameter(name));
		}

		// Set properties on context
		context.setAttribute("appUrl", getString("app.url"));
		context.setAttribute("facebookUrl", getString("facebook.url"));
		context.setAttribute("assetsUrl", getString("assets.url"));
		context.setAttribute("imagesUrl", getString("images.url"));
		context.setAttribute("fbAdminsUid", getString("facebook.admins_uid"));
		context.setAttribute("fbApiKey", getString("facebook.api_key"));
	}

	/**
	 * Direct access to the properties
	 */
	public Properties getProperties()
	{
		return properties;
	}
	
	/**
	 * Get string property
	 * @param key Key for property
	 * @return String value or empty if property does not exist
	 */
	public String getString(String key)
	{
		return properties.getProperty(key);
	}

	/**
	 * Get string property
	 * @param key Key for property
	 * @param defaultVal Default value if property does not exist
	 * @return String value or defaultVal if property does not exist
	 */
	public String getString(String key, String defaultVal)
	{
		return properties.getProperty(key, defaultVal);
	}
}

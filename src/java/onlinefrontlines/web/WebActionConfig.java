package onlinefrontlines.web;

import java.util.HashMap;
import java.lang.reflect.*;
import onlinefrontlines.utils.Tools;
import org.apache.log4j.Logger;

/**
 * Settings object for a WebAction
 * 
 * @see WebAction
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
public class WebActionConfig 
{
	private static final Logger log = Logger.getLogger(WebActionConfig.class);

	/**
	 * Possible methods
	 */
	public static enum ActionMethod 
	{
		INPUT,
		EXECUTE
	};
	
	/**
	 * Interceptor to use
	 */
	public WebInterceptor interceptor;
	
	/**
	 * Action to create
	 */
	public Class<?> action;
	
	/**
	 * View to use on input
	 */
	public WebView inputView;
	
	/**
	 * View to use on success
	 */
	public WebView successView;
	
	/**
	 * View to use on error
	 */
	public WebView errorView;
	
	/**
	 * View to use on redirect (used from interceptor)
	 */
	public WebView redirectView;
		
	/**
	 * Method to call
	 */
	public ActionMethod method;
	
	/**
	 * Cached information on a parameter for this action
	 */
	private static class Param
	{
		public Class<?> type;
		public Method method;		
		public Field field;

		public Param(Class<?> type, Method method, Field field)
		{
			this.type = type;
			this.method = method;
			this.field = field;
		}
	}
	
	/**
	 * Cached setters
	 */
	private HashMap<String, Param> setters = new HashMap<String, Param>();
	
	
	/**
	 * Cached getters
	 */
	private HashMap<String, Param> getters = new HashMap<String, Param>();	
	
	/**
	 * Constructor
	 */
	public WebActionConfig()
	{
		action = WebAction.class;
		method = ActionMethod.EXECUTE;
	}
	
	/**
	 * Copy constructor
	 */
	public WebActionConfig(WebActionConfig other)
	{
		interceptor = other.interceptor;
		action = other.action;
		inputView = other.inputView;
		successView = other.successView;
		errorView = other.errorView;
		redirectView = other.redirectView;
		method = ActionMethod.EXECUTE;
	}
	
	private Param createSetter(String name)
	{
		// Get method name
		String methodName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
		
		// Find method
		for (Method m : action.getMethods())
			if (m.getName().equals(methodName) 
				&& m.getParameterTypes().length == 1)
			{
				Class<?> t = m.getParameterTypes()[0];
				if (t.equals(String.class) 
					|| t.equals(int.class) 
					|| t.equals(long.class) 
					|| t.equals(boolean.class))
				{
					return new Param(t, m, null);
				}
			}
		
		// Try as field
		try
		{
			Field f = action.getField(name);
			Class<?> t = f.getType();
			if (t.equals(String.class)
					|| t.equals(int.class)
					|| t.equals(long.class)
					|| t.equals(boolean.class))
			{
				return new Param(t, null, f);
			}
		}
		catch (NoSuchFieldException e)
		{					
		}
		
		return new Param(null, null, null);
	}
	
	private Param createGetter(String name)
	{
		// Try as method
		try
		{
			String methodName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1, name.length());
			Method method = action.getMethod(methodName);
			return new Param(method.getReturnType(), method, null);
		}
		catch (NoSuchMethodException e)
		{				
		}
		
		// Try as field
		try
		{
			Field field = action.getField(name);
			return new Param(field.getType(), null, field);
		}
		catch (NoSuchFieldException e)
		{					
		}
		
		return new Param(null, null, null);
	}
	
	/**
	 * Set field on action
	 * 
	 * @param action Action to set field on
	 * @param name Name of field
	 * @param value Value of field
	 */
	public void setField(WebAction action, String name, String value)
	{
		// Get setter
		Param setter = setters.get(name);
		if (setter == null)
		{
			setter = createSetter(name);
			setters.put(name, setter);
		}
		
		try
		{
			// Call method
			if (setter.method != null)
			{
				if (setter.type.equals(String.class))
				{
					setter.method.invoke(action, value);
				}
				else if (setter.type.equals(int.class))
				{
					try
					{
						int intValue = Integer.parseInt(value);
						setter.method.invoke(action, intValue);
					}
					catch (NumberFormatException e)
					{
						log.warn(action.getClass().getName() + ": Parameter '" + name + "' could not be set to int value '" + value + "'");
					}
				}
				else if (setter.type.equals(long.class))
				{
					try
					{
						long longValue = Long.parseLong(value);
						setter.method.invoke(action, longValue);
					}
					catch (NumberFormatException e)
					{
						log.warn(action.getClass().getName() + ": Parameter '" + name + "' could not be set to long value '" + value + "'");
					}
				}
				else if (setter.type.equals(boolean.class))
				{
					boolean booleanValue = Boolean.parseBoolean(value);
					setter.method.invoke(action, booleanValue);						
				}
			}
			else if (setter.field != null)
			{
				if (setter.type.equals(String.class))
				{
					setter.field.set(action, value);
				}
				else if (setter.type.equals(int.class))
				{
					try
					{
						int intValue = Integer.parseInt(value);
						setter.field.set(action, intValue);
					}
					catch (NumberFormatException e)
					{
						log.warn(action.getClass().getName() + ": Parameter '" + name + "' could not be set to int value '" + value + "'");
					}
				}
				else if (setter.type.equals(long.class))
				{
					try
					{
						long longValue = Long.parseLong(value);
						setter.field.set(action, longValue);
					}
					catch (NumberFormatException e)
					{
						log.warn(action.getClass().getName() + ": Parameter '" + name + "' could not be set to long value '" + value + "'");
					}
				}
				else if (setter.type.equals(boolean.class))
				{
					boolean booleanValue = Boolean.parseBoolean(value);
					setter.field.set(action, booleanValue);
				}
			}
			else if (!action.setRequestParameter(name, value))
			{		
				log.warn(action.getClass().getName() + ": Parameter '" + name + "' could not be set to value '" + value + "'");
			}
		}
		catch (IllegalAccessException e)
		{			
			Tools.logException(action.getClass().getName() + ": Unable to set field '" + name + "' due to exception: ", e);
		}
		catch (InvocationTargetException e)
		{			
			Tools.logException(action.getClass().getName() + ": Unable to set field '" + name + "' due to exception: ", e);
		}
	}
	
	/**
	 * Get value of field on action
	 * 
	 * @param action Action to get field of
	 * @param name Field name
	 * @return Field value
	 */
	Object getField(WebAction action, String name)
	{
		// Get getter
		Param getter = getters.get(name);
		if (getter == null)
		{
			getter = createGetter(name);
			getters.put(name, getter);
		}
		
		try
		{
			if (getter.method != null)
			{
				return getter.method.invoke(action);
			}
			else if (getter.field != null)
			{
				return getter.field.get(action);
			}
			else
			{
				log.warn(action.getClass().getName() + ": Unable to get field '" + name + "'");
			}		
		}
		catch (IllegalAccessException e)
		{					
			Tools.logException(action.getClass().getName() + ": Unable to get field '" + name + "' due to exception: ", e);
		}
		catch (InvocationTargetException e)
		{			
			Tools.logException(action.getClass().getName() + ": Unable to get field '" + name + "' due to exception: ", e);
		}
		
		return null;
	}	
}

	
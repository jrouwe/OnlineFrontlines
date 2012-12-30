package onlinefrontlines.taglib;

import onlinefrontlines.Constants;
import onlinefrontlines.web.WebAction;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import net.sf.ehcache.*;
import net.sf.ehcache.constructs.blocking.*;

/**
 * Tag to cache jsp generated data
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
public class CacheTag extends BodyTagSupport 
{
	public static final long serialVersionUID = 0;
		
	/**
	 * Singleton cache
	 */
	private static BlockingCache cache;
	
	/**
	 * Mapping of groups to keys
	 */
	private static HashMap<String, HashSet<String>> groups = new HashMap<String, HashSet<String>>();
	
	/**
	 * Init singleton cache
	 */
	public static void initCache()
	{
		cache = new BlockingCache(CacheManager.getInstance().getCache("CacheTag"));
	}
	
	/**
	 * Shutdown singleton cache
	 */
	public static void shutdownCache()
	{
		cache = null;
	}
	
	/**
	 * Remove everything from the cache
	 */
	public static void purgeAll()
	{
		cache.removeAll();
	}
	
	/**
	 * Remove an element from the tag cache
	 * 
	 * @param key key attribute used on tag
	 * @param keyExp keyExp attribute used on tag
	 * @param userId user id associated with the tag (or 0 if none)
	 */
	public static void purgeElement(String key, String keyExp, int userId)
	{
		String k = key;
		
		if (keyExp != null && !keyExp.isEmpty())
			k += "_" + keyExp;
		
		if (userId != 0)
			k += "_" + userId;
		
		cache.remove(k);
	}
	
	/**
	 * Remove everything that belongs to a group from the cache
	 * 
	 * @param group group attribute used on tag
	 * @param keyExp keyExp attribute used on tag
	 * @param userId user id associated with the tag (or 0 if none)
	 */
	public static void purgeGroup(String group, String keyExp, int userId)
	{
		synchronized (groups)
		{
			HashSet<String> keySet = groups.get(group);
			if (keySet != null)
				for (String key : keySet)
				{
					purgeElement(key, keyExp, userId);
				}
		}
	}
	
	/**
	 * Cache key
	 */
	private String key;
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	/**
	 * Cache variable key expression
	 */
	private String keyExp;
	
	public void setKeyExp(String keyExp)
	{
		this.keyExp = keyExp;
	}

	/**
	 * If the cache should be user specific
	 */
	private boolean cachePerUser = false;
	
	public void setCachePerUser(boolean cachePerUser)
	{
		this.cachePerUser = cachePerUser;
	}
	
	/**
	 * How long this entry is valid (in seconds)
	 */
	private int timeToLiveSeconds = 120;
	
	public void setTimeToLiveSeconds(int timeToLiveSeconds)
	{
		this.timeToLiveSeconds = timeToLiveSeconds;
	}
	
	/**
	 * Group this tag belongs to (for purging elements from the cache)
	 */
	private String group = null;
	
	public void setGroup(String group)
	{
		this.group = group;
	}
	
	/**
	 * Calculated key
	 */
	private String calculatedKey;
	
	private void calculateKey()
	{
		// Start with specified key
		calculatedKey = key;
		
		// Add expression
		if (keyExp != null && !keyExp.isEmpty())
			calculatedKey += "_" + keyExp;
		
		// Make user specific with user ID if requested
		if (cachePerUser)
		{
			WebAction action = (WebAction)pageContext.getRequest().getAttribute(Constants.CURRENT_ACTION);
			if (action.user != null)
				calculatedKey += "_" + action.user.id;
		}
	}
	
	public int doStartTag() throws JspTagException
	{
		// Calculate key
		calculateKey();
		
		// Get element
		Element element;
		try
		{
			element = cache.get(calculatedKey);
		}
		catch (LockTimeoutException e)
		{
			throw new JspTagException(e);
		}
		
		if (element != null)
		{
			// Serve cached version
			try
			{
				pageContext.getOut().write((String)element.getObjectValue());
			}
			catch (IOException e)
			{
				throw new JspTagException(e);
			}
			
			return SKIP_BODY;
		}
		else
		{
			// Eval body
			return EVAL_BODY_BUFFERED;
		}
	}
	
	public int doAfterBody() throws JspTagException 
	{
		// Get content
		String content = getBodyContent().getString();
		
		// Store in cache
		Element element = new Element(calculatedKey, content);
		element.setTimeToLive(timeToLiveSeconds);
		cache.put(element);
		
		// Store group
		if (group != null)
		{
			synchronized (groups)
			{
				HashSet<String> set = groups.get(group);
				if (set != null)
				{
					// Add to existing set
					set.add(key);
				}
				else
				{
					// Create new set
					set = new HashSet<String>();
					set.add(key);
					groups.put(group, set);
				}
			}
		}
		
		// Serve newly cached version
		try
		{
			getPreviousOut().write(content);
		}
		catch (IOException e)
		{
			throw new JspTagException(e);
		}
		
		return SKIP_BODY;
	}
}

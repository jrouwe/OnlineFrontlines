package onlinefrontlines.utils;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import net.sf.ehcache.*;
import net.sf.ehcache.event.*;
import net.sf.ehcache.constructs.blocking.*;

/**
 * This class wraps ehcache
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
public abstract class Cache<Key, Value>
{
	private BlockingCache cache;

	/**
	 * Constructor, uses class name as cache name
	 */
	public Cache()
	{
		cache = new BlockingCache(CacheManager.getInstance().getCache(getClass().getSimpleName()));
	}
	
	/**
	 * Constructor
	 * 
	 * @param cacheName Name of the cache to use
	 */
	public Cache(String cacheName)
	{
		cache = new BlockingCache(CacheManager.getInstance().getCache(cacheName));
	}
	
	/**
	 * Constructor
	 * 
	 * @param cacheName Name of the cache to use
	 * @param listener Listener class to add
	 */
	public Cache(String cacheName, CacheEventListener listener)
	{
		cache = new BlockingCache(CacheManager.getInstance().getCache(cacheName));
		cache.getCacheEventNotificationService().registerListener(listener);	
	}

	/**
	 * Get cached object
	 */
	@SuppressWarnings("unchecked")
	public Value get(Key key) throws CacheException
	{
		try
		{
			// Get cached version
			Element element = cache.get(key);			
			if (element != null)
				return (Value)element.getObjectValue();
		}
		catch (LockTimeoutException e)
		{
			return null;
		}
				
		try
		{
			// Load object
			Value value = load(key);
			cache.put(new Element(key, value));
			return value;
		}
		catch (Throwable t)
		{
			cache.put(new Element(key, null));
			throw new CacheException(t);
		}
	}
	
	/**
	 * Get all objects in the cache
	 */
	@SuppressWarnings("unchecked")
	public Collection<Value> getValuesQuiet()
	{		
		List<Key> list = cache.getKeys();
		ArrayList<Value> values = new ArrayList<Value>();
		values.ensureCapacity(list.size());
		for (Key key : list)
		{
			Element element = cache.getQuiet(key);
			if (element != null && element.getObjectValue() != null)
				values.add((Value)element.getObjectValue());
		}
		return values;
	}
	
	/**
	 * Update / insert element in cache
	 * 
	 * @param key Key value
	 * @param value Value element
	 */
	public void put(Key key, Value value)
	{
		cache.put(new Element(key, value));
	}
	
	/**
	 * Remove element from cache
	 * 
	 * @param key Key value
	 */
	public void remove(Key key)
	{
		cache.remove(key);
	}
	
	/**
	 * Remove all elements from the cache
	 */
	public void removeAll()
	{
		cache.removeAll();
	}
	
	/**
	 * Load object
	 * 
	 * @param key Key for object
	 * @return Returned value
	 */
	protected abstract Value load(Key key) throws Throwable;
}

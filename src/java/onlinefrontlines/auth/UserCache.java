package onlinefrontlines.auth;

import java.sql.SQLException;
import onlinefrontlines.utils.Cache;
import onlinefrontlines.utils.CacheException;

/**
 * This class serves as a cache for the database for User objects
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
public class UserCache extends Cache<Integer, User>
{
	/**
	 * Singleton instance
	 */
	private final static UserCache instance = new UserCache();
	
	/**
	 * Get singleton instance
	 */
	public static UserCache getInstance()
	{
		return instance;
	}

	private Cache<String, Integer> idCache = new Cache<String, Integer>("UserIdCache")
	{
		@Override
		protected Integer load(String key) throws Throwable
		{
			return UserDAO.findUserId(key);
		}
	};

	private Cache<String, Integer> fbIdCache = new Cache<String, Integer>("UserFbIdCache")
	{
		@Override
		protected Integer load(String key) throws Throwable
		{
			return UserDAO.findUserByFacebookId(key);
		}
	};

	/**
	 * Get user by id
	 */
	@Override
	public User get(Integer id) throws CacheException
	{
		return super.get(id);
	}
	
	/**
	 * Get user by name
	 */
	public User get(String name) throws CacheException
	{
		Integer id = idCache.get(name);
		if (id == null)
			return null;
		
		return get(id);
	}
	
	/**
	 * Get user by facebook id
	 */
	public User getByFacebookId(String fbId) throws CacheException
	{
		Integer id = fbIdCache.get(fbId);
		if (id == null)
			return null;
		
		return get(id);
	}

	/**
	 * Put a new user in the cache
	 * 
	 * @param id Id of user
	 * @param user User to put in cache
	 */
	public void put(int id, User user)
	{
		super.put(id, user);
		
		idCache.put(user.username, id);
		
		fbIdCache.put(user.facebookId, id); 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected User load(Integer id) throws SQLException
	{
		return UserDAO.find(id);
	}
}

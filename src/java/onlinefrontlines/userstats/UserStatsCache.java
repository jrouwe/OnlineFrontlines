package onlinefrontlines.userstats;

import onlinefrontlines.utils.Cache;

/**
 * This class serves as a cache for the database for UserStats objects
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
public class UserStatsCache extends Cache<Integer, UserStats>
{
	/**
	 * Singleton instance
	 */
	private final static UserStatsCache instance = new UserStatsCache();
	
	/**
	 * Get singleton instance
	 */
	public static UserStatsCache getInstance()
	{
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UserStats load(Integer id) throws Throwable
	{
		return UserStatsDAO.getStats(id);
	}
}

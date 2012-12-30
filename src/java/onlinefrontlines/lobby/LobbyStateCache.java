package onlinefrontlines.lobby;

import java.sql.SQLException;
import java.util.Collection;
import onlinefrontlines.profiler.Profiler;
import onlinefrontlines.profiler.Sampler;
import onlinefrontlines.profiler.TimeSeriesCallback;
import onlinefrontlines.utils.Cache;
import onlinefrontlines.utils.CacheException;

/**
 * This class serves as a cache for the database for LobbyState objects
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
public final class LobbyStateCache
{
	/**
	 * Singleton instance
	 */
	private final static LobbyStateCache instance = new LobbyStateCache();
	
	/**
	 * Get singleton instance
	 */
	public static LobbyStateCache getInstance()
	{
		return instance;
	}
	
	/**
	 * Track amount of games in memory over time
	 */
	private final class CachedLobbiesTimeSeries implements TimeSeriesCallback
	{
		public double getValue()
		{
			return internalCache.getValuesQuiet().size(); 
		}
	}
	
	/**
	 * Track amount of lobby users over time
	 */
	private final class LobbyUserCountTimeSeries implements TimeSeriesCallback
	{
		public double getValue()
		{
			int count = 0;
			
			for (LobbyState l : internalCache.getValuesQuiet())
			{
				synchronized (l)
				{
					int[] c = l.getLobbyUserCount();
					count += c[0] + c[1];
				}
			}
			
			return count;
		}
	}

	/**
	 * Register all profilers
	 */
	public void registerProfilers()
	{
		Profiler.getInstance().registerTimeSeries("Lobbies in Memory", new CachedLobbiesTimeSeries());
		Profiler.getInstance().registerTimeSeries("Lobby Users Online", new LobbyUserCountTimeSeries());
	}

	/**
	 * Internal cache
	 */
	private Cache<Integer, LobbyState> internalCache = new Cache<Integer, LobbyState>("LobbyStateCache")
	{
		@Override
		protected LobbyState load(Integer id) throws Throwable
		{
			Sampler sampler = Profiler.getInstance().startSampler(Profiler.CATEGORY_GENERAL, "LobbyStateCache.load");
			try
			{
				LobbyConfig c = LobbyConfig.allLobbiesMap.get(id);
				return new LobbyState(c);
			}
			finally
			{
				sampler.stop();
			}
		}	
	};

	/**
	 * Get element from cache
	 */
	public LobbyState get(int id) throws SQLException, CacheException
	{
		// Check valid
		if (id == 0)
			return null;

    	// Return lobby
		return internalCache.get(id);
	}

	/**
	 * Put element in cache
	 */
	public void put(int id, LobbyState lobbyState)
	{
		internalCache.put(id, lobbyState);
	}
	
	/**
	 * Remove all elements from cache
	 */
	public void removeAll()
	{
		internalCache.removeAll();
	}
	
	/**
	 * Get all objects in the cache
	 */
	public Collection<LobbyState> getValuesQuiet()
	{
		return internalCache.getValuesQuiet();
	}
}

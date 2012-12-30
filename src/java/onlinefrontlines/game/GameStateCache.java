package onlinefrontlines.game;

import java.sql.SQLException;
import java.util.Collection;
import onlinefrontlines.profiler.*;
import onlinefrontlines.utils.Cache;
import onlinefrontlines.utils.CacheException;
import onlinefrontlines.utils.Tools;

/**
 * This class serves as a cache for the database for GameState objects
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
public final class GameStateCache
{
	/**
	 * Amount of time before game times out and is removed from memory
	 */
	public final static long TIME_OUT = 10 * 60 * 1000;
	
	/**
	 * Singleton instance
	 */
	private final static GameStateCache instance = new GameStateCache();
	
	/**
	 * Get singleton instance
	 */
	public static GameStateCache getInstance()
	{
		return instance;
	}

	/**
	 * Track amount of games in memory over time
	 */
	private final class CachedGamesTimeSeries implements TimeSeriesCallback
	{
		public double getValue()
		{
			return getValuesQuiet().size(); 
		}
	}

	/**
	 * Track amount of users in game over time
	 */
	private final class PlayerCountTimeSeries implements TimeSeriesCallback
	{
		public double getValue()
		{
			int count = 0;
			
			for (GameState g : getValuesQuiet())
			{
				synchronized (g)
				{
					if (g.isPlayerConnected(Faction.f1))
						++count;
					
					if (g.isPlayerConnected(Faction.f2))
						++count;
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
		Profiler.getInstance().registerTimeSeries("Games in Memory", new CachedGamesTimeSeries());
		Profiler.getInstance().registerTimeSeries("Players in Game", new PlayerCountTimeSeries());
	}
	
	/**
	 * Internal cache
	 */
	private Cache<Integer, GameState> internalCache = new Cache<Integer, GameState>("GameStateCache")
	{
		@Override
		protected GameState load(Integer id) throws Throwable
		{
			Sampler sampler = Profiler.getInstance().startSampler(Profiler.CATEGORY_GENERAL, "GameStateCache.load");
			try
			{
				return GameStateDAO.load(id);
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
	public GameState get(int id) throws SQLException, CacheException
	{
		// Check valid
		if (id == 0)
			return null;

		// Return game
		return internalCache.get(id);
	}

	/**
	 * Put element in cache
	 */
	public void put(int id, GameState gameState)
	{
		internalCache.put(id, gameState);
	}
	
	/**
	 * Get all objects in the cache
	 */
	public Collection<GameState> getValuesQuiet()
	{
		return internalCache.getValuesQuiet();
	}

	/**
	 * Write all pending changes to the db
	 */
	public void updateDbAll()
	{
		for (GameState gameState : getValuesQuiet())
			synchronized (gameState)
			{
				try
				{
					gameState.updateDb();
				}
				catch (SQLException e)
				{
					Tools.logException(e);
				}
			}
	}
}

package onlinefrontlines.game;

import java.sql.SQLException;
import onlinefrontlines.utils.CacheException;
import onlinefrontlines.utils.IllegalRequestException;
import onlinefrontlines.utils.Tools;

/**
 * Set up for a game
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
public final class CountryConfig 
{
	/**
	 * Id of the country config
	 */
	public int id = -1;
	
	/**
	 * Name of the country config
	 */
	public String name;
	
	/**
	 * Tile data for this game
	 */
	public int mapId;
	
	/**
	 * Unit setup for this game
	 */
	public final int[] deploymentConfigId = new int[2];
	
	/**
	 * If fog of war is enabled
	 */
	public boolean fogOfWarEnabled = true;
	
	/**
	 * Target amount of points to win the game
	 */
	public int scoreLimit = 750;
	
	/**
	 * If this country is a capture point
	 */
	public boolean isCapturePoint = false;
	
	/**
	 * Level required for user to be able to play this
	 */
	public int requiredLevel;
	
	/**
	 * If level is suitable for AI
	 */
	public boolean suitableForAI = false;
	
	/**
	 * Country type for the country
	 */
	public CountryType countryType;
	
	/**
	 * User that created this config
	 */
	public int creatorUserId;
	
	/**
	 * Publish state
	 */
	public PublishState publishState = PublishState.unpublished;
	
	/**
	 * Constructor
	 */
	public CountryConfig()
	{		
	}
	
	/**
	 * Copy constructor
	 */
	public CountryConfig(CountryConfig other)
	{
		id = other.id;
		name = other.name;
		mapId = other.mapId;
		deploymentConfigId[0] = other.deploymentConfigId[0];
		deploymentConfigId[1] = other.deploymentConfigId[1];
		fogOfWarEnabled = other.fogOfWarEnabled;
		scoreLimit = other.scoreLimit;
		isCapturePoint = other.isCapturePoint;
		requiredLevel = other.requiredLevel;
		creatorUserId = other.creatorUserId;
		publishState = other.publishState;
		suitableForAI = other.suitableForAI;
	}
	
	/**
	 * Create game state
	 * 
	 * @return Newly created game state
	 */
	public GameState createGameState(boolean faction1IsRed, boolean faction1Starts, int lobbyId, int attackedCountryX, int attackedCountryY, int defendedCountryX, int defendedCountryY, boolean playByMail, long randomSeed)
	{
		return new GameState(this, faction1IsRed, faction1Starts, lobbyId, attackedCountryX, attackedCountryY, defendedCountryX, defendedCountryY, playByMail, randomSeed);
	}
	
	/**
	 * Create a new game and register it in the database
	 * 
	 * @return Newly created game state
	 */
	public GameState createAndRegisterGameState(boolean faction1IsRed, boolean faction1Starts, int lobbyId, int attackedCountryX, int attackedCountryY, int defendedCountryX, int defendedCountryY, boolean playByMail, long randomSeed) throws SQLException, IllegalRequestException, DeploymentFailedException
	{
    	// Create game state
    	GameState gameState = createGameState(faction1IsRed, faction1Starts, lobbyId, attackedCountryX, attackedCountryY, defendedCountryX, defendedCountryY, playByMail, randomSeed);
    	
		// Insert into database
		GameStateDAO.create(gameState);
		
		try
		{
	    	// Initialize the game
	    	gameState.initGame();

	    	// Add to cache
			GameStateCache.getInstance().put(gameState.id, gameState);
		}
		catch (DeploymentFailedException e)
		{
			// Delete game from database if it failed to initialize
			GameStateDAO.delete(gameState.id);
			throw e;
		}
		catch (IllegalRequestException e)
		{
			// Delete game from database if it failed to initialize
			GameStateDAO.delete(gameState.id);
			throw e;
		}
		catch (RuntimeException e)
		{
			// Delete game from database if it failed to initialize
			GameStateDAO.delete(gameState.id);
			throw e;
		}

		return gameState;		
	}
	
	/**
	 * Get id
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Get name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Tile data for this game
	 */
	public int getMapId()
	{
		return mapId;
	}

	/**
	 * If this country is a capture point
	 */
	public boolean getIsCapturePoint()
	{
		return isCapturePoint;
	}
	
	/**
	 * If this country is suitable for the AI to play
	 */
	public boolean getSuitableForAI()
	{
		return suitableForAI;
	}

	/**
	 * Country type for the country
	 */
	public CountryType getCountryType()
	{
		return countryType;
	}
	
	/**
	 * User that created this config
	 */
	public int getCreatorUserId()
	{
		return creatorUserId;
	}
	
	/**
	 * Publish state
	 */
	public int getPublishStateAsInt()
	{
		return PublishState.toInt(publishState);
	}

	/**
	 * Get map config
	 * 
	 * This function is not a simple accessor, so should not be called in an inner loop
	 */
	public MapConfig getMapConfig()
	{
		try
		{
			return MapConfigCache.getInstance().get(mapId);
		}
		catch (CacheException e)
		{
			Tools.logException(e);
			return null;
		}
	}
	
	/**
	 * Get deployment config
	 * 
	 * This function is not a simple accessor, so should not be called in an inner loop
	 */
	public DeploymentConfig getDeploymentConfig(int i)
	{
		try
		{
			return DeploymentConfigCache.getInstance().get(deploymentConfigId[i]);
		}
		catch (CacheException e)
		{
			Tools.logException(e);
			return null;
		}
	}
	
	/**
	 * Get average number of units per faction
	 */
	public String getNumUnits()
	{
		if (!isCapturePoint)
		{
			DeploymentConfig c1 = getDeploymentConfig(0);
			DeploymentConfig c2 = getDeploymentConfig(1);
			if (c1 != null && c2 != null)
				return (c1.getTotalUnits() + " vs "	+ c2.getTotalUnits());
		}
		
		return "-";
	}
}

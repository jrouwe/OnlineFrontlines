package onlinefrontlines.game;

import java.util.*;
import java.sql.SQLException;
import onlinefrontlines.utils.CacheException;
import onlinefrontlines.utils.DbQueryHelper;

/**
 * This class communicates with the database and manages reading/writing CountryConfig objects
 * 
 * @see onlinefrontlines.game.CountryConfig

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
public class CountryConfigDAO 
{
	public static class Summary
	{
		public int id;
		public String name;
		public int mapId;
		public String mapName;
		public final int[] deploymentConfigId = new int[2];
		public int countryTypeId;
		public int scoreLimit;
		public int creatorUserId;
		public PublishState publishState;
		
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
		 * Get map id
		 */
		public int getMapId()
		{
			return mapId;
		}
		
		/**
		 * Get map name
		 */
		public String getMapName()
		{
			return mapName;
		}
		
		/**
		 * Get country type
		 */
		public int getCountryTypeId()
		{
			return countryTypeId;
		}
		
		/**
		 * Get score limit
		 */
		public int getScoreLimit()
		{
			return scoreLimit;
		}
				
		/**
		 * Get average number of units per faction
		 */
		public String getNumUnits()
		{
			try
			{
				return DeploymentConfigCache.getInstance().get(deploymentConfigId[0]).getTotalUnits()
						+ " vs "
						+ DeploymentConfigCache.getInstance().get(deploymentConfigId[1]).getTotalUnits();
			}
			catch (CacheException e)
			{
				return "-";
			}
		}
		
		/**
		 * Creator user id
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
	}
	
	/**
	 * Get list of country configs
	 *
	 * @param creatorUserId Will return only maps that are by this creator, 0 turns this filter off
	 * @param requiredLevel Will return only countries that are lower than this level, Integer.MAX_VALUE to turns this filter off
	 * @param publishedOnly Will only return countries that have been published
	 * @param requestToPublishOnly Will only return countries that are being requested to publish
	 * @param includeCapturePoints Will also include capture points
	 * @param suitableForAIOnly Will return only maps suitable for ai
	 * @return List of country configs
	 */
	public static ArrayList<Summary> list(int creatorUserId, int requiredLevel, boolean publishedOnly, boolean requestToPublishOnly, boolean includeCapturePoints, boolean suitableForAIOnly) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
    		ArrayList<Summary> rv = new ArrayList<Summary>();
    		
        	helper.prepareQuery("SELECT country_configs.id, country_configs.name, mapId, maps.name, deploymentConfigId1, deploymentConfigId2, countryTypeId, scoreLimit, country_configs.creatorUserId, publishState FROM country_configs JOIN maps ON maps.id=mapId WHERE (?>=requiredLevel) AND (? OR country_configs.creatorUserId=?) AND (? OR publishState=?) AND (? OR NOT isCapturePoint) AND (? OR publishState=?) AND (? OR suitableForAI)");
        	helper.setInt(1, requiredLevel);
        	helper.setInt(2, creatorUserId != 0? 0 : 1);
        	helper.setInt(3, creatorUserId);
        	helper.setInt(4, publishedOnly? 0 : 1);
       		helper.setInt(5, PublishState.toInt(PublishState.published));
       		helper.setInt(6, includeCapturePoints? 1 : 0);
       		helper.setInt(7, requestToPublishOnly? 0 : 1);
       		helper.setInt(8, PublishState.toInt(PublishState.requestToPublish));
       		helper.setInt(9, suitableForAIOnly? 0 : 1);
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
	    		Summary s = new Summary();
	    		s.id = helper.getInt(1);
	    		s.name = helper.getString(2);
	    		s.mapId = helper.getInt(3);
	    		s.mapName = helper.getString(4);
	    		s.deploymentConfigId[0] = helper.getInt(5);
	    		s.deploymentConfigId[1] = helper.getInt(6);
	    		s.countryTypeId = helper.getInt(7);
	    		s.scoreLimit = helper.getInt(8);
	    		s.creatorUserId = helper.getInt(9);
	    		s.publishState = PublishState.fromInt(helper.getInt(10));
	    		rv.add(s);
	    	}
	    	
	    	return rv;
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Create a new CountryConfig in the database
	 * 
	 * @param countryConfig Config to add to the database
	 * @throws SQLException
	 */
	public static void create(CountryConfig countryConfig) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Create record
	    	helper.prepareQuery("INSERT INTO country_configs (name, mapId, deploymentConfigId1, deploymentConfigId2, scoreLimit, fogOfWarEnabled, isCapturePoint, countryTypeId, requiredLevel, creatorUserId, publishState, suitableForAI) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	    	helper.setString(1, countryConfig.name);
	    	helper.setInt(2, countryConfig.mapId);
	    	helper.setInt(3, countryConfig.deploymentConfigId[0]);
	    	helper.setInt(4, countryConfig.deploymentConfigId[1]);
	    	helper.setInt(5, countryConfig.scoreLimit);
	    	helper.setInt(6, countryConfig.fogOfWarEnabled? 1 : 0);
	    	helper.setInt(7, countryConfig.isCapturePoint? 1 : 0);
	    	if (countryConfig.countryType != null)
	    		helper.setInt(8, countryConfig.countryType.getId());
	    	else
	    		helper.setNull(8);
	    	helper.setInt(9, countryConfig.requiredLevel);
	    	helper.setInt(10, countryConfig.creatorUserId);
	    	helper.setInt(11, PublishState.toInt(countryConfig.publishState));
	    	helper.setInt(12, countryConfig.suitableForAI? 1 : 0);
	    	helper.executeUpdate();
	    	
	    	// Set id
	    	ArrayList<Integer> generatedKeys = helper.getGeneratedKeys();
	    	countryConfig.id = generatedKeys.get(0);
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Load existing country config
	 * 
	 * @param id Id of country config
	 * @return Loaded country config
	 * @throws SQLException
	 */
	public static CountryConfig load(int id) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	helper.prepareQuery("SELECT name, mapId, deploymentConfigId1, deploymentConfigId2, scoreLimit, fogOfWarEnabled, isCapturePoint, countryTypeId, requiredLevel, creatorUserId, publishState, suitableForAI FROM country_configs WHERE id=?");
        	helper.setInt(1, id);
	    	helper.executeQuery();
	    	
	    	if (!helper.nextRecord())
	    		return null;

	    	CountryConfig c = new CountryConfig();
    		c.id = id;
    		c.name = helper.getString(1);
    		c.mapId = helper.getInt(2);
    		c.deploymentConfigId[0] = helper.getInt(3);
    		c.deploymentConfigId[1] = helper.getInt(4);
    		c.scoreLimit = helper.getInt(5);
    		c.fogOfWarEnabled = helper.getInt(6) != 0;
    		c.isCapturePoint = helper.getInt(7) != 0;
    		c.countryType = CountryType.allTypesMap.get(helper.getInt(8));
    		c.requiredLevel = helper.getInt(9);
    		c.creatorUserId = helper.getInt(10);
    		c.publishState = PublishState.fromInt(helper.getInt(11));
    		c.suitableForAI = helper.getInt(12) != 0;
	    	
	    	return c;
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Remove country config from database
	 * 
	 * @param id ID of the country config to remove
	 * @throws SQLException
	 */
	public static void delete(int id) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery("DELETE FROM country_configs WHERE id=?");
	    	helper.setInt(1, id);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Updates a previously created country config in the database
	 * 
	 * @param countryConfig The country config to save
	 * @throws SQLException
	 */
	public static void save(CountryConfig countryConfig) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery("UPDATE country_configs SET name=?, mapId=?, deploymentConfigId1=?, deploymentConfigId2=?, scoreLimit=?, fogOfWarEnabled=?, isCapturePoint=?, countryTypeId=?, requiredLevel=?, creatorUserId=?, publishState=?, suitableForAI=? WHERE id=?");
	    	helper.setString(1, countryConfig.name);
	    	helper.setInt(2, countryConfig.mapId);
	    	helper.setInt(3, countryConfig.deploymentConfigId[0]);
	    	helper.setInt(4, countryConfig.deploymentConfigId[1]);
	    	helper.setInt(5, countryConfig.scoreLimit);
	    	helper.setInt(6, countryConfig.fogOfWarEnabled? 1 : 0);
	    	helper.setInt(7, countryConfig.isCapturePoint? 1 : 0);
	    	if (countryConfig.countryType != null)
	    		helper.setInt(8, countryConfig.countryType.getId());
	    	else
	    		helper.setNull(8);
	    	helper.setInt(9, countryConfig.requiredLevel);
	    	helper.setInt(10, countryConfig.creatorUserId);
	    	helper.setInt(11, PublishState.toInt(countryConfig.publishState));
	    	helper.setInt(12, countryConfig.suitableForAI? 1 : 0);
	    	helper.setInt(13, countryConfig.id);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}	
}

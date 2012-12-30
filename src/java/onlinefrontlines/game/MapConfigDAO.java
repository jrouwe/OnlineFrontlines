package onlinefrontlines.game;

import java.util.*;
import java.sql.SQLException;
import onlinefrontlines.utils.DbQueryHelper;

/**
 * This class communicates with the database and manages reading/writing MapConfig objects
 * 
 * @see onlinefrontlines.game.MapConfig

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
public class MapConfigDAO 
{
	/**
	 * Queries the database for all maps
	 * 
	 * @param creatorUserId User id of creator of map or 0 to disable filter
	 * @return All maps in the database with only their id and name filled in
	 * @throws SQLException
	 */
	public static ArrayList<MapConfig> list(int creatorUserId) throws SQLException
	{
		ArrayList<MapConfig> maps = new ArrayList<MapConfig>();

    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find all maps
	    	helper.prepareQuery("SELECT id, name, creatorUserId FROM maps WHERE (? OR creatorUserId=?)");
	    	helper.setInt(1, creatorUserId != 0? 0 : 1);
	    	helper.setInt(2, creatorUserId);
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
		        // Construct MapConfig object
		        MapConfig mapConfig = new MapConfig(helper.getInt(1), helper.getString(2), 0, 0);
		        mapConfig.creatorUserId = helper.getInt(3);
		        maps.add(mapConfig);
	    	}
        }
        finally
        {
        	helper.close();
        }
        
        return maps;
	}

	/**
	 * Inserts a new MapConfig in the database
	 * 
	 * @param mapConfig The map to store in the database
	 * @throws SQLException
	 */
	public static void create(MapConfig mapConfig) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Create maps record
	    	helper.prepareQuery("INSERT INTO maps (name, sizeX, sizeY, tileImageNumbers, tileOwners, creatorUserId) VALUES (?, ?, ?, ?, ?, ?)");
	    	helper.setString(1, mapConfig.name);
	    	helper.setInt(2, mapConfig.sizeX);
	    	helper.setInt(3, mapConfig.sizeY);
	    	helper.setString(4, mapConfig.tileImageNumbersToString());
	    	helper.setString(5, mapConfig.tileOwnersToString());
	    	helper.setInt(6, mapConfig.creatorUserId);	    	
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Remove map from database
	 * 
	 * @param id ID of the map to remove
	 * @throws SQLException
	 */
	public static void delete(int id) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Remove maps record
	    	helper.prepareQuery("DELETE FROM maps WHERE id=?");
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
	 * Load full map from the database
	 * 
	 * @param id ID of the map to load
	 * @return The map that was loaded
	 * @throws SQLException
	 */
	public static MapConfig load(int id) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find map record
	    	helper.prepareQuery("SELECT name, sizeX, sizeY, tileImageNumbers, tileOwners, creatorUserId FROM maps WHERE id=?");
	    	helper.setInt(1, id);
	    	helper.executeQuery();
	    	
	    	// Validate map exists
	    	if (!helper.nextRecord())
	    		return null;
	    	
	        // Construct MapConfig object
	        MapConfig mapConfig = new MapConfig(id, helper.getString(1), helper.getInt(2), helper.getInt(3));
	        mapConfig.tileImageNumbersFromString(helper.getString(4));
	        mapConfig.tileOwnersFromString(helper.getString(5));
	        mapConfig.creatorUserId = helper.getInt(6);
	        
	        return mapConfig;
        }
        finally
        {
        	helper.close();
        }
	}

	/**
	 * Updates a previously created map in the database
	 * 
	 * @param mapConfig The map to save
	 * @throws SQLException
	 */
	public static void save(MapConfig mapConfig) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Update maps record
	    	helper.prepareQuery("UPDATE maps SET name=?, sizeX=?, sizeY=?, tileImageNumbers=?, tileOwners=?, creatorUserId=? WHERE id=?");
	    	helper.setString(1, mapConfig.name);
	    	helper.setInt(2, mapConfig.sizeX);
	    	helper.setInt(3, mapConfig.sizeY);
	    	helper.setString(4, mapConfig.tileImageNumbersToString());
	    	helper.setString(5, mapConfig.tileOwnersToString());
	    	helper.setInt(6, mapConfig.creatorUserId);
	    	helper.setInt(7, mapConfig.id);	    	
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}	

	/**
	 * Derive publish state from linked countries
	 * 
	 * @param deploymentId Map id to check for
	 * @return Publish state
	 */
	public static PublishState getPublishState(int mapId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery("SELECT MAX(publishState) FROM country_configs WHERE mapId=?");
	    	helper.setInt(1, mapId);
	    	helper.executeQuery();    	
	    	helper.nextRecord();
	    	return PublishState.fromInt(helper.getInt(1));    	
	    }
        finally
        {
        	helper.close();
        }		
	}
}

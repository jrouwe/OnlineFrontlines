package onlinefrontlines.lobby;

import java.sql.SQLException;
import java.util.ArrayList;
import onlinefrontlines.utils.DbQueryHelper;
import org.apache.log4j.Logger;

/**
 * This class communicates with the database and manages reading/writing LobbyConfig objects
 * 
 * @see onlinefrontlines.game.LobbyConfig

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
public class LobbyConfigDAO 
{
	private static final Logger log = Logger.getLogger(LobbyConfigDAO.class);

	/**
	 * Queries the database for all lobbies
	 * 
	 * @return All lobbies in the database
	 * @throws SQLException
	 */
	public static ArrayList<LobbyConfig> loadAllLobbies() throws SQLException
	{
		ArrayList<LobbyConfig> lobbies = new ArrayList<LobbyConfig>();

    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find all maps
	    	helper.prepareQuery("SELECT id, name, backgroundImageNumber, sizeX, sizeY, tileCountryConfigIds, worldMapEnterButtonX, worldMapEnterButtonY, minRequiredLevel, maxLevel, maxUsers FROM lobbies");
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
		        // Construct LobbyConfig object
		        LobbyConfig lobbyConfig = new LobbyConfig(helper.getInt(1), helper.getString(2), helper.getInt(3), helper.getInt(4), helper.getInt(5), helper.getInt(7), helper.getInt(8), helper.getInt(9), helper.getInt(10), helper.getInt(11));
		        lobbyConfig.tileCountryConfigIdsFromString(helper.getString(6));
		        lobbies.add(lobbyConfig);
	    	}
        }
        finally
        {
        	helper.close();
        }
        
        log.info("Loaded " + lobbies.size() + " lobbies");

        return lobbies;
	}

	/**
	 * Create a new LobbyConfig in the database
	 * 
	 * @param lobbyConfig Config to add to the database
	 * @throws SQLException
	 */
	public static void create(LobbyConfig lobbyConfig) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Create record
	    	helper.prepareQuery("INSERT INTO lobbies (name, sizeX, sizeY, backgroundImageNumber, tileCountryConfigIds, worldMapEnterButtonX, worldMapEnterButtonY, minRequiredLevel, maxLevel, maxUsers) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	    	helper.setString(1, lobbyConfig.name);
	    	helper.setInt(2, lobbyConfig.sizeX);
	    	helper.setInt(3, lobbyConfig.sizeY);
	    	helper.setInt(4, lobbyConfig.backgroundImageNumber);
	    	helper.setString(5, lobbyConfig.tileCountryConfigIdsToString());
	    	helper.setInt(6, lobbyConfig.worldMapEnterButtonX);
	    	helper.setInt(7, lobbyConfig.worldMapEnterButtonY);
	    	helper.setInt(8, lobbyConfig.minRequiredLevel);
	    	helper.setInt(9, lobbyConfig.maxLevel);
	    	helper.setInt(10, lobbyConfig.maxUsers);
	    	helper.executeUpdate();
	    	
	    	// Set id
	    	ArrayList<Integer> generatedKeys = helper.getGeneratedKeys();
	    	lobbyConfig.id = generatedKeys.get(0);
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Load a particular lobby config
	 * 
	 * @param id Id of the config
	 * @return Lobby config
	 * @throws SQLException
	 */
	public static LobbyConfig load(int id) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find all maps
	    	helper.prepareQuery("SELECT name, backgroundImageNumber, sizeX, sizeY, tileCountryConfigIds, worldMapEnterButtonX, worldMapEnterButtonY, minRequiredLevel, maxLevel, maxUsers FROM lobbies WHERE id=?");
	    	helper.setInt(1, id);
	    	helper.executeQuery();
	    	
	    	if (!helper.nextRecord())
	    		return null;
	    	
	        // Construct LobbyConfig object
	        LobbyConfig lobbyConfig = new LobbyConfig(id, helper.getString(1), helper.getInt(2), helper.getInt(3), helper.getInt(4), helper.getInt(6), helper.getInt(7), helper.getInt(8), helper.getInt(9), helper.getInt(10));
	        lobbyConfig.tileCountryConfigIdsFromString(helper.getString(5));
	        return lobbyConfig;
        }
        finally
        {
        	helper.close();
        }
	}

	/**
	 * Remove lobby from database
	 * 
	 * @param id ID of the lobby to remove
	 * @throws SQLException
	 */
	public static void delete(int id) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Remove lobby record
	    	helper.prepareQuery("DELETE FROM lobbies WHERE id=?");
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
	 * Updates a previously created lobby in the database
	 * 
	 * @param lobbyConfig The lobby to save
	 * @throws SQLException
	 */
	public static void save(LobbyConfig lobbyConfig) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Update maps record
	    	helper.prepareQuery("UPDATE lobbies SET name=?, backgroundImageNumber=?, sizeX=?, sizeY=?, tileCountryConfigIds=?, worldMapEnterButtonX=?, worldMapEnterButtonY=?, minRequiredLevel=?, maxLevel=?, maxUsers=? WHERE id=?");
	    	helper.setString(1, lobbyConfig.name);
	    	helper.setInt(2, lobbyConfig.backgroundImageNumber);
	    	helper.setInt(3, lobbyConfig.sizeX);
	    	helper.setInt(4, lobbyConfig.sizeY);
	    	helper.setString(5, lobbyConfig.tileCountryConfigIdsToString());
	    	helper.setInt(6, lobbyConfig.worldMapEnterButtonX);
	    	helper.setInt(7, lobbyConfig.worldMapEnterButtonY);
	    	helper.setInt(8, lobbyConfig.minRequiredLevel);
	    	helper.setInt(9, lobbyConfig.maxLevel);
	    	helper.setInt(10, lobbyConfig.maxUsers);
	    	helper.setInt(11, lobbyConfig.id);
	    	
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}	
}

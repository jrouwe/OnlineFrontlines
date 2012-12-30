package onlinefrontlines.game;

import java.util.*;
import java.sql.SQLException;
import onlinefrontlines.utils.DbQueryHelper;
import org.apache.log4j.Logger;

/**
 * This class communicates with the database and manages reading/writing TerrainConfig objects
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
public class TerrainConfigDAO 
{
	private static final Logger log = Logger.getLogger(TerrainConfigDAO.class);

	/**
	 * Loads all terrain information from the database
	 * 
	 * @return a list of all TerrainConfig objects in the database
	 * 
	 * @throws SQLException
	 */
	public static ArrayList<TerrainConfig> loadAllTerrain() throws SQLException
	{
		ArrayList<TerrainConfig> terrain = new ArrayList<TerrainConfig>();

    	DbQueryHelper helper = new DbQueryHelper();
    	DbQueryHelper helper2 = new DbQueryHelper();
        try
        {
        	// Find all maps
	    	helper.prepareQuery("SELECT id, name, victoryPoints, strengthModifier FROM terrain");
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
		        // Construct TerrainConfig object
		        TerrainConfig terrainConfig = new TerrainConfig();
		        terrainConfig.id = helper.getInt(1);
		        terrainConfig.name = helper.getString(2);
		        terrainConfig.victoryPoints = helper.getInt(3);
		        terrainConfig.strengthModifier = helper.getInt(4);
		        
		        // Get tile image numbers
		        helper2.prepareQuery("SELECT tileImageNumber, edgeTerrainImageNumber, openTerrainImageNumber FROM terrain_tile_properties WHERE terrainId=?");
		        helper2.setInt(1, terrainConfig.id);
		        helper2.executeQuery();
		        while (helper2.nextRecord())
		        {
		        	TerrainTileProperties ttp = new TerrainTileProperties();
		        	ttp.tileImageNumber = helper2.getInt(1);
		        	ttp.edgeTerrainImageNumber = helper2.getInt(2);
		        	ttp.openTerrainImageNumber = helper2.getInt(3);
		        	terrainConfig.tileProperties.add(ttp);
		        }
		        
		        // Add to list
		        terrain.add(terrainConfig);
	    	}
        }
        finally
        {
        	helper.close();
        	helper2.close();
        }
        
        log.info("Loaded " + terrain.size() + " terrain");
        
        return terrain;
	}
}

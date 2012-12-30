package onlinefrontlines.game;

import java.sql.SQLException;
import java.util.*;

/**
 * This class holds information about a terrain type
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
public final class TerrainConfig 
{
	/**
	 * Id of the terrain type
	 */
	public int id = -1;
	
	/**
	 * Name of the terrain type
	 */
	public String name;
	
	/**
	 * Number of victory points associated with this terrain
	 */
	public int victoryPoints;
	
	/**
	 * Image numbers of tiles corresponding to terrain type
	 */
    public final ArrayList<TerrainTileProperties> tileProperties = new ArrayList<TerrainTileProperties>();
    
    /**
     * Strength modifier for the terrain (percent change relative to max damage)
     */
    public int strengthModifier;
    
    /**
     * Static access to predefined terrain configs
     */
	public static TerrainConfig airTerrain;
	public static TerrainConfig plainsTerrain;
	public static TerrainConfig waterTerrain;

	/**
	 * Static access to all terrain info
	 */
	public static ArrayList<TerrainConfig> allTerrain = new ArrayList<TerrainConfig>();
	
	/**
	 * Maps tileImageNumber to TerrainConfig
	 */
	public static HashMap<Integer, TerrainConfig> allTerrainMap = new HashMap<Integer, TerrainConfig>();

	/**
	 * Load all terrain information from the database
	 * 
	 * @throws SQLException
	 */
	static public void loadAll() throws SQLException
	{
		allTerrainMap.clear();
    	allTerrain = TerrainConfigDAO.loadAllTerrain();
    	
    	for (TerrainConfig t : allTerrain) 
    	{
    		if (t.name.equalsIgnoreCase("air"))
    			airTerrain = t;

    		if (t.name.equalsIgnoreCase("plains"))
    			plainsTerrain = t;

    		if (t.name.equalsIgnoreCase("water"))
    			waterTerrain = t;

    		for (TerrainTileProperties ttp : t.tileProperties)
    			allTerrainMap.put(ttp.tileImageNumber, t);
    	}
	}
	
	/**
	 * Remove all loaded terrain information
	 */
	static public void unloadAll()
	{
		allTerrainMap.clear();
		allTerrain.clear();
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
	 * Victory points
	 */
	public int getVictoryPoints()
	{
		return victoryPoints;
	}
	
	/**
	 * Image numbers of tiles corresponding to terrain type
	 */
    public ArrayList<TerrainTileProperties> getTileProperties()
    {
    	return tileProperties;
    }
    
    /**
	 * Strength modifier
	 */
	public int getStrengthModifier()
	{
		return strengthModifier;
	}

	/**
	 * Get first tile image number
	 */
	public int getFirstTileImageNumber()
	{
		if (tileProperties.size() > 0)
			return tileProperties.get(0).getTileImageNumber();
		else
			return -1;
	}
}

package onlinefrontlines.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.sql.SQLException;
import onlinefrontlines.utils.HexagonGrid;

/**
 * Defenition of one continent (lobby)
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
public class LobbyConfig extends HexagonGrid
{
	/**
	 * Id for this lobby
	 */
	public int id;
	
	/**
	 * Name for this lobby
	 */
	public String name;
	
	/**
	 * Background image number
	 */
	public int backgroundImageNumber;
	
	/**
	 * Country config ids
	 */
	private int[] tileCountryConfigIds;
	
	/**
	 * Used country config ids
	 */
	private int[] usedCountryConfigIds;
	
	/**
	 * Location of the enter button in the world map
	 */
	public int worldMapEnterButtonX;
	public int worldMapEnterButtonY;
	
	/**
	 * Minimal level required for a player to enter
	 */
	public int minRequiredLevel;
	
	/**
	 * Maximum level for a player to enter
	 */
	public int maxLevel;
	
	/**
	 * Maximum users allowed to play in this lobby
	 */
	public int maxUsers;
	
	/**
	 * Static access to all lobbies
	 */
	public static ArrayList<LobbyConfig> allLobbies;
	
	/**
	 * Maps id to LobbyConfig
	 */
	public static HashMap<Integer, LobbyConfig> allLobbiesMap = new HashMap<Integer, LobbyConfig>();

	/**
	 * Constructor
	 */
	public LobbyConfig(int id, String name, int backgroundImageNumber, int sizeX, int sizeY, int worldMapEnterButtonX, int worldMapEnterButtonY, int minRequiredLevel, int maxLevel, int maxUsers)
	{
		super(sizeX, sizeY);
		
		this.id = id;
		this.name = name;
		this.backgroundImageNumber = backgroundImageNumber;
		this.worldMapEnterButtonX = worldMapEnterButtonX;
		this.worldMapEnterButtonY = worldMapEnterButtonY;
		this.minRequiredLevel = minRequiredLevel;
		this.maxLevel = maxLevel;
		this.maxUsers = maxUsers;
		
		tileCountryConfigIds = new int [sizeX * sizeY];	
		for (int i = 0; i < tileCountryConfigIds.length; ++i)
			tileCountryConfigIds[i] = 0;
	}
	
	/**
	 * Load all lobby information from the database
	 * 
	 * @throws SQLException
	 */
	static public void loadAll() throws SQLException
	{
		allLobbiesMap.clear();
    	allLobbies = LobbyConfigDAO.loadAllLobbies();

    	for (LobbyConfig l : allLobbies)
   			allLobbiesMap.put(l.id, l);
	}
	
	/**
	 * Unload all lobby information
	 */
	static public void unloadAll()
	{
		allLobbiesMap.clear();
		allLobbies.clear();
	}
	
	/**
	 * Lobby id
	 */
	public int getId()
	{
		return id;		
	}
	
	/**
	 * Lobby name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get location of world map enter button
	 */
	public int getWorldMapEnterButtonX()
	{
		return worldMapEnterButtonX;
	}
	
	/**
	 * Get location of world map enter button
	 */
	public int getWorldMapEnterButtonY()
	{
		return worldMapEnterButtonY;
	}
	
	/**
	 * Minimal level required for a player to enter
	 */
	public int getMinRequiredLevel()
	{
		return minRequiredLevel;
	}
	
	/**
	 * Maximum level for a player to enter
	 */
	public int getMaxLevel()
	{
		return maxLevel;
	}
	
	/**
	 * Maximum users allowed to play in this lobby
	 */
	public int getMaxUsers()
	{
		return maxUsers;
	}

	/**
	 * Get name suitable as a prefix for a filename
	 */
	public String getFilenamePrefix()
	{
		return name.toLowerCase().replace(' ', '_');
	}
	
	/**
	 * Get country config id for a tile
	 */
	public int getCountryConfigId(int x, int y)
	{
		return tileCountryConfigIds[x + y * sizeX];
	}
	
	/**
	 * Get list of config ids that are used by this lobby
	 */
	public int[] getUsedCountryConfigIds()
	{
		return usedCountryConfigIds;
	}
	
	/**
	 * Get country config ids as comma separated string
	 */
	public String tileCountryConfigIdsToString()
	{
		StringBuilder b = new StringBuilder(tileCountryConfigIds.length * 3);
		
		if (tileCountryConfigIds.length > 0)
			b.append(tileCountryConfigIds[0]);
		
		for (int i = 1; i < tileCountryConfigIds.length; ++i)
		{
			b.append(',');
			b.append(tileCountryConfigIds[i]);
		}
		
		return b.toString();
	}
	
	/**
	 * Parse country config ids from comma separated string
	 */
	public void tileCountryConfigIdsFromString(String data)
	{
		int n = sizeX * sizeY;

		// Split the list of comma separated numbers
		String[] tiles = data.split(",");
		if (tiles.length != n) 
			throw new RuntimeException("Incorrect number of tiles");
		
		// Convert to int array
		HashSet<Integer> usedConfigs = new HashSet<Integer>();
		tileCountryConfigIds = new int[n];
		for (int i = 0; i < n; ++i)
		{
			int id = Integer.parseInt(tiles[i]);
			tileCountryConfigIds[i] = id;
			if (id > 0)
				usedConfigs.add(id);
		}
		usedCountryConfigIds = new int[usedConfigs.size()];
		Iterator<Integer> it = usedConfigs.iterator(); 
		for (int i = 0; i < usedConfigs.size(); ++i)
			usedCountryConfigIds[i] = it.next();
	}
}

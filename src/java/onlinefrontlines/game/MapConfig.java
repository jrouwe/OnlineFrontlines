package onlinefrontlines.game;

import onlinefrontlines.utils.HexagonGrid;

/**
 * Tile layout for a game
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
public final class MapConfig extends HexagonGrid
{
	/**
	 * Id for this map
	 */
	public final int id;
	
	/**
	 * Name for this map
	 */
	public String name;
	
	/**
	 * Tile image numbers
	 */
	private int[] tileImageNumbers;
	
	/**
	 * Owning faction of the tiles
	 */
	private Faction[] tileOwners;
	
	/**
	 * User that created this config
	 */
	public int creatorUserId;

	/**
	 * Constructor
	 *
	 * @param id Id for the map
	 * @param name Name of the new map
	 * @param sizeX Horizontal amount of tiles
	 * @param sizeY Vertical amount of tiles
	 */
	public MapConfig(int id, String name, int sizeX, int sizeY)
	{
		super(sizeX, sizeY);
		
		this.id = id;
		this.name = name;
		
		tileImageNumbers = new int [sizeX * sizeY];	
		for (int i = 0; i < tileImageNumbers.length; ++i)
			tileImageNumbers[i] = 1;
		
		tileOwners = new Faction[sizeX * sizeY];
		for (int y = 0; y < sizeY; ++y)
		{
			int x = 0;
			for (; x < sizeX / 2 - 1; ++x)
				tileOwners[x + y * sizeX] = Faction.f1;
			for (; x < sizeX / 2 + 1; ++x)
				tileOwners[x + y * sizeX] = Faction.none;
			for (; x < sizeX; ++x)
				tileOwners[x + y * sizeX] = Faction.f2;
		}
	}
	
	/**
	 * Copy constructor
	 */
	public MapConfig(MapConfig other)
	{
		super(other);
		
		id = other.id;
		name = other.name;
		tileImageNumbers = other.tileImageNumbers.clone();
		tileOwners = other.tileOwners.clone();
		creatorUserId = other.creatorUserId;
	}
		
	/**
	 * Id for this map
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Name for this map
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * User that created this config
	 */
	public int getCreatorUserId()
	{
		return creatorUserId;
	}

	/**
	 * Get map image numbers as comma separated string
	 */
	public String tileImageNumbersToString()
	{
		StringBuilder b = new StringBuilder(tileImageNumbers.length * 3);
		
		if (tileImageNumbers.length > 0)
			b.append(tileImageNumbers[0]);
		
		for (int i = 1; i < tileImageNumbers.length; ++i)
		{
			b.append(',');
			b.append(Integer.toString(tileImageNumbers[i]));
		}
		
		return b.toString();
	}
	
	/**
	 * Parse map image numbers from comma separated string
	 */
	public void tileImageNumbersFromString(String data)
	{
		int n = sizeX * sizeY;

		// Split the list of comma separated numbers
		String[] tiles = data.split(",");
		if (tiles.length != n) 
			throw new RuntimeException("Incorrect number of tiles");
		
		// Convert to tileImageNumbers int array
		tileImageNumbers = new int[n];
		for (int i = 0; i < n; ++i)
			tileImageNumbers[i] = Integer.parseInt(tiles[i]);
	}
	
	/**
	 * Get map tile owners as comma separated string
	 */
	public String tileOwnersToString()
	{
		StringBuilder b = new StringBuilder(tileOwners.length * 2);
		
		if (tileOwners.length > 0)
			b.append(Faction.toInt(tileOwners[0]));
		
		for (int i = 1; i < tileOwners.length; ++i)
		{
			b.append(',');
			b.append(Faction.toInt(tileOwners[i]));
		}
		
		return b.toString();
	}
	
	/**
	 * Parse map tile owners from comma separated string
	 */
	public void tileOwnersFromString(String data)
	{
		int n = sizeX * sizeY;

		// Split the list of comma separated numbers
		String[] owners = data.split(",");
		if (owners.length != n) 
			throw new RuntimeException("Incorrect number of tiles");
		
		// Convert to tileOwners Faction array
		tileOwners = new Faction[n];
		for (int i = 0; i < n; ++i)
			tileOwners[i] = Faction.fromInt(Integer.parseInt(owners[i]));
	}
	
	/**
	 * Get terrain id at location x, y
	 */
	public TerrainConfig getTerrainAt(int x, int y)
	{
		return TerrainConfig.allTerrainMap.get(tileImageNumbers[x + y * sizeX]);
	}
	
	/**
	 * Returns terrain owner at x, y
	 */
	public Faction getTerrainOwnerAt(int x, int y)
	{
		return tileOwners[x + y * sizeX];
	}

	/**
	 * Returns a clone of the tile owners array
	 */
	public Faction[] cloneTileOwners()
	{
		return tileOwners.clone();
	}
	
	/**
	 * Get amount of victory points available in the map for one faction
	 */
	public int getVictoryPoints(Faction faction)
	{
		int points = 0;
		
		for (int x = 0; x < sizeX; ++x)
			for (int y = 0; y < sizeY; ++y)
				if (getTerrainOwnerAt(x, y) == faction)
					points += getTerrainAt(x, y).victoryPoints;

		return points;
	}	
}

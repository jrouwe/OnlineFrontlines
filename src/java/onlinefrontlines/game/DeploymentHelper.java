package onlinefrontlines.game;

import java.awt.Point;
import java.util.Collections;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import onlinefrontlines.profiler.Profiler;
import onlinefrontlines.profiler.Sampler;
import onlinefrontlines.utils.HexagonGridImpl;

/**
 * Helper class to determine which units can go where initially
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
public class DeploymentHelper 
{	
	/**
	 * Random generator
	 */
	private Random random;

	/**
	 * Cached country config
	 */
	private CountryConfig countryConfig;
	
	/**
	 * Cached map config
	 */
	private MapConfig mapConfig;
	
	/**
	 * Generated unit list
	 */
	ArrayList<UnitState> unitList;
	
	/**
	 * Units on grid
	 */
	private UnitState[] unitGrid;
	
	/**
	 * Next unit id
	 */
	private int unitId;
	
	/**
	 * Warnings generated during deployment
	 */
	private ArrayList<String> warnings;
	
	/**
	 * Constructor
	 */
	public DeploymentHelper(Random random)
	{
		this.random = random;
	}
	
	/**
	 * Get deployment for country
	 */
	public ArrayList<UnitState> getDeployment(CountryConfig countryConfig) throws DeploymentFailedException
	{
		Sampler sampler = Profiler.getInstance().startSampler(Profiler.CATEGORY_GENERAL, "DeploymentHelper.getDeployment");
		try
		{
			// Cache country and map
			this.countryConfig = countryConfig;		
			mapConfig = countryConfig.getMapConfig();
	
			// Create list/grid
			unitList = new ArrayList<UnitState>();
			unitGrid = new UnitState[mapConfig.sizeX * mapConfig.sizeY];
			
			// Create warnings array
			warnings = new ArrayList<String>();
			
			// Get deployment for each faction
			getDeploymentForFaction(Faction.f1);
			getDeploymentForFaction(Faction.f2);
			
			// Randomize units
			Collections.shuffle(unitList, random);
			
			// Assign id's
			unitId = 1;
			for (UnitState u : unitList)
				assignId(u);
			
			return unitList;
		}
		finally
		{
			sampler.stop();
		}
	}
	
	/**
	 * Get warnings while generating
	 */
	public ArrayList<String> getWarnings()
	{
		return warnings;
	}

	/**
	 * Recursively assign Id's
	 */
	private void assignId(UnitState u)
	{
		u.id = unitId++;
		
		for (UnitState c : u.containedUnits)
			assignId(c);
	}
	
	/**
	 * Get unit on grid
	 */
	private UnitState getUnit(int x, int y)
	{
		return unitGrid[x + y * mapConfig.sizeX];
	}
	
	/**
	 * Add unit to grid
	 */
	private void addUnit(UnitState unit)
	{		
		UnitState container = getUnit(unit.locationX, unit.locationY);
		if (container != null)
		{
			unit.container = container;
			container.containedUnits.add(unit);
		}
		else
		{
			unitGrid[unit.locationX + unit.locationY * mapConfig.sizeX] = unit;
			unitList.add(unit);
		}
	}
	
	/**
	 * Class that keeps track of a deployment location
	 */
	private class Location implements Comparable<Location>
	{
		public int x;
		public int y;
		public int cost;
		public int distToCenter;

		public Location(int x, int y, int cost, int distToCenter)
		{
			this.x = x;
			this.y = y;
			this.cost = cost;
			this.distToCenter = distToCenter;
		}
		
		@Override
		public int compareTo(Location other)
		{
			// First base on cost
			int diff = cost - other.cost;
			if (diff != 0)
				return diff;
			
			// Then base on distance to center
			return distToCenter - other.distToCenter;
		}
	}
	
	/**
	 * Helper class to contain deployment info
	 */
	private static class Deployment implements Comparable<Deployment>
	{		
		/**
		 * Config of unit to place
		 */
		public UnitConfig unitConfig;
		
		/**
		 * Amount of units to place
		 */
		public int amount;
		
		/**
		 * Possible locations of unit
		 */
		public ArrayList<Location> locations = new ArrayList<Location>();

		/**
		 * Comparator that sorts on bases first then on least amount of placement locations
		 */
		@Override
		public int compareTo(Deployment other) 
		{
			// Bases go first
			if (unitConfig.isBase != other.unitConfig.isBase)
				return unitConfig.isBase? -1 : 1;

			// Units that have less spots to place them on go first
			int l = locations.size() - amount;
			int r = other.locations.size() - other.amount;
			return l - r;
		}
	}

	/**
	 * Class that keeps track of cost
	 */
	@SuppressWarnings("serial")
	private static class Cost extends Point implements Comparable<Cost>
	{
		public int cost;

		public Cost(int x, int y, int cost)
		{
			super(x, y);
			this.cost = cost;
		}
		
		@Override
		public int compareTo(Cost other)
		{
			return cost - other.cost;
		}
	}
	
	/**
	 * Calculate grid of movement costs to enemy terrain for unit
	 */
	private HexagonGridImpl<Integer> getMovementCostToEnemyTerrain(UnitConfig config, Faction enemyFaction)
	{
		PriorityQueue<Cost> openList = new PriorityQueue<Cost>();
		
		// Get list of enemy terrain and init cost to 0
		for (int y = 1; y < mapConfig.sizeY - 1; ++y)
			for (int x = 1; x < mapConfig.sizeX - 1; ++x)
				if (mapConfig.getTerrainOwnerAt(x, y) == enemyFaction)
					openList.add(new Cost(x, y, 0));
		
		// Initialize grid
		HexagonGridImpl<Integer> grid = new HexagonGridImpl<Integer>(mapConfig.sizeX, mapConfig.sizeY, Integer.MAX_VALUE);
		
		Cost p;
		while ((p = openList.poll()) != null)
		{
			// Check to see if we found a better route
			if (grid.get(p) <= p.cost)
				continue;
			
			// Store best so far
			grid.set(p, p.cost);
			
			// Add neighbours to open list
			for (Point n : mapConfig.getNeighbours(p))
			{
				TerrainConfig terrain = mapConfig.getTerrainAt(n.x, n.y);
				UnitMovementCostProperties cp = config.movementCostProperties.get(terrain.id);
				openList.add(new Cost(n.x, n.y, p.cost + (cp != null? cp.movementCost : 100 * config.movementPoints)));
			}					
		}
		
		return grid;
	}
	
	/**
	 * Get deployment for one faction
	 */
	private void getDeploymentForFaction(Faction faction) throws DeploymentFailedException
	{		
		DeploymentConfig deploymentConfig = countryConfig.getDeploymentConfig(faction == Faction.f1? 0 : 1);

		// Determine center of map
		int centerX = mapConfig.sizeX / 2;
		int centerY = mapConfig.sizeY / 2;
		
		// Figure out where all units could potentially go
		ArrayList<Deployment> unitsToBeDeployed = new ArrayList<Deployment>();
		for (DeploymentAmount dpa : deploymentConfig.deploymentAmounts)
		{
			Deployment d = new Deployment();
			d.unitConfig = UnitConfig.allUnitsMap.get(dpa.unitId);
			d.amount = dpa.amount;

			// Get movement cost of all cells
			HexagonGridImpl<Integer> movementCostGrid = getMovementCostToEnemyTerrain(d.unitConfig, Faction.opposite(faction));
			
			// Add possible locations
			for (int y = 1; y < mapConfig.sizeY - 1; ++y)
				for (int x = 1; x < mapConfig.sizeX - 1; ++x)
					if (GameState.canUnitBeSetupOnHelper(mapConfig, d.unitConfig, faction, x, y))
						d.locations.add(new Location(x, y, movementCostGrid.get(x, y), MapConfig.getDistance(x,  y, centerX, centerY)));
			
			// Sort on closeness to the action
			Collections.sort(d.locations);
			
			// Bases want to be as far away as possible
			if (d.unitConfig.isBase)
				Collections.reverse(d.locations);
			
			unitsToBeDeployed.add(d);
		}
		Collections.sort(unitsToBeDeployed);

		// Loop all units that need to be placed
		ArrayList<UnitConfig> unableToPlace = new ArrayList<UnitConfig>();
		for (Deployment d : unitsToBeDeployed)
			for (int i = 0; i < d.amount; ++i)
			{
				// Find best location to place
				Location bestPoint = null;
				int bestDistance = 0;
				for (Location p : d.locations)
				{
					// Check if other unit in the way 
					if (getUnit(p.x, p.y) != null)
						continue;
					
					// If not a base then location was found
					if (!d.unitConfig.isBase)
					{
						bestPoint = p;
						break;
					}

					// Get closest distance to other bases
					int distanceToBase = Integer.MAX_VALUE;
					for (UnitState u : unitList)
						if (u.faction == faction 
							&& u.unitConfig.isBase)
						{
							int dist = u.getDistanceTo(p.x, p.y);
							if (dist < distanceToBase)
								distanceToBase = dist;
						}

					// For bases find the location that is farthest away from any other base
					// to avoid placement conflicts
					if (bestDistance < distanceToBase)
					{
						bestDistance = distanceToBase;
						bestPoint = p;
					}
				}

				if (bestPoint != null)
				{	
					// Check base distance
					if (d.unitConfig.isBase && bestDistance <= 2)
					{
						warnings.add("Base '" + d.unitConfig.name + "' (id=" + d.unitConfig.id + ") placed too close to other base "
								+ "on map '" + mapConfig.name + "' (id=" + mapConfig.id + ") "
								+ "using country '" + countryConfig.name + "' (id=" + countryConfig.id + ") "
								+ "for faction " + faction + " created by " + countryConfig.creatorUserId);
					}
					
					// Add the unit
					addUnit(new UnitState(-1, bestPoint.x, bestPoint.y, faction, d.unitConfig));
				}
				else
				{
					// Cannot place this unit
					unableToPlace.add(d.unitConfig);
				}
			}
		
		// Loop through all remaining units and check if they can be put in another unit
		for (UnitConfig u : unableToPlace)
		{
			// Loop through all units to see if it can contain the unit
			boolean done = false;
			for (UnitState c : unitList)
				if (c.faction == faction
					&& c.canHold(u))
				{
					// Add the unit
					addUnit(new UnitState(-1, c.locationX, c.locationY, faction, u));

					done = true;
					break;					
				}
			
			// Check if successful
			if (!done)
				throw new DeploymentFailedException(u, faction, countryConfig);
		}
	}
}

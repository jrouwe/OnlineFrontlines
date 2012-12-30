package onlinefrontlines.game;

import java.util.*;
import java.sql.SQLException;

/**
 * Settings for a unit type
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
public final class UnitConfig
{
	/**
	 * Id for the unit type
	 */
	public int id = -1;
	
	/**
	 * Name of the unit type
	 */
	public String name;
	
	/**
	 * Class of the unit
	 */
	public UnitClass unitClass = UnitClass.none;
	
	/**
	 * Image number for the unit
	 */
	public int imageNumber = -1;
	
	/**
	 * Maximum amount of armour when unit is spawned
	 */
	public int maxArmour = 0;
	
	/**
	 * Maps unit class to attack strength
	 */
	public final HashMap<UnitClass, UnitStrengthProperties> strengthProperties = new HashMap<UnitClass,UnitStrengthProperties>();
	
	/**
	 * Maximum amount of ammo when unit is spawned / recharged
	 */
	public int maxAmmo = 0;
	
	/**
	 * Amount of tiles the unit can look
	 */
	public int visionRange = 0;
	
	/**
	 * Amount of movement points that the unit has at the start of every turn
	 */
	public int movementPoints = 0;
	
	/**
	 * Amount of actions unit can perform (move = 1 action, attack = 1 action) 
	 */
	public int actions = 0;
	
	/**
	 * Maps TerrainConfig.id to movement cost for this terrain type
	 */
	public final HashMap<Integer, UnitMovementCostProperties> movementCostProperties = new HashMap<Integer, UnitMovementCostProperties>();
	
	/**
	 * Gives TerrainConfig.id's for terrains that this unit can be set up on 
	 */
	public final ArrayList<Integer> unitSetupOn = new ArrayList<Integer>();
	
	/**
	 * Gives TerrainConfig.id's for terrains that this unit must be next to (only if non empty) 
	 */
	public final ArrayList<Integer> unitSetupNextTo = new ArrayList<Integer>();

	/**
	 * Number of units this unit can contain
	 */
	public int containerMaxUnits = 0;
	
	/**
	 * List of units this unit can contain
	 */
	public ArrayList<Integer> containerUnitIds = new ArrayList<Integer>();
	
	/**
	 * Percentage of armour that is restored every turn for units contained in this unit (0-100)
	 */
	public int containerArmourPercentagePerTurn = 0;
	
	/**
	 * Percentage of ammo that is restored every turn for units contained in this unit (0-100)
	 */
	public int containerAmmoPercentagePerTurn = 0;
		
	/**
	 * If this unit can be transformed, this is the id of the unit it can transform to
	 */
	public int transformableToUnitId = 0;
	
	/**
	 * If this unit transforms in a container
	 */
	public TransformableType transformableType = TransformableType.none;

	/**
	 * Number of victory points associated with this unit
	 */
	public int victoryPoints;
	
	/**
	 * Description for the unit
	 */
	public String description;
	
	/**
	 * If this unit is a base or not
	 */
	public boolean isBase;	
	
	/**
	 * Victory category (if all units from one category are destroyed the opponent wins, possible values 0-31)
	 */
	public int victoryCategory;
	
	/**
	 * If unit is equal or closer to this range it will be detected by the enemy (show up as a ?) 
	 */
	public int beDetectedRange;

	/**
	 * Static access to all units
	 */
	static public ArrayList<UnitConfig> allUnits = new ArrayList<UnitConfig>();
	
	/**
	 * Maps UnitConfig.id to UnitConfig
	 */
	static public HashMap<Integer, UnitConfig> allUnitsMap = new HashMap<Integer, UnitConfig>();
	
	/**
	 * Reference to the unknown UnitConfig
	 */
	static public UnitConfig unknownUnit;
	
	/**
	 * Load all units from the database
	 * 
	 * @throws SQLException
	 */
	static public void loadAll() throws SQLException
	{
		allUnitsMap.clear();
	   	allUnits = UnitConfigDAO.loadAllUnits();
		unknownUnit = null;
    	
    	for (UnitConfig u : allUnits) 
    	{
    		if (u.name.equalsIgnoreCase("unknown"))
    			unknownUnit = u;
    		
    		allUnitsMap.put(u.id, u);		
    	}
	}
	
	/**
	 * Remove all loaded units
	 */
	static public void unloadAll()
	{
		allUnitsMap.clear();
		allUnits.clear();
		unknownUnit = null;
	}
	
	/**
	 * Unit id
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Unit name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Max unit armour
	 */
	public int getMaxArmour()
	{
		return maxArmour;
	}
	
	/**
	 * Max unit ammo
	 */
	public int getMaxAmmo()
	{
		return maxAmmo;
	}
	
	/**
	 * Vision range
	 */
	public int getVisionRange()
	{
		return visionRange;
	}
	
	/**
	 * Movement points
	 */
	public int getMovementPoints()
	{
		return movementPoints;
	}
	
	/**
	 * Actions
	 */
	public int getActions()
	{
		return actions;
	}
	
	/**
	 * Max contained units
	 */
	public int getContainerMaxUnits()
	{
		return containerMaxUnits;
	}
	
	/**
	 * Get container unit ids as string
	 */
	public String getContainerUnitIdsStringValue()
	{
		if (containerUnitIds.size() == 0)
			return "";
		
		StringBuilder rv = new StringBuilder();
		
		rv.append(containerUnitIds.get(0));
		
		for (int i = 1; i < containerUnitIds.size(); ++i)
		{
			rv.append(',');
			rv.append(containerUnitIds.get(i));
		}
		
		return rv.toString();
	}

	/**
	 * Amount of armour contained units gain per turn
	 */
	public int getContainerArmourPercentagePerTurn()
	{
		return containerArmourPercentagePerTurn;
	}
	
	/**
	 * Amount of ammo contained units gain per turn
	 */
	public int getContainerAmmoPercentagePerTurn()
	{
		return containerAmmoPercentagePerTurn;
	}
	
	/**
	 * Victory points
	 */
	public int getVictoryPoints()
	{
		return victoryPoints;
	}
	
	/**
	 * Description for the unit
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * If this unit is a base or not
	 */
	public boolean getIsBase()
	{
		return isBase;	
	}

	/**
	 * Victory category
	 */
	public int getVictoryCategory()
	{
		return victoryCategory;
	}
	
	/**
	 * Range where unit is detected
	 */
	public int getBeDetectedRange()
	{
		return beDetectedRange;
	}
	
	/*
	 * Image number for the unit
	 */
	public int getImageNumber()
	{
		return imageNumber;
	}
	
	/**
	 * Convert unit class to integer and return it
	 */
	public int getUnitClassIntValue()
	{
		return UnitClass.toInt(unitClass);
	}
	
	/**
	 * Convert unit class to string and return it
	 */
	public String getUnitClassStringValue()
	{
		return unitClass.toString();
	}
	
	/**
	 * Get all strength properties
	 */
	public Collection<UnitStrengthProperties> getAllStrengthProperties()
	{
		return strengthProperties.values();
	}
	
	/**
	 * Get strength vs land
	 */
	public UnitStrengthProperties getStrengthVsLand()
	{
		return strengthProperties.get(UnitClass.land);
	}
	
	/**
	 * Get strength vs air
	 */
	public UnitStrengthProperties getStrengthVsAir()
	{
		return strengthProperties.get(UnitClass.air);
	}
	
	/**
	 * Get strength vs water
	 */
	public UnitStrengthProperties getStrengthVsWater()
	{
		return strengthProperties.get(UnitClass.water);
	}
	
	/**
	 * Get strength vs land units as a string
	 */
	public String getStrengthVsLandStringValue()
	{
		UnitStrengthProperties result = strengthProperties.get(UnitClass.land);
		return result != null? result.toString() : "X";
	}
	
	/**
	 * Get strength vs water units as a string
	 */
	public String getStrengthVsWaterStringValue()
	{
		UnitStrengthProperties result = strengthProperties.get(UnitClass.water);
		return result != null? result.toString() : "X";
	}
	
	/**
	 * Get strength vs air units as a string
	 */
	public String getStrengthVsAirStringValue()
	{
		UnitStrengthProperties result = strengthProperties.get(UnitClass.air);
		return result != null? result.toString() : "X";
	}

	/**
	 * Get all movement costs
	 */
	public Collection<UnitMovementCostProperties> getAllMovementCostProperties()
	{
		return movementCostProperties.values();
	}
	
	/**
	 * Set up on string list
	 */
	public String getUnitSetupOnStringValue()
	{
		if (unitSetupOn.size() == 0)
			return "";
		
		StringBuilder rv = new StringBuilder();
		
		rv.append(unitSetupOn.get(0));
		
		for (int i = 1; i < unitSetupOn.size(); ++i)
		{
			rv.append(',');
			rv.append(unitSetupOn.get(i));
		}
		
		return rv.toString();
	}

	/**
	 * Set up next to string list
	 */
	public String getUnitSetupNextToStringValue()
	{
		if (unitSetupNextTo.size() == 0)
			return "";
		
		StringBuilder rv = new StringBuilder();
		
		rv.append(unitSetupNextTo.get(0));
		
		for (int i = 1; i < unitSetupNextTo.size(); ++i)
		{
			rv.append(',');
			rv.append(unitSetupNextTo.get(i));
		}
		
		return rv.toString();
	}

	/**
	 * If this unit transforms in a container
	 */
	public int getTransformableTypeIntValue()
	{
		return TransformableType.toInt(transformableType);
	}
	
	/**
	 * If this unit transforms in a container
	 */
	public String getTransformableTypeStringValue()
	{
		return transformableType.toString();
	}
	
	/**
	 * If this unit can be transformed, this is the id of the unit it can transform to
	 */
	public int getTransformableToUnitId()
	{
		return transformableToUnitId;
	}

	/** 
	 * Get strength properties
	 */
	public UnitStrengthProperties getStrengthProperties(UnitClass unitClass)
	{
		UnitStrengthProperties sp = strengthProperties.get(unitClass);
		if (sp != null)
			return sp;
		else
			return new UnitStrengthProperties(unitClass, 0, 0, 0);
	}
	
	/**
	 * Set strength properties
	 * 
	 * @param sp Strength properties
	 */
	public void setStrengthProperties(UnitStrengthProperties sp)
	{
		strengthProperties.remove(sp.enemyUnitClass);	
		
		if (sp.attackRange > 0 && (sp.maxStrengthWithAmmo > 0 || sp.maxStrengthWithoutAmmo > 0))
			strengthProperties.put(sp.enemyUnitClass, sp);
	}
	
	/**
	 * Get minimum movement cost per tile
	 */
	public int getMinimumMovementCost()
	{
		int smallest = Integer.MAX_VALUE;
		
		for (UnitMovementCostProperties cost : movementCostProperties.values())
			if (cost.movementCost < smallest)
				smallest = cost.movementCost;
				
		return smallest;
	}

	/**
	 * Get max amount of tiles this unit can move
	 */
	public int getMaxMovement()
	{
		return movementPoints / getMinimumMovementCost();
	}

	/**
	 * Get movement cost for specific terrain
	 */
	public int getMovementCost(TerrainConfig terrain)
	{
		if (movementCostProperties.get(terrain.id) != null)
			return movementCostProperties.get(terrain.id).movementCost;
		else
			return movementPoints + 1;
	}
	
	/**
	 * Set movement cost for specific terrain
	 * 
	 * @param terrainId Terrain type id
	 * @param movementCost Movement cost
	 */
	public void setMovementCost(UnitMovementCostProperties umc)
	{
		assert(umc.movementCost > 0);

		movementCostProperties.remove(umc.terrainId);
		
		if (umc.movementCost <= movementPoints)
			movementCostProperties.put(umc.terrainId, umc);
	}
	
	/**
	 * Check if the unit can move on a specific type of terrain 
	 */
	public boolean canMoveOn(TerrainConfig terrain)
	{
		return movementCostProperties.get(terrain.id) != null;
	}	
}

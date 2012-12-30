package onlinefrontlines.game;

import java.util.*;
import java.awt.Point;
import onlinefrontlines.utils.Tools;

/**
 * State of an active unit in the game
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
public final class UnitState
{
	// State
	public int id;
	public UnitConfig unitConfig;
	public UnitConfig initialUnitConfig;
	public int locationX;
	public int locationY;
	public final Faction initialFaction;
	public Faction faction;
	public int armour;
	public int ammo;
	public int movementPointsLeft = 0;
	public int actionsLeft = 0;
	public boolean lastActionWasMove = false;
	public ArrayList<UnitState> containedUnits = new ArrayList<UnitState>();
	public UnitState container = null;
	private boolean detectedByEnemy = false;
	private boolean signalDetectionLost = false; // Flag to indicate that detectedByEnemy needs to be set to false again
	public boolean identifiedByEnemy = false;
	
	/**
	 * Constructor
	 */
	public UnitState(int id, int locationX, int locationY, Faction faction, UnitConfig unitConfig)
	{
		// Fill in properties
		this.id = id;
		this.locationX = locationX;
		this.locationY = locationY;
		this.initialFaction = faction;
		this.faction = faction;
		this.unitConfig = unitConfig;
		this.initialUnitConfig = unitConfig;
		this.armour = unitConfig.maxArmour;
		this.ammo = unitConfig.maxAmmo;
	}

	/**
	 * Save state of a unit
	 */
	public UnitState saveState()
	{
		UnitState u = new UnitState(id, locationX, locationY, faction, unitConfig);

		u.armour = armour;
		u.ammo = ammo;
		u.movementPointsLeft = movementPointsLeft;
		u.actionsLeft = actionsLeft;
		u.lastActionWasMove = lastActionWasMove;
		u.container = container;
		
		u.containedUnits.clear();
		for (UnitState c : containedUnits)
			u.containedUnits.add(c);
		
		return u;
	}
	
	/**
	 * Restore state of a unit
	 */
	public void restoreState(UnitState s)
	{
		id = s.id;
		unitConfig = s.unitConfig;
		locationX = s.locationX;
		locationY = s.locationY;
		faction = s.faction;
		armour = s.armour;
		ammo = s.ammo;
		movementPointsLeft = s.movementPointsLeft;
		actionsLeft = s.actionsLeft;
		lastActionWasMove = s.lastActionWasMove;
		container = s.container;
		
		containedUnits.clear();
		for (UnitState c : s.containedUnits)
			containedUnits.add(c);
	}
	
	/**
	 * Get location
	 */
	public Point getLocation()
	{
		return new Point(locationX, locationY);
	}
	
	/**
	 * Get distance to specified location
	 */
	public int getDistanceTo(int x, int y)
	{
		return MapConfig.getDistance(locationX, locationY, x, y);
	}						
	
	/**
	 * Transform to other unit type
	 */
	public void transform()
	{
		// Get new config
		UnitConfig config = UnitConfig.allUnitsMap.get(unitConfig.transformableToUnitId);
		
		// Transform
		armour = (config.maxArmour * armour) / unitConfig.maxArmour;
		unitConfig = config;
	}			
	
	/**
	 * Check if a unit can be contained in this unit
	 */
	public boolean canHold(UnitConfig config)
	{
		return container == null
			&& unitConfig.containerUnitIds.contains(config.id)
			&& unitConfig.containerMaxUnits > 0
			&& containedUnits.size() < unitConfig.containerMaxUnits;
	}

	/**
	 * Check if a unit can be contained in this unit
	 */
	public boolean canHold(UnitState unit)
	{
		return unit.faction == faction
			&& unit.container == null
			&& canHold(unit.unitConfig);
	}

	/**
	 * Add unit as a contained unit
	 */
	public void addUnit(UnitState unit)
	{
		// Add to container
		containedUnits.add(unit);
		unit.container = this;
		unit.locationX = -1000;
		unit.locationY = -1000;
		if (!detectedByEnemy || !identifiedByEnemy)
			unit.setDetectedRecursive(false, Integer.MAX_VALUE);

		// Transform unit if needed
		UnitConfig otherConfig = UnitConfig.allUnitsMap.get(unit.unitConfig.transformableToUnitId);					
		if (unit.unitConfig.transformableType == TransformableType.inBase
			&& unitConfig.containerUnitIds.contains(otherConfig.id))
			unit.transform();
	}

	/**
	 * Remove unit as contained unit
	 */
	public void removeUnit(UnitState unit)
	{
		// Remove from array
		assert(unit.container == this);
		assert(containedUnits.contains(unit));
		containedUnits.remove(unit);			 
		unit.container = null;
	}
	
	/**
	 * Check if this unit can attack another unit of type unitClass
	 */
	public boolean canAttackUnitClass(UnitClass unitClass, Faction unitFaction)
	{
		UnitStrengthProperties sp = unitConfig.getStrengthProperties(unitClass);
		return faction != unitFaction
			&& armour > 0
			&& sp.getStrength(ammo > 0) > 0;
	}
	
	/**
	 * Check if the unit can attack another unit
	 */
	public boolean canAttack(UnitState unit)
	{
		return canAttackFrom(unit.getDistanceTo(locationX, locationY), unit);
	}	
	
	/**
	 * Check if the unit can counter attack another unit
	 */
	public boolean canCounterAttack(UnitState unit)
	{
		return canCounterAttackFrom(unit.getDistanceTo(locationX, locationY), unit);
	}	

	/**
	 * Check if the unit can attack another unit from position x, y
	 */
	public boolean canAttackFrom(int distance, UnitState unit)
	{
		return actionsLeft > 0
			&& canCounterAttackFrom(distance, unit);
	}	

	/**
	 * Check if the unit can counter attack another unit from position x, y
	 */
	public boolean canCounterAttackFrom(int distance, UnitState unit)
	{
		return container == null
			&& unit.container == null
			&& canAttackUnitClass(unit.unitConfig.unitClass, unit.faction)
			&& distance <= unitConfig.getStrengthProperties(unit.unitConfig.unitClass).attackRange;
	}	
	
	/**
	 * Determine the max armour loss an attack could have on unit of class unitClass
	 */
	public double getMaxArmourLossForAttack(UnitClass unitClass)
	{
		// Check if unit has armour
		if (unitConfig.maxArmour <= 0)
			return 0;
			
		// Get strength properties
		UnitStrengthProperties sp = unitConfig.getStrengthProperties(unitClass);
		
		// Get strength
		double strength = ((double)sp.getStrength(ammo > 0)) * armour / unitConfig.maxArmour;
		if (strength <= 0)
			return 0;
		
		// Calculate maximum armour loss for target
		double maxArmourLoss = Math.max(strength, 3.0);
		
		// Get max loss
		return maxArmourLoss;
	}

	/**
	 * Determine terrain modifier for attack
	 */
	public double getTerrainModifierForAttack(TerrainConfig attackerTerrain, TerrainConfig defenderTerrain)
	{
		// Get terrain modifier multiplier
		return 1.0 + ((double)attackerTerrain.strengthModifier - defenderTerrain.strengthModifier) / 100.0;
	}
	
	/**
	 * Average loss
	 */
	public double getAverageArmourLossForAttack(UnitClass unitClass, TerrainConfig attackerTerrain, TerrainConfig defenderTerrain)
	{
		// Get attack properties
		double maxArmourLoss = getMaxArmourLossForAttack(unitClass);
		double terrainModifier = getTerrainModifierForAttack(attackerTerrain, defenderTerrain);

		// Get uniformly distributed random number
		return 0.5 * terrainModifier * maxArmourLoss;		
	}

	/**
	 * Execute an attack from this unit to another unit (call again with parameters swapped to perform the counter attack)
	 */
	public void attack(UnitState unit, TerrainConfig attackerTerrain, TerrainConfig defenderTerrain, Random random)
	{
		// Check if other unit alive
		if (unit.armour <= 0)
			return;
		
		// Get attack properties
		double maxArmourLoss = getMaxArmourLossForAttack(unit.unitConfig.unitClass);
		if (maxArmourLoss == 0)
			return;
		double terrainModifier = getTerrainModifierForAttack(attackerTerrain, defenderTerrain);

		// Get uniformly distributed random number
		double average = 0.5 * terrainModifier * maxArmourLoss;
		double standardDeviation = 0.15 * maxArmourLoss;
		double rnd = Tools.gaussianRandom(average, standardDeviation, random);

		// Calculate loss
		int armourLoss = (int)Math.min(Math.max(Math.ceil(rnd), 0), maxArmourLoss);

		// Subtract loss
		unit.armour -= armourLoss;
		if (unit.armour <= 0)
		{
			if (!unit.unitConfig.isBase)
			{
				// This is not a base so it's broken now
				unit.armour = 0;		
			}
			else
			{
				// Bases change faction when destroyed
				unit.faction = faction;
				unit.armour = unit.unitConfig.maxArmour;
			}
		}

		// Decrease ammo
		UnitStrengthProperties sp = unitConfig.getStrengthProperties(unit.unitConfig.unitClass);
		if (sp.maxStrengthWithAmmo > 0)
		{
			--ammo;
			if (ammo < 0)
				ammo = 0;
		}
	}

	/**
	 * Execute an attack from this unit to another unit, then perform the counter attack
	 */
	public void attackAndCounter(UnitState unit, TerrainConfig attackerTerrain, TerrainConfig defenderTerrain, Random random)
	{
		// Attack the unit
		attack(unit, attackerTerrain, defenderTerrain, random);

		// Counter attack			
		if (unit.armour > 0 
			&& unit.canCounterAttack(this))
			unit.attack(this, defenderTerrain, attackerTerrain, random);
	}
	
	/**
	 * Recursively identify unit
	 */
	public void identifyUnitRecursive()
	{
		// Identify this unit
		setDetected(true);
		identifiedByEnemy = true;
		
		// Identify children
		for (UnitState u : containedUnits)
			u.identifyUnitRecursive(); 
	}
	
	/**
	 * Comparator for sorting units on their id's
	 */
	private static class SortOnId implements Comparator<UnitState>
	{
		 public int compare(UnitState u1, UnitState u2)
		 {
			 return u2.id - u1.id;
		 }
	}

	/**
	 * Dump the state of the game
	 */
	public void dumpState(StringBuilder b, Faction localFaction, int level)
	{
		if (!detectedByEnemy && localFaction == Faction.opposite(faction))
			return;
		
		for (int i = 0; i < level; ++i)
			b.append(" ");
		
		if (!identifiedByEnemy
			&& localFaction == Faction.opposite(faction))
		{
			b.append("id: ");
			b.append(id);
			b.append(", configId: unknown, locationX: ");
			b.append(locationX);
			b.append(", locationY: " + locationY);
			b.append(", faction: ");
			b.append(Faction.toInt(faction));
			b.append("\n");
		}
		else
		{
			b.append("id: ");
			b.append(id);
			b.append(", configId: ");
			b.append(unitConfig.id);
			b.append(", locationX: ");
			b.append(locationX);
			b.append(", locationY: " + locationY);
			b.append(", faction: ");
			b.append(Faction.toInt(faction));
			b.append(", armour: ");
			b.append(armour);
			b.append(", ammo: ");
			b.append(ammo);
			b.append(", movementPointsLeft: ");
			b.append(movementPointsLeft);
			b.append(", actionsLeft: ");
			b.append(actionsLeft);
			b.append(", lastActionWasMove: ");
			b.append(lastActionWasMove);
			b.append("\n");
		}
		
		// Sort contained units
		UnitState[] c = containedUnits.toArray(new UnitState[0]);
    	Arrays.sort(c, new SortOnId());
				
    	// Dump contained units
		for (UnitState u : c)
			u.dumpState(b, localFaction, level + 1);
	}

	/**
	 * Get number of containers this unit is in
	 */
	public int getNumContainers()
	{
		int num = 0;
		for (UnitState unit = container; unit != null; unit = unit.container)
			++num;
		return num;
	}
	
	/**
	 * Update detection state of unit
	 */
	public void setDetected(boolean detected)
	{
		// Update detection state
		if (detected)
		{
			detectedByEnemy = true;
			signalDetectionLost = false;
		}
		else
		{
			signalDetectionLost = detectedByEnemy;
		}
	}
	
	/**
	 * Recursively update detection state of unit
	 */
	public void setDetectedRecursive(boolean detected, int maxDepth)
	{
		// Detect unit
		setDetected(detected);
	
		// Limit depth
		--maxDepth;
		if (maxDepth <= 0)
			return;
		
		// Recursively update children
		for (UnitState u : containedUnits)
			u.setDetectedRecursive(detected, maxDepth);
	}
	
	/**
	 * Check if unit is currently detected by enemy
	 */
	public boolean isDetected()
	{
		return detectedByEnemy;		
	}
	
	/**
	 * Check if detection of unit has been lost since last action
	 */
	public boolean checkDetectionLost()
	{
		return signalDetectionLost;
	}
	
	/**
	 * Confirm detection lost, reset internal state
	 */
	public void confirmDetectionLost()
	{
		detectedByEnemy = false;
		signalDetectionLost = false;
		identifiedByEnemy = false;
	}
	
	/**
	 * Get total contained victory points
	 */
	public int getTotalVictoryPoints()
	{
		int points = unitConfig.victoryPoints;
		
		for (UnitState u : containedUnits)
			points += u.getTotalVictoryPoints();
		
		return points;
	}
}

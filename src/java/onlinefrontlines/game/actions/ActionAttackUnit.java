package onlinefrontlines.game.actions;

import onlinefrontlines.auth.User;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.IllegalRequestException;

/**
 * Action that performs an attack
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
public class ActionAttackUnit extends Action
{
	private UnitState selectedUnit;
	private UnitState targettedUnit;
	
	private int newStateSelectedUnitArmour = -1;
	private int newStateSelectedUnitAmmo;
	private Faction newStateSelectedUnitFaction;
	
	private int newStateTargettedUnitArmour;
	private int newStateTargettedUnitAmmo;
	private Faction newStateTargettedUnitFaction;
	
	/**
	 * Default constructor
	 */
	public ActionAttackUnit()
	{		
	}
	
	/**
	 * Constructor used by AI
	 */
	public ActionAttackUnit(UnitState selectedUnit, UnitState targettedUnit)
	{
		this.selectedUnit = selectedUnit;
		this.targettedUnit = targettedUnit;
	}
	
	/**
	 * Apply the action
	 */
	public void doAction(boolean addToDb) throws IllegalRequestException
	{
		// Both units needs to be identified
		selectedUnit.identifiedByEnemy = true;
		selectedUnit.setDetectedRecursive(true, 2);
		targettedUnit.identifiedByEnemy = true;
		targettedUnit.setDetectedRecursive(true, 2);
		
		// Deduct action point
		selectedUnit.actionsLeft--;
		selectedUnit.lastActionWasMove = false;			
		if (selectedUnit.actionsLeft <= 0)
			selectedUnit.movementPointsLeft = 0;		

		// Get terrain types	
		TerrainConfig selectedUnitTerrain = gameState.getAttackTerrain(selectedUnit);
		TerrainConfig targettedUnitTerrain = gameState.getAttackTerrain(targettedUnit);
		
		Faction oldSelectedUnitFaction = selectedUnit.faction;
		int oldSelectedUnitArmour = selectedUnit.armour;
		Faction oldTargettedUnitFaction = targettedUnit.faction;
		int oldTargettedUnitArmour = targettedUnit.armour;

		if (newStateSelectedUnitArmour == -1)
		{
			// Perform attack	
			selectedUnit.attackAndCounter(targettedUnit, selectedUnitTerrain, targettedUnitTerrain, gameState.random);
		}
		else
		{
			// Replay attack
			selectedUnit.armour = newStateSelectedUnitArmour;
			selectedUnit.ammo = newStateSelectedUnitAmmo;
			selectedUnit.faction = newStateSelectedUnitFaction;
			
			targettedUnit.armour = newStateTargettedUnitArmour;
			targettedUnit.ammo = newStateTargettedUnitAmmo;
			targettedUnit.faction = newStateTargettedUnitFaction;
		}

		// Accumulate stats for this attack
		gameState.accumulateAttackStats(
				selectedUnit.faction, 
				selectedUnit.unitConfig.id, 
				targettedUnit.unitConfig.id, 
				calculateDeaths(selectedUnit, oldSelectedUnitFaction), 
				calculateDeaths(targettedUnit, oldTargettedUnitFaction), 
				calculateDamage(targettedUnit, oldTargettedUnitArmour, oldTargettedUnitFaction), 
				calculateDamage(selectedUnit, oldSelectedUnitArmour, oldSelectedUnitFaction)); 

		// Check selected unit destroyed
		if (selectedUnit.armour <= 0)
		{
			calculateScore(selectedUnit, true);
			gameState.removeUnit(selectedUnit);
		}
		else if (selectedUnit.faction != oldSelectedUnitFaction)
		{
			calculateScore(selectedUnit, false);
			selectedUnit.containedUnits.clear();
		}

		// Check targetted unit destroyed
		if (targettedUnit.armour <= 0)
		{
			calculateScore(targettedUnit, true);
			gameState.removeUnit(targettedUnit);
		}
		else if (targettedUnit.faction != oldTargettedUnitFaction)
		{
			calculateScore(targettedUnit, false);
			targettedUnit.containedUnits.clear();
		}

		// Set new owner			
		gameState.updateTerrainOwner(selectedUnit);
		gameState.updateTerrainOwner(targettedUnit);

		// Save state selected unit
		newStateSelectedUnitArmour = selectedUnit.armour;
		newStateSelectedUnitAmmo = selectedUnit.ammo;
		newStateSelectedUnitFaction = selectedUnit.faction;
		
		// Save state targetted unit
		newStateTargettedUnitArmour = targettedUnit.armour;
		newStateTargettedUnitAmmo = targettedUnit.ammo;
		newStateTargettedUnitFaction = targettedUnit.faction;
		
		// Current player did something
		gameState.currentPlayerIdle = false;
	}
	
	/**
	 * Calculate damage taken
	 */
	private int calculateDamage(UnitState unit, int oldArmour, Faction oldFaction)
	{
		if (unit.faction != oldFaction)
			return oldArmour;
		
		return oldArmour - unit.armour;
	}
	
	/**
	 * Calculate number of deaths
	 */
	private int calculateDeaths(UnitState unit, Faction oldFaction)
	{
		if (unit.armour > 0 && unit.faction == oldFaction)
			return 0;
		
		return calculateDeathsRecursive(unit);
	}
	
	private int calculateDeathsRecursive(UnitState unit)
	{
		int count = 1;
		for (UnitState c : unit.containedUnits)
			count += calculateDeathsRecursive(c);
		return count;
	}
	
	/**
	 * Recursively determine score for units lost
	 */
	public void calculateScore(UnitState unit, boolean unregisterUnit)
	{
		// Unit needs to be identified or else the remote cannot calculate the score
		unit.setDetected(true);
		unit.identifiedByEnemy = true;
		
		// Unregister unit (so it cannot be used anymore in other actions)
		if (unregisterUnit)
			gameState.unregisterUnit(unit);
		
		// Get score for opposite faction
		Score oppositeScore = gameState.getScore(Faction.opposite(unit.initialFaction));
		
		if (!unit.unitConfig.isBase)
		{
			// Normal unit
			oppositeScore.numberOfUnitsDestroyed++;
			oppositeScore.victoryPointsForUnits += unit.unitConfig.victoryPoints;
		}
		else
		{
			// Base				
			if (unit.faction != unit.initialFaction)
			{
				// Base was captured, add score
				oppositeScore.numberOfBasesDestroyed++;
				oppositeScore.victoryPointsForBases += unit.unitConfig.victoryPoints;
			}
			else
			{
				// Base was recaptured, deduct score
				oppositeScore.numberOfBasesDestroyed--;
				oppositeScore.victoryPointsForBases -= unit.unitConfig.victoryPoints;
			}
		}
		
		// Recurse to children
		for (UnitState c : unit.containedUnits)
			calculateScore(c, true);
	}		
	
	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param, User initiatingUser) throws IllegalRequestException, IgnoreActionException
	{
		// Check phase
		if (gameState.turnNumber == 0)
			throw new IllegalRequestException("Not valid during setup phase");
		
		// Check winning state
		if (gameState.winningFaction != Faction.invalid)
			throw new IllegalRequestException("Game has ended");

		// Get selected unit
		selectedUnit = gameState.getUnitById(Integer.parseInt(param[1]));
		if (selectedUnit == null)
			throw new IllegalRequestException("Invalid selected unit");
		
		// Get targetted unit
		targettedUnit = gameState.getUnitById(Integer.parseInt(param[2]));
		if (targettedUnit == null)
			throw new IllegalRequestException("Invalid targetted unit");
		
		// Check if unit can attack other unit
		if (!selectedUnit.canAttack(targettedUnit))
			throw new IllegalRequestException("Unit cannot attack other unit");

		// Client is not allowed to send damage data
		if (initiatingUser == null)
		{
			// Get new state selected unit
			newStateSelectedUnitArmour = Integer.parseInt(param[3]);
			newStateSelectedUnitAmmo = Integer.parseInt(param[4]);
			newStateSelectedUnitFaction = Faction.fromInt(Integer.parseInt(param[5]));
			
			// Get new state targetted unit
			newStateTargettedUnitArmour = Integer.parseInt(param[6]);
			newStateTargettedUnitAmmo = Integer.parseInt(param[7]);
			newStateTargettedUnitFaction = Faction.fromInt(Integer.parseInt(param[8]));
		}
	}
	
	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction)
	{
		return "a," 
			+ selectedUnit.id + "," 
			+ targettedUnit.id + "," 
			+ newStateSelectedUnitArmour + "," 
			+ newStateSelectedUnitAmmo + "," 
			+ Faction.toInt(newStateSelectedUnitFaction) + "," 
			+ newStateTargettedUnitArmour + "," 
			+ newStateTargettedUnitAmmo + "," 
			+ Faction.toInt(newStateTargettedUnitFaction);  
	}
}

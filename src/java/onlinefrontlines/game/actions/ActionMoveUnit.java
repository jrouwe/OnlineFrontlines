package onlinefrontlines.game.actions;

import java.util.ArrayList;
import java.awt.Point;
import onlinefrontlines.auth.User;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.IllegalRequestException;

/**
 * Action to move a unit, possibly in or out a container.
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
public class ActionMoveUnit extends Action
{
	private UnitState selectedUnit;
	private ArrayList<Point> path;
	private boolean captureTiles;
	
	/**
	 * Default constructor
	 */
	public ActionMoveUnit()
	{
	}
	
	/**
	 * Constructor to set properties
	 */
	public ActionMoveUnit(UnitState selectedUnit, ArrayList<Point> path)
	{
		this.selectedUnit = selectedUnit;
		this.path = path;
	}
	
	/**
	 * Query if remote client should receive this action
	 */
	public receiveTime pendingActionGetReceiveTime(Faction remoteFaction)
	{
		return remoteFaction != Faction.opposite(selectedUnit.faction) 
			|| selectedUnit.isDetected()? receiveTime.now : receiveTime.never;
	}

	/**
	 * Apply the action
	 */
	public void doAction(boolean addToDb) throws IllegalRequestException
	{
		// Calculate cost of move
		int cost = 0;
		for (int i = 1; i < path.size(); ++i)
		{
			Point p = path.get(i);
			cost += gameState.getMovementCost(selectedUnit, p.x, p.y);
		}
		
		// Determine if this unit will capture tiles (remote client can be unaware of this when unit is in fog of war)
		captureTiles = selectedUnit.unitConfig.unitClass == UnitClass.land;

		// Change ownership of area
		if (captureTiles)
			for (Point p : path)
				if (gameState.getTerrainAt(p.x, p.y).victoryPoints > 0)
					gameState.setTerrainOwnerAt(p.x, p.y, selectedUnit.faction);

		// Remember container we came from
		UnitState fromContainer = selectedUnit.container;
		
		// Get target
		Point last = path.get(path.size() - 1);

		// Get unit at target location
		UnitState targetUnit = gameState.getUnit(last.x, last.y);
		
		// Move unit
		if (selectedUnit.container != null)
		{
			// If the units requires transforming before he can move here do it now
			UnitConfig otherConfig = UnitConfig.allUnitsMap.get(selectedUnit.unitConfig.transformableToUnitId);
			TerrainConfig terrain = gameState.getTerrainAt(last.x, last.y);
			if (!selectedUnit.unitConfig.canMoveOn(terrain)
				 && otherConfig != null 
				 && otherConfig.canMoveOn(terrain))
				selectedUnit.transform();				

			// Move out of container
			gameState.moveUnitOutOfContainer(selectedUnit, selectedUnit.container, last.x, last.y);
		}
		else if (targetUnit == null)
		{					
			// Move the unit
			gameState.moveUnit(selectedUnit, last.x, last.y);
		}
		else
		{
			// Move the unit in the container
			gameState.moveUnitInContainer(selectedUnit, targetUnit);
		}
		
		// Update action points
		if (fromContainer != null)
		{
			// Deduct cost when leaving container
			selectedUnit.actionsLeft--;
			selectedUnit.lastActionWasMove = false;
		}
		else if (selectedUnit.container != null)
		{
			// Lose all points when going into container
			selectedUnit.movementPointsLeft = 0;
			selectedUnit.actionsLeft = 0;
			selectedUnit.lastActionWasMove = true;
		}
		else
		{
			// Deduct cost
			if (!selectedUnit.lastActionWasMove)
				selectedUnit.actionsLeft--;
			selectedUnit.movementPointsLeft -= cost;
			selectedUnit.lastActionWasMove = true;
		}

		// Current player did something
		gameState.currentPlayerIdle = false;
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
			throw new IllegalRequestException("Invalid unit");
		
		// Get path
		path = new ArrayList<Point>();
		Point prev = null;
		for (int c = 3; c < param.length; c += 2)
		{
			if (c + 2 > param.length)
				throw new IllegalRequestException("Expected 2 parameters for point");
			
			Point p = new Point(Integer.parseInt(param[c]), Integer.parseInt(param[c + 1]));
			
			if (!gameState.mapConfig.isTileInPlayableArea(p))
				throw new IllegalRequestException("Position '" + p.toString() + "' is not in playable area");
			
			if (prev != null && MapConfig.getDistance(prev.x, prev.y, p.x, p.y) != 1)
				throw new IllegalRequestException("Position '" + p.toString() + "' is not one tile away from '" + prev.toString() + "'");
			
			path.add(p);
			
			prev = p;
		}
		
		// Check path is valid
		if (path.size() < 2)
			throw new IllegalRequestException("Path length < 2");
				
		// Get first and last point
		Point first = path.get(0);
		Point last = path.get(path.size() - 1);

		// Validate initial position
		if ((selectedUnit.container == null && (selectedUnit.locationX != first.x || selectedUnit.locationY != first.y))
			|| (selectedUnit.container != null && (selectedUnit.container.locationX != first.x || selectedUnit.container.locationY != first.y)))
			throw new IllegalRequestException("Invalid starting point");

		// Get unit at target location
		UnitState targetUnit = gameState.getUnit(last.x, last.y);

		// Check target is valid
		if (targetUnit != null && !targetUnit.canHold(selectedUnit))
			throw new IllegalRequestException("Trying to move into container when it is not possible");
		
		// Check if not moving from and to container
		if (targetUnit != null && selectedUnit.container != null)
			throw new IllegalRequestException("Cannot move from container to container");

		if (selectedUnit.container == null)
		{
			// Normal move
			
			// Check if actions left
			if (!selectedUnit.lastActionWasMove && selectedUnit.actionsLeft <= 0)
				throw new IllegalRequestException("No actions left");

			// Check if we have enough movement points
			int cost = 0;
			for (int i = 1; i < path.size(); ++i)
			{
				Point p = path.get(i);
				cost += gameState.getMovementCost(selectedUnit, p.x, p.y);
			}
			if (selectedUnit.movementPointsLeft < cost)
				throw new IllegalRequestException("Not enough movement points");
		}
		else
		{
			// Deploying
			
			// Check if actions left
			if (selectedUnit.actionsLeft <= 0)
				throw new IllegalRequestException("No actions left");

			// Check deploying from a contained container
			if (selectedUnit.container.container != null)
				throw new IllegalRequestException("Cannot deploy from a container in a container");
			
			// Check if path length is 2 
			if (path.size() != 2)
				throw new IllegalRequestException("Deploy must have path length 2");
			
			// Check if unit can be deployed at target
			UnitConfig otherConfig = UnitConfig.allUnitsMap.get(selectedUnit.unitConfig.transformableToUnitId);
			TerrainConfig terrain = gameState.getTerrainAt(last.x, last.y);
			if (!selectedUnit.unitConfig.canMoveOn(terrain)
				&& (selectedUnit.unitConfig.transformableType == TransformableType.inBase || otherConfig == null || !otherConfig.canMoveOn(terrain)))
				throw new IllegalRequestException("Unit cannot be deployed here");
		}		
	}
	
	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction)
	{
		String rv = "m," + selectedUnit.id + "," + (captureTiles? 1 : 0);
		
		for (Point p : path)
			rv += "," + p.x + "," + p.y;
		
		return rv;
	}
}

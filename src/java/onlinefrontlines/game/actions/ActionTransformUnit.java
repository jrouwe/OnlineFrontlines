package onlinefrontlines.game.actions;

import onlinefrontlines.auth.User;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.IllegalRequestException;

/**
 * Action sent by client to request the transforming of a unit.
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
public class ActionTransformUnit extends Action 
{
	private UnitState selectedUnit;
	private int locationX;
	private int locationY;
	private boolean captureTiles;
	
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
		// Transform unit
		selectedUnit.transform();
		gameState.moveUnit(selectedUnit, locationX, locationY);
		
		// Deduct cost
		selectedUnit.actionsLeft--;

		// Determine if this unit will capture tiles (remote client can be unaware of this when unit is in fog of war)
		captureTiles = selectedUnit.unitConfig.unitClass == UnitClass.land;

		// Change ownership of area
		gameState.updateTerrainOwner(selectedUnit);

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
		
		// Check if actions left
		if (selectedUnit.actionsLeft <= 0)
			throw new IllegalRequestException("No actions left");

		// Get new location
		locationX = Integer.parseInt(param[2]);
		locationY = Integer.parseInt(param[3]);

		// Check location
		if (!gameState.mapConfig.isTileInPlayableArea(locationX, locationY))
			throw new IllegalRequestException("Location is not in playable area");

		// Check if transformation is possible
		if (selectedUnit.containedUnits.size() != 0)
			throw new IllegalRequestException("Unit cannot be transformed with units inside");

		UnitConfig otherConfig = UnitConfig.allUnitsMap.get(selectedUnit.unitConfig.transformableToUnitId);					
		switch (selectedUnit.unitConfig.transformableType)
		{
		case inBase:
			throw new IllegalRequestException("Unit can only be transformed in base");
			
		case moveOneTile:
			if (!otherConfig.canMoveOn(gameState.getTerrain(selectedUnit)))
			{
				if (selectedUnit.container != null 
					|| selectedUnit.actionsLeft <= 0
					|| MapConfig.getDistance(selectedUnit.locationX, selectedUnit.locationY, locationX, locationY) != 1
					|| gameState.getUnit(locationX, locationY) != null
					|| !otherConfig.canMoveOn(gameState.getTerrainAt(locationX, locationY)))
					throw new IllegalRequestException("Unit cannot transform and move to new location");
				break;
			}
			// fall through, unit can convert on the spot
			
		case onSpot:
			if (selectedUnit.container != null 
				|| selectedUnit.actionsLeft <= 0 
				|| selectedUnit.locationX != locationX
				|| selectedUnit.locationY != locationY
				|| !otherConfig.canMoveOn(gameState.getTerrain(selectedUnit)))
				throw new IllegalRequestException("Unit cannot transform on spot");
			break;
			
		default:
			throw new IllegalRequestException("Unit cannot be transformed");				
		}
	}

	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction) 
	{
		return "t," + selectedUnit.id + "," + locationX + "," + locationY + "," + (captureTiles? 1 : 0);
	}
}

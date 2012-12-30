package onlinefrontlines.game.actions;

import onlinefrontlines.auth.User;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.IllegalRequestException;

/**
 * Action used during the setup phase to move a unit.
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
public class ActionTeleportUnit extends Action
{
	private UnitState selectedUnit;
	private int locationX;
	private int locationY;
	
	/**
	 * Query if remote client is allowed to send this action when it is not his turn 
	 */
	public boolean remoteHasPermissionToSendWhenNotHisTurn()
	{
		return true;
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
		// Get unit at target location
		UnitState targetUnit = gameState.getUnit(locationX, locationY);
	
		if (selectedUnit.container != null)
		{
			// Move out of container
			gameState.moveUnitOutOfContainer(selectedUnit, selectedUnit.container, locationX, locationY);

			// Transform unit back if needed
			if (selectedUnit.unitConfig != selectedUnit.initialUnitConfig)
				selectedUnit.transform();
		}
		else if (targetUnit == null)
		{					
			// Move the unit
			gameState.moveUnit(selectedUnit, locationX, locationY);
		}
		else
		{
			// Move the unit in the container
			gameState.moveUnitInContainer(selectedUnit, targetUnit);
		}
	}
	
	// Convert action from a string
	public void fromString(String[] param, User initiatingUser) throws IllegalRequestException, IgnoreActionException
	{
		// Check phase
		if (gameState.turnNumber != 0)
			throw new IllegalRequestException("Not valid during playing phase");
		
		// Get selected unit
		selectedUnit = gameState.getUnitById(Integer.parseInt(param[1]));
		if (selectedUnit == null)
			throw new IllegalRequestException("Invalid unit");

		// Check faction
		if (initiatingUser != null && gameState.getPlayerId(selectedUnit.faction) != initiatingUser.id)
			throw new IllegalRequestException("Trying to move a unit for the other player");
		
		// Get location
		locationX = Integer.parseInt(param[2]);
		locationY = Integer.parseInt(param[3]);
		
		// Validate position
		if (!gameState.mapConfig.isTileInPlayableArea(locationX, locationY))
			throw new IllegalRequestException("Position is not in playable area");
			
		// Get unit at target location
		UnitState targetUnit = gameState.getUnit(locationX, locationY);
		if (targetUnit != null)
		{
			// Check target is valid
			if (!targetUnit.canHold(selectedUnit))
				throw new IllegalRequestException("Trying to move into container when it is not possible");
		
			// Check if not moving from and to container
			if (selectedUnit.container != null)
				throw new IllegalRequestException("Cannot move from container to container");
		}
		else
		{
			// Check if unit can be moved to position
			if (!gameState.canUnitBeTeleportedTo(selectedUnit, locationX, locationY))
				throw new IllegalRequestException("Unit cannot move here");
		}
	}
	
	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction)
	{
		return "l," + selectedUnit.id + "," + locationX + "," + locationY;
	}
}

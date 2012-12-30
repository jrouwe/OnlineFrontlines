package onlinefrontlines.game.actions;

import onlinefrontlines.auth.User;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.IllegalRequestException;

/**
 * Action send by a client to request the end of its current turn. Also used by the server in case of a time out.
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
public class ActionEndTurn extends Action 
{
	/**
	 * Apply the action
	 */
	public void doAction(boolean addToDb) throws IllegalRequestException 
	{
		// Increment turn number
		if (gameState.currentPlayer == (gameState.faction1Starts? Faction.f2 : Faction.f1))
			gameState.turnNumber++;
			
		// Swap current player
		gameState.currentPlayer = Faction.opposite(gameState.currentPlayer);

		// Sync previous score with current score
		int c = Faction.toInt(gameState.currentPlayer) - 1;
		gameState.previousTurnScores[c] = new Score(gameState.scores[c]);

		// Reset any pending draw requests
		gameState.drawRequested[c] = false;

		// Current player is idle until he performs a move
		gameState.currentPlayerIdle = true;
		
		// Update units
		for (UnitState u : gameState.getUnits())
			if (u.faction == gameState.currentPlayer)
				prepareUnit(u);
			else
				unprepareUnit(u);

		if (addToDb)
		{
			// Reset turn end time
			gameState.resetTimeLeft();
			
			// Reset cache for top of page
			gameState.clearPageTopCache();
		}

		// Check if first turn
		if (gameState.turnNumber == 1 && gameState.currentPlayer == (gameState.faction1Starts? Faction.f1 : Faction.f2))
		{
			// Calculate initial terrain state
			gameState.calculateInitialTerrainOwners();
			
			// Determine amount of units
			gameState.determineInitialVictoryCategories();

			// Calculate initial identification
			gameState.updateIdentification();
		}
	}
	
	/**
	 * Prepare unit and children for next round
	 * 
	 * @return Number of units prepared
	 */
	private void prepareUnit(UnitState unit)
	{
		// Set action points
		unit.movementPointsLeft = unit.unitConfig.movementPoints;
		unit.actionsLeft = unit.unitConfig.actions;
		unit.lastActionWasMove = false;

		// Restock unit
		if (unit.container != null)
		{
			unit.armour = (int)Math.min(unit.unitConfig.maxArmour, unit.armour + Math.ceil(unit.unitConfig.maxArmour * unit.container.unitConfig.containerArmourPercentagePerTurn / 100.0));
			unit.ammo = (int)Math.min(unit.unitConfig.maxAmmo, unit.ammo + Math.ceil(unit.unitConfig.maxAmmo * unit.container.unitConfig.containerAmmoPercentagePerTurn / 100.0));
		}
		
		// Recurse to children
		for (UnitState c : unit.containedUnits)
			prepareUnit(c);
	}
	
	/**
	 * Prepare unit and children from other faction for next round
	 */
	private void unprepareUnit(UnitState unit)
	{
		// Get rid of left over action points
		unit.movementPointsLeft = 0;
		unit.actionsLeft = 0;

		// Recurse to children
		for (UnitState c : unit.containedUnits)
			unprepareUnit(c);
	}
	
	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param, User initiatingUser) throws IllegalRequestException, IgnoreActionException 
	{
		// Check winning state
		if (gameState.winningFaction != Faction.invalid)
			throw new IllegalRequestException("Game has ended");
	}

	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction) 
	{
		return "e";
	}
}

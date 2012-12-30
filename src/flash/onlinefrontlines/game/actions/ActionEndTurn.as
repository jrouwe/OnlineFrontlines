package onlinefrontlines.game.actions
{
	import mx.core.Application;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Ends the current turn
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
		// Only show scores once
		private var showScore : Boolean = true;
		
		// Undo info
		private var oldCurrentPlayer : int;
		private var oldTurnNumber : int;
		private var oldUnitState : Object;
		
		// Only notify once
		private var didNotification : Boolean = false;
	
		// Apply the action
		public override function doAction() : void
		{
			// Store old state
			oldCurrentPlayer = gameState.currentPlayer;
			oldTurnNumber = gameState.turnNumber;
			oldUnitState = new Object();
			saveState(gameState.getUnits());
			
			// Increment turn number
			if (gameState.currentPlayer == (gameState.faction1Starts? Faction.f2 : Faction.f1))
				gameState.turnNumber++;
				
			// Swap current player
			gameState.currentPlayer = Faction.opposite(gameState.currentPlayer);

			// Reset any pending draw requests
			gameState.drawRequested[gameState.currentPlayer - 1] = false;
			
 			// Update units
			var c : UnitState;	
			for each (var u : UnitState in gameState.getUnits())
				if (u.faction == gameState.currentPlayer)
					prepareUnit(u);
				else
					unprepareUnit(u);

			// Check if first turn
			if (gameState.turnNumber == 1 && gameState.currentPlayer == (gameState.faction1Starts? Faction.f1 : Faction.f2))
			{
				// Calculate initial terrain state
				gameState.calculateInitialTerrainOwners();
			}

			// Show notifications
			if (!didNotification)
			{
				if (gameState.currentPlayer == gameState.localPlayer)
					Application.application.slideBox.show("It is your turn!");
				else if (gameState.playByMail && gameState.currentPlayer != gameState.localPlayer)
					Application.application.slideBox.show("An e-mail notification has been sent!");
				didNotification = true;
			}

			// Switch state
			gameState.toState(new UIStateEndTurn(showScore));
			showScore = false;
		}
		
		// Check if the function is undoable or not
		public override function isUndoable() : Boolean
		{
			return gameState.turnNumber > 0;
		}

		// Undo the action
		public override function undoAction() : void
		{
			// Restore state
			gameState.turnNumber = oldTurnNumber;
			gameState.currentPlayer = oldCurrentPlayer;
			restoreState(gameState.getUnits());
								
			// Switch state
			gameState.toState(new UIStateEndTurn(false));
		}	
		
		// Recursively save state of all units in array
		private function saveState(units : Array) : void
		{
			for each (var u : UnitState in units)
			{
				oldUnitState[u.id] = u.saveState();
				saveState(u.containedUnits);
			}
		}
		
		// Recursively restore state of all units in array
		private function restoreState(units : Array) : void
		{
			for each (var u : UnitState in units)
			{
				u.restoreState(oldUnitState[u.id]);
				restoreState(u.containedUnits);
			}
		}
	
		/**
		 * Prepare unit and children for next round
		 */
		private function prepareUnit(unit : UnitState) : void
		{
			// Set action points
			unit.movementPointsLeft = unit.unitConfig.movementPoints;
			unit.actionsLeft = unit.unitConfig.actions;
			unit.lastActionWasMove = false;
			
			// Restock unit
			if (unit.container != null)
			{
				unit.armour = Math.min(unit.unitConfig.maxArmour, unit.armour + Math.ceil(unit.unitConfig.maxArmour * unit.container.unitConfig.containerArmourPercentagePerTurn / 100.0));
				unit.ammo = Math.min(unit.unitConfig.maxAmmo, unit.ammo + Math.ceil(unit.unitConfig.maxAmmo * unit.container.unitConfig.containerAmmoPercentagePerTurn / 100.0));
			}
			
			// Recurse to children
			for each (var c : UnitState in unit.containedUnits)
				prepareUnit(c);
		}
		
		/**
		 * Prepare unit and children from other faction for next round
		 */
		private function unprepareUnit(unit : UnitState) : void
		{
			// Get rid of left over action points
			unit.movementPointsLeft = 0;
			unit.actionsLeft = 0;
	
			// Recurse to children
			for each (var c : UnitState in unit.containedUnits)
				unprepareUnit(c);
		}
		
		// Convert action from a string
		public override function fromString(param : Array) : void
		{
		}
	
		// Convert action to a string
		public override function toString() : String
		{
			return "e";
		}
	}
}

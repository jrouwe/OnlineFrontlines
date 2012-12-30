package onlinefrontlines.game.actions
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Move a unit 
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
		private var selectedUnit : UnitState;
		private var captureTiles : Boolean;
		private var path : Array;
		
		// Undo info
		private var previousOwner : Array = new Array();
		private var oldState : UnitState;
	
		// Constructor
		public function ActionMoveUnit(selectedUnit : UnitState, path : Array) : void
		{
			this.selectedUnit = selectedUnit;
			this.captureTiles = selectedUnit != null && selectedUnit.unitConfig.unitClass == UnitClass.land;
			this.path = path;
		}
	
		// Apply the action
		public override function doAction() : void
		{
			// Save state
			oldState = selectedUnit.saveState();
			
			// Calculate cost of move
			var cost : int = 0;
			for (var i : int = 1; i < path.length; ++i)
				cost += gameState.getMovementCost(selectedUnit, path[i].x, path[i].y);
			
			// Change ownership of area
			if (captureTiles)
				for each (var p : Object in path)
				{
					previousOwner.push(gameState.getTerrainOwnerAt(p.x, p.y));
					if (gameState.getTerrainAt(p.x, p.y).victoryPoints > 0)
						gameState.setTerrainOwnerAt(p.x, p.y, selectedUnit.faction);		
				}
			
			// Remember container we came from
			var fromContainer : UnitState = selectedUnit.container;
			
			// Move unit
			performMove(path);		
			
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
	
			// Switch state
			gameState.toState(new UIStateUnitMoving(selectedUnit, fromContainer, path));
		}
		
		// Undo the action
		public override function undoAction() : void
		{
			// Reverse the path
			var reversedPath : Array = new Array();
			for (var i : int = path.length - 1; i >= 0; --i)
				reversedPath.push(path[i]);
				
			// Restore ownership of area
			if (captureTiles)
				for each (var p : Object in reversedPath)
					gameState.setTerrainOwnerAt(p.x, p.y, previousOwner.pop());		
			
			// Remember container we came from
			var fromContainer : UnitState = selectedUnit.container;
			
			// Move unit
			performMove(reversedPath);				
			
			// Restore old state
			selectedUnit.restoreState(oldState);

			// Switch state
			gameState.toState(new UIStateUnitMoving(selectedUnit, fromContainer, reversedPath));
		}	
		
		// Internal function that performs the actual move
		private function performMove(pathToFollow : Array) : void
		{
			// Get target
			var last : Object = pathToFollow[pathToFollow.length - 1];

			// Get unit at target location
			var targetUnit : UnitState = gameState.getUnit(last.x, last.y);
			
			if (selectedUnit.container != null)
			{
				// If the units requires transforming before he can move here do it now
				var otherConfig : UnitConfig = UnitConfig.allUnitsMap[selectedUnit.unitConfig.transformableToUnitId];
				var terrain : TerrainConfig = gameState.getTerrainAt(last.x, last.y);
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
		}		

		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			// Get selected unit
			selectedUnit = gameState.getUnitById(int(param[1]));
			captureTiles = int(param[2]) == 1;
			
			// Get path
			path = new Array();
			for (var c : int = 3; c < param.length; c += 2)
			{
				var p : Object = { x : int(param[c]), y : int(param[c + 1]) };
				
				path.push(p);
			}
		}
		
		/**
		 * Convert action to a string
		 */
		public override function toString() : String
		{
			var rv : String = "m," + selectedUnit.id + "," + (captureTiles? 1 : 0);
			
			for each (var p : Object in path)
				rv += "," + p.x + "," + p.y;
			
			return rv;
		}
	}
}

package onlinefrontlines.game.actions
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Spawn a new unit
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
	public class ActionCreateUnit extends Action 
	{
		private var id : int;
		private var locationX : int;
		private var locationY : int;
		private var faction : int;
		private var configId : int;
		private var containerId : int;
		private var reCreateUnit : Boolean;
	
		// Undo info
		private var unit : UnitState;

		// When undoing / redoing actions stop at this action
		public override function undoRedoStop() : Boolean
		{
			return false;
		}

		// Check if the function is undoable or not
		public override function isUndoable() : Boolean
		{
			return gameState.turnNumber > 0;
		}

		// Apply the action
		public override function doAction() : void
		{
			// Create unit
			if (unit == null)
				unit = new UnitState(id, locationX, locationY, faction, UnitConfig.allUnitsMap[configId]);

			// Register unit	
			gameState.registerUnit(unit);

			// Add to map
			if (containerId != -1)
			{
				var container : UnitState = gameState.getUnitById(containerId);
				container.addUnit(unit);
			}
			else
				gameState.addUnit(unit);

			// Switch state
			gameState.toState(new UIStateCreateRemoveUnit(unit, container, true));
		}	
		
		// Undo the action
		public override function undoAction() : void
		{
			// Remove from map
			var container : UnitState = null;
			if (containerId != -1)
			{
				container = gameState.getUnitById(containerId);
				container.removeUnit(unit);
			}				
			else
				gameState.removeUnit(unit);
			
			// Unregister unit
			gameState.unregisterUnit(unit);
			
			// Switch state
			gameState.toState(new UIStateCreateRemoveUnit(unit, container, false));
		}

		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			id = int(param[1]); 
			locationX = int(param[2]); 
			locationY = int(param[3]);
			faction = int(param[4]); 
			configId = int(param[5]);
			containerId = int(param[6]); 
			reCreateUnit = int(param[7]) != 0;
		}
		
		// Convert action to a string
		public override function toString() : String
		{
			return "c," 
					+ id + "," 
					+ locationX + "," 
					+ locationY + ","
					+ faction + "," 
					+ configId + ","
					+ containerId + "," 
					+ (reCreateUnit? 1 : 0);
		}
	}
}

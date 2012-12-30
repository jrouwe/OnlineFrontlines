package onlinefrontlines.game.actions
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	
	/*
	 * Conveys unit type to client (replace question mark with real unit)
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
	public class ActionIdentifyUnit extends Action 
	{
		private var id : int;
		private var configId : int;
		private var movementPointsLeft : int;
		private var actionsLeft : int;
		private var lastActionWasMove : Boolean;
		private var armour : int;
		private var ammo : int;
		
		// Undo info
		private var oldState : UnitState;
	
		// When undoing / redoing actions stop at this action
		public override function undoRedoStop() : Boolean
		{
			return false;
		}

		// Apply the action
		public override function doAction() : void
		{
			// Get unit
			var unit : UnitState = gameState.getUnitById(id);
			
			// Save state
			oldState = unit.saveState();

			// Reinit unit
			unit.init(UnitConfig.allUnitsMap[configId]);
			unit.movementPointsLeft = movementPointsLeft;
			unit.actionsLeft = actionsLeft;
			unit.lastActionWasMove = lastActionWasMove;
			unit.armour = armour;
			unit.ammo = ammo;
			
			// Update graphics
			unit.graphics.updateGraphics();
			unit.graphics.setVisible(unit.container == null);
			
			// Make sure that we're in an interactive state if this happens to be the last action executed
			gameState.toDefaultStateIfNoCurrentState();
		}	
		
		// Undo the action
		public override function undoAction() : void
		{
			// Get unit
			var unit : UnitState = gameState.getUnitById(id);

			// Restore state
			unit.restoreState(oldState);
		}
		
		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			id = int(param[1]); 
			configId = int(param[2]);
			movementPointsLeft = int(param[3]);
			actionsLeft = int(param[4]);
			lastActionWasMove = int(param[5]) != 0;
			armour = int(param[6]);
			ammo = int(param[7]);
		}
		
		// Convert action to a string
		public override function toString() : String
		{
			return "i," 
					+ id + "," 
					+ configId + ","
					+ movementPointsLeft + ","
					+ actionsLeft + ","
					+ (lastActionWasMove? 1 : 0) + ","
					+ armour + ","
					+ ammo;
		}
	}
}

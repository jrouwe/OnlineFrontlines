package onlinefrontlines.game.actions
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Remove unit from the grid
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
	public class ActionRemoveUnit extends Action 
	{
		private var id : int;
		
		// Undo info
		private var unit : UnitState;
		private var container : UnitState;
	
		// When undoing / redoing actions stop at this action
		public override function undoRedoStop() : Boolean
		{
			return false;
		}

		// Apply the action
		public override function doAction() : void
		{
			// Store undo info
			if (unit == null)
			{
				unit = gameState.getUnitById(id);
				container = unit.container;
			}
			
			// Remove from map
			if (container != null)
				container.removeUnit(unit);
			else
				gameState.removeUnit(unit);
			
			// Unregister unit
			gameState.unregisterUnit(unit);

			// Switch state
			gameState.toState(new UIStateCreateRemoveUnit(unit, container, false));
		}	
		
		// Undo the action
		public override function undoAction() : void
		{
			// Register unit	
			gameState.registerUnit(unit);

			// Add to map
			if (container != null)
				container.addUnit(unit);
			else
				gameState.addUnit(unit);

			// Switch state
			gameState.toState(new UIStateCreateRemoveUnit(unit, container, true));
		}	

		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			id = int(param[1]); 
		}
		
		// Convert action to a string
		public override function toString() : String
		{
			return "u," 
					+ id;
		}
	}
}

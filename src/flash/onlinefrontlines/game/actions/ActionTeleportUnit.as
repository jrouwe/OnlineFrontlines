package onlinefrontlines.game.actions
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Teleport unit (used during deployment)
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
		private var selectedUnit : UnitState;
		private var locationX : int;
		private var locationY : int;
		
		// Constructor
		public function ActionTeleportUnit(selectedUnit : UnitState, locationX : int, locationY : int) : void
		{
			this.selectedUnit = selectedUnit;
			this.locationX = locationX;
			this.locationY = locationY;
		}
	
		// Check if the function is undoable or not
		public override function isUndoable() : Boolean
		{
			return false;
		}

		// Apply the action
		public override function doAction() : void
		{
			// Remember container we came from
			var fromContainer : UnitState = selectedUnit.container;

			// Get unit at target location
			var targetUnit : UnitState = gameState.getUnit(locationX, locationY);

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

			// Mark that we've dragged a unit
			if (gameState.localPlayer == selectedUnit.faction)
			{
				if (selectedUnit.unitConfig.isBase)
					gameState.hasTeleportedBase = true;
				else
					gameState.hasTeleportedUnit = true;
			}
			
			// Switch state
			gameState.toState(new UIStateTeleportUnit(selectedUnit, fromContainer));
		}
		
		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			// Get selected unit
			selectedUnit = gameState.getUnitById(int(param[1]));
			locationX = int(param[2]);
			locationY = int(param[3]);
		}
		
		/**
		 * Convert action to a string
		 */
		public override function toString() : String
		{
			return "l," + selectedUnit.id + "," + locationX + "," + locationY;
		}
	}
}

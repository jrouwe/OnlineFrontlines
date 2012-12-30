package onlinefrontlines.game.actions
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Transform unit from one type to another (i.e. marine to lander)
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
		private var selectedUnit : UnitState;
		private var locationX : int;
		private var locationY : int;
		private var captureTiles : Boolean;
		
		// Undo info
		private var oldState : UnitState;
		private var previousOwner : int;
	
		// Constructor
		public function ActionTransformUnit(selectedUnit : UnitState, locationX : int, locationY : int) : void
		{
			this.selectedUnit = selectedUnit;
			this.locationX = locationX;
			this.locationY = locationY;
			if (selectedUnit != null)
			{
				var otherConfig : UnitConfig = UnitConfig.allUnitsMap[selectedUnit.unitConfig.transformableToUnitId];
				this.captureTiles = otherConfig.unitClass == UnitClass.land;
			}
			else
			{
				this.captureTiles = false;
			}
		}
	
		// Apply the action
		public override function doAction() : void
		{
			// Save state
			oldState = selectedUnit.saveState();
			
			// Transform unit
			selectedUnit.transform();
			gameState.moveUnit(selectedUnit, locationX, locationY);
			
			// Deduct cost
			selectedUnit.actionsLeft--;
	
			// Change ownership of area
			if (captureTiles)
			{
				previousOwner = gameState.getTerrainOwnerAt(selectedUnit.locationX, selectedUnit.locationY);
				gameState.updateTerrainOwner(selectedUnit);
			}

			// Switch state
			var path : Array = oldState.getSimplePath(selectedUnit.locationX, selectedUnit.locationY);
			gameState.toState(new UIStateUnitTransformed(selectedUnit, path));
		}
		
		// Undo the action
		public override function undoAction() : void
		{
			// Restore ownership of area
			if (captureTiles)
				gameState.setTerrainOwnerAt(selectedUnit.locationX, selectedUnit.locationY, previousOwner);		

			// Restore state
			gameState.removeUnit(selectedUnit);
			selectedUnit.restoreState(oldState);
			gameState.addUnit(selectedUnit);
			
			// Switch state
			var path : Array = selectedUnit.getSimplePath(locationX, locationY);
			if (path != null) path.reverse();
			gameState.toState(new UIStateUnitTransformed(selectedUnit, path));
		}	
		
		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			// Get selected unit
			selectedUnit = gameState.getUnitById(int(param[1]));
			locationX = int(param[2]);
			locationY = int(param[3]);
			captureTiles = int(param[4]) != 0;
		}
	
		// Convert action to a string
		public override function toString() : String
		{
			return "t," + selectedUnit.id + "," + locationX + "," + locationY + "," + captureTiles;
		}
	}
}

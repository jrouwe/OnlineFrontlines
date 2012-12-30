package onlinefrontlines.game.uistates
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * During deployment: Drag unit
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
	public class UIStateSettingUpDeployUnit extends UIState 
	{
		private var selectedUnit : UnitState;
		private var unitToBeDeployed : UnitState;

		public function UIStateSettingUpDeployUnit(selectedUnit : UnitState, unitToBeDeployed : UnitState) : void
		{
			this.selectedUnit = selectedUnit;
			this.unitToBeDeployed = unitToBeDeployed;
		}

		public override function onEnterState() : void
		{					
			// Mark tiles that unit can be deployed on
			for each (var l : Object in gameState.mapConfig.getNeighbours(selectedUnit.locationX, selectedUnit.locationY))
				if (gameState.getUnit(l.x, l.y) == null
					&& gameState.canUnitBeTeleportedTo(unitToBeDeployed, l.x, l.y))
					gameState.mapConfig.getTile(l.x, l.y).setSelectionImage(MapTile.tileSelectionMove);					
		}
		
		public override function onLeaveState() : void
		{
			gameState.clearTileSelection();
		}
		
		public override function onMousePressed(tile : MapTile) : void
		{
			// Check if valid tile clicked
			if (gameState.getUnitOnTile(tile) == null
				&& selectedUnit.getDistanceTo(tile.locationX, tile.locationY) == 1
				&& gameState.canUnitBeTeleportedTo(unitToBeDeployed, tile.locationX, tile.locationY))
			{
				// Request move
				gameState.request(new ActionTeleportUnit(unitToBeDeployed, tile.locationX, tile.locationY));
			}
			else
			{
				// Treat as normal drag operation
				gameState.toState(new UIStateSettingUp());
				gameState.tilePressed(tile);
			}
		}
	}
}

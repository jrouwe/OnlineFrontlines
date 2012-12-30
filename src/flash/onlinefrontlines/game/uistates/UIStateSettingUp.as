package onlinefrontlines.game.uistates
{
	import mx.core.Application;
	import flash.events.MouseEvent;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * During deployment: allow user to select unit
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
	public class UIStateSettingUp extends UIState 
	{
		private var selectedUnit : UnitState;

		public override function onEnterState() : void
		{
			// Go to waiting state if it is not our turn
			if (!gameState.canPerformAction())
			{
				gameState.toState(null);
				return;
			}
		}
		
		public override function onLeaveState() : void
		{
			stopDrag();
			
			gameState.clearTileSelection();
		}
		
		public override function onMousePressed(tile : MapTile) : void
		{
			if (selectedUnit != null)
				return;
				
			var unit : UnitState = gameState.getUnitOnTile(tile);
			if (unit != null
				&& unit.faction == gameState.localPlayer)
			{
				// Start dragging
				selectedUnit = unit;
				selectedUnit.graphics.doStartDrag();
				
				// Calculate possible drop locations
				for (var x : int = 0; x < gameState.mapConfig.sizeX; ++x)
					for (var y : int = 0; y < gameState.mapConfig.sizeY; ++y)
					{
						var selection : int;
						
						var otherUnit : UnitState = gameState.getUnit(x, y);
						if (otherUnit == selectedUnit
							|| (otherUnit != null && otherUnit.canHold(selectedUnit))
							|| (otherUnit == null && gameState.canUnitBeTeleportedTo(selectedUnit, x, y)))
							selection = MapTile.tileSelectionNone;
						else
							selection = MapTile.tileSelectionForbidden;
						
						gameState.mapConfig.getTile(x, y).setSelectionImage(selection);						
					}

				// Register mouse up handler
				Application.application.addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
			}
		}
		
		public override function onMouseReleased(tile : MapTile) : void
		{
			if (selectedUnit == null)
				return;

			if (tile != null)
			{
				var unit : UnitState = gameState.getUnitOnTile(tile);
				if (unit == selectedUnit 
					&& unit.containedUnits.length > 0)
				{
					stopDrag();
					selectedUnit.graphics.moveToCurrentTile();
					Application.application.baseMenu.show(Application.application.mouseX, Application.application.mouseY, selectedUnit, onBaseMenuExit, onBaseMenuDeploy, false);
					return;
				}
				else if (unit != null 
					&& unit.canHold(selectedUnit))
				{
					// Request move
					gameState.request(new ActionTeleportUnit(selectedUnit, tile.locationX, tile.locationY));
					return;
				}
				else if (unit == null 
					&& gameState.canUnitBeTeleportedTo(selectedUnit, tile.locationX, tile.locationY))
				{
					// Request move
					gameState.request(new ActionTeleportUnit(selectedUnit, tile.locationX, tile.locationY));
					return;
				}
				else if (unit != selectedUnit)
				{
					HelpBalloon.queueCenterOnHexagonGrid(gameState.mapConfig, tile.locationX, tile.locationY, 
						"This unit cannot be dropped here. Units can only be dropped in your deployment area (tiles of your color), on terrain that supports the unit or in bases that support the unit.", true); 
				}
			}		
			
			selectedUnit.graphics.moveToCurrentTile();
			gameState.toState(new UIStateSettingUp());
		}
		
		// Called when exit button is clicked in base menu
		private function onBaseMenuExit() : void
		{			
			gameState.toState(new UIStateSettingUp());
		}
		
		// Called when unit is deployed from base menu
		private function onBaseMenuDeploy(unit : UnitState) : void
		{
			gameState.toState(new UIStateSettingUpDeployUnit(selectedUnit, unit));
		}

		// Stop dragging
		private function stopDrag() : void
		{
			if (selectedUnit != null)
				selectedUnit.graphics.doStopDrag();
			
			Application.application.removeEventListener(MouseEvent.MOUSE_UP, onMouseUp);
		}

		// Callback to make sure that we stop dragging when the mouse goes up
		private function onMouseUp(event : MouseEvent) : void
		{
			stopDrag();
			
			selectedUnit.graphics.moveToCurrentTile();
			selectedUnit = null;
		}
	}
}

package onlinefrontlines.game.uistates
{
	import mx.core.Application;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * Unit selected, allow user to determine next action
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
	public class UIStateUnitSelected extends UIState
	{
		private var selectedUnit : UnitState;
		private var movementPoints : Array; // of Array of int
		private var attackPossibilities : Array; // of Array of int
		private var movementArrow : MovementArrow;
		private var enterBaseConfirmX : int;
		private var enterBaseConfirmY : int;

		// Constructor
		public function UIStateUnitSelected(selectedUnit : UnitState) : void
		{
			this.selectedUnit = selectedUnit;
			
			enterBaseConfirmX = -1;
			enterBaseConfirmY = -1;
		}

		// Called when entering this state
		public override function onEnterState() : void
		{
			// Go to waiting state if it is not our turn
			if (!gameState.canPerformAction())
			{
				gameState.toState(null);
				return;
			}

			// Go to selecting state if current selected unit cannot do anything
			if (!gameState.canUnitPerformAction(selectedUnit))
			{
				gameState.toState(new UIStateSelectingUnit());
				return;
			}
			
			// Calculate movement range for unit
			movementPoints = gameState.calculateMovementArea(selectedUnit);
			
			// Calculate attackable units
			attackPossibilities = gameState.calculateAttackPossibilities(selectedUnit, movementPoints);
			
			var s : int;
			
			// Update tile selection state
			for (var x : int = 0; x < gameState.mapConfig.sizeX; ++x)
				for (var y : int = 0; y < gameState.mapConfig.sizeY; ++y)
				{
					s = MapTile.tileSelectionNone;					
					var unit : UnitState = gameState.getUnit(x, y);
					if (attackPossibilities[x][y] == GameState.attackPossible)
					{
						unit.graphics.setTextColor(BitmapText.colorRed);
						s = MapTile.tileSelectionAttack;
					}
					else if (attackPossibilities[x][y] == GameState.attackPossibleAfterMove)
					{
						unit.graphics.setTextColor(BitmapText.colorRed);
					}
					else if (movementPoints[x][y] >= 0)
					{
						if (unit != null)
						{
							if (unit.canHold(selectedUnit))
								s = MapTile.tileSelectionEnterBase;
						}
						else
							s = MapTile.tileSelectionMove;
					}					
					gameState.mapConfig.getTile(x, y).setSelectionImage(s);
				}				
			
			// Select unit
			if (selectedUnit.containedUnits.length > 0)	
				s = MapTile.tileSelectionMenu;
			else if (gameState.canUnitBeTransformed(selectedUnit))
				switch (selectedUnit.unitConfig.unitClass)
				{ 
				case UnitClass.air:
				case UnitClass.water:
					s = MapTile.tileSelectionLand;
					break;
					
				case UnitClass.land:
					s = MapTile.tileSelectionBoat;
					break;
				}
			else
				s = MapTile.tileSelectionSelected;
			gameState.mapConfig.getTile(selectedUnit.locationX, selectedUnit.locationY).setSelectionImage(s);
		}

		// Called when leaving this state
		public override function onLeaveState() : void
		{
			if (movementArrow != null)	
			{
				movementArrow.destroy();
				movementArrow = null;
			}

			gameState.clearUnitTexts(true);
			gameState.clearTileSelection();
		}
		
		// Called when mouse starts hovering over a tile
		public override function onMouseRollOver(tile : MapTile) : void
		{
			// Skip when over a unit that we cannot enter
			var unit : UnitState = gameState.getUnitOnTile(tile);
			if (unit != null && !unit.canHold(selectedUnit))
				return;
			
			// Skip if we cannot move here
			if (movementPoints[tile.locationX][tile.locationY] < 0)
				return;

			// Create arrow				
			movementArrow = new MovementArrow(getPath(tile.locationX, tile.locationY), Application.application.mapConfig);
		}
		
		// Called when mouse stops hovering over a tile
		public override function onMouseRollOut(tile : MapTile) : void
		{
			if (movementArrow != null)	
			{
				movementArrow.destroy();
				movementArrow = null;
			}
		}
		
		// Called when mouse is pressed over a tile
		public override function onMousePressed(tile : MapTile) : void
		{
			var unit : UnitState = gameState.getUnitOnTile(tile);
			if (unit != null)
			{
				if (unit == selectedUnit 
					&& selectedUnit.containedUnits.length > 0)
					Application.application.baseMenu.show(Application.application.mouseX, Application.application.mouseY, selectedUnit, onBaseMenuExit, onBaseMenuDeploy, true);
				if (unit == selectedUnit 
					&& gameState.canUnitBeTransformed(selectedUnit))
				{
					var otherConfig : UnitConfig = UnitConfig.allUnitsMap[unit.unitConfig.transformableToUnitId];
					switch (selectedUnit.unitConfig.transformableType)
					{
					case TransformableType.moveOneTile:
						if (!otherConfig.canMoveOn(gameState.getTerrain(unit)))
						{
							// To transformation state
							gameState.toState(new UIStateUnitTransform(selectedUnit));
							break;
						}
						
						// Note: Same code as for case TransformableType.onSpot
						// A fallthrough construction could have been used here, but this seems
						// to screw up Flash Player 9,0,115,0 Release which stops the program from running
						// on entering this function. The Debug Flash Player works fine b.t.w.
						gameState.request(new ActionTransformUnit(selectedUnit, selectedUnit.locationX, selectedUnit.locationY));
						break;						

					case TransformableType.onSpot:
						// Transform unit
						gameState.request(new ActionTransformUnit(selectedUnit, selectedUnit.locationX, selectedUnit.locationY));
						break;						
					}
				}
				else if (selectedUnit.canAttack(unit))
				{
					// Attack unit
					gameState.request(new ActionAttackUnit(selectedUnit, unit));
				}
				else if (movementPoints[tile.locationX][tile.locationY] >= 0 
						&& unit.canHold(selectedUnit))
				{
					if (enterBaseConfirmX == tile.locationX 
						&& enterBaseConfirmY == tile.locationY)
					{
						// Get path
						var moveInContainerPath : Array = getPath(tile.locationX, tile.locationY);
						
						// Move unit in container
						gameState.request(new ActionMoveUnit(selectedUnit, moveInContainerPath));
					}
					else
					{
						// Reset previous confirm state
						if (enterBaseConfirmX != -1 && enterBaseConfirmY != -1)
							gameState.mapConfig.getTile(enterBaseConfirmX, enterBaseConfirmY).setSelectionImage(MapTile.tileSelectionEnterBase);

						// Enter confirm state
						gameState.mapConfig.getTile(tile.locationX, tile.locationY).setSelectionImage(MapTile.tileSelectionEnterBaseConfirm);
						enterBaseConfirmX = tile.locationX;
						enterBaseConfirmY = tile.locationY;
					}
				}
				else 
				{
					// Treat this as a normal unit select action
					gameState.toState(new UIStateSelectingUnit());
					gameState.tilePressed(tile);
				}
			}
			else
			{
				if (movementPoints[tile.locationX][tile.locationY] >= 0)
				{
					// Get path
					var moveToPath : Array = getPath(tile.locationX, tile.locationY);
					
					// Move to location
					gameState.request(new ActionMoveUnit(selectedUnit, moveToPath));
				}
				else
				{
					// Go back to selecting state
					gameState.toState(new UIStateSelectingUnit());
				}
			}
		}
		
		// Called when exit button is clicked in base menu
		private function onBaseMenuExit() : void
		{
			gameState.toState(new UIStateSelectingUnit());
		}
		
		// Called when unit is deployed from base menu
		private function onBaseMenuDeploy(unit : UnitState) : void
		{
			gameState.toState(new UIStateDeployUnit(selectedUnit, unit));
		}
		
		// Get shortest path from selectedUnit to x, y, returns array of objects { x : ?, y : ? }
		private function getPath(x : int, y : int) : Array
		{
			// Start path with end point
			var path : Array = new Array();
			path.push({ x : x, y : y });

			// Loop from end path to unit location
			var highestPoints : int = movementPoints[x][y];			
			while (selectedUnit.locationX != x || selectedUnit.locationY != y)
			{			
				// Get neighbour with highest movement points left
				var bestLocation : Object;
				for each (var n : Object in gameState.mapConfig.getNeighbours(x, y))
				{
					var p : int = movementPoints[n.x][n.y];
					if (p > highestPoints)
					{
						highestPoints = p;
						bestLocation = n;
					}
				}
					
				// This should be closer to the starting point
				x = bestLocation.x;
				y = bestLocation.y;
				
				// Add to path
				path.push(bestLocation);
			}
			
			// Reverse the path so it is from begin to end
			path.reverse();			
			return path;
		}
	}
}

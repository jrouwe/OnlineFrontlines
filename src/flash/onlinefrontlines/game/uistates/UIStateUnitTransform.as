package onlinefrontlines.game.uistates
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * Allow user to transform unit (i.e. marine to lander)
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
	public class UIStateUnitTransform extends UIState
	{
		private var selectedUnit : UnitState;
		
		public function UIStateUnitTransform(selectedUnit : UnitState) : void
		{
			this.selectedUnit = selectedUnit;
		}

		public override function onEnterState() : void
		{
			// Mark unit location
			var s : int = MapTile.tileSelectionNone;								
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
			gameState.mapConfig.getTile(selectedUnit.locationX, selectedUnit.locationY).setSelectionImage(s);
			
			// Mark tiles that unit can be deployed on
			var otherConfig : UnitConfig = UnitConfig.allUnitsMap[selectedUnit.unitConfig.transformableToUnitId];					
			for each (var l : Object in gameState.mapConfig.getNeighbours(selectedUnit.locationX, selectedUnit.locationY))
				if (gameState.getUnit(l.x, l.y) == null
					&& gameState.mapConfig.isTileInPlayableArea(l.x, l.y)
					&& otherConfig.canMoveOn(gameState.getTerrainAt(l.x, l.y)))
					gameState.mapConfig.getTile(l.x, l.y).setSelectionImage(MapTile.tileSelectionMove);					
		}
		
		public override function onLeaveState() : void
		{
			selectedUnit.graphics.updateGraphics();
			
			gameState.clearTileSelection();
		}
		
		public override function onMousePressed(tile : MapTile) : void
		{
			// Check if valid tile clicked
			var otherConfig : UnitConfig = UnitConfig.allUnitsMap[selectedUnit.unitConfig.transformableToUnitId];					
			if (gameState.getUnitOnTile(tile) == null
				&& selectedUnit.getDistanceTo(tile.locationX, tile.locationY) == 1
				&& gameState.mapConfig.isTileInPlayableArea(tile.locationX, tile.locationY)
				&& otherConfig.canMoveOn(gameState.getTerrainAt(tile.locationX, tile.locationY)))
			{
				// Transform to location
				gameState.request(new ActionTransformUnit(selectedUnit, tile.locationX, tile.locationY));
			}
			else
			{
				// Treat as normal selection click
				gameState.toState(new UIStateSelectingUnit());
				gameState.tilePressed(tile);
			}
		}
	}
}

package onlinefrontlines.game.uistates
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * Unit moving animations
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
	public class UIStateUnitMoving extends UIState
	{
		private var selectedUnit : UnitState;
		private var fromContainer : UnitState;
		private var path : Array;
		
		private var unitMovementHelper : UnitMovementHelper;
		
		public function UIStateUnitMoving(selectedUnit : UnitState, fromContainer : UnitState, path : Array)
		{
			this.selectedUnit = selectedUnit;
			this.fromContainer = fromContainer;
			this.path = path;
		}

		// If this state is playing an animation
		public override function isPlaying() : Boolean
		{ 
			return true;
		}

		public override function onEnterState() : void
		{
			selectedUnit.graphics.updateGraphics();		
			if (fromContainer != null)
				fromContainer.graphics.updateGraphics();			

			unitMovementHelper = new UnitMovementHelper(selectedUnit, fromContainer, path, gameState.mapConfig);
			unitMovementHelper.start();				
		}
		
		public override function onLeaveState() : void
		{
			unitMovementHelper.stop();
			
			if (selectedUnit.container != null)
				selectedUnit.container.graphics.updateGraphics();
		}
		
		public override function onUpdate() : void
		{
			unitMovementHelper.update();
			
			if (unitMovementHelper.hasFinished)
			{
				if (fromContainer == null)
					gameState.toState(new UIStateUnitSelected(selectedUnit));
				else
					gameState.toState(new UIStateSelectingUnit());
			}
		}

		public override function onMousePressed(tile : MapTile) : void
		{
			var unit : UnitState = gameState.getUnitOnTile(tile);
			if (unit != null 
				&& unit.faction == gameState.currentPlayer
				&& gameState.canUnitPerformAction(unit))
			{
				// Treat as normal selection click
				gameState.toState(new UIStateSelectingUnit());
				gameState.tilePressed(tile);
			}
		}
	}
}

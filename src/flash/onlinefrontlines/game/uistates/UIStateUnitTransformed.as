package onlinefrontlines.game.uistates
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * After unit is transformed
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
	public class UIStateUnitTransformed extends UIState
	{
		// Sounds
		[Embed(source="../../../assets/sounds/unit_transformed.mp3")]
		private static var unitTransformedClass : Class;

		private var selectedUnit : UnitState;
		private var path : Array;

		private var unitMovementHelper : UnitMovementHelper;

		public function UIStateUnitTransformed(selectedUnit : UnitState, path : Array) : void
		{
			this.selectedUnit = selectedUnit;
			this.path = path;
		}

		// If this state is playing an animation
		public override function isPlaying() : Boolean
		{ 
			return true;
		}

		public override function onEnterState() : void
		{
			// Play sound
			SoundSystem.play(unitTransformedClass);
			
			// Create movement helper
			unitMovementHelper = new UnitMovementHelper(selectedUnit, null, path, gameState.mapConfig);
			unitMovementHelper.start();

			// Check if there will be any movement
			if (unitMovementHelper.hasFinished)
				gameState.toState(new UIStateUnitSelected(selectedUnit));
		}
		
		public override function onLeaveState() : void
		{
			// Stop movement
			unitMovementHelper.stop();
			
			// Update graphics
			selectedUnit.graphics.updateGraphics();
		}		
		
		public override function onUpdate() : void
		{
			// Update helper
			unitMovementHelper.update();
			
			// Switch state if movement done
			if (unitMovementHelper.hasFinished)
				gameState.toState(new UIStateUnitSelected(selectedUnit));
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

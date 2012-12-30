package onlinefrontlines.game.uistates
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.actions.*;
	
	/*
	 * Allow user to select unit
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
	public class UIStateSelectingUnit extends UIState
	{
		// Sounds
		[Embed(source="../../../assets/sounds/unit_selected.mp3")]
		private static var unitSelectedClass : Class;

		public override function onEnterState() : void
		{
			// Go to waiting state if it is not our turn
			if (!gameState.canPerformAction())
			{
				gameState.toState(null);
				return;
			}
		}

		public override function onMousePressed(tile : MapTile) : void
		{
			var unit : UnitState = gameState.getUnitOnTile(tile);
			if (unit != null 
				&& unit.faction == gameState.currentPlayer)
			{
				if (gameState.canUnitPerformAction(unit))
				{
					// Play sound
					SoundSystem.play(unitSelectedClass);
					
					// Switch state
					gameState.toState(new UIStateUnitSelected(unit));
				}
				else
				{
					HelpBalloon.queueCenterOnHexagonGrid(gameState.mapConfig, tile.locationX, tile.locationY, 
						"This unit cannot currently perform any action. The number above this unit is gray to indicate this.", true); 
				}
			}
		}
	}
}

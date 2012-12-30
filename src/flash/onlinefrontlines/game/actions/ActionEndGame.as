package onlinefrontlines.game.actions
{
	import mx.core.Application;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Ends the game
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
	public class ActionEndGame extends Action 
	{
		// Sounds
		[Embed(source="../../../assets/sounds/end_game.mp3")]
		private static var endGameClass : Class;

		private var faction : int;
		
		// Apply the action
		public override function doAction() : void
		{
			// End game
			gameState.setWinningFaction(faction);
			
			// Play sound
			SoundSystem.play(endGameClass);
			
			// Show message
			if (faction != Faction.none)
				Application.application.slideBox.show(gameState.getPlayer(faction).name + " wins!");

			// Show message
			Application.application.victoryWindow.showVictory(gameState);

			// Switch state
			gameState.toState(null);
		}
		
		// Undo the action
		public override function undoAction() : void
		{
			gameState.toState(new UIStateSelectingUnit());
		}
		
		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			faction = int(param[1]);
		}

		// Convert action to a string
		public override function toString() : String
		{
			return "g," + faction;
		}
	}
}

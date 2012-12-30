package onlinefrontlines.game.actions
{
	import mx.core.Application;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Player surrendered
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
	public class ActionSurrender extends Action 
	{
		// Sounds
		[Embed(source="../../../assets/sounds/surrender.mp3")]
		private static var surrenderClass : Class;

		// Faction that surrendered
		private var faction : int;
	
		// Constructor	
		public function ActionSurrender(faction : int) : void
		{
			this.faction = faction;
		}
	
		// Apply the action
		public override function doAction() : void
		{
			// End game
			gameState.setWinningFaction(Faction.opposite(faction));

			// Play sound
			SoundSystem.play(surrenderClass);

			// Show message box
			Application.application.slideBox.show(gameState.getPlayer(faction).name + " has surrendered!");

			// Show message
			Application.application.victoryWindow.showVictory(gameState);

			// Switch state
			gameState.toState(null);
		}
		
		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			faction = int(param[1]);
		}
	
		// Convert action to a string
		public override function toString() : String
		{
			return "s," + faction;
		}
	}
}

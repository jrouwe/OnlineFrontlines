package onlinefrontlines.game.actions
{
	import mx.core.Application;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Indicate a new player joined
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
	public class ActionPlayerJoin extends Action 
	{
		// Sounds
		[Embed(source="../../../assets/sounds/player_joined.mp3")]
		private static var playerJoinedClass : Class;

		private var faction : int;
		private var id : int;
		private var name : String;
		
		// Check if the function is undoable or not
		public override function isUndoable() : Boolean
		{
			return false;
		}

		// Apply the action
		public override function doAction() : void
		{
			if (gameState.localPlayer != faction)
			{
				// Play sound
				SoundSystem.play(playerJoinedClass);
				
				// Show message
				Application.application.slideBox.show(name + " has joined!");
			}
			
			gameState.reJoinGame(faction, new Player(id, name));

			if (faction == Faction.f1)
				Application.application.playerName1.text = name;
			else
				Application.application.playerName2.text = name;
		}
		
		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			faction = int(param[1]);
			id = int(param[2]);
			name = unescape(param[3]);
		}

		// Convert action to a string
		public override function toString() : String
		{
			return "j," 
				+ faction + "," 
				+ id;
		}
	}
}

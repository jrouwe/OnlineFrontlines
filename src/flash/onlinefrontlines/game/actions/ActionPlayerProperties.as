package onlinefrontlines.game.actions
{
	import mx.core.Application;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Set properties for a player
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
	public class ActionPlayerProperties extends Action 
	{
		private var faction : int;
		private var totalPoints : int;
		private var level : int;
		private var pointsNeededToNextLevel : int;
		private var avatarURL : String;
		
		// Check if the function is undoable or not
		public override function isUndoable() : Boolean
		{
			return false;
		}

		// Apply the action
		public override function doAction() : void
		{
			var player : Player = gameState.getPlayer(faction);
			
			player.totalPoints = totalPoints;
			player.level = level;
			player.pointsNeededToNextLevel = pointsNeededToNextLevel;
			player.avatarURL = avatarURL;
		}
		
		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			faction = int(param[1]);
			totalPoints = int(param[2]);
			level = int(param[3]);
			pointsNeededToNextLevel = int(param[4]);
			avatarURL = param[5];
		}

		// Convert action to a string
		public override function toString() : String
		{
			return "p," 
				+ faction + ","
				+ totalPoints + ","
				+ level + ","
				+ pointsNeededToNextLevel + ","
				+ avatarURL;
		}
	}
}

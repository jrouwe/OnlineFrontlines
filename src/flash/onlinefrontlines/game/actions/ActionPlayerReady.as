package onlinefrontlines.game.actions
{
	import mx.core.Application;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Indicate player has finished deployment
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
	public class ActionPlayerReady extends Action 
	{
		// Sounds
		[Embed(source="../../../assets/sounds/player_ready.mp3")]
		private static var playerReadyClass : Class;

		// User that is ready
		private var userId : int;
	
		// Only notify once
		private var didNotification : Boolean = false;

		// Constructor	
		public function ActionPlayerReady(userId : int) : void
		{
			this.userId = userId;
		}
	
		// Apply the action
		public override function doAction() : void
		{
			// Set flag
			if (gameState.getPlayerId(Faction.f1) == userId)
				gameState.playerReady[0] = true;
			else if (gameState.getPlayerId(Faction.f2) == userId)
				gameState.playerReady[1] = true;
				
			// Play sound
			SoundSystem.play(playerReadyClass);

			// Show notifications
			if (!didNotification)
			{
				if (gameState.playByMail && gameState.getPlayerId(gameState.localPlayer) == userId && (!gameState.playerReady[0] || !gameState.playerReady[1]))
					Application.application.slideBox.show("An e-mail notification has been sent!");
				didNotification = true;
			}

			// Only switch state if it is our own action
			if (userId == gameState.getLocalPlayerId())
				gameState.toState(new UIStateSettingUp());
		}
		
		// Check if the function is undoable or not
		public override function isUndoable() : Boolean
		{
			return false;
		}

		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			userId = int(param[1]);
		}
	
		// Convert action to a string
		public override function toString() : String
		{
			return "r," + userId;
		}
	}
}

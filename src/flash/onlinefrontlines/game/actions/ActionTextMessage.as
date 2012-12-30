package onlinefrontlines.game.actions
{
	import mx.core.Application;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * Chat message received
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
	public class ActionTextMessage extends Action 
	{
		// Sounds
		[Embed(source="../../../assets/sounds/chat_sent.mp3")]
		private static var chatSentClass : Class;
		[Embed(source="../../../assets/sounds/chat_received.mp3")]
		private static var chatReceivedClass : Class;

		private var userId : int;
		private var message : String;
		
		// Constructor
		public function ActionTextMessage(userId : int, message : String) : void
		{
			this.userId = userId;
			this.message = message;
		}

		// When requesting this action, set uistate to null until request serviced?
		public override function resetStateAfterRequest() : Boolean
		{
			return false;
		}

		// Check if this function can be performed any time or if it must be executed in the order that actions are received
		public override function canBeExecutedAnyTime() : Boolean
		{
			return true;
		}

		// Check if the function is undoable or not
		public override function isUndoable() : Boolean
		{
			return false;
		}

		// Apply the action
		public override function doAction() : void
		{
			// Play sound
			if (userId == gameState.getLocalPlayerId())
				SoundSystem.play(chatSentClass);
			else
				SoundSystem.play(chatReceivedClass);
				
			// Show message
			Application.application.chatWindow.addText(message);
		}
		
		// Convert action from a string
		public override function fromString(param : Array) : void
		{
			userId = int(param[1]);
			message = unescape(param[2]);
		}
		
		/**
		 * Convert action to a string
		 */
		public override function toString() : String
		{
			return "x," + userId + "," + escape(message);
		}
	}
}

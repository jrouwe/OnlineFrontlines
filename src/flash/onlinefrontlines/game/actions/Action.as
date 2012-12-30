package onlinefrontlines.game.actions
{
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	
	/*
	 * Base class game action
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
	public class Action 
	{
		protected var gameState : GameState;
		
		// Set game state
		public function setGameState(gameState : GameState) : void
		{
			this.gameState = gameState;
		}
		
		// When requesting this action, set uistate to null until request serviced?
		public function resetStateAfterRequest() : Boolean
		{
			return true;
		}
		
		// When undoing / redoing actions stop at this action
		public function undoRedoStop() : Boolean
		{
			return true;
		}

		// Apply the action
		public function doAction() : void
		{
		}
		
		// Check if this function can be performed any time or if it must be executed in the order that actions are received
		public function canBeExecutedAnyTime() : Boolean
		{
			return false;
		}
		
		// Check if the function is undoable or not
		public function isUndoable() : Boolean
		{
			return true;
		}
		
		// Undo the action
		public function undoAction() : void
		{
		}	
		
		// Convert to string
		public function toString() : String
		{
			return null;
		}

		// Convert from string (string is passed split on ',')
		public function fromString(param : Array) : void
		{
		}
		
		// Factory function to create action from action string
		public static function createAction(identifier : String) : Action 
		{
			var action : Action = null;
			if (identifier == "c")
				action = new ActionCreateUnit();
			else if (identifier == "l")
				action = new ActionTeleportUnit(null, 0, 0);
			else if (identifier == "m")
				action = new ActionMoveUnit(null, null);
			else if (identifier == "a")
				action = new ActionAttackUnit(null, null);
			else if (identifier == "t")
				action = new ActionTransformUnit(null, 0, 0);
			else if (identifier == "e")
				action = new ActionEndTurn();
			else if (identifier == "g")
				action = new ActionEndGame();
			else if (identifier == "x")
				action = new ActionTextMessage(-1, null);
			else if (identifier == "j")
				action = new ActionPlayerJoin();
			else if (identifier == "r")
				action = new ActionPlayerReady(-1);
			else if (identifier == "u")
				action = new ActionRemoveUnit();
			else if (identifier == "i")
				action = new ActionIdentifyUnit();
			else if (identifier == "s")
				action = new ActionSurrender(-1);
			else if (identifier == "d")
				action = new ActionRequestDraw(-1);
			else if (identifier == "o")
				action = new ActionTimeOut();
			else if (identifier == "p")
				action = new ActionPlayerProperties();
			else if (identifier == "at")
				action = new ActionAnnotateTiles();
			return action;
		}
	}
}

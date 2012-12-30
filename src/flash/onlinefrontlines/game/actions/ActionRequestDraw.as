package onlinefrontlines.game.actions
{
	import mx.core.Application;
	import onlinefrontlines.utils.*;
	import onlinefrontlines.game.*;
	import onlinefrontlines.game.uistates.*;
	
	/*
	 * One player requests a draw
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
	public class ActionRequestDraw extends Action 
	{
		// Faction that requested draw
		private var faction : int;
	
		// Constructor	
		public function ActionRequestDraw(faction : int) : void
		{
			this.faction = faction;
		}
	
		// Check if the function is undoable or not
		public override function isUndoable() : Boolean
		{
			return false;
		}

		// Apply the action
		public override function doAction() : void
		{
			// Set flag
			gameState.drawRequested[faction - 1] = true;
			
			// Go to selecting state
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
			return "d," + faction;
		}
	}
}

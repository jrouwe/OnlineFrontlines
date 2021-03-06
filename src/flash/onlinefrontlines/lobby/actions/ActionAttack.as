package onlinefrontlines.lobby.actions
{
	import onlinefrontlines.utils.*;
	
	/*
	 * Action to attack a country
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
	public class ActionAttack extends Action 
	{
		private var defendLocationX : int;
		private var defendLocationY : int;
		private var attackLocationX : int;
		private var attackLocationY : int;
		
		// Constructor
		public function ActionAttack(defendLocationX : int, defendLocationY : int, attackLocationX : int, attackLocationY : int)
		{
			this.defendLocationX = defendLocationX;
			this.defendLocationY = defendLocationY;
			this.attackLocationX = attackLocationX;
			this.attackLocationY = attackLocationY;
		}
		
		// Convert to string
		public override function toString() : String
		{
			return "a," + defendLocationX + "," + defendLocationY + "," + attackLocationX + "," + attackLocationY;
		}
	}
}

package onlinefrontlines.game
{
	import onlinefrontlines.utils.*;

	/*
	 * Unit faction
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
	public class Faction
	{
		public static var invalid : int = 0;
		public static var f1 : int = 1;
		public static var f2 : int = 2;
		public static var none : int = 3;
		
		// This determines which army faction 1 is
		public static var faction1IsRed : Boolean = true; 
		
		// Get opposite faction
		public static function opposite(faction : int) : int
		{
			switch (faction)
			{
			case f1:
				return f2;
			case f2:
				return f1;
			default:
				return none;
			}
		}		

		/**
		 * Determine army that corresponds to a faction
		 */
		public static function toArmy(faction : int) : int
		{
			switch (faction)
			{
			case f1:
				return faction1IsRed? Army.red : Army.blue;
			case f2:
				return faction1IsRed? Army.blue : Army.red;
			default:
				return Army.none;
			}
		}			
	}
}
package onlinefrontlines.utils
{
	/*
	 * Army enum
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
	public class Army
	{
		// Enum values		
		public static var none : int = -1;
		public static var red : int = 0;
		public static var blue : int = 1;
		
		// Small icons
		[Embed(source='../../../../web/assets/user_icons/red.png')]
		private static var redClass : Class;
		[Embed(source='../../../../web/assets/user_icons/blue.png')]
		private static var blueClass : Class;

		// Get opposite army
		public static function opposite(army : int) : int
		{
			switch (army)
			{
			case red:
				return blue;
			case blue:
				return red;
			default:
				return none;
			}
		}		
		
		/**
		 * Get small icon for army
		 */
		public static function getSmallIcon(army : int) : Class
		{
			switch (army)
			{
			case red:
				return redClass;
			case blue:
				return blueClass;
			default:
				return null;
			}			
		}
	}
}
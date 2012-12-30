package onlinefrontlines.game;

/**
 * Class of a unit
 * 
 * @author jorrit
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
public enum UnitClass
{
	land,
	water,
	air,
	none;

	/**
	 * Convert integer to UnitClass
	 * 
	 * @param intValue Integer value
	 * @return Enum value
	 */
	public static UnitClass fromInt(int intValue)
	{
		switch (intValue)
		{
		case 1: return land;
		case 2: return water;
		case 3: return air;
		case 4: return none;
		
		default: return none;
		}
	}
	
	/**
	 * Convert UnitClass to integer
	 * 
	 * @param unitClass Enum value
	 * @return Integer value
	 */
	public static int toInt(UnitClass unitClass)
	{
		switch (unitClass)
		{
		case land: return 1;
		case water: return 2;
		case air: return 3;
		case none: return 4;
		
		default: return 4;
		}
	}
}

package onlinefrontlines.game;

/**
 * Faction
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
public enum Faction
{
	invalid,
	f1,
	f2,
	none;
	
	/**
	 * Returns the opposite faction from incoming faction
	 * 
	 * @param faction Incoming faction
	 * @return Opposite faction
	 */
	public static Faction opposite(Faction faction)
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
	 * Convert faction to integer value
	 * 
	 * @param faction Faction to convert
	 * @return Integer value
	 */
	public static int toInt(Faction faction)
	{
		switch (faction)
		{
		case f1:
			return 1;
		case f2:
			return 2;
		case none:
			return 3;
		default:
			return 0;
		}
	}
	
	/**
	 * Convert integer to faction
	 * 
	 * @param value Integer value
	 * @return Faction
	 */
	public static Faction fromInt(int value)
	{
		switch (value)
		{
		case 1:
			return f1;
		case 2:
			return f2;
		case 3:
			return none;
		default:
			return invalid;
		}
	}
}

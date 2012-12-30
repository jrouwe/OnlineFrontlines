package onlinefrontlines;

/**
 * Army
 * 
 * @author jrouwe
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
public enum Army
{
	none,
	red,
	blue;
	
	/**
	 * Returns the opposite army
	 * 
	 * @param army Incoming army
	 * @return Opposite army
	 */
	public static Army opposite(Army army)
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
	 * Convert army to integer value
	 * 
	 * @param army Army to convert
	 * @return Integer value
	 */
	public static int toInt(Army army)
	{
		switch (army)
		{
		case red:
			return 0;
		case blue:
			return 1;
		default:
			return -1;
		}
	}
	
	/**
	 * Convert integer to army
	 * 
	 * @param value Integer value
	 * @return Army
	 */
	public static Army fromInt(int value)
	{
		switch (value)
		{
		case 0:
			return red;
		case 1:
			return blue;
		default:
			return none;
		}
	}

	/**
	 * Parse string and return army
	 */
	public static Army fromString(String inValue)
	{
		if (inValue != null)
		{
			if (inValue.equals("red"))
				return red;
			else if (inValue.equals("blue"))
				return blue;
			else if (inValue.equals("none"))
				return none;
		}
		
		assert(false);
		return none;
	}
}

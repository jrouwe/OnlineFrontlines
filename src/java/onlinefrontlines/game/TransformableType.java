package onlinefrontlines.game;

/**
 * Indicates if and where a unit can transform to another unit
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
public enum TransformableType
{
	none,
	inBase,
	onSpot,
	moveOneTile;
	
	/**
	 * Convert type to integer value
	 * 
	 * @param type Type 
	 * @return Integer value
	 */
	public static int toInt(TransformableType type)
	{
		switch (type)
		{
		case inBase:
			return 1;
		case onSpot:
			return 2;
		case moveOneTile:
			return 3;
		default:
			return 0;
		}
	}
	
	/**
	 * Convert integer to type
	 * 
	 * @param value Integer value
	 * @return Type
	 */
	public static TransformableType fromInt(int value)
	{
		switch (value)
		{
		case 1:
			return inBase;
		case 2:
			return onSpot;
		case 3:
			return moveOneTile;
		default:
			return none;
		}
	}
}

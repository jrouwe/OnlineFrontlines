package onlinefrontlines.game;

/**
 * Contains strength modifiers when attacking a specific unit class
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
public final class UnitStrengthProperties 
{
	/**
	 * Unit class of unit to attack
	 */
	public UnitClass enemyUnitClass;
	
	/**
	 * Strength when full armour and when having ammo
	 */
	public int maxStrengthWithAmmo;

	/**
	 * Strength when full armour and when not having ammo
	 */
	public int maxStrengthWithoutAmmo;
	
	/**
	 * Amount of tiles the unit can attack
	 */
	public int attackRange;
	
	/**
	 * Constructor
	 */
	public UnitStrengthProperties(UnitClass enemyUnitClass, int maxStrengthWithAmmo, int maxStrengthWithoutAmmo, int attackRange)
	{
		this.enemyUnitClass = enemyUnitClass;
		this.maxStrengthWithAmmo = maxStrengthWithAmmo;
		this.maxStrengthWithoutAmmo = maxStrengthWithoutAmmo;
		this.attackRange = attackRange;
	}

	/**
	 * Unit class of unit to attack as integer value
	 */
	public int getEnemyUnitClassIntValue()
	{
		return UnitClass.toInt(enemyUnitClass);
	}
	
	/**
	 * Strength when full armour and when having ammo
	 */
	public int getMaxStrengthWithAmmo()
	{
		return maxStrengthWithAmmo;
	}
	
	/**
	 * Strength when full armour and when not having ammo
	 */
	public int getMaxStrengthWithoutAmmo()
	{
		return maxStrengthWithoutAmmo;
	}
		
	/**
	 * Amount of tiles the unit can attack
	 */
	public int getAttackRange()
	{
		return attackRange;
	}
	
	/**
	 * Get strength depending on if we have ammo or not
	 */
	public int getStrength(boolean hasAmmo)
	{
		return Math.max(hasAmmo? maxStrengthWithAmmo : maxStrengthWithoutAmmo, maxStrengthWithoutAmmo);
	}

	/**
	 * Get strength as string
	 */
	public String getStrengthString()
	{
		if (maxStrengthWithAmmo > 0)
			return maxStrengthWithAmmo + "/" + maxStrengthWithoutAmmo;
		else if (maxStrengthWithoutAmmo > 0)
			return Integer.toString(maxStrengthWithoutAmmo);
		else 
			return "X";
	}

	/**
	 * Convert to user readable string
	 */
	public String toString()
	{
		String range = attackRange > 1? " R" + attackRange : "";
		return getStrengthString() + range;		
	}
}

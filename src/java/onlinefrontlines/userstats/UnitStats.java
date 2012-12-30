package onlinefrontlines.userstats;

import java.text.DecimalFormat;

/**
 * Class that contains statistics per unit
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
public class UnitStats
{
	public int unitId;
	public String unitName;
	
	public int numAttacks = 0;
	public int numDefends = 0;
	public int kills = 0;
	public int deaths = 0;
	public int damageDealt = 0;
	public int damageReceived = 0;
	
	/**
	 * Unit name
	 */
	public String getUnitName()
	{
		return unitName;
	}
	
	/**
	 * Get number of attacks performed
	 */
	public int getNumAttacks()
	{
		return numAttacks;
	}
	
	/**
	 * Get number of times attacked
	 */
	public int getNumDefends()
	{
		return numDefends;
	}
	
	/**
	 * Number of times that enemy unit was killed
	 */
	public int getKills()
	{
		return kills;
	}
	
	/**
	 * Number of times friendly unit was killed
	 */
	public int getDeaths()
	{
		return deaths;
	}
	
	/**
	 * Total damage dealt by unit
	 */
	public int getDamageDealt()
	{
		return damageDealt;
	}
	
	/**
	 * Total damage sustained for this unit type
	 */
	public int getDamageReceived()
	{
		return damageReceived;
	}

	/**
	 * Get amount of attacks needed for a kill as a string
	 */
	public String getAttacksPerKillString()
	{
		// Guard against div by zero
		if (kills <= 0)
			return "-";

		// Convert to string
    	DecimalFormat f = new DecimalFormat("#.#");
    	return f.format((float)numAttacks / kills);
	}

	/**
	 * Get average damage per attack as a string
	 */
	public String getAverageDamageString()
	{
		// Guard against div by zero
		if (numAttacks <= 0)
			return "-";
		
		// Convert to string
    	DecimalFormat f = new DecimalFormat("#.#");
    	return f.format((float)damageDealt / numAttacks);
	}
};


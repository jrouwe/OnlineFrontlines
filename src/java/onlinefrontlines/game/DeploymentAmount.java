package onlinefrontlines.game;

/**
 * How many units of a particular type can be used in a game
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
public final class DeploymentAmount
{
	/**
	 * Type of the unit
	 */
	public int unitId;
	
	/**
	 * Amount of units available
	 */
	public int amount;

	/**
	 * Constructor
	 */
	public DeploymentAmount(int unitId, int amount)
	{
		this.unitId = unitId;
		this.amount = amount;
	}	
	
	/**
	 * Copy constructor
	 */
	public DeploymentAmount(DeploymentAmount other)
	{
		unitId = other.unitId;
		amount = other.amount;
	}
}

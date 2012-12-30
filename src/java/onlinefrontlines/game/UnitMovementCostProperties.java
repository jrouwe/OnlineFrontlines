package onlinefrontlines.game;

/**
 * Contains the cost in action points for moving a unit
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
public final class UnitMovementCostProperties 
{
	/**
	 * Other terrain type
	 */
	public int terrainId;
	
	/**
	 * Amount of action points this move costs
	 */
	public int movementCost;
	
	/**
	 * Constructor
	 */
	public UnitMovementCostProperties(int terrainId, int movementCost)
	{
		this.terrainId = terrainId;
		this.movementCost = movementCost;
	}
	
	/**
	 * Other terrain type
	 */
	public int getTerrainId()
	{
		return terrainId;
	}
	
	/**
	 * Amount of action points this move costs
	 */
	public int getMovementCost()
	{
		return movementCost;
	}
}

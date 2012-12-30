package onlinefrontlines.game;

import java.util.*;

/**
 * Amount and type of units that can be used in a game
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
public final class DeploymentConfig
{
	/**
	 * Id of the deployment configuration
	 */
	public int id = -1;
	
	/**
	 * Name of the deployment configuration
	 */
	public String name;
	
	/**
	 * User that created this config
	 */
	public int creatorUserId;

	/**
	 * List of units and their amounts
	 */
	public final ArrayList<DeploymentAmount> deploymentAmounts = new ArrayList<DeploymentAmount>();
	
	/**
	 * Constructor
	 */
	public DeploymentConfig()
	{		
	}
	
	/**
	 * Copy constructor
	 */
	public DeploymentConfig(DeploymentConfig other)
	{
		id = other.id;
		name = other.name;
		creatorUserId = other.creatorUserId;

		deploymentAmounts.ensureCapacity(other.deploymentAmounts.size());
		for (DeploymentAmount d : other.deploymentAmounts)
			deploymentAmounts.add(new DeploymentAmount(d));
	}
		
	/**
	 * Id of the deployment configuration
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Name of the deployment configuration
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * User that created this config
	 */
	public int getCreatorUserId()
	{
		return creatorUserId;
	}

	/**
	 * Get amount of units for particular unit id
	 * 
	 * @param unitId The unit id
	 * @return Amount of units needed
	 */
	public int getAmount(int unitId)
	{
		for (DeploymentAmount a : deploymentAmounts)
			if (a.unitId == unitId)
				return a.amount;
		
		return 0;
	}

	/**
	 * Set amount of units for particular unit id
	 * 
	 * @param unitId The unit id
	 * @param amount The amount of units of this type
	 */
	public void setAmount(int unitId, int amount)
	{
		for (DeploymentAmount a : deploymentAmounts)
			if (a.unitId == unitId)
			{
				if (amount != 0)
					a.amount = amount;
				else
					deploymentAmounts.remove(a);
				return;
			}
		
		// Insert new
		deploymentAmounts.add(new DeploymentAmount(unitId, amount));
	}
	
	/**
	 * Get total amount of units
	 */
	public int getTotalUnits()
	{
		int total = 0;
		for (DeploymentAmount a : deploymentAmounts)
			total += a.amount;
		return total;		
	}
	
	/**
	 * Get total amount of victory points in this deployment
	 * 
	 * @return Victory points
	 */
	public int getTotalVictoryPoints()
	{
		int total = 0;
		for (DeploymentAmount a : deploymentAmounts)
			total += a.amount * UnitConfig.allUnitsMap.get(a.unitId).victoryPoints;
		return total;		
	}
}

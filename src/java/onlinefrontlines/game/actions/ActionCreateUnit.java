package onlinefrontlines.game.actions;

import onlinefrontlines.auth.User;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.IllegalRequestException;

/**
 * Action that is sent by the server only to create a new unit.
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
public class ActionCreateUnit extends Action
{
	private int id;
	private int locationX;
	private int locationY;
	private Faction faction;
	private int configId;
	private int containerId;
	private boolean reCreateUnit;
	
	private UnitState unit;
	
	private int oldStateLocationX;
	private int oldStateLocationY;
	private Faction oldStateFaction;
	private UnitState oldStateContainer;
		
	/**
	 * Constructor
	 */
	public ActionCreateUnit()
	{
	}
	
	/**
	 * Constructor
	 */
	public ActionCreateUnit(int id, int locationX, int locationY, int containerId, Faction faction, int configId, boolean reCreateUnit)
	{
		this.id = id;
		this.locationX = locationX;
		this.locationY = locationY;
		this.containerId = containerId;
		this.faction = faction;
		this.configId = configId;
		this.reCreateUnit = reCreateUnit;
	}
	
	/**
	 * Get unit id of created unit
	 */
	public int getUnitId()
	{
		return id;
	}
	
	/**
	 * Query if remote client is allowed to send this action
	 */
	public boolean remoteHasPermissionToSend()
	{
		return false;
	}
	
	/**
	 * Update call for actions that are in the pending list
	 * Called before each executed action to get state from before the action
	 */
	public void pendingActionUpdate()
	{
		oldStateFaction = unit.faction;
		
		oldStateLocationX = unit.locationX;
		oldStateLocationY = unit.locationY;
		oldStateContainer = unit.container;
	}

	/**
	 * Query if remote client should receive this action
	 */
	public receiveTime pendingActionGetReceiveTime(Faction remoteFaction)
	{
		// Everyone but the enemy can see this unit
		if (Faction.opposite(unit.faction) != remoteFaction)
			return reCreateUnit? receiveTime.never : receiveTime.now;
		
		// During setup phase don't create unit
		if (gameState.turnNumber == 0)
			return receiveTime.later;
		
		// If unit has been marked 'detected' send the creation now
		if (unit.isDetected())
			return receiveTime.now;

		// Receive the creation later
		return receiveTime.later;
	}
	
	/**
	 * Pending actions are sorted so that the client receives them in the correct order
	 */
	public int pendingActionGetSortKey()
	{
		return unit.getNumContainers();
	}
	
	/**
	 * Apply the action
	 */
	public void doAction(boolean addToDb) throws IllegalRequestException
	{
		if (reCreateUnit)
		{
			// This action is purely for the enemy client, we don't need to create a new unit
			unit = gameState.getUnitById(id);
		}
		else
		{
			// Create a new unit
			unit = new UnitState(id, locationX, locationY, faction, UnitConfig.allUnitsMap.get(configId));
			gameState.registerUnit(unit);
	
			// Link it to the container
			if (containerId != -1)
			{
				UnitState container = gameState.getUnitById(containerId);
				container.addUnit(unit);
			}
			else
				gameState.addUnit(unit);
		}
	}
	
	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param, User initiatingUser) throws IllegalRequestException, IgnoreActionException
	{
		id = Integer.parseInt(param[1]); 
		locationX = Integer.parseInt(param[2]); 
		locationY = Integer.parseInt(param[3]); 
		faction = Faction.fromInt(Integer.parseInt(param[4])); 
		configId = Integer.parseInt(param[5]);
		containerId = Integer.parseInt(param[6]);
		reCreateUnit = Integer.parseInt(param[7]) != 0;
		
		// These should no longer be stored in the db
		if (reCreateUnit)
			throw new IgnoreActionException();
	}
	
	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction)
	{
		if (remoteFaction == Faction.opposite(faction))
		{
			if (oldStateContainer != null && !oldStateContainer.isDetected())
				return "c," 
						+ id + "," 
						+ oldStateContainer.locationX + "," 
						+ oldStateContainer.locationY + ","
						+ Faction.toInt(oldStateFaction) + "," 
						+ UnitConfig.unknownUnit.id + ","
						+ "-1,"
						+ (reCreateUnit? 1 : 0);
			else
				return "c," 
						+ id + "," 
						+ oldStateLocationX + "," 
						+ oldStateLocationY + ","
						+ Faction.toInt(oldStateFaction) + "," 
						+ UnitConfig.unknownUnit.id + ","
						+ (oldStateContainer != null? oldStateContainer.id : -1) + ","
						+ (reCreateUnit? 1 : 0);
		}
		else
			return "c," 
					+ id + "," 
					+ locationX + "," 
					+ locationY + ","
					+ Faction.toInt(faction) + "," 
					+ configId + ","
					+ containerId + ","
					+ (reCreateUnit? 1 : 0);
	}
}

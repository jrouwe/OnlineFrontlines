package onlinefrontlines.game.actions;

import onlinefrontlines.auth.*;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.IllegalRequestException;

/**
 * Action sent by the server to identify the unit type of an unknown unit.
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
public class ActionIdentifyUnit extends Action
{
	private int id;

	private Faction oldStateFaction;	
	private int oldStateUnitConfigId;
	private int oldStateMovementPointsLeft;
	private int oldStateActionsLeft;
	private boolean oldStateLastActionWasMove; 
	private int oldStateArmour;
	private int oldStateAmmo;
	
	/**
	 * Constructor
	 */
	public ActionIdentifyUnit()
	{
	}
	
	/**
	 * Constructor
	 */
	public ActionIdentifyUnit(int id)
	{
		this.id = id;
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
		UnitState u = gameState.getUnitById(id);
	
		oldStateFaction = u.faction;		
		oldStateUnitConfigId = u.unitConfig.id;
		oldStateMovementPointsLeft = u.movementPointsLeft;
		oldStateActionsLeft = u.actionsLeft;
		oldStateLastActionWasMove = u.lastActionWasMove; 
		oldStateArmour = u.armour;
		oldStateAmmo = u.ammo;
	}
	
	/**
	 * Query if remote client should receive this action
	 */
	public receiveTime pendingActionGetReceiveTime(Faction remoteFaction)
	{
		// Get unit
		UnitState unit = gameState.getUnitById(id);
		
		// If unit is destroyed before it is identified, identify it now
		if (unit == null)
			return receiveTime.now;
		
		// Everyone but the enemy can see this unit so only the enemy needs identification
		if (Faction.opposite(unit.faction) != remoteFaction)
			return receiveTime.never;
		
		// During setup phase don't identify unit
		if (gameState.turnNumber == 0)
			return receiveTime.later;
		
		// If unit can no longer be seen, this identification doesn't need to be sent anymore
		if (unit.checkDetectionLost())
			return receiveTime.never;
		
		// If unit has been marked 'identified' send the identification now
		if (unit.identifiedByEnemy)
			return receiveTime.now;

		// Receive the identification later
		return receiveTime.later;
	}
	
	/**
	 * Pending actions are sorted so that the client receives them in the correct order
	 */
	public int pendingActionGetSortKey()
	{
		return 1000;
	}
	
	/**
	 * Apply the action
	 */
	public void doAction(boolean addToDb) throws IllegalRequestException
	{
		// On the server there is nothing to be done
	}
	
	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param, User initiatingUser) throws IllegalRequestException, IgnoreActionException
	{
		// These should no longer be stored in the db
		throw new IgnoreActionException();
	}
	
	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction)
	{
		if (oldStateFaction != null && remoteFaction == Faction.opposite(oldStateFaction))
			return "i," 
					+ id + "," 
					+ oldStateUnitConfigId + ","
					+ oldStateMovementPointsLeft + ","
					+ oldStateActionsLeft + ","
					+ (oldStateLastActionWasMove? 1 : 0) + "," 
					+ oldStateArmour + ","
					+ oldStateAmmo;
		else
			return "i," + id;
	}
}

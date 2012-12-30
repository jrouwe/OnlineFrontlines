package onlinefrontlines.game.actions;

import onlinefrontlines.auth.User;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.IllegalRequestException;

/**
 * Action sent by the client to indicate that the player has finished setting up its units.
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
public class ActionPlayerReady extends Action 
{
	private int userId;
	
	/**
	 * Default constructor
	 */
	public ActionPlayerReady()
	{		
	}
	
	/**
	 * Constructor to directly fill the action
	 */
	public ActionPlayerReady(int userId)
	{
		this.userId = userId;
	}
	
	/**
	 * Query if remote client is allowed to send this action when it is not his turn 
	 */
	public boolean remoteHasPermissionToSendWhenNotHisTurn()
	{
		return true;
	}

	/**
	 * Apply the action
	 */
	public void doAction(boolean addToDb) throws IllegalRequestException 
	{
		if (gameState.getPlayerId(Faction.f1) == userId)
			gameState.playerReady[0] = true;
		else if (gameState.getPlayerId(Faction.f2) == userId)
			gameState.playerReady[1] = true;
		
		if (addToDb)
		{			
			// Reset cache for top of page
			gameState.clearPageTopCache();
		}
	}

	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param, User initiatingUser) throws IllegalRequestException, IgnoreActionException 
	{
		// Check winning state
		if (gameState.winningFaction != Faction.invalid)
			throw new IllegalRequestException("Game has ended");

		// Get user id
		if (initiatingUser != null)
			userId = initiatingUser.id;
		else			
			userId = Integer.parseInt(param[1]);
		
		// Check if already ready
		if (gameState.getPlayerId(Faction.f1) == userId)
		{
			if (gameState.playerReady[0])
				throw new IllegalRequestException("Player is already ready");
		}
		else if (gameState.getPlayerId(Faction.f2) == userId)
		{
			if (gameState.playerReady[1])
				throw new IllegalRequestException("Player is already ready");
		}
	}

	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction) 
	{
		return "r," + userId;
	}
}

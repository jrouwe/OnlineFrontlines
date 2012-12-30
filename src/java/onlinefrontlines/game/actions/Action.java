package onlinefrontlines.game.actions;

import onlinefrontlines.game.*;
import onlinefrontlines.utils.IllegalRequestException;
import onlinefrontlines.auth.*;

/**
 * Base class game action. These actions modify the game state and are sent from clients to the server to do requests. The server sends them back (possibly modified) to execute the action. Some actions are only sent from the server.
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
public abstract class Action 
{
	protected GameState gameState;
	
	/**
	 * Set game state
	 */ 
	public void setGameState(GameState gameState)
	{
		this.gameState = gameState;
	}
	
	/**
	 * Get game state
	 */
	public GameState getGameState()
	{
		return gameState;
	}
	
	/**
	 * Query if remote client is allowed to send this action
	 */
	public boolean remoteHasPermissionToSend()
	{
		return true;
	}
	
	/**
	 * Query if remote client is allowed to send this action when it is not his turn 
	 */
	public boolean remoteHasPermissionToSendWhenNotHisTurn()
	{
		return false;
	}
	
	/**
	 * Update call for actions that are in the pending list
	 * Called before each executed action to get state from before the action
	 */
	public void pendingActionUpdate()
	{		
	}
	
	/**
	 * Enum to indicate when this action should be received
	 */
	public static enum receiveTime
	{
		now,
		later,
		never
	}
	
	/**
	 * Query if remote client should receive this action
	 * This function is called after each executed action while an action is in the pending to be sent list
	 */
	public receiveTime pendingActionGetReceiveTime(Faction remoteFaction)
	{
		return receiveTime.now;
	}
	
	/**
	 * Pending actions are sorted so that the client receives them in the correct order
	 * The lower the number the sooner it is sent
	 */
	public int pendingActionGetSortKey()
	{
		return 0;
	}
	
	/**
	 * Apply the action
	 * @param addToDb If the action will be added to the database (or false if it is being loaded and executed again)
	 */
	public abstract void doAction(boolean addToDb) throws IllegalRequestException;
	
	/**
	 * Convert action from a string
	 */
	public abstract void fromString(String[] param, User initiatingUser) throws IllegalRequestException, IgnoreActionException;
	
	/**
	 * Convert action to a string
	 */
	public final String toString()
	{
		return toString(null);
	}
	
	/**
	 * Convert action to a string
	 */
	public abstract String toString(Faction remoteFaction);
	
	/**
	 * Factory function to create action from action string
	 */
	public static Action createAction(String identifier) throws IllegalRequestException
	{
		if (identifier.equals("c"))
			return new ActionCreateUnit();
		else if (identifier.equals("l"))
			return new ActionTeleportUnit();
		else if (identifier.equals("m"))
			return new ActionMoveUnit();
		else if (identifier.equals("a"))
			return new ActionAttackUnit();
		else if (identifier.equals("t"))
			return new ActionTransformUnit();
		else if (identifier.equals("e"))
			return new ActionEndTurn();
		else if (identifier.equals("g"))
			return new ActionEndGame();
		else if (identifier.equals("x"))
			return new ActionTextMessage();
		else if (identifier.equals("j"))
			return new ActionPlayerJoin();
		else if (identifier.equals("r"))
			return new ActionPlayerReady();
		else if (identifier.equals("u"))
			return new ActionRemoveUnit();
		else if (identifier.equals("i"))
			return new ActionIdentifyUnit();
		else if (identifier.equals("s"))
			return new ActionSurrender();
		else if (identifier.equals("d"))
			return new ActionRequestDraw();
		else if (identifier.equals("o"))
			return new ActionTimeOut();
		else if (identifier.equals("p"))
			return new ActionPlayerProperties();
		else if (identifier.equals("at"))
			return new ActionAnnotateTiles();
		else
   			throw new IllegalRequestException("Unknown action");
	}
}

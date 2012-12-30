package onlinefrontlines.lobby.actions;

import java.sql.SQLException;
import onlinefrontlines.lobby.*;
import onlinefrontlines.utils.IllegalRequestException;
import onlinefrontlines.utils.CacheException;

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
	protected LobbyState lobbyState;
	protected LobbyUser lobbyUser;
	
	/**
	 * Set lobby state
	 */ 
	public void setLobbyState(LobbyState lobbyState)
	{
		this.lobbyState = lobbyState;
	}
	
	/**
	 * Set lobby user
	 */
	public void setLobbyUser(LobbyUser lobbyUser)
	{
		this.lobbyUser = lobbyUser;
	}
	
	/**
	 * Apply the action
	 */
	public abstract void doAction() throws SQLException, CacheException, IllegalRequestException;
	
	/**
	 * Convert action from a string
	 */
	public abstract void fromString(String[] param) throws IllegalRequestException;

	/**
	 * Factory function to create action from action string
	 */
	public static Action createAction(String identifier) throws IllegalRequestException
	{
		if (identifier.equals("a"))
			return new ActionAttack();
		else if (identifier.equals("d"))
			return new ActionDefend();
		else if (identifier.equals("y"))
			return new ActionAcceptChallenge();
		else if (identifier.equals("n"))
			return new ActionDeclineChallenge();
		else if (identifier.equals("c"))
			return new ActionCancel();
		else if (identifier.equals("x"))
			return new ActionTextMessage();
		else
   			throw new IllegalRequestException("Unknown action");
	}
}

package onlinefrontlines.game.actions;

import onlinefrontlines.auth.User;
import onlinefrontlines.game.Faction;
import onlinefrontlines.game.IgnoreActionException;
import onlinefrontlines.utils.IllegalRequestException;
import onlinefrontlines.utils.Tools;

/**
 * Action sent by a client to chat with other users.
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
public class ActionTextMessage extends Action
{
	private int userId;
	private String prefix = "";
	private String message = "";
		
	/**
	 * Query if remote client is allowed to send this action when it is not his turn 
	 */
	public boolean remoteHasPermissionToSendWhenNotHisTurn()
	{
		return true;
	}
	
	/**
	 * Query if remote client should receive this action
	 */
	public receiveTime pendingActionGetReceiveTime(Faction remoteFaction)
	{
		// Chat messages are only displayed to players not to spectators
		return remoteFaction != Faction.none? receiveTime.now : receiveTime.never;
	}
	
	/**
	 * Apply the action
	 */
	public void doAction(boolean addToDb) throws IllegalRequestException
	{
	}
	
	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param, User initiatingUser) throws IllegalRequestException, IgnoreActionException
	{
		// Get user id
		if (initiatingUser != null)
			userId = initiatingUser.id;
		else
			userId = Integer.parseInt(param[1]);

		// Get message
		message = param[2];
		if (message == null || message.length() == 0)
			throw new IllegalRequestException("No message specified");
		if (message.contains("\n"))
			throw new IllegalRequestException("Cannot put line feeds in message");
		
		// Calculate prefix
		if (initiatingUser != null)
			prefix = Tools.flashEscape(initiatingUser.username + ": ");
	}
	
	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction)
	{
		// Limit to 256 characters to fit into db
		String value = "x," + userId + "," + prefix + message;
		return value.length() > 256? value.substring(0, 256) : value;
	}
}

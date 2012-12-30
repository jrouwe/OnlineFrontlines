package onlinefrontlines.lobby.actions;

import java.sql.SQLException;
import onlinefrontlines.lobby.TextMessage;
import onlinefrontlines.utils.IllegalRequestException;
import onlinefrontlines.utils.Tools;
import onlinefrontlines.utils.CacheException;

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
	private String message = "";
		
	/**
	 * Apply the action
	 */
	public void doAction() throws SQLException, CacheException, IllegalRequestException
	{
		lobbyState.addTextMessage(new TextMessage(userId, message));
	}
	
	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param) throws IllegalRequestException
	{
		// Get user id
		userId = Integer.parseInt(param[1]);
		if (lobbyUser.userId != userId)
			throw new IllegalRequestException("Initiating user is pretending to be someone else");

		// Get message
		message = param[2];
		if (message == null || message.length() == 0)
			throw new IllegalRequestException("No message specified");
		
		// Add prefix
		message = Tools.flashEscape(lobbyUser.cachedUsername + ": ") + message;
	}
}

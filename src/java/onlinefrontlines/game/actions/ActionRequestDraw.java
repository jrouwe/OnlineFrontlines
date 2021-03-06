package onlinefrontlines.game.actions;

import onlinefrontlines.auth.User;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.IllegalRequestException;

/**
 * Action sent by the client to request a draw.
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
public class ActionRequestDraw extends Action 
{
	/**
	 * Faction that sent this request
	 */
	private Faction faction; 
	
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
		gameState.drawRequested[Faction.toInt(faction) - 1] = true;
	}

	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param, User initiatingUser) throws IllegalRequestException, IgnoreActionException 
	{
		// Check winning state
		if (gameState.winningFaction != Faction.invalid)
			throw new IllegalRequestException("Game has ended");
		
		// Check if allowed to terminate
		if (!gameState.userCanTerminateGame())
			throw new IllegalRequestException("User not allowed to terminate game yet");
		
		// Get faction that requested draw
		faction = Faction.fromInt(Integer.parseInt(param[1]));
		
		// Check if faction is correct
		if (initiatingUser != null)
		{
			Player player = gameState.getPlayer(faction);
			if (player == null || player.id != initiatingUser.id)
				throw new IllegalRequestException("Initiating user is requesting a draw for someone else");
		}

		// Check if draw was already requested
		if (gameState.drawRequested[Faction.toInt(faction) - 1])
			throw new IllegalRequestException("Draw already requested this turn");
	}

	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction) 
	{
		return "d," + Faction.toInt(faction);
	}
}

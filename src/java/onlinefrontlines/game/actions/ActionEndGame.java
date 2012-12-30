package onlinefrontlines.game.actions;

import onlinefrontlines.auth.User;
import onlinefrontlines.game.*;
import onlinefrontlines.utils.IllegalRequestException;

/**
 * Action that is sent by the server only to indicate the end of the game and who won.
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
public class ActionEndGame extends Action
{
	private Faction faction;
	
	/**
	 * Constructor
	 */
	public ActionEndGame()
	{
	}
	
	/**
	 * Constructor
	 */
	public ActionEndGame(Faction faction)
	{
		this.faction = faction;
	}
	
	/**
	 * Query if remote client is allowed to send this action
	 */
	public boolean remoteHasPermissionToSend()
	{
		return false;
	}
	
	/**
	 * Apply the action
	 */
	public void doAction(boolean addToDb) throws IllegalRequestException
	{
		// End game
		gameState.setWinningFaction(faction);
		
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

		faction = Faction.fromInt(Integer.parseInt(param[1]));
	}
	
	/**
	 * Convert action to a string
	 */
	public String toString(Faction remoteFaction)
	{
		return "g," + Faction.toInt(faction);
	}
}

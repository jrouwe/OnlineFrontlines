package onlinefrontlines.lobby.actions;

import java.sql.SQLException;
import onlinefrontlines.lobby.*;
import onlinefrontlines.utils.IllegalRequestException;
import onlinefrontlines.utils.CacheException;

/**
 * Action declines a challenge from another player
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
public class ActionDeclineChallenge extends Action
{
	private int userId;
	
	/**
	 * Constructor
	 */
	public ActionDeclineChallenge()
	{		
	}
	
	/**
	 * Constructor
	 */
	public ActionDeclineChallenge(int userId)
	{
		this.userId = userId;
	}
	
	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param) throws IllegalRequestException
	{
		userId = Integer.parseInt(param[1]);
	}

	/**
	 * Apply the action
	 */
	public void doAction() throws SQLException, CacheException, IllegalRequestException
	{
		if (lobbyUser.hasAcceptedChallenge)
			throw new IllegalRequestException("Already accepted");

		Country attackedCountry = lobbyUser.getAttackedCountry();
		
		if (attackedCountry == null)
			throw new IllegalRequestException("No country");
		
		if (attackedCountry.defender == null)
			throw new IllegalRequestException("No defender");
		
		if (attackedCountry.defender.userId != userId)
			throw new IllegalRequestException("Different defender");
		
		// Kick user out
		LobbyUser defender = attackedCountry.defender; 
		defender.setCountries(null, null);
		lobbyState.notifyLobbyUserChanged(defender);
	}
}

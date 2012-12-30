package onlinefrontlines.lobby.actions;

import onlinefrontlines.lobby.LobbyUser;
import onlinefrontlines.utils.IllegalRequestException;
import onlinefrontlines.utils.CacheException;

/**
 * Cancels attack / defense for a country
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
public class ActionCancel extends Action
{
	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param) throws IllegalRequestException
	{
	}

	/**
	 * Apply the action
	 */
	public void doAction() throws CacheException, IllegalRequestException
	{
		// Reset defender state
		if (lobbyUser.getAttackedCountry() != null)
		{
			LobbyUser defender = lobbyUser.getAttackedCountry().defender;
			if (defender != null
				&& defender.hasAcceptedChallenge)
			{
				defender.setCountries(null, null);
				lobbyState.notifyLobbyUserChanged(defender);
			}
		}
		
		// Cancel attack / defense
		lobbyUser.setCountries(null, null);
		lobbyState.notifyLobbyUserChanged(lobbyUser);
	}
}

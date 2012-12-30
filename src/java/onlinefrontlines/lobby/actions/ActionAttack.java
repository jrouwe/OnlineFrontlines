package onlinefrontlines.lobby.actions;

import java.util.Calendar;
import java.sql.SQLException;
import onlinefrontlines.Constants;
import onlinefrontlines.auth.User;
import onlinefrontlines.lobby.*;
import onlinefrontlines.utils.IllegalRequestException;
import onlinefrontlines.utils.CacheException;

/**
 * Action starts attacking a country
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
public class ActionAttack extends Action
{
	private int defendLocationX;
	private int defendLocationY;
	private int attackLocationX;
	private int attackLocationY;

	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param) throws IllegalRequestException
	{
		defendLocationX = Integer.parseInt(param[1]);
		defendLocationY = Integer.parseInt(param[2]);
		attackLocationX = Integer.parseInt(param[3]);
		attackLocationY = Integer.parseInt(param[4]);
	}

	/**
	 * Apply the action
	 */
	public void doAction() throws SQLException, CacheException, IllegalRequestException
	{
		try
		{
			// Reset current country
			lobbyUser.setCountries(null, null);
			
			// Get countries
			Country dcountry = lobbyState.getCountry(defendLocationX, defendLocationY);
			Country acountry = lobbyState.getCountry(attackLocationX, attackLocationY);
			
			// Check if country is still exclusive to someone else
			if (dcountry.getOwnerExclusiveTimeLeft() > 0 
				&& dcountry.ownerUserId != lobbyUser.userId)
				throw new IllegalRequestException("Defended country is exclusive");
			if (acountry.getOwnerExclusiveTimeLeft() > 0 
				&& acountry.ownerUserId != lobbyUser.userId)
				throw new IllegalRequestException("Attacked country is exclusive");

			// Check if attack is possible
			if (!lobbyState.isAttackPossible(dcountry, acountry, lobbyUser.army))
				throw new IllegalRequestException("Attack not possible");			
			
			// Init country
			lobbyUser.setCountries(dcountry, acountry);
			lobbyUser.challengeValidUntil = Calendar.getInstance().getTime().getTime() + Constants.LOBBY_CHALLENGE_TIME;
			
			// Check auto defend
			if (acountry.ownerUserId != 0)
			{
				// Check if owner is busy
				LobbyUser owner = lobbyState.getLobbyUser(acountry.ownerUserId);
				if (owner.getAttackedCountry() == null || !owner.hasAcceptedChallenge)
				{
					// Check if owner has auto defend turned on
					User ownerUser = owner.getUser();
					if (ownerUser.autoDefendOwnedCountry) 
					{
						// Current owner wants to defend
						ActionDefend defend = new ActionDefend(acountry.locationX, acountry.locationY, lobbyUser.userId);
						defend.setLobbyState(lobbyState);
						defend.setLobbyUser(owner);
						defend.doAction();
					}
				}
			}
		}
		finally
		{
			lobbyState.notifyLobbyUserChanged(lobbyUser);
		}
	}
}

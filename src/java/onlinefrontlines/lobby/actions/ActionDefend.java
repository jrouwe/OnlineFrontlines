package onlinefrontlines.lobby.actions;

import java.sql.SQLException;
import onlinefrontlines.auth.User;
import onlinefrontlines.lobby.Country;
import onlinefrontlines.utils.IllegalRequestException;
import onlinefrontlines.utils.CacheException;

/**
 * Action starts defending a country that is under attack
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
public class ActionDefend extends Action
{
	private int locationX;
	private int locationY;
	private int userId;
	
	/**
	 * Constructor
	 */
	public ActionDefend()
	{		
	}
	
	/**
	 * Constructor
	 * 
	 * @param locationX
	 * @param locationY
	 * @param userId
	 */
	public ActionDefend(int locationX, int locationY, int userId)
	{
		this.locationX = locationX;
		this.locationY = locationY;
		this.userId = userId;
	}

	/**
	 * Convert action from a string
	 */
	public void fromString(String[] param) throws IllegalRequestException
	{
		locationX = Integer.parseInt(param[1]);
		locationY = Integer.parseInt(param[2]);
		userId = Integer.parseInt(param[3]);
	}

	/**
	 * Apply the action
	 */
	public void doAction() throws SQLException, CacheException, IllegalRequestException
	{
		Country country = lobbyState.getCountry(locationX, locationY);
		
		if (country == null)
			throw new IllegalRequestException("Country does not exist at " + locationX + "," + locationY + " for lobby " + lobbyState.lobbyConfig.id);
		
		if (country.currentGameId != 0)
			throw new IllegalRequestException("Game in progress");

		if (country.defender != null)
			throw new IllegalRequestException("Already defended");

		if (country.attacker == null)
			throw new IllegalRequestException("Not attacked");
		
		if (country.attacker.userId == lobbyUser.userId)
			throw new IllegalRequestException("Cannot defend your own attack");

		if (country.attacker.userId != userId)
			throw new IllegalRequestException("Attacked by someone else");
		
		// Init country
		lobbyUser.setCountries(country, country.attacker.getDefendedCountry());
		lobbyUser.hasAcceptedChallenge = true;
		lobbyState.notifyLobbyUserChanged(lobbyUser);
		
		User attackerUser = country.attacker.getUser(); 
		if (country.defender.army == country.attacker.army
			&& attackerUser.autoDeclineFriendlyDefender)
		{
			// Auto decline
			ActionDeclineChallenge decline = new ActionDeclineChallenge(lobbyUser.userId); 
			decline.setLobbyState(lobbyState);
			decline.setLobbyUser(country.attacker);
			decline.doAction();
		}
		else
		{
			// Auto accept
			ActionAcceptChallenge accept = new ActionAcceptChallenge(lobbyUser.userId);
			accept.setLobbyState(lobbyState);
			accept.setLobbyUser(country.attacker);
			accept.doAction();
		}		
	}
}

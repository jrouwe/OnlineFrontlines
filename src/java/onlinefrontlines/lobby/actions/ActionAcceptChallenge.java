package onlinefrontlines.lobby.actions;

import java.util.Random;
import java.sql.SQLException;
import onlinefrontlines.Army;
import onlinefrontlines.game.*;
import onlinefrontlines.auth.User;
import onlinefrontlines.lobby.*;
import onlinefrontlines.utils.IllegalRequestException;
import onlinefrontlines.utils.Tools;
import onlinefrontlines.utils.CacheException;

/**
 * Action accepts a challenge from another player
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
public class ActionAcceptChallenge extends Action
{
	private int userId;
	
	/**
	 * Constructor
	 */
	ActionAcceptChallenge()
	{		
	}
	
	/**
	 * Constructor
	 * 
	 * @param userId User that is defending
	 */
	ActionAcceptChallenge(int userId)
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
		Country defendedCountry = lobbyUser.getDefendedCountry();
		
		if (attackedCountry == null)
			throw new IllegalRequestException("No country");
		
		if (attackedCountry.defender == null)
			throw new IllegalRequestException("No defender");
		
		if (attackedCountry.defender.userId != userId)
			throw new IllegalRequestException("Different defender");
		
		if (defendedCountry.currentGameId != 0)
			throw new IllegalRequestException("Game in progress");

		if (attackedCountry.currentGameId != 0)
			throw new IllegalRequestException("Game in progress");
		
		// Get lobby users
		LobbyUser attacker = attackedCountry.attacker;
		LobbyUser defender = attackedCountry.defender;
		
		// Get users (do before creating game in case there is an exception getting the users)
		User attackerUser = attacker.getUser();
		User defenderUser = defender.getUser();
		
		try
		{
			// Create game state
			GameState gameState = attackedCountry.countryConfig.createAndRegisterGameState(attacker.army == Army.red, new Random().nextBoolean(), lobbyState.getLobbyId(), attackedCountry.locationX, attackedCountry.locationY, defendedCountry.locationX, defendedCountry.locationY, true, -1);

	    	// Setup players
	    	gameState.joinGame(Faction.f1, attackerUser);    	
	    	gameState.joinGame(Faction.f2, defenderUser);
    	
	    	// Store game id
	    	defendedCountry.currentGameId = gameState.id;
	    	defendedCountry.currentGameUserId = defender.userId;
	    	attackedCountry.currentGameId = gameState.id;
	    	attackedCountry.currentGameUserId = attacker.userId;
			lobbyState.notifyCountryChanged(defendedCountry);			
			lobbyState.notifyCountryChanged(attackedCountry);
		}
		catch (DeploymentFailedException e)
		{
			Tools.logException(e);
		}
		
    	// Cancel challenge
    	attacker.setCountries(null, null);
    	defender.setCountries(null, null);
    	lobbyState.notifyLobbyUserChanged(attacker);
    	lobbyState.notifyLobbyUserChanged(defender);

	}
}

package onlinefrontlines.lobby;

import org.apache.log4j.Logger;
import onlinefrontlines.game.GameStateDAO;
import onlinefrontlines.utils.Tools;

/**
 * This thread wakes up every X seconds and queries the state of all games in progress
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
public class LobbyGameEndThread extends Thread 
{
	private static final Logger log = Logger.getLogger(LobbyGameEndThread.class);

	/**
	 * Constructor
	 */
	public LobbyGameEndThread()
	{
		super("LobbyGameEndThread");
	}
	
	/**
	 * Run the thread
	 */
	public void run()
	{
		log.info("Thread started");

		for (;;)
		{			
			try
			{
				// Loop through all lobbies
				for (LobbyState lobbyState : LobbyStateCache.getInstance().getValuesQuiet())
					synchronized (lobbyState)
					{
						// Loop through all games that need processing
						for (GameStateDAO.GameForLobby e : GameStateDAO.getGamesForLobbyToProcess(lobbyState.getLobbyId()))
						{
							// Notify lobby
							lobbyState.notifyGameEnd(e.attackerUserId, e.defenderUserId, e.attackedCountryX, e.attackedCountryY, e.defendedCountryX, e.defendedCountryY, e.winningFaction);
							
							// Mark game as processed
							GameStateDAO.markGameProcessedByLobby(e.gameId);
						}
					}
			}
			catch (Exception e)
			{			
				// Log error, retry
				Tools.logException(e);
			}

			try
			{
				// Wait for next cycle
				synchronized (this)
				{
					wait(5000);
				}
			}
			catch (InterruptedException e)
			{
				// Normal termination
				break;
			}
		}
		
		log.info("Thread stopped");
	}
}

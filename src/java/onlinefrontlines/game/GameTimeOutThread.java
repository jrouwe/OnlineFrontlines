package onlinefrontlines.game;

import java.util.*;
import org.apache.log4j.Logger;
import onlinefrontlines.utils.Tools;

/**
 * This thread wakes up every X seconds to check if there are games that are in a time out state.
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
public class GameTimeOutThread extends Thread 
{
	private static final Logger log = Logger.getLogger(GameTimeOutThread.class);
	
	/**
	 * Constructor
	 */
	public GameTimeOutThread()
	{
		super("GameTimeOutThread");
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
				// Get games
				ArrayList<Integer> games = GameStateDAO.getTimedOutGames();			
				for (Integer id : games)
				{
					try
					{
						// Get game
						GameState gameState = GameStateCache.getInstance().get(id);
						if (gameState == null)
							continue;
						
						// End game
						synchronized (gameState)
						{
							// Check timeouts on this game
							gameState.checkTimeout();
								
							// Update cache
							GameStateCache.getInstance().put(gameState.id, gameState);
						}
					}
					catch (Exception e)
					{
						// Log error, continue
						Tools.logException(e);
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
					wait(10 * 60 * 1000);
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

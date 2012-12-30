package onlinefrontlines.lobby;

import onlinefrontlines.Constants;
import onlinefrontlines.utils.Tools;

import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 * This thread wakes up every X seconds checks if a lobby needs to be reset because all tiles have been conquered by one army
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
public class LobbyAllConqueredThread extends Thread 
{
	private static final Logger log = Logger.getLogger(LobbyAllConqueredThread.class);

	/**
	 * Constructor
	 */
	public LobbyAllConqueredThread()
	{
		super("LobbyAllConqueredThread");
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
				long time = Calendar.getInstance().getTime().getTime();
				
				// Loop through all lobbies
				for (LobbyState lobbyState : LobbyStateCache.getInstance().getValuesQuiet())
					synchronized (lobbyState)
					{
						// Randomize lobby if it has been conquered
						if (time - lobbyState.allConqueredTime > Constants.TIME_BEFORE_RESET_AFTER_ALL_CONQUERED)
							lobbyState.randomizeAllCountries();
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

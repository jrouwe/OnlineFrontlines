package onlinefrontlines.lobby;

import java.util.ArrayList;
import java.sql.SQLException;
import onlinefrontlines.profiler.Profiler;
import onlinefrontlines.profiler.Sampler;
import onlinefrontlines.utils.Tools;
import onlinefrontlines.utils.CacheException;
import org.apache.log4j.Logger;

/**
 * This thread wakes up every X seconds to update the user stats
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
public class LobbyUserUpdateThread extends Thread 
{
	private static final Logger log = Logger.getLogger(LobbyUserUpdateThread.class);

	/**
	 * Constructor
	 */
	public LobbyUserUpdateThread()
	{
		super("LobbyUserUpdateThread");
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
				// Loop through all lobbies and get all users
				ArrayList<LobbyUser> users = new ArrayList<LobbyUser>();
				for (LobbyState lobbyState : LobbyStateCache.getInstance().getValuesQuiet())
					synchronized (lobbyState)
					{
						users.addAll(lobbyState.getUsers());
					}
				
				// Process them all one by one
				for (LobbyUser u : users)
				{
		    		Sampler sampler = Profiler.getInstance().startSampler(Profiler.CATEGORY_GENERAL, "LobbyUserUpdateThread.run update user");
		    		try
		    		{
						// Process user
						u.updateUserInfo();
					}
					catch (CacheException e)
					{
						Tools.logException(e);
					}
					catch (SQLException e)
					{
						Tools.logException(e);
					}
					finally
					{
						sampler.stop();
					}

					// Wait
					synchronized (this)
					{
						wait(1000);
					}
				}
			}
			catch (InterruptedException e)
			{
				// Normal termination
				break;
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

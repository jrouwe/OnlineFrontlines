package onlinefrontlines.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import onlinefrontlines.game.*;
import onlinefrontlines.game.ai.GameAIThread;
import onlinefrontlines.lobby.*;
import onlinefrontlines.profiler.Profiler;
import onlinefrontlines.utils.*;
import onlinefrontlines.help.*;
import onlinefrontlines.taglib.CacheTag;
import net.sf.ehcache.CacheManager;

/**
 * ServletContextListener that handles static initialization / shutdown when the webapp starts / stops
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
public final class ApplicationListener implements ServletContextListener 
{
	private static final Logger log = Logger.getLogger(ApplicationListener.class);

	private static Thread gameTimeOutThread;
	private static Thread gameUpdateDbThread;
	private static Thread lobbyUserTimeOutThread;
	private static Thread lobbyUserUpdateThread;
	private static Thread lobbyGameEndThread;
	private static Thread lobbyAllConqueredThread;
	private static Thread gameAIThread;
	
	/**
	 * Called when the webapp start
	 */
	public void contextInitialized(ServletContextEvent event) 
    {
		// Log start
		log.info("Starting");
		
    	try
    	{
			// Start ehcache
    		CacheManager.create();
    		CacheTag.initCache();    		
    		
	    	// Start profiler
	    	Profiler.getInstance().init();
	    	GameStateCache.getInstance().registerProfilers();
	    	LobbyStateCache.getInstance().registerProfilers();
	    	
    		// Init database connection pool
    		DbConnectionPool.getInstance().init(DbConnectionPool.DS_DEFAULT);
    		
    		// Load global properties
    		GlobalProperties.getInstance().load(event.getServletContext());
    		
    		// Load tips
    		Tips.getInstance().tips = TipsDAO.loadTips();

    		// Load all stuff from DB
    		CountryType.loadAll();
    		TerrainConfig.loadAll();
	    	UnitConfig.loadAll();
	    	LobbyConfig.loadAll();
	    	
	    	// Create threads
	    	gameTimeOutThread = new GameTimeOutThread();
	    	gameUpdateDbThread = new GameUpdateDbThread();
	    	lobbyUserTimeOutThread = new LobbyUserTimeOutThread();
	    	lobbyUserUpdateThread = new LobbyUserUpdateThread();
	    	lobbyGameEndThread = new LobbyGameEndThread();
	    	lobbyAllConqueredThread = new LobbyAllConqueredThread(); 
	    	gameAIThread = new GameAIThread();

	    	// Start threads
	    	Mailer.getInstance().start();
	    	gameTimeOutThread.start();
	    	gameUpdateDbThread.start();
	    	lobbyUserTimeOutThread.start();
	    	lobbyUserUpdateThread.start();
	    	lobbyGameEndThread.start();
	    	lobbyAllConqueredThread.start();
	    	gameAIThread.start();
    	}
    	catch (Exception e)
    	{
			Tools.logException(e);
    	}
    	
    	log.info("Started");
    }

	/**
	 * Shut down a specific thread
	 * 
	 * @param thread
	 */
	private void killThread(Thread thread)
	{
		thread.interrupt();
		
    	while (thread.isAlive()) 
    	{ 
    		try 
    		{ 
    			synchronized (this)
    			{
    				wait(100);
    			}
    		} 
    		catch (InterruptedException e) 
    		{    			
    		} 
    	}
	}
	
	/**
	 * Called when the webapp stops
	 */
    public void contextDestroyed(ServletContextEvent event) 
    {
    	// Log end
    	log.info("Stopping");
    	
    	try
    	{
	    	// Stop threads
	    	killThread(gameAIThread);
	    	killThread(gameTimeOutThread);
	    	killThread(gameUpdateDbThread);
	    	killThread(lobbyUserTimeOutThread);
	    	killThread(lobbyUserUpdateThread);
	    	killThread(lobbyGameEndThread);
	    	killThread(lobbyAllConqueredThread);
	    	killThread(Mailer.getInstance());
	    	
	    	// Unload all stuff
	    	TerrainConfig.unloadAll();
	    	UnitConfig.unloadAll();
	    	LobbyConfig.unloadAll();
	    	
			// Write all pending changes
			GameStateCache.getInstance().updateDbAll();

	    	// Stop profiler
	    	Profiler.getInstance().exit();
	    	
			// Shutdown cache
			CacheTag.shutdownCache();
			CacheManager.getInstance().clearAll();
			CacheManager.getInstance().shutdown();
    	}
    	catch (Exception e)
    	{
    		Tools.logException(e);
    	}

		// Log stop
		log.info("Stopped");		
    }
}

package onlinefrontlines.game.web;

import java.util.*;
import org.apache.log4j.Logger;
import onlinefrontlines.web.*;
import onlinefrontlines.game.*;
import onlinefrontlines.help.Tips;

/**
 * This action is called from the game flash application to load the game data
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
public class GameLoadAction extends WebAction
{
	private static final Logger log = Logger.getLogger(GameLoadAction.class);
		   
	/**
	 * Game Id for the game to load
	 */
    public int gameId;
    
    /**
     * Faction to show the game for 
     */
    public int localPlayer;
    
    /**
     * Map image data
     */
    public String tileImageNumbers;
    
    /**
     * Map ownership data
     */
    public String tileOwners;
        
    /**
     * Score limiit
     */
    public int scoreLimit;
    
	/**
	 * If fog of war is enabled
	 */
	public int fogOfWarEnabled;
	
	/**
	 * Color of faction 1
	 */
	public int faction1IsRed;
	
	/**
	 * If faction 1 or faction 2 starts
	 */
	public int faction1Starts;
	
	/**
	 * Lobby id that hosted the game
	 */
	public int lobbyId;
	
	/**
	 * If the game is play by mail
	 */
	public int playByMail;
        
    /**
     * Access to all terrain types
     */
    public Collection<TerrainConfig> getTerrain()
    {
    	return TerrainConfig.allTerrain;
    }

    /**
     * Access to all unit types
     */
    public Collection<UnitConfig> getUnits()
    {
    	return UnitConfig.allUnits;
    }
    
    /**
     * Access to all tips
     */
    public Collection<Tips.Entry> getTips()
    {
    	ArrayList<Tips.Entry> tips = new ArrayList<Tips.Entry>();
    	
    	tips.ensureCapacity(Tips.getInstance().tips.size());
    	
    	for (Tips.Entry e : Tips.getInstance().tips)
    		if (e.showInGame)
    			tips.add(e);
    	
    	return tips;
    }
    
    /**
     * Access to actions
     */
    public ArrayList<String> executedActions = new ArrayList<String>();
    
    /**
     * Returned error code
     */
    public int errorCode = -1;
    
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	log.info("User '" + (user != null? user.id : 0) + "' loaded game '" + gameId + "'");

        // Get the game
    	GameState gameState = GameStateCache.getInstance().get(gameId);
    	if (gameState == null)
    		return getErrorView();
    	
    	// Prevent concurrent access
    	synchronized (gameState)
    	{	    	
    		// Get requesting user faction
    		Faction userFaction = Faction.fromInt(localPlayer);
    		
			// Games in progress cannot be viewed by other users
    		if (gameState.winningFaction == Faction.invalid && userFaction == Faction.none)
				return getErrorView();

			// Check if requested faction is correct
			if (userFaction != gameState.getUserFaction(user) && userFaction != Faction.none)
				return getErrorView();

	    	// Fill in fields
	    	tileImageNumbers = gameState.mapConfig.tileImageNumbersToString();
	    	tileOwners = gameState.mapConfig.tileOwnersToString();
	    	scoreLimit = gameState.countryConfig.scoreLimit;
	    	fogOfWarEnabled = gameState.countryConfig.fogOfWarEnabled? 1 : 0;
	    	faction1IsRed = gameState.faction1IsRed? 1 : 0;
	    	faction1Starts = gameState.faction1Starts? 1 : 0;
	    	lobbyId = gameState.winningFaction == Faction.invalid? gameState.lobbyId : 0; // only allow user to go back to lobby when not in replay mode
	    	playByMail = gameState.playByMail? 1 : 0;
	    	for (String a : gameState.getFilteredList(userFaction).sendList)
	    		executedActions.add(a);
    	}
    	
        return getSuccessView();
    }
}

package onlinefrontlines.game.web;

import org.apache.log4j.Logger;
import onlinefrontlines.web.*;
import onlinefrontlines.game.*;
import onlinefrontlines.userstats.UserRank;

/**
 * This action joins a game in progress
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
public class GameDoJoinAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(GameDoJoinAction.class);
				   
	/**
	 * Id of the game to play
	 */
    public int gameId;
    
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
        // Get game
        GameState gameState = GameStateCache.getInstance().get(gameId);
        if (gameState == null)
        {
        	addActionError(getText("gameDoesNotExist"));
        	return getErrorView();
        }
    	if (gameState.countryConfig.requiredLevel > UserRank.getLevel(user.id)
    		&& gameState.countryConfig.creatorUserId != user.id)
    	{
    		addActionError(getText("needHigherRank"));
    		return getErrorView();
    	}
        
        synchronized (gameState)
        {
        	// Check if the game is still joinable
        	int otherPlayerId = gameState.getPlayerId(Faction.f1);
        	if (otherPlayerId != user.id
        		&& gameState.getPlayer(Faction.f2) == null
        		&& gameState.winningFaction == Faction.invalid)
        	{
        		// Join the game
        		gameState.joinGame(Faction.f2, user);
        		
        		// Update cache
        		GameStateCache.getInstance().put(gameState.id, gameState);
        		
        		log.info("Game '" + gameId + "' joined by '" + user.id + "'");

            	return new WebViewRedirect("GamePlay.do?gameId=" + gameId);
        	}
        	
        	addActionError(getText("gameCannotBeJoined"));
        	return getErrorView();
        }
    }
}

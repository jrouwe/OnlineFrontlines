package onlinefrontlines.game.web;

import org.apache.log4j.Logger;
import onlinefrontlines.web.*;
import onlinefrontlines.game.*;

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
public class GamePlayAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(GamePlayAction.class);
		
	/**
	 * Id of the game to play
	 */
    public int gameId;

	/**
	 * Local player faction
	 */
    public int localPlayer;
    
    /**
     * If liking should be enabled
     */
    public boolean enableLike = false;

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
        
        synchronized (gameState)
        {
        	// Check if we're part of the game
        	Faction f;
        	if (user != null && gameState.getPlayerId(Faction.f1) == user.id)
        		f = Faction.f1;
        	else if (user != null && gameState.getPlayerId(Faction.f2) == user.id)
        		f = Faction.f2;
        	else
        	{
        		if (gameState.winningFaction == Faction.invalid)
        		{
        			// Game is not finished, give error
	        		addActionError(getText("youDoNotParticipate"));
	        		return getErrorView();
        		}
        		else
        		{
        			// Game is finished, go replay
        			return new WebViewRedirect("GameReplay.do?gameId=" + gameState.id);
        		}
        	}

			// Set player
			localPlayer = Faction.toInt(f);
			
    		log.info("Game '" + gameId + "' played by '" + (user != null? user.id : 0) + "'");

    		return getSuccessView();
        }		
    }
}

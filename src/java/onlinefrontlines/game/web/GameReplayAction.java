package onlinefrontlines.game.web;

import onlinefrontlines.web.*;
import onlinefrontlines.game.*;

/**
 * This action views a finished game
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
public class GameReplayAction extends WebAction 
{	
	/**
	 * Id of the game to play
	 */
    public int gameId;

	/**
	 * Local player faction
	 */
    public int localPlayer = Faction.toInt(Faction.none);

    /**
     * If liking should be enabled
     */
    public boolean enableLike = true;

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
        	// Check if game is finished
        	if (gameState.winningFaction == Faction.invalid)
        	{
        		if (user != null && gameState.getUserFaction(user) != Faction.none)
        		{
        			// Game is not finished but user is playing
        			return new WebViewRedirect("GamePlay.do?gameId=" + gameState.id);
        		}
        		else
        		{
        			// Game is not finished and user is not playing
	        		addActionError(getText("gameAccessRestricted"));
	        		return getErrorView();
        		}
        	}

    		return getSuccessView();
        }		
    }
}

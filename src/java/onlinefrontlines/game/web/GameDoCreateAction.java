package onlinefrontlines.game.web;

import java.util.Random;
import org.apache.log4j.Logger;
import onlinefrontlines.Army;
import onlinefrontlines.Constants;
import onlinefrontlines.web.*;
import onlinefrontlines.auth.UserCache;
import onlinefrontlines.game.*;
import onlinefrontlines.game.actions.ActionPlayerReady;
import onlinefrontlines.userstats.UserRank;
import onlinefrontlines.utils.Tools;

/**
 * This action creates a new game 
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
public class GameDoCreateAction extends WebAction
{
	private static final Logger log = Logger.getLogger(GameDoCreateAction.class);
		
	/**
	 * Id of country config
	 */
	public int selectedCountryConfig;
	
	/**
	 * If this is an ai game or not
	 */
	public boolean ai;
		
	/**
	 * Game id of created game
	 */
	public int gameId;
	
	/**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	// Get country config
    	CountryConfig countryConfig = CountryConfigCache.getInstance().get(selectedCountryConfig);
    	if (countryConfig == null)
    	{
    		addActionError(getText("countryDoesNotExist"));
    		return getErrorView();
    	}
    	
    	// Check rank
    	if (countryConfig.requiredLevel > UserRank.getLevel(user.id) 
    		&& countryConfig.creatorUserId != user.id)
    	{
    		addActionError(getText("needHigherRank"));
    		return getErrorView();
    	}
    	
    	try
    	{
	    	// Create game state
	    	GameState gameState = countryConfig.createAndRegisterGameState(ai? (user.army == Army.blue) : (user.army == Army.red), new Random().nextBoolean(), 0, -1, -1, -1, -1, false, -1);
	
	    	// Store parameters for JSP
	    	gameId = gameState.id;
	    	
	    	if (ai)
	    	{
		    	// Setup players
		    	gameState.joinGame(Faction.f1, UserCache.getInstance().get(Constants.USER_ID_AI));
		    	gameState.joinGame(Faction.f2, user);    	
		    	
		    	// AI is always ready
		    	gameState.execute(new ActionPlayerReady(Constants.USER_ID_AI), true);
		    	
	    		log.info("Game against AI '" + gameId + "' created by '" + user.id + "'");
	    	}
	    	else
	    	{
	    		// Setup player
		    	gameState.joinGame(Faction.f1, user);    	

		    	log.info("Game '" + gameId + "' created by '" + user.id + "'");
	    	}

			return new WebViewRedirect("GamePlay.do?gameId=" + gameId);
    	}
    	catch (DeploymentFailedException e)
    	{
    		Tools.logException(e);
    		
    		addActionError(getText("deploymentFailed", e.getUnit(), e.getFaction()));
    		return getErrorView();
    	}
    }
}
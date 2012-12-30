package onlinefrontlines.game.web;

import org.apache.log4j.Logger;
import onlinefrontlines.game.*;
import onlinefrontlines.game.actions.*;
import onlinefrontlines.web.*;

/**
 * This action is used for debugging, it forcefully executes an action in a game (one action at a time) 
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
public class GameExecuteAction extends WebAction 
{	
	private static final Logger log = Logger.getLogger(GameExecuteAction.class);

	/**
	 * Game Id for the game
	 */
    public int gameId;
    
    /**
     * Requested actions
     */
    public String requestedActions = "";
    
    /**
     * Number of actions to actually execute
     */
    public int numActionsToExecute = 1;
    
    /**
     * Number of milliseconds between actions
     */
    public int delayBetweenActions = 0;
            
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	log.info("User '" + user.id + "' executed '" + numActionsToExecute + "' from '" + requestedActions + "'");

    	// Split up by line
    	String splitActions[] = requestedActions.split("\n");
    	
    	// Reconstruct actions that are left
    	requestedActions = "";
    	for (int i = numActionsToExecute; i < splitActions.length; ++i)
    		requestedActions += splitActions[i].trim() + "\n";

    	// Get the game
    	GameState gameState = GameStateCache.getInstance().get(gameId);
    	if (gameState == null)
    	{
    		addFieldError("gameId", getText("gameDoesNotExist"));
    		return getInputView();
    	}
    	
    	// Prevent concurrent access
    	synchronized (gameState)
    	{
    		for (int i = 0; i < numActionsToExecute && i < splitActions.length; ++i)
    		{
    			// Execute action
    	    	String requestedAction = splitActions[i].trim();    	
	    		String[] params = requestedAction.split(",");
	    		Action action = Action.createAction(params[0]);
	    		action.setGameState(gameState);
	    		action.fromString(params, null);
	    		gameState.execute(action, true);
	    		
	    		// Update cache
	    		GameStateCache.getInstance().put(gameState.id, gameState);
	    		
	    		// Do delay if delay requested
	    		if (delayBetweenActions > 0)
   	    			gameState.wait(delayBetweenActions);
    		}
    	}

    	return getSuccessView();
    }
}

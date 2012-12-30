package onlinefrontlines.game.web;

import java.io.*;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;
import onlinefrontlines.auth.*;
import onlinefrontlines.game.*;
import onlinefrontlines.game.actions.Action;
import onlinefrontlines.web.*;
import onlinefrontlines.profiler.Profiler;
import onlinefrontlines.profiler.Sampler;
import onlinefrontlines.utils.IllegalRequestException;
import onlinefrontlines.utils.Tools;

/**
 * This servlet is being called by the flash game application to update the state of the game
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
public final class GameUpdateServlet extends HttpServlet 
{
	private static final Logger log = Logger.getLogger(GameUpdateServlet.class);
	private static final long serialVersionUID = 0;

    /**
     * Diff two blocks of text
     */
    private String diff(String s1, String s2)
    {
    	String rv = "";
    	
    	// Split into lines
        String[] l1 = s1.split("\n");
        String[] l2 = s2.split("\n");

        // opt[i][j] = length of LCS of x[i .. l1.length] and y[j .. l2.length]
        int[][] opt = new int[l1.length + 1][l2.length + 1];

        // Compute length of LCS and all subproblems via dynamic programming
        for (int i = l1.length - 1; i >= 0; i--)
            for (int j = l2.length - 1; j >= 0; j--)
                if (l1[i].equals(l2[j]))
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                else 
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);

        // Recover LCS itself and print out non-matching lines to standard output
        int i = 0, j = 0;
        while (i < l1.length && j < l2.length) 
        {
            if (l1[i].equals(l2[j])) 
            {
                i++;
                j++;
            }
            else if (opt[i + 1][j] >= opt[i][j+1]) rv += "< " + l1[i++] + "\n";
            else                                   rv += "> " + l2[j++] + "\n";
        }

        // Dump out one remainder of one string if the other is exhausted
        while (i < l1.length || j < l2.length) 
        {
            if      (i == l1.length) rv += "> " + l2[j++] + "\n";
            else if (j == l2.length) rv += "< " + l1[i++] + "\n";
        }
        
        return rv;
    }
    
    /**
     * Output error
     */
    public void printError(PrintWriter out)
    {
    	out.print("<response><code>-1</code></response>");
    }
        
    /**
     * Handle get
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
		Sampler sampler = Profiler.getInstance().startSampler(Profiler.CATEGORY_HTTP_REQUEST, request.getRequestURI());
		try
		{
	    	// Set content type
	        response.setContentType("text/xml");
	
	        // Do not cache results
	        WebUtils.setNoCacheHeaders(response);
	
	        // Get output
	        PrintWriter out = response.getWriter();
	
	        // Get user
	        AutoAuth.AuthResult result = AutoAuth.getAuthenticatedUser(request, response);
	        User user = result != null? result.user : null;
	        
	        // Get game
	        int gameId; 
	        try 
	        { 
	        	gameId = Integer.parseInt(request.getParameter("gameId")); 
	        } 
	        catch (NumberFormatException e) 
	        { 
	        	gameId = 0; 
	        }
	        
	        // Get local player
	        int localPlayer; 
	        try 
	        { 
	        	localPlayer = Integer.parseInt(request.getParameter("localPlayer")); 
	        } 
	        catch (NumberFormatException e) 
	        { 
	        	localPlayer = 0; 
	        }
	        
	        // Get number of actions received
	        int numActionsReceived; 
	        try 
	        { 
	        	numActionsReceived = Integer.parseInt(request.getParameter("numActionsReceived")); 
	        } 
	        catch (NumberFormatException e) 
	        { 
	        	numActionsReceived = 0; 
	        }
	        
	        // Get other parameters
	        String requestedAction = request.getParameter("requestedAction");		
	        String remoteStateHash = request.getParameter("remoteStateHash");		
	        String remoteState = request.getParameter("remoteState");
	        
	    	try
	    	{
	            // Get the game
		    	GameState gameState = GameStateCache.getInstance().get(gameId);
		    	if (gameState == null)
		    	{
		    		printError(out);
		    		return;
		    	}
	
		    	// Prevent concurrent access
		    	synchronized (gameState)
		    	{
		    		// Get requesting user faction
		    		Faction userFaction = Faction.fromInt(localPlayer);
		    		
	    			// Games in progress cannot be viewed by other users
		    		if (gameState.winningFaction == Faction.invalid && userFaction == Faction.none)
			    	{
			    		printError(out);
			    		return;
			    	}
	
	    			// Check if requested faction is correct
	    			if (userFaction != gameState.getUserFaction(user) && userFaction != Faction.none)
	    	    	{
	    	    		printError(out);
	    	    		return;
	    	    	}
	
		    		// Get current player
		    		Player currentPlayer = gameState.getPlayer(gameState.currentPlayer);
		    		
		    		// Mark request time
		    		gameState.markPlayerConnected(userFaction);
	
			    	// Determine list of actions to send from
			    	ArrayList<String> sendList = gameState.getFilteredList(userFaction).sendList;
	
	    			// Check numActionsReceived parameter
			    	if (numActionsReceived < 0 || numActionsReceived > sendList.size())
			    		throw new IllegalRequestException("Invalid num actions received specified: '" + numActionsReceived + "'");
			    	
			    	// Handle requested action
			    	if (requestedAction != null && requestedAction.length() > 0)
			    	{
			    		Sampler executeActionSampler = Profiler.getInstance().startSampler(Profiler.CATEGORY_GENERAL, "GameUpdateServlet.doGet execute action");
			    		try
			    		{
				    		// Check if user participates
			    			if (userFaction == Faction.none)
			    				throw new IllegalRequestException("This user does not participate in this game");
		
			    			// Split parameters
				    		String[] params = requestedAction.split(",");
		
				    		// Determine action
				    		Action action = Action.createAction(params[0]);
				    		
				    		// Check if permission to execute this action
				    		if (!action.remoteHasPermissionToSend())
				    			throw new IllegalRequestException("Action not expected from remote");
		
				    		// Check if current user can do actions
				    		if (!action.remoteHasPermissionToSendWhenNotHisTurn() 
				    			&& (currentPlayer == null || currentPlayer.id != user.id))
				    			throw new IllegalRequestException("This user cannot perform this action right now");
				    		
				    		// Set properties
				    		action.setGameState(gameState);
				    		
				    		// Convert action from string
				    		action.fromString(params, user);
				    		
				    		// Perform the action
				    		gameState.execute(action, true);
			    		}
			    		finally
			    		{
			    			executeActionSampler.stop();
			    		}
			    	}
			    	
			    	// If we have nothing for the remote we can compare state 
			    	boolean dumpStateRequested = false;
		    		if (!gameState.loggedStateMismatch
		    			&& gameState.turnNumber > 0
		    			&& numActionsReceived == sendList.size())
		    		{
		    			Sampler compareStateSampler = Profiler.getInstance().startSampler(Profiler.CATEGORY_GENERAL, "GameUpdateServlet.doGet compare state");
		    			try
		    			{
			    			// See if we can compare full state
			    			String localState = gameState.dumpState(userFaction);
			    			if (remoteState != null && !localState.equals(remoteState))
			    			{
			    				log.error("State mismatch.\n"
			    	    				+ "game: '" + gameId + "', " 
			    	    				+ "user: '" + (user != null? user.id : 0) + "', "
			    	    				+ "user faction: '" + userFaction.toString() + "', "
			    	    				+ "num actions: '" + gameState.actions.split("\n").length + "', "
			    						+ "local state:\n" 
			    						+ localState + "\n"
			    						+ "remote state:\n" 
			    						+ remoteState + "\n"
			    						+ "diff:\n"
			    						+ diff(localState, remoteState));
			    				
			    				// We have logged the state mismatch, don't do it again
			    				gameState.loggedStateMismatch = true;		    				
			    			}
			    			else if (remoteStateHash != null)
			    			{
			    				// Compare hash only
				    			String localStateHash = AuthTools.hashMD5(localState);
			    				if (!localStateHash.equals(remoteStateHash))
				    			{
				    				log.error("State hash mismatch.\n"
				    	    				+ "game: '" + gameId + "', " 
				    	    				+ "user: '" + (user != null? user.id : 0) + "', "
				    	    				+ "user faction: '" + userFaction.toString() + "', "
				    	    				+ "num actions: '" + gameState.actions.split("\n").length + "'");
			
				    				// Request full state from remote
				    				dumpStateRequested = true;
				    			}
			    			}
		    			}
		    			finally
		    			{
		    				compareStateSampler.stop();
		    			}
		    		}
		    		
		    		// Check for timeouts
		    		gameState.checkTimeout();
		    			    	
			    	// Update cache
			    	GameStateCache.getInstance().put(gameState.id, gameState);
			    	
			    	// Render output
			    	out.print("<response><code>0</code><time>");
			   		out.print(gameState.getTimeLeft());
			    	out.print("</time><p1c>");
			   		out.print(gameState.isPlayerConnected(Faction.f1)? 1 : 0);
			    	out.print("</p1c><p2c>");
			   		out.print(gameState.isPlayerConnected(Faction.f2)? 1 : 0);
			    	out.print("</p2c><dsr>");
			   		out.print(dumpStateRequested? 1 : 0);
			   		out.print("</dsr><tbp>");
					out.print(gameState.getTimeBetweenPolls());
					out.print("</tbp>");
			    	for (int a = numActionsReceived; a < sendList.size(); ++a)
			    	{
			    		out.print("<act>");
			    		out.print(sendList.get(a));
			    		out.print("</act>");
			    	}
			    	if (gameState.canPerformAction(userFaction))
				    	out.print("<turn>1</turn>");
			    	else
			    		out.print("<turn>0</turn>");
			    	out.print("</response>");
		    	}
	    	}
	    	catch (Exception e)
	    	{
	    		Tools.logException("Exception caught while executing action, "
				    				+ "game: '" + gameId + "', " 
				    				+ "user: '" + (user != null? user.id : 0) + "', "
				    				+ "request: '" + requestedAction + "', "
				    				+ "num actions received: '" + numActionsReceived + "', "
				    				+ "message: ", e);
	    		
	    		printError(out);
	    	}
		}
		finally
		{
			sampler.stop();
		}
    }

	/**
	 * Handle post
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		doGet(request, response);
	}
}

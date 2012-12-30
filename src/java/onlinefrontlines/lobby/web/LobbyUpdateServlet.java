package onlinefrontlines.lobby.web;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import onlinefrontlines.lobby.LobbyState;
import onlinefrontlines.lobby.LobbyStateCache;
import onlinefrontlines.lobby.LobbyUser;
import onlinefrontlines.userstats.UserRank;
import onlinefrontlines.utils.Tools;
import onlinefrontlines.auth.AutoAuth;
import onlinefrontlines.auth.User;
import onlinefrontlines.web.*;
import onlinefrontlines.lobby.Country;
import onlinefrontlines.lobby.TextMessage;
import onlinefrontlines.lobby.actions.Action;
import onlinefrontlines.profiler.Profiler;
import onlinefrontlines.profiler.Sampler;

/**
 * This servlet is being called by the lobby flash application to update the state
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
public final class LobbyUpdateServlet extends HttpServlet 
{
	private static final long serialVersionUID = 0;

    /**
     * Output error
     */
    public void printError(PrintWriter out, int code)
    {
    	out.print("<response><code>");
    	out.print(code);
    	out.print("</code></response>");
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
	        
	        // Get lobby
	        int lobbyId; 
	        try 
	        { 
	        	lobbyId = Integer.parseInt(request.getParameter("lobbyId")); 
	        } 
	        catch (NumberFormatException e) 
	        { 
	        	lobbyId = 0; 
	        }
	        
	        // Get change count
	        int changeCount; 
	        try 
	        { 
	        	changeCount = Integer.parseInt(request.getParameter("changeCount")); 
	        } 
	        catch (NumberFormatException e) 
	        { 
	        	changeCount = 0; 
	        }
	                
	        // Get other parameters
	        String requestedAction = request.getParameter("requestedAction");		
	        
	        try
	        {
		        // Get lobby that the user is in
				LobbyState lobbyState = LobbyStateCache.getInstance().get(lobbyId);
				if (lobbyState == null)
		    	{
		    		printError(out, -2);
		    		return;
		    	}
				
		    	// Check required level
				int level = user != null? UserRank.getLevel(user.id) : 0;
		    	if (level < lobbyState.lobbyConfig.minRequiredLevel
		    		|| (lobbyState.lobbyConfig.maxLevel >= 0 && level > lobbyState.lobbyConfig.maxLevel))
		    	{
		    		printError(out, -3);
		    		return;
		    	}
		
		    	synchronized (lobbyState)
				{    		
		    		if (user != null)
		    		{
			    		// Get user in lobby
		    			LobbyUser lobbyUser = lobbyState.getOrCreateLobbyUser(user);
			    		if (lobbyUser == null)
			        	{
			    			// No more room
			        		printError(out, -4);
			        		return;
			        	}
		    		
			    		// Mark user still active
						lobbyUser.markConnected();
						
						// Execute action
						if (requestedAction != null)
						{
							try
							{
				    			// Split parameters
					    		String[] params = requestedAction.split(",");
			
					    		// Determine action
					    		Action action = Action.createAction(params[0]);
					    		action.setLobbyState(lobbyState);
					    		action.setLobbyUser(lobbyUser);
					    		
					    		// Parse action
					    		action.fromString(params);
					    		
					    		// Execute action
					    		action.doAction();
							}
							catch (Exception e)
							{
								Tools.logException(e);
							}    
						}
		    		}
		
					// Mark changed
					LobbyStateCache.getInstance().put(lobbyId, lobbyState);
					
					// Render output
			    	out.print("<response><code>0</code><cnty>");
			    	for (Country c : lobbyState.getChangedCountries(changeCount))
			    	{
			    		out.print("<c>");
			    		out.print(c.toString());
			    		out.print("</c>");
			    	}
			    	out.print("</cnty><usrs>");
			    	for (LobbyUser u : lobbyState.getChangedUsers(changeCount))
			    	{
			    		out.print("<u>");
			    		out.print(u.toString());
			    		out.print("</u>");
			    	}
			    	out.print("</usrs><msgs>");
			    	for (TextMessage m : lobbyState.getChangedTextMessages(changeCount))
			    	{
			    		out.print("<m>");
			    		out.print(m.toString());
			    		out.print("</m>");
			    	}
			    	out.print("</msgs><cgct>");
		    		out.print(lobbyState.getCurrentChangeCount());
			    	out.print("</cgct><ctim>");
			    	out.print(lobbyState.creationTime);
			    	out.print("</ctim></response>");
				}
	        }
	        catch (Exception e)
	        {
	    		Tools.logException(e);
	    		printError(out, -1);
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

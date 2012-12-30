package onlinefrontlines.tv.web;

import java.sql.SQLException;
import java.util.ArrayList;
import onlinefrontlines.web.*;
import onlinefrontlines.tv.*;
import onlinefrontlines.utils.Tools;

/**
 * This action shows the top matches of the day
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
public class TVAction extends WebAction
{
	/**
	 * The top match
	 */
	private TopMatches.Match topMatch;
	
	public TopMatches.Match getTopMatch()
	{
		if (matches == null)
			initMatches();
		return topMatch;
	}
	
	/**
	 * Runner up matches
	 */
	private ArrayList<TopMatches.Match> matches;
	
	public ArrayList<TopMatches.Match> getMatches()
	{
		if (matches == null)
			initMatches();
		return matches;
	}
		
	/**
     * Determine matches
     */
    public void initMatches() 
    {
    	try
    	{
	    	// Get top matches
	    	matches = TopMatchesDAO.getTopMatches().matches;
	    	
	    	// First one is THE top match
	    	if (matches.size() > 0)
	    		topMatch = matches.get(0);
    	}
    	catch (SQLException e)
    	{
			Tools.logException(e);
 
    		// Create empty list
    		matches = new ArrayList<TopMatches.Match>();
    	}
    }
}


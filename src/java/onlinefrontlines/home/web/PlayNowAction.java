package onlinefrontlines.home.web;

import java.sql.SQLException;
import onlinefrontlines.web.*;
import onlinefrontlines.game.GameStateDAO;
import onlinefrontlines.utils.*;

/**
 * This displays the play now page
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
public class PlayNowAction extends WebAction
{
	/**
	 * Indicates if there is a game to continue
	 */ 
	public boolean canContinueGame; 
	
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	try
    	{
    		canContinueGame = user != null && GameStateDAO.getNumberOfGamesToContinue(user.id) > 0;
    	}
    	catch (SQLException e)
    	{
    		canContinueGame = false;
    		
    		Tools.logException(e);
    	}
    	
    	return getSuccessView();
    }
}
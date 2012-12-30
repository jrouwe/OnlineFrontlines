package onlinefrontlines.game.web;

import onlinefrontlines.web.*;
import java.util.ArrayList;
import onlinefrontlines.game.*;

/**
 * This action shows the game results
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
public class GameResultsAction extends WebAction 
{
	public ArrayList<GameStateDAO.GameResultsByMap> byMap;
	public ArrayList<GameStateDAO.GameResultsByCountry> byCountry;
	
    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	byMap = GameStateDAO.getGameResultsByMap();
    	byCountry = GameStateDAO.getGameResultsByCountryConfig();
    	
        return getSuccessView();
    }
}

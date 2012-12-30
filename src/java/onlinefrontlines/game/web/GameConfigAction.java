package onlinefrontlines.game.web;

import java.util.*;
import onlinefrontlines.web.*;
import onlinefrontlines.game.*;

/**
 * This action shows the game settings
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
public class GameConfigAction extends WebAction 
{	
    /**
     * Access to all terrain types
     */
    public Collection<TerrainConfig> getTerrain()
    {
    	return TerrainConfig.allTerrain;
    }

 	/**
 	 * Access to all unit types
 	 */
    public Collection<UnitConfig> getUnits()
    {
    	return UnitConfig.allUnits;
    }
    
    /**
     * Access to the movement cost table
     */
    public String[][] movementCostTable;

    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	// Calculate movement table
    	movementCostTable = new String[UnitConfig.allUnits.size()][TerrainConfig.allTerrain.size() + 1];
    	
		for (int i = 0; i < UnitConfig.allUnits.size(); ++i)
		{
			UnitConfig u = UnitConfig.allUnits.get(i);
			
			movementCostTable[i][0] = u.name;

	    	for (int j = 0; j < TerrainConfig.allTerrain.size(); ++j)
	    	{
	    		TerrainConfig t = TerrainConfig.allTerrain.get(j);
	    		
    			String result = "X";
    			UnitMovementCostProperties p = u.movementCostProperties.get(t.id);    			
    			if (p != null)
    				result = Integer.toString(p.movementCost);

    			movementCostTable[i][j + 1] = result;
    		}
    	}
   		
        return getSuccessView();
    }
}

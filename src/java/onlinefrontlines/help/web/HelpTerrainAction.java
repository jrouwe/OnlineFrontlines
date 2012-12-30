package onlinefrontlines.help.web;

import java.util.*;
import onlinefrontlines.game.*;
import onlinefrontlines.web.*;

/**
 * This action shows help for a terrain type
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
public class HelpTerrainAction extends WebAction 
{	
	/**
	 * Selected terrain id
	 */
	public int terrainId = 1;
		
	/**
	 * Get selected terrain config
	 */
	public TerrainConfig getSelectedTerrain()
	{
		for (TerrainConfig c : TerrainConfig.allTerrain)
			if (c.id == terrainId)
				return c;
		
		return null;
	}

	/**
	 * Get description for selected terrain
	 */
	public String getSelectedTerrainDescription()
	{
		return getText("terrainDescription" + getSelectedTerrain().id);
	}
	
    /**
     * Access to all terrain types
     */
    public Collection<TerrainConfig> getTerrain()
    {
    	return TerrainConfig.allTerrain;
    }
    
    /**
     * Helper class to indicate unit movement per unit type
     */
    public static class UnitMovement
    {
    	/**
    	 * Name of the unit
    	 */
    	public String name;
    	
    	/**
    	 * Max amount of tiles the unit can move
    	 */
    	public int maxMovement;
    	
    	/**
    	 * Name of the unit
    	 */
    	public String getName()
    	{
    		return name;
    	}
    	
    	/**
    	 * Max amount of tiles the unit can move
    	 */
    	public int getMaxMovement()
    	{
    		return maxMovement;
    	}
    }
    
    /**
     * Get max movement for all unit types
     */
    public ArrayList<UnitMovement> getUnitMovement()
    {
    	TerrainConfig t = getSelectedTerrain();
    	
    	ArrayList<UnitMovement> all = new ArrayList<UnitMovement>();

    	for (UnitConfig u : UnitConfig.allUnits)
    	{
    		int maxMovement = u.movementPoints / u.getMovementCost(t);
    		if (maxMovement > 0)
    		{
    			UnitMovement m = new UnitMovement();
    			m.name = u.name;
    			m.maxMovement = maxMovement;
    			all.add(m);
    		}
    	}
    	
    	return all;
    }
}

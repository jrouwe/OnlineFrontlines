package onlinefrontlines.help.web;

import java.util.*;
import onlinefrontlines.web.*;
import onlinefrontlines.game.*;

/**
 * This action shows help for a unit
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
public class HelpUnitAction extends WebAction 
{
	/**
	 * Selected unit id
	 */
	public int unitId = -1;
	
	/**
	 * Selected unit class
	 */
	public int unitClass = UnitClass.toInt(UnitClass.land); 
	
	/**
	 * Units to be displayed in the index
	 */
	public ArrayList<UnitConfig> units = new ArrayList<UnitConfig>();

	/**
	 * Get selected unit config
	 */
	public UnitConfig getSelectedUnit()
	{
		return UnitConfig.allUnitsMap.get(unitId);
	}

	/**
	 * Get description for selected unit
	 */
	public String getSelectedUnitDescription()
	{
		return getText("unitDescription" + getSelectedUnit().id);
	}
	
 	/**
 	 * Access to all unit types
 	 */
    public Collection<UnitConfig> getUnits()
    {
    	return units;
    }
    
    /**
     * Execute action
     */
    protected WebView execute() throws Exception
    {
    	// Determine units in left menu
    	for (UnitConfig c : UnitConfig.allUnits)
    		if (UnitClass.toInt(c.unitClass) == unitClass)
    			units.add(c);

    	// Select first unit if no unit selected
    	if (unitId < 0 && units.size() > 0)
    		unitId = units.get(0).id;
    	
    	return getSuccessView();
    }
}

package onlinefrontlines.game.web;

import onlinefrontlines.game.*;
import onlinefrontlines.taglib.CacheTag;
import onlinefrontlines.web.*;
import org.apache.log4j.Logger;
import java.util.ArrayList;

/**
 * This action starts editing a unit.
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
public class UnitEditAction extends WebAction 
{
	private static final Logger log = Logger.getLogger(UnitEditAction.class);

	/**
	 * Id of the unit to edit
	 */
    public int unitId;
    
    /**
     * Unit config we're editing
     */
    public UnitConfig unitConfig;
    
    /**
     * Unit strength
     */
    public static class Strength
    {
    	public int id;
    	public String name;
    	public int maxStrengthWithAmmo;
    	public int maxStrengthWithoutAmmo;
    	public int attackRange;
    	
    	public int getId()
    	{
    		return id;
    	}
    	
    	public String getName()
    	{
    		return name;
    	}
    	
    	public int getMaxStrengthWithAmmo()
    	{
    		return maxStrengthWithAmmo;
    	}
    	
    	public int getMaxStrengthWithoutAmmo()
    	{
    		return maxStrengthWithoutAmmo;
    	}
    	
    	public int getAttackRange()
    	{
    		return attackRange;
    	}
    }
    
    public ArrayList<Strength> strengthList = new ArrayList<Strength>();
    
    /**
     * Movement cost
     */
    public static class Cost
    {
    	public int id;
    	public String name;
    	public int cost;
    	
    	public int getId()
    	{
    		return id;
    	}
    	
    	public String getName()
    	{
    		return name;
    	}
    	
    	public int getCost()
    	{
    		return cost;
    	}
    }
    
    public ArrayList<Cost> costList = new ArrayList<Cost>();
    
    /**
     * Containable units
     */    
    public static class Containable
    {
    	public int id;
    	public String name;
    	public boolean containable;

    	public int getId()
    	{
    		return id;
    	}
    	
    	public String getName()
    	{
    		return name;
    	}
    	
    	public boolean getContainable()
    	{
    		return containable;
    	}
    }
    
    public ArrayList<Containable> containableList = new ArrayList<Containable>();
    
    /**
     * Get parameter 
     * 
     * @param name Name of the parameter to look for
     * @param def Default value
     * @param min Minimal value
     * @param max Maximal value
     * @param replacementForX If value is 'X' this is the value it will be replaced with
     * @return Integer value of parameter
     */
    public int getParameter(String name, int def, int min, int max, int replacementForX)
    {
		// See if request overrides this parameter
		String p = request.getParameter(name);
		if (p == null)
			return def;
		
		// Check if parameter is X
		if (p.toLowerCase().equals("x"))
			return replacementForX;
		
		// Convert to int
		int tmp;
		try
		{
			tmp = Integer.parseInt(p);
		}
		catch (NumberFormatException e)
		{
    		addFieldError(name, getText("invalidValue"));
    		return def;
		}
		
		// Check range
		if (tmp < min || tmp > max)
		{
    		addFieldError(name, getText("invalidValue"));
    		return def;
		}
		
		return tmp;
    }
    
    public boolean getParameter(String name, boolean def)
    {
		// See if request overrides this parameter
		String p = request.getParameter(name);
		if (p == null)
			return def;
		
		// Convert to bool
		try
		{
			return Boolean.parseBoolean(p);
		}
		catch (NumberFormatException e)
		{
    		addFieldError(name, getText("invalidValue"));
    		return def;
		}
    }

    /**
     * Execute the action
     */
    protected WebView execute() throws Exception 
    {
    	log.info("User '" + user.id + "' edited unit '" + unitId + "'");

    	// Load config
    	unitConfig = UnitConfig.allUnitsMap.get(unitId);
    	if (unitConfig == null)
    	{
    		addActionError(getText("unitDoesNotExist"));
    		return getErrorView();
    	}
    	
    	// Get parameters
    	int tmpMaxArmour = getParameter("maxArmour", unitConfig.maxArmour, 0, Integer.MAX_VALUE, 0);
    	int tmpMaxAmmo = getParameter("maxAmmo", unitConfig.maxAmmo, 0, Integer.MAX_VALUE, 0);
    	int tmpVisionRange = getParameter("visionRange", unitConfig.visionRange, 0, Integer.MAX_VALUE, 0);
    	int tmpMovementPoints = getParameter("movementPoints", unitConfig.movementPoints, 0, Integer.MAX_VALUE, 0);
    	int tmpActions = getParameter("actions", unitConfig.actions, 0, Integer.MAX_VALUE, 0);
    	int tmpContainerMaxUnits = getParameter("containerMaxUnits", unitConfig.containerMaxUnits, 0, Integer.MAX_VALUE, 0);
    	int tmpContainerArmourPercentagePerTurn = getParameter("containerArmourPercentagePerTurn", unitConfig.containerArmourPercentagePerTurn, 0, 100, 0);
    	int tmpContainerAmmoPercentagePerTurn = getParameter("containerAmmoPercentagePerTurn", unitConfig.containerAmmoPercentagePerTurn, 0, 100, 0);
    	int tmpVictoryPoints = getParameter("victoryPoints", unitConfig.victoryPoints, 0, Integer.MAX_VALUE, 0);
    	int tmpVictoryCategory = getParameter("victoryCategory", unitConfig.victoryCategory, 0, 31, 0);
    	int tmpBeDetectedRange = getParameter("beDetectedRange", unitConfig.beDetectedRange, 1, 1000, 1000);
    	
    	// Check if anything changed
    	boolean hasChanged = tmpMaxArmour != unitConfig.maxArmour
	    	|| tmpMaxAmmo != unitConfig.maxAmmo
	    	|| tmpVisionRange != unitConfig.visionRange
	    	|| tmpMovementPoints != unitConfig.movementPoints
	    	|| tmpActions != unitConfig.actions
	    	|| tmpContainerMaxUnits != unitConfig.containerMaxUnits
	    	|| tmpContainerArmourPercentagePerTurn != unitConfig.containerArmourPercentagePerTurn
	    	|| tmpContainerAmmoPercentagePerTurn != unitConfig.containerAmmoPercentagePerTurn
	    	|| tmpVictoryPoints != unitConfig.victoryPoints
	    	|| tmpVictoryCategory != unitConfig.victoryCategory
	    	|| tmpBeDetectedRange != unitConfig.beDetectedRange;

    	if (hasChanged)
    	{
    		// Store new values
	    	unitConfig.maxArmour = tmpMaxArmour;
	    	unitConfig.maxAmmo = tmpMaxAmmo;
	    	unitConfig.visionRange = tmpVisionRange;
	    	unitConfig.movementPoints = tmpMovementPoints;
	    	unitConfig.actions = tmpActions;
	    	unitConfig.containerMaxUnits = tmpContainerMaxUnits;
	    	unitConfig.containerArmourPercentagePerTurn = tmpContainerArmourPercentagePerTurn;
	    	unitConfig.containerAmmoPercentagePerTurn = tmpContainerAmmoPercentagePerTurn;
	    	unitConfig.victoryPoints = tmpVictoryPoints;
	    	unitConfig.victoryCategory = tmpVictoryCategory;
	    	unitConfig.beDetectedRange = tmpBeDetectedRange;
    	}    	

    	// Determine strength
    	for (UnitClass c : new UnitClass[] { UnitClass.land, UnitClass.water, UnitClass.air })
    	{
    		UnitStrengthProperties sp = unitConfig.getStrengthProperties(c);

    		// See if request overrides this parameter
    		int unitClassInt = UnitClass.toInt(c);
    		int tmpMaxStrengthWithAmmo = getParameter("maxStrengthWithAmmo" + unitClassInt, sp.maxStrengthWithAmmo, 0, Integer.MAX_VALUE, 0);
    		int tmpMaxStrengthWithoutAmmo = getParameter("maxStrengthWithoutAmmo" + unitClassInt, sp.maxStrengthWithoutAmmo, 0, Integer.MAX_VALUE, 0);
    		int tmpAttackRange = getParameter("attackRange" + unitClassInt, sp.attackRange, 0, Integer.MAX_VALUE, 0);
    		if (tmpMaxStrengthWithAmmo != sp.maxStrengthWithAmmo
    			|| tmpMaxStrengthWithoutAmmo != sp.maxStrengthWithoutAmmo
    			|| tmpAttackRange != sp.attackRange)
    		{
    			// Store new value
    			sp = new UnitStrengthProperties(c, tmpMaxStrengthWithAmmo, tmpMaxStrengthWithoutAmmo, tmpAttackRange);
    			unitConfig.setStrengthProperties(sp);
   				hasChanged = true;
    		}
    		
    		// Add to list
    		Strength s = new Strength();
    		s.id = unitClassInt;
    		s.name = c.toString();
    		s.maxStrengthWithAmmo = sp.maxStrengthWithAmmo;
    		s.maxStrengthWithoutAmmo = sp.maxStrengthWithoutAmmo;
    		s.attackRange = sp.attackRange;
    		strengthList.add(s);    		
    	}
    	
    	// Determine movement cost
    	for (TerrainConfig t : TerrainConfig.allTerrain)
    	{
    		// Get cost for this terrain type
    		int cost = unitConfig.getMovementCost(t);

			// See if request overrides this parameter
    		int tmpCost = getParameter("cost" + t.id, cost, 0, unitConfig.movementPoints + 1, unitConfig.movementPoints + 1);
			if (tmpCost != cost)
			{
				cost = tmpCost;
				unitConfig.setMovementCost(new UnitMovementCostProperties(t.id, cost));
				hasChanged = true;
			}

    		// Insert in list
    		Cost c = new Cost();
    		c.id = t.id;
    		c.name = t.name;
    		c.cost = cost;
    		costList.add(c);
    	}
    	
    	// Determine containable units
    	for (UnitConfig u : UnitConfig.allUnits)
    	{
    		// Get cost for this terrain type
    		boolean containable = unitConfig.containerUnitIds.contains(u.id);

			// See if request overrides this parameter
    		boolean tmpContainable = getParameter("containable" + u.id, containable);
			if (tmpContainable != containable)
			{
				containable = tmpContainable;
				int idx = unitConfig.containerUnitIds.indexOf(u.id);
				if (containable)
				{
					if (idx < 0)
						unitConfig.containerUnitIds.add(u.id);
				}
				else
				{
					if (idx >= 0)
						unitConfig.containerUnitIds.remove(idx);
				}
				hasChanged = true;
			}

    		// Insert in list
    		Containable c = new Containable();
    		c.id = u.id;
    		c.name = u.name;
    		c.containable = containable;
    		containableList.add(c);
    	}

    	// Check error
    	if (hasErrors())
    		return getInputView();

		// Save to database if changed
    	if (hasChanged)
    	{
    		UnitConfigDAO.save(unitConfig);
    		CacheTag.purgeAll();
    	}

    	return getSuccessView();
    }
}
package onlinefrontlines.game;

import java.util.*;
import java.sql.SQLException;
import onlinefrontlines.utils.DbQueryHelper;
import org.apache.log4j.Logger;

/**
 * This class communicates with the database and manages reading / writing UnitConfig objects
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
public class UnitConfigDAO 
{
	private static final Logger log = Logger.getLogger(UnitConfigDAO.class);

	/**
	 * Loads all UnitConfig objects from the database
	 * 
	 * @return A list of all UnitConfig objects in the database
	 * 
	 * @throws SQLException
	 */
	public static ArrayList<UnitConfig> loadAllUnits() throws SQLException
	{
		ArrayList<UnitConfig> units = new ArrayList<UnitConfig>();

    	DbQueryHelper helper = new DbQueryHelper();
    	DbQueryHelper helper2 = new DbQueryHelper();
        try
        {
        	// Find all units
	    	helper.prepareQuery("SELECT id, name, imageNumber, unitClass, maxArmour, maxAmmo, visionRange, movementPoints, actions, containerMaxUnits, containerArmourPercentagePerTurn, containerAmmoPercentagePerTurn, transformableToUnitId, transformableType, victoryPoints, description, isBase, victoryCategory, beDetectedRange FROM units");
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
		        // Construct UnitConfig object
		        UnitConfig unitConfig = new UnitConfig();
		        unitConfig.id = helper.getInt(1);
		        unitConfig.name = helper.getString(2);
		        unitConfig.imageNumber = helper.getInt(3);
		        unitConfig.unitClass = UnitClass.fromInt(helper.getInt(4));
		        unitConfig.maxArmour = helper.getInt(5);
		        unitConfig.maxAmmo = helper.getInt(6);
		        unitConfig.visionRange = helper.getInt(7);
		        unitConfig.movementPoints = helper.getInt(8);
		        unitConfig.actions = helper.getInt(9);
		        unitConfig.containerMaxUnits = helper.getInt(10);
		        unitConfig.containerArmourPercentagePerTurn = helper.getInt(11);
		        unitConfig.containerAmmoPercentagePerTurn = helper.getInt(12);
		        unitConfig.transformableToUnitId = helper.getInt(13);
		        unitConfig.transformableType = TransformableType.fromInt(helper.getInt(14));
		        unitConfig.victoryPoints = helper.getInt(15);
		        unitConfig.description = helper.getString(16);
		        unitConfig.isBase = helper.getInt(17) != 0;
		        unitConfig.victoryCategory = helper.getInt(18);
		        unitConfig.beDetectedRange = helper.getInt(19);
		        
		        // Get strength properties
		        helper2.prepareQuery("SELECT enemyUnitClass, strengthWithAmmo, strengthWithoutAmmo, attackRange FROM units_strength_properties WHERE unitId=?");
		        helper2.setInt(1, unitConfig.id);
		        helper2.executeQuery();
		        while (helper2.nextRecord())
		        {
		        	unitConfig.setStrengthProperties(new UnitStrengthProperties(UnitClass.fromInt(helper2.getInt(1)), helper2.getInt(2), helper2.getInt(3), helper2.getInt(4)));
		        }
		        
		        // Get set up on terrain
		        helper2.prepareQuery("SELECT terrainId FROM units_set_up_on WHERE unitId=?");
		        helper2.setInt(1, unitConfig.id);
		        helper2.executeQuery();
		        while (helper2.nextRecord())
		        	unitConfig.unitSetupOn.add(helper2.getInt(1));

		        // Get set up next to terrain
		        helper2.prepareQuery("SELECT terrainId FROM units_set_up_next_to WHERE unitId=?");
		        helper2.setInt(1, unitConfig.id);
		        helper2.executeQuery();
		        while (helper2.nextRecord())
		        	unitConfig.unitSetupNextTo.add(helper2.getInt(1));

		        // Get movement cost
		        helper2.prepareQuery("SELECT terrainId, movementCost FROM units_movement_cost WHERE unitId=?");
		        helper2.setInt(1, unitConfig.id);
		        helper2.executeQuery();
		        while (helper2.nextRecord())
		        {
		        	unitConfig.setMovementCost(new UnitMovementCostProperties(helper2.getInt(1), helper2.getInt(2)));
		        }
		        
		        // Get contained units
		        helper2.prepareQuery("SELECT containedUnitId FROM units_container WHERE containerUnitId=?");
		        helper2.setInt(1, unitConfig.id);
		        helper2.executeQuery();
		        while (helper2.nextRecord())
		        	unitConfig.containerUnitIds.add(helper2.getInt(1));
		        
		        // Add to list
		        units.add(unitConfig);
	    	}
        }
        finally
        {
        	helper.close();
        	helper2.close();
        }
        
        log.info("Loaded " + units.size() + " units");
        
        return units;
	}
	
	/**
	 * Save unit config
	 * 
	 * @param unitConfig Unit config to save
	 * @throws SQLException
	 */
	public static void save(UnitConfig unitConfig) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Update unit
	    	helper.prepareQuery("UPDATE units SET name=?, imageNumber=?, unitClass=?, maxArmour=?, maxAmmo=?, visionRange=?, movementPoints=?, actions=?, containerMaxUnits=?, containerArmourPercentagePerTurn=?, containerAmmoPercentagePerTurn=?, transformableToUnitId=?, transformableType=?, victoryPoints=?, description=?, isBase=?, victoryCategory=?, beDetectedRange=? WHERE id=?");
	        helper.setString(1, unitConfig.name);
	        helper.setInt(2, unitConfig.imageNumber);
	        helper.setInt(3, UnitClass.toInt(unitConfig.unitClass));
	        helper.setInt(4, unitConfig.maxArmour);
	        helper.setInt(5, unitConfig.maxAmmo);
	        helper.setInt(6, unitConfig.visionRange);
	        helper.setInt(7, unitConfig.movementPoints);
	        helper.setInt(8, unitConfig.actions);
	        helper.setInt(9, unitConfig.containerMaxUnits);
	        helper.setInt(10, unitConfig.containerArmourPercentagePerTurn);
	        helper.setInt(11, unitConfig.containerAmmoPercentagePerTurn);
	        helper.setInt(12, unitConfig.transformableToUnitId);
	        helper.setInt(13, TransformableType.toInt(unitConfig.transformableType));
	        helper.setInt(14, unitConfig.victoryPoints);
	        helper.setString(15, unitConfig.description);
	        helper.setInt(16, unitConfig.isBase? 1 : 0);
	        helper.setInt(17, unitConfig.victoryCategory);
	        helper.setInt(18, unitConfig.beDetectedRange);
	        helper.setInt(19, unitConfig.id);
	    	helper.executeUpdate();
	        
	    	// Delete previous strength properties
	        helper.prepareQuery("DELETE FROM units_strength_properties WHERE unitId=?");
	        helper.setInt(1, unitConfig.id);
	        helper.executeUpdate();
	        
	        // Insert new strength properties
	        for (UnitStrengthProperties usp : unitConfig.strengthProperties.values())
	        {
		        // Get strength properties
		        helper.prepareQuery("INSERT INTO units_strength_properties (unitId, enemyUnitClass, strengthWithAmmo, strengthWithoutAmmo, attackRange) VALUES (?, ?, ?, ?, ?)");
		        helper.setInt(1, unitConfig.id);
		        helper.setInt(2, UnitClass.toInt(usp.enemyUnitClass));
	        	helper.setInt(3, usp.maxStrengthWithAmmo);
	        	helper.setInt(4, usp.maxStrengthWithoutAmmo);
	        	helper.setInt(5, usp.attackRange);
		        helper.executeUpdate();
	        }

	    	// Delete previous set up on terrain
	        helper.prepareQuery("DELETE FROM units_set_up_on WHERE unitId=?");
	        helper.setInt(1, unitConfig.id);
	        helper.executeUpdate();
	        
	        // Insert set up on terrain
	        for (Integer suo : unitConfig.unitSetupOn)
	        {
	        	helper.prepareQuery("INSERT INTO units_set_up_on (unitId, terrainId) VALUES (?, ?)");
	        	helper.setInt(1, unitConfig.id);
	        	helper.setInt(2, suo);
	        	helper.executeUpdate();
	        }

	    	// Delete previous set up next to terrain
	        helper.prepareQuery("DELETE FROM units_set_up_next_to WHERE unitId=?");
	        helper.setInt(1, unitConfig.id);
	        helper.executeUpdate();

	        // Insert set up next to terrain
	        for (Integer sun : unitConfig.unitSetupNextTo)
	        {
	        	helper.prepareQuery("INSERT INTO units_set_up_next_to (unitId, terrainId) VALUES (?, ?)");
	        	helper.setInt(1, unitConfig.id);
	        	helper.setInt(2, sun);
	        	helper.executeUpdate();
	        }

	        // Delete previous movement costs
	        helper.prepareQuery("DELETE FROM units_movement_cost WHERE unitId=?");
	        helper.setInt(1, unitConfig.id);
	        helper.executeUpdate();
	        
	        // Insert new movement costs
	        for (UnitMovementCostProperties umc : unitConfig.movementCostProperties.values())
	        {
	        	helper.prepareQuery("INSERT INTO units_movement_cost (unitId, terrainId, movementCost) VALUES (?, ?, ?)");
	        	helper.setInt(1, unitConfig.id);
	        	helper.setInt(2, umc.terrainId);
	        	helper.setInt(3, umc.movementCost);
	        	helper.executeUpdate();
	        }

	        // Delete previous container
	        helper.prepareQuery("DELETE FROM units_container WHERE containerUnitId=?");
	        helper.setInt(1, unitConfig.id);
	        helper.executeUpdate();

	        // Insert set up next to terrain
	        for (Integer ctd : unitConfig.containerUnitIds)
	        {
	        	helper.prepareQuery("INSERT INTO units_container (containerUnitId, containedUnitId) VALUES (?, ?)");
	        	helper.setInt(1, unitConfig.id);
	        	helper.setInt(2, ctd);
	        	helper.executeUpdate();
	        }
        }
        finally
        {
        	helper.close();
        }
	}
}

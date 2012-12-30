package onlinefrontlines.game;

import java.util.*;
import java.sql.SQLException;
import onlinefrontlines.utils.DbQueryHelper;

/**
 * This class communicates with the database and manages reading/writing DeploymentConfig objects
 * 
 * @see onlinefrontlines.game.DeploymentConfig

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
public class DeploymentConfigDAO 
{
	/**
	 * Queries the database for all deployment configs
	 * 
	 * @param creatorUserId User id of creator of config, 0 to disable filter
	 * @return All deployment configs in the database with only their id, name and creatorUserId filled in
	 * @throws SQLException
	 */
	public static ArrayList<DeploymentConfig> list(int creatorUserId) throws SQLException
	{
		ArrayList<DeploymentConfig> deployments = new ArrayList<DeploymentConfig>();

    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find all deployments
	    	helper.prepareQuery("SELECT id, name, creatorUserId FROM deployment WHERE (? OR creatorUserId=?)");
	    	helper.setInt(1, creatorUserId != 0? 0 : 1);
	    	helper.setInt(2, creatorUserId);
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
		        // Construct DeploymentConfig object
		        DeploymentConfig deploymentConfig = new DeploymentConfig();
		        deploymentConfig.id = helper.getInt(1);
		        deploymentConfig.name = helper.getString(2);
		        deploymentConfig.creatorUserId = helper.getInt(3);
		        deployments.add(deploymentConfig);
	    	}
        }
        finally
        {
        	helper.close();
        }
        
        return deployments;
	}
	
	/**
	 * Inserts a new deployment config in the database
	 * 
	 * @param deploymentConfig The deployment config to store in the database
	 * @throws SQLException
	 */
	public static void create(DeploymentConfig deploymentConfig) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Create deployment record
	    	helper.prepareQuery("INSERT INTO deployment (name, creatorUserId) VALUES (?, ?)");
	    	helper.setString(1, deploymentConfig.name);
	    	helper.setInt(2, deploymentConfig.creatorUserId);
	    	helper.executeUpdate();

	    	// Set id
	    	ArrayList<Integer> generatedKeys = helper.getGeneratedKeys();
	    	deploymentConfig.id = generatedKeys.get(0);
	    		    	
	    	// Insert amounts
	    	for (DeploymentAmount a : deploymentConfig.deploymentAmounts)
	    	{
	    		helper.prepareQuery("INSERT INTO deployment_amount (deploymentId, unitId, amount) VALUES (?, ?)");
	    		helper.setInt(1, deploymentConfig.id);
	    		helper.setInt(2, a.unitId);
	    		helper.setInt(3, a.amount);
	    		helper.executeUpdate();
	    	}	    	
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Remove deployment config from database
	 * 
	 * @param id ID of the deployment config to remove
	 * @throws SQLException
	 */
	public static void delete(int id) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Remove deployment record
	    	helper.prepareQuery("DELETE FROM deployment WHERE id=?");
	    	helper.setInt(1, id);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Load full deployment config from the database
	 * 
	 * @param id ID of the deployment config to load
	 * @return The deployment config that was loaded
	 * @throws SQLException
	 */
	public static DeploymentConfig load(int id) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find deployment config record
	    	helper.prepareQuery("SELECT name, creatorUserId FROM deployment WHERE id=?");
	    	helper.setInt(1, id);
	    	helper.executeQuery();
	    	
	       	// Validate deployment config exists
	    	if (!helper.nextRecord())
	    		return null;
	    	
	    	// Construct deployment config object
	        DeploymentConfig deploymentConfig = new DeploymentConfig();
	        deploymentConfig.id = id;
	        deploymentConfig.name = helper.getString(1);
	        deploymentConfig.creatorUserId = helper.getInt(2);
	        
	        // Get unit amounts
	        helper.prepareQuery("SELECT unitId, amount FROM deployment_amount WHERE deploymentId=?");
	        helper.setInt(1, id);
	        helper.executeQuery();	        
	        while (helper.nextRecord())
	        {
	        	DeploymentAmount a = new DeploymentAmount(helper.getInt(1), helper.getInt(2));
	        	deploymentConfig.deploymentAmounts.add(a);
	        }
	        
	        return deploymentConfig;
	    }
        finally
        {
        	helper.close();
        }
	}
	
	/**
	 * Updates a previously created deployment config in the database
	 * 
	 * @param deploymentConfig The deployment config to save
	 * @throws SQLException
	 */
	public static void save(DeploymentConfig deploymentConfig) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Update deployment record
	    	helper.prepareQuery("UPDATE deployment SET name=?, creatorUserId=? WHERE id=?");
	    	helper.setString(1, deploymentConfig.name);
	    	helper.setInt(2, deploymentConfig.creatorUserId);
	    	helper.setInt(3, deploymentConfig.id);
	    	helper.executeUpdate();
	    	
	    	// Remove all unit amount records
	    	helper.prepareQuery("DELETE FROM deployment_amount WHERE deploymentId=?");
	    	helper.setInt(1, deploymentConfig.id);
	    	helper.executeUpdate();

	    	// Insert new amounts
	    	for (DeploymentAmount a : deploymentConfig.deploymentAmounts)
	    	{
	    		helper.prepareQuery("INSERT INTO deployment_amount (deploymentId, unitId, amount) VALUES (?, ?, ?)");
	    		helper.setInt(1, deploymentConfig.id);
	    		helper.setInt(2, a.unitId);
	    		helper.setInt(3, a.amount);
	    		helper.executeUpdate();
	    	}	    	
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}	
	
	/**
	 * Derive publish state from linked countries
	 * 
	 * @param deploymentId Deployment id to check for
	 * @return Publish state
	 */
	public static PublishState getPublishState(int deploymentId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery("SELECT MAX(publishState) FROM country_configs WHERE deploymentConfigId1=? OR deploymentConfigId2=?");
	    	helper.setInt(1, deploymentId);
	    	helper.setInt(2, deploymentId);
	    	helper.executeQuery();    	
	    	helper.nextRecord();
	    	return PublishState.fromInt(helper.getInt(1));    	
	    }
        finally
        {
        	helper.close();
        }		
	}
}

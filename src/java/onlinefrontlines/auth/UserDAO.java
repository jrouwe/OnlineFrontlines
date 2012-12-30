package onlinefrontlines.auth;

import java.util.ArrayList;
import java.util.Calendar;
import java.sql.SQLException;
import onlinefrontlines.Army;
import onlinefrontlines.utils.DbQueryHelper;

/**
 * This class communicates with the database and manages reading/writing of the User object
 * 
 * @see onlinefrontlines.auth.User
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
public class UserDAO 
{
	/**
	 * Find user id by username in the database
	 * 
	 * @param username Name of the user to find (case insensitive)
	 * @return Id of the user or 0 if not found 
	 * @throws SQLException
	 */
	public static int findUserId(String username) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find user record
	    	helper.prepareQuery("SELECT id FROM users WHERE username=?");
	    	helper.setString(1, username);
	    	helper.executeQuery();
	    	
	    	// Validate user exists
	    	if (!helper.nextRecord())
	    		return 0;
	    	
	    	return helper.getInt(1);
        }
        finally
        {
        	helper.close();
        }
	}
	
	/**
	 * Find user id by facebook id in the database
	 * 
	 * @param facebookId Facebook user id
	 * @return Id of the user or 0 if not found 
	 * @throws SQLException
	 */
	public static int findUserByFacebookId(String facebookId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find user record
	    	helper.prepareQuery("SELECT id FROM users WHERE facebookId=?");
	    	helper.setString(1, facebookId);
	    	helper.executeQuery();
	    	
	    	// Validate user exists
	    	if (!helper.nextRecord())
	    		return 0;
	    	
	    	return helper.getInt(1);
        }
        finally
        {
        	helper.close();
        }
	}

	/**
	 * Find user by id in the database and get its properties
	 * 
	 * @param id Id of the user
	 * @return The user object or null if the user didn't exist 
	 * @throws SQLException
	 */
	public static User find(int id) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find user record
	    	helper.prepareQuery("SELECT id, facebookId, username, realname, email, country, city, website, isAdmin, army, creationTime, receiveGameEventsByMail, autoDefendOwnedCountry, showHelpBalloons, autoDeclineFriendlyDefender FROM users WHERE id=?");
	    	helper.setInt(1, id);
	    	helper.executeQuery();
	    	
	    	// Validate user exists
	    	if (!helper.nextRecord())
	    		return null;
	    	
	        // Construct user object
	        User user = new User();
	        user.id = helper.getInt(1);
	        user.facebookId = helper.getString(2);
	        user.username = helper.getString(3);
	        user.realname = helper.getString(4);
	        user.email = helper.getString(5);
	        user.country = helper.getString(6);
	        user.city = helper.getString(7);
	        user.website = helper.getString(8);
	        user.isAdmin = helper.getInt(9) != 0;
	        user.army = Army.fromInt(helper.getInt(10));
	        user.creationTime = helper.getLong(11);
	        user.receiveGameEventsByMail = helper.getInt(12) != 0;
	        user.autoDefendOwnedCountry = helper.getInt(13) != 0;
	        user.showHelpBalloons = helper.getInt(14) != 0;
	        user.autoDeclineFriendlyDefender = helper.getInt(15) != 0;
	        
	        return user;
        }
        finally
        {
        	helper.close();
        }
	}
	
	/**
	 * Create a new user in the database
	 * 
	 * @param user Properties of the user to insert
	 * @throws SQLException
	 */
	public static void create(User user) throws SQLException
	{
		user.creationTime = Calendar.getInstance().getTime().getTime();
		
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Create user record
	    	helper.prepareQuery("INSERT INTO users (facebookId, username, realname, email, country, city, website, army, creationTime, receiveGameEventsByMail, autoDefendOwnedCountry, showHelpBalloons, autoDeclineFriendlyDefender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	    	helper.setString(1, user.facebookId);
	    	helper.setString(2, user.username);
	    	helper.setString(3, user.realname);
	    	helper.setString(4, user.email);
	    	helper.setString(5, user.country);
	    	helper.setString(6, user.city);
	    	helper.setString(7, user.website);
	    	helper.setInt(8, Army.toInt(user.army));
	    	helper.setLong(9, user.creationTime);
	    	helper.setInt(10, user.receiveGameEventsByMail? 1 : 0);
	    	helper.setInt(11, user.autoDefendOwnedCountry? 1 : 0);
	    	helper.setInt(12, user.showHelpBalloons? 1 : 0);
	    	helper.setInt(13, user.autoDeclineFriendlyDefender? 1 : 0);
	    	helper.executeUpdate();

	    	// Set id
	    	ArrayList<Integer> generatedKeys = helper.getGeneratedKeys();
	    	user.id = generatedKeys.get(0);
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }
	}

	/**
	 * Update user properties in database (realname, country, city, website)
	 * 
	 * @param user User to update
	 * @throws SQLException
	 */
	public static void updateProfile(User user) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Update user record
	    	helper.prepareQuery("UPDATE users SET realname=?, country=?, city=?, website=?, autoDefendOwnedCountry=?, showHelpBalloons=?, autoDeclineFriendlyDefender=?, email=?, receiveGameEventsByMail=? WHERE id=?");
	    	helper.setString(1, user.realname);
	    	helper.setString(2, user.country);
	    	helper.setString(3, user.city);
	    	helper.setString(4, user.website);
	    	helper.setInt(5, user.autoDefendOwnedCountry? 1 : 0);
	    	helper.setInt(6, user.showHelpBalloons? 1 : 0);
	    	helper.setInt(7, user.autoDeclineFriendlyDefender? 1 : 0);
	    	helper.setString(8, user.email);
	    	helper.setInt(9, user.receiveGameEventsByMail? 1 : 0);
	    	helper.setInt(10, user.id);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }
	}

	/**
	 * Update a users facebook id
	 * 
	 * @param user User to update
	 * @throws SQLException
	 */
	public static void updateFacebookId(User user) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery("UPDATE users SET facebookId=? WHERE id=?");
	    	helper.setString(1, user.facebookId);
	    	helper.setInt(2, user.id);
	    	
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Update user's army in the database
	 * 
	 * @param user User to update
	 * @throws SQLException
	 */
	public static void updateArmy(User user) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Update user record
	    	helper.prepareQuery("UPDATE users SET army=? WHERE id=?");
	    	helper.setInt(1, Army.toInt(user.army));
	    	helper.setInt(2, user.id);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }
	}

	/**
	 * Finds all users that have a particular e-mail registered
	 * 
	 * @param email Email for registered user
	 * @return Array of user ids that have registered with this email
	 */
	public static ArrayList<Integer> findUsersByEmail(String email) throws SQLException
	{
		DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	// Find users by email
	    	helper.prepareQuery("SELECT id FROM users WHERE LOWER(email)=?");
	    	helper.setString(1, email.toLowerCase());
	    	helper.executeQuery();
	    	
	    	// Add users
    		ArrayList<Integer> users = new ArrayList<Integer>();
	    	while (helper.nextRecord())
	    		users.add(helper.getInt(1));
	    	return users;
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Get number of registered members
	 */
	public static int getNumMembers()
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery("SELECT COUNT(1) FROM users");
	    	helper.executeQuery();	    	
	    	helper.nextRecord();
	    	return helper.getInt(1);
        }
        catch (SQLException e)
        {
        	return 0;
        }
        finally
        {
        	helper.close();
        }
	}
	
	/**
	 * Helper class that stores referrer information
	 */
	public static class Referrer
	{
		private int count;
		private String url;
		
		public Referrer(int count, String url)
		{
			this.count = count;
			this.url = url;
		}
		
		public int getCount()
		{
			return count;
		}
		
		public String getUrl()
		{
			return url == null || url.isEmpty()? "<empty>" : url;
		}
	}
}

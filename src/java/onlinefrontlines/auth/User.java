package onlinefrontlines.auth;

import java.io.UnsupportedEncodingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import onlinefrontlines.Army;
import onlinefrontlines.utils.GlobalProperties;
import onlinefrontlines.utils.Tools;
import java.util.Random;

/**
 * This class mirrors a user record from the database
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
public class User
{
	/**
	 * User id in the database
	 */
	public int id = -1;
	
	/**
	 * Facebook user id
	 */
	public String facebookId;
	
	/**
	 * Display name of the user
	 */
	public String username;
	
	/**
	 * Real name of user
	 */
	public String realname;
	
	/**
	 * Email address for the user
	 */
	public String email;
	
	/**
	 * User wants to receive email notifications for game events
	 */
	public boolean receiveGameEventsByMail = true;
	
	/**
	 * Country the user lives in
	 */
	public String country;
	
	/**
	 * City the user lives in
	 */
	public String city;
	
	/**
	 * Web site of the user
	 */
	public String website;
	
	/**
	 * Army the user is registered to (or none when user has not yet chosen)
	 */
	public Army army = Army.none;
	
	/**
	 * If the user is an administrator
	 */
	public boolean isAdmin = false;
	
	/**
	 * Time user was created
	 */
	public long creationTime;
	
	/**
	 * If user automatically declines friendly defenders on the world map
	 */
	public boolean autoDeclineFriendlyDefender = false;
	
	/**
	 * If user automatically defends countries that he owns on the world map
	 */
	public boolean autoDefendOwnedCountry = false;
	
	/**
	 * Show help balloons in flash applications?
	 */
	public boolean showHelpBalloons = true;
	
	/**
	 * Random generator
	 */
	private static final Random random = new Random();
	
	/**
	 * Get id
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Get facebook id
	 */
	public String getFacebookId()
	{
		return facebookId;
	}

	/**
	 * Get user name
	 */
	public String getUsername()
	{
		return username;
	}
	
	/**
	 * Check if user has a real name filled in
	 */
	public boolean hasRealName()
	{
		return realname != null && realname.length() > 0;
	}

	/**
	 * Get real name of user
	 */
	public String getRealname()
	{
		return realname;
	}
	
	/**
	 * Returns real name if available, otherwise user name
	 */
	public String getFriendlyName()
	{
		return hasRealName()? (realname + " (" + username + ")") : username;
	}
	
	/**
	 * Get country user lives in
	 */
	public String getCountry()
	{
		return country;
	}
	
	/**
	 * Get city user lives in
	 */
	public String getCity()
	{
		return city;
	}

	/**
	 * Determine a new army for this user (used to create a new user)
	 */
	public void determineArmy()
	{
		String defaultArmy = GlobalProperties.getInstance().getString("default.army");
		if (defaultArmy != null && defaultArmy.equals("random"))
			army = random.nextBoolean()? Army.red : Army.blue;
		else
			army = Army.fromString(defaultArmy);
	}

	/**
	 * Get army
	 */
	public Army getArmy()
	{
		return army;
	}
	
	/**
	 * Get army as int
	 */
	public int getArmyAsInt()
	{
		return Army.toInt(army);
	}
	
	/**
	 * Check if user selected army
	 */
	public boolean getHasArmy()
	{
		return army != Army.none;
	}
	
	/**
	 * Check if user is admin
	 */
	public boolean getIsAdmin()
	{
		return isAdmin;
	}
	
	/**
	 * Check if user has an email filled in
	 */
	public boolean getHasEmail()
	{
		return email != null && email.length() > 0;
	}
		
	/**
	 * Return email
	 */
	public String getEmail()
	{
		return email;
	}
	
	/**
	 * Get email address
	 */
	public InternetAddress getEmailAsInternetAddress()
	{
		if (getHasEmail())
		{
	    	try
	    	{
	        	InternetAddress address;
	    		address = new InternetAddress(email, true);
	   			address.setPersonal(getFriendlyName(), "UTF-8");
	   			return address;
	    	}
	    	catch (AddressException e)
	    	{
	    	}
	    	catch (UnsupportedEncodingException e)
	    	{
	    		Tools.logException(e);
	    	}
		}
    	
		return null;
	}
	
	/**
	 * Receive game events
	 */
	public boolean getReceiveGameEventsByMail()
	{
		return receiveGameEventsByMail;
	}

	/**
	 * Get website
	 */
	public String getWebsite()
	{
		return website;
	}
	
	/**
	 * Get web site prefixed by 'http://'
	 */
	public String getWebsiteFullPath()
	{
		if (website == null || website.isEmpty())
			return null;
		
		String lowered = website.toLowerCase(); 
		if (!lowered.startsWith("http://") && !lowered.startsWith("https://"))
			return "http://" + website;

		return website;
	}
	
	/**
	 * If user automatically declines friendly defenders on the world map
	 */
	public boolean getAutoDeclineFriendlyDefender()
	{
		return autoDeclineFriendlyDefender;
	}
	
	/**
	 * If user automatically defends countries that he owns on the world map
	 */
	public boolean getAutoDefendOwnedCountry()
	{
		return autoDefendOwnedCountry;
	}
	
	/**
	 * Show help balloons in flash applications?  
	 */
	public boolean getShowHelpBalloons()
	{
		return showHelpBalloons;
	}
	
	/**
	 * Get profile image
	 */
	public String getProfileImageURL()
	{
		if (facebookId != null)
			return "https://graph.facebook.com/" + facebookId + "/picture?type=square";
		else
			return GlobalProperties.getInstance().getString("assets.url") + "/user_icons/user_dummy.gif";
	}
}
package onlinefrontlines.auth;

import onlinefrontlines.Constants;
import onlinefrontlines.facebook.Facebook;
import onlinefrontlines.utils.GlobalProperties;
import onlinefrontlines.utils.Tools;
import java.security.*;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

/**
 * Helper tools to automatically authenticate
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
public class AutoAuth 
{
	/**
	 * Key pair changes with every restart. If key pair changed, user will have to login again.
	 */
	private static KeyPair keyPair;
	
	/**
	 * Get existing or generate new key pair
	 */
	private static KeyPair getKeyPair()
	{
		try
		{
			if (keyPair == null)
			{
				KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
				generator.initialize(2048, new SecureRandom());
				keyPair = generator.generateKeyPair();
			}
		}
		catch (Exception e)
		{
			Tools.logException(e);
		}
		
		return keyPair;
	}

	/**
	 * Generate string to automatically authenticate a user
	 * 
	 * @param user User to authenticate
	 * @param timeValid How long this stays valid in milliseconds
	 * @return String that contains all information needed to authenticate a user 
	 * @throws Exception
	 */
    public static String generateAuthenticationString(User user, long timeValid) throws Exception
    {
		long now = Calendar.getInstance().getTime().getTime();
    	String parameters = user.id + "-" + (now + timeValid);
    	String signature = AuthTools.sign(parameters, getKeyPair().getPrivate());
    	return parameters + "-" + signature;
    }
    
    /**
     * Contains the result of parsing the authentication string
     */
    public static class AuthResult
    {
    	public User user;
    	public String facebookAccessToken;
    }
    
    /**
     * Parse authentication string and return result
     * 
     * @param authString Authentication string
     * @return Result or null if not valid
     * @throws Exception
     */
    public static AuthResult parseAuthenticationString(String authString) throws Exception
    {
    	// Validate that a string was passed
    	if (authString == null)
    		return null;
    	
    	// Split string
    	String[] values = authString.split("-");
    	if (values.length != 3)
    		return null;
    	
    	// Parse user id
    	int userId = Integer.parseInt(values[0]);
		
    	// Parse validity
    	long validUntil = Long.parseLong(values[1]);

		// Check validity
		long diff = validUntil - Calendar.getInstance().getTime().getTime();
   		if (diff < 0 || diff > Constants.AUTH_TIME_DEFAULT)
   			return null;
   		
		// Get the new user
		User user = UserCache.getInstance().get(userId);
		if (user == null)
			return null;
	
   		// Check signature
    	StringBuilder valueToVerify = new StringBuilder();
    	valueToVerify.append(userId);
    	valueToVerify.append('-');
    	valueToVerify.append(validUntil);
		if (!AuthTools.verifySignature(valueToVerify.toString(), values[2], getKeyPair().getPublic()))
			return null;
		
		// Return result
		AuthResult result = new AuthResult();
		result.user = user;
		return result;
    }
    
    /**
     * Get facebook access token from cookie
     * 
     * @param cookie
     * @return
     */
    public static AuthResult parseFbAuthenticationCookie(String cookie) throws Exception
    {
		// Get user
		Facebook.AuthenticatedUser fbUser = Facebook.getAuthenticatedUser(cookie);
		if (fbUser == null)
			return null;
		
		// Find user
		User user = UserCache.getInstance().getByFacebookId(fbUser.uid);
		if (user == null)
		{				
			// Get my data
			Facebook.UserDetails result = Facebook.getMyDetails(fbUser.accessToken);
			if (result == null)
				return null;
			
			// Create new user
            user = new User();
            user.facebookId = result.facebookId;
            user.username = result.name;
            user.realname = result.name;
            user.website = result.website;
            user.email = result.email;
            user.determineArmy();
            
            // Add to database
           	UserDAO.create(user);
           	
           	// Register with cache
           	UserCache.getInstance().put(user.id, user);
		}
		
		// Return result
		AuthResult result = new AuthResult();
		result.user = user;
		result.facebookAccessToken = fbUser.accessToken;
		return result;
    }

    /**
     * Generate cookies to automatically authenticate user
     * 
     * @param response HTTP response
	 * @param user User to authenticate
	 * @param timeValid How long the login stays valid in milliseconds
	 * @param cookieMaxAge How long the cookie stays valid in seconds, 0 for forever, -1 for as long as the browser is active
	 * @throws Exception
     */
    public static void generateAuthenticationCookies(HttpServletResponse response, User user, long timeValid, int cookieMaxAge) throws Exception
    {
    	String auth = generateAuthenticationString(user, timeValid);
    	
    	Cookie c = new Cookie(Constants.AUTH_STRING, auth);
    	c.setMaxAge(cookieMaxAge);
    	response.addCookie(c);
    }
    
    /**
     * Removes the cookies to automatically authenticate a user
     * @param response
     */
    public static void removeAuthenticationCookies(HttpServletResponse response)
    {
    	// Remove our authentication cookie
    	Cookie c = new Cookie(Constants.AUTH_STRING, "");
    	c.setMaxAge(0);
    	response.addCookie(c);
    }
    
    /**
     * Validate authentication parameters and determine user
     * 
     * @param request The HTTP request object
     * @param response The HTTP response object
     * @return User if request contains valid authentication info
     */
	public static AuthResult getAuthenticatedUser(HttpServletRequest request, HttpServletResponse response)
    {
		try
		{
			// Get auth result from cookie
			AuthResult cookieAuth = null;
			AuthResult facebookAuth = null;
    		Cookie[] cookies = request.getCookies();
    		if (cookies != null)
    			for (Cookie c : cookies)
    			{
    				String name = c.getName();
    				if (name.equals(Constants.AUTH_STRING))
    					cookieAuth = parseAuthenticationString(c.getValue());
    				else if (name.equals("fbsr_" + GlobalProperties.getInstance().getString("facebook.api_key")))
    					facebookAuth = parseFbAuthenticationCookie(c.getValue());
    			}    		
		
	    	// Determine which authentication to use
	    	AuthResult auth;
	    	if (cookieAuth != null)
	    		auth = cookieAuth;
	    	else if (facebookAuth != null)
	    		auth = facebookAuth;
	    	else
	    		return null;
	    	
    		return auth;
		}
		catch (Exception e)
		{
			// Something went wrong, no authenticated user
			Tools.logException(e);
			return null;
		}
    }
}

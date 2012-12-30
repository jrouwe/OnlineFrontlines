package onlinefrontlines.facebook;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.simple.parser.JSONParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import onlinefrontlines.utils.Cache;
import onlinefrontlines.utils.GlobalProperties;
import onlinefrontlines.utils.Tools;

/**
 * Helper class that wraps the Facebook API
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
public class Facebook 
{
	/**
	 * Helper class that contains parsed authentication cookie
	 */
	private static class AuthenticationCookie
	{
		String uid;
		String code;
	}
	
	/**
	 * Parse authentication cookie and return results 
	 */
	private static Cache<String, AuthenticationCookie> authenticationCookieCache = new Cache<String, AuthenticationCookie>("FBAuthenticationCookieCache")
	{
		@Override
		protected AuthenticationCookie load(String cookie) throws Throwable
		{
			// Parse cookie into signature and payload
	    	String[] params = cookie.split("\\.");
		   	if (params.length != 2)
		   		throw new Exception("Cookie could not be split");
		   	byte[] signature = Base64.decodeBase64(params[0]);
		   	String payload = params[1];
		   	String payloadDecoded = new String(Base64.decodeBase64(payload));
		   	
		   	// Parse JSON
			JSONParser parser = new JSONParser();
			@SuppressWarnings("unchecked")
			Map<String, Object> payloadJson = (Map<String, Object>)parser.parse(payloadDecoded);
			
			// Check signing algorithm
			Object algorithm = payloadJson.get("algorithm");
			if (algorithm == null || !algorithm.toString().toUpperCase().equals("HMAC-SHA256"))
				throw new Exception("Algorithm was incorrect");
			
			// Get user id
			Object userIdObject = payloadJson.get("user_id");
			if (userIdObject == null)
				throw new Exception("User id is missing");
			
			// Get code
			Object codeObject = payloadJson.get("code");
			if (codeObject == null)
				throw new Exception("Code is missing");
			
			// Validate signature
			SecretKeySpec secretKey = new SecretKeySpec((GlobalProperties.getInstance().getString("facebook.secret")).getBytes(), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(secretKey);
			byte[] calculatedSignature = mac.doFinal(payload.getBytes());
			if (signature.length != calculatedSignature.length)
				throw new Exception("Signature length mismatch");
			for (int i = 0; i < signature.length; ++i)
				if (signature[i] != calculatedSignature[i])
					throw new Exception("Signatures don't match");	
			
			// Construct cookie
			AuthenticationCookie r = new AuthenticationCookie();
			r.uid = userIdObject.toString();
			r.code = codeObject.toString();
			return r;
		}
	};

	/**
	 * Given an OAuth 2.0 code, return access token
	 */
	private static Cache<String, String> accessTokenCache = new Cache<String, String>("FBAccessTokenCache")
	{
		@Override
		protected String load(String code) throws Throwable
		{
			URL tokenUrl = new URL("https://graph.facebook.com/oauth/access_token?client_id=" + GlobalProperties.getInstance().getString("facebook.api_key") + "&redirect_uri=&client_secret=" + GlobalProperties.getInstance().getString("facebook.secret") + "&code=" + code);
			String tokenResult = IOUtils.toString(tokenUrl.openStream());
			for (String p : tokenResult.split("&"))	
			{
				int equals = p.indexOf('=');
				if (equals >= 0)
				{
					String key = p.substring(0, equals);
					if (key.equals("access_token"))
						return p.substring(equals + 1);
				}
			}
			
			throw new Exception("Did not find access token");
		}
	};
	
	/**
	 * Helper class that allows easy parsing of Facebook API result
	 */
	public static class Result
	{	
		private final Object result;

		/**
		 * Constructor
		 * 
		 * @param result JSON result
		 */
		public Result(Object result)
		{
			this.result = result;
		}
		
		/**
		 * Get a sub node
		 * 
		 * @param name Name of node
		 */
		public Result getNode(String name)
		{
			if (!(result instanceof Map))
				return new Result(new Boolean(false));
			Map<?, ?> map = (Map<?, ?>)result;
			Object value = map.get(name);
			return new Result(value != null? value : new Boolean(false));
		}
		
		/**
		 * Treat current node as a list
		 */
		public List<Result> getList()
		{
			if (!(result instanceof List))
				return new ArrayList<Result>();
			List<?> list = (List<?>)result;
			
			ArrayList<Result> result = new ArrayList<Result>();
			for (Object o : list)
				result.add(new Result(o));
			return result;
		}
		
		/**
		 * Check if current node is of type boolean with value false
		 */
		public boolean isBooleanFalse()
		{
			if (!(result instanceof Boolean))
				return false;
			return !((Boolean)result).booleanValue();
		}
		
		/**
		 * Convert current node to string
		 */
		public String getString()
		{
			return result != null? result.toString() : null;
		}
	}

	/**
	 * Call a faceboook API
	 * 
	 * @param url URL to call (ususally https://graph.facebook.com/...&access_token=...)
	 * @return
	 */
	private static Result callApi(String url)
    {
    	try
    	{
			// Get my data
			URL urlObject = new URL(url);
	    	JSONParser parser = new JSONParser();
			return new Result(parser.parse(new InputStreamReader(urlObject.openStream())));
		}
    	catch (Exception e)
		{
			Tools.logException(e);
			return null;
		}
    }
	
	/**
	 * Helper class that contains information on an authenticated user
	 */
	public static class AuthenticatedUser
	{
		public String uid;
		public String accessToken;
	}
	
	/**
	 * Parses cookie string and returns authenticated user
	 * 
	 * @param cookie Value of facebook fbrs_* cookie
	 */
    public static AuthenticatedUser getAuthenticatedUser(String cookie) throws Exception
    {
		// Crack cookie
		AuthenticationCookie authCookie;
    	try
    	{
    		authCookie = authenticationCookieCache.get(cookie);
		}
    	catch (Exception e)
		{
			Tools.logException(e);
			return null;
		}

		// Get access token
		String accessToken;
    	try
    	{
    		accessToken = accessTokenCache.get(authCookie.code);
    	}
    	catch (Exception e)
    	{
			Tools.logException(e);
    		return null;
    	}
    	
    	// Return data
    	AuthenticatedUser u = new AuthenticatedUser();
    	u.uid = authCookie.uid;
    	u.accessToken = accessToken;
    	return u;
    }
    
    /**
     * Helper class that contains properties about a user
     */
    public static class UserDetails
    {
    	public String facebookId;
    	public String name;
    	public String website;
    	public String email;
    }
    
    /**
     * Get properties for current user
     * 
     * @param accessToken Facebook access token
     */
    public static UserDetails getMyDetails(String accessToken)
    {
		Result result = callApi("https://graph.facebook.com/me?access_token=" + accessToken);
		if (result == null || result.isBooleanFalse())
			return null;
		
        UserDetails user = new UserDetails();
        user.facebookId = result.getNode("id").getString();
        user.name = result.getNode("name").getString();
        user.website = result.getNode("link").getString();
        user.email = result.getNode("email").getString();
        return user;    	
    }
        
    /**
     * Helper class that lists invitation details     * 
     */
    public static class RequestDetails
    {
    	public String requestId;
    	public String senderName;
    	public String senderFacebookId;
    	public String data;
    	
    	public String getRequestId()
    	{
    		return requestId;
    	}
    	
    	public String getSenderName()
    	{
    		return senderName;
    	}
    	
    	public String getSenderFacebookId()
    	{
    		return senderFacebookId;
    	}
    	
    	public String getData()
    	{
    		return data;
    	}
    }
    
    /**
     * Get number of pending invitations
     * 
     * @param accessToken Facebook access token
     */
    public static int getPendingRequestCount(String accessToken)
    {
    	try
    	{
	    	String query = "SELECT request_id FROM apprequest WHERE recipient_uid=me() AND app_id=" + GlobalProperties.getInstance().getString("facebook.api_key");
	    	Result result = callApi("https://graph.facebook.com/fql?q=" + URLEncoder.encode(query, "ISO-8859-1") + "&access_token=" + accessToken);
	    	if (result == null || result.isBooleanFalse())
	    		return 0;
	    	List<Result> list = result.getNode("data").getList();
	    	return list.size();
    	}
    	catch (UnsupportedEncodingException e)
    	{
    		Tools.logException(e);
    		return 0;
    	}
    }

    /**
     * Get all pending invitations
     * 
     * @param accessToken Facebook access token
     */
    public static ArrayList<RequestDetails> getAllPendingRequests(String accessToken)
    {
    	try
    	{
	    	String query = "SELECT request_id FROM apprequest WHERE recipient_uid=me() AND app_id=" + GlobalProperties.getInstance().getString("facebook.api_key");
	    	Result result = callApi("https://graph.facebook.com/fql?q=" + URLEncoder.encode(query, "ISO-8859-1") + "&access_token=" + accessToken);
	    	if (result == null || result.isBooleanFalse())
	    		return null;
	    	List<Result> list = result.getNode("data").getList();
	    	
	    	ArrayList<RequestDetails> details = new ArrayList<RequestDetails>();
	    	for (Result r : list)
	    	{
	    		RequestDetails d = getRequestDetails(r.getNode("request_id").getString(), accessToken);
	    		if (d != null)
	    			details.add(d);
	    	}
	    	return details;
    	}
    	catch (UnsupportedEncodingException e)
    	{
    		Tools.logException(e);
    		return null;
    	}
    }
    
    /**
     * Get details for invitation
     * 
     * @param requestId Facebook request id: <requestid>_<userid>
     * @param accessToken Facebook access token
     */
    public static RequestDetails getRequestDetails(String requestId, String accessToken)
    {
    	assert(requestId.indexOf('_') >= 0); // requestId is of the form requestid_userid
    	
    	Result result = callApi("https://graph.facebook.com/" + requestId + "?access_token=" + accessToken);
    	if (result == null || result.isBooleanFalse())
    		return null;
    	
    	RequestDetails details = new RequestDetails();
    	details.requestId = result.getNode("id").getString();
    	details.senderName = result.getNode("from").getNode("name").getString();
    	details.senderFacebookId = result.getNode("from").getNode("id").getString();
    	details.data = result.getNode("data").getString();
    	return details;
    }
    
    /**
     * Delete invitation
     * 
     * @param requestId Facebook request id: <requestid>_<userid>
     * @param accessToken Facebook access token
     */
    public static void deleteRequest(String requestId, String accessToken)
    {
    	assert(requestId.indexOf('_') >= 0); // requestId is of the form requestid_userid

    	callApi("https://graph.facebook.com/" + requestId + "?access_token=" + accessToken + "&method=delete");
    }
}

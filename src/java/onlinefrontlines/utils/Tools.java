package onlinefrontlines.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;
import org.apache.log4j.Logger;
import java.io.UnsupportedEncodingException;

/**
 * General tools
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
public class Tools 
{
	private static final Logger log = Logger.getLogger(Tools.class);

	/**
	 * Get random value with standard normally distributed values (P(x) = 1 / sqrt(2 PI) * exp(-x^2 / 2))
	 * 
	 * @param random Random number generator
	 * 
	 * @see http://en.wikipedia.org/wiki/Gaussian_distribution
	 */
	public static double standardGaussianRandom(Random random)
	{
		return Math.sqrt(-2.0 * Math.log(random.nextDouble())) * Math.cos(2 * Math.PI * random.nextDouble());
	}
	
	/**
	 * Get random value with normally distributed values (P(x) = 1 / (sigma * sqrt(2 PI)) * exp(-(x - mu)^2 / (2 * sigma^2)))
	 * 
	 * @param mu Value the distribution centers around
	 * @param sigma Standard deviation of distribution
	 * @param random Random number generator
	 */
	public static double gaussianRandom(double mu, double sigma, Random random)
	{
		return standardGaussianRandom(random) * sigma + mu;
	}
		
	/**
	 * Log an exception
	 * 
	 * @param msg Message to add to log
	 * @param t Throwable to log
	 */
	public static void logException(String msg, Throwable t)
	{
		while (t != null)
		{
			// Log
			log.error(msg + t.toString());
			t.printStackTrace();
			
			// Get cause
			t = t.getCause();
			msg = "caused by ";
		}		
	}
	
	/**
	 * Log an exception
	 * 
	 * @param t Throwable to log 
	 */
	public static void logException(Throwable t)
	{
		logException("Exception caught ", t);		
	}
	
	/**
	 * Characters that do not need to be escaped 
	 */
	private static final String unescaped = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@-_.*+/";

    /**
     * Encode GET parameter in such a way that Unicode characters survive
     */
    public static String encodeGetParameter(String value) throws UnsupportedEncodingException
    {
    	// All HTTP GET request parameters are encoded as ISO-8859-1 by default
    	// To make all unicode characters survive we first encode as UTF-8 and then as ISO-8859-1 so that on the other side we only have to decode from UTF-8
    	return value != null? URLEncoder.encode(URLEncoder.encode(value, "UTF-8"), "ISO-8859-1") : null;
    }
    
    /**
     * Decode GET parameter assuming it has been gotten from a request like: request.getParameter(...)
     */
    public static String decodeGetParameter(String value) throws UnsupportedEncodingException
    {
    	return value != null? URLDecoder.decode(value, "UTF-8") : null;
    }

    /**
	 * Equivalent of Flash's escape command (which is not compatible with URLEncoder.encode when it concerns UNICODE characters)
	 */
	public static String flashEscape(String value)
	{
		StringBuilder rv = new StringBuilder();
		rv.ensureCapacity(value.length());
		
		for (int i = 0; i < value.length(); ++i)
		{
			char val = value.charAt(i);
			if (unescaped.indexOf(val) >= 0)
			{
				rv.append(val);
			}
			else
			{
				String hex = Integer.toHexString(val);
				switch (hex.length())
				{
				case 1:
					rv.append("%0");
					break;
				case 2:
					rv.append("%");
					break;
				case 3:
					rv.append("%u0");
					break;
				case 4:
					rv.append("%u");
					break;
				default:
					assert(false);
					break;
				}
				rv.append(hex);
			}
		}
		
		return rv.toString();
	}
}

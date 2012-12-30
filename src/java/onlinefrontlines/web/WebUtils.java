package onlinefrontlines.web;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletResponse;
import onlinefrontlines.utils.Tools;

/**
 * Handy util class
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
public final class WebUtils 
{
	/**
	 * Resource bundle to get localized text from
	 */
	private static ResourceBundle bundle;
	
	/**
	 * Static constructor
	 */
	static
	{
		try
		{
			bundle = ResourceBundle.getBundle("onlinefrontlines.package", Locale.US);
		}
		catch (Exception e)
		{
			Tools.logException(e);
		}		
	}
	
	/**
	 * Helper function to set no cache headers
	 * 
	 * @param response Response to set headers on
	 */
	public static void setNoCacheHeaders(HttpServletResponse response)
	{
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache"); 
		response.setDateHeader("Expires", 946080000000L);
		response.setDateHeader("Last-Modified", System.currentTimeMillis());
	}

	/**
	 * Localize text given a key
	 * 
	 * @param key Key to look up from the resource bundle
	 * @return Localized text or key when text is not found
	 */
	public static String getText(String key)
	{
		try
		{
			return bundle.getString(key);
		}
		catch (MissingResourceException e)
		{
			return key;
		}
	}
	
    /**
     * Escape html entity characters and high characters (eg "curvy" Word quotes).
     * Note this method can also be used to encode XML.
     * 
     * @param s the String to escape.
     * @return the escaped string
     */
    public final static String htmlEncode(String s) 
    {
        if (s == null)
        	return "";

        StringBuffer str = new StringBuffer();

        for (int j = 0; j < s.length(); j++) 
        {
            char c = s.charAt(j);

            if (c < '\200') 
            {
                // Encode standard ASCII characters into HTML entities where needed
                switch (c) 
                {
                case '"':
                    str.append("&quot;");

                    break;

                case '&':
                    str.append("&amp;");

                    break;

                case '<':
                    str.append("&lt;");

                    break;

                case '>':
                    str.append("&gt;");

                    break;

                default:
                    str.append(c);
                }
            }            
            else if (c < '\377') 
            {
            	// Encode 'ugly' characters (ie Word "curvy" quotes etc)
                String hexChars = "0123456789ABCDEF";
                int a = c % 16;
                int b = (c - a) / 16;
                String hex = "" + hexChars.charAt(b) + hexChars.charAt(a);
                str.append("&#x" + hex + ";");
            }
            else 
            {
                // Add other characters back in - to handle character sets other than ASCII
                str.append(c);
            }
        }

        return str.toString();
    }
}

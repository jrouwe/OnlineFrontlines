package onlinefrontlines.home.web;

import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import onlinefrontlines.web.*;
import onlinefrontlines.userstats.UserLeaderboardDAO;
import onlinefrontlines.utils.*;

/**
 * This action displays the home page
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
public class HomeAction extends WebAction
{
	private static final Logger log = Logger.getLogger(HomeAction.class);
	
	/**
	 * Get most active users
	 */
	public List<UserLeaderboardDAO.MostActive> getMostActive()
	{
		try
		{
			return UserLeaderboardDAO.getLeaderboardMostActive();
		}
		catch (SQLException e)
		{
			Tools.logException(e);
	
			return null;
		}
	}

	/**
	 * News in html format
	 */
	public String getNewsHTML()
	{
		try
		{
			// Output string
			StringBuilder output = new StringBuilder();
			
			// Get news source
			URL xmlUrl = new URL(GlobalProperties.getInstance().getString("news.rss.url"));
			InputStream is = xmlUrl.openStream();
			
			try
			{
		    	// Open parser
	            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	            Document doc = docBuilder.parse(is);
	            if (doc == null)
	            	throw new Exception("Document could not be parsed");
	
	            // Normalize text representation
	            doc.getDocumentElement().normalize();
	            
	            // Get items
	            NodeList items = doc.getElementsByTagName("item");
	            if (items == null)
	            	throw new Exception("No items found");
	            
	            // Loop through first 4 items
	            for (int i = 0; i < items.getLength() && i < 4; ++i)
	            {
	            	Node item = items.item(i);
	            	
	            	// Get attributes
	            	String pubDate = "";
	            	String author = "";
	            	String description = "";
	            	
	            	for (int j = 0; j < item.getChildNodes().getLength(); ++j)
	            	{
	            		Node child = item.getChildNodes().item(j);            		
	            		String childName = child.getNodeName().toLowerCase();
	            		
	            		if (childName.equals("pubdate"))
	            		{
	            			pubDate = child.getFirstChild().getNodeValue();
	            		}
	            		else if (childName.equals("author"))
	            		{
	            			author = child.getFirstChild().getNodeValue();
	            		}
	            		else if (childName.equals("description"))
	            		{
	            			description = child.getFirstChild().getNodeValue();
	            		}
	            	}
	            	
	            	// Generate HTML
	            	output.append("<div class=\"news_item\">");
					output.append("<div class=\"news_header\">");
					output.append(pubDate);
					output.append(" - ");
					output.append(author);
					output.append("</div>");
					output.append("<div class=\"news_text\">");
					output.append(description);
					output.append("</div>");
					output.append("</div>");				
	            }
			}
			finally
			{
				is.close();
			}
            
			return output.toString();
        }
		catch (UnknownHostException e)
		{
			log.error("Unable to connect to news rss feed");
		}
		catch (Exception e)
		{			
			Tools.logException(e);
		}

		return "";
	}
}
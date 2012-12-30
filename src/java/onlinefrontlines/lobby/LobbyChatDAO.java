package onlinefrontlines.lobby;

import java.util.*;
import java.sql.SQLException;
import onlinefrontlines.utils.DbQueryHelper;

/**
 * This class communicates with the database and manages reading/writing state of lobby chat messages
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
public class LobbyChatDAO 
{
	/**
	 * Queries the database for all messages for a particular lobby
	 */
	public static ArrayList<TextMessage> getMessages(int lobbyId, int maxMessages) throws SQLException
	{
		ArrayList<TextMessage> list = new ArrayList<TextMessage>();

    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find all feedback
	    	helper.prepareQuery("SELECT userId, message FROM lobby_chat WHERE lobbyId=? ORDER BY id DESC LIMIT ?");
	    	helper.setInt(1, lobbyId);
	    	helper.setInt(2, maxMessages);
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
		        // Construct object
	    		TextMessage m = new TextMessage(helper.getInt(1), helper.getString(2));
		        list.add(m);
	    	}
        }
        finally
        {
        	helper.close();
        }
        
        Collections.reverse(list);
        return list;
	}

	/**
	 * Add message to database
	 * 
	 * @param lobbyId Lobby id
	 * @param message Text message to store
	 * @throws SQLException
	 */
	public static void addMessage(int lobbyId, TextMessage message) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Create record
	    	helper.prepareQuery("INSERT INTO lobby_chat (lobbyId, userId, message) VALUES (?, ?, ?)");
	    	helper.setInt(1, lobbyId);
	    	helper.setInt(2, message.userId);
	    	helper.setString(3, message.message);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
}
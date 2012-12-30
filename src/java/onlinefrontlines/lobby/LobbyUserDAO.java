package onlinefrontlines.lobby;

import java.util.*;
import java.sql.SQLException;
import onlinefrontlines.utils.DbQueryHelper;
import onlinefrontlines.utils.DbStoredProcHelper;
import onlinefrontlines.Army;

/**
 * This class communicates with the database and manages reading/writing state of lobby users
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
public class LobbyUserDAO 
{
	public static class State
	{
		public int userId;
		public int defendedCountryX;
		public int defendedCountryY;
		public int attackedCountryX;
		public int attackedCountryY;
		public boolean hasAcceptedChallenge;
		public long challengeValidUntil;
		public Army army;
	};
	
	/**
	 * Queries the database for all users for a particular lobby
	 */
	public static ArrayList<State> getUsers(int lobbyId) throws SQLException
	{
		ArrayList<State> list = new ArrayList<State>();

    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find all feedback
	    	helper.prepareQuery("SELECT userId, defendedCountryX, defendedCountryY, attackedCountryX, attackedCountryY, hasAcceptedChallenge, challengeValidUntil, army FROM lobby_users WHERE lobbyId=?");
	    	helper.setInt(1, lobbyId);
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
		        // Construct object
	    		State s = new State();
	    		s.userId = helper.getInt(1);
	    		s.defendedCountryX = helper.getInt(2);
	    		s.defendedCountryY = helper.getInt(3);
	    		s.attackedCountryX = helper.getInt(4);
	    		s.attackedCountryY = helper.getInt(5);
	    		s.hasAcceptedChallenge = helper.getInt(6) != 0;
	    		s.challengeValidUntil = helper.getLong(7);
	    		s.army = Army.fromInt(helper.getInt(8));
		        list.add(s);
	    	}
        }
        finally
        {
        	helper.close();
        }
        
        return list;
	}

	/**
	 * Create / update lobby user in database
	 * 
	 * @param lobbyId Lobby id
	 * @param lobbyUser Lobby user to add
	 * @throws SQLException
	 */
	public static void createOrUpdateUser(int lobbyId, LobbyUser lobbyUser) throws SQLException
	{
    	DbStoredProcHelper helper = new DbStoredProcHelper();
        try
        {
    		helper.prepareCall("{CALL createOrUpdateLobbyUser(?, ?, ?, ?, ?, ?, ?, ?, ?)}");
    		helper.setInt(1, lobbyId);
    		helper.setInt(2, lobbyUser.userId);
    		helper.setInt(3, lobbyUser.getDefendedCountryX());
    		helper.setInt(4, lobbyUser.getDefendedCountryY());
    		helper.setInt(5, lobbyUser.getAttackedCountryX());
    		helper.setInt(6, lobbyUser.getAttackedCountryY());
    		helper.setInt(7, lobbyUser.hasAcceptedChallenge? 1 : 0);
    		helper.setLong(8, lobbyUser.challengeValidUntil);    		
    		helper.setInt(9, Army.toInt(lobbyUser.army));
	    	helper.execute();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Remove lobby user from database
	 * 
	 * @param lobbyId Lobby id
	 * @param userId User id
	 * @throws SQLException
	 */
	public static void removeUser(int lobbyId, int userId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
	    	helper.prepareQuery("DELETE FROM lobby_users WHERE lobbyId=? AND userId=?");
	    	helper.setInt(1, lobbyId);
	    	helper.setInt(2, userId);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}

	/**
	 * Queries the database for all users for a particular lobby
	 */
	public static int getChallengeCountForLobby(int lobbyId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find all feedback
	    	helper.prepareQuery("SELECT COUNT(1) FROM lobby_users WHERE lobbyId=? AND challengeValidUntil>=? AND defendedCountryX>=0 AND hasAcceptedChallenge=0");
	    	helper.setInt(1, lobbyId);
	    	helper.setLong(2, Calendar.getInstance().getTime().getTime());
	    	helper.executeQuery();
	    	helper.nextRecord();
	    	return helper.getInt(1);
        }
        finally
        {
        	helper.close();
        }
	}
}
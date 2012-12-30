package onlinefrontlines.feedback;

import java.text.DateFormat;
import java.util.*;
import java.sql.SQLException;
import onlinefrontlines.utils.CacheException;
import onlinefrontlines.auth.UserCache;
import onlinefrontlines.utils.DbQueryHelper;

/**
 * This class communicates with the database and manages reading/writing feedback
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
public class FeedbackDAO 
{
	/**
	 * Queries the database for all feedback
	 */
	public static ArrayList<Feedback> list(int opponentUserId, int maxReturnedRecords) throws SQLException
	{
		ArrayList<Feedback> feedbackList = new ArrayList<Feedback>();

    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find all feedback
	    	helper.prepareQuery("SELECT id, reporterUserId, opponentUserId, gameId, score, comments, reply, creationTime FROM feedback WHERE opponentUserId=? ORDER BY id DESC LIMIT ?");
	    	helper.setInt(1, opponentUserId);
	    	helper.setInt(2, maxReturnedRecords);
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
		        // Construct object
		        Feedback feedback = new Feedback();
		        feedback.id = helper.getInt(1);
		        feedback.reporterUserId = helper.getInt(2);
		        feedback.opponentUserId = helper.getInt(3);
		        feedback.gameId = helper.getInt(4);
		        feedback.score = helper.getInt(5);
		        feedback.comments = helper.getString(6);
		        feedback.reply = helper.getString(7);
		        feedback.creationDate = helper.getLong(8);
		        feedbackList.add(feedback);
	    	}
        }
        finally
        {
        	helper.close();
        }
        
        return feedbackList;
	}
	
	public static class FeedbackRequired
	{
		/**
		 * Game id for which feedback is required
		 */
		public int gameId;
		
		/**
		 * Creation time of game
		 */
		public long creationTime;
		
		/**
		 * User id of opponent
		 */
		public int opponentUserId;

		/**
		 * Game id for which feedback is required
		 */
		public int getGameId()
		{
			return gameId;
		}
		
		/**
		 * User id of opponent
		 */
		public int getOpponentUserId()
		{
			return opponentUserId;
		}

		/**
		 * Get creation time as a string
		 */
		public String getCreationDateString()
		{
			return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US).format(new Date(creationTime)); 
		}

		/**
		 * Translate user id into user name
		 */
		public String getOpponentUsername()
		{
			try
			{
				return UserCache.getInstance().get(opponentUserId).username;
			}
			catch (CacheException e)
			{
				return "<unknown>";
			}
		}
	}
	
	/**
	 * Queries the database for all games that require feedback
	 */
	public static ArrayList<FeedbackRequired> listGamesRequiringFeedback(int userId) throws SQLException
	{
		ArrayList<FeedbackRequired> list = new ArrayList<FeedbackRequired>();

    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find all feedback
	    	helper.prepareQuery("SELECT id, creationTime, userId1, userId2 FROM games WHERE (userId2 IS NOT NULL) AND (userId1=? OR userId2=?) AND (corrupt=0) AND (turnEndTime>?) AND (id NOT IN (SELECT gameId FROM feedback WHERE reporterUserId=?)) ORDER BY id DESC");
	    	helper.setInt(1, userId);
	    	helper.setInt(2, userId);
	    	helper.setLong(3, Calendar.getInstance().getTime().getTime() - 7L * 24L * 60L * 60L * 1000L);
	    	helper.setInt(4, userId);
	    	helper.executeQuery();
	    	
	    	while (helper.nextRecord())
	    	{
		        // Construct object
		        FeedbackRequired required = new FeedbackRequired();
		        required.gameId = helper.getInt(1);
		        required.creationTime = helper.getLong(2);
		        required.opponentUserId = helper.getInt(3) == userId? helper.getInt(4) : helper.getInt(3);
		        list.add(required);
	    	}
        }
        finally
        {
        	helper.close();
        }
        
        return list;
	}
	
	/**
	 * Get feedback record
	 */
	public static Feedback find(int id) throws SQLException
	{
	   	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find all feedback
	    	helper.prepareQuery("SELECT reporterUserId, opponentUserId, gameId, score, comments, reply, creationTime FROM feedback WHERE id=?");
	    	helper.setInt(1, id);
	    	helper.executeQuery();
	    	
	    	if (!helper.nextRecord())
	    		return null;

	    	// Construct object
	        Feedback feedback = new Feedback();
	        feedback.id = id;
	        feedback.reporterUserId = helper.getInt(1);
	        feedback.opponentUserId = helper.getInt(2);
	        feedback.gameId = helper.getInt(3);
	        feedback.score = helper.getInt(4);
	        feedback.comments = helper.getString(5);
	        feedback.reply = helper.getString(6);
	        feedback.creationDate = helper.getLong(7);
	        return feedback;
        }
        finally
        {
        	helper.close();
        }
	}

	/**
	 * Inserts feedback in the database
	 * 
	 * @param feedback The feedback to store in the database
	 * @throws SQLException
	 */
	public static void create(Feedback feedback) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Create record
	    	helper.prepareQuery("INSERT INTO feedback (reporterUserId, opponentUserId, gameId, score, comments, reply, creationTime) VALUES (?, ?, ?, ?, ?, ?, ?)");
	    	helper.setInt(1, feedback.reporterUserId);
	    	helper.setInt(2, feedback.opponentUserId);
	    	helper.setInt(3, feedback.gameId);
	    	helper.setInt(4, feedback.score);
	    	helper.setString(5, feedback.comments);
	    	helper.setString(6, feedback.reply);
	    	helper.setLong(7, feedback.creationDate);
	    	helper.executeUpdate();
	    	
	    	// Get id
	    	ArrayList<Integer> generatedKeys = helper.getGeneratedKeys();
	    	feedback.id = generatedKeys.get(0);
	    	
	    	// Update score for this pair of users
	    	helper.prepareQuery("UPDATE feedback_score_per_pair SET score=? WHERE reporterUserId=? AND opponentUserId=?");
	    	helper.setInt(1, feedback.score);
	    	helper.setInt(2, feedback.reporterUserId);
	    	helper.setInt(3, feedback.opponentUserId);
	    	if (helper.executeUpdate() == 0)
	    	{
	    		// Insert new record
	    		helper.prepareQuery("INSERT INTO feedback_score_per_pair (reporterUserId, opponentUserId, score) VALUES (?, ?, ?)");
		    	helper.setInt(1, feedback.reporterUserId);
		    	helper.setInt(2, feedback.opponentUserId);
		    	helper.setInt(3, feedback.score);
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
	 * Updates feedback in the database
	 * Note that currently only the reply is updated
	 * 
	 * @param feedback The feedback to update in the database
	 * @throws SQLException
	 */
	public static void update(Feedback feedback) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Create record
	    	helper.prepareQuery("UPDATE feedback SET reply=? WHERE id=?");
	    	helper.setString(1, feedback.reply);
	    	helper.setInt(2, feedback.id);
	    	helper.executeUpdate();
	    }
        finally
        {
        	// Close database connection
        	helper.close();
        }		
	}
	
	/**
	 * Get feedback score for user
	 * 
	 * @param userId User id of the user
	 * @return Feedback score
	 * @throws SQLException
	 */
	public static int getScore(int userId) throws SQLException
	{
    	DbQueryHelper helper = new DbQueryHelper();
        try
        {
        	// Find all feedback
	    	helper.prepareQuery("SELECT score FROM feedback_score WHERE userId=?");
	    	helper.setInt(1, userId);
	    	helper.executeQuery();
	    	
	    	if (helper.nextRecord())
	    		return helper.getInt(1);
	    	else
	    		return 0;
        }
        finally
        {
        	helper.close();
        }
	}
}
